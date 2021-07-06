/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dashboard;

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
public class MainDashBoardSummaryProcessor {
    
    public MainDashBoardSummaryProcessor()
    {
        
    }
    
    public JSONArray getMainDashboardSummary(String fromDate, String toDate) 
    {

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
        String dataQueryPlayersSummary="";
        
        
        String c2bDepositAccBal="0";
        String betWinDepositAccBal="0";
        String depositPlayers="0";
        String b2cwithdrawAccBal="0";
        String wallet2BetwithdrawAccBal="0";
        String wallet2GoldenraceBetwithdrawAccBal="0";
        String wallet2SlotGameBetwithdrawAccBal="0";
        String wallet2JackpotBetwithdrawAccBal="0";
        String betsWonAccBal="0";
        String goldenRaceBetwinAccBal="0";
        String slotGameBetwinAccBal="0";
        String withdrawPlayers="0";
        String playersRMBalance="0";
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
        
        String referedPlayers="0";
        String refereesPlayers="0";
        String activePlayers="0";
        String inactivePlayers="0";
        String totalPlayers="0";
        
        String placedBets="0";
        String wonBets="0";
        String lostBets="0";
        String rejectedBets="0";
        String cancelledBets="0";
        String settledBets="0";
        String totalBets="0";
        
        String USSD_Bets="0";
        String Computer_Web_Bets="0";
        String SMS_Bets="0";
        String Mobile_Web_Bets="0";
        
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
        //sms,ussd,web,app
        registrations="select count(id),(select count(id) from player where date(registration_date) between  '" + fromDate + "' and '" + toDate + "' and user_channel=2), "
                    + "(select count(id) from player where date(registration_date) between  '" + fromDate + "' and '" + toDate + "' and user_channel=3), "
                    + "(select count(id) from player where date(registration_date) between  '" + fromDate + "' and '" + toDate + "' and user_channel=4) "
                    + "from player where date(registration_date) between  '" + fromDate + "' and '" + toDate + "' and user_channel=1 ";

        dataQueryDeposit = "SELECT count(Acc_ID),ifnull(sum(Acc_Amount),0) as 'mpesa deposits' "
                + "FROM user_accounts where Acc_Trans_Type = 1 and  date(Acc_Date) between '" + fromDate + "' and '" + toDate + "' and Acc_Mpesa_Trans_No not like 'BET%' ";

        dataQueryWithdrawals = "SELECT  count(Acc_ID),ifnull(sum(Acc_Amount),0) as 'user b2c withdrawal', "
                            + "(select  ifnull(sum(Play_Bet_Stake),0) as 'betstakes' from player_bets where DATE(Play_Bet_Timestamp) between '" + fromDate + "' and '" + toDate + "' and Play_Bet_Status in (201,202,203)) as 'normal bet withdrawal' , "
                            + "(select ifnull(sum(Acc_Amount),0) from user_accounts where Acc_Trans_Type = 4 and Acc_Gateway like '%GOLDENRACE_BET%' and date(Acc_Date) between '" + fromDate + "' and '" + toDate + "' ) as 'goldenrace bet withdrawal' , "
                            + "(select ifnull(sum(Acc_Amount),0) from user_accounts where Acc_Trans_Type = 4 and Golden_Race_GameCycleId like 'SM%' and date(Acc_Date) between '" + fromDate + "' and '" + toDate + "' ) as 'slotgame bet withdrawal' , "
                            + "(select ifnull(sum(Acc_Amount),0) from user_accounts where Acc_Trans_Type = 3 and Golden_Race_Trans_Type like 'win%' and Acc_Gateway like '%GOLDENRACE_BET%' and date(Acc_Date) between '" + fromDate + "' and '" + toDate + "' ) as 'goldenrace bet win' , "
                            + "(select ifnull(sum(Acc_Amount),0) from user_accounts where Acc_Trans_Type = 3  and Golden_Race_Trans_Type like 'win%' and Golden_Race_GameCycleId like 'SM%' and date(Acc_Date) between '" + fromDate + "' and '" + toDate + "' ) as 'slotgame bet win' , "
                            + "(select ifnull(sum(Play_Bet_Possible_Winning),0) from player_bets where date(Play_Bet_Timestamp) between '" + fromDate + "' and '" + toDate + "' and Play_Bet_Status=202) as 'bets won' ,"
                            + "(select  ifnull(sum(Play_Bet_Stake),0) from player_bets where DATE(Play_Bet_Timestamp) between '" + fromDate + "' and '" + toDate + "' and Play_Bet_Status in (201,202,203) and Play_Bet_Type=4 )  as 'Jackport Stake'"
                            + "FROM user_accounts where Acc_Trans_Type = 2 and  date(Acc_Date) between '" + fromDate + "' and '" + toDate + "'";

        dataQueryBalance = "select ifnull(sum(Acc_Amount),0) from user_accounts where date(Acc_Date) between '" + fromDate + "' and '" + toDate + "' ";

        
        dataQueryBetsByStatus = "select Play_Bet_Status,count(Play_Bet_ID) from player_bets where date(Play_Bet_Timestamp) between '" + fromDate + "' and '" + toDate + "' GROUP BY Play_Bet_Status";
        
        //sms,ussd,web,bet
        dataQueryBetsByChannel="select count(Play_Bet_Slip_ID),(select count(Play_Bet_Slip_ID) from player_bets where date(Play_Bet_Timestamp) between '" + fromDate + "' and '" + toDate + "' and Play_Bet_Channel = 2), "
                + "(select count(Play_Bet_Slip_ID) from player_bets where date(Play_Bet_Timestamp) between '" + fromDate + "' and '" + toDate + "' and Play_Bet_Channel = 3), "
                + "(select count(Play_Bet_Slip_ID) from player_bets where date(Play_Bet_Timestamp) between '" + fromDate + "' and '" + toDate + "' and Play_Bet_Channel = 4) "
                + "from player_bets where date(Play_Bet_Timestamp) between '" + fromDate + "' and '" + toDate + "' and Play_Bet_Channel = 1 " ;

        dataQueryBetsByBetType="select Play_Bet_Type, count(Play_Bet_Type) from player_bets  where date(Play_Bet_Timestamp) between '" + fromDate + "' and '" + toDate + "' and Play_Bet_Status in (201,202,203) GROUP BY Play_Bet_Type";
        
        dataQueryPlayersSummary="select count(distinct(refered)),count(distinct(referee)),(select count(id) from  player where date(registration_date) between '" + fromDate + "' and '" + toDate + "' and `status`=0 ) as 'Active Players', " 
                      + "(select count(id) from  player where date(registration_date) between '" + fromDate + "' and '" + toDate + "' and `status`=1) as 'Inactive Players' from  refferafriend where date(datecreated) between '" + fromDate + "' and '" + toDate + "' ";
        
        String financequeries=dataQueryDeposit+"#"+dataQueryWithdrawals+"#"+dataQueryBalance;
        collection=financequeries.split("#");

            try
            {
                conn = new DBManager().getDBConnection();
                stmt = conn.createStatement();
                
                for(int i=0;i<collection.length;i++)
                {
                    System.out.println(i+"financeQuery==="+collection[i]);
                    rs = stmt.executeQuery(collection[i]); 
                    
                    while (rs.next()) 
                    {
                        switch (i) 
                        {
                            case 0:
                               depositPlayers=rs.getString(1);
                               c2bDepositAccBal=String.valueOf((Math.round(Double.valueOf(rs.getString(2)).intValue())));
                            break;
                            case 1:
                               withdrawPlayers=rs.getString(1);
                               b2cwithdrawAccBal=String.valueOf((Math.round(Double.valueOf(rs.getString(2)).intValue())));
                               wallet2BetwithdrawAccBal=String.valueOf((Math.round(Double.valueOf(rs.getString(3)).intValue())));
                               wallet2GoldenraceBetwithdrawAccBal=String.valueOf((Math.round(Double.valueOf(rs.getString(4)).intValue())));
                               wallet2SlotGameBetwithdrawAccBal=String.valueOf((Math.round(Double.valueOf(rs.getString(5)).intValue())));
                               goldenRaceBetwinAccBal=String.valueOf((Math.round(Double.valueOf(rs.getString(6)).intValue())));
                               slotGameBetwinAccBal=String.valueOf((Math.round(Double.valueOf(rs.getString(7)).intValue())));
                               betsWonAccBal=String.valueOf((Math.round(Double.valueOf(rs.getString(8)).intValue())));
                               wallet2JackpotBetwithdrawAccBal=String.valueOf((Math.round(Double.valueOf(rs.getString(9)).intValue())));
                            break;
                            case 2:
                                playersRMBalance=String.valueOf((Math.round(Double.valueOf(rs.getString(1)).intValue())));
                            break;
                            default:
                            break;
                        }  
                    } 
                    
                    rs.close();
                }
                
                dataObj  = new JSONObject();
                dataObj.put("C2BDepositsValue", c2bDepositAccBal);
                dataObj.put("B2CWithdrawalValue", b2cwithdrawAccBal.replace("-", ""));
                dataObj.put("TotalBetStake", wallet2BetwithdrawAccBal.replace("-", ""));
                dataObj.put("TotalGoldenRaceBetStake", wallet2GoldenraceBetwithdrawAccBal.replace("-", ""));
                dataObj.put("SlotGamesBetStake", wallet2SlotGameBetwithdrawAccBal.replace("-", ""));
                dataObj.put("TotalJackPotBetStake", wallet2JackpotBetwithdrawAccBal.replace("-", ""));
                dataObj.put("TotalgoldenRaceBetwinAccBal", goldenRaceBetwinAccBal);
                dataObj.put("TotalSlotGameBetwinAccBal", slotGameBetwinAccBal);
                dataObj.put("TotalBetsWonAmount", betsWonAccBal);
                
                main.put("Finance_Summary", dataObj);
                
                
                String profitqueries=dataQueryTurnover+"#"+dataQueryOpenBetsTurnOver+"#"+dataQueryWinnings+"#"+dataQuerySettledBetsTurnOver+"#"+dataQueryWonBetsTurnOver;
                collection=profitqueries.split("#");
                for(int i=0;i<collection.length;i++)
                {
                   System.out.println(i+"profitQuery==="+collection[i]);
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
                               totalWinnings=String.valueOf((Math.round(Double.valueOf(rs.getString(1)).intValue())));;
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
                    rs.close();
                }
                
                
                dataObj  = new JSONObject();
                dataObj.put("TurnoverRM", totalTurnoverRM);
                dataObj.put("TotalWinnings", totalWinnings);
                
                GGR = Double.valueOf(totalTurnoverRM) ;
                double ngr_val =GGR - Double.valueOf(totalWinnings);
                NGR=ngr_val-(ngr_val*0.15);
                Profit=Double.valueOf(settledBetsTurnoverRM)-(Double.valueOf(totalWinnings));//-Double.valueOf(wonBetsTurnoverRM)*0.15
                
                
                dataObj.put("GGR", String.valueOf(GGR));
                dataObj.put("NGR", String.valueOf(NGR));
                
                if((int) Profit < 0)
                {
                    dataObj.put("Profit","0");
                    dataObj.put("Loss", String.valueOf(Profit*-1));
                }
                else
                {
                    dataObj.put("Profit",String.valueOf(Profit));
                    dataObj.put("Loss", "0");
                }
                
                
                main.put("Profit_Summary", dataObj);
                
                
                System.out.println("regQuery==="+registrations);
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
                int total_reg=Integer.valueOf(SMS_Reg)+Integer.valueOf(USSD_Reg)+Integer.valueOf(computer_Web_Reg)+Integer.valueOf(mobile_Web_Reg);
                dataObj.put("Total_Reg", String.valueOf(total_reg));
                
                main.put("Registration_Summary", dataObj);
                rs.close();
                
                
                
                System.out.println("betsByStatusQuery==="+dataQueryBetsByStatus);
                rs = stmt.executeQuery(dataQueryBetsByStatus); 
                while (rs.next()) 
                {
                    switch (rs.getInt(1)) 
                    {
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
                    //System.out.println("placedBets>>>"+placedBets+">>>"+wonBets+"<<<lostBets>>>"+lostBets+"<<<rejectedBets>>>"+rejectedBets+"<<<cancelledBets>>>"+cancelledBets);
                    settledBets = String.valueOf(Integer.valueOf(lostBets) + Integer.valueOf(wonBets));
                }
                rs.close();
                
                dataObj  = new JSONObject();
                dataObj.put("Open_Bets", placedBets);
                dataObj.put("Won_Bets", wonBets);
                dataObj.put("Lost_Bets", lostBets);
                dataObj.put("Rejected_Bets", rejectedBets);
                dataObj.put("Cancelled_Bets", cancelledBets);
                dataObj.put("Settled_Bets", settledBets);
                
                main.put("BetsByStatus_Summary", dataObj);
                
                
                
                
                System.out.println("betByChannelQuery==="+dataQueryBetsByChannel);
                rs = stmt.executeQuery(dataQueryBetsByChannel); 
                while (rs.next()) 
                {
                    
                    USSD_Bets=rs.getString(1);
                    SMS_Bets=rs.getString(2);
                    Computer_Web_Bets=rs.getString(3);
                    Mobile_Web_Bets=rs.getString(4);
                }
                dataObj  = new JSONObject();
                dataObj.put("SMS_Bets", SMS_Bets);
                dataObj.put("USSD_Bets", USSD_Bets);
                dataObj.put("Computer_Web_Bets", Computer_Web_Bets);
                dataObj.put("Mobile_Web_Bets", Mobile_Web_Bets);
                
                main.put("BetsByChannel_Summary", dataObj);
                rs.close();
                
                
                
                System.out.println("betByTypeQuery==="+dataQueryBetsByBetType);
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
                rs.close();
                
                System.out.println("dataQueryPlayersSummary==="+dataQueryPlayersSummary);
                rs = stmt.executeQuery(dataQueryPlayersSummary); 
                while (rs.next()) 
                {
                    referedPlayers=rs.getString(1);
                    refereesPlayers=rs.getString(2);
                    activePlayers=rs.getString(3);
                    inactivePlayers=rs.getString(4);
                    totalPlayers=String.valueOf(Integer.valueOf(activePlayers)+Integer.valueOf(inactivePlayers));    
                }
                dataObj  = new JSONObject();
                dataObj.put("ReferedPlayers", referedPlayers);
                dataObj.put("RefereesPlayers", refereesPlayers);
                dataObj.put("ActivePlayers", activePlayers);
                dataObj.put("InactivePlayers", inactivePlayers);
                dataObj.put("TotalPlayers", totalPlayers);
                
                main.put("PlayersSummary", dataObj);
                rs.close();
                
                dataArray.put(main);
              

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error MainDashBoardSummaryProcessor=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

        return dataArray;
    }
    
}
