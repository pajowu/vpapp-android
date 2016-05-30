package de.pajowu.vp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Body;
import retrofit2.http.Query;

import de.pajowu.vp.models.MonteVP;
import de.pajowu.vp.models.RegisterRequest;


public interface VPGaeService {
	@GET("vp/{schoolcode}")
	Call<MonteVP> getVP(@Path("schoolcode") String schoolcode);

	@GET("setclass")
	Call<MonteVP> setClass(@Query("dev_id") String dev_id, @Query("class") String filter);

	@POST("device/register/")
	Call<Void> registerToken(@Body RegisterRequest body);
}