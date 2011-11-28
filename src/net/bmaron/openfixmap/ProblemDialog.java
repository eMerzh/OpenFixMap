package net.bmaron.openfixmap;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ProblemDialog extends Dialog{ 
	public ProblemDialog(Context context, String title, String desc) {
		super(context);
        setContentView(R.layout.errordetail_dialog);
        setTitle(title);

        TextView text = (TextView) findViewById(R.id.text);
        text.setText(desc);
        ImageView image = (ImageView) findViewById(R.id.image);
        image.setImageResource(R.drawable.robot);
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
