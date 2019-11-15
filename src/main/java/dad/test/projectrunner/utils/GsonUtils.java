package dad.test.projectrunner.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class GsonUtils {
	
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public static <T> T loadFromJson(File json, Class<T> type) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		return gson.fromJson(new FileReader(json), type);
	}
	
}
