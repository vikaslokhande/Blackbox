package com.whiznic.blackbox1;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.whiznic.blackbox1.helper.Api_Helper;
import com.whiznic.blackbox1.helper.Application_Helper;
import com.whiznic.blackbox1.helper.Exception_Handler;
import com.whiznic.blackbox1.helper.MyPreference;
import com.whiznic.blackbox1.helper.NavDrawerItem;
import com.whiznic.blackbox1.services.My_Service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity
{
	private Dialog progressDialog;
	private static final String url_path_logout = "GPS_Tracker/index.php/api/logout";
	private static final String url_IpAddress_logout = "107.155.108.112";
	private static final String url_PORT = "6000";
    private Intent intent;
    String type = "ANDROID";
    private DrawerLayout mDrawerLayout;
    TextView t1;
    private ListView mDrawerList;
	/** called when the activity is first created. */

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Thread.setDefaultUncaughtExceptionHandler(new Exception_Handler(this));
        MyPreference myPreference = new MyPreference(MainActivity.this);
        if(myPreference.getUserId().trim().length() == 0) {
            finish();
            startActivity(new Intent(MainActivity.this, Login_Activity.class));
        }
        intent = new Intent(MainActivity.this, My_Service.class);
        startService(intent);

       NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(108);
        myPreference.setNotification(new ArrayList<String>());

        setContentView(R.layout.activity_main);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        ArrayList navDrawerItems = new ArrayList<NavDrawerItem>();
		findViewById(R.id.btn_logout).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new logout().execute();
			}
		});

		WebView wv = (WebView) findViewById(R.id.my_webview);
		WebSettings webSettings = wv.getSettings();
		//
		wv.setVisibility(View.INVISIBLE);
		webSettings.setDatabaseEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(false);
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportMultipleWindows(false);
		wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("URLS", url);
                if (url.contains("http://107.155.108.112/GPS_Tracker/index.php/sessions/login1")) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d("url", url);
                WebView wv = (WebView) findViewById(R.id.my_webview);
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);
                progressBar.setVisibility(View.GONE);
                wv.setVisibility(View.VISIBLE);
            }
        });

		//getting imei number
		TelephonyManager telephoneManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telephoneManager.getDeviceId();
		final String imei_device_id = telephoneManager.getDeviceId();

		myPreference = new MyPreference(MainActivity.this);
		String user_id = myPreference.getUserId();
		String key = URLEncoder.encode(myPreference.getAPI_Key());

		wv.loadUrl("http://107.155.108.112/GPS_Tracker/mo?userid=" + user_id + "&token=" + key + "&imei=" + imei_device_id);
      if(getIntent().hasExtra("url")){
            wv.loadUrl("http://107.155.108.112/GPS_Tracker/mo/" + getIntent().getStringExtra("url"));
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if(checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.CAMERA, android.Manifest.permission.SEND_SMS}, 101);
            }
	}
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Application_Helper application_helper = new Application_Helper(MainActivity.this);
                    Dialog dialog = application_helper.showMessageDialog("Please grant all required permission");
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    });
                }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(getIntent().hasExtra("url")){
            WebView wv = (WebView) findViewById(R.id.my_webview);
            wv.loadUrl("http://107.155.108.112/GPS_Tracker/mo/" + getIntent().getStringExtra("url"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(apiCall);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(handler != null) {
            handler.removeCallbacks(apiCall);
        }
    }

    private Handler handler = new Handler();
    private Runnable apiCall = new Runnable() {
        @Override
        public void run() {
            TelephonyManager telephoneManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            telephoneManager.getDeviceId();
            final String imei_device_id = telephoneManager.getDeviceId();

            MyPreference preference = new MyPreference(MainActivity.this);

            new FetchServerInfo(MainActivity.this, "http://107.155.108.112/GPS_Tracker/index.php/api/updateDevLatLong?imei=" + imei_device_id
                    + "&latitude=" + preference.getLatitude() + "&longitude=" + preference.getLongitude()).execute();
            handler.postDelayed(this, 30 * 1000);
        }
    };



    class FetchServerInfo extends AsyncTask<Void, Void, Void> {

        private String url;
        private boolean isFail, isNetworkAvailable;
        private Context context;
        private String response;

        public FetchServerInfo(Context context, String url) {
            // TODO Auto-generated constructor stub
            this.url = url;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            isFail = false;
            isNetworkAvailable = false;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            isNetworkAvailable = isNetworkAvailable();
            if (isNetworkAvailable) {
                Log.d("black box", url);
                response = getResponseFromUrl(url);
                Log.d("black box", response);
                isFail = false;
            }
            return null;
        }

        public String getResponseFromUrl(String url) {
            String json = null;
            try {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                json = EntityUtils.toString(httpEntity);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                onError(e);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                onError(e);
            } catch (IOException e) {
                e.printStackTrace();
                onError(e);
            }
            return json;
        }

        private void onError(Exception e) {
            isFail = true;
            response = e.toString();
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (isNetworkAvailable) {
                if(isFail) {
                    Log.d("evotrack", response);
                    return;
                }

            }
            else {
               Application_Helper aaa=new Application_Helper(context);
               aaa.noInternetConnection();
                Log.d("evotrack", "Please check your internet connection or try again later");
            }
        }

        /**
         *
         * @return true is Internet available on device.
         */
        private boolean isNetworkAvailable() {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager
                    .getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        WebView wv = (WebView) findViewById(R.id.my_webview);

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (wv.canGoBack()) {
                        wv.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onBackPressed ()
    { WebView wv = (WebView) findViewById(R.id.my_webview);

        if (wv.isFocused() && wv.canGoBack())
        {
            wv.goBack();
        }
        else
        {
            super.onBackPressed();
            finish();
        }
    }
/*

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		WebView wv = (WebView) findViewById(R.id.my_webview);
		if((keyCode == KeyEvent.KEYCODE_BACK)&& wv.canGoBack())
		{
			wv.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

*/

	public class logout extends AsyncTask<Void, Void, Void>
	{
		private boolean isFail, isNetworkAvailable;
		private String errorMsg_1, sucessMsg_1;

		@Override
		protected void onPreExecute()
		{
			// TODO Auto-generated method stub
			super.onPreExecute();
			isFail = false;
			isNetworkAvailable = false;
			errorMsg_1 = "";
			sucessMsg_1 = "";
			if (progressDialog == null || progressDialog.isShowing() == false)
			{
				progressDialog = new Application_Helper(MainActivity.this)
				               .showProgressDialog(MainActivity.this, true);
				progressDialog.show();
			}
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			isNetworkAvailable = isNetworkAvailable();
			if (isNetworkAvailable)
			{
				sucessMsg_1 = getResponseFromUrl();
				isFail = false;
			}
			return null;
		}

		private String getResponseFromUrl()
		{
			HttpClient httpclient = new DefaultHttpClient();


            String url = new Api_Helper(MainActivity.this).getLogout(
				   url_IpAddress_logout, url_path_logout );


			HttpPost httppost = new HttpPost(url);

			String json = "";

			//this is the logout send userid
			MyPreference mypreference = new MyPreference(MainActivity.this);
			String user_id = mypreference.getUserId();
			//getting imei number
			TelephonyManager telephoneManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			telephoneManager.getDeviceId();
			String imei_device_id = telephoneManager.getDeviceId();

			try
			{
				// Add your data
				List<NameValuePair> nameValuePairs = new
						ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("userid", user_id));
				nameValuePairs.add(new BasicNameValuePair("imei", imei_device_id));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				HttpResponse response_logout = httpclient.execute(httppost);
				json = EntityUtils.toString(response_logout.getEntity());
			} catch (ClientProtocolException e)
			  {
				  // TODO Auto-generated catch block
				  e.printStackTrace();
				  onError(e);
			  }
			  catch (IOException e)
			  {
				// TODO Auto-generated catch block
				e.printStackTrace();
				onError(e);
			  }
			  return json;
		}

		private void onError(Exception e)
		{
			isFail = true;
			errorMsg_1 = e.toString();
		}

		@Override
		protected void onPostExecute(Void result)
		{
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Application_Helper applicationHelper = new Application_Helper(
					MainActivity.this);
			if (progressDialog != null && progressDialog.isShowing())
				progressDialog.dismiss();
			if (isNetworkAvailable)
			{
				if (isFail)
				{
					applicationHelper.showMessageDialog(errorMsg_1);
					return;
				}
				if (sucessMsg_1.contains("\"result\":\"true\""))
				{

					finish();
					startActivity(new Intent(MainActivity.this,Login_Activity.class));
					//set userid blank
					MyPreference myPreference = new MyPreference(MainActivity.this);
					myPreference.saveUserId("");

				}
			 }
		}
		//this logic is used to check the network is availabe or not.
				public boolean isNetworkAvailable()
				{
					// TODO Auto-generated method stub
				    ConnectivityManager ConnectManager = (ConnectivityManager)
				    		MainActivity.this
				    		.getSystemService(Context.CONNECTIVITY_SERVICE);
				    NetworkInfo NetInfo = ConnectManager.getActiveNetworkInfo();

				    return NetInfo !=null && NetInfo.isConnected();
				}
	}
}
