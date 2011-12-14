package net.bmaron.openfixmap.ErrorParsers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.bmaron.openfixmap.ErrorItem;
import net.bmaron.openfixmap.OpenFixMapActivity;
import net.bmaron.openfixmap.PlatformManager;
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

public class OpenStreetBugs extends ErrorPlatform {

	private String base_url="";
	public OpenStreetBugs(PlatformManager mgr) {
		super(mgr);
	}
	
	@Override
	public void load() {
		lItems.clear();
		OpenStreetBugsGPX parser = new OpenStreetBugsGPX(this);
    	parser.parse(this.eLevel, this.showClosed, this.boundingBox);
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
	
	@Override
	public boolean closeError(ErrorItem item) {
		super.closeError(item);
		//http://openstreetbugs.schokokeks.org/api/0.1/closePOIexec?id=620912
		HttpClient httpClient = new DefaultHttpClient();
		try {
			String uri  = "http://openstreetbugs.schokokeks.org/api/0.1/closePOIexec?id=" + item.getId();
			HttpGet httpget = new HttpGet(uri);
		
			org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OpenStreetBugs.class);
	        logger.info("Put: "+ uri);
    		String env= getManager().getPreferences().getString("env");
    		if(env == null || ! env.equals("debug"))
    		{
    			HttpResponse response = httpClient.execute(httpget, new BasicHttpContext());
    			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
    			//reader.readLine();
    		}
    		return true;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	
	}
	
	
	@Override
	public boolean createError(ErrorItem item) {
		super.createError(item);
		
		String[] error_types_val = getManager().getContext().getResources().getStringArray(R.array.error_type_value);
		String[] error_types_lab = getManager().getContext().getResources().getStringArray(R.array.error_type_label);
		String title="";
		for(int i=0; i< error_types_val.length;i++) {
			if(error_types_val[i].equals(item.getTitle())){
				title = error_types_lab[i];
			}
				
		}
		//http://openstreetbugs.schokokeks.org/api/0.1/addPOIexec?=brol&lat=50.83374489342907&lon=4.38738708543749&text=tes%20%5Bbrol%5D
		HttpClient httpClient = new DefaultHttpClient();
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("lat", String.valueOf(item.getLat()) ));
		qparams.add(new BasicNameValuePair("lon", String.valueOf(item.getLon()) ));
		qparams.add(new BasicNameValuePair("text", title + " : " +item.getDescription() + " [OpenFixMap]"));

		try {
			
			 URI uri = URIUtils.createURI("http", "openstreetbugs.schokokeks.org", -1, "/api/0.1/addPOIexec", 
					URLEncodedUtils.format(qparams, "UTF-8"), null);
			HttpGet httpget = new HttpGet(uri);
				
			org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OpenStreetBugs.class);
	        logger.info("Put: "+ uri);
    		String env= getManager().getPreferences().getString("env");
    		if(env == null || ! env.equals("debug"))
    		{
    			HttpResponse response = httpClient.execute(httpget, new BasicHttpContext());
    			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
    			String resp = reader.readLine();
    			//response.getEntity().getContent();
    		}
    		item.setErrorStatus(ErrorItem.ST_CLOSE);
    		item.setSavedStatus(ErrorItem.ER_CLEAN);
    		return true;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
