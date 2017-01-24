package com.whiznic.blackbox1;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ToggleButton;

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
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Uss on 09/02/2016.
 */
public class BinDetailAcitivity extends Activity {

    private ImageView binImage;
    private static final String url_path = "Blackbox";
    private static final String url_address = "107.155.108.112";
    private BinMemberAdapter adapter;
    private Spinner memberList;
    private EditText note;
    private ToggleButton toggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Thread.setDefaultUncaughtExceptionHandler(new Exception_Handler(this));

        setContentView(R.layout.layout_bin_details);

        setupUI(findViewById(R.id.layout));

        binImage = (ImageView) findViewById(R.id.binImage);
        memberList = (Spinner) findViewById(R.id.spnMemberList);
        note = (EditText) findViewById(R.id.note);
        toggleButton = (ToggleButton) findViewById(R.id.emptyToggle);

        adapter = new BinMemberAdapter(this);
        memberList.setAdapter(adapter);

        AssetsHelper helper = new AssetsHelper();
        helper.assetName = "Select Vehicle";
        helper.simNumber = "";
        adapter.addItem(helper);

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btnTakePhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                startActivityForResult(intent, 1);
            }
        });

        findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Application_Helper application_helper = new Application_Helper(BinDetailAcitivity.this);
                if(memberList.getSelectedItemPosition() == 0) {
                    application_helper.showMessageDialog("Please select vechicle");
                    return;
                } else if(binImage.getTag() == null) {
                    application_helper.showMessageDialog("Please take photo");
                    return;
                } else if(note.getText().toString().trim().length() == 0) {
                    application_helper.showMessageDialog("Please fill note");
                    return;
                } else {
                    new Upload().execute();
                }
            }
        });
        permissionRequest();
        new GetAssetList().execute();
    }

    private void permissionRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 101);
            }
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 101:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else if(grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Application_Helper application_helper = new Application_Helper(BinDetailAcitivity.this);
                    Dialog dialog = application_helper.showMessageDialog("Please grant required permission");
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    });
                }
        }
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
                progressDialog = new Application_Helper(BinDetailAcitivity.this)
                        .showProgressDialog(BinDetailAcitivity.this, true);
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



            String url = new Api_Helper(BinDetailAcitivity.this).getAssetsBinList(
                    url_address, url_path);

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
                    BinDetailAcitivity.this);
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
                            assetsHelper.id = object.getString("id");
                            adapter.addItem(assetsHelper);
                        }
                        adapter.notifyDataSetChanged();
                        hideSoftKeyboard(BinDetailAcitivity.this);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                else if (sucessMsg.contains("\"result\":\"false\"")) {
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                BinDetailAcitivity.this);
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
                    BinDetailAcitivity.this
                            .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo NetInfo = ConnectManager.getActiveNetworkInfo();
            return NetInfo !=null && NetInfo.isConnected();
        }


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            if (requestCode == 1) {
                //h=0;
                File f = new File(Environment.getExternalStorageDirectory().toString());

                for (File temp : f.listFiles()) {

                    if (temp.getName().equals("temp.jpg")) {

                        f = temp;
                        break;

                    }

                }

                Bitmap thumbnail = decodeFile(f);

                String imgPath = f.getPath();
                Log.w("path of image", f.getPath() + "");
                binImage.setTag(imgPath);
                binImage.setImageBitmap(thumbnail);

            }
        }
    }

    private Bitmap decodeFile(File f) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 128;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }


            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap bm = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
            Bitmap bitmap = null;

            ExifInterface exif = new ExifInterface(f.getAbsolutePath());

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.e("ExifInteface .........", "rotation ="+orientation);

            //exif.setAttribute(ExifInterface.ORIENTATION_ROTATE_90, 90);

            Log.e("orientation", "" + orientation);
            Matrix m = new Matrix();
            if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
                m.postRotate(180);
                Log.e("in orientation", "" + orientation);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
                return bitmap;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                m.postRotate(90);
                Log.e("in orientation", "" + orientation);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
                return bitmap;
            }
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                m.postRotate(270);
                Log.e("in orientation", "" + orientation);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
                return bitmap;
            } else {
                if (bm.getWidth() > bm.getHeight()) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(270);
                    bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
                    return bitmap;
                }
                return bm;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(BinDetailAcitivity.this);
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }
    }

    private class Upload extends AsyncTask<Void, Void, String> {

        private Dialog progress;
        private Application_Helper appHelper;
        private boolean isFail = false, isInternetAvailable = true;
        private String errorMsg;
        private String sucessMsg;

        private String url;



        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            sucessMsg = "";
            appHelper = new Application_Helper(BinDetailAcitivity.this);
            progress = appHelper.showProgressDialog(BinDetailAcitivity.this,
                    false);
            url = new Api_Helper(BinDetailAcitivity.this).getBinAPI(
                    url_address, url_path);
            progress.show();

        }

        @Override
        protected String doInBackground(Void... params) {
            if (appHelper.isInternetAvailable(BinDetailAcitivity.this)) {

                try {

                    sucessMsg = executeMultipartPostvideo();
                    isFail = false;
                } catch (Exception e) {
                    isFail = true;
                    errorMsg = e.toString();
                    e.printStackTrace();

                }
            }

            return sucessMsg;
        }

        public String executeMultipartPostvideo() throws Exception {
            try {


                HttpClient httpClient = new DefaultHttpClient();
                HttpPost postRequest = new HttpPost(url);

                final HttpParams httpParameters = httpClient.getParams();

                HttpConnectionParams
                        .setConnectionTimeout(httpParameters, 10000);
                HttpConnectionParams.setSoTimeout(httpParameters, 10000);
                postRequest.setParams(httpParameters);
                MultipartEntity reqEntity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);
                MyPreference preference = new MyPreference(BinDetailAcitivity.this);
                reqEntity.addPart(
                        "upload_photo",
                        new FileBody(compressImage(binImage.getTag().toString())));
                reqEntity.addPart(
                        "user_id",
                        new StringBody(preference.getUserId()));
                reqEntity.addPart(
                        "lat",
                        new StringBody(preference.getLatitude()));
                reqEntity.addPart(
                        "lang",
                        new StringBody(preference.getLongitude()));
                reqEntity.addPart("empty_or_full", new StringBody(toggleButton.isChecked() ? "1" : "0"));
                reqEntity.addPart("asset_id", new StringBody(adapter.getItem(memberList.getSelectedItemPosition()).id));
                reqEntity.addPart("note", new StringBody(note.getText().toString().trim()));


                postRequest.setEntity(reqEntity);

                HttpResponse response = httpClient.execute(postRequest);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                response.getEntity().getContent(), "UTF-8"));
                String sResponse;
                StringBuilder s = new StringBuilder();

                while ((sResponse = reader.readLine()) != null) {
                    s = s.append(sResponse);
                }
                //Log.d("KKK", s.toString());
                return s.toString();
            } catch (Exception e) {
                // handle exception here
                Log.e(e.getClass().getName(), e.getMessage());
                e.printStackTrace();
                errorMsg = e.toString();
                isFail = true;

            }
            return null;
        }

        private File compressImage(String image) {
            try {
                Bitmap bitmap = decodeFile(new File (image));
                String path = Environment.getExternalStorageDirectory().toString();
                OutputStream fOut = null;
                File file = new File(path, "hb.jpg"); // the File to save to
                fOut = new FileOutputStream(file);
                // obtaining the Bitmap
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                fOut.flush();
                fOut.close();
                return file;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new File(image);
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            closeDialog();

            if (isInternetAvailable == false) {
                appHelper
                        .showMessageDialog("Please check your internet connection");
            } else if (isFail) {
                appHelper
                        .showMessageDialog(errorMsg);
            }

            if (isFail == false) {
                if(sucessMsg == null)
                    return;
                String json = (new String(sucessMsg.getBytes()));

                if (json.contains("\"result\":\"true\"")) {

                    try {
                        Dialog dialog = appHelper.showMessageDialog("Bin details successfully saved");
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                dialog.dismiss();
                                finish();
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }
        }

        private void closeDialog() {
            try {
                if (progress != null)
                    if (progress.isShowing())
                        progress.dismiss();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        }
    }
}
