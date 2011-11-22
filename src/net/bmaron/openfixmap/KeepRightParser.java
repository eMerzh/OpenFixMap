package net.bmaron.openfixmap;

import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


// http://keepright.ipax.at/export.php?format=gpx&ch=0,30,40,50,60,70,90,100,110,120,130,150,160,170,180,191,192,193,194,195,196,197,198,201,202,203,204,205,206,207,208,210,220,231,232,270,281,282,283,284,291,292,293,311,312,313,350,380,411,412,413,20,300,360,390&left=4.3714046044177&bottom=50.832611071166&right=4.4036984009558&top=50.839440986483
public class KeepRightParser extends DefaultHandler{
	
	private List<ErrorItem> lItems;
	
	//to maintain context
	private String tempVal;
	private ErrorItem tempItem;
	
	public KeepRightParser(){
		lItems = new ArrayList<ErrorItem>();
	}
	

	
	public void parseDocument(InputStream file) {

		//get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {

			//get a new instance of parser
			SAXParser sp = spf.newSAXParser();

			//parse the file and also register this class for call backs
			sp.parse(file, this);

		}catch(SAXException se) {
			se.printStackTrace();
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch (IOException ie) {
			ie.printStackTrace();
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
		return lItems;
	}
	
	/*
	 * 
	 * OLD PARSER WITH TAB
	 * 
        try {
            CSVReader reader = new CSVReader(new InputStreamReader(),'\t', '\0', 0);
            for(;;) {
                next = reader.readNext();
                if(next != null) {
                    list.add(next);
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



        
        List<String[]> list = new ArrayList<String[]>();

        
        List<OverlayItem> pList = new ArrayList<OverlayItem>();
        for(int i=1; i<= 10; i++) {
        	logger.info("array is"+Arrays.toString(list.get(i)));
        	if(list.get(i).length < 12) continue;
        	logger.info("Hello "+list.get(i)[0]+" World "+list.get(i)[1]);
        	
        	GeoPoint point = new GeoPoint(Double.parseDouble(list.get(i)[0]), Double.parseDouble(list.get(i)[1]));
            OverlayItem myItem = new OverlayItem(list.get(i)[2],list.get(i)[10],point);
            pList.add(myItem);

        }
	*/
	 
}
