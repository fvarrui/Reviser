package fvarrui.reviser.config;

import java.io.File;
import java.io.IOException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import fvarrui.reviser.json.JSONUtils;
import fvarrui.reviser.ui.Dialogs;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

public class Config {

	public static final File configDir = new File(System.getProperty("user.home"), ".Reviser");
	public static final File submissionsDir = new File(configDir, "submissions");
	public static final File configFile = new File(configDir, "config.json");

	private ObjectProperty<Dimension2D> stageSize = new SimpleObjectProperty<>();
	private ObjectProperty<Point2D> stageCoords = new SimpleObjectProperty<>();
	private BooleanProperty maximized = new SimpleBooleanProperty();

	private static Config config;

	public static Config getConfig() {
		return config;
	}

	public static void load() {
		if (!configDir.exists()) {
			configDir.mkdir();
		}
		if (!submissionsDir.exists()) {
			submissionsDir.mkdir();			
		}
		if (configFile.exists()) {
			try {
				config = JSONUtils.loadFromJson(configFile, Config.class);
			} catch (JsonSyntaxException | JsonIOException | IOException e) {
				Dialogs.error("No se pudo cargar la configuración desde el fichero '" + configFile + "'.", e);
			}
		} else {
			config = new Config();
		}
	}

	public static void save() {
		try {
			JSONUtils.jsonToFile(config, configFile);
		} catch (JsonSyntaxException | JsonIOException | IOException e) {
			Dialogs.error("No se pudo guardar la configuración en el fichero '" + configFile + "'.", e);
		}
	}

	public final ObjectProperty<Dimension2D> stageSizeProperty() {
		return this.stageSize;
	}

	public final Dimension2D getStageSize() {
		return this.stageSizeProperty().get();
	}

	public final void setStageSize(final Dimension2D stageSize) {
		this.stageSizeProperty().set(stageSize);
	}

	public final ObjectProperty<Point2D> stageCoordsProperty() {
		return this.stageCoords;
	}

	public final Point2D getStageCoords() {
		return this.stageCoordsProperty().get();
	}

	public final void setStageCoords(final Point2D stageCoords) {
		this.stageCoordsProperty().set(stageCoords);
	}

	public final BooleanProperty maximizedProperty() {
		return this.maximized;
	}

	public final boolean isMaximized() {
		return this.maximizedProperty().get();
	}

	public final void setMaximized(final boolean maximized) {
		this.maximizedProperty().set(maximized);
	}

}
