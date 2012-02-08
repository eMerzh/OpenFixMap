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
import net.bmaron.openfixmap.R;

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
		String next[] = {};

        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OsmoseParser.class);
        try {
        	

        	HttpClient httpClient = new DefaultHttpClient();
        	HttpContext localContext = new BasicHttpContext();
        	List<NameValuePair> qparams = new ArrayList<NameValuePair>();

        	qparams.add(new BasicNameValuePair("b", String.valueOf(boundingBox.getLatSouthE6()/ 1E6) ));
        	qparams.add(new BasicNameValuePair("r", String.valueOf(boundingBox.getLonEastE6()/ 1E6) ));
        	qparams.add(new BasicNameValuePair("l", String.valueOf(boundingBox.getLonWestE6()/ 1E6) ));
        	qparams.add(new BasicNameValuePair("t", String.valueOf(boundingBox.getLatNorthE6()/ 1E6) ));

        	String host;
        	String env= error.getManager().getPreferences().getString("env");
    		if(env != null && env.equals("debug"))
    			host = "dev.osmose.openstreetmap.fr";
    		else
    			host = "osmose.openstreetmap.fr";
    		
        	URI uri = URIUtils.createURI("http", host, -1, "/api/0.1/getBugsByUser", 
					URLEncodedUtils.format(qparams, "UTF-8") + "&item=" + getChosenErrorsString() , null);

        	HttpGet httpget = new HttpGet(uri);
            logger.info("Fetch "+ httpget.getURI());
        	HttpResponse response = httpClient.execute(httpget, localContext);

        	
        	CSVReader reader = new CSVReader(new InputStreamReader(response.getEntity().getContent()), ',', '"', 1);
        	int i= 0;
		    for(;;) {
		    	next = reader.readNext();
		        if(next != null){
		        	if(next.length == 8) {
		        		ErrorItem tItem = new ErrorItem(error);
				        tItem.setLat(Double.parseDouble(next[4]));
				        tItem.setLon(Double.parseDouble(next[3]));
				        tItem.setTitle(next[5]);
				        //Set description to subtitle or title if empty
				        tItem.setDescription(next[6].equals("") ? next[5] : next[6]);
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
						tItem.setId(i++); //Dummy Id for usability
										        
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
