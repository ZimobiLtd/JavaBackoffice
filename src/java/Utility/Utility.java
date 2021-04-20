package Utility;


import Database.DBManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jac
 */
public class Utility {
    
    public void Utility()
    {
        
    }
    
    
    public  String sendSMS(String mobile,String msg)
    {
        String respo="";String url="http://62.171.191.3:8080/StarBet_Web/SMS_API";
        String data = "mobile="+mobile+"&text="+msg;
        StringBuffer response = null;

        try
        {
            String charset = "UTF-8";
            HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();

            connection.setDoOutput(true);
            connection.setRequestProperty("cache-control", "no-cache");
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            //connection.setRequestProperty("Content-type", "application/json");
            //connection.setRequestProperty("Authorization", "Bearer ukdpyoliwya4lfch");
            //connection.setRequestProperty("Content-Length", "" + Integer.toString(data.getBytes().length));

            OutputStream output = connection.getOutputStream();
            output.write(data.getBytes("UTF-8"));
            output.close();

            int responseCode=connection.getResponseCode();
            if(responseCode==200)
            {
                InputStream inputStream = connection.getInputStream();
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                response = new StringBuffer();
                String inputLine;
                while ((inputLine = responseReader.readLine()) != null)
                {
                  response.append(inputLine);
                }
                System.out.println("Player Pin Reset Response>>>" + response.toString());
                respo=response.toString();
                responseReader.close();
            }
            else
            {
                respo="";
            }
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
        }
        
    return respo;
    }
    
    
    public String generateCode(int count) 
    {
        /*String ALPHA_NUMERIC_STRING = "15104359524231928374650007709240938170";*/
         SecureRandom objSecureRandom = new SecureRandom(); 
        int random = objSecureRandom.nextInt(10000000);
        String randomInt=String.valueOf(random);

        StringBuilder builder = new StringBuilder();

        while (count-- != 0) 
        {
           int character = (int) (Math.random() * randomInt.length());

           builder.append(randomInt.charAt(character));
           String out = builder.toString();
        }
    return builder.toString();
    }
}
