package net.bmaron.openfixmap;

import java.util.Date;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class ErrorDetailsActivity extends Activity{ 
	protected ErrorItem item;
	protected ProgressDialog dialog;
	protected Boolean return_dialog;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.errordetail_dialog);
        
		String  tmp_pltform = getIntent().getStringExtra("error_platform");
		int tmp_id = getIntent().getIntExtra("error_id", 0);
		
		PlatformManager plManager = PlatformManager.getInstance();
		item = plManager.getItem(tmp_pltform, tmp_id);
		
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(item.getTitleOr(getResources().getString(R.string.details_bug_title)));

        TextView text = (TextView) findViewById(R.id.text);
        text.setText(item.getDescription());

        Drawable img = getResources().getDrawable(item.getPlatform().getIcon());
        img.setBounds( 0, 0, 16, 16 );
        
        title.setCompoundDrawables(img, null, null, null);
        if(item.getErrorStatus() == ErrorItem.ST_CLOSE) {
    		CheckBox checkbox = (CheckBox) findViewById(R.id.detail_mark_as_close);
    		checkbox.setVisibility(2);
        }
        
        TextView status = (TextView) findViewById(R.id.status_txt);
        if(item.getErrorStatus() == ErrorItem.ST_CLOSE) {
        	status.setTextColor(Color.GREEN);
        	status.setText(getResources().getString(R.string.dialog_status_close));
        } else if(item.getErrorStatus() == ErrorItem.ST_OPEN) {
        	status.setTextColor(Color.RED);
        	status.setText(getResources().getString(R.string.dialog_status_open));
        } else if(item.getErrorStatus() == ErrorItem.ST_INVALID) {
        	status.setTextColor(Color.YELLOW);
        	status.setText(getResources().getString(R.string.dialog_status_invalid));
        }
        
        TextView parse_name = (TextView) findViewById(R.id.parser);
        parse_name.setText(item.getPlatform().getName());
        
        TextView error_date = (TextView) findViewById(R.id.error_date);
        java.text.DateFormat formDate= DateFormat.getMediumDateFormat(this);
        Date itemDate = item.getDate();
        if(itemDate == null)
        	itemDate = new Date();
        error_date.setText(formDate.format(itemDate));
        
        Button button = (Button) findViewById(R.id.close_button);
        
        
        button.setOnClickListener(new View.OnClickListener(){
        	
			@Override
			public void onClick(View v) {
				final CheckBox checkbox = (CheckBox) findViewById(R.id.detail_mark_as_close);
				//Close the bug if checked and not already closed 
				if(checkbox.isChecked() && ErrorDetailsActivity.this.item.getErrorStatus() != ErrorItem.ST_CLOSE) {
	                finish();

					dialog = ProgressDialog.show(ErrorDetailsActivity.this, "", 
								ErrorDetailsActivity.this.getResources().getString(R.string.dialog_loading_message), true);
					new Thread() 
					{
					    public void run() { 

				    		ErrorDetailsActivity.this.item.setErrorStatus(ErrorItem.ST_CLOSE);
				    		return_dialog = ErrorDetailsActivity.this.item.getPlatform().closeError(ErrorDetailsActivity.this.item);

							runOnUiThread(new Runnable() {
							    public void run() {
							    	finish();
					                if(return_dialog) {
					                	Toast toast = Toast.makeText(ErrorDetailsActivity.this,
					                		getResources().getString(R.string.dialog_close_message),
					                		Toast.LENGTH_LONG);
					                	toast.show();
					                }
											
							    }
							});
					    }
					}.start(); 
				}else {
					finish();
				}
			}
        	
        });

        Button bFieldInfo = (Button) findViewById(R.id.note_info);
        bFieldInfo.setOnClickListener(new View.OnClickListener(){
        	
			@Override
			public void onClick(View v) {
		        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		        
		        emailIntent .setType("text/html");
		         
		        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
		        		Html.fromHtml("<a href=\""+ErrorDetailsActivity.this.item.getLink()+"\">"+ErrorDetailsActivity.this.item.getTitle()+"</a>"));
		        startActivity(Intent.createChooser(emailIntent, "Note OSM Error"));
		        //emailIntent .putExtra(android.content.Intent.EXTRA_SUBJECT, subject);

			}
        	
        });


	}
    
    @Override  
    protected void onStop() {  
     super.onStop();  
     if (dialog != null) {  
    	 dialog.dismiss();  
    	 dialog = null;  
     }  
    }  
}
