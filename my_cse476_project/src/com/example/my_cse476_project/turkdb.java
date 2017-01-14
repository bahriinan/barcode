package com.example.my_cse476_project;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Activity;
import android.webkit.WebView;

public class turkdb extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.turkdb);
		WebView webview = (WebView) findViewById(R.id.webView);
		webview.getSettings().setJavaScriptEnabled(true);
		Bundle my_bundle = getIntent().getExtras();
		final ProgressDialog mydialog = ProgressDialog.show(this,"Mobil","Loading..",true,false);
		if(my_bundle != null){
			final String barcodeValue = my_bundle.getString("barcodeValue");
			if(barcodeValue.matches("[0-9]+") && barcodeValue.length() > 2 ){
				webview.loadUrl("http://barkodoku.com/" + barcodeValue);
			}
			else{
				webview.loadUrl(barcodeValue);
			}
		}
	}
}
