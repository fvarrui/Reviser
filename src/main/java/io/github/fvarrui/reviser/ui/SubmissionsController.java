package io.github.fvarrui.reviser.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import io.github.fvarrui.reviser.model.Exercise;
import io.github.fvarrui.reviser.model.Submission;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;

public class SubmissionsController implements Initializable {

	// controllers

	private GradingController gradingController;

	// model

	private ObjectProperty<Exercise> exercise = new SimpleObjectProperty<>(new Exercise());

	// view

	@FXML
	private BorderPane view;

	@FXML
	private TableView<Submission> submissionsTable;

	@FXML
	private TableColumn<Submission, String> nameColumn;

	@FXML
	private TableColumn<Submission, Number> scoreColumn;

	@FXML
	private TableColumn<Submission, String> emailColumn;

	@FXML
	private TableColumn<Submission, String> feedbackColumn;

	@FXML
	private TableColumn<Submission, String> directoryColumn;

	@FXML
	private TableColumn<Submission, Boolean> evaluatedColumn;

	@FXML
	private BorderPane gradingPane;

	public SubmissionsController() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SubmissionsView.fxml"));
		loader.setController(this);
		loader.load();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		try {

			// creates controllers
			gradingController = new GradingController();

			// add listener when results changed
			exercise.addListener((o, ov, nv) -> onExerciseChanged(o, ov, nv));

			// set cell value factories
			nameColumn.setCellValueFactory(v -> v.getValue().nameProperty());
			emailColumn.setCellValueFactory(v -> v.getValue().emailProperty());
			directoryColumn.setCellValueFactory(v -> v.getValue().directoryProperty());
			feedbackColumn.setCellValueFactory(v -> v.getValue().feedbackProperty());
			scoreColumn.setCellValueFactory(v -> v.getValue().scoreProperty());
			evaluatedColumn.setCellValueFactory(v -> v.getValue().evaluatedProperty());

			feedbackColumn.setCellFactory(TextFieldTableCell.forTableColumn());
			emailColumn.setCellFactory(TextFieldTableCell.forTableColumn());
			evaluatedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(evaluatedColumn));

			// divides table width between the columns
			nameColumn.prefWidthProperty().bind(submissionsTable.widthProperty().multiply(0.20));
			emailColumn.prefWidthProperty().bind(submissionsTable.widthProperty().multiply(0.20));
			directoryColumn.prefWidthProperty().bind(submissionsTable.widthProperty().multiply(0.20));
			feedbackColumn.prefWidthProperty().bind(submissionsTable.widthProperty().multiply(0.20));
			scoreColumn.prefWidthProperty().bind(submissionsTable.widthProperty().multiply(0.075));
			evaluatedColumn.prefWidthProperty().bind(submissionsTable.widthProperty().multiply(0.075));

			// set grading form
			gradingPane.setCenter(gradingController.getView());

			// form controller bindings
			gradingController.submissionProperty().bind(submissionsTable.getSelectionModel().selectedItemProperty());

		} catch (IOException e) {

			e.printStackTrace();
			System.exit(1);

		}

	}

	private void onExerciseChanged(ObservableValue<? extends Exercise> o, Exercise ov, Exercise nv) {
		submissionsTable.getSelectionModel().clearSelection();
		if (ov != null) {
			submissionsTable.itemsProperty().unbind();
			submissionsTable.itemsProperty().get().clear();
			gradingController.formProperty().unbind();
			gradingController.submissionsDirProperty().unbind();	
		}
		if (nv != null) {
			submissionsTable.getItems().clear();
			submissionsTable.itemsProperty().bind(nv.submissionsProperty());
			gradingController.formProperty().bind(nv.formProperty());
			gradingController.submissionsDirProperty().bind(nv.directoryProperty());
		}
	}

	public BorderPane getView() {
		return view;
	}

	public GradingController getGradingController() {
		return gradingController;
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
