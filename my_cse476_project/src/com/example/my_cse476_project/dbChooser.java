package com.example.my_cse476_project;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class dbChooser extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose);
		ImageButton forAmazon = (ImageButton)findViewById(R.id.imageButtonAmazon);
		ImageButton forTurk = (ImageButton)findViewById(R.id.imageButtonBarkod);
		Bundle my_bund = getIntent().getExtras();
		if(my_bund != null){
			final String barcode_value = my_bund.getString("barcodevalue");
			forAmazon.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(dbChooser.this,amzonbd.class);
					intent.putExtra("barcodevalue",barcode_value);
					startActivity(intent);
				}
			});
			forTurk.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(dbChooser.this,turkdb.class);
					intent.putExtra("barcodevalue",barcode_value);
					startActivity(intent);
				}
			});
		}
		else{
			Toast.makeText(getApplicationContext(), "Please try again",Toast.LENGTH_LONG).show();
		}
	}
}
