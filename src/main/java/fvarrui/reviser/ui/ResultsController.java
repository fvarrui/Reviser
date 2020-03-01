package fvarrui.reviser.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import fvarrui.reviser.model.Result;
import fvarrui.reviser.model.Results;
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

public class ResultsController implements Initializable {

	// controllers

	private FormController formController;

	// model

	private ObjectProperty<File> submissionsDir = new SimpleObjectProperty<>();
	private ObjectProperty<Results> results = new SimpleObjectProperty<>(new Results());

	// view

	@FXML
	private BorderPane view;

	@FXML
	private TableView<Result> resultsTable;

	@FXML
	private TableColumn<Result, String> nameColumn;

	@FXML
	private TableColumn<Result, Number> scoreColumn;

	@FXML
	private TableColumn<Result, String> emailColumn;

	@FXML
	private TableColumn<Result, String> feedbackColumn;

	@FXML
	private TableColumn<Result, String> directoryColumn;
	
    @FXML
    private TableColumn<Result, Boolean> evaluatedColumn;

	@FXML
	private BorderPane formPane;

	public ResultsController() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ResultsView.fxml"));
		loader.setController(this);
		loader.load();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		try {

			// creates controllers
			formController = new FormController();

			// add listener when results changed
			results.addListener((o, ov, nv) -> onResultsChanged(o, ov, nv));

			// set cell value factories
			nameColumn.setCellValueFactory(v -> v.getValue().nameProperty());
			emailColumn.setCellValueFactory(v -> v.getValue().emailProperty());
			directoryColumn.setCellValueFactory(v -> v.getValue().directoryProperty());
			feedbackColumn.setCellValueFactory(v -> v.getValue().feedbackProperty());
			scoreColumn.setCellValueFactory(v -> v.getValue().scoreProperty());
			evaluatedColumn.setCellValueFactory(v -> v.getValue().evaluatedProperty());
			
			feedbackColumn.setCellFactory(TextFieldTableCell.forTableColumn());
			evaluatedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(evaluatedColumn));

			// divides table width between the columns
			nameColumn.prefWidthProperty().bind(resultsTable.widthProperty().multiply(0.20));
			emailColumn.prefWidthProperty().bind(resultsTable.widthProperty().multiply(0.20));
			directoryColumn.prefWidthProperty().bind(resultsTable.widthProperty().multiply(0.20));
			feedbackColumn.prefWidthProperty().bind(resultsTable.widthProperty().multiply(0.20));
			scoreColumn.prefWidthProperty().bind(resultsTable.widthProperty().multiply(0.075));
			evaluatedColumn.prefWidthProperty().bind(resultsTable.widthProperty().multiply(0.075));

			// set grading form
			formPane.setCenter(formController.getView());

			// form controller bindings
			formController.submissionsDirProperty().bind(submissionsDir);
			formController.resultProperty().bind(resultsTable.getSelectionModel().selectedItemProperty());

		} catch (IOException e) {

			e.printStackTrace();
			System.exit(1);

		}

	}

	private void onResultsChanged(ObservableValue<? extends Results> o, Results ov, Results nv) {
		if (ov != null) {
			resultsTable.itemsProperty().unbind();
			formController.formProperty().unbind();
		}
		if (nv != null) {
			resultsTable.itemsProperty().bind(nv.resultsProperty());
			formController.formProperty().bind(nv.formProperty());
		}
	}

	public BorderPane getView() {
		return view;
	}

	public final ObjectProperty<File> submissionsDirProperty() {
		return this.submissionsDir;
	}

	public final File getSubmissionsDir() {
		return this.submissionsDirProperty().get();
	}

	public final void setSubmissionsDir(final File submissionsDir) {
		this.submissionsDirProperty().set(submissionsDir);
	}

	public final ObjectProperty<Results> resultsProperty() {
		return this.results;
	}

	public final Results getResults() {
		return this.resultsProperty().get();
	}

	public final void setResults(final Results results) {
		this.resultsProperty().set(results);
	}

}
