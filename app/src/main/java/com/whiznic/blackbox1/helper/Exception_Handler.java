package com.whiznic.blackbox1.helper;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Exception_Handler implements
		Thread.UncaughtExceptionHandler {
	private final Context myContext;

	public Exception_Handler(Context context) {
		myContext = context;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable exception) {
		final StringWriter stackTrace = new StringWriter();
		exception.printStackTrace(new PrintWriter(stackTrace));
		System.err.println(stackTrace);
		exception.printStackTrace();
		String version = "";
		try 
		{
			version = myContext.getPackageManager()
					.getPackageInfo(myContext.getPackageName(), 0).versionName;
		} 
		catch (NameNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			//version = "3.5";
		}
		//appendLog("ERROR  + " + stackTrace.toString(), myContext.getString(string.app_name) + "_" + version);
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(10);
	}
	
	/*@SuppressLint("SimpleDateFormat")
	private static void appendLog(String text, String prefix)
	{
		Log.d("appendLog", text);
		SimpleDateFormat date = new SimpleDateFormat("dd-MM");

		String localTime = date.format(Calendar.getInstance().getTime());

		File logFile = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/" + prefix +"_" + ".txt");
		if (!logFile.exists())
		{
			try
			{
				logFile.createNewFile();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try
		{
			// BufferedWriter for performance, true to set append to file
			// flag
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile,true));
			buf.append(text + "\n");
			buf.newLine();
			buf.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}