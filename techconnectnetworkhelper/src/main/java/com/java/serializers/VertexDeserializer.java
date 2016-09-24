package com.java.serializers;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.java.model.ChartComment;
import com.java.model.Vertex;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class VertexDeserializer implements JsonDeserializer<Vertex>  {
	
	
	@Override
	public Vertex deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) {
		JsonObject jsonObject = json.getAsJsonObject();
		Gson myGson = new Gson();
		
		final String id = jsonObject.get("_id").getAsString();
		final String name = jsonObject.get("name").getAsString();
		final String details = jsonObject.get("details").getAsString();
		final ArrayList<String> resources = new ArrayList<String>();
		for (JsonElement j : jsonObject.get("resources").getAsJsonArray())  {
			resources.add(j.getAsString());
		}
		final ArrayList<String> images = new ArrayList<String>();
		for (JsonElement j : jsonObject.get("images").getAsJsonArray()) {
			images.add(j.getAsString());
		}
		final ArrayList<ChartComment> comments = new ArrayList<ChartComment>();
		for (JsonElement j : jsonObject.get("comments").getAsJsonArray()) {
			comments.add(myGson.fromJson(j, ChartComment.class));
		}
		
		final Vertex vertex = new Vertex();
		vertex.setId(id);
		vertex.setName(name);
		vertex.setDetails(details);
		vertex.setResources(resources);
		vertex.setImages(images);
		vertex.setComments(comments);
		return vertex;
		
	}

}
