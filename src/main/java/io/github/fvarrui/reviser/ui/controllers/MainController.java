package io.github.fvarrui.reviser.ui.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;
import org.controlsfx.dialog.ProgressDialog;

import io.github.fvarrui.reviser.config.Config;
import io.github.fvarrui.reviser.model.Exercise;
import io.github.fvarrui.reviser.ui.Reviser;
import io.github.fvarrui.reviser.ui.tasks.ImportExerciseTask;
import io.github.fvarrui.reviser.ui.utils.Dialogs;
import io.github.fvarrui.reviser.ui.utils.FileListCell;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class MainController implements Initializable {

	// controllers

	private ExerciseController exerciseController;

	// model

	private ListProperty<File> exercises = new SimpleListProperty<>(FXCollections.observableArrayList());
	private ObjectProperty<File> selectedExercise = new SimpleObjectProperty<>();

	// view

	@FXML
	private SplitPane view;

	@FXML
	private ListView<File> exercisesList;

	@FXML
	private Button importButton;

	@FXML
	private Button refreshButton;

	@FXML
	private BorderPane exercisePane;

	@FXML
	private VBox noSelectionPane;

	public MainController() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
		loader.setController(this);
		loader.load();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		try {

			// creates and binds controllers
			exerciseController = new ExerciseController();

			// set submission controller view
			exercisePane.setCenter(exerciseController.getView());
			
			// renderes
			exercisesList.setCellFactory((ListView<File> param) -> new FileListCell());

			// binds
			selectedExercise.bind(exercisesList.getSelectionModel().selectedItemProperty());
			noSelectionPane.visibleProperty().bind(selectedExercise.isNull());
			exercisePane.visibleProperty().bind(selectedExercise.isNotNull());
			exercisesList.setItems(new SortedList<>(exercises));
			
			// listeners
			selectedExercise.addListener((o, ov, nv) -> onSelectedExerciseChanged(o, ov, nv));

			// refresh exercises
			refreshExercises();

		} catch (IOException e) {

			e.printStackTrace();
			System.exit(1);

		}

	}

	private void onSelectedExerciseChanged(ObservableValue<? extends File> o, File ov, File nv) {
		exerciseController.setExercise(nv != null ? Exercise.load(nv) : null);
	}

	public void refreshExercises() {
		exercises.setAll(Arrays.asList(Config.exercisesDir.listFiles(f -> f.isDirectory() && new File(f, Exercise.EXERCISE_FILENAME).exists())));
	}

	@FXML
	private void onImportExercise(ActionEvent event) {

		File file = Dialogs.chooseFileOrFolder();
		if (file != null) {
			importExercise(file);
		}

	}

	private void importExercise(final File file) {
		
		ImportExerciseTask task = new ImportExerciseTask(file);
		task.setOnScheduled(event -> {
			ExerciseController.me.showConsole();
			ConsoleController.me.clearConsole();
		});
		task.setOnSucceeded(event -> {
			exercises.add(task.getExerciseDir());
			exercisesList.getSelectionModel().select(task.getExerciseDir());
			Platform.runLater(() -> {
				exercisesList.requestFocus();
			});
		});
		task.setOnFailed(event -> {
			event.getSource().getException().printStackTrace();
			Dialogs.error("Error procesando entregas", event.getSource().getException());
		});
		task.start();

		ProgressDialog progressDialog = new ProgressDialog(task);
		progressDialog.initOwner(Reviser.primaryStage);
		progressDialog.setTitle("Importando ejercicio...");
		progressDialog.setHeaderText(file.getName());
		progressDialog.showAndWait();		
		
	}

	@FXML
	private void onRefreshExercises(ActionEvent event) {
		refreshExercises();
	}

	public SplitPane getView() {
		return view;
	}

	public void removeExercise(Exercise s) {
		String title = s.getDirectory().getName();
		if (Dialogs.confirm("Eliminar ejercicio", "Se va a eliminar el ejercicio '" + title + "' con todas las entregas.", "¿Desea continuar?"))
			try {
				exercises.get().remove(s.getDirectory());
				FileUtils.deleteDirectory(s.getDirectory());
			} catch (IOException e) {
				Dialogs.error("Error al eliminar el ejercicio '" + title + "'.", e);
			}
	}
	
    @FXML
    void onExercisesListDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
        	db.getFiles().forEach(f -> importExercise(f));
            success = true;
        }
        event.setDropCompleted(success);
        event.consume();
    }

    @FXML
    void onExercisesListDragOver(DragEvent event) {
        if (event.getGestureSource() != exercisesList && event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

}
