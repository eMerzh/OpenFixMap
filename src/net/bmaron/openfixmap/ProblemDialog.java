package net.bmaron.openfixmap;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
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
	}

}
