package com.example.firebasetest1.RestClient;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.firebasetest1.GsonBuilder.DateDeserializer;
import com.example.firebasetest1.GsonBuilder.TimeDeserializer;
import com.example.firebasetest1.Model.Token;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class RestClient {

    public static final String BASE_URL =
            "http://capiwater-env.nnyv2mmdf9.ap-southeast-2.elasticbeanstalk.com/";

    public static Boolean getToken() {
        final String methodPath = "token";
        //initialise
        String result = get("GET",BASE_URL + methodPath);
        try {
            JSONObject jObject = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  null;
    }


    private static String get(String method,String urlString){
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        try {
            url = new URL(urlString);
//open the connection
            conn = (HttpURLConnection) url.openConnection();
//set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            //set method
            conn.setRequestMethod(method);
//add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
//Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
//read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return textResult;
    }

    public static JsonObjectRequest putToken(String url, Token token){

        Map<String, String>  params = parameters(token) ;
        JsonObjectRequest putRequest = new JsonObjectRequest (Request.Method.PUT, BASE_URL+ url, new JSONObject(params),
                response -> {
                    // response
                    Log.d("Response", response.toString());


                },
                error -> {
                    // error
                    try{
                        Log.d("Error.Response", error.getMessage());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                }
        ) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=utf-8");
                // params.put("Accept", "application/json");
                return params;
            }

        };
        return putRequest;
    }

    public static JsonObjectRequest postObject(String url, Object object) throws JSONException {

        String jsonInString = new Gson().toJson(object);
        JsonObjectRequest postRequest = new JsonObjectRequest (Request.Method.POST, BASE_URL+ url, new JSONObject(jsonInString),
                response -> {
                    // response
                    Log.d("Response", response.toString());
                },
                error -> {
                    // error

                    try{
                        Log.d("Error.Response", error.getMessage());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=utf-8");
                params.put("Accept", "application/json");
                return params;
            }

        };
        return postRequest;
    }



    public static Map<String, String> parameters(Object obj) {
        Map<String, String> map = new HashMap<>();
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try { map.put(field.getName(),(String) field.get(obj));

            } catch (Exception e) { }
        }
        return map;
    }

    private void function(){
        GsonBuilder gSonBuilder=  new GsonBuilder();
        gSonBuilder.registerTypeAdapter(Date.class, new DateDeserializer());
        gSonBuilder.registerTypeAdapter(Time.class, new TimeDeserializer());
        Gson gSon = gSonBuilder.create();
    }
}






