package net.bmaron.openfixmap.ErrorParsers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.bmaron.openfixmap.ErrorItem;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.osmdroid.util.BoundingBoxE6;

public class OsmoseParser{
	
	private List<ErrorItem> lItems;
	protected ErrorPlatform error;
	
	public OsmoseParser(ErrorPlatform e) {
		lItems = new ArrayList<ErrorItem>();
		error = e;
	}
	
	public void parse(BoundingBoxE6 boundingBox , int eLevel, boolean show_closed)
	{
		
		//All errors
		String errorTypes="";
		switch(eLevel) {
			case 0:  //All 
				errorTypes = "2060";
				break;
			case 1: //Only on field
				errorTypes = "2060";
				break;
			case 2:  //Few 
				errorTypes = "2060";
				break;
		}
		String next[] = {};

        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OsmoseParser.class);
        try {
        	

        	HttpClient httpClient = new DefaultHttpClient();
        	HttpContext localContext = new BasicHttpContext();
        	List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        	//# timestamp, username, error_id, lon, lat, title, subtitle, item
        	qparams.add(new BasicNameValuePair("b", String.valueOf(boundingBox.getLatSouthE6()/ 1E6) ));
        	qparams.add(new BasicNameValuePair("r", String.valueOf(boundingBox.getLonEastE6()/ 1E6) ));
        	qparams.add(new BasicNameValuePair("l", String.valueOf(boundingBox.getLonWestE6()/ 1E6) ));
        	qparams.add(new BasicNameValuePair("t", String.valueOf(boundingBox.getLatNorthE6()/ 1E6) ));

        	URI uri;
        	uri = URIUtils.createURI("http", "osmose.openstreetmap.fr", -1, "/api/0.1/getBugsByUser", 
					URLEncodedUtils.format(qparams, "UTF-8") + "&item="+errorTypes , null);

        	HttpGet httpget = new HttpGet(uri);
            logger.info("Fetch "+ httpget.getURI());
        	HttpResponse response = httpClient.execute(httpget, localContext);

        	
        	CSVReader reader = new CSVReader(new InputStreamReader(response.getEntity().getContent()), ',', '"', 1);
		    for(;;) {
		    	next = reader.readNext();
		        if(next != null){
		        	if(next.length == 8) {
		        		ErrorItem tItem = new ErrorItem(error);
				        tItem.setLat(Double.parseDouble(next[4]));
				        tItem.setLon(Double.parseDouble(next[3]));
				        tItem.setTitle("");
				        tItem.setDescription(next[5]);
				        tItem.getExtendedInfo().put("id",next[2]);
				        tItem.setLink("http://osmose.openstreetmap.fr/map/?zoom=18&lat=" + tItem.getLat() + "&lon=" +tItem.getLon());
				        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.00"); 
						try {
					        Date dateObj;
							dateObj = curFormater.parse(next[0]);
					        tItem.setDate(dateObj);

						} catch (ParseException e) {
							e.printStackTrace();
						} 
						tItem.setErrorStatus(ErrorItem.ST_OPEN);

						/*
						// Check status
				        if(next[12].equals("ignore")) {
				        	tItem.setErrorStatus(ErrorItem.ST_INVALID);
				        }else if(next[12].equals("ignore_t")) {
				        	tItem.setErrorStatus(ErrorItem.ST_CLOSE);
				        }else if(next[12].equals("new")) {
				        	tItem.setErrorStatus(ErrorItem.ST_OPEN);
				        }*/
				        
				        lItems.add(tItem);
		        	}else {
			            logger.error("Abord number of field not expected :"+ next.length);
			            logger.info(Arrays.toString(next));
		        	}
			        
		        } else {
		        	break;
		        }

		    }
        }catch (IOException ie) {
			ie.printStackTrace();
		}
		catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	
	public List<ErrorItem> getItems() {
		org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OsmoseParser.class);
        logger.info("getting items : # "+ lItems.size());
        
		return lItems;
	}
}
