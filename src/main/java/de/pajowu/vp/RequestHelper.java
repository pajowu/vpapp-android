package de.pajowu.vp;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import okhttp3.OkHttpClient;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;

import android.content.Context;
public class RequestHelper {
	Retrofit mRetrofit;
	VPGaeService mService;
	Context mContext;
	RequestHelper(Context cont) {
		mContext = cont;
		OkHttpClient.Builder httpClient = new OkHttpClient.Builder();  
		httpClient.addInterceptor(new Interceptor() {  
		    @Override
		    public Response intercept(Interceptor.Chain chain) throws IOException {
		        Request original = chain.request();

		        Request request = original.newBuilder()
		            .header("User-Agent", getUserAgentString())
		            .method(original.method(), original.body())
		            .build();

		        return chain.proceed(request);
		    }
		});
		OkHttpClient client = httpClient.build();  

		mRetrofit = new Retrofit.Builder()
		    .baseUrl(BuildConfig.BASE_URL)
		    .addConverterFactory(GsonConverterFactory.create())
		    .client(client)
		    .build();
	}
	
	public VPGaeService getService() {

		return mRetrofit.create(VPGaeService.class);
		
	}

	private String getUserAgentString() {
        String appName = "";
        String appVersion = "";
        int versionCode = 0;
        try {
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            appName = packageInfo.packageName;
            appVersion = packageInfo.versionName;
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException ignore) {}

        return String.format("%1$s/%2$s (%3$s)", appName, appVersion, versionCode);
    }
}