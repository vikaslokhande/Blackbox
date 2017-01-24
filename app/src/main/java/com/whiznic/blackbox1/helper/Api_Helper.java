package com.whiznic.blackbox1.helper;


import android.content.Context;

public class Api_Helper 
{
	public static String HOST ="http://107.155.108.112/GPS_Tracker/mo/login.php";
	private Context context;
	private String PATH="GPS_Tracker/mo/login.php";
	public static String HOST_LOGOUT ="";
	private String PATH_LOGOUT = "/GPS_Tracker";
	
	//constructor of api_helper class
	
	public Api_Helper (Context context)
	{
		this.context = context;
		MyPreference mypreference = new MyPreference(context);
		HOST = mypreference.getIpAddress();
		PATH = mypreference.getServerPath();
		if (PATH.trim().length() > 0)
		{
			PATH = "/"+mypreference.getServerPath();
		}
	}
	
		public String getAllPointReportApi() 
		{
			return "http://" + HOST  +  PATH + "/index.php/api/all_points";
		}

		public String getDistanceReportApi() 
		{
			return "http://" + HOST  +  PATH + "/index.php/api/distance_report";
		}

		public String getStopPointsReportApi() 
		{
			return "http://" + HOST  +  PATH + "/index.php/api/stop_points";
		}

		public String getLandMarkReportApi() 
		{
			return "http://" + HOST  +  PATH + "/index.php/api/landmark_report";
		}

		public String getAreaInOutReportApi() 
		{
			return "http://" + HOST  +  PATH + "/index.php/api/area_in_out";
		}
		
		//comment this//
		//change told by dhaval sir
		public String getAlertAPI() 
		{
		 	return "http://" + HOST  +  PATH + "/index.php/api/alert_master/";
		}
		
		public String getLastPointAPI() {
			return "http://" + HOST  +  PATH + "/index.php/api/last_point/";
		}
		
		public String getAssetListApi() 
		{
			return "http://" + HOST  +  PATH + "/index.php/api/get_assets/"
					+ new MyPreference(context).getUserId() + "/{pageNo}";
		}
		
		public String getSearchAssetListApi() 
		{
			return "http://" + HOST  +  PATH + "/index.php/api/get_assets/1/1/{key}";
		}
		
		public String getMarkerPath() 
		{
			return "http://" + HOST  +  PATH + "/assets/marker-images/";
		}

		public String getLogin() 
		{
			if (HOST.trim().length() > 0)
				return "http://" + HOST  +  PATH + "/index.php/api/login";
			else
				return "";
		}
		
		public String getLogin(String ip, String path) 
		{
			/*if (HOST.trim().length() > 0)
				return "http://" + HOST  +  PATH + "/index.php/api/login";
			else*/
				return "http://" + ip  + "/" +  path + "/index.php/api/login";	
		}


    public String getAssetsList(String ip, String path, String userid)
    {
			/*if (HOST.trim().length() > 0)
				return "http://" + HOST  +  PATH + "/index.php/api/login";
			else*/
        return "http://" + ip  + "/" +  path + "/index.php/api/get_assets_sim/" + userid;
    }

    public String getAssetsBinList(String ip, String path)
    {
			/*if (HOST.trim().length() > 0)
				return "http://" + HOST  +  PATH + "/index.php/api/login";
			else*/
        return "http://" + ip  + "/" +  path + "/index.php/api/get_assets_all/1";
    }


    public String getBinAPI(String ip, String path)
    {
			/*if (HOST.trim().length() > 0)
				return "http://" + HOST  +  PATH + "/index.php/api/login";
			else*/
        return "http://" + ip  + "/" +  path + "/index.php/api/api/bin_details";
    }

    public String getLogout()
		{
			if (HOST.trim().length()>0)
			{
				return "http://"+HOST+PATH_LOGOUT+"/index.php/api/logout";
			}
			else
			{
				return "";
			}
		}
		public String getLogout(String ip, String path)
		{
			/*if (HOST.trim().length() > 0)
			{
				Exception_Handler.appendLog("this is 2", "logout");
				return "http://" + HOST  +  PATH_LOGOUT + "/index.php/api/logout";
			}
			else*/
			{
				return "http://" + ip  + "/" +  path + "/GPS_Tracker/index.php/api/logout";
				
			}
			
		}
}