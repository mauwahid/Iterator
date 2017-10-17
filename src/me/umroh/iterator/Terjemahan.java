package me.umroh.iterator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;



import android.util.Log;



public class Terjemahan {
	
	private String url = "http://www.mauwahid.web.id:80/kamus.php";
	private String pKey = "C1779AD5D34D58C79B01638A9D6534F38A3CBEAE";
	
	private String ind; //indonesia
	private String eng; //english
	private String sun; //sunda
	
	
	public String terjemahkan(String teks,String asal, String tujuan){
		String hasil = null;
		//ind = eng = bal = jaw = sun = man = nia = null;
		
		ind = eng = sun = null;
		
		if(asal.trim().contentEquals(tujuan)){
			hasil = teks;
			return hasil;
		}
		
		if(asal.trim().contentEquals("ind") && tujuan.trim().contentEquals("eng")){
			hasil = translateBing(teks, asal, tujuan);
			return hasil;
		}else if(asal.trim().contentEquals("eng") && tujuan.trim().contentEquals("ind")){
			hasil = translateBing(teks, asal, tujuan);
			return hasil;
		}else{
			kirim(teks,asal);
		}
		
		
		
		if(tujuan.trim().contentEquals("ind")){
			hasil = ind;
		}
		if(tujuan.trim().contentEquals("eng")){
			hasil = eng;
		}
		if(tujuan.trim().contentEquals("sun")){
			hasil = sun;
		}
		if(hasil == null){
			hasil = "Data Kosong";
		}
		return hasil;	
	}
	public void kirim(String teks,String asal){
		//	String hasil = "cek";
			url+= "?asal="+asal;
			url+= "&teks="+teks;
			
			String data = getData(url);
			
			try {
				JSONArray jArray = new JSONArray(data);
				JSONObject jObj;
				
				for(int i = 0; i< jArray.length();i++){
					jObj = jArray.getJSONObject(i);
					ind = jObj.getString("ind");
					eng = jObj.getString("eng");
					sun = jObj.getString("sun");
					
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	
	private String getData(String url){
		StringBuilder builder = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		try {
			
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntitiy = httpResponse.getEntity();
			InputStream inStream = httpEntitiy.getContent();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
			
			String line;
			while((line = reader.readLine())!=null){
				builder.append(line+"/n");
			}
			
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i("builder :",builder.toString());
		return builder.toString();
	}
	
	public String translateBing(String teks, String asal,String tujuan){
		String hasil = "";
		
		Translate.setKey(pKey);
		
		if(tujuan.trim().contentEquals("ind")){
			try {
				hasil = Translate.execute(teks, Language.ENGLISH, Language.INDONESIAN);
				return hasil;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
			}
		else{
			try {
				hasil = Translate.execute(teks, Language.INDONESIAN, Language.ENGLISH);
				return hasil;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
			return hasil;	
	}
	
}
