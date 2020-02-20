package fvarrui.reviser.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

public class ConsoleController implements Initializable {

	// model
	
	private StringProperty console = new SimpleStringProperty();

	// view

	@FXML
	private BorderPane view;

	@FXML
	private TextArea consoleText;

	@FXML
	private Button clearButton;

	public ConsoleController() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ConsoleView.fxml"));
		loader.setController(this);
		loader.load();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		App.console = new MessageConsumer(consoleText);
		App.console.start();
		
		console.bindBidirectional(consoleText.textProperty());
		clearButton.disableProperty().bind(console.isEmpty());
	}
	
	public BorderPane getView() {
		return view;
	}

	public void clearConsole() {
		consoleText.clear();
	}
	
	@FXML
	private void onClear(ActionEvent e) {
		clearConsole();
	}

}
