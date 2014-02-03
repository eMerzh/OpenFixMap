package net.bmaron.openfixmap.ErrorParsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.bmaron.openfixmap.ErrorItem;
import net.bmaron.openfixmap.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.BoundingBoxE6;

public class OsmoseParser{
	
	private List<ErrorItem> lItems;
	protected ErrorPlatform error;
	
	public OsmoseParser(ErrorPlatform e) {
		lItems = new ArrayList<ErrorItem>();
		error = e;
	}
	
	protected String getChosenErrorsString() {
        StringBuilder sb = new StringBuilder();
        String [] checkers = error.getManager().getErrorsChoices("osmose", R.array.err_type_osmose_values);
        for(int i=0; i < checkers.length; i++) {
        	sb.append(checkers[i]+",");       	
        }
        sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
	public void parse(BoundingBoxE6 boundingBox , int eLevel, boolean show_closed)
	{
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OsmoseParser.class);
        try {
        	

        	HttpClient httpClient = new DefaultHttpClient();
        	
			StringBuilder fullURL = new StringBuilder();

			fullURL.append("http://osmose.openstreetmap.fr/en/api/0.2/errors" +
        			"?bbox=" + String.valueOf(boundingBox.getLonWestE6()/ 1E6) +
        			"," + String.valueOf(boundingBox.getLatSouthE6()/ 1E6) +
        			"," + String.valueOf(boundingBox.getLonEastE6()/ 1E6) +
        			"," + String.valueOf(boundingBox.getLatNorthE6()/ 1E6) +
        			"&item=" + getChosenErrorsString() +
        			"&full=true");
			
			
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

				JSONArray jitemArr = jroot.getJSONArray("errors");
			    SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssz", Locale.US);
			    //"lat", "lon", "error_id", "item", "source", "class", "elems", "subclass", "subtitle", "title", "level", "update", "username"
				for (int i = 0; i < jitemArr.length(); i++) {
					
					JSONArray el = jitemArr.getJSONArray(i);
	        		ErrorItem tItem = new ErrorItem(error);
	        		tItem.setLat(Double.parseDouble((String) el.get(0)));
	        		tItem.setLon(Double.parseDouble((String) el.get(1)));
	        		tItem.setId(Integer.parseInt((String)el.get(2)));
	        		String tmpTitle = (String) el.get(9);
	        		
	        		String[] error_types_val = error.getManager().getContext().getResources().getStringArray(R.array.err_type_osmose_values);
	        		String[] error_types_lab = error.getManager().getContext().getResources().getStringArray(R.array.err_type_osmose_labels);
	        		String title= tmpTitle;
	        		for(int i1=0; i1< error_types_val.length;i1++) {
	        			if(error_types_val[i1].equals((String) el.get(3))){
	        				title = error_types_lab[i1];
	        			}
	        		}
			        tItem.setTitle(title);
			        //logger.info("Type" + tItem.getLat() + "---" + tItem.getLon());

	        		tItem.setDescription(((String) el.get(8)).equals("") ? tmpTitle : (String) el.get(8));

			        tItem.getExtendedInfo().put("id", tItem.getId());
			        tItem.getExtendedInfo().put("type", (String)el.get(3));
			        tItem.setLink("http://osmose.openstreetmap.fr/map/?zoom=18&lat=" + tItem.getLat() + "&lon=" +tItem.getLon());
			        tItem.setErrorStatus(ErrorItem.ST_OPEN);
			        
	        		try {
		        		Date dateObj = curFormater.parse((String) el.get(11));
						tItem.setDate(dateObj);

					} catch (ParseException e) {
						e.printStackTrace();
					}
	        		lItems.add(tItem);
				}
			}
        }catch (IOException ie) {
			ie.printStackTrace();
		}
		catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
	}
	
	public List<ErrorItem> getItems() {
		org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OsmoseParser.class);
        logger.info("getting items : # "+ lItems.size());
        
		return lItems;
	}
}
