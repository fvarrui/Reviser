package fvarrui.reviser.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.codehaus.plexus.util.cli.CommandLineException;

import fvarrui.reviser.config.Config;
import fvarrui.reviser.model.Submission;
import fvarrui.reviser.utils.ZipUtils;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class MainController implements Initializable {
	
	// controllers
	
	private SubmissionController submissionController;

	// model
	
	private ListProperty<Submission> submissions = new SimpleListProperty<>(FXCollections.observableArrayList()); 
	private ObjectProperty<Submission> selectedSubmission = new SimpleObjectProperty<>();

	// view

	@FXML
	private SplitPane view;

    @FXML
    private ListView<Submission> submissionsList;

    @FXML
    private Button importButton;

    @FXML
    private Button refreshButton;
    
    @FXML
    private BorderPane submissionPane;
    
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

			// creates controllers
			submissionController = new SubmissionController();
		
			// set submission controller view
			submissionPane.setCenter(submissionController.getView());
	
			// binds
			selectedSubmission.bind(submissionsList.getSelectionModel().selectedItemProperty());
			noSelectionPane.visibleProperty().bind(selectedSubmission.isNull());
			submissionPane.visibleProperty().bind(selectedSubmission.isNotNull());
			submissionsList.itemsProperty().bind(submissions);
			submissionController.submissionProperty().bind(selectedSubmission);
			
			// refresh submissions
			refreshSubmissions();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			System.exit(1);
			
		}
			
	}

	public void refreshSubmissions() {
		submissions.setAll(
				Arrays.asList(Config.submissionsDir.listFiles())
					.stream()
					.filter(f -> f.isDirectory())
					.map(f -> new Submission(f))
					.collect(Collectors.toList())
				);
	}

    @FXML
    private void onImportSubmission(ActionEvent event) {
		File file = Dialogs.chooseFile("Importar entregas desde un fichero ZIP descargado de Moodle", "Fichero de entregas", "*.zip");
		if (file != null) {
			try {
				file = ZipUtils.uncompress(file, Config.submissionsDir);
				Submission s = new Submission(file);
				submissions.add(new Submission(file));
				submissionsList.getSelectionModel().select(s);
			} catch (IOException | CommandLineException e) {
				Dialogs.error("Error al importar un fichero de entregas", e);
			}
		} 
	}

    @FXML
    private void onRefreshSubmissions(ActionEvent event) {
    	refreshSubmissions();
    }

	public SplitPane getView() {
		return view;
	}

}
