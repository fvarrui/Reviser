package io.github.fvarrui.reviser.ui;

import java.io.File;
import java.util.Optional;

import org.controlsfx.dialog.CommandLinksDialog;
import org.controlsfx.dialog.CommandLinksDialog.CommandLinksButtonType;

import io.github.fvarrui.reviser.config.Config;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class Dialogs {
	
	public static File chooseFileOrFolder() {
		CommandLinksDialog dialog = new CommandLinksDialog(
				new CommandLinksButtonType("Directorio de entregas", "Importar directamente un directorio de entregas.", true),				
				new CommandLinksButtonType("Fichero de entregas", "Importar desde un fichero ZIP con las entregas descargadas de Moodle.", false)
				);
		dialog.initOwner(App.primaryStage);
		dialog.setTitle(App.TITLE);
		dialog.setHeaderText("Seleccione desde donde quiere importar las entregas");
		Optional<ButtonType> result = dialog.showAndWait();
		if (result.get().getButtonData() == ButtonData.OK_DONE) {
			return openFolder("Importar un directorio de entregas");
		} else if (result.get().getButtonData() == ButtonData.OTHER)  { 
			return openFile("Importar un fichero ZIP con entregas descargado de Moodle", "Fichero de entregas", "*.zip");
		}
		return null;
	}

	public static File openFolder(String title) {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setInitialDirectory(new File(Config.getConfig().getLastDirectory()));
		chooser.setTitle(title);
		File directory = chooser.showDialog(App.primaryStage);
		if (directory != null) Config.getConfig().setLastDirectory(directory.getParentFile().getAbsolutePath());		
		return directory;
	}

	public static File openFile(String title, String filename, String description, String extension) {
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory(new File(Config.getConfig().getLastDirectory()));
		chooser.setInitialFileName(filename);
		chooser.setTitle(title);
		chooser.getExtensionFilters().addAll(
		    new ExtensionFilter(description, extension),
		    new ExtensionFilter("Todos los ficheros", "*.*")
		    );
		File file = chooser.showOpenDialog(App.primaryStage);
		if (file != null) Config.getConfig().setLastDirectory(file.getParentFile().getAbsolutePath());
		return file;
	}
	
	public static File saveFile(String title, String filename, String description, String extension) {
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory(new File(Config.getConfig().getLastDirectory()));
		chooser.setInitialFileName(filename);
		chooser.setTitle(title);
		chooser.getExtensionFilters().addAll(
		    new ExtensionFilter(description, extension),
		    new ExtensionFilter("Todos los ficheros", "*.*")
		    );
		File file = chooser.showSaveDialog(App.primaryStage);
		if (file != null) Config.getConfig().setLastDirectory(file.getParentFile().getAbsolutePath());
		return file;
	}
	
	public static File openFile(String title, String description, String extension) {
		return openFile(title, "", description, extension);
	}
	
	public static boolean confirm(String title, String header, String content) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
		alert.initOwner(App.primaryStage);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		Optional<ButtonType> response = alert.showAndWait();
		return ButtonType.YES.equals(response.get());
	}
	
	public static void error(String header, String content) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.initOwner(App.primaryStage);
		alert.setTitle("Error");
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}
	
	public static void error(String header, Throwable e) {
		e.printStackTrace();
		error(header, e.getMessage());
	}

	public static void info(String title, String header) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initOwner(App.primaryStage);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.showAndWait();
	}


}
