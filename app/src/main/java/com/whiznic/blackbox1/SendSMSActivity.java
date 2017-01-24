package com.whiznic.blackbox1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;

import com.whiznic.blackbox1.helper.Api_Helper;
import com.whiznic.blackbox1.helper.Application_Helper;
import com.whiznic.blackbox1.helper.AssetsHelper;
import com.whiznic.blackbox1.helper.Exception_Handler;
import com.whiznic.blackbox1.helper.MyPreference;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Uss on 26/01/2016.
 */
public class SendSMSActivity extends Activity {

    private AssetsAdapter adapter;
    private EditText editText;
    private static final String url_path = "GPS_Tracker";
    private static final String url_address = "107.155.108.112";
   // private boolean isGrantPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Thread.setDefaultUncaughtExceptionHandler(new Exception_Handler(this));

        setContentView(R.layout.layout_send_sms);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
        init();
        getAssetList();
        permissionRequest();
    }

    private void permissionRequest(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if(checkSelfPermission(android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.SEND_SMS}, 101);
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private void init(){
        ListView listView = (ListView) findViewById(R.id.assets);
        adapter = new AssetsAdapter(this);
        listView.setAdapter(adapter);

        editText = (EditText) findViewById(R.id.edtMsg);

        findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = getMobileNumbers();
                if (editText.getText().toString().length() <= 0) {
                    new Application_Helper(SendSMSActivity.this).showMessageDialog("Please fill message to send");
                    editText.requestFocus();
                    return;
                } else if(mobile.trim().length() <= 0) {
                    new Application_Helper(SendSMSActivity.this).showMessageDialog("Please select assets to send");
                } else {
                    String mobiles [] = getMobileNumbers().split(",");
                    for(int i = 0; i<mobiles.length; ++i) {
                        if(mobiles[i].trim().length() > 0)
                            SmsManager.getDefault().sendTextMessage(mobiles[i], "", editText.getText().toString(), null, null);
                    }
                    Dialog dialog = new Application_Helper(SendSMSActivity.this).showMessageDialog("Your messages sent successfully");
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    });
                }
            }
        });
    }

    public String getMobileNumbers(){
        String mobile = "";
        for(int i = 0; i < adapter.getCount();++i){
            AssetsHelper helper = adapter.getItem(i);
            if(helper.isChecked)
                mobile += helper.simNumber + ",";
        }
        Log.d("Mobile", mobile);
        if(mobile.endsWith(","))
            mobile.substring(0, mobile.length() - 1);
        return mobile;
    }

    private void getAssetList(){
//        String json = "{\"assets\":[{\"assets_name\":\"0429 (355488000045035)\",\"sim_number\":\"7024145651\"},{\"assets_name\":\"5419 (007247000942)\",\"sim_number\":\"7247000942\"},{\"assets_name\":\"abc (223211425)\",\"sim_number\":\"95200\"},{\"assets_name\":\"adasd (211425)\",\"sim_number\":\"95200\"},{\"assets_name\":\"ATest (009912345678)\",\"sim_number\":\"\"},{\"assets_name\":\"Atest_WMU (000012345678)\",\"sim_number\":\"\"},{\"assets_name\":\"harshal_test (009762140039)\",\"sim_number\":\"\"},{\"assets_name\":\"MH 40 AK 2991 (358899055017548)\",\"sim_number\":\"\"},{\"assets_name\":\"MP-28-TA-0829 (007219002512)\",\"sim_number\":\"\"},{\"assets_name\":\"poonam (9762508485)\",\"sim_number\":\"\"},{\"assets_name\":\"shivaji (867970028441950)\",\"sim_number\":\"\"},{\"assets_name\":\"vidhur (007024252388)\",\"sim_number\":\"7024252388\"}],\"result\":\"true\",\"count\":12}";


        new GetAssetList().execute();
    }

    public class GetAssetList extends AsyncTask<Void, Void, Void>
    {
        private boolean isFail, isNetworkAvailable;
        private String errorMsg, sucessMsg;
        private Dialog progressDialog;

        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            super.onPreExecute();
            isFail = false;
            isNetworkAvailable = false;
            errorMsg = "";
            sucessMsg = "";
            if (progressDialog == null || progressDialog.isShowing() == false)
            {
                progressDialog = new Application_Helper(SendSMSActivity.this)
                        .showProgressDialog(SendSMSActivity.this, true);
                progressDialog.show();
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



            String url = new Api_Helper(SendSMSActivity.this).getAssetsList(
                    url_address, url_path, new MyPreference(SendSMSActivity.this).getUserId());

            Log.d("HB", url);

            HttpPost httppost = new HttpPost(url);

            String json = "";
            try
            {
                // Add your data
                List<NameValuePair> nameValuePairs = new
                        ArrayList<NameValuePair>(1);


                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                json = EntityUtils.toString(response.getEntity());

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
                    SendSMSActivity.this);
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            if (isNetworkAvailable) {
                if (isFail) {
                    applicationHelper.showMessageDialog(errorMsg);
                    return;
                }
                Log.d("HB", sucessMsg + "");
                if (sucessMsg.contains("\"result\":\"true\"")) {
                    try {
                        JSONObject jsonObject = new JSONObject(sucessMsg);
                        JSONArray jsonArray = jsonObject.getJSONArray("assets");
                        for(int i = 0; i<jsonArray.length();++i){
                            JSONObject object = jsonArray.getJSONObject(i);
                            AssetsHelper assetsHelper = new AssetsHelper();
                            assetsHelper.assetName = object.getString("assets_name");
                            assetsHelper.simNumber = object.getString("sim_number");
                            adapter.addItem(assetsHelper);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    
                }

                else if (sucessMsg.contains("\"result\":\"false\"")) {
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                SendSMSActivity.this);
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
        }

        //this logic is used to check the network is availabe or not.
        public boolean isNetworkAvailable()
        {
            // TODO Auto-generated method stub
            ConnectivityManager ConnectManager = (ConnectivityManager)
                    SendSMSActivity.this
                            .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo NetInfo = ConnectManager.getActiveNetworkInfo();
            return NetInfo !=null && NetInfo.isConnected();
        }
    }
}
