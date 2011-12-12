package net.bmaron.openfixmap.ErrorParsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import net.bmaron.openfixmap.ErrorItem;
import net.bmaron.openfixmap.OpenFixMapActivity;
import net.bmaron.openfixmap.PlatformManager;
import net.bmaron.openfixmap.R;

public class MapDust extends ErrorPlatform {

	private String base_url;
	
	public MapDust(PlatformManager mgr) {
		super(mgr);
		String env= getManager().getPreferences().getString("env");
		if(env == null || ! env.equals("debug")) {
			base_url = "http://www.mapdust.com/api/";
		}
		else {
			base_url = "http://80.242.147.84/XY/api/";
		}
	}

	@Override
	public void load() {
		lItems.clear();
		MapDustParser parser = new MapDustParser(this);
    	parser.parse(this.boundingBox, this.eLevel, this.showClosed);
    	lItems.addAll(parser.getItems());
	}
	
	@Override
	public int getIcon() {
		return R.drawable.mapdust;
	}
	
	@Override
	public String getName() {
		return "MapDust";
	}
	@Override
	public boolean canAdd() {
		return true;
	}
	
	@Override
	public boolean closeBug(ErrorItem i) {
		super.closeBug(i);
		HttpClient httpclient = new DefaultHttpClient();

		base_url = base_url + "changeBugStatus";
		HttpPost httppost = new HttpPost(base_url);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("key", getManager().getPreferences().getString("mapdust_key")));
			nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(i.getId())));
			nameValuePairs.add(new BasicNameValuePair("nickname", "NoName"));
			nameValuePairs.add(new BasicNameValuePair("comment", "Marked as closed on OpenFixMap"));

    		switch(i.getErrorStatus())
    		{
    			case ErrorItem.ST_OPEN : nameValuePairs.add(new BasicNameValuePair("status", String.valueOf(1) )); break;
    			case ErrorItem.ST_CLOSE : nameValuePairs.add(new BasicNameValuePair("status", String.valueOf(2) )); break;
    			case ErrorItem.ST_INVALID : nameValuePairs.add(new BasicNameValuePair("status", String.valueOf(3) )); break;
    		}
			
			
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OpenFixMapActivity.class);
	        logger.info("Put: "+ httppost.getURI());
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			String resp = reader.readLine();
			JSONObject jroot = new JSONObject(resp);
			if(jroot.has("id")) {
				return true;
			}
			
	        logger.info("oook : "+resp);
		} catch (ClientProtocolException e) {
			//TODO Auto-generated catch block
		} catch (IOException e) {
			//TODO Auto-generated catch block
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return false;


	}
	
	@Override
	public boolean createBug(ErrorItem i) {
		super.createBug(i);
		HttpClient httpclient = new DefaultHttpClient();
		base_url = base_url + "addBug";
		HttpPost httppost = new HttpPost(base_url);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("key", getManager().getPreferences().getString("mapdust_key")));
			nameValuePairs.add(new BasicNameValuePair("type", i.getTitle()));
			nameValuePairs.add(new BasicNameValuePair("nickname", "NoName"));
			nameValuePairs.add(new BasicNameValuePair("description", i.getDescription()));
			nameValuePairs.add(new BasicNameValuePair("coordinates", String.valueOf(i.getLon()) +"," + String.valueOf(i.getLat())) ) ;
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OpenFixMapActivity.class);
	        logger.info("Put: "+ httppost.getURI());
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			String resp = reader.readLine();
			JSONObject jroot = new JSONObject(resp);
			if(jroot.has("id")) {
				return true;
			}
			
	        logger.info("oook : "+resp);
		} catch (ClientProtocolException e) {
			//TODO Auto-generated catch block
		} catch (IOException e) {
			//TODO Auto-generated catch block
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return false;


	}
}
