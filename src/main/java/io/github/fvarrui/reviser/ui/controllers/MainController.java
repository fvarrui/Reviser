package io.github.fvarrui.reviser.ui.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import io.github.fvarrui.reviser.config.Config;
import io.github.fvarrui.reviser.model.Exercise;
import io.github.fvarrui.reviser.ui.tasks.ImportExerciseTask;
import io.github.fvarrui.reviser.ui.tasks.LoadExerciseTask;
import io.github.fvarrui.reviser.ui.tasks.RemoveExerciseTask;
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

	private ExerciseController exerciseController = new ExerciseController();

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

	public MainController() {
		try { 
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
			loader.setController(this);
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

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
		selectedExercise.addListener(this::onSelectedExerciseChanged);

		// refresh exercises
		refreshExercises();

	}

	private void onSelectedExerciseChanged(ObservableValue<? extends File> o, File ov, File nv) {
		if (nv != null) {
			LoadExerciseTask task = new LoadExerciseTask(nv);
			task.setOnScheduled(event -> {
				Dialogs.progress("Cargando ejercicio...", nv.getName(), task);				
			});
			task.setOnSucceeded(event -> {
				exerciseController.setExercise(task.getValue());			
			});
			task.setOnFailed(event -> {
				event.getSource().getException().printStackTrace();				
				Dialogs.error("Error cargando el ejercicio '" + nv.getName() + "'.", task.getException());
			});
			task.start();
		} else {
			exerciseController.setExercise(null);
		}
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
			ExerciseController.me.showResults();
			ConsoleController.me.clearConsole();
			Dialogs.progress("Importando ejercicio...", file.getName(), task);			
		});
		task.setOnSucceeded(event -> {
			exercises.add(task.getValue());
			exercisesList.getSelectionModel().select(task.getValue());
			Platform.runLater(() -> {
				exercisesList.requestFocus();
			});
		});
		task.setOnFailed(event -> {
			event.getSource().getException().printStackTrace();
			Dialogs.error("Error procesando entregas", event.getSource().getException());
		});
		task.start();
		
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
		if (Dialogs.confirm("Eliminar ejercicio", "Se va a eliminar el ejercicio '" + title + "' con todas las entregas.", "¿Desea continuar?")) {

			exercisesList.getSelectionModel().clearSelection();
			exercises.get().remove(s.getDirectory());

			RemoveExerciseTask task = new RemoveExerciseTask(s.getDirectory());	
			task.setOnScheduled(event -> {
				Dialogs.progress("Eliminando ejercicio...", title, task);				
			});
			task.setOnFailed(event -> {
				event.getSource().getException().printStackTrace();				
				Dialogs.error("Error al eliminar el ejercicio '" + title + "'.", task.getException());
			});
			task.start();

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
