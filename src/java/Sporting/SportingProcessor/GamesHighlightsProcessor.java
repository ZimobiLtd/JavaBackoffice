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
import java.text.SimpleDateFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author jac
 */
public class GamesHighlightsProcessor {
    
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public GamesHighlightsProcessor()
    {
        
    }
    
    
    public JSONArray getgamesHighlits(String fromDate,String toDate)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String query = "";
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();
        query = "select  Torna_Sport_Name, Torna_Cat_Name, Torna_Name, Torna_Match_Event, Torna_Match_Event_Time, "
                     + "Torna_Match_ID, Torna_Sys_Game_ID, (case when Torna_Match_Status=0 then 'Active' when Torna_Match_Status=1 then 'Inactive' end), "
                     + "(case when Torna_Match_Special_Highlight=1 then 'Other Games' when Torna_Match_Special_Highlight=2 then 'Barner Highlights' when Torna_Match_Special_Highlight=0 then 'Page Highlights' end),"
                     + "Torna_Match_Special_Highlight,Jackpot_Status from tournament where date(Torna_Match_Event_Time) between '" + fromDate + "' and '" + toDate + "' and "
                     + "Torna_Match_Status = '0' and Torna_Match_Status='0' and Torna_Match_Stage !='Suspended' and Torna_Match_Stage !='Ended' "
                     + "and Torna_Match_Stage !='Deactivated' and Torna_Match_Event_Time >=now() order by Torna_Match_Highlight_Order desc";
        System.out.println("getgamesHighlits==="+query);

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
                String matchmode = rs.getString(9);
                String matchmodestatus = rs.getString(10);
                String jackpotMatchmodestatus = rs.getString(11);

                dataObj.put("TornamentMatchID", torna_match_id);
                dataObj.put("GameID", torna_sys_game_id);
                dataObj.put("Sport", sportname);
                dataObj.put("Country", countryname);
                dataObj.put("Tornament", torna_name);
                dataObj.put("Event", event);
                dataObj.put("EventTime", eventtime);
                dataObj.put("MatchStatus", matchstatus);
                dataObj.put("MatchMode", matchmode);
                dataObj.put("MatchModeStatus", matchmodestatus);
                dataObj.put("jackpotMatchmodestatus", jackpotMatchmodestatus);

                dataArray.put(dataObj);
            }

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getgamesHighlits=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
    
    
    
    
    public JSONArray filtergamesHighlits(String date)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String dataQuery = "";
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();
        
        dataQuery = "select  Torna_Sport_Name, Torna_Cat_Name, Torna_Name, Torna_Match_Event, Torna_Match_Event_Time, "
                     + "Torna_Match_ID, Torna_Sys_Game_ID, (case when Torna_Match_Status=0 then 'Active' when Torna_Match_Status=1 then 'Inactive' end), "
                     + "(case when Torna_Match_Special_Highlight=1 then 'Other Games' when Torna_Match_Special_Highlight=2 then 'Barner Highlights' when Torna_Match_Special_Highlight=0 then 'Page Highlights' end),"
                     + "Torna_Match_Special_Highlight,Jackpot_Status from tournament where date(Torna_Match_Event_Time)='" + date + "' and "
                     + "Torna_Match_Status = '0' and Torna_Match_Status='0' and Torna_Match_Stage !='Suspended' and Torna_Match_Stage !='Ended' "
                     + "and Torna_Match_Stage !='Deactivated' and Torna_Match_Event_Time >=now() order by Torna_Match_Highlight_Order desc";
        System.out.println("filtersgamesHighlits==="+dataQuery);

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(dataQuery);

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
                String matchmode = rs.getString(9);
                String matchmodestatus = rs.getString(10);
                String jackpotMatchmodestatus = rs.getString(11);

                dataObj.put("TornamentMatchID", torna_match_id);
                dataObj.put("GameID", torna_sys_game_id);
                dataObj.put("Sport", sportname);
                dataObj.put("Country", countryname);
                dataObj.put("Tornament", torna_name);
                dataObj.put("Event", event);
                dataObj.put("EventTime", eventtime);
                dataObj.put("MatchStatus", matchstatus);
                dataObj.put("MatchMode", matchmode);
                dataObj.put("MatchModeStatus", matchmodestatus);
                dataObj.put("jackpotMatchmodestatus", jackpotMatchmodestatus);

                dataArray.put(dataObj);
            }

            if(dataArray.length()==0)
            {
                dataObj  = new JSONObject();
                dataObj.put("TornamentMatchID", "0");
                dataObj.put("GameID", "0");
                dataObj.put("Country", "0");
                dataObj.put("Sport", "0");
                dataObj.put("Tornament", "0");
                dataObj.put("Event", "0");
                dataObj.put("EventTime", "0");
                dataObj.put("MatchStatus", "0");
                dataObj.put("MatchMode", "0");
                dataObj.put("MatchModeStatus", "0");
                 dataArray.put(dataObj);
            }

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error filtergamesHighlits=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }


    

    public JSONArray setHighlights(String matchId)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String query="";
        JSONObject dataObj  = new JSONObject();
        JSONArray dataArray = new JSONArray();
        query = " update tournament set Torna_Cat_Game_Mode ='2',Torna_Match_Special_Highlight=0,Torna_Match_Highlight_Order=1 where Torna_Match_ID ='" + matchId + "' limit 1 ";
        //System.out.println("setHighlights==="+query);
        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            int i=stmt.executeUpdate(query);
            if(i > 0)
            {
                dataObj.put("message", "highlight successful");
                dataArray.put(dataObj);
            }
            else
            {
                dataObj.put("error", "highlight failed");
                dataArray.put(dataObj);
            }
        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error filtergamesHighlits=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }


    
    
    
    public JSONArray setunHighlights(String matchId)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String query="";
        JSONObject dataObj  = new JSONObject();
        JSONArray dataArray = new JSONArray();
        query = " update tournament set Torna_Cat_Game_Mode ='1',Torna_Match_Special_Highlight=1,Torna_Match_Highlight_Order=0 where Torna_Match_ID ='" + matchId + "' limit 1 ";
        //System.out.println("setunHighlights==="+dataQuery);

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            int i=stmt.executeUpdate(query);
            if(i > 0)
            {
                dataObj.put("message", "unhighlight successful");
                dataArray.put(dataObj);
            }
            else
            {
                dataObj.put("error", "unhighlight failed");
                dataArray.put(dataObj);
            }
            

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error filtergamesHighlits=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }


    
    
    public JSONArray setBannerHighlights(String matchId)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String query="";
        JSONObject dataObj  = new JSONObject();
        JSONArray dataArray = new JSONArray();
        query = " update tournament set Torna_Match_Special_Highlight=2,Torna_Match_Highlight_Order=1 where Torna_Match_ID  ='" + matchId + "' limit 1 ";;
        //System.out.println("setBannerHighlights==="+query);
        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            int i=stmt.executeUpdate(query);
            if(i > 0)
            {
                dataObj.put("message", "highlight successful");
                dataArray.put(dataObj);
            }
            else
            {
                dataObj.put("error", "highlight failed");
                dataArray.put(dataObj);
            }
        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error setBannerHighlights=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
    
    
    
    
    public JSONArray setBannerunHighlights(String matchId)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String query="";
        JSONObject dataObj  = new JSONObject();
        JSONArray dataArray = new JSONArray();
        query = "update tournament set Torna_Match_Special_Highlight=1,Torna_Match_Highlight_Order=0 where Torna_Match_ID  ='" + matchId + "' limit 1";
        //System.out.println("setBannerunHighlights==="+query);
        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            int i=stmt.executeUpdate(query);
            if(i > 0)
            {
                dataObj.put("message", "Unhighlight successful");
                dataArray.put(dataObj);
            }
            else
            {
                dataObj.put("error", "Unhighlight failed");
                dataArray.put(dataObj);
            }
        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error setBannerunHighlights=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
    
    
    
    
    public JSONArray setHighlightJackpot(String matchId)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String query="";
        JSONObject dataObj  = new JSONObject();
        JSONArray dataArray = new JSONArray();
        query = " update tournament set Jackpot_Status =1,Torna_Match_Highlight_Order=1 where Torna_Match_ID ='" + matchId + "' limit 1 ";
        //System.out.println("setHighlightJackpot==="+query);
        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            int i=stmt.executeUpdate(query);
            if(i > 0)
            {
                dataObj.put("message", "highlight successful");
                dataArray.put(dataObj);
            }
            else
            {
                dataObj.put("error", "highlight failed");
                dataArray.put(dataObj);
            }
        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error setHighlightJackpot=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }


    
    
    
    public JSONArray setunHighlightJackpot(String matchId)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String query="";
        JSONObject dataObj  = new JSONObject();
        JSONArray dataArray = new JSONArray();
        query = " update tournament set Jackpot_Status=0,Torna_Match_Highlight_Order=0 where Torna_Match_ID ='" + matchId + "' limit 1 ";
        //System.out.println("setHighlightJackpot==="+dataQuery);

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            int i=stmt.executeUpdate(query);
            if(i > 0)
            {
                dataObj.put("message", "unhighlight successful");
                dataArray.put(dataObj);
            }
            else
            {
                dataObj.put("error", "unhighlight failed");
                dataArray.put(dataObj);
            }
            

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error setHighlightJackpot=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }

    
}
