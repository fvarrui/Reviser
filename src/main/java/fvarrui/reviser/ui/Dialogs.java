package fvarrui.reviser.ui;

import java.io.File;
import java.util.Optional;

import org.controlsfx.dialog.CommandLinksDialog;
import org.controlsfx.dialog.CommandLinksDialog.CommandLinksButtonType;

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
				new CommandLinksButtonType("Directorio de entregas", "Abrir directamente un directorio de entregas.", true),				
				new CommandLinksButtonType("Fichero de entregas", "Abrir desde un fichero ZIP con las entregas descargadas de Moodle.", false)
				);
		dialog.initOwner(App.primaryStage);
		dialog.setTitle(App.TITLE);
		dialog.setHeaderText("Seleccione desde donde quiere cargar las entregas a corregir");
		Optional<ButtonType> result = dialog.showAndWait();
		if (result.get().getButtonData() == ButtonData.OK_DONE) {
			return chooseFolder("Abrir un directorio de entregas");
		} else if (result.get().getButtonData() == ButtonData.OTHER)  { 
			return chooseFile("Abrir un fichero ZIP con entregas descargado de Moodle", "Fichero de entregas", "*.zip");
		}
		return null;
	}

	public static File chooseFolder(String title) {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setInitialDirectory(new File("."));
		chooser.setTitle(title);
		return chooser.showDialog(App.primaryStage);
	}

	public static File chooseFile(String title, String filename, String description, String extension) {
		FileChooser chooser = new FileChooser();
		chooser.setInitialFileName(filename);
		chooser.setInitialDirectory(new File("."));
		chooser.setTitle(title);
		chooser.getExtensionFilters().addAll(
		    new ExtensionFilter(description, extension),
		    new ExtensionFilter("Todos los ficheros", "*.*")
		    );
		return chooser.showOpenDialog(App.primaryStage);		
	}
	
	public static File chooseFile(String title, String description, String extension) {
		return chooseFile(title, "", description, extension);
	}
	
	public static boolean confirm(String title, String header, String content) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.initOwner(App.primaryStage);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		Optional<ButtonType> response = alert.showAndWait();
		return ButtonType.OK.equals(response.get());
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
