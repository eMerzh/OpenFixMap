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


// 
public class KeepRightGPXParser extends DefaultHandler{
	
	private List<ErrorItem> lItems;
	
	//to maintain context
	private String tempVal;
	private ErrorItem tempItem;
	private BoundingBoxE6 boundingBox;
	
	public KeepRightGPXParser(){
		lItems = new ArrayList<ErrorItem>();
	}
	
	public KeepRightGPXParser(BoundingBoxE6 bb) {
		boundingBox = bb;
		lItems = new ArrayList<ErrorItem>();
	}
	
	public void parse(int eLevel) {

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
		
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {

			//get a new instance of parser
			SAXParser sp = spf.newSAXParser();

			//parse the file and also register this class for call backs
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			List<NameValuePair> qparams = new ArrayList<NameValuePair>();
			qparams.add(new BasicNameValuePair("format", "gpx"));
			qparams.add(new BasicNameValuePair("show_ign", "0")); // Show Ignored 
			qparams.add(new BasicNameValuePair("show_tmpign", "0")); // Show Corrected 
			qparams.add(new BasicNameValuePair("left", ""+ String.valueOf(boundingBox.getLonWestE6()/ 1E6) ));
			qparams.add(new BasicNameValuePair("bottom", ""+ String.valueOf(boundingBox.getLatSouthE6()/ 1E6 ) ));
			qparams.add(new BasicNameValuePair("right", ""+ String.valueOf(boundingBox.getLonEastE6()/ 1E6) ));
			qparams.add(new BasicNameValuePair("top", ""+ String.valueOf(boundingBox.getLatNorthE6()/ 1E6)));
			URI uri;
			uri = URIUtils.createURI("http", "keepright.ipax.at", -1, "/export.php", 
					URLEncodedUtils.format(qparams, "UTF-8") + "&ch="+errorTypes , null);
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
		tempVal = new String(ch,start,length);
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if(qName.equalsIgnoreCase("wpt")) {
			//add it to the list
			lItems.add(tempItem);
			
		}else if (qName.equalsIgnoreCase("Name")) {
			tempItem.setTitle(tempVal);
		}else if (qName.equalsIgnoreCase("Id")) {
			tempItem.setId(Integer.parseInt(tempVal));
		}else if (qName.equalsIgnoreCase("Desc")) {
			tempItem.setDescription(tempVal);
		}
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
