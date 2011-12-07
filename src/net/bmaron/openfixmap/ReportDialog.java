package net.bmaron.openfixmap;

import org.osmdroid.util.GeoPoint;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ReportDialog extends Dialog {

	private GeoPoint point;
	private PlatformManager platforms; 
	private ErrorItem item_saved = null;
	public ReportDialog(Context context, GeoPoint p, PlatformManager pltForms) {
		super(context);
		platforms= pltForms;
        setContentView(R.layout.report_dialog);
        point = p;  
        setTitle(context.getResources().getString(R.string.report_dialog_title));
        getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        
        // Fill spinner with platforms that accept reports
        ArrayAdapter<CharSequence> adapter =new ArrayAdapter<CharSequence>(context, android.R.layout.simple_spinner_item);
        for(CharSequence itm : pltForms.getReportPtfms()) {
        	adapter.add(itm);
        }
     
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        Spinner platformField = (Spinner) findViewById(R.id.report_platform_spin);
        platformField.setAdapter(adapter);
   		         
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
		        
		        final Spinner titleField = (Spinner) findViewById(R.id.report_title_spin);  
		        String title = getContext().getResources().getStringArray(R.array.error_type_value)
		        		[titleField.getSelectedItemPosition()];
		        
		        final Spinner PlatformField = (Spinner) findViewById(R.id.report_platform_spin);  

		        item_saved = new  ErrorItem();
		        item_saved.setTitle(title);
		        item_saved.setDescription(description);
		        item_saved.setPoint(point);
		        // TODO: do better choice here
		        item_saved.setPlatform(platforms.getActiveAllowAddPlatforms().get(PlatformField.getSelectedItemPosition()));
		        item_saved.save();        
				dismiss();
			}
        	
        });
	}
	
	@Override
	public void dismiss(){
		super.dismiss();
		if(item_saved != null){
			Toast toast = Toast.makeText(getContext(),
					getContext().getResources().getString(R.string.report_finish_message),
	    			Toast.LENGTH_LONG);
	    	toast.show();	
		}
    	
	}

}
