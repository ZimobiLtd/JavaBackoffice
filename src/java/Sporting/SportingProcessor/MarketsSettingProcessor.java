/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Sporting.SportingProcessor;

import Database.DBManager;
import Utility.Utility;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author jac
 */
public class MarketsSettingProcessor {
    
    
    public MarketsSettingProcessor()
    {
        
    }
    
    
    public JSONArray getMarkets()
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String query = "select Mar_Code,Mar_Name from markets";
        System.out.println("getgetMarkets==="+query);

        JSONObject dataObj  = null;
        JSONArray dataArray1 = new JSONArray();
        JSONArray dataArray2 = new JSONArray();
        JSONArray respoArray = new JSONArray();

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next())
            {
               String code = rs.getString(1);
               String name = rs.getString(2);

               dataObj  = new JSONObject();
               dataObj.put("MarketCode", code);
               dataObj.put("MarketName", name);
               dataArray1.put(dataObj);                   
            }
            
            respoArray.put(dataArray1);



            query = "select Sport_Code,Sport_Name from sports";
            System.out.println("getgetSports==="+query);
            rs = stmt.executeQuery(query);
            while (rs.next())
            {
               String sport_code = rs.getString(1);
               String sport_name = rs.getString(2);

               dataObj  = new JSONObject();
               dataObj.put("SportCode", sport_code);
               dataObj.put("SportName", sport_name);
               dataArray2.put(dataObj);
            }
            
            respoArray.put(dataArray2);

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getMarkets=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return respoArray;
    }
    
    
    public int saveActiveMarkets(String market_id, String market_name, String sport_id) 
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        int respo_id=0; 
        String query = "INSERT INTO Active_markets (Mar_Name,Mar_ID,Sport_ID) VALUES(?,?,?)";

        java.sql.Timestamp timestamp = new java.sql.Timestamp(new Date().getTime());  
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();
        
        try
        {
            conn = new DBManager().getDBConnection();
            ps = conn.prepareStatement(query);

            ps.setInt(1,Integer.valueOf(market_id) );
            ps.setString(2,market_name ); 
            ps.setInt(3,Integer.valueOf(sport_id) ); 
            respo_id=ps.executeUpdate();

        }
        catch (SQLException ex) 
        {
            System.out.println("Error saveActiveMarkets=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }
    return respo_id;    
    }




    public JSONArray getActiveMarkets()
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String query = "select Mar_ID,Mar_Name,Sport_Name from Active_markets inner join sports on sports.Sport_Code=Active_markets.Sport_ID";
        System.out.println("getgetMarkets==="+query);

        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next())
            {
               String market_id = rs.getString(1);
               String market_name = rs.getString(2);
               String sport_name = rs.getString(3);

               dataObj  = new JSONObject();
               dataObj.put("MarketCode", market_id);
               dataObj.put("MarketName", market_name);
               dataObj.put("SportName", sport_name);
               dataArray.put(dataObj);                   
            }

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getActiveMarkets=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }




    public int removeActiveMarkets(String market_id,String sport_id) 
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        int status=0; 
        String query = "delete from Active_markets where Mar_ID="+market_id+" and Sport_ID="+sport_id+" ";

        java.sql.Timestamp timestamp = new java.sql.Timestamp(new Date().getTime());  
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();
        
        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            
            int i=stmt.executeUpdate(query);
            if(i > 0)
            {
                status=1;
            }
            else
            {
               status=0; 
            }
            
        }
        catch (SQLException ex) 
        {
            System.out.println("Error removeActiveMarkets=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }
    return status;    
    }
    
    
}
