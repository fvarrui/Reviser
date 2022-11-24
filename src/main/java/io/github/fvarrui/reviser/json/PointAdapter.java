package io.github.fvarrui.reviser.json;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import javafx.geometry.Point2D;

public class PointAdapter extends TypeAdapter<Point2D> {

	@Override
	public void write(JsonWriter out, Point2D value) throws IOException {
		out.beginObject();
		out.name("x");
		out.value(value.getX());
		out.name("y");
		out.value(value.getY());
		out.endObject();
	}

	@Override
	public Point2D read(JsonReader in) throws IOException {
		Double x = 0.0;
		Double y = 0.0;
		in.beginObject();
		while (in.hasNext()) {
			String fieldName = null;
			JsonToken token = in.peek();
			if (token.equals(JsonToken.NAME)) {
				fieldName = in.nextName();
				token = in.peek();
				switch (fieldName) {
				case "x": x = in.nextDouble(); break;
				case "y": y = in.nextDouble(); break;
				default: in.nextString();
				}
			}
		}
		in.endObject();
		return new Point2D(x, y);
	}

}
