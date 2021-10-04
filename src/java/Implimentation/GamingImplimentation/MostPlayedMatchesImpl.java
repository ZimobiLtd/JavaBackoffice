/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Implimentation.GamingImplimentation;

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
public class MostPlayedMatchesImpl {
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    public MostPlayedMatchesImpl()
    {
        
    }
    
    // most played 
    public JSONArray getMostPlayed(String dateFrom,String dateTo)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String dataQuery = "",settledQuery="",placedQuery="";
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            dataQuery = "select distinct(Mul_Match_ID), Mul_EventTime, Mul_Sportname, Mul_Tournament, Mul_Event,count(Mul_Match_ID) as 'bets on game',ifnull(Mul_Finished_Timestamp,'0') "
                    + "from multibets  where  date(Mul_EventTime) between '"+dateFrom+"' and '"+dateTo+"' group by Mul_Match_ID order by count(Mul_Match_ID) desc";
            System.out.println("getMostPlayed==="+dataQuery);
            
            conn = DBManager.getInstance().getDBConnection("read");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(dataQuery);

            while (rs.next())
            {
                dataObj  = new JSONObject();

                String matchid = rs.getString(1);
                String eventdate = sdf.format(rs.getTimestamp(2));
                String sport = rs.getString(3);
                String tornament = rs.getString(4);
                String event = rs.getString(5);
                String totalbets = rs.getString(6);
                String betsettleddate = rs.getString(7);

                dataObj.put("MatchID", matchid);
                dataObj.put("EventDate", eventdate);
                dataObj.put("BetsSettledDate", betsettleddate);
                dataObj.put("Sport", sport);
                dataObj.put("Tornament", tornament);
                dataObj.put("Event", event);
                dataObj.put("TotalBets", totalbets);

                dataArray.put(dataObj);
            }

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error filterMostPlayed=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }
    return dataArray;
    }



    // return record count in a result set
    public int getMostPlayedCount( Connection conn,Statement stmt,String query) 
    {
        int count = 0;
        ResultSet rs=null;    
        try 
        {
            rs =stmt.executeQuery(query);
            while (rs.next()) 
            {
                count = rs.getInt(1);
            }
        }
        catch (SQLException ex) 
        {
            System.out.println("Error getMostPlayedCount=== "+ex.getMessage());
        }
        /*finally
        {
            new Utility().doFinally(null,null,null,null);
        }*/
        return count;
    } 
    
}
