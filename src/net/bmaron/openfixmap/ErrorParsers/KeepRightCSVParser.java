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
import java.util.Locale;

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

import android.text.Html;

public class KeepRightCSVParser{
	
	private List<ErrorItem> lItems;
	protected ErrorPlatform error;
	
	public KeepRightCSVParser(ErrorPlatform e) {
		lItems = new ArrayList<ErrorItem>();
		error = e;
	}
	
	protected String getChosenErrorsString() {

        StringBuilder sb = new StringBuilder();
        String [] checkers = error.getManager().getErrorsChoices("keepright",R.array.err_type_keepright_values);
        
        for(int i=0; i < checkers.length; i++) {
        	sb.append(checkers[i]+",");       	
        }
        sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
	public void parse(BoundingBoxE6 boundingBox , int eLevel, boolean show_closed)
	{
		String next[] = {};

        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(KeepRightCSVParser.class);
        try {
        	

        	HttpClient httpClient = new DefaultHttpClient();
        	HttpContext localContext = new BasicHttpContext();
        	List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        	qparams.add(new BasicNameValuePair("format", "gpx"));
			if(show_closed)	{
				qparams.add(new BasicNameValuePair("show_ign", "1")); // Show Ignored 
				qparams.add(new BasicNameValuePair("show_tmpign", "1")); // Show Corrected
			}
			else {
				qparams.add(new BasicNameValuePair("show_ign", "0")); // Show Ignored 
				qparams.add(new BasicNameValuePair("show_tmpign", "0")); // Show Corrected
			}
        	qparams.add(new BasicNameValuePair("lat", String.valueOf(boundingBox.getCenter().getLatitudeE6()/ 1E6) ));
        	qparams.add(new BasicNameValuePair("lon", String.valueOf(boundingBox.getCenter().getLongitudeE6()/ 1E6 ) ));

            List<String> supportedLang= Arrays.asList("cs","da", "de", "es", "en", "et", "fa", "fi", 
            		"fr", "hu", "it", "lt", "nb", "nl", "pl", "pt_BR", "ru", "sl", "sv", "uk");
            String lang="en";
            if(supportedLang.contains(Locale.getDefault().getLanguage()))
            	lang=Locale.getDefault().getLanguage();
        	qparams.add(new BasicNameValuePair("lang", lang));

        	URI uri;
        	uri = URIUtils.createURI("http", "keepright.ipax.at", -1, "/points.php", 
					URLEncodedUtils.format(qparams, "UTF-8") + "&ch=" + getChosenErrorsString() , null);

        	HttpGet httpget = new HttpGet(uri);
            logger.info("Fetch "+ httpget.getURI());
        	HttpResponse response = httpClient.execute(httpget, localContext);
            logger.info("DONE");

        	
        	CSVReader reader = new CSVReader(new InputStreamReader(response.getEntity().getContent()),'\t', '\0', 1);
	        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	        Date dateObj;

		    for(;;) {
		    	next = reader.readNext();
		        if(next != null){
		        	if(next.length == 16) {
		        		ErrorItem tItem = new ErrorItem(error);
				        tItem.setLat(Double.parseDouble(next[0]));
				        tItem.setLon(Double.parseDouble(next[1]));
				        tItem.setTitle(next[2]);
				        tItem.setDescription(Html.fromHtml(next[10]).toString());
				        tItem.setId(Integer.parseInt(next[9]));
				        tItem.getExtendedInfo().put("schema",next[8]);
				        tItem.setLink("http://keepright.ipax.at/report_map.php?schema=" +next[8]+ "&error=" + tItem.getId());
						try {
							dateObj = curFormater.parse(next[7]);
					        tItem.setDate(dateObj);
						} catch (ParseException e) {
							e.printStackTrace();
						} 
						
						// Check status
				        if(next[12].equals("ignore")) {
				        	tItem.setErrorStatus(ErrorItem.ST_INVALID);
				        }else if(next[12].equals("ignore_t")) {
				        	tItem.setErrorStatus(ErrorItem.ST_CLOSE);
				        }else if(next[12].equals("new")) {
				        	tItem.setErrorStatus(ErrorItem.ST_OPEN);
				        }
				        
				        lItems.add(tItem);

		        	}else {
			            logger.error("Abord number of field not expected :"+ next.length);
			            logger.info(Arrays.toString(next));
		        	}
			        
		        } else {
		        	break;
		        }

		    }
        	//Error 20,300,360,390=> Warnings 
        }catch (IOException ie) {
			ie.printStackTrace();
		}
		catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	
	public List<ErrorItem> getItems() {
		org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(KeepRightCSVParser.class);
        logger.info("getting items : # "+ lItems.size());
        
		return lItems;
	}
}
