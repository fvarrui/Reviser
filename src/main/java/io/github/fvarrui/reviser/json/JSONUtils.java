package io.github.fvarrui.reviser.json;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.hildan.fxgson.FxGson;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class JSONUtils {
	
	private static final Gson gson = 
			FxGson
				.fullBuilder()
				.addSerializationExclusionStrategy(new SerializationExclusionEstratregy())
				.setPrettyPrinting()
				.create();

	/**
	 * Unmarshall a class from a json file
	 * @param <T> Type
	 * @param jsonFile Source file
	 * @param type Class type to be loaded
	 * @return
	 * @throws JsonSyntaxException
	 * @throws JsonIOException
	 * @throws IOException
	 */
	public static <T> T readJsonFromFile(File jsonFile, Class<T> type) throws JsonSyntaxException, JsonIOException, IOException {
		String jsonString = FileUtils.readFileToString(jsonFile, StandardCharsets.UTF_8);
		return gson.fromJson(jsonString, type);
	}
	
	/**
	 * Marshall a class to a json file
	 * @param object Object to be serialized
	 * @param jsonFile Destination file
	 * @throws JsonSyntaxException
	 * @throws JsonIOException
	 * @throws IOException
	 */
	public static void writeJsonToFile(Object object, File jsonFile) throws JsonSyntaxException, JsonIOException, IOException {
		String jsonString = gson.toJson(object, object.getClass());
		FileUtils.writeStringToFile(jsonFile, jsonString, StandardCharsets.UTF_8);
	}
	
}
