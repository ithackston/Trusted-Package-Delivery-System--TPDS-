package com.useless.tpds;

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

import android.util.Log;

public class Database {
	
	private static String streamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } /* finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } */
        return sb.toString();
    }
 
    public static JSONObject get(String url) {
    	JSONObject json = null;
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);
        HttpResponse response;
        
        try {
        	Log.i("TPDS","Requesting " + url);
            response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
 
            if (entity != null) {
                InputStream instream = entity.getContent();
                String result= streamToString(instream);
                json = new JSONObject(result);
                instream.close();
            }
 
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        
        return json;
    }
    
    public static JSONArray getArray(String url) {
    	JSONArray json = null;
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);
        HttpResponse response;
        
        try {
            response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
 
            if (entity != null) {
                InputStream instream = entity.getContent();
                String result= streamToString(instream);
                json = new JSONArray(result);
                instream.close();
            }
 
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return json;
    }
}