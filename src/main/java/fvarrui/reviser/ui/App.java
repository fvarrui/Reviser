package fvarrui.reviser.ui;

import fvarrui.reviser.config.Config;
import javafx.application.Application;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {
	
	public static final String TITLE = "Reviser";

	public static MessageConsumer console;
	public static Stage primaryStage;
	
	public static MainController mainController;
	
	@Override
	public void init() throws Exception {
		Config.load();
		super.init();
	}
	
	@Override
	public void stop() throws Exception {
		saveStage();
		Config.save();
		super.stop();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		App.primaryStage = primaryStage;

		restoreStage();
		
		mainController = new MainController();
		
		Scene scene = new Scene(mainController.getView(), 900, 700);

		primaryStage.setTitle(TITLE);
		primaryStage.getIcons().add(new Image("/images/logo-16x16.png"));
		primaryStage.getIcons().add(new Image("/images/logo-24x24.png"));
		primaryStage.getIcons().add(new Image("/images/logo-32x32.png"));
		primaryStage.getIcons().add(new Image("/images/logo-64x64.png"));
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}
	
	private void saveStage() {
		Config.getConfig().setStageCoords(new Point2D(primaryStage.getX(), primaryStage.getY()));
		Config.getConfig().setStageSize(new Dimension2D(primaryStage.getWidth(), primaryStage.getHeight()));
		Config.getConfig().setMaximized(primaryStage.isMaximized());
	}
	
	private void restoreStage() {
		if (Config.getConfig().getStageCoords() != null) {
			primaryStage.setX(Config.getConfig().getStageCoords().getX());
			primaryStage.setY(Config.getConfig().getStageCoords().getY());
		}
		if (Config.getConfig().getStageSize() != null) {
			primaryStage.setWidth(Config.getConfig().getStageSize().getWidth());
			primaryStage.setHeight(Config.getConfig().getStageSize().getHeight());
		}
		primaryStage.setMaximized(Config.getConfig().isMaximized());
	}

	public static void main(String[] args) {
		launch(args);
	}

}
