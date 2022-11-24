package io.github.fvarrui.reviser.json;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import javafx.geometry.Dimension2D;

public class DimensionAdapter extends TypeAdapter<Dimension2D> {

	@Override
	public void write(JsonWriter out, Dimension2D value) throws IOException {
		out.beginObject();
		out.name("width");
		out.value(value.getWidth());
		out.name("height");
		out.value(value.getHeight());
		out.endObject();
	}

	@Override
	public Dimension2D read(JsonReader in) throws IOException {
		Double width = 0.0;
		Double height = 0.0;
		in.beginObject();
		while (in.hasNext()) {
			String fieldName = null;
			JsonToken token = in.peek();
			if (token.equals(JsonToken.NAME)) {
				fieldName = in.nextName();
				token = in.peek();
				switch (fieldName) {
				case "width": width = in.nextDouble(); break;
				case "height": height = in.nextDouble(); break;
				default: in.nextString();
				}
			}
		}
		in.endObject();
		return new Dimension2D(width, height);
	}

}
