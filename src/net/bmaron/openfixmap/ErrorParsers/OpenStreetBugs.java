package net.bmaron.openfixmap.ErrorParsers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.bmaron.openfixmap.ErrorItem;
import net.bmaron.openfixmap.OpenFixMapActivity;
import net.bmaron.openfixmap.PlatformManager;
import net.bmaron.openfixmap.R;

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
	
	public void createBug(ErrorItem item) {
		super.createBug(item);
		
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
		HttpContext localContext = new BasicHttpContext();
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("lat", String.valueOf(item.getLat()) ));
		qparams.add(new BasicNameValuePair("lon", String.valueOf(item.getLon()) ));
		qparams.add(new BasicNameValuePair("text", title + " : " +item.getDescription() + " [OpenFixMap]"));

		try {
			
			 URI uri = URIUtils.createURI("http", "openstreetbugs.schokokeks.org", -1, "/api/0.1/addPOIexec", 
					URLEncodedUtils.format(qparams, "UTF-8"), null);
			HttpGet httpget = new HttpGet(uri);
			
			
			org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OpenFixMapActivity.class);
	        logger.info("Put: "+ uri);
    		String env= getManager().getPreferences().getString("env");
    		if(env == null || ! env.equals("debug"))
    		{
    			//HttpResponse response = 
    			httpClient.execute(httpget, localContext);
    			//response.getEntity().getContent();
    		}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
