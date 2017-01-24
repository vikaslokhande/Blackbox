package com.whiznic.blackbox1;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.whiznic.blackbox1.helper.MyPreference;

import org.json.JSONObject;

/**
 * Created by Uss on 8/30/2015.
 */


public class Notification_reciver extends  GcmListenerService{
//GCMListenerService
	 //private LogggingService.Logger logger;

	    public Notification_reciver() {
	    	//logger = new LogggingService.Logger(this);
	    }
    private int NOTIFICATION_ID = 108;
    
    @Override
	public void onMessageReceived(String from, Bundle data) {
        sendNotification("Received: " + data.toString());
        if(data.containsKey("message")) {
            try {
                JSONObject jsonObject = new JSONObject(data.getString("message"));
                if(jsonObject.getString("MessageType").equals("alert_notification")) {
                    String message = jsonObject.getString("message");
                    String id=jsonObject.getString("assets_id");
                    MyPreference my=new MyPreference(this);
                    my.savedeviceid(id);

                    // Use NotificationCompat.Builder to set up our notification.
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

                    //icon appears in device notification bar and right hand corner of notification
                    builder.setSmallIcon(R.drawable.cgs_screen_logo);

                    // This intent is fired when notification is clicked
                    Intent intent = new Intent(Notification_reciver.this, com.whiznic.blackbox1.Notification.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

                    // Set the intent that will fire when the user taps the notification.
                    builder.setContentIntent(pendingIntent);


                    // Content title, which appears in large type at the top of the notification
                    builder.setContentTitle("Alerts");

                    // Content text, which appears in smaller text below the title
                    builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message)
                            .setBigContentTitle("Alert"));
                    builder.setContentText(message);

                    builder.setDefaults(Notification.DEFAULT_ALL);

                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                    // Will display the notification in the notification bar
                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDeletedMessages() {
        sendNotification("Deleted messages on server");
    }

    @Override
    public void onMessageSent(String msgId) {
        sendNotification("Upstream message sent. Id=" + msgId);
    }

    @Override
    public void onSendError(String msgId, String error) {
        sendNotification("Upstream message send error. Id=" + msgId + ", error" + error);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
       // logger.log(Log.INFO, msg);
        Log.d("KKGCM", msg);
    }

}
