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
import java.util.Locale;

import net.bmaron.openfixmap.ErrorItem;
import net.bmaron.openfixmap.R;

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

	protected String getChosenErrorsString() {

        StringBuilder sb = new StringBuilder();
        String [] checkers = error.getManager().getErrorsChoices("mapdust", R.array.err_type_mapdust_values);
        for(int i=0; i < checkers.length; i++) {
        	sb.append(checkers[i]+",");       	
        }
        sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
    
	public void parse(BoundingBoxE6 boundingBox , int eLevel, boolean show_closed)
	{
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(MapDustParser.class);
        try {
    		String key= error.getManager().getPreferences().getString("mapdust_key");
    		String env= error.getManager().getPreferences().getString("env");
    		String url;
    		if(env != null && env.equals("debug"))
    			url = "http://st.www.mapdust.com";
    		else
    			url = "http://www.mapdust.com";
    		
			StringBuilder fullURL = new StringBuilder();

			fullURL.append(url + "/api/getBugs?key=" + key +
        			"&bbox=" + String.valueOf(boundingBox.getLonWestE6()/ 1E6) +
        			"," + String.valueOf(boundingBox.getLatSouthE6()/ 1E6) +
        			"," + String.valueOf(boundingBox.getLonEastE6()/ 1E6) +
        			"," + String.valueOf(boundingBox.getLatNorthE6()/ 1E6) +
        			"&ft=" + getChosenErrorsString() +
        			"&items=30");
			
			//ft=wrong,bad,other,...
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
			    SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

				for (int i = 0; i < jitemArr.length(); i++) {
	        		ErrorItem tItem = new ErrorItem(error);

					JSONObject jItem = jitemArr.getJSONObject(i);
					JSONArray jPoint = jItem.getJSONObject("geometry").getJSONArray("coordinates");
					tItem.setId(jItem.getInt("id"));
	        		JSONObject jProp = jItem.getJSONObject("properties");

	        		
	        		tItem.setLon((Double) jPoint.get(0));
	        		tItem.setLat((Double) jPoint.get(1));
	        		
	        		String[] error_types_val = error.getManager().getContext().getResources().getStringArray(R.array.err_type_mapdust_values);
	        		String[] error_types_lab = error.getManager().getContext().getResources().getStringArray(R.array.err_type_mapdust_labels);
	        		String title="";
	        		for(int i1=0; i1< error_types_val.length;i1++) {
	        			if(error_types_val[i1].equals(jProp.getString("type"))){
	        				title = error_types_lab[i1];
	        			}
	        		}
	        		
	        		tItem.setTitle(title);
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
