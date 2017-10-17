package me.umroh.iterator;

import me.umroh.iterator.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class Tentang extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Iterator");
		dialog.setMessage("Interactive Translator 2012.\nDikembangkan oleh mau.wahid@gmail.com");
		dialog.setCancelable(false);
		dialog.setPositiveButton("Ok", new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Tentang.this.finish();
			}
		});
		setContentView(R.layout.tentang);
		
		dialog.show();
	}

	
}
