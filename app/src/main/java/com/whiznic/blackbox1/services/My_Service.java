package com.whiznic.blackbox1.services;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsStatus.NmeaListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.whiznic.blackbox1.R.drawable;
import com.whiznic.blackbox1.R.string;
import com.whiznic.blackbox1.helper.MyPreference;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//import service.NotificationCompat;
//import com.dhaval.iHound.service.MyService.SendMessage;
//import  CGS.helper.trackeron.My_Service.SendMessage;

public class My_Service extends Service{

	Location location1 = null;
	public LocationManager locationManager = null;
	private String ipaddress, portnumber, imei_number = null;
	private Float speed = 0f, accuracy = 0f;
	private static Location oldLocation = null;
	private final int RESET_REQUEST_LOCATION = 1 * 60 * 1000;
	private final int TIME_DIFFERENCE_THRESHOLD = 1 * 60 * 1000;
	private String ns_indicator = "N", ew_indicator = "E";
	private Socket client;
	private PrintWriter printwriter;
	private TelephonyManager tel = null;
	private MyPreference myPreference;

	private long notify_intervel;
	// run on another Thread to avoid crash
	private Handler mHandler = new Handler();
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private LocationListener locationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			try {
				DateFormat dateFormatter = new SimpleDateFormat(
						"dd-MM-yyyy HH:mm:ss a");
				dateFormatter.setLenient(false);
				Date today = new Date();
				String currentDateTimeString = dateFormatter.format(today);
				String logMessage = currentDateTimeString;

				logMessage += "\nUpcoming Location:" + location.getLatitude()
						+ "," + location.getLongitude() + ","
						+ location.getProvider() + "\n";
				MyPreference appPreference = new MyPreference(My_Service.this);
				if (oldLocation == null) {
					oldLocation = location;
					appPreference.saveLocation(oldLocation.getLatitude() + "",
							oldLocation.getLongitude() + "",
							oldLocation.getSpeed() + "");
					location1 = oldLocation;
				} else {
					Location newLocation = doWorkWithNewLocation(location);
					logMessage += "\nBetter Location:"
							+ newLocation.getLatitude() + ","
							+ newLocation.getLongitude() + "\n";
					appPreference.saveLocation(newLocation.getLatitude() + "",
							newLocation.getLongitude() + "",
							newLocation.getSpeed() + "");
					location1 = newLocation;
				}


				if (myPreference.getisLatitude().trim().length() > 0) {
					Location locationstart = new Location("Start");
					locationstart.setLatitude(myPreference.getStartLatitude());
					locationstart
							.setLongitude(myPreference.getStartLongitude());

					Location locationend = new Location("Finish");

					locationend.setLatitude(location1.getLatitude()); // endpoint
																		// coordinates
					locationend.setLongitude(location1.getLongitude());

					double distance = locationstart.distanceTo(locationend); // calculate
																				// the
					// distance
					myPreference.saveLocation(location.getLatitude() + "",
							location.getLongitude() + "", location.getSpeed()
									+ "");

					if (myPreference.getAppMode().contains("Motion Wake")) {
						String motion = myPreference.getAppMode().split(",")[1];
						double modeDistance = Double.parseDouble(motion
								.substring(0, motion.length() - 1));
						Log.d("KKK", "distance" + distance + ">="
								+ modeDistance);
						appendLog(
								"Distace " + distance + " >= " + modeDistance,
								"log_");
						if (distance >= modeDistance) {
							myPreference.saveStartLocation(
									location.getLatitude() + "",
									location.getLongitude() + "");
							SendMessage sendMessageTask = new SendMessage();
							sendMessageTask.execute();

						}
					}
					Log.d("KKK", distance + "");
					myPreference.saveAccuracyInMeter(distance + "");

				}
				myPreference.saveLocation(location.getLatitude() + "",
						location.getLongitude() + "", location.getSpeed() + "");
				myPreference.saveStartLocation(location.getLatitude() + "",
						location.getLongitude() + "");
				if (myPreference.getAppMode().contains("Motion Wake")
						|| myPreference.getisFirstLocationSend() == false) {
					myPreference.setisFirstLocationSend(true);
					SendMessage sendMessageTask = new SendMessage();
					sendMessageTask.execute();
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				final StringWriter stackTrace = new StringWriter();
				e.printStackTrace(new PrintWriter(stackTrace));
				appendLog(getDate() + "\n" + stackTrace.toString(), "error_");
			}
		}
	};

	@Override
	public void onCreate() {

		try {
			myPreference = new MyPreference(My_Service.this);
			locationManager = (LocationManager) this
					.getSystemService(Context.LOCATION_SERVICE);
			requestForLocation();
			/*tel = (TelephonyManager) getApplicationContext().getSystemService(
					Context.TELEPHONY_SERVICE);

			notify_intervel = myPreference.getFrequencyUpdates() * 1000;
			

			mHandler = new Handler();
			mHandler.post(runnable);*/
			
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
                    if ( Build.VERSION.SDK_INT >= 23 &&
                            ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return  ;
                    }
					locationManager.removeUpdates(locationListener);
					requestForLocation();

					new Handler().postDelayed(this, RESET_REQUEST_LOCATION);
				}
			}, 1 * 60 * 1000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			final StringWriter stackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(stackTrace));
			appendLog(getDate() + "\n" + stackTrace.toString(), "error_");
		}

	}

	private void requestForLocation() {

		String logMessage = "in MyService\n";

		// Creating a criteria object to retrieve provider
		Criteria criteria = new Criteria();
		criteria.setPowerRequirement(Criteria.POWER_LOW); // Chose your desired
															// power consumption
															// level.
		criteria.setAccuracy(Criteria.ACCURACY_FINE); // Choose your accuracy
														// requirement.
		criteria.setSpeedRequired(true); // Chose if speed for first location
											// fix is required.
		criteria.setAltitudeRequired(false); // Choose if you use altitude.
		criteria.setBearingRequired(false); // Choose if you use bearing.
		criteria.setCostAllowed(false); // Choose if this provider can waste
										// money :-)

		// Getting the name of the best provider
		String provider = locationManager.getBestProvider(criteria, true);
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }
		oldLocation = locationManager.getLastKnownLocation(provider);
		if (oldLocation != null) {
			logMessage += String.format("Get Last Known Location:%f, %f\n",
					oldLocation.getLatitude(), oldLocation.getLongitude());
			if (new MyPreference(My_Service.this).getLatitude().equals("0")) {
				logMessage = "set first location \n";
				new MyPreference(My_Service.this).saveLocation(
						oldLocation.getLatitude() + "",
						oldLocation.getLongitude() + "", "0");
			}
		}
		locationManager
				.requestLocationUpdates(provider, 0, 0, locationListener);
		logMessage += ("Provider" + provider);
	}

	private boolean isBetterLocation(Location oldLocation, Location newLocation) {
		// If there is no old location, of course the new location is better.
		if (oldLocation == null) {
			return true;
		}

		// Check if new location is newer in time.
		boolean isNewer = newLocation.getTime() > oldLocation.getTime();

		// Check if new location more accurate. Accuracy is radius in meters, so
		// less is better.
		boolean isMoreAccurate = newLocation.getAccuracy() < oldLocation
				.getAccuracy();
		if (isMoreAccurate && isNewer) {
			// More accurate and newer is always better.
			return true;
		} else if (isMoreAccurate && !isNewer) {
			// More accurate but not newer can lead to bad fix because of user
			// movement.
			// Let us set a threshold for the maximum tolerance of time
			// difference.
			long timeDifference = newLocation.getTime() - oldLocation.getTime();

			// If time difference is not greater then allowed threshold we
			// accept it.
			if (timeDifference > -TIME_DIFFERENCE_THRESHOLD) {
				return true;
			}
		}

		return false;
	}

	private Location doWorkWithNewLocation(Location location) {

		if (isBetterLocation(oldLocation, location)) {
			// If location is better, do some user preview.
			return location;
		}

		return oldLocation;
	}
	
	
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			try {
				if (myPreference.getService().equals("true"))
				{
					outerBtnFun();
					notify_intervel = myPreference.getFrequencyUpdates() * 1000;
					mHandler.postDelayed(this, notify_intervel);
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				final StringWriter stackTrace = new StringWriter();
				e.printStackTrace(new PrintWriter(stackTrace));
				appendLog(getDate() + "\n" + stackTrace.toString(),
						"error_");
			}

		}

	};
	
	@SuppressLint("SimpleDateFormat")
	public void outerBtnFun() {
		try {

			imei_number = tel.getDeviceId();

			if (isGpsEnabled() == false) {
				MyPreference myPreference = new MyPreference(My_Service.this);
				if (myPreference.getLastNotifyTime() == 0
						|| myPreference.getLastNotifyTime()
								+ (24 * 3600 * 1000) < System
									.currentTimeMillis()) {
					NotificationCompat.Builder builder = new NotificationCompat.Builder(
							My_Service.this)
							.setSmallIcon(drawable.ic_hbgadget)
							.setContentTitle(getString(string.app_name))
							.setContentText("Would you like to enable GPS?")
							.setDefaults(
									Notification.DEFAULT_VIBRATE
											| Notification.DEFAULT_LIGHTS);
					int NOTIFICATION_ID = 12345;

					Intent targetIntent = new Intent(
							Settings.ACTION_LOCATION_SOURCE_SETTINGS);

					PendingIntent contentIntent = PendingIntent
							.getActivity(My_Service.this, 0, targetIntent,
									PendingIntent.FLAG_UPDATE_CURRENT);
					builder.setContentIntent(contentIntent);
					NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					nManager.notify(NOTIFICATION_ID, builder.build());
					myPreference.saveNotificationTime(System
							.currentTimeMillis());
				}
			}

			DateFormat dateFormatter = new SimpleDateFormat(
					"dd-MM-yyyy HH:mm:ss a");
			dateFormatter.setLenient(false);
            if ( Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return  ;
            }
			locationManager.addNmeaListener(new NmeaListener() {
				@Override
				public void onNmeaReceived(long timestamp, String nmea) {

					String indicator_arr[] = nmea.split(",");
					if (indicator_arr.length > 6) {
						ns_indicator = indicator_arr[3];
						ew_indicator = indicator_arr[5];
					}
				}
			});

			if (myPreference.getIpAddress().trim().length() > 0) {

				ipaddress = myPreference.getIpAddress();
				portnumber = myPreference.getPort();
				if (!myPreference.getAppMode().contains("Motion Wake")) {
					SendMessage sendMessageTask = new SendMessage();
					sendMessageTask.execute();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			final StringWriter stackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(stackTrace));
			appendLog(getDate() + "\n" + stackTrace.toString(), "error_");
		}
	}

	

	public void turnGPSOn() {
		Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
		intent.putExtra("enabled", true);
		sendBroadcast(intent);

		@SuppressWarnings("deprecation")
		String provider = Settings.Secure.getString(getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (!provider.contains("gps")) {
			// if gps is disabled
			final Intent poke = new Intent();
			poke.setClassName("com.android.settings",
					"com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			sendBroadcast(poke);
		}
	}

	@SuppressLint("SimpleDateFormat")
	public static void appendLog(String text, String prefix) {

		SimpleDateFormat date = new SimpleDateFormat("dd-MM");

		String localTime = date.format(Calendar.getInstance().getTime());

		File logFile = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/" + prefix + "_" + localTime + ".txt");
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			// BufferedWriter for performance, true to set append to file
			// flag
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile,
					true));
			buf.append(text + "\n");
			buf.newLine();
			buf.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private class SendMessage extends AsyncTask<Void, Void, Void> {

		@SuppressLint("SimpleDateFormat")
		@Override
		protected Void doInBackground(Void... params) {
			try {
				String messsage;
				String straccuracy = myPreference.getGPSAccuracyInMeter();
				if (straccuracy.equals("--"))
					straccuracy = "0";
				appendLog("Ip :" + ipaddress + "\nPort :" + portnumber, "log_");
				if (myPreference.getAccuracyUpdate() < Double
						.parseDouble(straccuracy)) {
					appendLog(
							"REquired Accuracy : "
									+ myPreference.getAccuracyUpdate() + "\n"
									+ "current accuracy : " + straccuracy,
							"log_");
					//return null;
				}

				client = new Socket(ipaddress, Integer.parseInt(portnumber)); // connect
																				// to
																				// the
																				// server
				printwriter = new PrintWriter(client.getOutputStream(), true);
				printwriter.println();
				// ////////////////////////////////////////////////////////
				DateFormat dateFormatter = new SimpleDateFormat(
						"dd-MM-yyyy HH:mm:ss a");
				dateFormatter.setLenient(false);
				Date today = new Date();
				String currentDateTimeString = dateFormatter.format(today);
				String phone_number = "";
				String phone_operator = "";


                int cid;
                int lac;
                try {
                    if (tel.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
                        GsmCellLocation cellLocation = (GsmCellLocation) tel
                                .getCellLocation();
                        phone_number = myPreference.getMobile();
                        phone_operator = tel.getNetworkOperatorName();
                        cid = cellLocation.getCid();
                        lac = cellLocation.getLac();
                    } else if (tel.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
                        CdmaCellLocation cellLocation = (CdmaCellLocation) tel
                                .getCellLocation();
                        phone_number = myPreference.getMobile();
                        phone_operator = tel.getNetworkOperatorName();
                        cid = cellLocation.getBaseStationLatitude();
                        lac = cellLocation.getBaseStationLongitude();
                    } else {

                        phone_number = "";
                        phone_operator = "NA";
                        cid = 0;
                        lac = 0;
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    phone_number = "";
                    phone_operator = "NA";
                    cid = 0;
                    lac = 0;
                }


                String lat = myPreference.getLatitude();
				String lng = myPreference.getLongitude();

				String strarrar[] = currentDateTimeString.split(" ");
				if (phone_number.equals("")) {
					phone_number = imei_number;
				}

				if (lat.length() == 1 && lng.length() == 1) {
					appendLog(String.format("No Location:%s. %s\n", lat, lng),
							"log_");
					return null;
				}
				messsage = phone_number + "," + imei_number + "," + lat + ","
						+ lng + "," + accuracy + "," + ns_indicator + ","
						+ ew_indicator + "," + strarrar[0] + "," + strarrar[1]
						+ "," + phone_operator + "," + cid + "," + lac + ","
						+ speed;
				Log.d("MyService", messsage);
				appendLog("Date:-" + new Date().toString() + "\n" + "TIME:-"
						+ notify_intervel + "\n" + "Message:-" + messsage,
						"log_");
				// ////////////////////////////////////////////////////
				printwriter.write(messsage); // write the message to output
				printwriter.flush();
				// stream
				BufferedReader in = new BufferedReader(new InputStreamReader(
						client.getInputStream()));
				while (true) {
					String serverResponse = in.readLine();
					if (serverResponse != null) {
						Log.d("RES", serverResponse);
						appendLog("RES" + serverResponse, "log_");
						break;

					}
				}
				printwriter.close();
				in.close();
				client.close(); // closing the connection

			} catch (UnknownHostException e) {
				e.printStackTrace();
				final StringWriter stackTrace = new StringWriter();
				e.printStackTrace(new PrintWriter(stackTrace));
				appendLog(getDate() + "\n" + stackTrace.toString(), "error_");
			} catch (IOException e) {
				e.printStackTrace();
				final StringWriter stackTrace = new StringWriter();
				e.printStackTrace(new PrintWriter(stackTrace));
				appendLog(getDate() + "\n" + stackTrace.toString(), "error_");
			}
			return null;
		}

	}

	@SuppressLint("SimpleDateFormat")
	private String getDate() {
		DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss a");
		dateFormatter.setLenient(false);
		Date today = new Date();
		String currentDateTimeString = dateFormatter.format(today);
		return currentDateTimeString;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//Exception_Handler.appendLog("OnDestroy MyService Ihound", "log");
		startService(new Intent(My_Service.this, My_Service.class));
	}

	private boolean isGpsEnabled() {
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		return service.isProviderEnabled(LocationManager.GPS_PROVIDER)
				&& service.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}

    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        return Service.START_STICKY;
    }


    @SuppressLint("NewApi")
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // TODO Auto-generated method stub
        super.onTaskRemoved(rootIntent);
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);
        Log.d("Kartik", "Myservice");
    }
}