package io.github.fvarrui.reviser.config;

import java.io.File;
import java.io.IOException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import io.github.fvarrui.reviser.json.JSONUtils;
import io.github.fvarrui.reviser.ui.utils.Dialogs;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

public class Config {

	private static final String DEFAULT_MAVEN_HOME = System.getenv("MAVEN_HOME");

	public static final File configDir = new File(System.getProperty("user.home"), ".Reviser");
	public static final File exercisesDir = new File(configDir, "exercises");
	public static final File configFile = new File(configDir, "config.json");

	private ObjectProperty<Dimension2D> stageSize = new SimpleObjectProperty<>();
	private ObjectProperty<Point2D> stageCoords = new SimpleObjectProperty<>();
	private BooleanProperty maximized = new SimpleBooleanProperty();
	private StringProperty lastDirectory = new SimpleStringProperty(".");
	private StringProperty mavenHome = new SimpleStringProperty(DEFAULT_MAVEN_HOME);
	private StringProperty gitHubToken = new SimpleStringProperty();

	private static Config config;

	public static Config getConfig() {
		return config;
	}

	public static void load() {
		if (!configDir.exists()) {
			configDir.mkdir();
		}
		if (!exercisesDir.exists()) {
			exercisesDir.mkdir();
		}
		if (configFile.exists()) {
			try {
				config = JSONUtils.readJsonFromFile(configFile, Config.class);
				if (config.getMavenHome() == null) {
					config.setMavenHome(System.getenv("MAVEN_HOME"));
				}
			} catch (JsonSyntaxException | JsonIOException | IOException e) {
				Dialogs.error("No se pudo cargar la configuración desde el fichero '" + configFile + "'.", e);
			}
		} else {
			config = new Config();
		}
	}

	public static void save() {
		try {
			JSONUtils.writeJsonToFile(config, configFile);
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

	public final StringProperty lastDirectoryProperty() {
		return this.lastDirectory;
	}

	public final String getLastDirectory() {
		return this.lastDirectoryProperty().get();
	}

	public final void setLastDirectory(final String lastDirectory) {
		this.lastDirectoryProperty().set(lastDirectory);
	}

	public final StringProperty mavenHomeProperty() {
		return this.mavenHome;
	}

	public final String getMavenHome() {
		return this.mavenHomeProperty().get();
	}

	public final void setMavenHome(final String mavenHome) {
		this.mavenHomeProperty().set(mavenHome);
	}

	public final StringProperty gitHubTokenProperty() {
		return this.gitHubToken;
	}

	public final String getGitHubToken() {
		return this.gitHubTokenProperty().get();
	}

	public final void setGitHubToken(final String gitHubToken) {
		this.gitHubTokenProperty().set(gitHubToken);
	}

}
