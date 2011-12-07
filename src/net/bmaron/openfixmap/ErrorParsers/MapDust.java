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
import net.bmaron.openfixmap.ErrorItem;
import net.bmaron.openfixmap.OpenFixMapActivity;
import net.bmaron.openfixmap.PlatformManager;
import net.bmaron.openfixmap.R;

public class MapDust extends ErrorPlatform {


	public MapDust(PlatformManager mgr) {
		super(mgr);
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
	
	public void createBug(ErrorItem i) {
		super.createBug(i);
/*
 * Example request:
http://www.mapdust.com/api/addBug
POST:
key={YOUR_API_KEY}
coordinates=13.3798017,52.5222716
type=other
description=test
nickname=test
*/
		HttpClient httpclient = new DefaultHttpClient();
		String url;
		String env= getManager().getPreferences().getString("env");
		if(env == null || ! env.equals("debug")) {
			url = "http://www.mapdust.com/api/addBug";
		}
		else {
			url = "http://80.242.147.84/XY/api/addBug";
		}
		
		HttpPost httppost = new HttpPost(url);
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
			
			
	        logger.info("oook : "+resp);
		} catch (ClientProtocolException e) {
			//TODO Auto-generated catch block
		} catch (IOException e) {
			//TODO Auto-generated catch block
		}


	}
}
