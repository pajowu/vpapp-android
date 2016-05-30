package de.pajowu.vp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

// AdMob
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

// Layout
import android.support.v7.widget.RecyclerView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;

//Data
import java.util.ArrayList;
import java.util.List;

// HTTP
import retrofit2.Call;

// Serialize to shared prefs
import com.google.gson.Gson;
import android.content.Context;
import android.content.SharedPreferences;

// dialogs
import android.app.AlertDialog;
import android.widget.EditText;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.content.DialogInterface;

// message handling
import android.content.Intent;
import android.net.Uri;

//workaround for swiperefreshview
import android.util.TypedValue;

// menu
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;

// hide ad
import android.view.View;

// setclass
import android.provider.Settings.Secure;

// models
import de.pajowu.vp.models.MonteVP;
import de.pajowu.vp.models.MonteVPEntry;
import de.pajowu.vp.models.FCMMessage;


public class MainActivity extends AppCompatActivity
{
	private static final String TAG = "VPAPP";
    AdView mAdView;
    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    LinearLayoutManager mSwipeLayoutManager;
    VPRecyclerAdapter mAdapter;
    List<MonteVPEntry> mItemList = new ArrayList<>();
    MonteVP mVP;
	VPGaeService mService;
	Gson mGson;
    String schoolCode;
    String classFilter = "";
    Boolean showAds;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initGlobals();
        initViews();
        loadVP();
        handleNotification(getIntent().getExtras());

        loadAd();
        toggleAds();

    }

    private void handleNotification(Bundle extras) {

        if (extras!=null)
        {
            Log.d(TAG, "Open Ac from Not");
            String msg = extras.getString("msg");
            if (msg!=null) {
                Log.d(TAG, "got msg: "+msg);
                final FCMMessage message = mGson.fromJson(msg, FCMMessage.class);
                if (message.action.equals("load_vp")){
                    mSwipeRefreshLayout.setProgressViewOffset(false, 0,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                            24, getResources().getDisplayMetrics()));
                    loadData();
                } else if (message.action.equals("message")) {
                    AlertDialog.Builder alert = message.getAlert(this);

                    alert.show();
                }
            }
        }

    }

    private void initGlobals() {

    	mAdView = (AdView) findViewById(R.id.adView);
    	mRecyclerView = (RecyclerView) findViewById(R.id.vpRecyclerView);
    	mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
    	mSwipeLayoutManager = new LinearLayoutManager(this);
		mSwipeLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		mVP = new MonteVP();
		mService = (new RequestHelper(this)).getService();
		mGson = new Gson();

        if (getString("schoolcode", "") == "") {
            setSchoolCode();
        } else {
            schoolCode = getString("schoolcode", "");
        }

        classFilter = getString("filter", "");

        showAds = !getBoolean("adsdisabled");

    }

    private void initViews() {

		mAdapter = new VPRecyclerAdapter(mItemList);

        mRecyclerView.setAdapter(mAdapter);
    	mRecyclerView.setLayoutManager(mSwipeLayoutManager);

		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
		    @Override
		    public void onRefresh() {
		        // Refresh items
		        loadData();
		    }
		});
    }

	private void loadData() {

        mSwipeRefreshLayout.setRefreshing(true);

		Thread requestThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
			        Call<MonteVP> get_vp_call = mService.getVP(schoolCode);
			        mVP = get_vp_call.execute().body();
			        saveVP();
			        runOnUiThread( new Runnable() {
			            @Override
			            public void run() {
			        		onItemsLoadComplete();
			            }
			        });

                } catch (Exception e) { Log.e(TAG, "requestThread", e); }
            }
        });
        requestThread.start();

    }

    void saveVP() {
        if (mVP != null) {
            String vp_json = mGson.toJson(mVP);
            putString("vp", vp_json);
        }
    }

    void loadVP() {

    	String vp_json = getString("vp", "");
    	Log.d(TAG, vp_json);

    	if (vp_json != "" ) {
            MonteVP tmpvp = mGson.fromJson(vp_json, MonteVP.class); 
            if (tmpvp != null) {
                mVP = tmpvp;
            }
        }
    	onItemsLoadComplete();

    }

    void putString(String key, String str) {

    	SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(key, str);
		editor.commit();

    }

    String getString(String key, String def) {

    	SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
		String str = sharedPref.getString(key, def);
		return str;

    }

    void putBoolean(String key, Boolean bool){

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, bool);
        editor.commit();

    }

    Boolean getBoolean(String key) {

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        Boolean str = sharedPref.getBoolean(key, false);
        return str;

    }

	void onItemsLoadComplete() {
        if (mVP != null) {
            mItemList.clear();
            mItemList.addAll(mVP.getItemList(MainActivity.this, classFilter));
            mAdapter.notifyDataSetChanged();
        }


	    mSwipeRefreshLayout.setRefreshing(false);

	}

    private void loadAd() {

        MobileAds.initialize(getApplicationContext(), BuildConfig.ADMOB_APP_ID);

        AdRequest adRequest = new AdRequest.Builder()
        						.addTestDevice("5E004FB21B564A5A33EAC8FB59ADC109")
        						.build();

        mAdView.loadAd(adRequest);

    }

    private void showAd() {

        mAdView.setEnabled(true);
        mAdView.setVisibility(View.VISIBLE);

    }

    private void hideAd() {
        mAdView.setEnabled(false);
        mAdView.setVisibility(View.GONE);
    }

    public void setSchoolCode(){
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle(getString(R.string.enter_school_code));
        alert.setMessage(getString(R.string.enter_school_code_text));
        final EditText input = new EditText(MainActivity.this);

        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        alert.setView(input);
        alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                schoolCode = input.getText().toString();
                putString("schoolcode", schoolCode);
                loadData();
            }
        });
        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {

          }
      });
        alert.show();
    }

    public void setClass(){
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle(getString(R.string.choose_class));
        alert.setMessage(getString(R.string.choose_class_text));
        final EditText input = new EditText(MainActivity.this);
        alert.setView(input);
        alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                classFilter = input.getText().toString();
                putString("filter",classFilter);
                onItemsLoadComplete();
                new AlertDialog.Builder(MainActivity.this)
                    .setTitle(getString(R.string.push_notifications))
                    .setMessage(getString(R.string.push_notifications_text))
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            setNotifyClass(classFilter);
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int whichButton) {}})
                    .show();
            }
        });
        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.show();
    }


    public void setNotifyClass(String classtoset){
        Thread requestThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    String deviceID = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
                    mService.setClass(deviceID, classFilter).execute();
                } catch (Exception e) {
                    Log.e(TAG, "requestThread", e);
                }
            }
        });
        // start the thread
        requestThread.start();
    }

    private void toggleAds() {
        if (!showAds) {
            hideAd();
        } else {
            showAd();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        for (int i = 0; i < menu.size(); ++i) {
            MenuItem mi = menu.getItem(i);
            if (mi.getItemId() == R.id.switchads) {
                mi.setChecked(!getBoolean("adsdisabled")); 
            }
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.switchads:
                putBoolean("adsdisabled",!getBoolean("adsdisabled"));
                showAds = !getBoolean("adsdisabled");
                toggleAds();
                item.setChecked(!getBoolean("adsdisabled"));
                return true;

            case R.id.setclass:
                setClass();
                return true;

            case R.id.setschoolcode:
                setSchoolCode();
                return true;

            case R.id.loadvp:
                loadData();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
