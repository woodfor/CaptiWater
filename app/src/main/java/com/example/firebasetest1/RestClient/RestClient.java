package com.example.firebasetest1.RestClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class RestClient {

    private static final String BASE_URL =
            "https://i259lrxiih.execute-api.ap-southeast-2.amazonaws.com/Token/";

    public static Boolean getToken() {
        final String methodPath = "token";
        //initialise
        String result = rest("GET",BASE_URL + methodPath);
        try {
            JSONObject jObject = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  null;
    }

    private static String rest(String method,String urlString){
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


}






