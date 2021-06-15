/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utility;

import Database.DBManager;
import static Players.PlayersAPIs.BettingReportAPI.sdf;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author jac
 */
public class TestClass 
{
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public TestClass()
    {
        
    }
    
    public static void main(String []args)
    {
        //JSONArray dataArray=new TestClass().getTornaments("0");
        //System.out.println(dataArray);
        String matchIDs=new TestClass().getUnresolvedBetsMatchIDs("2021-06-10");
        System.out.println("matchIDs==="+matchIDs);
        String[] arr=matchIDs.split(",");
        System.out.println("Arr Length=== "+arr.length);
        
        /*for(int i=0;i<arr.length;i++)
        {
            String matchID=arr[i];
            //new TestClass().resolve1X2Games(matchID) ;
        }*/
    }
    
    
    public String getUnresolvedBetsMatchIDs(String date)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        StringBuilder sb = new StringBuilder();
        String respo="0";
        String dataQuery = "select distinct(Mul_Match_ID) from player_bets,multibets where Mul_Group_ID=Play_Bet_Group_ID and date(Play_Bet_Timestamp) ='"+date+"' "
                + "and Play_Bet_Status in(201,202,203) group by Play_Bet_ID ";

        System.out.println("getUnresolvedBetsMatchIDs==="+dataQuery);

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(dataQuery);

            while (rs.next())
            {
                String matchID = ","+rs.getString(1);
                sb.append(matchID);
            }
            
            respo=sb.toString().substring(1);

        }
        catch (SQLException ex) 
        {
            System.out.println("Error getTornaments=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return respo;
    }
    
    
    
    

    
    
    
    public JSONArray getTornaments(String matchID)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String dataQuery = "select Torna_ID,Torna_Name,Torna_Sport_Name,Torna_Match_Event_Time,Torna_Sys_Game_ID,Torna_Match_ID,Torna_Match_Event, "
                + "group_concat(Odd_Mark_ID,'#',Odd_Market_ID,'#',Odd_Mark_Odd,'#',Odd_Mark_Status,'#',Odd_Change_Status,'#',Odd_Mark_Desc separator '&') as 'odds' from tournament, the_odds "
                + "where  Odd_Mark_Match_ID=Torna_Match_ID AND date(Torna_Match_Event_Time)='2021-06-03' AND (Torna_Match_Special_Highlight = '0' OR Torna_Match_Special_Highlight ='2') "
                + "AND Torna_Match_Event_Time > now() AND Torna_Live_Status!='Live' AND Torna_Sport_ID ='1' group by Torna_Match_ID  limit 1";

        System.out.println("getTornaments==="+dataQuery);

        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(dataQuery);

            while (rs.next())
            {
                String torna_id = rs.getString(1);
                String torna_name = rs.getString(2);
                String torna_sportname = rs.getString(3);
                String torna_match_event_time = rs.getString(4);
                String tornal_sys_gameID = rs.getString(5);
                String torna_match_id = rs.getString(6);
                String torna_match_event = rs.getString(7);
                String odds = rs.getString(8);

                dataObj  = new JSONObject();
                dataObj.put("Torna_ID", torna_id);
                dataObj.put("Torna_Name", torna_name);
                dataObj.put("Torna_Sport_Name", torna_sportname);
                dataObj.put("Torna_Event_Time",  torna_match_event_time);
                dataObj.put("Torna_Sys_Game_ID", tornal_sys_gameID);
                dataObj.put("Torna_Match_ID", torna_match_id);
                dataObj.put("Torna_Match_Event", torna_match_event);
                dataObj.put("Odds", odds);
                dataArray.put(dataObj);
            }

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getTornaments=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
    
}
