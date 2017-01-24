package com.whiznic.blackbox1;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.whiznic.blackbox1.helper.Application_Helper;
import com.whiznic.blackbox1.helper.DefaultSettings;
import com.whiznic.blackbox1.helper.Exception_Handler;
import com.whiznic.blackbox1.helper.MyPreference;

public class Notification extends Activity {


    private Dialog progressDialog;

    private static final String url_path = "GPS_Tracker/index.php/api/login";
    private static final String url_IpAddress = "107.155.108.112";
    private static final String url_PORT = "6000";
    String imei_number;
    TextView t1;
    String token;
    boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Thread.setDefaultUncaughtExceptionHandler(new Exception_Handler(this));
        setContentView(R.layout.notififcation);

        new DefaultSettings(Notification.this).saveDefaultSettings();
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
            init();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    Notification.this);
            builder.setTitle(getResources().getString(R.string.app_name));
            builder.setMessage("Please check network connection and try again.");
            builder.setIcon(android.R.drawable.ic_dialog_info);
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            // TODO Auto-generated method stub
                            dialog.dismiss();
                        }
                    });
            Dialog dialog = builder.create();
            dialog.show();
        }//	init();
//		new UIHelper(Login_Activity.this).setTypeFace(findViewById(R.id.layout_LoginActivity));
        startService(new Intent(Notification.this, MyInstanceIDListenerService.class));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.SEND_SMS}, 101);
            }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 101:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else if(grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Application_Helper application_helper = new Application_Helper(Notification.this);
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

    private void init()
    {
        TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        MyPreference my=new MyPreference(this);
        imei_number = my.getdeviceid();
        //String token_id = myPreference.getGCMToken();
        //t1 = (TextView) findViewById(R.id.textView);
        t1=(TextView)findViewById(R.id.textView);
//	t1.setText(token);

        token=	my.getGCMToken();
        t1.setText("token id is " + token);
        t1.setVisibility(View.GONE);

        //	String key = URLEncoder.encode(myPreference.getAPI_Key());
        WebView wv = (WebView) findViewById(R.id.my_webview);
        WebSettings webSettings = wv.getSettings();

        //
        //wv.setVisibility(View.INVISIBLE);
        //webSettings.setDatabaseEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        //webSettings.setUseWideViewPort(false);
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptEnabled(true);
        //webSettings.setSupportMultipleWindows(false);
//	setContentView(wv);
        //getting imei number
   //     wv.setWebViewClient(new WebViewClient() {

     //       @Override
       //     public boolean shouldOverrideUrlLoading(WebView view, String url){
         //       view.loadUrl(url);
           //     return true;
            //}

       // });
        wv.loadUrl("http://107.155.108.112/GPS_Tracker/mo/alert_report_live.php?device=" + imei_number );
//t1.setText(imei_number);
//		t2.setText(imei_number);
//new saveSettings();
  //      new login();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //	getTokenId();


    }
    @Override
    protected void onSaveInstanceState(Bundle outState )
    {
        super.onSaveInstanceState(outState);
        WebView wv = (WebView) findViewById(R.id.my_webview);

        wv.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        WebView wv = (WebView) findViewById(R.id.my_webview);

        wv.restoreState(savedInstanceState);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        WebView wv = (WebView) findViewById(R.id.my_webview);
        String aaa = wv.getUrl();

        String bbb = null;
        bbb=aaa.substring(aaa.lastIndexOf("/") + 1);

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
/*
    public boolean validation()
    {
        Application_Helper app_help = new Application_Helper(Login_Activity.this);
        if(EDT_UserName.getText().toString().trim().length() ==0)
        {
            app_help.showMessageDialog("please enter User Name");
            EDT_UserName.requestFocus();
            return false;
        }
        else if (EDT_Password.getText().toString().trim().length()==0)
        {
            app_help.showMessageDialog("please enter password");
            EDT_Password.requestFocus();
            return false;
        }
        return true;
    }

*/
/*
    public class login extends AsyncTask<Void, Void, Void>
    {
        private boolean isFail, isNetworkAvailable;
        private String errorMsg, sucessMsg, imei = "";

        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            super.onPreExecute();
            isFail = false;
            isNetworkAvailable = false;
            errorMsg = "";
            sucessMsg = "";
            imei = ((TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
            if (progressDialog == null || progressDialog.isShowing() == false)
            {
 //               progressDialog = new Application_Helper(Login_Activity.this)
   //                     .showProgressDialog(Login_Activity.this, true);
     //           progressDialog.show();
            }
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            isNetworkAvailable = isNetworkAvailable();
            if (isNetworkAvailable)
            {
                sucessMsg = getResponseFromUrl();
                isFail = false;

            }
            return null;
        }

        private String getResponseFromUrl()
        {
            HttpClient httpclient = new DefaultHttpClient();



       //     String url = new Api_Helper(Login_Activity.this).getLogin(
         //           url_IpAddress, url_path );

//            Log.d("evotrack", url);

  //          HttpPost httppost = new HttpPost(url);
//
            String json = "";

            //getting imei number
            TelephonyManager telephoneManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            telephoneManager.getDeviceId();
            String imei_device_id = telephoneManager.getDeviceId();
  //          MyPreference myPreference = new MyPreference(Login_Activity.this);
            try
            {
                // Add your data
                List<NameValuePair> nameValuePairs = new
                        ArrayList<NameValuePair>(2);
//                nameValuePairs.add(new BasicNameValuePair("username", EDT_UserName.getText().toString().trim()));
  //              nameValuePairs.add(new BasicNameValuePair("imei", imei));
    //            nameValuePairs.add(new BasicNameValuePair("token_id", myPreference.getGCMToken()));
      //          nameValuePairs.add(new BasicNameValuePair
        //                ("password", EDT_Password.getText().toString().trim()));

          //      httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
            //    HttpResponse response = httpclient.execute(httppost);
              //  json = EntityUtils.toString(response.getEntity());

            }
            catch (ClientProtocolException e)
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
            errorMsg = e.toString();
        }

        @Override
        protected void onPostExecute(Void result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            Application_Helper applicationHelper = new Application_Helper(
                    Login_Activity.this);
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            if (isNetworkAvailable) {
                if (isFail) {
//
//					Application_Helper aaa=new Application_Helper(Context);
//					aaa.noInternetConnection();
                    applicationHelper.showMessageDialog(errorMsg);
                    return;
                }
                if (sucessMsg.contains("\"result\":\"true\"")) {
                    try {

                        JSONObject jsonObject = new JSONObject(sucessMsg);
                        MyPreference myPreference = new MyPreference(Login_Activity.this);
                        myPreference.saveUserId(jsonObject.getString("msg"));

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    finish();
                    startActivity(new Intent(Login_Activity.this, MainActivity.class));
                }

                else if (sucessMsg.contains("\"result\":\"false\"")) {
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                Login_Activity.this);
                        builder.setTitle(getResources().getString(R.string.app_name));
                        builder.setMessage(new JSONObject(sucessMsg).getString("msg") + "");
                        builder.setIcon(android.R.drawable.ic_dialog_info);
                        builder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub
                                        dialog.dismiss();
                                    }
                                });
                        Dialog dialog = builder.create();
                        dialog.show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
            else
            {
                Application_Helper aaa=new Application_Helper(getApplicationContext());
                aaa.noInternetConnection();
            }
        }

        //this logic is used to check the network is availabe or not.
        public boolean isNetworkAvailable()
        {
            // TODO Auto-generated method stub
            ConnectivityManager ConnectManager = (ConnectivityManager)
                    Login_Activity.this
                            .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo NetInfo = ConnectManager.getActiveNetworkInfo();
            return NetInfo !=null && NetInfo.isConnected();
        }
    }
*/


}