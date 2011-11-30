package net.bmaron.openfixmap.ErrorParsers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import net.bmaron.openfixmap.CSVReader;
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

public class KeepRightCSVParser{
	
	private List<ErrorItem> lItems;

	public KeepRightCSVParser() {
		lItems = new ArrayList<ErrorItem>();
	}
	
	public void parse(BoundingBoxE6 boundingBox , int eLevel, boolean show_closed)
	{
		
		//All errors
		String errorTypes="";
		switch(eLevel) {
			case 0:  //All 
				errorTypes = "0,30,40,50,60,70,90,100,110,120,130,150,160,170,180," +
						"191,192,193,194,195,196,197,198,201,202,203,204,205,206," +
						"207,208,210,220,231,232,270,281,282,283,284,291,292,293," +
						"311,312,313,350,380,411,412,413";
				break;
			case 1: //Only on field
				errorTypes = "90,100,110,170,191,192,193,390";
				break;
			case 2:  //Few 
				errorTypes = "90,100,110,170,191,192,193";
				//Error 20,300,360,390=> Warnings
				break;
		//
		}
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
        	qparams.add(new BasicNameValuePair("lat", ""+ String.valueOf(boundingBox.getCenter().getLatitudeE6()/ 1E6) ));
        	qparams.add(new BasicNameValuePair("lon", ""+ String.valueOf(boundingBox.getCenter().getLongitudeE6()/ 1E6 ) ));
        	URI uri;
        	uri = URIUtils.createURI("http", "keepright.ipax.at", -1, "/points.php", 
					URLEncodedUtils.format(qparams, "UTF-8") + "&ch="+errorTypes , null);

        	HttpGet httpget = new HttpGet(uri);
            logger.info("Fetch "+ httpget.getURI());
        	HttpResponse response = httpClient.execute(httpget, localContext);

        	
        	CSVReader reader = new CSVReader(new InputStreamReader(response.getEntity().getContent()),'\t', '\0', 1);
		    for(;;) {
		    	next = reader.readNext();
		        if(next != null && next.length == 16) {
			        ErrorItem tItem = new ErrorItem("KeepRight");
			        tItem.setLat(Double.parseDouble(next[0]));
			        tItem.setLon(Double.parseDouble(next[1]));
			        tItem.setTitle(next[2]);
			        tItem.setDescription(next[10]);
			        tItem.setId(Integer.parseInt(next[9]));
			        
			        lItems.add(tItem);
		        } else {
		            logger.error("Abord number of field not expected");
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
