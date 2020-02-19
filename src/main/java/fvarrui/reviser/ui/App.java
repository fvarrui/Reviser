package fvarrui.reviser.ui;

import java.io.File;
import java.io.IOException;

import org.codehaus.plexus.util.cli.CommandLineException;

import fvarrui.reviser.config.Config;
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

	public static MessageConsumer console;
	public static Stage primaryStage;
	
	private ObjectProperty<File> submissionsDir = new SimpleObjectProperty<>();
	
	public static MainController mainController;
	
	@Override
	public void init() throws Exception {
		Config.load();
		super.init();
	}
	
	@Override
	public void stop() throws Exception {
		Config.save();
		super.stop();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		App.primaryStage = primaryStage;

		mainController = new MainController();
		mainController.submissionsDirProperty().bind(submissionsDir);
		
		Scene scene = new Scene(mainController.getView(), 900, 700);

		primaryStage.getIcons().add(new Image("/images/logo-16x16.png"));
		primaryStage.getIcons().add(new Image("/images/logo-24x24.png"));
		primaryStage.getIcons().add(new Image("/images/logo-32x32.png"));
		primaryStage.getIcons().add(new Image("/images/logo-64x64.png"));
		primaryStage.setScene(scene);
		primaryStage.setOnCloseRequest(e -> {
			if (Dialogs.confirm(TITLE, "Saliendo de la aplicación", "¿Desea guardar los cambios antes de salir?")) {
				mainController.saveResults();
			}
		});
		primaryStage.show();

		openSubmissions();
		
	}

	private void openSubmissions() throws IOException, CommandLineException {
		File file = Dialogs.chooseFileOrFolder();
		if (file == null) {
			Platform.exit();
		} else if (file.isFile()) {
			submissionsDir.set(ZipUtils.uncompress(file, Config.configDir));
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
