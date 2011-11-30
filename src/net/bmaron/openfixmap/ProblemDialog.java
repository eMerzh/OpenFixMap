package net.bmaron.openfixmap;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ProblemDialog extends Dialog{ 
	public ProblemDialog(Context context, ErrorItem item) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//Remove Default Title
        setContentView(R.layout.errordetail_dialog);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(item.getTitle());

        TextView text = (TextView) findViewById(R.id.text);
        text.setText(item.getDescription());
        ImageView image = (ImageView) findViewById(R.id.image);
        
        if(item.getErrorParser() == "KeepRight") {
        	image.setImageResource(R.drawable.keepright);
        } else if(item.getErrorParser() == "OpenStreetBugs") {
        	image.setImageResource(R.drawable.open_bug_marker);
        }
        
        
        TextView parse_name = (TextView) findViewById(R.id.parser);
        parse_name.setText(item.getErrorParser());
        
        TextView error_date = (TextView) findViewById(R.id.error_date);
        error_date.setText(DateFormat.getMediumDateFormat(context).format(item.getDate()));
        
        Button button = (Button) findViewById(R.id.close_button);
        
        
        button.setOnClickListener(new View.OnClickListener(){
        	
			@Override
			public void onClick(View v) {
				dismiss();
			}
        	
        });
        
        Button bFieldInfo = (Button) findViewById(R.id.note_info);
        bFieldInfo.setOnClickListener(new View.OnClickListener(){
        	
			@Override
			public void onClick(View v) {
		        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		        
		        emailIntent .setType("text/html");
		         
		        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml("<a href=\"http://bmaron.net\">Here</a>"));
		        ProblemDialog.this.getContext().startActivity(Intent.createChooser(emailIntent, "Note OSM Bug"));
		        //emailIntent .putExtra(android.content.Intent.EXTRA_SUBJECT, "yeey");

			}
        	
        });


	}

}
