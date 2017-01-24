package com.whiznic.blackbox1;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.whiznic.blackbox1.helper.Application_Helper;
import com.whiznic.blackbox1.helper.DefaultSettings;
import com.whiznic.blackbox1.helper.Exception_Handler;
import com.whiznic.blackbox1.helper.MyPreference;

import java.net.URLEncoder;

public class Login_Activity extends Activity {


	private Dialog progressDialog;
	private ProgressDialog pd;
	ProgressDialog loading = null;
	private static final String url_path = "GPS_Tracker/index.php/api/login";
	private static final String url_IpAddress = "107.155.108.112";
	private static final String url_PORT = "6000";
	private static final String TAG = "Main";
	String type = "ANDROID";
	String imei_number;
	WebView wv;
	String no;
	TextView t1;
	String token;
	ProgressBar p1;
	boolean connected = false;
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Thread.setDefaultUncaughtExceptionHandler(new Exception_Handler(this));
		setContentView(R.layout.login_activity);
		MyPreference my=new MyPreference(this);
		token = my.getGCMToken();

		new DefaultSettings(Login_Activity.this).saveDefaultSettings();
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
				connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
			//we are connected to a network
			connected = true;
			init();
			//loading.dismiss();
		} else {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					Login_Activity.this);
			builder.setTitle(getResources().getString(R.string.app_name));
			builder.setMessage("Please check network connection and try again.");
			builder.setCancelable(false);
			builder.setIcon(android.R.drawable.ic_dialog_info);
			builder.setPositiveButton("Refresh",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
											int which) {
							startActivity(new Intent(Login_Activity.this, Login_Activity.class));
							init();
						}
					});
			Dialog dialog = builder.create();
			dialog.show();
		}
		startService(new Intent(Login_Activity.this, MyInstanceIDListenerService.class));

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
			if(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
				requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.SEND_SMS}, 101);
			}
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case 101:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

				} else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
					Application_Helper application_helper = new Application_Helper(Login_Activity.this);
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

	private void init() {
		loading = new ProgressDialog(this);
		loading = new ProgressDialog(this);
		loading.setCancelable(true);
		loading.setMessage("Loading");

		loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		loading.show();
		t1=(TextView)findViewById(R.id.textView);
		final MyPreference my = new MyPreference(this);
				TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		imei_number = tel.getDeviceId();

		t1.setVisibility(View.GONE);
		wv = (WebView) findViewById(R.id.my_webview);
		WebSettings webSettings = wv.getSettings();
		webSettings.setDatabaseEnabled(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setUseWideViewPort(false);
		webSettings.setAllowFileAccess(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportMultipleWindows(false);
		wv.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				//	Log.d("url", url);
				WebView wv = (WebView) findViewById(R.id.my_webview);
				ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);
				if (url.contains("http://107.155.108.112/GPS_Tracker/mo/")) {
					wv.setVisibility(View.VISIBLE);
					loading.dismiss();

				} else {
					if (url.contains("tel")) {
						no = url;
						wv.goBack();
						Intent startMain = new Intent(Intent.ACTION_MAIN);
						startMain.addCategory(Intent.CATEGORY_HOME);
						startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(startMain);
						calling();
					}
				}
			}
		});

		Toast.makeText(
				Login_Activity.this,
				"Token "+token,
				Toast.LENGTH_SHORT).show();
		wv.loadUrl("http://107.155.108.112/GPS_Tracker/mo/login.php?imei=" + imei_number + "&token_id=" + URLEncoder.encode(my.getAPI_Key()) + "&type" + type);
	}
	public void calling()
	{
		if (no.trim().length() > 0) {
			Intent intent = new Intent(Intent.ACTION_CALL, Uri
					.parse(no));
			startActivity(intent);
		} else {
			Toast.makeText(
					Login_Activity.this,
					"No Selected contact avaliable so please select contact",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		WebView wv = (WebView) findViewById(R.id.my_webview);

		wv.saveState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		WebView wv = (WebView) findViewById(R.id.my_webview);

		wv.restoreState(savedInstanceState);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		WebView wv = (WebView) findViewById(R.id.my_webview);
		String aaa = wv.getUrl();

		String bbb = null;
		bbb = aaa.substring(aaa.lastIndexOf("/") + 1);

		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
				case KeyEvent.KEYCODE_BACK:
					if (wv.canGoBack() && !bbb.isEmpty()) {
						wv.goBack();
					} else {
						wv.stopLoading();
					}
					return true;
			}

		}

		return super.onKeyDown(keyCode, event);
	}


	@Override
	public void onStart() {
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Login_ Page", // TODO: Define a title for the content shown.A
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app deep link URI is correct.
				Uri.parse("android-app://com.whiznic.blackbox1/http/host/path")
		);
		AppIndex.AppIndexApi.start(client, viewAction);
	}

	@Override
	public void onStop() {
		super.onStop();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Login_ Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app deep link URI is correct.
				Uri.parse("android-app://com.whiznic.blackbox/http/host/path")
		);
		AppIndex.AppIndexApi.end(client, viewAction);
		client.disconnect();
	}




}