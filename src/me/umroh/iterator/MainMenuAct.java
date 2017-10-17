package me.umroh.iterator;

import me.umroh.iterator.R;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainMenuAct extends ListActivity{

	private Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub[
		super.onCreate(savedInstanceState);
		
		String[] values = new String[]{"Mulai Kamera","Pengaturan","Tentang","Exit"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
				android.R.id.text1,values);
		
		setContentView(R.layout.menu);
		setListAdapter(adapter);
	
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		
		switch(position){
			
		case 0 :
			intent = new Intent(this,CameraPreview.class);
			startActivity(intent);
			break;
		case 1 :
			intent = new Intent(this,LanguagePref.class);
			startActivity(intent);
			break;
		case 2 :
			intent = new Intent(this,Tentang.class);
			startActivity(intent);
			break;
		case 3 :
			finish();
			break;
		}
	}

	
	
	
}
