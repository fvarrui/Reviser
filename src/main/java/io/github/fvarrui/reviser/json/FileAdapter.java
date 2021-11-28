package io.github.fvarrui.reviser.json;

import java.io.File;
import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class FileAdapter extends TypeAdapter<File> {

	@Override
	public void write(JsonWriter out, File value) throws IOException {
	      out.beginObject(); 
	      out.name("name"); 
	      out.value(value.getPath()); 
	      out.endObject(); 
	}

	@Override
	public File read(JsonReader in) throws IOException {
		File file = null;
		in.beginObject();
		while (in.hasNext()) {
			String fieldName = null;
			JsonToken token = in.peek();
			if (token.equals(JsonToken.NAME)) {
				fieldName = in.nextName();
				if ("path".equals(fieldName)) {
					token = in.peek();
					file = new File(in.nextString());
				}
			}
		}
		in.endObject();
		return file;
	}

}
