/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DairyReports;

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
public class DairySummaryReportProcessor {
    
    public DairySummaryReportProcessor()
    {
        
    }
    
    
    public JSONArray getDailySummary(String datefrom,String dateto)
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
                //System.out.println("date====="+date);
                dataQuery = "select count(distinct (msisdn)) as 'registations'," +
                "(select count(distinct(Play_Bet_Mobile)) from player_bets where DATE(Play_Bet_Timestamp) ='"+date+"'  )as 'bettors'," +
                "(select count(Play_Bet_ID) from player_bets where date(Play_Bet_Timestamp)='"+date+"' and  Play_Bet_Type=1 and Play_Bet_Status in (201,202,203)) as 'singlebets'," +
                "(select ifnull(sum(Play_Bet_Stake),0) from player_bets where date(Play_Bet_Timestamp)='"+date+"' and  Play_Bet_Type=1 and Play_Bet_Status in (201,202,203)) as 'singebets amount'," +
                "(select ifnull(sum(Play_Bet_Possible_Winning),0) from player_bets where date(Play_Bet_Timestamp)='"+date+"' and  Play_Bet_Type=1 and  Play_Bet_Status=202) as 'singlebets payout'," +
                "(select ifnull(sum(Play_Bet_Stake),0) from player_bets where date(Play_Bet_Timestamp)='"+date+"' and  Play_Bet_Type=1 and  Play_Bet_Status in (202, 203))as 'settled singlebets'," +
                "(select count(Play_Bet_ID) from player_bets where date(Play_Bet_Timestamp)='"+date+"' and Play_Bet_Type=3 and Play_Bet_Status in (201,202,203)) as 'multibets'," +
                "(select ifnull(sum(Play_Bet_Stake),0) from player_bets where date(Play_Bet_Timestamp)='"+date+"'  and  Play_Bet_Type=3 and Play_Bet_Status in (201,202,203)) as 'multibets amount'," +
                "(select ifnull(sum(Play_Bet_Possible_Winning),0) from player_bets where date(Play_Bet_Timestamp)='"+date+"' and  Play_Bet_Type=3 and  Play_Bet_Status=202)  as 'multibets payout'," +
                "(select ifnull(sum(Play_Bet_Stake),0) from player_bets where date(Play_Bet_Timestamp)='"+date+"'  and  Play_Bet_Type=3 and  Play_Bet_Status in (202, 203)) as 'settled multibets'," +
                "(select count(Play_Bet_ID) from player_bets where date(Play_Bet_Timestamp)='"+date+"' and Play_Bet_Type=2 and Play_Bet_Status in (201,202,203)) as 'jpbets'," +
                "(select ifnull(sum(Play_Bet_Stake),0) from player_bets where date(Play_Bet_Timestamp)='"+date+"'  and  Play_Bet_Type=2 and Play_Bet_Status in (201,202,203)) as 'jp amount'," +
                "(select ifnull(sum(Play_Bet_Possible_Winning),0) from player_bets where date(Play_Bet_Timestamp)='"+date+"' and  Play_Bet_Type=2 and  Play_Bet_Status=202) as 'jp payout'," +
                "(select ifnull(sum(Play_Bet_Stake),0) from player_bets where date(Play_Bet_Timestamp)='"+date+"'  and  Play_Bet_Type=2 and  Play_Bet_Status in (202, 203)) as 'settled jpbets'," +
                "(select ifnull(sum(Acc_Amount),0) from user_accounts where DATE(Acc_Date) ='"+date+"' and Acc_Trans_Type = 7) as 'bonus redeemed'," +
                "(select ifnull(sum(Acc_Amount),0) from user_accounts where DATE(Acc_Date) = '"+date+"' and Acc_Trans_Type = 9) as 'bonus achieved'," +
                "(select count(distinct(Play_Bet_Mobile)) from player_bets  where DATE(Play_Bet_Timestamp) = '"+date+"' and  Play_Bet_Type = 1 )as 'unique singlebets'," +
                "(select count(distinct(Play_Bet_Mobile)) from player_bets  where DATE(Play_Bet_Timestamp) = '"+date+"' and  Play_Bet_Type = 3 )as 'unique multibets'," +
                "(select count(distinct(Play_Bet_Mobile)) from player_bets  where DATE(Play_Bet_Timestamp) = '"+date+"' and  Play_Bet_Type = 2 )as 'unique jpbets'," +
                "(select count(distinct(Play_Bet_Mobile)) from player_bets  where DATE(Play_Bet_Timestamp) = '"+date+"' and  Play_Bet_Type = 1 or Play_Bet_Type = 3  )as 'unique singleandmultibets'," +
                "(select count(distinct(Play_Bet_Mobile)) from player_bets  where DATE(Play_Bet_Timestamp) = '"+date+"' and  Play_Bet_Type = 1 or Play_Bet_Type = 2  )as 'unique singleandjpbets'," +
                "(select count(distinct(Play_Bet_Mobile)) from player_bets  where DATE(Play_Bet_Timestamp) = '"+date+"' and  Play_Bet_Type = 2 or Play_Bet_Type = 3  )as 'unique multiandjpbets'," +
                "(select count(distinct(Play_Bet_Mobile)) from player_bets  where DATE(Play_Bet_Timestamp) = '"+date+"'  and  Play_Bet_Type = 1 or   Play_Bet_Type = 2 or Play_Bet_Type = 3  )as 'unique allbets'" +
                "from player  where date(registration_date)='"+date+"' order by registration_date ";
                System.out.println("getDailySummary==="+dataQuery);
                
                rs = stmt.executeQuery(dataQuery);

                while (rs.next())
                {
                    String rptdate = date;
                    String registeredplayers = rs.getString(1);
                    String registeredplayers_bettors = rs.getString(2);
                    String singlebets = rs.getString(3);
                    String singlebetsamount = rs.getString(4);
                    double sp_payout=Double.valueOf(rs.getString(5))-(Double.valueOf(rs.getString(4))*0.2);
                    String singlebetspayout = String.format("%.2f", sp_payout);
                    String settledsinglebetsamount = rs.getString(6);
                    double singlebet_proft=Double.valueOf(settledsinglebetsamount) - Double.valueOf(singlebetspayout);
                    String sbProfits =String.format("%.2f", singlebet_proft);
                    String multibets=rs.getString(7);
                    String multibetsamount = rs.getString(8);
                    double mb_payout=Double.valueOf(rs.getString(9))-(Double.valueOf(rs.getString(8))*0.2);
                    String multibetspayout = String.format("%.2f", mb_payout);
                    String settledmultibetsamount = rs.getString(10);
                    double multibet_proft=Double.valueOf(settledmultibetsamount) - Double.valueOf(multibetspayout);
                    String mbProfits = String.format("%.2f", multibet_proft);
                    String jpbets=rs.getString(11);
                    String jpbetsamount = rs.getString(12);
                    double jp_payout=Double.valueOf(rs.getString(13))-(Double.valueOf(rs.getString(12))*0.2);
                    String jpbetspayout = String.format("%.2f", jp_payout);
                    String settledjpbetsamount = rs.getString(14);
                    double jackpot_proft=Double.valueOf(settledjpbetsamount) - Double.valueOf(jpbetspayout);
                    String jpProfits = String.format("%.2f", jackpot_proft);  
                    double total_profits=Double.valueOf(sbProfits) + Double.valueOf(mbProfits) + Double.valueOf(jpProfits);
                    String betsProfits =String.format("%.2f", total_profits); 
                    String bonusredeemed=rs.getString(15);
                    String bonusachieved=rs.getString(16);
                    String totalbetsProfits =betsProfits;// String.valueOf((Integer.valueOf(betsProfits) - Integer.valueOf(bonusachieved)));
                    String uniquesb= rs.getString(17);
                    String uniquemb = rs.getString(18);
                    String uniquejp = rs.getString(19);
                    String uniquesbandmbQuery = rs.getString(20);
                    String uniquesbandjpQuery = rs.getString(21);
                    String uniquembandjpQuery = rs.getString(22);
                    String uniquesbandmbandjpQuery = rs.getString(23);

                    dataObj.put("Report_Date", rptdate);
                    dataObj.put("RegisteredPlayers", registeredplayers);
                    dataObj.put("RegisteredPlayersBettors", registeredplayers_bettors);
                    dataObj.put("SingleBets", singlebets);
                    dataObj.put("SingleBetsAmount", singlebetsamount);
                    dataObj.put("SingleBetsPayout", singlebetspayout);
                    dataObj.put("SettledSingleBetsAmount", settledsinglebetsamount);
                    dataObj.put("SingleBetsProfits", sbProfits);
                    dataObj.put("MultiBets", multibets);
                    dataObj.put("MultiBetsAmount", multibetsamount);
                    dataObj.put("MultiBetsPayout", multibetspayout);
                    dataObj.put("SettledMultibetsAmount", settledmultibetsamount);
                    dataObj.put("MultiBetsProfit", mbProfits);
                    dataObj.put("JackpotBets", jpbets);
                    dataObj.put("JackpotBetsAmount", jpbetsamount);
                    dataObj.put("JackpotBetsPayout", jpbetspayout);
                    dataObj.put("SettledJackpotBetsAmount", settledjpbetsamount);
                    dataObj.put("JackpotBetsProfit", jpProfits);
                    dataObj.put("BetsProfit", betsProfits);
                    dataObj.put("BonusRedeemed", bonusredeemed);
                    dataObj.put("BonusAchieved", bonusachieved);
                    dataObj.put("TotalBetsProfit", totalbetsProfits);
                    dataObj.put("UniqueSingleBets", uniquesb);
                    dataObj.put("UniqueJackpotBets", uniquejp);
                    dataObj.put("UniqueMultiBets", uniquemb);
                    dataObj.put("UniqueSingleBets_MultiBets", uniquesbandmbQuery);
                    dataObj.put("UniqueSingleBets_JackpotBets", uniquesbandjpQuery);
                    dataObj.put("UniqueMultiBets_JackpotBets", uniquembandjpQuery);
                    dataObj.put("UniqueSingleBets_MultiBets_JackpotBets", uniquesbandmbandjpQuery);
                }
                
                dataArray.put(dataObj);
                rs.close();
            }

        } catch (SQLException | JSONException ex ) 
        {
            System.out.println("Error getDailySummary=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }



    public JSONArray filterDailySummary(String datefrom,String dateto)
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
                //System.out.println("date====="+date);
                dataQuery = "select count(distinct (msisdn)) as 'registations'," +
                "(select count(distinct(Play_Bet_Mobile)) from player_bets where DATE(Play_Bet_Timestamp) ='"+date+"'  )as 'bettors'," +
                "(select count(Play_Bet_ID) from player_bets where date(Play_Bet_Timestamp)='"+date+"' and  Play_Bet_Type=1 and Play_Bet_Status in (201,202,203)) as 'singlebets'," +
                "(select ifnull(sum(Play_Bet_Stake),0) from player_bets where date(Play_Bet_Timestamp)='"+date+"' and  Play_Bet_Type=1 and Play_Bet_Status in (201,202,203)) as 'singebets amount'," +
                "(select ifnull(sum(Play_Bet_Possible_Winning),0) from player_bets where date(Play_Bet_Timestamp)='"+date+"' and  Play_Bet_Type=1 and  Play_Bet_Status=202) as 'singlebets payout'," +
                "(select ifnull(sum(Play_Bet_Stake),0) from player_bets where date(Play_Bet_Timestamp)='"+date+"' and  Play_Bet_Type=1 and  Play_Bet_Status in (202, 203))as 'settled singlebets'," +
                "(select count(Play_Bet_ID) from player_bets where date(Play_Bet_Timestamp)='"+date+"' and Play_Bet_Type=3 and Play_Bet_Status in (201,202,203)) as 'multibets'," +
                "(select ifnull(sum(Play_Bet_Stake),0) from player_bets where date(Play_Bet_Timestamp)='"+date+"'  and  Play_Bet_Type=3 and Play_Bet_Status in (201,202,203)) as 'multibets amount'," +
                "(select ifnull(sum(Play_Bet_Possible_Winning),0) from player_bets where date(Play_Bet_Timestamp)='"+date+"' and  Play_Bet_Type=3 and  Play_Bet_Status=202)  as 'multibets payout'," +
                "(select ifnull(sum(Play_Bet_Stake),0) from player_bets where date(Play_Bet_Timestamp)='"+date+"'  and  Play_Bet_Type=3 and  Play_Bet_Status in (202, 203)) as 'settled multibets'," +
                "(select count(Play_Bet_ID) from player_bets where date(Play_Bet_Timestamp)='"+date+"' and Play_Bet_Type=2 and Play_Bet_Status in (201,202,203)) as 'jpbets'," +
                "(select ifnull(sum(Play_Bet_Stake),0) from player_bets where date(Play_Bet_Timestamp)='"+date+"'  and  Play_Bet_Type=2 and Play_Bet_Status in (201,202,203)) as 'jp amount'," +
                "(select ifnull(sum(Play_Bet_Possible_Winning),0) from player_bets where date(Play_Bet_Timestamp)='"+date+"' and  Play_Bet_Type=2 and  Play_Bet_Status=202) as 'jp payout'," +
                "(select ifnull(sum(Play_Bet_Stake),0) from player_bets where date(Play_Bet_Timestamp)='"+date+"'  and  Play_Bet_Type=2 and  Play_Bet_Status in (202, 203)) as 'settled jpbets'," +
                "(select sum(Acc_Amount) from user_accounts where DATE(Acc_Date) ='"+date+"' and Acc_Trans_Type = 7) as 'bonus redeemed'," +
                "(select sum(Acc_Amount) from user_accounts where DATE(Acc_Date) = '"+date+"' and Acc_Trans_Type = 9) as 'bonus achieved'," +
                "(select count(distinct(Play_Bet_Mobile)) from player_bets  where DATE(Play_Bet_Timestamp) = '"+date+"' and  Play_Bet_Type = 1 )as 'unique singlebets'," +
                "(select count(distinct(Play_Bet_Mobile)) from player_bets  where DATE(Play_Bet_Timestamp) = '"+date+"' and  Play_Bet_Type = 3 )as 'unique multibets'," +
                "(select count(distinct(Play_Bet_Mobile)) from player_bets  where DATE(Play_Bet_Timestamp) = '"+date+"' and  Play_Bet_Type = 2 )as 'unique jpbets'," +
                "(select count(distinct(Play_Bet_Mobile)) from player_bets  where DATE(Play_Bet_Timestamp) = '"+date+"' and  Play_Bet_Type = 1 or Play_Bet_Type = 3  )as 'unique singleandmultibets'," +
                "(select count(distinct(Play_Bet_Mobile)) from player_bets  where DATE(Play_Bet_Timestamp) = '"+date+"' and  Play_Bet_Type = 1 or Play_Bet_Type = 2  )as 'unique singleandjpbets'," +
                "(select count(distinct(Play_Bet_Mobile)) from player_bets  where DATE(Play_Bet_Timestamp) = '"+date+"' and  Play_Bet_Type = 2 or Play_Bet_Type = 3  )as 'unique multiandjpbets'," +
                "(select count(distinct(Play_Bet_Mobile)) from player_bets  where DATE(Play_Bet_Timestamp) = '"+date+"'  and  Play_Bet_Type = 1 or   Play_Bet_Type = 2 or Play_Bet_Type = 3  )as 'unique allbets'" +
                "from player  where date(registration_date)='"+date+"' ";
                
                System.out.println("filterDailySummary==="+dataQuery);
                
                rs = stmt.executeQuery(dataQuery);

                while (rs.next())
                {
                    String rptdate = date;
                    String registeredplayers = rs.getString(1);
                    String registeredplayers_bettors = rs.getString(2);
                    String singlebets = rs.getString(3);
                    String singlebetsamount = rs.getString(4);
                    double sp_payout=Double.valueOf(rs.getString(5))-(Double.valueOf(rs.getString(4))*0.2);
                    String singlebetspayout = String.format("%.2f", sp_payout);
                    String settledsinglebetsamount = rs.getString(6);
                    double singlebet_proft=Double.valueOf(settledsinglebetsamount) - Double.valueOf(singlebetspayout);
                    String sbProfits =String.format("%.2f", singlebet_proft);
                    String multibets=rs.getString(7);
                    String multibetsamount = rs.getString(8);
                    double mb_payout=Double.valueOf(rs.getString(9))-(Double.valueOf(rs.getString(8))*0.2);
                    String multibetspayout = String.format("%.2f", mb_payout);
                    String settledmultibetsamount = rs.getString(10);
                    double multibet_proft=Double.valueOf(settledmultibetsamount) - Double.valueOf(multibetspayout);
                    String mbProfits = String.format("%.2f", multibet_proft);
                    String jpbets=rs.getString(11);
                    String jpbetsamount = rs.getString(12);
                    double jp_payout=Double.valueOf(rs.getString(13))-(Double.valueOf(rs.getString(12))*0.2);
                    String jpbetspayout = String.format("%.2f", jp_payout);
                    String settledjpbetsamount = rs.getString(14);
                    double jackpot_proft=Double.valueOf(settledjpbetsamount) - Double.valueOf(jpbetspayout);
                    String jpProfits = String.format("%.2f", jackpot_proft);  
                    double total_profits=Double.valueOf(sbProfits) + Double.valueOf(mbProfits) + Double.valueOf(jpProfits);
                    String betsProfits =String.format("%.2f", total_profits); 
                    String bonusredeemed=rs.getString(15);
                    String bonusachieved=rs.getString(16);
                    String totalbetsProfits =betsProfits;// String.valueOf((Integer.valueOf(betsProfits) - Integer.valueOf(bonusachieved)));
                    String uniquesb= rs.getString(17);
                    String uniquemb = rs.getString(18);
                    String uniquejp = rs.getString(19);
                    String uniquesbandmbQuery = rs.getString(20);
                    String uniquesbandjpQuery = rs.getString(21);
                    String uniquembandjpQuery = rs.getString(22);
                    String uniquesbandmbandjpQuery = rs.getString(23);

                    dataObj.put("Report_Date", rptdate);
                    dataObj.put("RegisteredPlayers", registeredplayers);
                    dataObj.put("RegisteredPlayersBettors", registeredplayers_bettors);
                    dataObj.put("SingleBets", singlebets);
                    dataObj.put("SingleBetsAmount", singlebetsamount);
                    dataObj.put("SingleBetsPayout", singlebetspayout);
                    dataObj.put("SettledSingleBetsAmount", settledsinglebetsamount);
                    dataObj.put("SingleBetsProfits", sbProfits);
                    dataObj.put("MultiBets", multibets);
                    dataObj.put("MultiBetsAmount", multibetsamount);
                    dataObj.put("MultiBetsPayout", multibetspayout);
                    dataObj.put("SettledMultibetsAmount", settledmultibetsamount);
                    dataObj.put("MultiBetsProfit", mbProfits);
                    dataObj.put("JackpotBets", jpbets);
                    dataObj.put("JackpotBetsAmount", jpbetsamount);
                    dataObj.put("JackpotBetsPayout", jpbetspayout);
                    dataObj.put("SettledJackpotBetsAmount", settledjpbetsamount);
                    dataObj.put("JackpotBets", jpProfits);
                    dataObj.put("BetsProfit", betsProfits);
                    dataObj.put("BonusRedeemed", bonusredeemed);
                    dataObj.put("BonusAchieved", bonusachieved);
                    dataObj.put("TotalBetsProfit", totalbetsProfits);
                    dataObj.put("UniqueSingleBets", uniquesb);
                    dataObj.put("UniqueJackpotBets", uniquejp);
                    dataObj.put("UniqueSingleBets_MultiBets", uniquesbandmbQuery);
                    dataObj.put("UniqueSingleBets_JackpotBets", uniquesbandjpQuery);
                    dataObj.put("UniqueMultiBets_JackpotBets", uniquembandjpQuery);
                    dataObj.put("UniqueSingleBets_MultiBets_JackpotBets", uniquesbandmbandjpQuery);
                }

                dataArray.put(dataObj);
                rs.close();
            }

        } catch (SQLException | JSONException ex ) 
        {
            System.out.println("Error filterDailySummary=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
    
}
