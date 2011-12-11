package net.bmaron.openfixmap.ErrorParsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.bmaron.openfixmap.ErrorItem;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.BoundingBoxE6;

public class MapDustParser{
	
	private List<ErrorItem> lItems;
	protected ErrorPlatform error;
	
	public MapDustParser(ErrorPlatform e) {
		lItems = new ArrayList<ErrorItem>();
		error = e;
	}
	
	public void parse(BoundingBoxE6 boundingBox , int eLevel, boolean show_closed)
	{
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(MapDustParser.class);
        try {
    		String key= error.getManager().getPreferences().getString("mapdust_key");
    		String env= error.getManager().getPreferences().getString("env");
    		String url;
    		if(env != null && env.equals("debug"))
    			url = "http://80.242.147.84/XY";
    		else
    			url = "http://www.mapdust.com";
    		
			StringBuilder fullURL = new StringBuilder();

			fullURL.append(url + "/api/getBugs?key=" + key +
        			"&bbox=" + String.valueOf(boundingBox.getLonWestE6()/ 1E6) +
        			"," + String.valueOf(boundingBox.getLatSouthE6()/ 1E6) +
        			"," + String.valueOf(boundingBox.getLonEastE6()/ 1E6) +
        			"," + String.valueOf(boundingBox.getLatNorthE6()/ 1E6) +
        			"&items=30");
    		if(show_closed)
    			fullURL.append("&fs=1,2,3");
    		else
    			fullURL.append("&fs=1");
    		if(eLevel == 2){
    			fullURL.append("&idd=0&minr=3");
    		}
        	HttpClient httpClient = new DefaultHttpClient();
        	HttpGet httpget = new HttpGet(fullURL.toString());
            logger.info("Fetch "+ httpget.getURI());
        	HttpResponse response = httpClient.execute(httpget);
        	StatusLine statusLine = response.getStatusLine();

			if (statusLine.getStatusCode() == 200) {
				InputStream content = response.getEntity().getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				StringBuilder builder = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				
				
				
				JSONObject jroot = new JSONObject(builder.toString());
				logger.info("Type" + jroot.getString("type"));

				JSONArray jitemArr = jroot.getJSONArray("features"); 
			    SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); 

				for (int i = 0; i < jitemArr.length(); i++) {
					JSONObject jItem = jitemArr.getJSONObject(i);
					JSONArray jPoint = jItem.getJSONObject("geometry").getJSONArray("coordinates");
	        		JSONObject jProp = jItem.getJSONObject("properties");

	        		ErrorItem tItem = new ErrorItem(error);
	        		
	        		tItem.setLon((Double) jPoint.get(0));
	        		tItem.setLat((Double) jPoint.get(1));
	        		tItem.setTitle(jProp.getString("type"));
	        		switch(jProp.getInt("status"))
	        		{
	        			case 1: tItem.setErrorStatus(ErrorItem.ST_OPEN); break;
	        			case 2: tItem.setErrorStatus(ErrorItem.ST_CLOSE); break;
	        			case 3: tItem.setErrorStatus(ErrorItem.ST_INVALID); break;
	        		}
	        		tItem.setDescription(jProp.getString("description"));

					try {
						Date dateObj = curFormater.parse(jProp.getString("date_created"));
						tItem.setDate(dateObj);

					} catch (ParseException e) {
						e.printStackTrace();
					} 
	        		lItems.add(tItem);
				}
				
			} else {
	            logger.error( "Failed to download file");
			}
			            
        }catch (IOException ie) {
//			ie.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			logger.error( e.getMessage());
			e.printStackTrace();
		}
        
	}
	
	public List<ErrorItem> getItems() {
		org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(MapDustParser.class);
        logger.info("getting items : # "+ lItems.size());
        
		return lItems;
	}
}
