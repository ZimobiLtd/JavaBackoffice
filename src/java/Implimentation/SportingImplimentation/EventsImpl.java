/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Implimentation.SportingImplimentation;

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
public class EventsImpl {
    
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    public EventsImpl()
    {
        
    }
    
    
    public JSONArray getEvents(String fromDate,String toDate)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String query = "";
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();
        query = "select  Torna_Sport_Name, Torna_Cat_Name, Torna_Name, Torna_Match_Event, Torna_Match_Event_Time,  "
                    + "Torna_Match_ID, Torna_Sys_Game_ID, (case when Torna_Match_Status=0 then 'Active' when Torna_Match_Status=1 then 'Inactive' end),  "
                    + "(select Odd_Mark_Odd from the_odds where Odd_Mark_Match_ID = Torna_Match_ID  "
                    + "and Odd_Mark_Desc='Team1' and Odd_Market_ID=1 limit 1) as 'team1', "
                    + "(select Odd_Mark_Odd from the_odds where Odd_Mark_Match_ID = Torna_Match_ID  "
                    + "and Odd_Mark_Desc='draw' and Odd_Market_ID=1 limit 1) as 'draw', "
                    + "(select Odd_Mark_Odd from the_odds where Odd_Mark_Match_ID = Torna_Match_ID  "
                    + "and Odd_Mark_Desc='Team2' and Odd_Market_ID=1 limit 1) as 'team2', "
                    + "(select Odd_Mark_Odd from the_odds where Odd_Mark_Match_ID = Torna_Match_ID  "
                    + "and Odd_Mark_Desc='over 2.5' and Odd_Market_ID=18 limit 1) as 'ov25', "
                    + "(select Odd_Mark_Odd from the_odds where Odd_Mark_Match_ID = Torna_Match_ID  "
                    + "and Odd_Mark_Desc='under 2.5' and Odd_Market_ID=18 limit 1) as 'un25', "
                    + "(select Odd_Mark_Odd from the_odds where Odd_Mark_Match_ID = Torna_Match_ID  "
                    + "and Odd_Mark_Desc='yes' and Odd_Market_ID=29 limit 1) as 'gg', "
                    + "(select Odd_Mark_Odd from the_odds where Odd_Mark_Match_ID = Torna_Match_ID  "
                    + "and Odd_Mark_Desc='no' and Odd_Market_ID=29 limit 1) as 'ng' from tournament "
                    + "where date(Torna_Match_Event_Time) between '" + fromDate + "' and '" + toDate + "' and Torna_Match_Status = 0 and "
                    + "(select count(Odd_Mark_Table_ID) from the_odds where Odd_Mark_Match_ID = Torna_Match_ID ) > 0 and "
                    + "Torna_Match_ID in (select Odd_Mark_Match_ID from the_odds where Odd_Mark_Status='Active' and Odd_Market_ID=1 and Odd_Mark_Match_ID = Torna_Match_ID)"
                    + "order by Torna_Match_Event_Time desc";
        System.out.println("getevents==="+query);

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next())
            {
                dataObj  = new JSONObject();

                String sportname = rs.getString(1);
                String countryname = rs.getString(2);
                String torna_name = rs.getString(3);
                String event = rs.getString(4);
                String eventtime =sdf.format(rs.getTimestamp(5));
                String torna_match_id = rs.getString(6);
                String torna_sys_game_id = rs.getString(7);
                String matchstatus = rs.getString(8);
                String odd_hometeam = rs.getString(9);
                String odd_draw = rs.getString(10);
                String odd_awayteam = rs.getString(11);
                String odd_ov25 = rs.getString(12);
                String odd_un25 = rs.getString(13);
                String odd_gg = rs.getString(14);
                String odd_ng = rs.getString(15);

                dataObj.put("TornamentMatchID", torna_match_id);
                dataObj.put("GameID", torna_sys_game_id);
                dataObj.put("Country", countryname);
                dataObj.put("Sport", sportname);
                dataObj.put("Tornament", torna_name);
                dataObj.put("Event", event);
                dataObj.put("EventTime", eventtime);
                dataObj.put("MatchStatus", matchstatus);
                dataObj.put("HomeTeamOdd", odd_hometeam);
                dataObj.put("DrawOdd", odd_draw);
                dataObj.put("AwayTeamOdd", odd_awayteam);
                dataObj.put("Ov25Odd", odd_ov25);
                dataObj.put("Un25", odd_un25);
                dataObj.put("GGOdd", odd_gg);
                dataObj.put("NGOdd", odd_ng);

                dataArray.put(dataObj);
            }

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getEvents=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
     
    
    
    
    public JSONArray filterEvents(String fromDate,String toDate,String filters)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String query = "";
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();
        query = "select  Torna_Sport_Name, Torna_Cat_Name, Torna_Name, Torna_Match_Event, Torna_Match_Event_Time,  "
                    + "Torna_Match_ID, Torna_Sys_Game_ID, Torna_Match_Status,  "
                    + "(select Odd_Mark_Odd from the_odds where Odd_Mark_Match_ID = Torna_Match_ID  "
                    + "and Odd_Mark_Desc='Team1' and Odd_Market_ID=1 limit 1) as 'team1', "
                    + "(select Odd_Mark_Odd from the_odds where Odd_Mark_Match_ID = Torna_Match_ID  "
                    + "and Odd_Mark_Desc='draw' and Odd_Market_ID=1 limit 1) as 'draw', "
                    + "(select Odd_Mark_Odd from the_odds where Odd_Mark_Match_ID = Torna_Match_ID  "
                    + "and Odd_Mark_Desc='Team2' and Odd_Market_ID=1 limit 1) as 'team2', "
                    + "(select Odd_Mark_Odd from the_odds where Odd_Mark_Match_ID = Torna_Match_ID  "
                    + "and Odd_Mark_Desc='over 2.5' and Odd_Market_ID=18 limit 1) as 'ov25', "
                    + "(select Odd_Mark_Odd from the_odds where Odd_Mark_Match_ID = Torna_Match_ID  "
                    + "and Odd_Mark_Desc='under 2.5' and Odd_Market_ID=18 limit 1) as 'un25', "
                    + "(select Odd_Mark_Odd from the_odds where Odd_Mark_Match_ID = Torna_Match_ID  "
                    + "and Odd_Mark_Desc='yes' and Odd_Market_ID=29 limit 1) as 'gg', "
                    + "(select Odd_Mark_Odd from the_odds where Odd_Mark_Match_ID = Torna_Match_ID  "
                    + "and Odd_Mark_Desc='no' and Odd_Market_ID=29 limit 1) as 'ng' from tournament "
                    + "where Torna_Match_Event_Time between '" + fromDate + "' and '" + toDate + "' and Torna_Match_Status = 0 and "
                    + "Torna_Match_ID in (select Odd_Mark_Match_ID from the_odds where Odd_Mark_Status='Active' and Odd_Market_ID=1 and Odd_Mark_Match_ID = Torna_Match_ID) and "
                    + "(select count(Odd_Mark_Table_ID) from the_odds where Odd_Mark_Match_ID = Torna_Match_ID ) > 0  "+filters+" "
                    + "order by Torna_Match_Event_Time desc";

            System.out.println("filterEvents==="+query);

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next())
            {
                dataObj  = new JSONObject();

                String sportname = rs.getString(1);
                String countryname = rs.getString(2);
                String torna_name = rs.getString(3);
                String event = rs.getString(4);
                String eventtime = sdf.format(rs.getTimestamp(5));
                String torna_match_id = rs.getString(6);
                String torna_sys_game_id = rs.getString(7);
                String matchstatus = rs.getString(8);
                String odd_hometeam = rs.getString(9);
                String odd_draw = rs.getString(10);
                String odd_awayteam = rs.getString(11);
                String odd_ov25 = rs.getString(12);
                String odd_un25 = rs.getString(13);
                String odd_gg = rs.getString(14);
                String odd_ng = rs.getString(15);

                dataObj.put("TornamentMatchID", torna_match_id);
                dataObj.put("GameID", torna_sys_game_id);
                dataObj.put("Sport", sportname);
                dataObj.put("Country", countryname);
                dataObj.put("Tornament", torna_name);
                dataObj.put("Event", event);
                dataObj.put("EventTime", eventtime);
                dataObj.put("MatchStatus", matchstatus);
                dataObj.put("HomeTeamOdd", odd_hometeam);
                dataObj.put("DrawOdd", odd_draw);
                dataObj.put("AwayTeamOdd", odd_awayteam);
                dataObj.put("Ov25Odd", odd_ov25);
                dataObj.put("Un25", odd_un25);
                dataObj.put("GGOdd", odd_gg);
                dataObj.put("NGOdd", odd_ng);

                dataArray.put(dataObj);
            }

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error filterEvents=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }






    public JSONArray getEventOdds(String matchId)
    {

        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();
        String query = "select Odd_Mark_Table_ID, Odd_Mark_ID, Odd_Mark_Desc, Odd_Mark_Odd from the_odds where Odd_Mark_Match_ID = '" + matchId + "'";            
        System.out.println("getEventOdds==="+query);

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next())
            {
                dataObj  = new JSONObject();

                String id = rs.getString(1);
                String parentmarket = rs.getString(2);
                String market = rs.getString(3);
                String odd = rs.getString(4);

                dataObj.put("ID", id);
                dataObj.put("ParentMarket", parentmarket);
                dataObj.put("Market", market);
                dataObj.put("Odd", odd);

                dataArray.put(dataObj);
            }


        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getEventOdds=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }





    public JSONArray getTourrnaments()
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String query;
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            query = "select  group_concat( distinct Torna_Name  separator '#') from tournament ";
            System.out.println("getTornamets==="+query);
            
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next())
            {
                dataObj  = new JSONObject();

                String tornaments = rs.getString(1);
                dataObj.put("Tornaments", tornaments);

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
