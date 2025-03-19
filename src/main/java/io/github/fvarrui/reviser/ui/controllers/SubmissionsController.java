package io.github.fvarrui.reviser.ui.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import io.github.fvarrui.reviser.model.Exercise;
import io.github.fvarrui.reviser.model.Submission;
import io.github.fvarrui.reviser.model.SubmissionType;
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

	private final GradingController gradingController = new GradingController();

	// model

	private final ObjectProperty<Exercise> exercise = new SimpleObjectProperty<>();

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
	private TableColumn<Submission, String> projectTypeColumn;

	@FXML
	private TableColumn<Submission, SubmissionType> submissionTypeColumn;

	@FXML
	private BorderPane gradingPane;

	public SubmissionsController() {
		try { 
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SubmissionsView.fxml"));
			loader.setController(this);
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// add listener when results changed
		exercise.addListener(this::onExerciseChanged);

		// set cell value factories
		nameColumn.setCellValueFactory(v -> v.getValue().nameProperty());
		emailColumn.setCellValueFactory(v -> v.getValue().emailProperty());
		directoryColumn.setCellValueFactory(v -> v.getValue().directoryProperty());
		feedbackColumn.setCellValueFactory(v -> v.getValue().feedbackProperty());
		scoreColumn.setCellValueFactory(v -> v.getValue().scoreProperty());
		evaluatedColumn.setCellValueFactory(v -> v.getValue().evaluatedProperty());
		projectTypeColumn.setCellValueFactory(v -> v.getValue().testerProperty().asString());
		submissionTypeColumn.setCellValueFactory(v -> v.getValue().typeProperty());
		
		// set cell factories		
		feedbackColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		emailColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		evaluatedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(evaluatedColumn));

		// divides table width between the columns
		nameColumn.prefWidthProperty().bind(submissionsTable.widthProperty().multiply(0.17));
		emailColumn.prefWidthProperty().bind(submissionsTable.widthProperty().multiply(0.12));
		directoryColumn.prefWidthProperty().bind(submissionsTable.widthProperty().multiply(0.20));
		feedbackColumn.prefWidthProperty().bind(submissionsTable.widthProperty().multiply(0.20));
		scoreColumn.prefWidthProperty().bind(submissionsTable.widthProperty().multiply(0.075));
		evaluatedColumn.prefWidthProperty().bind(submissionsTable.widthProperty().multiply(0.05));
		projectTypeColumn.prefWidthProperty().bind(submissionsTable.widthProperty().multiply(0.08));
		submissionTypeColumn.prefWidthProperty().bind(submissionsTable.widthProperty().multiply(0.08));

		// set grading form
		gradingPane.setCenter(gradingController.getView());

		// form controller bindings
		gradingController.submissionProperty().bind(submissionsTable.getSelectionModel().selectedItemProperty());
		
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
			
			// sort tableview by name
			submissionsTable.getSortOrder().clear();
			submissionsTable.getSortOrder().add(nameColumn);
			
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
