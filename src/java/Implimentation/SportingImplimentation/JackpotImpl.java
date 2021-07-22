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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author jac
 */
public class JackpotImpl {
    
    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    public JackpotImpl()
    {
        
    }
    
    
    
    public JSONArray getJackports()
    {
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();
        
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String query = "select id,display_name,datecreated,start_date,end_date,stake_amount,amount,"
                + "(case when jp_status=1 then 'Active' when jp_status=2 then 'Expired' when jp_status=3 then 'Inactive' when jp_status=4 then 'Closed' end) as  'jp status',"
                + "(case when jp_result_status=1 then 'Open' when jp_status=2 then 'Closed'end) as 'result status',jp_total_games,jp_winners,jp_all_winners,jp_total_resulted from jackpot order by datecreated desc";
        
        System.out.println("getJackports==="+query);

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next())
            {
                dataObj  = new JSONObject();
                String jpID = rs.getString(1);
                String jpName = rs.getString(2);
                String dateCreated = sdf.format(rs.getTimestamp(3));
                String startDate = sdf.format(rs.getTimestamp(4));
                String endDate = sdf.format(rs.getTimestamp(5));
                String jbStake = rs.getString(6);
                String jpWinAmount = rs.getString(7);
                String jpStatus = rs.getString(8);
                String jpResultStatus = rs.getString(9);
                String jpTotalGames = rs.getString(10);
                String jpTotalWinners = rs.getString(11);
                String jpAllWinners = rs.getString(12);
                String jpTotalGamesResulted = rs.getString(13);
               
               
                dataObj.put("JackpotID", jpID);
                dataObj.put("JackpotName", jpName);
                dataObj.put("DateCreated", dateCreated);
                dataObj.put("StartDate", startDate);
                dataObj.put("EndDate", endDate);
                dataObj.put("JackpotStake", jbStake);
                dataObj.put("JackpotWinAmount", jpWinAmount);
                dataObj.put("JackpotStatus", jpStatus);
                dataObj.put("JackpotResultStatus", jpResultStatus);
                dataObj.put("JackpotTotalGames", jpTotalGames);
                dataObj.put("JackpotTotalWinners", jpTotalWinners);
                dataObj.put("JackpotAllWinners", jpAllWinners);
                dataObj.put("JackpotGamesResulted", jpTotalGamesResulted);

                dataArray.put(dataObj);
            }

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getJackports=== "+ex.getMessage());
        } 
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
    
    
    public String getJackpotMatchIDs(String jackpotID)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String data="";
        String query="select group_concat(Torna_Match_ID separator ',') from tournament where JackPot_Ref_No = " + jackpotID + " ";

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            
            while (rs.next()) 
            {
                data = rs.getString(1);
            }
        } 
        catch (SQLException ex) 
        {
            System.out.println("Error getJackpotMatchIDs=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return data;
    }
    
    
    
    public JSONArray getJackpotGames(String mulMatchID)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String query="select Torna_Sys_Game_ID,Torna_Match_ID,ifnull( Torna_Sport_Name,'no sport'), ifnull( Torna_Match_Event_Time,'no event time'), "
                + "ifnull(Torna_Match_Event,'no event'), '1x2',(select ifnull(Mul_Bet_Winning_Pred,'0') from multibets where Mul_Match_ID = Torna_Match_ID "
                + "group by Mul_Match_ID limit 1) as 'outcome' from tournament where Torna_Match_ID in ("+mulMatchID+")  and Torna_Match_ID = Torna_Match_ID group by Torna_Match_ID limit 10 ";
        System.out.println("getJackpotGames==="+query);

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
                String game_id = rs.getString(1);
                String match_id = rs.getString(2);
                String match_type = rs.getString(3);
                String event_date="";
                String val = rs.getString(4);
                if(val.equals("no event time"))
                {
                    event_date="no event time";
                }
                else
                {
                    event_date =val.substring(0,val.length() -3);
                }
                String event = rs.getString(5);
                String bet_market_id = rs.getString(6);
                String outcome = rs.getString(7);
                if(outcome == null)
                {
                    outcome="0";
                }

                dataObj.put("ID", game_id);
                dataObj.put("Match_ID", match_id);
                dataObj.put("Match_Type", match_type);
                dataObj.put("Event_Date", event_date);
                dataObj.put("Event", event);
                dataObj.put("Bet_Market_ID", bet_market_id);
                dataObj.put("Bet_Market_ID", bet_market_id);
                dataObj.put("Outcome", outcome);
                dataArray.put(dataObj);
            }

            if(dataArray.length()==0)
            {
                dataObj  = new JSONObject();
                dataObj.put("ID", "0");
                dataObj.put("Match_ID", "0");
                dataObj.put("Match_Type", "0");
                dataObj.put("Event_Date", "0");
                dataObj.put("Event", "0");
                dataObj.put("Bet_Market_ID", "0");
                dataObj.put("Outcome", "0");
                dataArray.put(dataObj);
            }

        }
        catch(SQLException | JSONException ex)
        {
            System.out.println("Error getJackpotGames== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
    
    
    
    public JSONArray getDeleteJackpot(String jackpotID)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        int status=500;
        JSONObject dataObj = new JSONObject();
        JSONArray dataArray = new JSONArray();
        String query="delete from jackpot where id =" + jackpotID + " ";
        System.out.println("getDeleteJackpot==="+query);

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            
            int i = stmt.executeUpdate(query);
            if(i > 0)
            {
                dataObj.put("message", "Request successful");
                dataArray.put(dataObj);
            }
            else
            {
                dataObj.put("error", "Request failed");
                dataArray.put(dataObj);
            }
        } 
        catch (SQLException | JSONException ex ) 
        {
            System.out.println("Error getDeleteJackpot=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
    
    
    
    public JSONArray setJackpotWinners(String jackpotID,String winners)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        JSONObject dataObj = new JSONObject();
        JSONArray dataArray = new JSONArray();
        String query="update  jackpot set jp_winners ="+winners+" where id = " + jackpotID + " ";

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            
            int i = stmt.executeUpdate(query);
            if(i > 0)
            {
                dataObj.put("message", "Request successful");
                dataArray.put(dataObj);
            }
            else
            {
                dataObj.put("error", "Request failed");
                dataArray.put(dataObj);
            }
        } 
        catch (SQLException | JSONException ex ) 
        {
            System.out.println("Error setJackpotWinners=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
    
    
    
    public JSONArray getJackpotWinners(int jackpotID)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String query="";
        
        if(jackpotID == 0)
        {
            query="select Jackpot_Ref_No,Jackpot_Winner,Jackpot_Betslip_ID,Jackpot_BetGroupID from jackpot_winners order by Jackpot_Ref_No desc";
        }
        else
        {
            query="select Jackpot_Ref_No,Jackpot_Winner,Jackpot_Betslip_ID from jackpot_winners where Jackpot_Ref_No="+jackpotID+" order by Jackpot_Ref_No desc";
        }
        
        System.out.println("getJackpotWinners==="+query);

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
                String jpRefNo = rs.getString(1);
                String winnerMobile = "0"+rs.getString(2).substring(3);
                String jpBetslipID = rs.getString(3);
                String jpBetBetGroupID = rs.getString(4);
                
                dataObj.put("JackpotID", jpRefNo);
                dataObj.put("JackpotWinnerMobile", winnerMobile);
                dataObj.put("JackpotBetSlipID", jpBetslipID);
                dataObj.put("JackpotBetGroupID", jpBetBetGroupID);
                dataArray.put(dataObj);
            }

            if(dataArray.length()==0)
            {
                dataObj  = new JSONObject();
                dataObj.put("JackpotID", "0");
                dataObj.put("JackpotWinnerMobile", "0");
                dataObj.put("JackpotBetSlipID", "0");
                dataObj.put("JackpotBetGroupID", "0");
                dataArray.put(dataObj);
            }

        }
        catch(SQLException | JSONException ex)
        {
            System.out.println("Error getJackpotWinners== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
    
    
}
