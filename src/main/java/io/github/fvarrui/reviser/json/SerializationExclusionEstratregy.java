package io.github.fvarrui.reviser.json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.annotations.Expose;

public class SerializationExclusionEstratregy implements ExclusionStrategy {

	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		return false;
	}

	@Override
	public boolean shouldSkipField(FieldAttributes f) {
		Expose expose = (Expose) f.getAnnotation(Expose.class);
		return expose != null && !expose.serialize();
	}

}
