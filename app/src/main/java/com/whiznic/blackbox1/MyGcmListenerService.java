package com.whiznic.blackbox1;

import android.app.NotificationManager;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.app.PendingIntent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.whiznic.blackbox1.helper.MyPreference;

import org.json.JSONObject;

import java.util.ArrayList;

public class MyGcmListenerService extends GcmListenerService {

	private LoggingService.Logger logger;
	
	public MyGcmListenerService(){
		logger = new LoggingService.Logger(this);
     //   showNotification(1, "TESTING");
	}
	
	private int NOTIFICATION_ID = 108;
	
	@Override
	public void onMessageReceived(String from, Bundle data) {
		sendNotification("Recived:" + data.toString());

        Log.d("K", data.toString());
		if(data.containsKey("message"))
		{
			try
			{
				JSONObject jsonObject = new JSONObject(data.getString("message"));
				//passing daata to the notification services


				if (jsonObject.getString("MessageType").equals("alert_notification"))
				{
				//	String id=jsonObject.getString("assets_id");
				//	MyPreference my=new MyPreference(this);
				//	my.savedeviceid(id);


					String message = jsonObject.getString("message");

					showNotification(1, message);
					String id=jsonObject.getString("assets_id");
					MyPreference my=new MyPreference(MyGcmListenerService.this);
					my.saveKeyInformation1(id);


				}
//				JSONObject jsonObject = new JSONObject(data.getString("message"));
				//passing daata to the notification services

			}
			catch (Exception e)
			{
			e.printStackTrace();	
			}
		}
	}

    private void showNotification(int count, String detailMsg)
    {


        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(108);
        MyPreference myPreference = new MyPreference(MyGcmListenerService.this);
        ArrayList<String> notifications = myPreference.getNotifications();

        Intent intent = new Intent(MyGcmListenerService.this, com.whiznic.blackbox1.Notification.class);
        //intent.putExtra("url", "alert_report_live.php");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity
                (MyGcmListenerService.this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String msg = "";
        for(int i=0;i<notifications.size();++i)
            msg += notifications.get(i) + "\n";
        msg += detailMsg;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setAutoCancel(true);
        mBuilder.setSmallIcon(R.drawable.ic_hbgadget);
        Log.d("Notification", msg);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(msg)
                .setBigContentTitle(getString(R.string.app_name)));
        mBuilder.setContentTitle(getString(R.string.app_name));
        mBuilder.setContentText("You get " + (notifications.size() + count) + " new alerts." );
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mBuilder.setTicker(getString(R.string.app_name));

        myPreference.addNotification(detailMsg);

        //set the intent that will fire when the user taps the notifcation
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.build();
		//mBuilder.setVibrate(new long[] { 500, 500 });
		mBuilder.setLights(Color.GREEN, 300, 3000);

//		Intent notificationIntent = new Intent(this, MainActivity.class);
//		notificationIntent.putExtra("SOUND_ENABLE", true);
//		mBuilder.setContentIntent(PendingIntent.getActivity(this, 0,
//				notificationIntent, 0));
        // notificationID allows you to update the notification later on.

        mNotificationManager.notify(108, mBuilder.build());



    }
	
	@Override
	public void  onDeletedMessages() {
		sendNotification("Deleted messages on server");
	}
	
	@Override
	public void onMessageSent(String msgId) {
		sendNotification("upstream message sent. id=" + msgId);
	}
	
	@Override
	public void onSendError(String msgId, String error)
	{
		sendNotification("upstream message send error id = "+msgId+", error= "+ error);
	}
	//put the message into a notification and post it
	private void sendNotification(String msg)
	{
		logger.log(Log.INFO,msg);
		Log.d("KKGCM", msg);
	}
}