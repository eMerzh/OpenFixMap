package net.bmaron.openfixmap.ErrorParsers;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.net.Uri;
import net.bmaron.openfixmap.ErrorItem;
import net.bmaron.openfixmap.OpenFixMapActivity;
import net.bmaron.openfixmap.PlatformManager;
import net.bmaron.openfixmap.R;

public class Osmose extends ErrorPlatform {

	public Osmose(PlatformManager mgr) {
		super(mgr);
	}

	@Override
	public void load() {
		lItems.clear();
    	OsmoseParser parser = new OsmoseParser(this);
    	parser.parse(this.boundingBox, this.eLevel, this.showClosed);
    	lItems.addAll(parser.getItems());
	}
	
	@Override
	public int getIcon() {
		return R.drawable.osmose;
	}
	
	@Override
	public String getName() {
		return "Osmose";
	}
	
	@Override
	public boolean canAdd() {
		return false;
	}
	
	@Override
	public boolean closeError(ErrorItem item) {
		super.closeError(item);
		HttpClient httpclient = new DefaultHttpClient();
		try {
			String host;
        	String env= getManager().getPreferences().getString("env");
    		if(env != null && env.equals("debug"))
    			host = "dev.osmose.openstreetmap.fr";
			else
				host = "osmose.openstreetmap.fr";
			Uri.Builder b = Uri.parse("http://" + host + "/api/0.1/closePOIexec").buildUpon();
			b.appendQueryParameter("id", (String) item.getExtendedInfo().get("id"));
    		HttpGet httpget = new HttpGet(b.build().toString());

			org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OpenFixMapActivity.class);
	        logger.info("Put: "+ httpget.getURI());
			// Execute HTTP Post Request

	        HttpResponse response = httpclient.execute(httpget);
	        logger.info("Put: "+ httpget.getURI());
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			String line = "";
			while ((line = rd.readLine()) != null) {
				logger.info("get "+line);
			}
    		return true;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	
	}
}
