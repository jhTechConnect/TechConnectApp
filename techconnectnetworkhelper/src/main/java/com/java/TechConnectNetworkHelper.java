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

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class TechConnectNetworkHelper {

	//First, I need  a Retrofit object which is able to understand JSON

	public static final String BASE_URL = "http://jhtechconnect.me/";
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
		Response<JsendResponse> resp = service.catalog().execute();
		//First, check whether there is an error. HAVE TO DO THIS TO SATISFY RETROFIT!
		//Cheking first if the http request was successful. if not, have to manually deserialize the JSON
		if (!resp.isSuccessful()) {
			JsendResponse error = myGson.fromJson(resp.errorBody().string(), JsendResponse.class);
			throw new IOException(error.getMessage());
		} else {
			//Now, I'm expecting a catalog
			JsonObject obj = resp.body().getData();
			//Obj should have a JsonArray of flowcharts
			ArrayList<FlowChart> flowcharts = new ArrayList<FlowChart>();
			for (JsonElement j : obj.get("flowcharts").getAsJsonArray()) {
				flowcharts.add(myGson.fromJson(j, FlowChart.class));
			}
			return flowcharts;
		}
	}

	public FlowChart getChart(String id) throws IOException {
		Response<JsendResponse> resp = service.flowchart(id).execute();
		//First, check whether there is an error
		if (!resp.isSuccessful()) {
			JsendResponse error = myGson.fromJson(resp.errorBody().string(),JsendResponse.class);
			throw new IOException(error.getMessage());
		} else {
			//Now, I know that there is a FlowChart contained in this resp. Just get it
			JsonObject obj = resp.body().getData();
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
		Response<JsendResponse> resp = service.flowcharts(ids).execute();
		if (!resp.isSuccessful()) {
			JsendResponse error = myGson.fromJson(resp.errorBody().string(), JsendResponse.class);
			throw new IOException(error.getMessage());
		} else {
			//Now I know, I should be getting two objects. bad ID strings as well as the actual flowcharts
			JsonObject obj = resp.body().getData();
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
		Response<JsendResponse> resp = service.login(email,password).execute();
		//System.out.println(service.login(email,password).execute().code());
		//First check to see if the request succeeded
		if (!resp.isSuccessful()) {
			//Must convert ourselves
			JsendResponse test = myGson.fromJson(resp.errorBody().string(),JsendResponse.class);
			throw new IOException(test.getMessage());
		} else {
			//Now, I'm expecting a data object with fields relevant
			JsonObject obj = resp.body().getData().getAsJsonObject();
			user = myGson.fromJson(obj, Tokens.class);
		}
	}

	public void logout() throws IOException {
		Response<JsendResponse> resp = service.logout(user.getAuthToken(),user.getUserId()).execute();
		if (!resp.isSuccessful()) {
			JsendResponse test = myGson.fromJson(resp.errorBody().string(), JsendResponse.class);
			throw new IOException(test.getMessage());
		} else {
			user = null;//Resest the user object so that it doesn't bleed over into future calls
		}


	}

	public void comment(ChartComment c) throws IOException {
		Response<JsendResponse> resp = service.comment(user.getAuthToken(), user.getUserId(), c).execute();
		if (!resp.isSuccessful()) {
			JsendResponse error = myGson.fromJson(resp.errorBody().string(),JsendResponse.class);
			throw new IOException(error.getMessage());
		}
	}

	private static Gson buildGson() {
		GsonBuilder gsonBuilder = new GsonBuilder();

		gsonBuilder.registerTypeAdapter(FlowChart.class, new FlowChartDeserializer());
		gsonBuilder.registerTypeAdapter(FlowChart.class, new FlowChartSerializer());
		gsonBuilder.registerTypeAdapter(JsendResponse.class, new JsendResponseDeserializer());
		gsonBuilder.registerTypeAdapter(Vertex.class, new VertexDeserializer());
		Gson myGson = gsonBuilder.create();

		return myGson;
	}



}
