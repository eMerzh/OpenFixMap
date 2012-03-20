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
				        //String orig_title = next[5];
		        		String[] error_types_val = error.getManager().getContext().getResources().getStringArray(R.array.err_type_osmose_values);
		        		String[] error_types_lab = error.getManager().getContext().getResources().getStringArray(R.array.err_type_osmose_labels);
		        		String title="";
		        		for(int i1=0; i1< error_types_val.length;i1++) {
		        			if(error_types_val[i1].equals(next[7])){
		        				title = error_types_lab[i1];
		        			}
		        		}
				        tItem.setTitle(title);
				        //Set description to subtitle or title if empty
				        tItem.setDescription(next[6].equals("") ? title : next[6]);
				        tItem.getExtendedInfo().put("id",next[2]);
				        tItem.getExtendedInfo().put("type",next[2]);
				        tItem.setLink("http://osmose.openstreetmap.fr/map/?zoom=18&lat=" + tItem.getLat() + "&lon=" +tItem.getLon());
				        String str[]=next[0].split("\\.");
			            logger.info("grr "+ next[0]);
				        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
						try {
					        Date dateObj;
							dateObj = curFormater.parse(str[0]);
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
