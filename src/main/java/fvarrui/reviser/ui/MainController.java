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

import fvarrui.reviser.csv.CsvResult;
import fvarrui.reviser.csv.CsvStudent;
import fvarrui.reviser.csv.CsvUtils;
import fvarrui.reviser.json.JSONUtils;
import fvarrui.reviser.model.Results;
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
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

public class MainController implements Initializable {
	
	// controllers
	
	private ResultsController resultsController;
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
    private TabPane tabPane;
    
    @FXML
    private Tab resultsTab, formDesignerTab, consoleTab;

	public MainController() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
		loader.setController(this);
		loader.load();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		try {

			// binds stage title
			App.primaryStage.titleProperty().bind(Bindings.concat(App.TITLE + ": ").concat(project));

			// creates controllers
			resultsController = new ResultsController();
			formDesignerController = new FormDesignerController();
			consoleController = new ConsoleController();
		
			// set tabs content
			resultsTab.setContent(resultsController.getView());
			formDesignerTab.setContent(formDesignerController.getView());
			consoleTab.setContent(consoleController.getView());
	
			// add listener when submissions dir changed
			submissionsDir.addListener((o, ov, nv) -> onSubmissionsDirChanged(o, ov, nv));
			
		} catch (IOException e) {
			
			e.printStackTrace();
			System.exit(1);
			
		}
			
	}

	private void onSubmissionsDirChanged(ObservableValue<? extends File> o, File ov, File nv) {
		
		// sets project name
		project.set(submissionsDir.get().getName());
		
		// loads results from json if exists, or creates from scratch 
		resultsFile = new File(submissionsDir.get(), "results.json");
		results.set(Results.load(resultsFile, submissionsDir.get()));

		// binds results view
		resultsController.submissionsDirProperty().bind(submissionsDir);
		resultsController.resultsProperty().bind(results);
		
		// binds form designer view
		formDesignerController.resultsProperty().bind(results);
		
	}

    @FXML
    private void onExportResults(ActionEvent e) {

    	// export results to csv file for moodle
		File resultsFile = Dialogs.chooseFile("Exportar resultados en CSV para Moodle", getSubmissionsDir().getName(), "Fichero CSV", "*.csv");
		if (resultsFile != null)
			try {
				List<CsvResult> results = this.results.get().getResults().stream()
						.map(r -> new CsvResult(r.getScore(), r.getName(), r.getFeedback(), r.getEmail()))
						.collect(Collectors.toList());
				CsvUtils.resultsToCsv(results, resultsFile);
			} catch (IOException | CsvException e1) {
				Dialogs.error("El fichero CSV de resultados no ha podido exportarse", e1);
			}
    	
    }

    @FXML
    private void onImportEmails(ActionEvent e) {

    	// import emails from csv file
		File studentsFile = Dialogs.chooseFile("Seleccione un fichero CSV de calificaciones exportado desde Moodle", "Fichero CSV", "*.csv");
		if (studentsFile != null)
			try {
				List<CsvStudent> students = CsvUtils.csvToStudents(studentsFile);
				results.get().updateFromStudents(students);
			} catch (IOException | CsvException e1) {
				Dialogs.error("El fichero CSV no se ha podido abrir", e1);
			}
				
    }

    @FXML
    private void onSaveResults(ActionEvent e) {
    	saveResults();
    }

	public void saveResults() {
		// save results to json file in submissions directory
    	try {
			JSONUtils.jsonToFile(results.get(), resultsFile);
			Dialogs.info("Resultados guardados", "Los resultados se han guardado correctamente en '" + resultsFile + "'.");
		} catch (JsonSyntaxException | JsonIOException | IOException e1) {
			Dialogs.error("Error guardando resultados en '" + resultsFile.getName() + "'", e1);
		}
	}
    
    public void showConsole() {
    	tabPane.getSelectionModel().select(consoleTab);
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
