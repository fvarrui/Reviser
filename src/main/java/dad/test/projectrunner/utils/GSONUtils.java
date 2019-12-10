package dad.test.projectrunner.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class GSONUtils {
	
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public static <T> T loadFromJson(File json, Class<T> type) throws JsonSyntaxException, JsonIOException, IOException {
		return gson.fromJson(new FileReader(json, Charset.forName("UTF-8")), type);
	}
	
	public static void jsonToFile(Object object, File json) throws JsonSyntaxException, JsonIOException, IOException {
		String jsonString = gson.toJson(object, object.getClass());
		FileUtils.writeStringToFile(json, jsonString, Charset.forName("UTF-8"));
	}
	
}
