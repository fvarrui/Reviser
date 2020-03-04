package fvarrui.reviser.ui;

import static org.apache.commons.io.FileUtils.copyDirectory;
import static org.apache.commons.io.FileUtils.deleteDirectory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.util.cli.CommandLineException;

import fvarrui.reviser.config.Config;
import fvarrui.reviser.model.Submission;
import fvarrui.reviser.utils.ZipUtils;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
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

			// creates and binds controllers
			submissionController = new SubmissionController();
			submissionController.submissionProperty().bind(selectedSubmission);

			// set submission controller view
			submissionPane.setCenter(submissionController.getView());

			// binds
			selectedSubmission.bind(submissionsList.getSelectionModel().selectedItemProperty());
			noSelectionPane.visibleProperty().bind(selectedSubmission.isNull());
			submissionPane.visibleProperty().bind(selectedSubmission.isNotNull());
			submissionsList.setItems(new SortedList<>(submissions, Submission::compareTo));

			// refresh submissions
			refreshSubmissions();

		} catch (IOException e) {

			e.printStackTrace();
			System.exit(1);

		}

	}

	public void refreshSubmissions() {
		submissions.setAll(Arrays.asList(Config.submissionsDir.listFiles()).stream().filter(f -> f.isDirectory())
				.map(f -> new Submission(f)).collect(Collectors.toList()));
	}

	@FXML
	private void onImportSubmission(ActionEvent event) {

		File file = Dialogs.chooseFileOrFolder();
		if (file != null) {

			if (file.isFile()) {
				try {
					file = ZipUtils.uncompress(file, Config.submissionsDir);
				} catch (IOException | CommandLineException e) {
					Dialogs.error("Error al importar un fichero de entregas", e);
					return;
				}
			} else if (file.isDirectory()) {
				try {
					File destination = new File(Config.submissionsDir, file.getName());
					copyDirectory(file, destination);
					if (Dialogs.confirm("Importar directorio de entregas", "Eliminar directorio original",
							"¿Desea eliminar el directorio '" + file.getAbsolutePath() + "'?")) {
						deleteDirectory(file);
					}
					file = destination;
				} catch (IOException e) {
					Dialogs.error("Error al importar un directorio de entregas", e);
					return;
				}
			}

			Submission s = new Submission(file);
			submissions.add(new Submission(file));
			submissionsList.getSelectionModel().select(s);

			Platform.runLater(() -> submissionsList.requestFocus());

		}

	}

	@FXML
	private void onRefreshSubmissions(ActionEvent event) {
		refreshSubmissions();
	}

	public SplitPane getView() {
		return view;
	}

	public void removeSubmission(Submission s) {
		String title = s.getDirectory().getName();
		if (Dialogs.confirm("Eliminar entregas", "Se van a eliminar todas las entregas de la tarea '" + title + "'.", "¿Desea continuar?"))
			try {
				submissions.get().remove(s);
				FileUtils.deleteDirectory(s.getDirectory());
			} catch (IOException e) {
				Dialogs.error("Error al eliminar la entrega '" + title + "'.", e);
			}
	}

}
