package de.pajowu.vp;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import android.provider.Settings.Secure;
import retrofit2.Retrofit;
import retrofit2.Call;
import retrofit2.converter.gson.GsonConverterFactory;

import de.pajowu.vp.models.RegisterRequest;
public class VPFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        registerToServer(refreshedToken);
    }

    private void registerToServer(String token) {
        String deviceID = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

        VPGaeService service = (new RequestHelper(this)).getService();
        try {
            service.registerToken(new RegisterRequest(deviceID, token)).execute();
        } catch (Exception e) {Log.d(TAG, "e", e);}
    }
}