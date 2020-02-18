package fvarrui.reviser.ui;

import java.io.File;
import java.io.IOException;

import org.codehaus.plexus.util.cli.CommandLineException;

import fvarrui.reviser.utils.ZipUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {
	
	public static final String TITLE = "Reviser";

	public static File configDir = new File(System.getProperty("user.home"), ".Reviser"); 
	public static Stage primaryStage;
	
	private ObjectProperty<File> submissionsDir = new SimpleObjectProperty<>();
	
	private MainController controller;
	
	@Override
	public void init() throws Exception {
		if (!configDir.exists()) configDir.mkdir();
		super.init();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		App.primaryStage = primaryStage;

		controller = new MainController();
		controller.submissionsDirProperty().bind(submissionsDir);
		
		Scene scene = new Scene(controller.getView(), 900, 700);

		primaryStage.getIcons().add(new Image("/images/logo-16x16.png"));
		primaryStage.getIcons().add(new Image("/images/logo-24x24.png"));
		primaryStage.getIcons().add(new Image("/images/logo-32x32.png"));
		primaryStage.getIcons().add(new Image("/images/logo-64x64.png"));
		primaryStage.setScene(scene);
		primaryStage.show();

		openSubmissions();
		
	}

	private void openSubmissions() throws IOException, CommandLineException {
		File file = Dialogs.chooseFileOrFolder();
		if (file == null) {
			Platform.exit();
		} else if (file.isFile()) {
			submissionsDir.set(ZipUtils.uncompress(file, true));
			if (Dialogs.confirm(
					TITLE, 
					"Eliminar fichero original", 
					"¿Quiere eliminar el fichero '" + file.getName() + "'?")
					) {
				file.delete();
			}
		} else {
			submissionsDir.set(file);
		}
	}
	

	public static void main(String[] args) {
		launch(args);
	}

}
