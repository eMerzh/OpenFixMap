package net.bmaron.openfixmap;

import org.osmdroid.util.GeoPoint;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ReportActivity extends Activity {

	private GeoPoint point;
	private PlatformManager platforms; 
	private ErrorItem item_saved = null;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_dialog);
		platforms= PlatformManager.getInstance();
		point = new GeoPoint(getIntent().getIntExtra("p_lat", 0), getIntent().getIntExtra("p_lon", 0));
        
        // Fill spinner with platforms that accept reports
        ArrayAdapter<CharSequence> adapter =new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        for(CharSequence itm : platforms.getReportPtfms()) {
        	adapter.add(itm);
        }
     
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        Spinner platformField = (Spinner) findViewById(R.id.report_platform_spin);
        platformField.setAdapter(adapter);
   		         
        Button cancelBtn = (Button) findViewById(R.id.report_cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				finish();
			}
        	
        });
        
        Button saveBtn = (Button) findViewById(R.id.report_save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {

		        final EditText descriptionField = (EditText) findViewById(R.id.report_description);  
		        String description = descriptionField.getText().toString();  
		        
		        final Spinner titleField = (Spinner) findViewById(R.id.report_title_spin);  
		        String title = getResources().getStringArray(R.array.error_type_value)
		        		[titleField.getSelectedItemPosition()];
		        
		        final Spinner PlatformField = (Spinner) findViewById(R.id.report_platform_spin);  

		        item_saved = new  ErrorItem();
		        item_saved.setTitle(title);
		        item_saved.setDescription(description);
		        item_saved.setPoint(point);
		        // TODO: do better choice here
		        item_saved.setPlatform(platforms.getActiveAllowAddPlatforms().get(PlatformField.getSelectedItemPosition()));
		        item_saved.save();
		        Toast toast;
		        if(item_saved.getSavedStatus() == ErrorItem.ER_CLEAN) {
		        	finish();
		        	toast = Toast.makeText(ReportActivity.this,
							getResources().getString(R.string.report_finish_message),
			    			Toast.LENGTH_LONG);
		        } else {
					toast = Toast.makeText(ReportActivity.this,
							getResources().getString(R.string.report_error_message),
			    			Toast.LENGTH_LONG);
		        }
		    	toast.show();	

			}
        	
        });
	}

}
