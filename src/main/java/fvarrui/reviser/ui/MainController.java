package fvarrui.reviser.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.opencsv.exceptions.CsvException;

import fvarrui.reviser.Testing;
import fvarrui.reviser.csv.CsvResult;
import fvarrui.reviser.csv.CsvStudent;
import fvarrui.reviser.csv.CsvUtils;
import fvarrui.reviser.model.Criterion;
import fvarrui.reviser.model.GradingForm;
import fvarrui.reviser.model.Result;
import fvarrui.reviser.model.Results;
import fvarrui.reviser.utils.JSONUtils;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

public class MainController implements Initializable {
	
	// controllers
	
	private FormDesignerController formDesignerController;
	private ConsoleController consoleController;

	// model
	
	private File resultsFile;

	private StringProperty project = new SimpleStringProperty("");
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
    private TableColumn<Result, String> directoryColumn;

    @FXML
    private TextArea consoleText;
    
    @FXML
    private Tab formDesignerTab, consoleTab;

	public MainController() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
		loader.setController(this);
		loader.load();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		try {
			formDesignerController = new FormDesignerController();
			consoleController = new ConsoleController();
		} catch (IOException e) {
			Dialogs.error("Error instanciando controlador", e);
			Platform.exit();
		}
		
		App.primaryStage.titleProperty().bind(Bindings.concat(App.TITLE + ": ").concat(project));

		submissionsDir.addListener((o, ov, nv) -> onSubmissionsDirChanged(o, ov, nv));
		
		results.addListener((o, ov, nv) -> onResultsChanged(o, ov, nv));
		
		nameColumn.setCellValueFactory(v -> v.getValue().nameProperty());
		emailColumn.setCellValueFactory(v -> v.getValue().emailProperty());
		directoryColumn.setCellValueFactory(v -> v.getValue().directoryProperty());
		scoreColumn.setCellValueFactory(v -> v.getValue().scoreProperty());

		nameColumn.prefWidthProperty().bind(resultsTable.widthProperty().multiply(0.25));
		emailColumn.prefWidthProperty().bind(resultsTable.widthProperty().multiply(0.25));
		directoryColumn.prefWidthProperty().bind(resultsTable.widthProperty().multiply(0.40));
		scoreColumn.prefWidthProperty().bind(resultsTable.widthProperty().multiply(0.10));
	
		formDesignerTab.setContent(formDesignerController.getView());
		consoleTab.setContent(consoleController.getView());
	}

	private void onResultsChanged(ObservableValue<? extends Results> o, Results ov, Results nv) {

		resultsTable.itemsProperty().bind(nv.resultsProperty());
				
	}

	private void onSubmissionsDirChanged(ObservableValue<? extends File> o, File ov, File nv) {
		
		// sets project name
		project.set(submissionsDir.get().getName());
		
		// loads results from json if exists, or creates from scratch 
		resultsFile = new File(submissionsDir.get(), "results.json");
		results.set(Testing.loadResultsFromJson(resultsFile, submissionsDir.get()));

		// init grading form
		if (results.get().getForm() == null) {
			GradingForm form = new GradingForm();
			form.getCriteria().add(new Criterion());
			results.get().setForm(form);
		}

		// binds to grading form
		formDesignerController.formProperty().bind(results.get().formProperty());
		
	}

    @FXML
    private void onExportResults(ActionEvent e) {

    	// export results to csv file for moodle
		File resultsFile = Dialogs.chooseFile("Export results for Moodle", getSubmissionsDir().getName(), "Results CSV file", "*.csv");
		if (resultsFile != null)
			try {
				List<CsvResult> results = this.results.get().getResults().stream()
						.map(r -> new CsvResult(r.getScore(), r.getName(), r.getFeedback(), r.getEmail()))
						.collect(Collectors.toList());
				CsvUtils.resultsToCsv(results, resultsFile);
			} catch (IOException | CsvException e1) {
				Dialogs.error("Results CSV file couldn't be exported", e1);
			}
    	
    }

    @FXML
    private void onImportEmails(ActionEvent e) {

    	// import emails from csv file
		File studentsFile = Dialogs.chooseFile("Select a students CSV file exported from Moodle", "Students CSV file", "*.csv");
		if (studentsFile != null)
			try {
				List<CsvStudent> students = CsvUtils.csvToStudents(studentsFile);
				Testing.updateResultsFromStudents(results.get(), students);
			} catch (IOException | CsvException e1) {
				Dialogs.error("CSV file couldn't be opened", e1);
			}
		
    }

    @FXML
    private void onSaveResults(ActionEvent e) {
    	
    	// save results to json file in submissions directory
    	try {
			JSONUtils.jsonToFile(results.get(), resultsFile);
			Dialogs.info("Results saved", "Results have been saved to '" + resultsFile + "'.");
		} catch (JsonSyntaxException | JsonIOException | IOException e1) {
			Dialogs.error("Error saving results to '" + resultsFile.getName() + "'", e1);
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

}
