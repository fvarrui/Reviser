package io.github.fvarrui.reviser.ui.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import io.github.fvarrui.reviser.ui.Reviser;
import io.github.fvarrui.reviser.ui.utils.MessageConsumer;
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
	
	public static ConsoleController me;

	// model
	
	private StringProperty console = new SimpleStringProperty();

	// view

	@FXML
	private BorderPane view;

	@FXML
	private TextArea consoleText;

	@FXML
	private Button clearButton;

	public ConsoleController() {
		try { 
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ConsoleView.fxml"));
			loader.setController(this);
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		me = this;
		
		Reviser.console = new MessageConsumer(consoleText);
		Reviser.console.start();
		
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
