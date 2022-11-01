package io.github.fvarrui.reviser.ui.controllers;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.opencsv.exceptions.CsvException;

import io.github.fvarrui.reviser.csv.CsvResult;
import io.github.fvarrui.reviser.csv.CsvStudent;
import io.github.fvarrui.reviser.csv.CsvUtils;
import io.github.fvarrui.reviser.model.Exercise;
import io.github.fvarrui.reviser.ui.Reviser;
import io.github.fvarrui.reviser.ui.tasks.CompareSubmissionsTask;
import io.github.fvarrui.reviser.ui.utils.Dialogs;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

public class ExerciseController implements Initializable {

	public static ExerciseController me;

	// controllers

	private SubmissionsController submissionsController;
	private DesignerController designerController;
	private ConsoleController consoleController;

	// model

	private StringProperty title = new SimpleStringProperty("");
	private ObjectProperty<Exercise> exercise = new SimpleObjectProperty<>();

	// view

	@FXML
	private BorderPane view;

	@FXML
	private TabPane tabPane;

	@FXML
	private Label titleLabel;

	@FXML
	private Tab resultsTab, designerTab, consoleTab;

	public ExerciseController() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ExerciseView.fxml"));
		loader.setController(this);
		loader.load();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		me = this;

		try {

			// creates controllers
			designerController = new DesignerController();
			consoleController = new ConsoleController();
			submissionsController = new SubmissionsController();

			// set tabs content
			resultsTab.setContent(submissionsController.getView());
			designerTab.setContent(designerController.getView());
			consoleTab.setContent(consoleController.getView());

			// add listener when submissions dir changed
			exercise.addListener((o, ov, nv) -> onExerciseChanged(o, ov, nv));

			// binds
			titleLabel.textProperty().bind(title);

		} catch (IOException e) {

			e.printStackTrace();
			System.exit(1);

		}

		Reviser.primaryStage.setOnCloseRequest(e -> {
			saveExercise(getExercise());
		});

	}

	public void saveExercise(Exercise exercise) {
		try {
			if (exercise != null) {
				exercise.save();
			}
		} catch (JsonSyntaxException | JsonIOException | IOException e1) {
			Dialogs.error("No se pudo guardar el ejercicio '" + exercise + "'", e1);
		}
	}

	private void onExerciseChanged(ObservableValue<? extends Exercise> o, Exercise ov, Exercise nv) {

		String desde = (ov != null && ov.getDirectory() != null) ? ov.getDirectory().getName() : "<ninguno>";
		String hasta = (nv != null && nv.getDirectory() != null) ? nv.getDirectory().getName() : "<ninguno>";
		System.out.println("cambiando desde " + desde + " a " + hasta);

		// saves old exercise
		if (ov != null) {
			saveExercise(ov);
		}

		if (nv != null) {

			// sets title
			title.set(nv.getDirectory().getName());

			// binds results view
			submissionsController.setExercise(nv);

			// binds form designer view
			designerController.setExercise(nv);

		} else {

			// unsets title
			title.set("");

			exercise.set(null);

			// unbinds results view
			submissionsController.setExercise(null);

			// unbinds form designer view
			designerController.setExercise(null);

		}

	}

	@FXML
	private void onOpenExplorer(ActionEvent e) {
		try {
			Desktop.getDesktop().open(getExercise().getDirectory());
		} catch (IOException e1) {
			Dialogs.error("Error al abrir la carpeta '" + getExercise().getDirectory() + "' en el explorador de archivos del sistema", e1);
		}
	}

	@FXML
	private void onExportResults(ActionEvent e) {
		File resultsFile = Dialogs.saveFile("Exportar resultados en CSV para Moodle", getExercise().getDirectory().getName(), "Fichero CSV", "*.csv");
		if (resultsFile != null)
			try {
				List<CsvResult> results = this.exercise.get().getSubmissions().stream()
						.map(r -> new CsvResult(r.getScore(), r.getName(), r.getFullFeedback(), r.getEmail()))
						.collect(Collectors.toList());
				CsvUtils.resultsToCsv(results, resultsFile);
			} catch (IOException | CsvException e1) {
				Dialogs.error("El fichero CSV de resultados no ha podido exportarse", e1);
			}
	}

	@FXML
	private void onImportEmails(ActionEvent e) {
		File studentsFile = Dialogs.openFile("Seleccione un fichero CSV de calificaciones exportado desde Moodle", "Fichero CSV", "*.csv");
		if (studentsFile != null)
			try {
				List<CsvStudent> students = CsvUtils.csvToStudents(studentsFile);
				getExercise().updateFromStudents(students);
			} catch (IOException | CsvException e1) {
				Dialogs.error("El fichero CSV no se ha podido abrir", e1);
			}
	}

	@FXML
	private void onEvaluateAll(ActionEvent e) {
		if (Dialogs.confirm("Evaluar todas las entregas", "Marcando todas las entregas como evaluadas.", "¿Desea continuar?")) {
			getExercise().evaluateAll(true);
		}
	}
	
	@FXML
	private void onCompareSubmissions(ActionEvent e) {
		CompareSubmissionsTask task = new CompareSubmissionsTask(getExercise().getSubmissions());
		task.setOnScheduled(event -> {
			ExerciseController.me.showConsole();
			ConsoleController.me.clearConsole();
		});
		task.setOnFailed(event -> {
			Reviser.console.println(event.getSource().getException());
		});
		task.start();	
	}

	@FXML
	private void onRemoveExercise(ActionEvent e) {
		Reviser.mainController.removeExercise(exercise.get());
	}

	public void showConsole() {
		tabPane.getSelectionModel().select(consoleTab);
	}
	
	public void showResults() {
		tabPane.getSelectionModel().select(resultsTab);
	}

	public BorderPane getView() {
		return view;
	}

	public final ObjectProperty<Exercise> exerciseProperty() {
		return this.exercise;
	}
	
	public final Exercise getExercise() {
		return this.exerciseProperty().get();
	}

	public final void setExercise(final Exercise exercise) {
		this.exerciseProperty().set(exercise);
	}

}
