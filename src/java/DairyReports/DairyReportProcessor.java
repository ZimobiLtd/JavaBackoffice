/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DairyReports;

import static DairyReports.DailyReportAPI.sdf;
import Database.DBManager;
import Utility.Utility;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author jac
 */
public class DairyReportProcessor {
    
    public DairyReportProcessor()
    {
        
    }
    
    
    public JSONArray getDailyReport(String datefrom,String dateto)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String dataQuery = "";
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            
            for (String date : new Utility().getDatesList(datefrom ,dateto)) 
            {
                dataObj  = new JSONObject();
                //System.out.println("date==="+date);
                dataQuery = "select ifnull(sum(Acc_Amount),0) as 'deposits', " +
                "(select ifnull(sum(Acc_Amount),0) FROM user_accounts WHERE Acc_Trans_Type =2 and DATE(Acc_Date) = '"+date+"') as'withdrawals' ," +
                "(select  ifnull(sum(Play_Bet_Stake),0) as 'betstakes' from player_bets where DATE(Play_Bet_Timestamp)= '"+date+"' and Play_Bet_Status in (201,202,203)) as 'bet stake'," +
                "(select  ifnull(sum(Play_Bet_Bonus_Stake),0) from player_bets where DATE(Play_Bet_Timestamp)= '"+date+"' and Play_Bet_Status in (201,202,203)) as 'bet bonus stake'," +
                "(select ifnull(sum(Acc_Bonus_Amount),0) from user_accounts where Acc_Trans_Type=7 and DATE(Acc_Date) ='"+date+"' )as 'acc bonus amount'," +
                "(select count(Play_Bet_ID) from player_bets where DATE(Play_Bet_Timestamp)= '"+date+"' and Play_Bet_Status  in (201,202,203)) as 'player bets'," +
                "(select ifnull(SUM(total),0) from (select sum(Play_Bet_Stake) as 'total' from player_bets where Play_Bet_Status  in (202, 203) and DATE(Play_Bet_Timestamp) = '"+date+"' " +
                "union ALL select -(ifnull(sum(Play_Bet_Possible_Winning),0)-(ifnull(sum(Play_Bet_Possible_Winning),0)*0.2)) as 'total' from player_bets where Play_Bet_Status = '202' and DATE(Play_Bet_Timestamp) = '"+date+"') as mytable)as 'GGR', " +
                "(select count(id)  from player where DATE(registration_date) =  '"+date+"' ) as 'registered players'," +
                "(select count(distinct(Play_Bet_Mobile))  from player_bets where DATE(Play_Bet_Timestamp) = '" + date + "') as 'players',"+
                "(select count(distinct(Acc_Mobile)) from user_accounts where DATE(Acc_Date) =  '"+date+"' and Acc_Trans_Type = 1 ) as 'first deposit players'," +
                "(select ifnull(sum(Acc_Amount),0) from user_accounts where DATE(Acc_Date) =  '"+date+"' and Acc_Trans_Type = 1 )as 'first deposit amount'" +
                "FROM user_accounts WHERE Acc_Trans_Type =1 and DATE(Acc_Date) = '"+date+"' ";
                System.out.println("getDailyReport==="+dataQuery);

                rs = stmt.executeQuery(dataQuery);

                while (rs.next())
                {
                    String rptdate = date;
                    String deposits = rs.getString(1);
                    String withdrawals = rs.getString(2);
                    String betstake = rs.getString(3);
                    String betsbonusstake = rs.getString(4);
                    String accbonusamnt = rs.getString(5);
                    String playerbets = rs.getString(6);
                    String ngr = rs.getString(7);
                    String registeredplayers = rs.getString(8);
                    String players = rs.getString(9);
                    String firstdepositplayers = rs.getString(10);
                    String firstdepositplayersamnt = rs.getString(11);

                    dataObj.put("Report_Date", rptdate);
                    dataObj.put("Deposits", deposits);
                    dataObj.put("Withdrawals", withdrawals);
                    dataObj.put("BetStake", betstake);
                    dataObj.put("BetsBonusStake", betsbonusstake);
                    dataObj.put("AccBonusAmount", accbonusamnt);
                    dataObj.put("Bets", playerbets);
                    dataObj.put("GGR", betstake);
                    dataObj.put("NGR", String.format("%.2f", Double.parseDouble(ngr)));
                    dataObj.put("RegisteredPlayers", registeredplayers);
                    dataObj.put("Players", players);
                    dataObj.put("FirstDepositPlayers", firstdepositplayers);
                    dataObj.put("FirstDepositAmount", firstdepositplayersamnt);
                }

                dataArray.put(dataObj);
                rs.close();
            }

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getBCLBReport=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
        
        
        
    public JSONArray filterDailyReport(String datefrom,String dateto)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String dataQuery = "";
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            
            for (String date : new Utility().getDatesList(datefrom ,dateto)) 
            {
                dataObj  = new JSONObject();

                dataQuery = "select ifnull(sum(Acc_Amount),0) as 'deposits', " +
                "(select ifnull(sum(Acc_Amount),0) FROM user_accounts WHERE Acc_Trans_Type =2 and DATE(Acc_Date) = '"+date+"') as'withdrawals' ," +
                "(select  ifnull(sum(Play_Bet_Stake),0) as 'betstakes' from player_bets where DATE(Play_Bet_Timestamp)= '"+date+"' and Play_Bet_Status in (201,202,203)) as 'bet stake'," +
                "(select  '0' from player_bets where DATE(Play_Bet_Timestamp)= '"+date+"' and Play_Bet_Status in (201,202,203)) as 'bet bonus stake'," +
                "(select ifnull(sum(Acc_Bonus_Amount),0) from user_accounts where Acc_Trans_Type=7 and DATE(Acc_Date) ='"+date+"' )as 'acc bonus amount'," +
                "(select count(Play_Bet_ID) from player_bets where DATE(Play_Bet_Timestamp)= '"+date+"' and Play_Bet_Status  in (201,202,203)) as 'player bets'," +
                "(select ifnull(SUM(total),0) from (select sum(Play_Bet_Stake) as 'total' from player_bets where Play_Bet_Status  in (202, 203) and DATE(Play_Bet_Timestamp) = '"+date+"' " +
                "union ALL select -(ifnull(sum(Play_Bet_Possible_Winning),0)) as 'total' from player_bets where Play_Bet_Status = '202' and DATE(Play_Bet_Timestamp) = '"+date+"') as mytable)as 'GGR', " +
                "(select count(id)  from player where DATE(registration_date) =  '"+date+"' ) as 'registered players'," +
                "(select count(distinct(Acc_Mobile)) from user_accounts where DATE(Acc_Date) =  '"+date+"' and Acc_Trans_Type = 1 ) as 'first deposit players'," +
                "(select ifnull(sum(Acc_Amount),0) from user_accounts where DATE(Acc_Date) =  '"+date+"' and Acc_Trans_Type = 1 )as 'first deposit amount'" +
                "FROM user_accounts WHERE Acc_Trans_Type =1 and DATE(Acc_Date) = '"+date+"' ";
                System.out.println("filterDailyReport==="+dataQuery);
                
                rs = stmt.executeQuery(dataQuery);

                while (rs.next())
                {
                   String rptdate = date;
                    String deposits = rs.getString(1);
                    String withdrawals = rs.getString(2);
                    String betstake = rs.getString(3);
                    String betsbonusstake = rs.getString(4);
                    String accbonusamnt = rs.getString(5);
                    String playerbets = rs.getString(6);
                    String ggr = rs.getString(7);
                    String registeredplayers = rs.getString(8);
                    String firstdepositplayers = rs.getString(9);
                    String firstdepositplayersamnt = rs.getString(10);

                    dataObj.put("Report_Date", rptdate);
                    dataObj.put("Deposits", deposits);
                    dataObj.put("Withdrawals", withdrawals);
                    dataObj.put("BetStake", betstake);
                    dataObj.put("BetsBonusStake", betsbonusstake);
                    dataObj.put("AccBonusAmount", accbonusamnt);
                    dataObj.put("PlayerBets", playerbets);
                    dataObj.put("GGR", String.format("%.2f", Double.parseDouble(ggr)));
                    dataObj.put("RegisteredPlayers", registeredplayers);
                    dataObj.put("FirstDepositPlayers", firstdepositplayers);
                    dataObj.put("FirstDepositAmount", firstdepositplayersamnt);
                }

                dataArray.put(dataObj);
                rs.close();
            }

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error filterDailyReport=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
      
    
}
