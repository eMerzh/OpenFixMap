package net.bmaron.openfixmap.ErrorParsers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.bmaron.openfixmap.ErrorItem;
import net.bmaron.openfixmap.OpenFixMapActivity;
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

public class OpenStreetBugs extends ErrorPlatform {

	public OpenStreetBugs() {
		super();
	}

	public OpenStreetBugs(BoundingBoxE6 bb, int ErrorLevel, boolean show_closed) {
		super(bb, ErrorLevel, show_closed);
	}
	
	@Override
	public void load() {
		OpenStreetBugsGPX parser = new OpenStreetBugsGPX(this);
    	parser.parse(this.eLevel, this.show_closed, this.boundingBox);
    	lItems.addAll(parser.getItems());
	}
	
	@Override
	public int getIcon() {
		return R.drawable.open_bug_marker;
	}

	@Override
	public String getName() {
		return "OpenStreetBugs";
	}
	
	@Override
	public boolean canAdd() {
		return true;
	}
	
	public void createBug(ErrorItem i) {
		super.createBug(i);
		//http://openstreetbugs.schokokeks.org/api/0.1/addPOIexec?=brol&lat=50.83374489342907&lon=4.38738708543749&text=tes%20%5Bbrol%5D
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("lat", String.valueOf(i.getLat()) ));
		qparams.add(new BasicNameValuePair("lon", String.valueOf(i.getLon()) ));
		qparams.add(new BasicNameValuePair("text", i.getDescription() + " [OpenFixMap]"));

		try {
			
			 URI uri = URIUtils.createURI("http", "openstreetbugs.schokokeks.org", -1, "/api/0.1/addPOIexec", 
					URLEncodedUtils.format(qparams, "UTF-8"), null);
			HttpGet httpget = new HttpGet(uri);
			
			
			org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OpenFixMapActivity.class);
	        logger.info("Put: "+ uri);
			//HttpResponse response = httpClient.execute(httpget, localContext);
			//response.getEntity().getContent();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
