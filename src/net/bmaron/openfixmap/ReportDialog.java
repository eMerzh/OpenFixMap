package net.bmaron.openfixmap;

import org.osmdroid.util.GeoPoint;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;

public class ReportDialog extends Dialog {

	private GeoPoint point;
	private PlatformManager platforms; 
	public ReportDialog(Context context, GeoPoint p, PlatformManager pltForms) {
		super(context);
		platforms= pltForms;
        setContentView(R.layout.report_dialog);
        point = p;  
        setTitle(context.getResources().getString(R.string.report_dialog_title));
        getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        

		
		org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OpenFixMapActivity.class);
        logger.info("wooo");
        
        Button cancelBtn = (Button) findViewById(R.id.report_cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				dismiss();
			}
        	
        });
        
        Button saveBtn = (Button) findViewById(R.id.report_save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {

		        final EditText descriptionField = (EditText) findViewById(R.id.report_description);  
		        String description = descriptionField.getText().toString();  
		        
		        final EditText titleField = (EditText) findViewById(R.id.report_title);  
		        String title = titleField.getText().toString();
		        
		        ErrorItem i = new  ErrorItem();
		        i.setTitle(title);
		        i.setDescription(description);
		        i.setPoint(point);
		        // TODO: do better choice here
		        i.setPlatform(platforms.getActiveAllowAddPlatforms().get(0));
		        i.save();
		        
				dismiss();
			}
        	
        });
	}


}
