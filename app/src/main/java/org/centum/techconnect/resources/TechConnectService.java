package org.centum.techconnect.resources;

import org.centum.techconnect.model.Catalog;
import org.centum.techconnect.model.FlowChart_tempo;
import org.centum.techconnect.model.LoginAttempt;
import org.centum.techconnect.model.LoginResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TechConnectService {
	
	//Define all of the headers which are needed for the communication here
	//Initially defined as null
	@Headers({
		"X-Auth-Token: ",
		"X-User-Id: "
	})
	
	//Just want to get the catalog, no input needed
	@GET("api/v1/catalog") //CHANGE FOR THE REAL THING! This was for testing
	Call<Catalog> catalog();
	
	@GET("api/v1/chart/{id}")
	Call<FlowChart_tempo> flowchart(@Path("id") String id);
	
	@GET("api/v1/charts/{ids}")
	Call<List<FlowChart_tempo>> flowcharts(@Path("ids") String ids);
	//This String ids is a comma separated list of the ids desired
	
	//Login the user
	@POST("api/v1/login")
	Call<LoginResponse> login(@Body LoginAttempt la);
	
	//Logout the user. I don't think that I need to pass in anything? Maybe the user?
	@POST("api/v1/logout")
	Call<LoginResponse> logout(@Header("X-Auth-Token") String auth_token, @Header("X-User-Id") String userId);

}
