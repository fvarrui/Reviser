package io.github.fvarrui.reviser.ui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import io.github.fvarrui.reviser.model.Grade;
import io.github.fvarrui.reviser.model.GradingForm;
import io.github.fvarrui.reviser.model.Submission;
import io.github.fvarrui.reviser.ui.tasks.RunSubmissionTask;
import io.github.fvarrui.reviser.ui.utils.Dialogs;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.converter.NumberStringConverter;

public class GradingController implements Initializable {
	
	// model
	
	private ChangeListener<Number> listener = (o, ov, nv) -> getSubmission().updateScore();

	private ObjectProperty<File> submissionsDir = new SimpleObjectProperty<>();
	private ObjectProperty<GradingForm> form = new SimpleObjectProperty<>();
	private ObjectProperty<Submission> submission = new SimpleObjectProperty<>();

	private StringProperty name = new SimpleStringProperty();
	private BooleanProperty evaluated = new SimpleBooleanProperty();
	private DoubleProperty score = new SimpleDoubleProperty();
	private ListProperty<Grade> grades = new SimpleListProperty<>(FXCollections.observableArrayList());

	// view

	@FXML
	private BorderPane view;

	@FXML
	private VBox criteriaPane;

	@FXML
	private Label nameLabel, gradeLabel;

	@FXML
	private TextField weightText;

	@FXML
	private Button runButton, clearButton;

    @FXML
    private TableView<Grade> gradesTable;

    @FXML
    private TableColumn<Grade, String> nameColumn;

    @FXML
    private TableColumn<Grade, Number> weightColumn;

    @FXML
    private TableColumn<Grade, Number> valueColumn;

    @FXML
    private TableColumn<Grade, Number> weightedColumn;
    
    @FXML
    private TableColumn<Grade, String> feedbackColumn;
    
    @FXML
    private CheckBox evaluatedCheck;

	public GradingController() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GradingView.fxml"));
		loader.setController(this);
		loader.load();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		submission.addListener((o, ov, nv) -> onResultChanged(o, ov, nv));
		
		// binds 
		gradesTable.itemsProperty().bind(grades);
		nameLabel.textProperty().bind(name);
		gradeLabel.textProperty().bindBidirectional(score, new NumberStringConverter());
		evaluatedCheck.selectedProperty().bindBidirectional(evaluated);
		
		// disables run and clear buttons when no result selected
		view.disableProperty().bind(submission.isNull());
		gradesTable.disableProperty().bind(evaluated);
		clearButton.disableProperty().bind(evaluated);
		runButton.disableProperty().bind(evaluated);
		
		// set cell value factories
		nameColumn.setCellValueFactory(v -> v.getValue().getCriterion().nameProperty());
		weightColumn.setCellValueFactory(v -> v.getValue().getCriterion().weightProperty());
		valueColumn.setCellValueFactory(v -> v.getValue().valueProperty());
		weightedColumn.setCellValueFactory(v -> v.getValue().weightedValueProperty());
		feedbackColumn.setCellValueFactory(v -> v.getValue().feedbackProperty());
		
		valueColumn.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
		feedbackColumn.setCellFactory(TextFieldTableCell.forTableColumn());

		// divides table width between the columns
		nameColumn.prefWidthProperty().bind(gradesTable.widthProperty().multiply(0.2));
		weightColumn.prefWidthProperty().bind(gradesTable.widthProperty().multiply(0.075));
		valueColumn.prefWidthProperty().bind(gradesTable.widthProperty().multiply(0.1));
		weightedColumn.prefWidthProperty().bind(gradesTable.widthProperty().multiply(0.1));
		feedbackColumn.prefWidthProperty().bind(gradesTable.widthProperty().multiply(0.5));
        
        gradesTable.getSelectionModel().setCellSelectionEnabled(true);

	}

	private void onResultChanged(ObservableValue<? extends Submission> o, Submission ov, Submission nv) {
		System.out.println("onResultChanged(ov=" + ov + "/nv=" + nv + ")");
		
		if (ov != null) {
			
			grades.stream().forEach(g -> g.valueProperty().removeListener(listener));
			grades.set(null);
			
			name.unbind();
			score.unbind();
			evaluated.unbindBidirectional(ov.evaluatedProperty());
			
		}
		
		if (nv != null) {
			
			grades.set(nv.getGrades());
			grades.stream().forEach(g -> g.valueProperty().addListener(listener));
			
			name.bind(nv.nameProperty());
			score.bind(nv.scoreProperty());
			evaluated.bindBidirectional(nv.evaluatedProperty());
			
		} else {
			name.set("");
			score.set(0);
			evaluated.set(false);
		}
	}

	// events

	@FXML
	private void onRun(ActionEvent e) {
		File inputFile = new File(getSubmissionsDir(), "input.txt");
		RunSubmissionTask task = new RunSubmissionTask(inputFile, getSubmission());
		task.setOnScheduled(event -> {
			ExerciseController.me.showConsole();
			ConsoleController.me.clearConsole();
		});
		task.setOnFailed(event -> {
			App.console.println(event.getSource().getException());
			getSubmission().fail(event.getSource().getException().getMessage());
		});
		task.start();
	}

	@FXML
	private void onClearScore(ActionEvent e) {
		if (Dialogs.confirm(App.TITLE, "Limpiar las puntuaciones y los comentarios de '" + submission.get().getName() + "'.", "¿Está seguro?")) {
			getSubmission().resetScore();
		}
	}
	
	@FXML
	private void onOpenExplorer(ActionEvent e) {
		File folder = new File(getSubmissionsDir(), getSubmission().getDirectory());
		try {
			Desktop.getDesktop().open(folder);
		} catch (IOException e1) {
			Dialogs.error("Error al abrir la carpeta '" + folder + "' en el explorador de archivos del sistema", e1);
		}
	}
	
	@FXML
	private void onPerfect(ActionEvent e) {
		getSubmission().setEvaluated(true);
		getSubmission().getGrades().forEach(g -> g.setValue(100));
	}

	// getters and setters

	public BorderPane getView() {
		return view;
	}

	public final ObjectProperty<GradingForm> formProperty() {
		return this.form;
	}

	public final GradingForm getForm() {
		return this.formProperty().get();
	}

	public final void setForm(final GradingForm form) {
		this.formProperty().set(form);
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

	public final ObjectProperty<File> submissionsDirProperty() {
		return this.submissionsDir;
	}

	public final File getSubmissionsDir() {
		return this.submissionsDirProperty().get();
	}

	public final void setSubmissionsDir(final File submissionsDir) {
		this.submissionsDirProperty().set(submissionsDir);
	}

}
