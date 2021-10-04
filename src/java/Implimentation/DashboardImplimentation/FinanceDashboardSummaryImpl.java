/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Implimentation.DashboardImplimentation;

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
public class FinanceDashboardSummaryImpl {
    
    public FinanceDashboardSummaryImpl()
    {
        
    }
    
    
    // dashboard deposit values
    public JSONArray getFinancialDashboardSummary(String fromDate,String toDate) {

        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String []collection=null;
        String dataQueryDeposit = "";
        String dataQueryWithdrawals = "";
        String dataQueryBalance = "";
        String dataQueryBalanceCount = "";
        String bonusbalancecount = "";
        String bonusbalancesum = "";
        String registrations="";
        String dataQueryTurnover = "";
        String dataQueryOpenBetsTurnOver = "";
        String dataQueryWinnings = "";
        String dataQuerySettledTurnOver = "";
        String dataQueryBetsByStatus="";
        String dataQueryBetsByChannel="";
        String dataQueryBetsByBetType="";
        String dataQuerySettledBetsTurnOver="";
        String dataQueryWonBetsTurnOver="";
        
        String depositAccBal="0";
        String depositPlayers="0";
        String withdrawAccBal="0";
        String withdrawPlayers="0";
        String playersRMBalance="0";
        String playersRMCount="0";
        String playersBMBalance="0";
        String playersBMCount="0";
        
        String totalTurnoverRM="0";
        String totalTurnoverBM="0";
        String totalOpenBetsRMTurnOver="0";
        String totalOpenBetsBMTurnOver="0";
        String settledBetsTurnoverRM="0";
        String wonBetsTurnoverRM="0";
        String totalWinnings="0";
        
        String USSD_Reg="0";
        String computer_Web_Reg="0";
        String SMS_Reg="0";
        String mobile_Web_Reg="0";
        
        String placedBets="0";
        String wonBets="0";
        String lostBets="0";
        String rejectedBets="0";
        String cancelledBets="0";
        String settledBets="0";
        
        String USSD_Bets="0";
        String computer_Web_Bets="0";
        String mobile_Web_Bets="0";
        String SMS_Bets="0";
        
        String Single_Bets="0";
        String Multi_Bets="0";
        String Jackpot_Bets="0";
        
        double GGR=0;
        double NGR=0;
        double Profit=0;
        double Lost=0;
        
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();
        JSONObject main = new JSONObject();
        
        dataQueryTurnover = "select ifnull(sum(Play_Bet_Stake),0), ifnull(sum(Play_Bet_Bonus_Stake),0) from player_bets where Play_Bet_Status in (201,202,203)"
                + " and  date(Play_Bet_Timestamp) between '" + fromDate + "' and '" + toDate + "'";

        dataQueryOpenBetsTurnOver = "select ifnull(sum(Play_Bet_Stake),0), ifnull(sum(Play_Bet_Bonus_Stake),0) from player_bets where Play_Bet_Status in (201) "
                + " and  date(Play_Bet_Timestamp) between '" + fromDate + "' and '" + toDate + "'";
        
        dataQuerySettledBetsTurnOver = "select ifnull(sum(Play_Bet_Stake),0), ifnull(sum(Play_Bet_Bonus_Stake),0) from player_bets where Play_Bet_Status in (202,203) "
                + " and  date(Play_Bet_Timestamp) between '" + fromDate + "' and '" + toDate + "'";
        
        dataQueryWonBetsTurnOver = "select ifnull(sum(Play_Bet_Stake),0), ifnull(sum(Play_Bet_Bonus_Stake),0) from player_bets where Play_Bet_Status in (202) "
                + " and  date(Play_Bet_Timestamp) between '" + fromDate + "' and '" + toDate + "'";

        dataQueryWinnings = "select ifnull(sum(Play_Bet_Possible_Winning),0) from player_bets where Play_Bet_Status in (202) "
                + " and  date(Play_Bet_Timestamp) between '" + fromDate + "' and '" + toDate + "'";

        registrations="select count(id),(select count(id) from player where date(registration_date) between  '" + fromDate + "' and '" + toDate + "' and user_channel=2), "
                    + "(select count(id) from player where date(registration_date) between  '" + fromDate + "' and '" + toDate + "' and user_channel=3), "
                    + "(select count(id) from player where date(registration_date) between  '" + fromDate + "' and '" + toDate + "' and user_channel=4) "
                    + "from player where date(registration_date) between  '" + fromDate + "' and '" + toDate + "' and user_channel=1 ";

        dataQueryDeposit = "SELECT count(Acc_ID),ifnull(sum(Acc_Amount),0) FROM user_accounts "
                + "WHERE Acc_Trans_Type = 1 and  date(Acc_Date) between '" + fromDate + "' and '" + toDate + "'";

        dataQueryWithdrawals = "SELECT  count(Acc_ID),ifnull(sum(Acc_Amount),0) FROM user_accounts "
               + "WHERE Acc_Trans_Type = 2 and  date(Acc_Date) between '" + fromDate + "' and '" + toDate + "'";

        dataQueryBalance = "select  sum(Acc_Amount),(select sum(Acc_Amount) from user_accounts where  Acc_Trans_Type in (1,3,9)   and date(Acc_Date)  between '" + fromDate + "' and '" + toDate + "' ) as 'Deposits' ,"
                + "( select sum(Acc_Amount) from user_accounts where  Acc_Trans_Type in (4,2,8)   and date(Acc_Date)  between '" + fromDate + "' and '" + toDate + "' )as 'Withdrawals' "
                + "from user_accounts  where  date(Acc_Date) between '" + fromDate + "' and '" + toDate + "' ";

        //bonusbalancesum = "select count(id),ifnull(sum(Bonus_Balance),0) from player where Bonus_Balance > 0 and date(registration_date) between '" + fromDate + "' and '" + toDate + "' ";

        dataQueryBetsByStatus = "select Play_Bet_Status,count(Play_Bet_ID) from player_bets where date(Play_Bet_Timestamp) between '" + fromDate + "' and '" + toDate + "' GROUP BY Play_Bet_Status";
        
        dataQueryBetsByChannel="select count(Play_Bet_Slip_ID),(select count(Play_Bet_Slip_ID) from player_bets where date(Play_Bet_Timestamp) between '" + fromDate + "' and '" + toDate + "' and Play_Bet_Channel = 2), "
                + "(select count(Play_Bet_Slip_ID) from player_bets where date(Play_Bet_Timestamp) between '" + fromDate + "' and '" + toDate + "' and Play_Bet_Channel = 3), "
                + "(select count(Play_Bet_Slip_ID) from player_bets where date(Play_Bet_Timestamp) between '" + fromDate + "' and '" + toDate + "' and Play_Bet_Channel = 4) "
                + "from player_bets where date(Play_Bet_Timestamp) between '" + fromDate + "' and '" + toDate + "' and Play_Bet_Channel = 1 " ;

        dataQueryBetsByBetType="select ifnull(Play_Bet_Type,0), ifnull(count(Play_Bet_Type),0) from player_bets  where date(Play_Bet_Timestamp) between '" + fromDate + "' and '" + toDate + "' and Play_Bet_Status in (201,202,203) GROUP BY Play_Bet_Type";
        
        
        String financequeries=dataQueryDeposit+"#"+dataQueryWithdrawals+"#"+dataQueryBalance;
        collection=financequeries.split("#");

       
            try
            {
                conn = DBManager.getInstance().getDBConnection("read");
                stmt = conn.createStatement();
                
                for(int i=0;i<collection.length;i++)
                {
                    System.out.println(i+"dataQueryDeposit==="+collection[i]);
                    rs = stmt.executeQuery(collection[i]); 
                    while (rs.next()) 
                    {
                        switch (i) 
                        {
                            case 0:
                               depositPlayers=rs.getString(1);
                               depositAccBal=String.valueOf((Math.round(Double.valueOf(rs.getString(2)).intValue())));
                            break;
                            case 1:
                               withdrawPlayers=rs.getString(1);
                               withdrawAccBal=String.valueOf((Math.round(Double.valueOf(rs.getString(2)).intValue())));
                            break;
                            case 2:
                                playersRMBalance=String.valueOf((Math.round(Double.valueOf(rs.getString(1)).intValue())));
                            break;
                            default:
                            break;
                        }
                        
                    } 
                }
                
                dataObj  = new JSONObject();
                dataObj.put("DepositCount", depositPlayers);
                dataObj.put("DepositsValue", depositAccBal);
                dataObj.put("WithdrawalCount", withdrawPlayers);
                dataObj.put("WithdrawalValue", withdrawAccBal);
                dataObj.put("RMBalanceValue", playersRMBalance);
                
                main.put("Finance_Summary", dataObj);
                
                
                String profitqueries=dataQueryTurnover+"#"+dataQueryOpenBetsTurnOver+"#"+dataQueryWinnings+"#"+dataQuerySettledBetsTurnOver+"#"+dataQueryWonBetsTurnOver;
                collection=profitqueries.split("#");
                for(int i=0;i<collection.length;i++)
                {
                   System.out.println(i+"profitquery==="+collection[i]);
                    rs = stmt.executeQuery(collection[i]); 
                    while (rs.next()) 
                    {  
                        switch (i) 
                        {
                            case 0:
                               totalTurnoverRM=String.valueOf((Math.round(Double.valueOf(rs.getString(1)).intValue())));
                               totalTurnoverBM=String.valueOf((Math.round(Double.valueOf(rs.getString(2)).intValue())));
                            break;
                            case 1:
                               totalOpenBetsRMTurnOver=String.valueOf((Math.round(Double.valueOf(rs.getString(1)).intValue())));
                               totalOpenBetsBMTurnOver=String.valueOf((Math.round(Double.valueOf(rs.getString(2)).intValue())));
                            break;
                            case 2:
                               totalWinnings=rs.getString(1);
                            break;
                            case 3:
                               settledBetsTurnoverRM=String.valueOf((Math.round(Double.valueOf(rs.getString(1)).intValue())));;
                            break;
                            case 4:
                               wonBetsTurnoverRM=String.valueOf((Math.round(Double.valueOf(rs.getString(1)).intValue())));;
                            break;
                            default:
                            break;
                        }                      
                    } 
                }
                
                dataObj  = new JSONObject();
                
                //double win_tax=Double.valueOf(totalWinnings)*0.2;
                double taxedamount_won=Double.valueOf(totalWinnings);//-win_tax;
                
                dataObj.put("TotalTurnover", totalTurnoverRM);
                dataObj.put("TurnoverRM", totalTurnoverRM);
                dataObj.put("TurnoverBM", totalTurnoverBM);
                dataObj.put("TotalOpenBetsRM", totalOpenBetsRMTurnOver);
                dataObj.put("TotalOpenBetsBM", totalOpenBetsBMTurnOver);
                dataObj.put("TotalWinnings", taxedamount_won);
                
                GGR = Integer.valueOf(totalTurnoverRM) ;
                double ngr_val =GGR - (Double.valueOf(totalOpenBetsRMTurnOver) + taxedamount_won);
                NGR=ngr_val-(ngr_val*0.15);
                Profit=Double.valueOf(settledBetsTurnoverRM)-(Double.valueOf(wonBetsTurnoverRM)-Double.valueOf(wonBetsTurnoverRM)*0.15);
                Lost=taxedamount_won;
                
                dataObj.put("GGR", String.valueOf(GGR));
                dataObj.put("NGR", String.valueOf(NGR));
                dataObj.put("Profit",String.valueOf(Profit));
                dataObj.put("Lost", String.valueOf(Lost));
                
                main.put("Profit_Summary", dataObj);
                
                
                
                System.out.println("registrations==="+registrations);
                rs = stmt.executeQuery(registrations); 
                while (rs.next()) 
                {
                    USSD_Reg=rs.getString(1);
                    SMS_Reg=rs.getString(2);
                    computer_Web_Reg=rs.getString(3);
                    mobile_Web_Reg=rs.getString(4);
                }
                dataObj  = new JSONObject();
                dataObj.put("SMS_Reg", SMS_Reg);
                dataObj.put("USSD_Reg", USSD_Reg);
                dataObj.put("Computer_Web", computer_Web_Reg);
                dataObj.put("Mobile_Web", mobile_Web_Reg);
                
                main.put("Registration_Summary", dataObj);
                
                
                
                System.out.println("dataQueryBetsByStatus==="+dataQueryBetsByStatus);
                rs = stmt.executeQuery(dataQueryBetsByStatus); 
                while (rs.next()) 
                {
                    switch (rs.getInt(1)) {
                        case 201:
                            placedBets = rs.getString(2);
                            break;
                        case 202:
                            wonBets = rs.getString(2);
                            break;
                        case 203:
                            lostBets = rs.getString(2);
                            break;
                        case 204:
                            rejectedBets = rs.getString(2);
                            break;
                        case 205:
                            cancelledBets = rs.getString(2);
                            break;
                        default:
                            break;
                    }
                    
                    settledBets = String.valueOf(Integer.valueOf(lostBets) + Integer.valueOf(wonBets));
                }
                
                
                dataObj  = new JSONObject();
                dataObj.put("Open_Bets", placedBets);
                dataObj.put("Won_Bets", wonBets);
                dataObj.put("Lost_Bets", lostBets);
                dataObj.put("Rejected_Bets", rejectedBets);
                dataObj.put("Cancelled_Bets", cancelledBets);
                dataObj.put("Settled_Bets", settledBets);
                
                main.put("BetsByStatus_Summary", dataObj);
                
                
                
                
                System.out.println("dataQueryBetsByChannel==="+dataQueryBetsByChannel);
                rs = stmt.executeQuery(dataQueryBetsByChannel); 
                 while (rs.next()) 
                {
                    USSD_Bets=rs.getString(1);
                    SMS_Bets=rs.getString(2);
                    computer_Web_Bets=rs.getString(3);
                    mobile_Web_Bets=rs.getString(4);
                }
                dataObj  = new JSONObject();
                dataObj.put("SMS_Bets", SMS_Bets);
                dataObj.put("USSD_Bets", USSD_Bets);
                dataObj.put("Computer_Web_Bets", computer_Web_Bets);
                dataObj.put("Mobile_Web_Bets", mobile_Web_Bets);
                
                main.put("BetsByChannel_Summary", dataObj);
                
                
                
                
                System.out.println("dataQueryBetsByBetType==="+dataQueryBetsByBetType);
                rs = stmt.executeQuery(dataQueryBetsByBetType);
                while (rs.next()) 
                {
                    if(rs.getInt(1)>0)
                    {
                        switch (rs.getInt(1)) 
                        {
                            case 1:
                                Single_Bets = rs.getString(2);
                            break;
                            case 4:
                                Jackpot_Bets = rs.getString(2);
                            break;
                            case 3:
                                Multi_Bets = rs.getString(2);
                            break;
                            default:
                            break;
                        } 
                    }
                    
                }
                dataObj  = new JSONObject();
                dataObj.put("Single_Bets", Single_Bets);
                dataObj.put("Multi_Bets", Multi_Bets);
                dataObj.put("Jackpot_Bets", Jackpot_Bets);
                
                main.put("BetsByBetType_Summary", dataObj);
                
                dataArray.put(main);
              

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getFinancialDashboardSummary=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

        
        return dataArray;
    }
    
   
     
    
}
