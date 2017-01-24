package com.whiznic.blackbox1.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class My_Reciver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		/*MyPreference myPreference = new MyPreference(context);
		String serviceStatus = myPreference.getService();
		//Exception_Handler.appendLog("RESTART == " + serviceStatus, "log");
		if (serviceStatus.equals("true")) {
			Intent service = new Intent(context, My_Service.class);
			context.startService(service);
		}
		*//*Intent service = new Intent(context, Notification_services.class);
		context.startService(service);*/
	}
}
