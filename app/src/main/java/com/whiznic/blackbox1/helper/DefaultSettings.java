package com.whiznic.blackbox1.helper;


import android.content.Context;

public class DefaultSettings {

//	private final String IP = "223.27.30.236";
//	private final String PORT = "6006";
//	private final String PATH = "test"9;
	
	private final String APP_MODE = "Continuous Updates";
	private final String FREQUENCY = "Every 1min";
	private final String ACCURACY = "Within 100m";
	//63.142.252.191/vts
	private final String IP = "107.155.108.112";
	private final String PORT = "6000";
	private final String PATH="GPS_Tracker/index.php/api/login";
	//69.163.37.194/allclients/index.php/api/login
	private Context mContext;
	
	public DefaultSettings(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
	}
	
	public void saveDefaultSettings() 
	{
		MyPreference preference= new MyPreference(mContext);
		if(preference.getIpAddress().trim().length() == 0)
		{
            preference.saveService(false + ""); // set true to start tracking
			preference.saveServerInformation(IP, PORT);
			preference.saveServerPath(PATH);
			preference.saveAppMode(APP_MODE);
			preference.saveFrequency(FREQUENCY);
			preference.saveFrequency(ACCURACY);
		}
	}
}