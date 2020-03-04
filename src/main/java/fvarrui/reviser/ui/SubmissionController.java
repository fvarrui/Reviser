package fvarrui.reviser.ui;

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

import fvarrui.reviser.csv.CsvResult;
import fvarrui.reviser.csv.CsvStudent;
import fvarrui.reviser.csv.CsvUtils;
import fvarrui.reviser.model.Results;
import fvarrui.reviser.model.Submission;
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

public class SubmissionController implements Initializable {
	
	public static SubmissionController me;

	// controllers

	private ResultsController resultsController;
	private DesignerController formDesignerController;
	private ConsoleController consoleController;

	// model

	private File resultsFile;

	private StringProperty title = new SimpleStringProperty("");
	private ObjectProperty<Results> results = new SimpleObjectProperty<>(new Results());
	private ObjectProperty<Submission> submission = new SimpleObjectProperty<>();

	// view
	
	@FXML
	private BorderPane view;

	@FXML
	private TabPane tabPane;
	
	@FXML
	private Label titleLabel;

	@FXML
	private Tab resultsTab, formDesignerTab, consoleTab;

	public SubmissionController() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SubmissionView.fxml"));
		loader.setController(this);
		loader.load();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		me = this;

		try {

			// creates controllers
			resultsController = new ResultsController();
			formDesignerController = new DesignerController();
			consoleController = new ConsoleController();

			// set tabs content
			resultsTab.setContent(resultsController.getView());
			formDesignerTab.setContent(formDesignerController.getView());
			consoleTab.setContent(consoleController.getView());

			// add listener when submissions dir changed
			submission.addListener((o, ov, nv) -> onSubmissionChanged(o, ov, nv));
			
			// binds
			titleLabel.textProperty().bind(title);

		} catch (IOException e) {

			e.printStackTrace();
			System.exit(1);

		}
		
		App.primaryStage.setOnCloseRequest(e -> {
			try {
				if (this.resultsFile != null && this.results.get() != null)
					this.results.get().save(resultsFile);
			} catch (JsonSyntaxException | JsonIOException | IOException e1) {
				Dialogs.error("Error al guardar los resultados", e1);
			}
		});

	}

	private void onSubmissionChanged(ObservableValue<? extends Submission> o, Submission ov, Submission nv) {
		
		if (ov != null) {
			try {
				results.get().save(resultsFile);
			} catch (JsonSyntaxException | JsonIOException | IOException e) {
				Dialogs.error("No se pudieron guardar los resultados en el fichero '" + resultsFile + "'", e);
			}
		}
		
		if (nv != null) {
			
			// sets title
			title.set(nv.getDirectory().getName());
	
			// loads results from json if exists, or creates from scratch
			resultsFile = new File(nv.getDirectory(), "results.json");
			results.set(Results.load(resultsFile, nv.getDirectory()));
	
			// binds results view
			resultsController.setSubmissionsDir(nv.getDirectory());
			resultsController.resultsProperty().bind(results);
	
			// binds form designer view
			formDesignerController.resultsProperty().bind(results);
			
		} else {
			
			// unsets title
			title.set("");
	
			// unsets results
			results.set(null);
	
			// unbinds 
			resultsController.setSubmissionsDir(null);
			resultsController.resultsProperty().unbind();
			formDesignerController.resultsProperty().unbind();
			
		}

	}

	@FXML
	private void onOpenExplorer(ActionEvent e) {
		try {
			Desktop.getDesktop().open(getSubmission().getDirectory());
		} catch (IOException e1) {
			Dialogs.error("Error al abrir la carpeta '" + getSubmission().getDirectory() + "' en el explorador de archivos del sistema", e1);
		}
	}

	@FXML
	private void onExportResults(ActionEvent e) {
		File resultsFile = Dialogs.saveFile("Exportar resultados en CSV para Moodle", getSubmission().getDirectory().getName(), "Fichero CSV", "*.csv");
		if (resultsFile != null)
			try {
				List<CsvResult> results = this.results.get().getResults().stream()
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
				results.get().updateFromStudents(students);
			} catch (IOException | CsvException e1) {
				Dialogs.error("El fichero CSV no se ha podido abrir", e1);
			}
	}
	
	@FXML
	private void onEvaluateAll(ActionEvent e) {
		if (Dialogs.confirm("Evaluar todas las entregas", "Marcando todas las entregas como evaluadas.", "¿Desea continuar?")) {
			results.get().evaluateAll(true);
		}
	}
	
	@FXML
	private void onRemoveSubmission(ActionEvent e) {
		App.mainController.removeSubmission(getSubmission());
	}

	public void showConsole() {
		tabPane.getSelectionModel().select(consoleTab);
	}

	public BorderPane getView() {
		return view;
	}

	public final ObjectProperty<Submission> submissionProperty() {
		return this.submission;
	}

	public final Submission getSubmission() {
		return this.submissionProperty().get();
	}

	public final void setSubmission(final Submission submission) {
		this.submissionProperty().set(submission);
	}

}
