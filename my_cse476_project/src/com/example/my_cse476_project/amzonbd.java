package com.example.my_cse476_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.R.string;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ScrollView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class amzonbd extends Activity {
	private ExpandableListView my_list_view;
	private static String web_address;
	private static String temp_web_address;
	static int count;
	static ExpandableListAdapter my_list_adapter;
	List<String> my_list;
	HashMap<String, List<String>> my_hash_list;
	List<String> counrtyname;
	ArrayList<ArrayList<String>> keeper;
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.amazondbase);
		ProgressDialog newdialog = new ProgressDialog(this);
		newdialog.setMessage("Loading...");
		newdialog.setCancelable(false);
		count = 0;
		Bundle my_bundle = getIntent().getExtras();
		if(my_bundle != null){
			String barcodeValue = my_bundle.getString("barcodeValue");
			web_address = "https://api.priceapi.com/products/single?token=QFPUOZCAPKMYCMHHPJTHHIKBWBTZQXLYCHZDRVQRYYGOMTZKLSSMPIDLVXOJLVFI&" +
                    "country=de&source=amazon&currentness=daily_updated&completeness=one_page&key=gtin&value="+barcodeValue;
            new JSONParse().execute();
		}
	}
	private class JSONParse extends AsyncTask<String, String, JSONObject>{
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(amzonbd.this);			progressDialog.show();
			
		}
		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
			JSONParser jparse = new JSONParser();
			String json = "";//get json from url;
			try{
				InputStream is = OpenHttpConnection(web_address);
				BufferedReader breader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
				StringBuilder my_string_builder = new StringBuilder();
				String line = null;
				while((line = breader.readLine()) != null){
					my_string_builder.append(line +"n");
				}
				is.close();
				json = my_string_builder.toString();
				Log.d("hooopp :", json);
			}
			catch(Exception myexp){
				Log.e("buffer error","Error convert"+myexp.toString());
			}
			JSONObject jsnoObject = null;
			try{
				jsnoObject = new JSONObject(json);
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
			return jsnoObject;
		}
		@Override
		protected void onPostExecute(JSONObject json) {
			try{
				my_list = new ArrayList<String>();
				my_hash_list = new HashMap<String, List<String>>();
				if(count == 0){
					my_list.add("Germany");
					JSONObject productions = json;
					JSONArray my_new_array = productions.getJSONArray("product");
					counrtyname = new ArrayList<String>();
					keeper = new ArrayList<ArrayList<String>>();
					for(int i = 0; i <my_new_array.length();i++){
						JSONObject tempobject = my_new_array.getJSONObject(i);
						if(tempobject.getString("source") != null){
							counrtyname.add("Source : "+tempobject.getInt("source"));
						}
						if(tempobject.getString("country") != null){
							counrtyname.add("Country : "+tempobject.getInt("country"));
						}
						if(tempobject.getString("name") != null){
							counrtyname.add("Product Name : "+tempobject.getInt("name"));
						}
						if(tempobject.getString("category_name") != null){
							counrtyname.add("Category is : "+tempobject.getInt("category_name"));
						}
						if(tempobject.getString("url") != null){
							counrtyname.add("Web Address is : "+tempobject.getInt("url"));
						}
						if(tempobject.getString("dimensions") != null){
							counrtyname.add("DIM is : "+tempobject.getInt("dimensions"));
						}
						JSONArray temp2object = tempobject.getJSONArray("offers");
						int x = 6;
						for(int j = 0 ; j < temp2object.length(); j++){
							JSONObject temp2 =temp2object.getJSONObject(j);
							if(temp2.getString("shop_name") != null){
								counrtyname.add("Shop name is: "+tempobject.getInt("shop_name"));
							}
							if(temp2.getString("price") != null){
								counrtyname.add("The Price is: "+tempobject.getInt("price"));
							}
							if(temp2.getString("price_with_shipping") != null){
								counrtyname.add("The Shipping Cost is : "+tempobject.getInt("price_with_sihpping"));
							}
							if(temp2.getString("currency") != null){
								counrtyname.add("The Currency is: "+tempobject.getInt("currency"));
							}
							if(temp2.getString("details") != null){
								counrtyname.add("The Deteails is: "+tempobject.getInt("details"));
							}
							Log.d("germany    ",counrtyname.subList(6,12).toString());
							if(x+6 <= 60){
								my_hash_list.put(counrtyname.get(x),counrtyname);
								my_list.add(counrtyname.get(x));
							}
							x += 6;
							Log.d("price  ",temp2.getString("price"));
						}
						keeper.add((ArrayList<String>) counrtyname);
					}
				}
				my_list_adapter = new ExpandableListAdapter(amzonbd.this,my_list,my_hash_list);
				my_list_view.setAdapter(my_list_adapter);
				my_list_view.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
					
					@Override
					public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
						// TODO Auto-generated method stub
						Intent intent =new Intent(amzonbd.this,turkdb.class);
						intent.putExtra("barcodevalue",temp_web_address);
						startActivity(intent);
						return false;
					}
				});
				progressDialog.dismiss();
			}catch(Exception ex1){}
		}	
	}
	public InputStream OpenHttpConnection(String web_address2) throws IOException {
	// TODO Auto-generated method stub
		InputStream is = null;
		int x = -1;
		URL temp_addr = new URL (web_address2);
		String local = temp_addr.toString();
		URLConnection conn = temp_addr.openConnection();
		if(!(conn instanceof HttpURLConnection)){
			throw new IOException("NOT HTTP");
		}
		try{
			HttpURLConnection httpconn = (HttpURLConnection) conn;
			httpconn.setAllowUserInteraction(false);
			httpconn.setInstanceFollowRedirects(true);
			httpconn.setRequestMethod("GET");
			httpconn.connect();
			x = httpconn.getResponseCode();
			if(x == HttpURLConnection.HTTP_OK){
				is = httpconn.getInputStream();
			}
		}
		catch(Exception e){
			throw new IOException("Error connection");
		}
		return is;
	}
}