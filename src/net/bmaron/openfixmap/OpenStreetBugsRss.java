package net.bmaron.openfixmap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

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

public class OpenStreetBugsRss extends DefaultHandler {

//http://openstreetbugs.schokokeks.org/api/0.1/getRSSfeed?b=50.62895&t=50.78353&l=6.89193&r=7.30323
//https://github.com/emka/openstreetbugs/blob/master/api/0.1/getRSSfeed
	
	
	

	private List<ErrorItem> lItems;
	
	//to maintain context
	private String tempVal;
	private ErrorItem tempItem;
	private BoundingBoxE6 boundingBox;
	
	public OpenStreetBugsRss(){
		lItems = new ArrayList<ErrorItem>();
	}
	
	public OpenStreetBugsRss(BoundingBoxE6 bb) {
		boundingBox = bb;
		lItems = new ArrayList<ErrorItem>();
	}
	
	public void parse() {

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
			uri = URIUtils.createURI("http", "openstreetbugs.schokokeks.org", -1, "/api/0.1/getRSSfeed", 
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
		if(qName.equalsIgnoreCase("item")) {
			tempItem = new ErrorItem();
		}		
	}
	

	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = new String(ch,start,length);
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(tempItem == null) return; // We are still in the head
		
		if(qName.equalsIgnoreCase("item")) {
			//add it to the list
			lItems.add(tempItem);
		}else if (qName.equalsIgnoreCase("geo:lat")) {
			tempItem.setLat(Double.parseDouble(tempVal));
		}else if (qName.equalsIgnoreCase("geo:long")) {
			tempItem.setLon(Double.parseDouble(tempVal));	
		}else if (qName.equalsIgnoreCase("title")) {
			tempItem.setTitle(tempVal);
		/*}else if (qName.equalsIgnoreCase("guid")) {
			tempItem.setId(Integer.parseInt(tempVal));
			NEED TO split <guid> elem
			*/
		}else if (qName.equalsIgnoreCase("Description")) {
			tempItem.setDescription(tempVal);
		}
		/** @TODO: <LINK> element */
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

