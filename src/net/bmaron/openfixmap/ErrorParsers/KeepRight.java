package net.bmaron.openfixmap.ErrorParsers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;

import android.net.Uri;

import net.bmaron.openfixmap.ErrorItem;
import net.bmaron.openfixmap.OpenFixMapActivity;
import net.bmaron.openfixmap.PlatformManager;
import net.bmaron.openfixmap.R;

public class KeepRight extends ErrorPlatform {

	public KeepRight(PlatformManager mgr) {
		super(mgr);
	}
	
	@Override
	public void load() {
		lItems.clear();
    	KeepRightCSVParser parser = new KeepRightCSVParser(this);
    	parser.parse(this.boundingBox, this.eLevel, this.showClosed);
    	lItems.addAll(parser.getItems());
	}
	
	@Override
	public int getIcon() {
		return R.drawable.keepright;
	}
	
	@Override
	public String getName() {
		return "KeepRight";
	}
	
	@Override
	public boolean canAdd() {
		return false;
	}
	
	@Override
	public boolean closeBug(ErrorItem item) {
		super.closeBug(item);
		HttpClient httpclient = new DefaultHttpClient();
		//http://keepright.ipax.at/comment.php?st=ignore_t&co=&id=<id>&schema=<schema>
		try {
			Uri.Builder b = Uri.parse("http://keepright.ipax.at/comment.php").buildUpon();
    
			b.appendQueryParameter("id", String.valueOf(item.getId()));
			int schema = Integer.parseInt((String) item.getExtendedInfo().get("schema"));
			b.appendQueryParameter("schema",  String.valueOf(schema));
			b.appendQueryParameter("co", "Marked as closed on OpenFixMap");

    		switch(item.getErrorStatus())
    		{
    			case ErrorItem.ST_OPEN : b.appendQueryParameter("st", ""); break;
    			case ErrorItem.ST_CLOSE : b.appendQueryParameter("st", "ignore_t"); break;
    			case ErrorItem.ST_INVALID : b.appendQueryParameter("st", "ignore"); break;
    		}
			
    		HttpGet httpget = new HttpGet(b.build().toString());

			org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OpenFixMapActivity.class);
	        logger.info("Put: "+ httpget.getURI());
			// Execute HTTP Post Request
    		String env= getManager().getPreferences().getString("env");
    		if(env == null || ! env.equals("debug"))
    		{
    			HttpResponse response = httpclient.execute(httpget);
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
	
	
}
