/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Implimentation.MessageImplimentation;

import Database.DBManager;
import Utility.Utility;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author jac
 */
public class MessageImpl {
    
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    public MessageImpl()
    {
        
    }
    
    
    public JSONArray saveSentMessage(String messageSender,String messageReceiver,String message)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        int status=500;
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();
        String query = "insert into sent_messages (message_sender,message_receiver,message) values(?,?,?) ";

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();

            ps = conn.prepareStatement(query);
            ps.setString(1, messageSender);                
            ps.setString(2, messageReceiver);
            ps.setString(3, message);
            int i=ps.executeUpdate();
            if(i > 0)
            {
                dataObj  = new JSONObject();
                dataObj.put("message","Messaged saved successfully");
            }
            else
            {
                dataObj  = new JSONObject();
                dataObj.put("error","Request failed");
            }
            dataArray.put(dataObj);
        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error saveSentMessage=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

     return dataArray;
    }
    
    
    public JSONArray getSentMessages(String from,String to)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String query = "select date(message_date),message_receiver,message_sender,message from sent_messages where date(message_date) between '"+from+"' and '"+to+"' order by message_date desc ";
        System.out.println("getSentMessages==="+query);

        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            
            while (rs.next())
            {
                String messageDate = sdf.format(rs.getTimestamp(1));
                String messageReceiver = "0"+rs.getString(2).substring(3);
                String messageSender = rs.getString(3);
                String message = rs.getString(4);

                dataObj  = new JSONObject();
                dataObj.put("MessageDate", messageDate);
                dataObj.put("MessageReceiver", messageReceiver);
                dataObj.put("MessageSender", messageSender);
                dataObj.put("Message", message);
                dataArray.put(dataObj);
            }
            if(dataArray.length()==0)
            {
                dataObj  = new JSONObject();
                dataObj.put("MessageDate", "0");
                dataObj.put("MessageReceiver", "0");
                dataObj.put("MessageSender", "0");
                dataObj.put("Message", "0");
                dataArray.put(dataObj);
            }


        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getSentMessages=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }




    
    
}
