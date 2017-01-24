package com.whiznic.blackbox1.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.ArrayList;

public class MyPreference 
{
	private Context context;

	public MyPreference(Context context) 
	{
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public String getGCMToken(){
		SharedPreferences pref = context.getSharedPreferences("MIMNUCH", Context.MODE_PRIVATE);
		return pref.getString("token", "");
	}

	public boolean isGCMTokenChanged(){
		SharedPreferences pref = context.getSharedPreferences("MIMNUCH", Context.MODE_PRIVATE);
		return pref.getBoolean("isTokenChanged", false);
	}

	public void setGCMToken(String token) {
		SharedPreferences pref = context.getSharedPreferences("MIMNUCH", Context.MODE_PRIVATE);

		if(getGCMToken().equals(token))
			return;
        Editor editor = pref.edit();
		editor.putString("token", token);
		editor.putBoolean("isTokenChanged", true);
		editor.commit();
	}
	
	// UserId
	
	//public void saveUserId(String userId) 
	public String saveUserId(String userId) 
	{
		SharedPreferences preferences = context.getSharedPreferences(
				"UserDetail", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("id", userId);
		editor.commit();
		//remove userid when removed with the string
		return userId;
	}

	public String getUserId() 
	{
		SharedPreferences preferences = context.getSharedPreferences(
				"UserDetail", Context.MODE_PRIVATE);
		return preferences.getString("id", "");
	}

    public ArrayList<String> getNotifications(){
        SharedPreferences preferences = context.getSharedPreferences(
                "Notifications", Context.MODE_PRIVATE);
        ArrayList<String> notifications = new ArrayList<String>();
        for(int i = 0 ; i < preferences.getInt("count", 0); ++i) {
            notifications.add(preferences.getString("" + (i + 1), ""));
        }
        return notifications;
    }

    public void setNotification(ArrayList<String> notification)
    {
        SharedPreferences preferences = context.getSharedPreferences(
                "Notifications", Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        for(int i = 0; i < notification.size(); ++i) {
            editor.putString("" + (i + 1), notification.get(i));
        }
        editor.putInt("count", notification.size());
        editor.commit();
    }
    public void addNotification(String msg)
    {
        SharedPreferences preferences = context.getSharedPreferences(
                "Notifications", Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        ArrayList<String> notification = getNotifications();
        notification.add(msg);
        for(int i = 0; i < notification.size(); ++i) {
            editor.putString("" + (i + 1), notification.get(i));
        }
        editor.putInt("count", notification.size());
        editor.commit();
    }

	// LastId

	public void saveLastId(String LastId) {
		SharedPreferences preferences = context.getSharedPreferences(
				"UserDetail", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("LastId", LastId);
		editor.commit();
	}

	public String getLastId() {
		SharedPreferences preferences = context.getSharedPreferences("UserDetail",
				Context.MODE_PRIVATE);
		return preferences.getString("LastId", "");
	}

	// Notification Delay time

	public void saveNotificationTime(long time) {
		SharedPreferences preferences = context.getSharedPreferences("Notify",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putLong("time", time);
		editor.commit();
	}

	public long getLastNotifyTime() {
		SharedPreferences preferences = context.getSharedPreferences("Notify",
				Context.MODE_PRIVATE);
		return preferences.getLong("time", 0);
	}	

	public void savePushNotification(boolean isOnOff) {
		SharedPreferences preferences = context.getSharedPreferences("PushNotification",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean("push_notify", isOnOff);
		editor.commit();
	}

	public boolean getPushNotification() {
		SharedPreferences preferences = context.getSharedPreferences("PushNotification",
				Context.MODE_PRIVATE);
		return preferences.getBoolean("push_notify", false);
	}
	
	public void saveDashboardPushNotification(boolean isOnOff) {
		SharedPreferences preferences = context.getSharedPreferences("DashboardPushNotification",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean("push_notify", isOnOff);
		editor.commit();
	}

	public boolean getDashboardPushNotification() {
		SharedPreferences preferences = context.getSharedPreferences("DashboardPushNotification",
				Context.MODE_PRIVATE);
		return preferences.getBoolean("push_notify", false);
	}

	// Service Status
	public void saveService(String service) {
		SharedPreferences preferences = context.getSharedPreferences(
				"Settings", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("service", service);
		editor.commit();
	}

	public String getService() {
		SharedPreferences preferences = context.getSharedPreferences(
				"Settings", Context.MODE_PRIVATE);
		return preferences.getString("service", "false");
	}

	// Frequency
	public void saveFrequency(String frequency) {
		SharedPreferences preferences = context.getSharedPreferences(
				"UpdateSettings", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("frequency", frequency);
		editor.commit();
	}

	public String getFrequency() {
		SharedPreferences preferences = context.getSharedPreferences(
				"UpdateSettings", Context.MODE_PRIVATE);
		return preferences.getString("frequency", "");
	}

	public int getFrequencyUpdates() {
		SharedPreferences preferences = context.getSharedPreferences(
				"UpdateSettings", Context.MODE_PRIVATE);
		String frequency = preferences.getString("frequency", "");
		if (frequency.equals("Every 20s"))
			return 20;
		else if (frequency.equals("Every 30s"))
			return 30;
		else if (frequency.equals("Every 40s"))
			return 40;
		else if (frequency.equals("Every 1m"))
			return 60;
		else if (frequency.equals("Every 2m"))
			return 120;
		else if (frequency.equals("Every 5m"))
			return 300;
		return 60;
	}

	// GPS Accuracy
	public void saveAccuracy(String accuracy) {
		SharedPreferences preferences = context.getSharedPreferences(
				"UpdateSettings", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("accuracy", accuracy);
		editor.commit();
	}

	public String getGPSAccuracy() {
		SharedPreferences preferences = context.getSharedPreferences(
				"UpdateSettings", Context.MODE_PRIVATE);
		return preferences.getString("accuracy", "");
	}

	public int getAccuracyUpdate() {
		SharedPreferences preferences = context.getSharedPreferences(
				"UpdateSettings", Context.MODE_PRIVATE);
		String accuracy = preferences.getString("accuracy", "");
		if (accuracy.equals("Highest Possible"))
			return 4000;
		else if (accuracy.equals("Within 10m"))
			return 10;
		else if (accuracy.equals("Within 100m"))
			return 100;
		else if (accuracy.equals("Within 1000m"))
			return 1000;
		else if (accuracy.equals("Within 3000m"))
			return 3000;
		return 4000;
	}

	// App Mode
	public void saveAppMode(String appmode) {
		SharedPreferences preferences = context.getSharedPreferences(
				"UpdateSettings", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("appmode", appmode);
		editor.commit();
	}

	public String getAppMode() {
		SharedPreferences preferences = context.getSharedPreferences(
				"UpdateSettings", Context.MODE_PRIVATE);
		return preferences.getString("appmode", "");
	}

	// Contact Details

	public void saveContact(String mobileNumber, String name) {
		SharedPreferences preferences = context.getSharedPreferences("Contact",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("mobileNumber", mobileNumber);
		editor.putString("name", name);
		editor.commit();
	}

	public String getContact() {
		SharedPreferences preferences = context.getSharedPreferences("Contact",
				Context.MODE_PRIVATE);
		return preferences.getString("mobileNumber", "");
	}

	public String getContactName() {
		SharedPreferences preferences = context.getSharedPreferences("Contact",
				Context.MODE_PRIVATE);
		return preferences.getString("name", "");
	}

	// Key Activation Details

	public void saveKeyInformation(String key) {
		SharedPreferences preferences = context.getSharedPreferences("Key",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("usedKey", key);
		editor.commit();
	}

	public String getKeyInformation() {
		SharedPreferences preferences = context.getSharedPreferences("Key",
				Context.MODE_PRIVATE);
		return preferences.getString("usedKey", "");
	}
	
	public void saveMobileNumber(String mobile) {
		SharedPreferences preferences = context.getSharedPreferences("Settings",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("mobile", mobile);
		editor.commit();
	}
	
	public String getMobile() {
		SharedPreferences preferences = context.getSharedPreferences("Settings",
				Context.MODE_PRIVATE);
		return preferences.getString("mobile", "");
	}


	public void savedeviceid(String did) {
		SharedPreferences preferences = context.getSharedPreferences("did",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("did", did);
		editor.commit();
	}

	public String getdeviceid() {
		SharedPreferences preferences = context.getSharedPreferences("did",
				Context.MODE_PRIVATE);
		return preferences.getString("did", "");
	}
	public void saveKeyInformation1(String key) {
		SharedPreferences preferences = context.getSharedPreferences("Key",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("usedKey", key);
		editor.commit();
	}

	public String getKeyInformation1() {
		SharedPreferences preferences = context.getSharedPreferences("Key",
				Context.MODE_PRIVATE);
		return preferences.getString("usedKey", "");
	}


	// IP/PORT Details

	public void saveServerInformation(String ip, String urlPort) {
		SharedPreferences preferences = context.getSharedPreferences("Server",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("ipAddress", ip);
		editor.putString("port", urlPort);
		editor.commit();
	}

	public String getIpAddress() {
		SharedPreferences preferences = context.getSharedPreferences("Server",
				Context.MODE_PRIVATE);
		return preferences.getString("ipAddress", "107.155.108.112");
	}
//	return preferences.getString("ipAddress", "");
	public String getPort() {
		SharedPreferences preferences = context.getSharedPreferences("Server",
				Context.MODE_PRIVATE);
		return preferences.getString("port", "6000");
	}
	//return preferences.getString("port", "");
	// Setting Details

	public void saveDelayTime(String time) {
		SharedPreferences preferences = context.getSharedPreferences(
				"Settings", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("time", time);
		editor.commit();
	}

	public String getTime() {
		SharedPreferences preferences = context.getSharedPreferences(
				"Settings", Context.MODE_PRIVATE);
		return preferences.getString("time", "");
	}

	// Location Details

	public void saveLocation(String lat, String lng, String speed) {
		SharedPreferences preferences = context.getSharedPreferences(
				"Location", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("lat", lat);
		editor.putString("lng", lng);
		editor.putString("speed", speed);
		editor.commit();
	}

	public boolean getisFirstLocationSend() {
		SharedPreferences preferences = context.getSharedPreferences(
				"Location", Context.MODE_PRIVATE);
		return preferences.getBoolean("isFirstLocationSend", false);
	}

	public void clearFirstLocationSend() {
		SharedPreferences preferences = context.getSharedPreferences(
				"Location", Context.MODE_PRIVATE);
		preferences.edit().clear().commit();
	}

	public void clearStartLocation() {
		SharedPreferences preferences = context.getSharedPreferences(
				"Start_Location", Context.MODE_PRIVATE);
		preferences.edit().clear().commit();
	}

	public void setisFirstLocationSend(boolean isFirstLocationSend) {
		SharedPreferences preferences = context.getSharedPreferences(
				"Location", Context.MODE_PRIVATE);
		preferences.edit()
				.putBoolean("isFirstLocationSend", isFirstLocationSend)
				.commit();
	}

	public String getSpeed() {
		SharedPreferences preferences = context.getSharedPreferences(
				"Location", Context.MODE_PRIVATE);
		return preferences.getString("speed", "0");
	}

	public String getLatitude() {
		SharedPreferences preferences = context.getSharedPreferences(
				"Location", Context.MODE_PRIVATE);
		return preferences.getString("lat", "0");
	}

	public String getLongitude() {
		SharedPreferences preferences = context.getSharedPreferences(
				"Location", Context.MODE_PRIVATE);
		return preferences.getString("lng", "0");
	}

	public void saveStartLocation(String lat, String lng) {
		SharedPreferences preferences = context.getSharedPreferences(
				"Start_Location", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("lat", lat);
		editor.putString("lng", lng);
		editor.commit();
	}

	public String getisLatitude() {
		SharedPreferences preferences = context.getSharedPreferences(
				"Start_Location", Context.MODE_PRIVATE);
		return preferences.getString("lat", "");
	}

	public double getStartLatitude() {
		SharedPreferences preferences = context.getSharedPreferences(
				"Start_Location", Context.MODE_PRIVATE);
		return Double.parseDouble(preferences.getString("lat", "0"));
	}

	public void saveAccuracyInMeter(String acString) {
		SharedPreferences preferences = context.getSharedPreferences(
				"accuracy", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("accuracy", acString);
		editor.commit();
	}

	public String getServerPath() {
		SharedPreferences preferences = context.getSharedPreferences(
				"TrackerOn", Context.MODE_PRIVATE);
		
		String path = preferences.getString("path", "/GPS_Tracker");
		if(path.length() > 0 && path.charAt(0) == '/')
			return path.substring(1, path.length());
		else
			return path;
	}
	
	public void saveServerPath(String acString) {
		SharedPreferences preferences = context.getSharedPreferences(
				"TrackerOn", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		
		editor.putString("path", acString.trim().length() > 0 ? "/" + acString : acString);
		editor.commit();
	}

	public String getGPSAccuracyInMeter() {
		SharedPreferences preferences = context.getSharedPreferences(
				"accuracy", Context.MODE_PRIVATE);
		return preferences.getString("accuracy", "--");
	}

	public double getStartLongitude() {
		SharedPreferences preferences = context.getSharedPreferences(
				"Start_Location", Context.MODE_PRIVATE);
		return Double.parseDouble(preferences.getString("lng", "0"));
	}
	public void saveAPI_key() {
		SharedPreferences preferences = context.getSharedPreferences(
				"Start_Location", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("lat","AIzaSyCs5XVzigXZ9CaFa9DxPoACawHzuG4Qges" );
		//older api key
		//AIzaSyCZ7IVPABEDbMaRJ63kHKyXShG6HICwHO8
		editor.commit();
	}

	public String getAPI_Key(){
		SharedPreferences preferences = context.getSharedPreferences("api_key", Context.MODE_PRIVATE);
		return preferences.getString("api_key", "AIzaSyCs5XVzigXZ9CaFa9DxPoACawHzuG4Qges");
	}
}
