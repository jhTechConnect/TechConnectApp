package com.java;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.java.model.ChartComment;
import com.java.model.FlowChart;
import com.java.model.JsendResponse;
import com.java.model.Tokens;
import com.java.model.Vertex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;




public class TechConnectNetworkHelper {
	
	//First, I need  a Retrofit object which is able to understand JSON
	
	//public static final String BASE_URL = "http://127.0.0.1:8000"; //This is the base url of the directory we will talk to
	public static final String BASE_URL = "http://192.168.1.111:3000/";
	private Tokens user = new Tokens();
	private Gson myGson = buildGson();
	private Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
			.addConverterFactory(GsonConverterFactory.create(myGson))
			.build();
	private TechConnectService service = retrofit.create(TechConnectService.class);
	
	
	
	//Default constructor
	public TechConnectNetworkHelper() {
		
	}
	
	public List<FlowChart> getCatalog() throws IOException {
		//Call and get a response 
		JsendResponse resp = service.catalog().execute().body();
		//First, check whether there is an error
		if (resp.getStatus().equalsIgnoreCase("error")) {
			throw new IOException(resp.getMessage());
		} else {
			//Now, I'm expecting a catalog
			JsonObject obj = resp.getData();
			//Obj should have a JsonArray of flowcharts
			ArrayList<FlowChart> flowcharts = new ArrayList<FlowChart>();
			for (JsonElement j : obj.get("flowcharts").getAsJsonArray()) {
				flowcharts.add(myGson.fromJson(j, FlowChart.class));
			}
			return flowcharts;
		}
	}
	
	public FlowChart getChart(String id) throws IOException {
		JsendResponse resp = service.flowchart(id).execute().body();
		//First, check whether there is an error
		if (resp.getStatus().equalsIgnoreCase("error")) {
			throw new IOException(resp.getMessage());
		} else {
			//Now, I know that there is a FlowChart contained in this resp. Just get it
			JsonObject obj = resp.getData();
			return myGson.fromJson(obj.get("flowchart"), FlowChart.class);
		}
	}
	
	//We having some issues here
	/**
	 * This function is used to get a list of specific charts. This list must be sent to
	 * Retrofit as a comma separated list of ids as a String. This converts from a list of integers
	 * for now. May not want that format in the future, but it's a place to start.
	 * @param ids
	 * @return
	 * @throws IOException
	 */
	
	
	public List<FlowChart> getCharts(String[] ids) throws IOException {
		JsendResponse resp = service.flowcharts(ids).execute().body();
		if (resp == null) {
			System.out.println("Null");
		}
		if (resp.getStatus().equalsIgnoreCase("error")) {
			throw new IOException(resp.getMessage());
		} else {
			//Now I know, I should be getting two objects. bad ID strings as well as the actual flowcharts
			JsonObject obj = resp.getData();
			//For now, let's just look at the good charts
			ArrayList<FlowChart> flowcharts = new ArrayList<FlowChart>();
			for (JsonElement j : obj.get("flowcharts").getAsJsonArray()) {
				flowcharts.add(myGson.fromJson(j,FlowChart.class));
			}
			return flowcharts;
		}
	}
	//
	//I have no clue how this should be done at all just threw something together.
	public void login(String email, String password) throws IOException {
		JsendResponse resp = service.login(email,password).execute().body();
		//First check to see if the request succeeded
		if (resp == null) {
			System.out.println("Null response");
		}
		if (resp.getStatus().equalsIgnoreCase("error")) {
			throw new IOException(resp.getMessage());
		} else {
			//Now, I'm expecting a data object with fields relevant 
			JsonObject obj = resp.getData().getAsJsonObject();
			user = myGson.fromJson(obj, Tokens.class);
		}
	}
	
	public void logout() throws IOException {
		JsendResponse resp = service.logout(user.getAuthToken(),user.getUserId()).execute().body();
		if (resp.getStatus().equalsIgnoreCase("error")) {
			throw new IOException(resp.getMessage());
		} else {
			user = null;//Resest the user object so that it doesn't bleed over into future calls
		}

		
	}
	
	public void comment(ChartComment c) throws IOException {
		JsendResponse resp = service.comment(user.getAuthToken(),user.getUserId(), c).execute().body();
		if (resp.getStatus().equalsIgnoreCase("error")) {
			throw new IOException(resp.getMessage());
		}
	}
	
	private static Gson buildGson() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		
		gsonBuilder.registerTypeAdapter(FlowChart.class, new FlowChartDeserializer());
		gsonBuilder.registerTypeAdapter(JsendResponse.class, new JsendResponseDeserializer());
		gsonBuilder.registerTypeAdapter(Vertex.class, new VertexDeserializer());
		Gson myGson = gsonBuilder.create();
		
		return myGson;
	}



}
