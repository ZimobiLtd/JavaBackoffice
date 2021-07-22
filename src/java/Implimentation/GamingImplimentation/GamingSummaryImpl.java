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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author jac
 */
public class GamingSummaryImpl {
    
    public GamingSummaryImpl()
    {
        
    }
    
    
    public JSONArray getGamingSummary()
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            
            String reg_today = "select count(id) from player where DATE(registration_date) = curdate() ";

            String reg_played_today = "select count(Play_Bet_Mobile) from player_bets,player where  Play_Bet_Mobile= msisdn  and DATE(registration_date) = curdate() ";

            String playerstoday = "select count(distinct(Play_Bet_Mobile)) from player_bets where DATE(Play_Bet_Timestamp) = curdate() ";

            String turnovertoday = "select ifnull(sum(Play_Bet_Stake),0) from player_bets where DATE(Play_Bet_Timestamp) = curdate() and Play_Bet_Status in (200,201,202,203) ";

            String turnoverweekly = "select ifnull(sum(Play_Bet_Stake),0) from player_bets where Play_Bet_Timestamp between  DATE_SUB(curdate(),INTERVAL 7 DAY) and curdate() "
                                    + "and Play_Bet_Status in (200,201,202,203) ";

            String turnovermonthly = "select ifnull(sum(Play_Bet_Stake),0) from player_bets where Play_Bet_Timestamp between  DATE_SUB(curdate(),INTERVAL 30 DAY) and curdate() "
                                    + "and Play_Bet_Status in (200,201,202,203)";

            String revenuetoday = "SELECT ifnull(sum(totals),0) FROM (SELECT SUM(Play_Bet_Stake) totals FROM player_bets where " +
                                    "DATE(Play_Bet_Timestamp) = curdate() and Play_Bet_Status in (200,201,202,203) union all " +
                                    "SELECT SUM(-Play_Bet_Possible_Winning) totals FROM player_bets where " +
                                    "DATE(Play_Bet_Timestamp) = curdate() and Play_Bet_Status='202') a ";

            String revenueweekly = "SELECT ifnull(sum(totals),0) FROM (SELECT SUM(Play_Bet_Stake) totals FROM player_bets where " +
                                    "DATE(Play_Bet_Timestamp) = curdate() and Play_Bet_Status in (200,201,202,203) union all " +
                                    "SELECT SUM(-Play_Bet_Possible_Winning) totals FROM player_bets where " +
                                    "DATE(Play_Bet_Timestamp)  between  DATE_SUB(curdate(),INTERVAL 7 DAY) and curdate() and Play_Bet_Status='202') a ";        

            String revenuemonthly = "SELECT ifnull(sum(totals),0) FROM (SELECT SUM(Play_Bet_Stake) totals FROM player_bets where " +
                                    "DATE(Play_Bet_Timestamp) = curdate() and Play_Bet_Status in (200,201,202,203) union all " +
                                    "SELECT SUM(-Play_Bet_Possible_Winning) totals FROM player_bets where " +
                                    "DATE(Play_Bet_Timestamp)  between  DATE_SUB(curdate(),INTERVAL 30 DAY) and curdate() and Play_Bet_Status='202') a ";

            String betstoday = "select count(Play_Bet_ID) from player_bets where DATE(Play_Bet_Timestamp) = curdate() and Play_Bet_Status = '201' ";

            String settledbetstoday = "select count(Play_Bet_ID) from player_bets where DATE(Play_Bet_Timestamp) = curdate() and Play_Bet_Status in (202, 203) ";

            String wonbetstoday = "select count(Play_Bet_ID) from player_bets where DATE(Play_Bet_Timestamp) = curdate() and Play_Bet_Status in (202) ";


            dataObj  = new JSONObject();

            int registrations_today =getgamingSummaryData(conn,stmt,rs,reg_today);
            int registered_played_today = getgamingSummaryData(conn,stmt,rs,reg_played_today);
            int players_today = getgamingSummaryData(conn,stmt,rs,playerstoday);
            int turnover_today = getgamingSummaryData(conn,stmt,rs,turnovertoday);
            int turnover_weekly = getgamingSummaryData(conn,stmt,rs,turnoverweekly);
            int turnover_monthly = getgamingSummaryData(conn,stmt,rs,turnovermonthly);
            int revenue_today = getgamingSummaryData(conn,stmt,rs,revenuetoday);
            int revenue_weekly = getgamingSummaryData(conn,stmt,rs,revenueweekly);
            int revenue_monthly = getgamingSummaryData(conn,stmt,rs,revenuemonthly);
            int bets_today = getgamingSummaryData(conn,stmt,rs,betstoday);
            int settledbets_today = getgamingSummaryData(conn,stmt,rs,settledbetstoday);
            int wonbets_today = getgamingSummaryData(conn,stmt,rs,wonbetstoday);

            dataObj.put("RegistrationsToday", registrations_today);
            dataObj.put("Registered_Payed_Today", registered_played_today);
            dataObj.put("PlayersToday", players_today);
            dataObj.put("TurnOverToday", revenue_today);
            dataObj.put("TurnoverWeekly", revenue_weekly);
            dataObj.put("TurnoverMonthly", revenue_monthly);
            dataObj.put("BetStakeToday", turnover_today);
            dataObj.put("BetStakeWeekly", turnover_weekly);
            dataObj.put("BetStakeMonthly", turnover_monthly);
            dataObj.put("PendingBets", bets_today);
            dataObj.put("SettledBetsToday", settledbets_today);
            dataObj.put("WonBetsToday", wonbets_today);

            dataArray.put(dataObj);

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getGamingSummary=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
    
    
    // return record count in a result set
    public int getgamingSummaryData( Connection conn,Statement stmt,ResultSet rs, String query) 
    {
        int count = 0;
        try 
        {
            Statement stmtCount = (Statement) conn.createStatement();
            rs = (ResultSet) stmtCount.executeQuery(query);

            while (rs.next()) 
            {
                count = rs.getInt(1);
            }

        }
        catch (SQLException ex) 
        {
            System.out.println("Error getGamingSummary=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(null,null,rs,null);
        }

        return count;
    } 
    
}
