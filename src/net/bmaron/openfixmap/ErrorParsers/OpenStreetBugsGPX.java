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
import java.util.ArrayList;
import java.util.List;

public class OpenStreetBugsGPX extends DefaultHandler implements IErrorParser {

//http://openstreetbugs.schokokeks.org/api/0.1/getRSSfeed?b=50.62895&t=50.78353&l=6.89193&r=7.30323
//https://github.com/emka/openstreetbugs/blob/master/api/0.1/getRSSfeed
	
	
	

	private List<ErrorItem> lItems;
	
	//to maintain context
	private String tempVal;
	private ErrorItem tempItem;
	private BoundingBoxE6 boundingBox;
	
	public OpenStreetBugsGPX(){
		lItems = new ArrayList<ErrorItem>();
	}
	
	public OpenStreetBugsGPX(BoundingBoxE6 bb) {
		boundingBox = bb;
		lItems = new ArrayList<ErrorItem>();
	}
	
	public void parse(int eLevel) {

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
			tempItem = new ErrorItem();
			tempItem.setLat(Double.parseDouble(attributes.getValue("lat")));
			tempItem.setLon(Double.parseDouble(attributes.getValue("lon")));

		}
	}
	

	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = tempVal +new String(ch,start,length);
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		//org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OpenFixMapActivity.class);

		if(qName.equalsIgnoreCase("wpt")) {
			//add it to the list
			
			tempItem.setTitle("-");
			lItems.add(tempItem);
			
		}/*else if (qName.equalsIgnoreCase("Name")) {
			tempItem.setTitle("-");
		}*/else if (qName.equalsIgnoreCase("Id")) {
			tempItem.setId(Integer.parseInt(tempVal));
		}else if (qName.equalsIgnoreCase("Desc")) {
			tempItem.setDescription(tempVal);
		}
		//type
	}
	
	
	public List<ErrorItem> getItems()
	{
		org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OpenFixMapActivity.class);
        logger.info("getting items : # "+ lItems.size());
        
		return lItems;
	}

	public BoundingBoxE6 getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(BoundingBoxE6 boundingBox) {
		this.boundingBox = boundingBox;
	} 
}

