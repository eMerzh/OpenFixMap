package net.bmaron.openfixmap;

import android.app.Activity;

public class ShowDetailsRunnable implements Runnable {
	private Activity ctx;
	private ErrorItem itm;
	public void setInfo(Activity ctx, ErrorItem itm){
		this.ctx = ctx;
		this.itm = itm;
	}
	
    public void run() { 
     DetailsDialog detail_dialog = new DetailsDialog(ctx ,itm);
   	 detail_dialog.setOwnerActivity(ctx);
   	 detail_dialog.show();
   	}
}
