package io.github.fvarrui.reviser.model.submissions;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import io.github.fvarrui.reviser.model.Submission;

public class SubmissionDeserializer implements JsonDeserializer<Submission> {

	@Override
	public Submission deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        Submission submission = new Gson().fromJson(json.getAsJsonObject(), Submission.class);

        // TODO añadir el submission factory aquí (determinar el tipo de entrega de que se trata y devolver el objeto adecuado) 
		
		return submission;
	}

}
