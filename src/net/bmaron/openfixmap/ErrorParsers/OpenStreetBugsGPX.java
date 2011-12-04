package net.bmaron.openfixmap.ErrorParsers;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.bmaron.openfixmap.ErrorItem;
import net.bmaron.openfixmap.OpenFixMapActivity;

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
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenStreetBugsGPX extends DefaultHandler {

//http://openstreetbugs.schokokeks.org/api/0.1/getRSSfeed?b=50.62895&t=50.78353&l=6.89193&r=7.30323
//https://github.com/emka/openstreetbugs/blob/master/api/0.1/getRSSfeed
	
	
	

	private List<ErrorItem> lItems;
	
	//to maintain context
	private String tempVal;
	private ErrorItem tempItem;
	protected ErrorPlatform error;
	
	public OpenStreetBugsGPX(ErrorPlatform e) {
		lItems = new ArrayList<ErrorItem>();
		error = e;
	}
	
	public void parse(int eLevel, boolean show_closed, 	BoundingBoxE6 boundingBox) {

		//get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {

			//get a new instance of parser
			SAXParser sp = spf.newSAXParser();

			//parse the file and also register this class for call backs
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			List<NameValuePair> qparams = new ArrayList<NameValuePair>();
			qparams.add(new BasicNameValuePair("l", ""+ String.valueOf(boundingBox.getLonWestE6()/ 1E6) ));
			qparams.add(new BasicNameValuePair("b", ""+ String.valueOf(boundingBox.getLatSouthE6()/ 1E6 ) ));
			qparams.add(new BasicNameValuePair("r", ""+ String.valueOf(boundingBox.getLonEastE6()/ 1E6) ));
			qparams.add(new BasicNameValuePair("t", ""+ String.valueOf(boundingBox.getLatNorthE6()/ 1E6)));
			if( !show_closed)	{
				qparams.add(new BasicNameValuePair("open", "1"));
			}
			URI uri;
			uri = URIUtils.createURI("http", "openstreetbugs.schokokeks.org", -1, "/api/0.1/getGPX", 
					URLEncodedUtils.format(qparams, "UTF-8"), null);
			HttpGet httpget = new HttpGet(uri);
			
			HttpResponse response = httpClient.execute(httpget, localContext);
			
			org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OpenFixMapActivity.class);
            logger.info("Fetch "+ httpget.getURI());

			sp.parse(response.getEntity().getContent(), this);

		}catch(SAXException se) {
			se.printStackTrace();
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch (IOException ie) {
			ie.printStackTrace();
		}
		catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	//Event Handlers
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		//reset
		tempVal = "";
		if(qName.equalsIgnoreCase("wpt")) {
			tempItem = new ErrorItem(error);
			tempItem.setLat(Double.parseDouble(attributes.getValue("lat")));
			tempItem.setLon(Double.parseDouble(attributes.getValue("lon")));

		}
	}
	

	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = tempVal +new String(ch,start,length);
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OpenFixMapActivity.class);

		if(qName.equalsIgnoreCase("wpt")) {
			//add it to the list
			
			tempItem.setTitle("New Bug");
			tempItem.setLink("http://openstreetbugs.schokokeks.org/?zoom=18&amp;lat=" + tempItem.getLat() +"&amp;lon="+tempItem.getLon());
			lItems.add(tempItem);
			
		}/*else if (qName.equalsIgnoreCase("Name")) {
			tempItem.setTitle("-");
		}*/else if (qName.equalsIgnoreCase("Id")) {
			tempItem.setId(Integer.parseInt(tempVal));
		}else if (qName.equalsIgnoreCase("Desc")) {
			
			Pattern pattern = Pattern.compile("(.+)(\\[(.*), (.*) CET\\])$");
			Matcher matcher = pattern.matcher(tempVal);
			if(matcher.find()) {
				tempItem.setDescription(matcher.group(1));
		        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss"); 
				try {
			        Date dateObj;
					dateObj = curFormater.parse(matcher.group(4));
					tempItem.setDate(dateObj);

				} catch (ParseException e) {
					e.printStackTrace();
				} 
		        
			}
			logger.info(tempVal);
			//[Bogumil, 2011-09-13 00:22:30 CEST]	
			
		}
		//type
	}
	
	
	public List<ErrorItem> getItems()
	{
		org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OpenFixMapActivity.class);
        logger.info("getting items : # "+ lItems.size());
		return lItems;
	}
}

