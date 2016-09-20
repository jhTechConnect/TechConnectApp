package org.centum.techconnect.resources;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.centum.techconnect.model.JsendResponse;

import java.lang.reflect.Type;


public class JsendResponseDeserializer implements JsonDeserializer<JsendResponse>  {
	
	@Override
	public JsendResponse deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) {
		final JsonObject jsonObject = json.getAsJsonObject();
		//This object has the fields status and data guaranteed, message optional
		
		final String status = jsonObject.get("status").getAsString();
		final JsonObject data = jsonObject.get("data").getAsJsonObject();
		
		//Add these fields to a new JsendResponse object
		final JsendResponse resp = new JsendResponse();
		resp.setStatus(status);
		resp.setData(data);
		
		if (jsonObject.has("message")) {
			final String message = jsonObject.get("message").getAsString();
			resp.setMessage(message);
		}
		
		return resp;
		
		
	}

}
