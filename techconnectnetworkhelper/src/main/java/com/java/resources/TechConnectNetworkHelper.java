package com.java.resources;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.java.model.ChartComment;
import com.java.model.FlowChart;
import com.java.model.JsendResponse;
import com.java.model.Tokens;
import com.java.model.Vertex;
import com.java.serializers.FlowChartSerializer;
import com.java.serializers.JsendResponseDeserializer;
import com.java.serializers.VertexDeserializer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class TechConnectNetworkHelper {

	//First, I need  a Retrofit object which is able to understand JSON

	public static final String BASE_URL = "http://jhtechconnect.me/";
	//Copied from old NetworkHelper for the Image and pdf resources
	public static final String URL = "http://tech-connect-database.s3-website-us-west-2.amazonaws.com/";
	public static final String JSON_FOLDER = "json/";
	public static final String RESOURCE_FOLDER = "resources/";
	private static final String INDEX_FILE = "index.json";
	private Tokens user = new Tokens();
	private Gson myGson = buildGson();
	private Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
			.addConverterFactory(GsonConverterFactory.create(myGson))
			.build();
	private TechConnectService service = retrofit.create(TechConnectService.class);

	//Store references to the possible device, problem, and misc flowcharts
	private ArrayList<String> device_ids = new ArrayList<String>();
	private ArrayList<String> problem_ids = new ArrayList<String>();
	private ArrayList<String> misc_ids = new ArrayList<String>();

	private Context context;//I think I may need this in order to have a app-specific location to store
	//info


	//Default constructor
	public TechConnectNetworkHelper(Context context) {
		this.context = context;
	}

	/**
	 * This method uses the device_ids object to download all device-related flowcharts
	 *
	 * @return
	 */
	public List<FlowChart> getDevices() {
		ArrayList<FlowChart> devices = new ArrayList<FlowChart>();
		String[] ids = new String[device_ids.size()];
		ids = device_ids.toArray(ids);
		try {
			devices = (ArrayList<FlowChart>) getCharts(ids);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//If we successfully get the devices, I need to iterate through the FlowCharts
		//And download all of the associated String Resources
		//Need to test!!!
		for (FlowChart f : devices) {
			for (String resourcePath : f.getAllRes()) {
				String url = URL + RESOURCE_FOLDER + resourcePath;
				if (ResourceHandler.get().hasStringResource(resourcePath)) {
					Log.d(TechConnectNetworkHelper.class.getName(), "ResourceHandler has \"" + resourcePath + "\"");
				} else {
					String file = null;
					try {
						file = downloadFile(RESOURCE_FOLDER + resourcePath);
					} catch (IOException e) {
						//Image can't be loaded, eh ignore it for now.
						//TODO somehow inform user of failed image loading
						e.printStackTrace();
						Log.e(TechConnectNetworkHelper.class.getName(), "Failed to load: " + url);
						file = null;
					}
					ResourceHandler.get().addStringResource(resourcePath, file);
				}
			}
		}
		return devices;
	}

	/**
	 * Downloads an image.
	 *
	 * @param fileUrl
	 * @return
	 * @throws IOException
	 */
	private String downloadFile(String fileUrl) throws IOException {
		Log.d(TechConnectNetworkHelper.class.getName(), "Attempting to download " + fileUrl);
		String fileName = "i" + (int) Math.round(Integer.MAX_VALUE * Math.random());
		HttpURLConnection connection = (HttpURLConnection) new URL(fileUrl.replace(" ", "%20")).openConnection();

		FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
		InputStream inputStream = connection.getInputStream();

		int readBytes;
		byte buffer[] = new byte[1024];
		if (inputStream != null) {
			while ((readBytes = inputStream.read(buffer)) > -1) {
				fileOutputStream.write(buffer, 0, readBytes);
			}
			inputStream.close();
		}

		connection.disconnect();
		fileOutputStream.flush();
		fileOutputStream.close();

		Logger.getLogger(getClass().getName()).log(Level.INFO, "Downloaded file: " + fileUrl + " --> " + fileName);
		return fileName;
	}

	/**
	 * This method uses the problem_ids object to download all problem-related charts
	 * @return
	 */
	public List<FlowChart> getProblems() {
		ArrayList<FlowChart> probs = new ArrayList<FlowChart>();
		String[] ids = new String[problem_ids.size()];
		ids = problem_ids.toArray(ids);
		try {
			probs = (ArrayList<FlowChart>) getCharts(ids);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return probs;
	}

	/**
	 * This method uses the Misc_ids object to download all misc-related charts
	 * @return
	 */
	public List<FlowChart> getMisc() {
		ArrayList<FlowChart> misc = new ArrayList<FlowChart>();
		String[] ids = new String[misc_ids.size()];
		ids = misc_ids.toArray(ids);
		try {
			misc = (ArrayList<FlowChart>) getCharts(ids);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return misc;
	}

	public List<FlowChart> getCatalog(boolean update_lists) throws IOException {
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
				FlowChart f = myGson.fromJson(j, FlowChart.class);
				//Read through each flowchart and identify the the devices, problems, and misc
				//Only if requested
				if (update_lists) {
					this.device_ids.clear();
					this.problem_ids.clear();
					this.misc_ids.clear();
					switch (f.getType()) {
						case DEVICE:
							this.device_ids.add(f.getId());
							break;
						case PROBLEM:
							this.problem_ids.add(f.getId());
							break;
						default:
							//For now, until we have a reason otherwise!!!!
							//!!!!!
							this.device_ids.add(f.getId());
					}
					flowcharts.add(f);
				}
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

		gsonBuilder.registerTypeAdapter(FlowChart.class, new com.java.serializers.FlowChartDeserializer());
		gsonBuilder.registerTypeAdapter(FlowChart.class, new FlowChartSerializer());
		gsonBuilder.registerTypeAdapter(JsendResponse.class, new JsendResponseDeserializer());
		gsonBuilder.registerTypeAdapter(Vertex.class, new VertexDeserializer());
		Gson myGson = gsonBuilder.create();

		return myGson;
	}



}
