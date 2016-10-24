package org.techconnect.network.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.techconnect.model.JsendResponse;

import java.lang.reflect.Type;

public class JsendResponseDeserializer implements JsonDeserializer<JsendResponse>  {
	
	@Override
	public JsendResponse deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) {
		final JsonObject jsonObject = json.getAsJsonObject();
		//This object has the fields status and data guaranteed, message optional
		
		final String status = jsonObject.get("status").getAsString();

		
		//Add these fields to a new JsendResponse object
		final JsendResponse resp = new JsendResponse();

		resp.setStatus(status);
		if (jsonObject.has("data")) {
			final JsonObject data = jsonObject.get("data").getAsJsonObject();
			resp.setData(data);
		}


		if (jsonObject.has("message")) {
			final String message = jsonObject.get("message").getAsString();
			resp.setMessage(message);
		}
		
		return resp;
		
		
	}

}
