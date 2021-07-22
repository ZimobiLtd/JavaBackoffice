/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Implimentation.UtilityImplimentation;

import Database.DBManager;
import Utility.Utility;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author jac
 */
public class MatchDetails {
    
    
    public MatchDetails()
    {
        
    }
    
    
    
    public JSONArray getMatchDetails(String matchID)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String res="";
        String query="select Torna_Name,Torna_Sport_Name,Torna_Match_ID,Torna_Match_Event,Torna_Match_Event_Time,Torna_Sys_Game_ID from tournament where  Torna_Match_ID in ("+matchID+") ";
        System.out.println("getMatchDetails==="+query);

        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next())
            {
                dataObj  = new JSONObject();
                String tornaName = rs.getString(1);
                String tornaSportName = rs.getString(2);
                String tornaMatchID = rs.getString(3);
                String tornaEvent = rs.getString(4);
                String event_date="";
                String val = rs.getString(5);
                if(val.equals("no event time"))
                {
                    event_date="no event time";
                }
                else
                {
                    event_date =val.substring(0,val.length() -3);
                }
                String tornaGameID = rs.getString(6);

                dataObj.put("Match ID", tornaMatchID);
                dataObj.put("Match Type", tornaSportName);
                dataObj.put("Event Date", event_date);
                dataObj.put("Event", tornaEvent);
                dataObj.put("Game ID", tornaGameID);
                dataArray.put(dataObj);
            }

            if(dataArray.length()==0)
            {
                dataObj  = new JSONObject();
                dataObj.put("Message", "Match was not found");
                dataArray.put(dataObj);
            }
        }
        catch(SQLException | JSONException ex)
        {
            System.out.println("Error getMatchDetails=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
    
    
    public JSONArray removeGameByMatchID(String matchID)
    {
        JSONObject dataObj  = new JSONObject();
        JSONArray dataArr  = new JSONArray();
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String data="";
        String query="delete from tournament where Torna_Match_ID in ("+matchID+") limit 1";

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            int i = stmt.executeUpdate(query);
            if(i > 0)
            {
                dataObj.put("Message", "Game has been removed successfully");
                dataArr.put(dataObj);
            }
            else
            {
                dataObj.put("Message", "Failed to remove the game");
                dataArr.put(dataObj);
            }

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error removeGameByMatchID=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArr;
    }
    
    
}
