package fvarrui.reviser.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.hildan.fxgson.FxGson;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class JSONUtils {
	
	private static final Gson gson = FxGson.fullBuilder().setPrettyPrinting().create();

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
	public static <T> T loadFromJson(File jsonFile, Class<T> type) throws JsonSyntaxException, JsonIOException, IOException {
		return gson.fromJson(new FileReader(jsonFile, Charset.forName("UTF-8")), type);
	}
	
	/**
	 * Marshall a class to a json file
	 * @param object Object to be serialized
	 * @param jsonFile Destination file
	 * @throws JsonSyntaxException
	 * @throws JsonIOException
	 * @throws IOException
	 */
	public static void jsonToFile(Object object, File jsonFile) throws JsonSyntaxException, JsonIOException, IOException {
		String jsonString = gson.toJson(object, object.getClass());
		FileUtils.writeStringToFile(jsonFile, jsonString, Charset.forName("UTF-8"));
	}
	
}
