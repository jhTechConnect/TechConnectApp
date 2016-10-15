package org.centum.techconnect.resources;

import com.java.model.ChartComment;
import com.java.model.JsendResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TechConnectRetrofit {
	
	//Define all of the headers which are needed for the communication here
	//Initially defined as null
	@Headers({
		"X-Auth-Token: ",
		"X-User-Id: "
	})
	
	//Just want to get the catalog, no input needed
	@GET("api/v1/catalog")
	Call<JsendResponse> catalog();
	
	@GET("api/v1/chart/{id}")
	Call<JsendResponse> flowchart(@Path("id") String id); 
	
	@FormUrlEncoded
	@POST("api/v1/charts")
	Call<JsendResponse> flowcharts(@Field("ids[]") String[] ids);
	//This String ids is a comma separated list of the ids desired

	//Delete a specific chart from the Server
	@DELETE("api/v1/chart/{id}")
	Call<JsendResponse> deleteChart(@Header("X-Auth-Token") String auth_token, @Header("X-User-Id") String userId, @Path("id") String id);

	//Login the user
	@FormUrlEncoded
	@POST("api/v1/login")
	Call<JsendResponse> login(@Field("email") String email, @Field("password") String pass);
	
	//Logout the user. I don't think that I need to pass in anything? Maybe the user?
	@POST("api/v1/logout")
	Call<JsendResponse> logout(@Header("X-Auth-Token") String auth_token, @Header("X-User-Id") String userId);
	
	//Attempts to post a comment onto a chart or node with id id.
	@POST("api/v1/chart/{id}/comment")
	Call<JsendResponse> comment(@Header("X-Auth-Token") String auth_token, @Header("X-User-Id") String userId, @Path("id") String chartId, @Body ChartComment comment);

	//Attempt to delete a comment from a chart
	//Learn how to use the OkHttp structure to just send a JSON object: http://stackoverflow.com/questions/34179922/okhttp-post-body-as-json
	//Json object just has the field "commentId"
	@DELETE("api/v1/chart/{id}/comment")
	Call<JsendResponse> deleteComment(@Header("X-Auth-Token") String auth_token, @Header("X-User-Id") String userId, @Path("id") String chartId, @Body RequestBody body);

	//Post feedback on a chart, which is upvoting and downvoting
	//Use same approach as above: Json object has field "feedback"
	@POST("api/v1/chart/{id}/feedback")
	Call<JsendResponse> feedback(@Header("X-Auth-Token") String auth_token, @Header("X-User-Id") String userId,@Path("id") String chart_id, @Body RequestBody body);

}
