package com.whiznic.blackbox1;

import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;
import com.whiznic.blackbox1.helper.MyPreference;

import java.io.IOException;

/**
 * Created by Uss on 8/30/2015.
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService {

    private LoggingService.Logger logger;

    @Override
    public void onCreate() {
        super.onCreate();
        logger = new LoggingService.Logger(this);
        getTokenId();
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        getTokenId();
    }

    public void getTokenId() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InstanceID instanceID = InstanceID.getInstance(MyInstanceIDListenerService.this);
                    String token = instanceID.getToken(getResources().getString(R.string.gcm_id),
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    MyPreference appPreference = new MyPreference(MyInstanceIDListenerService.this);
                    appPreference.setGCMToken(token);
                    Log.d("Tracker", token);
                    if(appPreference.isGCMTokenChanged()) {
                        // upload token ID
//                        registerGCMId();
                   }
                    logger.log(Log.ASSERT, token);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void registerGCMId() {
    	MyPreference appPreference = new MyPreference(MyInstanceIDListenerService.this);
//        String url = APIHelper.REG_GCM;
//
//            url= url.replace("{venue_id}", appPreference.getvenueId());
//
//        url = url.replace("{gcm_id}", appPreference.getGCMToken());
//        sendInfo = new SendInfo(MyInstanceIDListenerService.this, url, new SendInfo.ServerInfo() {
//            @Override
//            public void preExecute() {
//
//            }
//
//            @Override
//            public void postExecute() {
//                if(sendInfo.isFail() == false) {
//                    Log.d("LLL", sendInfo.getSucessMsg());
//                }
//            }
//        }){
//            @Override
//            public MultipartEntity getReqEntity() throws Exception {
//                return null;
//            }
//        };
//        sendInfo.execute();
    }
}
