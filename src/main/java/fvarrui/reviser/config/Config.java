package fvarrui.reviser.config;

import java.io.File;
import java.io.IOException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import fvarrui.reviser.json.JSONUtils;
import fvarrui.reviser.ui.Dialogs;

public class Config {
	
	public static final File configDir = new File(System.getProperty("user.home"), ".Reviser"); 
	public static final File configFile = new File(configDir, "config.json");
	
	private static Config config;

	public static void load() {
		
		if (!configDir.exists()) {
			configDir.mkdir();
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

}
