/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Implimentation.BettingReportImplimentation;

import Database.DBManager;
import Utility.Utility;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author jac
 */
public class BettingReportImpl {
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    public BettingReportImpl()
    {
        
    }
    
    
    public JSONArray getBettingReportByBetslipID(String betSlipID)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String dataQuery = "";
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();
        JSONArray respoArray = new JSONArray();
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        int totalBetStake=0;
        int totalWinnings=0;

        try
        {

            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            dataQuery = "select Play_Bet_Timestamp, Play_Bet_Slip_ID, (case when Play_Bet_Status=200 then 'Pending'  when Play_Bet_Status=201 then 'Placed' when Play_Bet_Status=202 then 'Won' when Play_Bet_Status=203 then 'Lost' when Play_Bet_Status=204 then 'Rejected' when Play_Bet_Status=205 then 'Cancelled' when Play_Bet_Status=209 then 'Voided' end)as 'bet_status', "+
                        " Chan_Mode_Name , Play_Bet_Mobile,Play_Bet_Cash_Stake, Play_Bet_Bonus_Stake , Play_Bet_Possible_Winning, Play_Bet_Gross_Possible_Winning, " +
                        "Play_Bet_Group_ID, Play_Bet_Possible_BonusWinning,(Case when Play_Bet_Type=1 then 'Single Bet' when Play_Bet_Type=4 then 'Jackpot' when Play_Bet_Type=3 then 'Multi Bet' end),Play_Bet_BetType from player_bets ,channels_used where  Chan_Table_ID = play_bet_Channel  " +
                        "and play_Bet_Type in (0,1,2,3,4)  and Play_Bet_Status in (200,201, 202, 203, 204,205,209) and  player_bets.Play_Bet_Slip_ID='"+betSlipID+"' order by Play_Bet_Timestamp desc ";        

            System.out.println("getBettingReportByBetslipID==="+dataQuery);

            rs = stmt.executeQuery(dataQuery);

            while (rs.next())
            {
                dataObj  = new JSONObject();

                String betdate = sdf.format(rs.getTimestamp(1));
                String betslip_id = rs.getString(2);
                String betstatus = rs.getString(3);
                String betchannel = rs.getString(4);
                String betmobile = rs.getString(5);
                String betstake = rs.getString(6);//bet rm 
                String betbonusstake = rs.getString(7);//bet bm 
                String betgrossstake = rs.getString(8);//gross rm 
                String betpossiblewinning = rs.getString(9);//bet rm possible win
                String betgrosspossiblewinning = rs.getString(10);//bet rm possible win
                String betgroup_id = rs.getString(11);
                String betbonuspossiblewinning = rs.getString(12);//bet rm possible win
                String bettype = rs.getString(13);//bet type
                String game_bettype = rs.getString(14);//(prelive or live)


                String settled_rm="0.00",settled_bm="0.00",openbet_bm="0.00",ggr_rm="0.00",ngr_rm="0.00",ngrtax_rm="0.00",
                taxedngr_rm="0.00",bonusamountopen="0.00",bonusmoneysettled="0.00",openbet_rm="0.00",winamount_rm="0.00",winamount_bm="0.00",
                ggr_bm="0.00",ngr_bm="0.00",refund_rm="0.00",taxedwinamount_rm="0.00",refund_bm="0.00",winning_tax="0.00",ngr_tax="0.00";

                if (betstatus.equalsIgnoreCase("Placed")) 
                {
                    openbet_rm = String.valueOf(betstake); // open rm
                    settled_rm = "0.00"; // settled rm
                    winamount_rm =String.format("%.2f", Double.valueOf(betpossiblewinning));// "0.00"; // win rm
                    ggr_rm = "0.00"; // ggr rm
                    openbet_bm = String.valueOf(betbonusstake); // open bm
                    settled_bm = "0.00"; // settled bm
                    winamount_bm = "0.00"; // win bm
                    ggr_bm = "0.00"; // ggr bm
                    ngr_rm = "0.00"; // ngr 
                    refund_rm = "0.00"; // rm refund
                    refund_bm = "0.00"; // bm refund

                    totalBetStake+=Double.valueOf(betstake);
                } 
                else if (betstatus.equalsIgnoreCase("Won")) 
                {
                    openbet_rm = "0.00"; // open rm
                    settled_rm = String.valueOf(betstake); // settled rm

                    double win_tax=(Double.valueOf(betgrosspossiblewinning)-Double.valueOf(betpossiblewinning) );
                    double taxedamount_won=Double.valueOf(betgrosspossiblewinning);

                    taxedwinamount_rm=String.format("%.2f", taxedamount_won);
                    winning_tax=String.format("%.2f", win_tax);// win_tax rm
                    winamount_rm =String.format("%.2f", Double.valueOf(betpossiblewinning)); // win rm
                    ggr_rm = String.valueOf(Double.valueOf(betstake)); // ggr rm
                    openbet_bm = "0.00"; // open bm
                    settled_bm = String.valueOf(betbonusstake); // settled bm
                    winamount_bm = String.valueOf(betbonuspossiblewinning); // win bm
                    ggr_bm = String.valueOf(Double.valueOf(betbonusstake)); // ggr bm
                    refund_rm = "0.00"; // rm refund
                    refund_bm = "0.00"; // bm refund

                    totalWinnings+=(int) taxedamount_won;
                } 
                else if (betstatus.equalsIgnoreCase("Lost"))
                {
                    openbet_rm = "0.00"; // open rm
                    settled_rm = String.valueOf(betstake); // settled rm
                    winamount_rm = "0.00"; // win rm
                    ggr_rm = String.valueOf(settled_rm); // ggr rm
                    ngr_rm = betstake; // ngr 
                    openbet_bm = "0.00"; // open bm
                    settled_bm = String.valueOf(betbonusstake); // settled bm
                    winamount_bm = "0.00"; // win bm
                    ggr_bm = String.valueOf(betbonusstake); // ggr bm
                    refund_rm = "0.00"; // rm refund
                    refund_bm = "0.00"; // bm refund
                } 
                else if (betstatus.equalsIgnoreCase("Rejected"))
                {
                    openbet_rm = "0.00"; // open rm
                    settled_rm = "0.00"; // settled rm
                    winamount_rm = "0.00"; // win rm
                    ggr_rm = "0.00"; // ggr rm
                    openbet_bm = "0.00"; // open bm
                    settled_bm = "0.00"; // settled bm
                    winamount_bm = "0.00"; // win bm
                    ggr_bm = "0.00"; // ggr bm
                    ngr_rm = "0.00"; // ngr 
                    refund_rm = String.valueOf(betstake); // rm refund
                    refund_bm = String.valueOf(betbonusstake);  // bm refund
                } 
                else if (betstatus.equalsIgnoreCase("Cancelled")) 
                {
                    openbet_rm = "0.00"; // open rm
                    settled_rm = "0.00"; // settled rm
                    winamount_rm = "0.00"; // win rm
                    ggr_rm = "0.00"; // ggr rm
                    openbet_bm = "0.00"; // open bm
                    settled_bm = "0.00"; // settled bm
                    winamount_bm = "0.00"; // win bm
                    ggr_bm = "0.00"; // ggr bm
                    ngr_rm = "0.00"; // ngr 
                    refund_rm = String.valueOf(betstake); // rm refund
                    refund_bm = String.valueOf(betbonusstake);  // bm refund
                } 
                else if (betstatus.equalsIgnoreCase("Pending")) 
                {
                    openbet_rm = "0.00"; // open rm
                    settled_rm = String.valueOf(betstake); // settled rm
                    winamount_rm = "0.00"; // win rm
                    ggr_rm = "0.00"; // ggr rm
                    openbet_bm = "0.00"; // open bm
                    settled_bm = String.valueOf(betstake); // settled bm
                    winamount_bm = "0.00"; // win bm
                    ggr_bm = "0.00"; // ggr bm
                    ngr_rm = "0.00"; // ngr 
                    refund_rm = String.valueOf(betstake); // rm refund
                    refund_bm = String.valueOf(betbonusstake);  // bm refund
                }
                
                double exciseTaxMultiplierValue=1.075;
                double exciseTax=(Double.valueOf(betstake)- (Double.valueOf(betstake)/exciseTaxMultiplierValue));

                dataObj.put("BetDate", betdate);
                dataObj.put("BetSlipID", betslip_id);
                dataObj.put("BetStatus", betstatus);
                dataObj.put("BetType", bettype);
                dataObj.put("GameBetType", game_bettype);
                dataObj.put("BetChannel", betchannel);
                dataObj.put("BetMobile", betmobile);
                dataObj.put("GrossStake", betgrossstake);
                dataObj.put("RMOpen", openbet_rm);
                dataObj.put("RMSettled", settled_rm);
                dataObj.put("RMWinAmountTax", winning_tax);
                dataObj.put("RMWinAmount", winamount_rm);
                dataObj.put("RMTaxedWinAmount", taxedwinamount_rm);
                dataObj.put("GGRRM", ggr_rm);
                dataObj.put("NGRRM", ngr_rm);
                dataObj.put("NGRRMTax", ngr_tax);
                dataObj.put("TaxedNGRRM", taxedngr_rm);
                dataObj.put("RefundRM", refund_rm);
                dataObj.put("BMOpen", openbet_bm);
                dataObj.put("BMSettled", settled_bm);
                dataObj.put("BMWinAmount", winamount_bm);
                dataObj.put("GGRBM", ggr_bm);
                dataObj.put("RefundBM", refund_bm);
                dataObj.put("ExciseTax", exciseTax);

                dataArray.put(dataObj);
            }
            if(dataArray.length()==0)
            {
                dataObj  = new JSONObject();
                dataObj.put("BetDate", "0");
                dataObj.put("BetSlipID", "0");
                dataObj.put("BetStatus", "0");
                dataObj.put("BetType", "0");
                dataObj.put("GameBetType", "0");
                dataObj.put("BetChannel", "0");
                dataObj.put("BetMobile", "0");
                dataObj.put("GrossStake", "0");
                dataObj.put("RMOpen", "0");
                dataObj.put("RMSettled", "0");
                dataObj.put("RMWinAmountTax", "0");
                dataObj.put("RMWinAmount", "0");
                dataObj.put("RMTaxedWinAmount", "0");
                dataObj.put("GGRRM", "0");
                dataObj.put("NGRRM", "0");
                dataObj.put("NGRRMTax", "0");
                dataObj.put("TaxedNGRRM", "0");
                dataObj.put("RefundRM", "0");
                dataObj.put("BMOpen", "0");
                dataObj.put("BMSettled", "0");
                dataObj.put("BMWinAmount", "0");
                dataObj.put("GGRBM", "0");
                dataObj.put("RefundBM", "0");
                dataObj.put("ExciseTax", "0");
                dataArray.put(dataObj);
            }

            respoArray.put(dataArray);

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getBettingReportByBetslipID=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return respoArray;
    }
    
    
    
    public JSONArray getAllBettingReport(String fromDate,String toDate)
    {
        String res="0.00";
        String dataQuery = "";
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        try
        {
            dataQuery = "select Play_Bet_Timestamp, Play_Bet_Slip_ID, (case when Play_Bet_Status=200 then 'Pending'  when Play_Bet_Status=201 then 'Placed' when Play_Bet_Status=202 then 'Won' when Play_Bet_Status=203 then 'Lost' when Play_Bet_Status=204 then 'Rejected' when Play_Bet_Status=205 then 'Cancelled' when Play_Bet_Status=209 then 'Voided' end)as 'bet_status', "+
                        " Chan_Mode_Name , Play_Bet_Mobile,Play_Bet_Cash_Stake, Play_Bet_Bonus_Stake,Play_Bet_Gross_Stake , Play_Bet_Possible_Winning, Play_Bet_Gross_Possible_Winning, " +
                        "Play_Bet_Group_ID, Play_Bet_Possible_BonusWinning,(Case when Play_Bet_Type=1 then 'Single Bet' when Play_Bet_Type=4 then 'Jackpot' when Play_Bet_Type=3 then 'Multi Bet' end),Play_Bet_BetType from player_bets ,channels_used where  Chan_Table_ID = play_bet_Channel and  date(Play_Bet_Timestamp) between   '"+fromDate+"' and '"+toDate+"'  " +
                        "and play_Bet_Type in (0,1,2,3,4)  and Play_Bet_Status in (200,201, 202, 203, 204,205,209) order by Play_Bet_Timestamp desc ";
            
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(dataQuery);
            
            System.out.println("getBettingReport==="+dataQuery);

            rs = stmt.executeQuery(dataQuery);
            while (rs.next())
            {
                dataObj  = new JSONObject();
                String betdate = sdf.format(rs.getTimestamp(1));
                String betslip_id = rs.getString(2);
                String betstatus = rs.getString(3);
                String betchannel = rs.getString(4);
                String betmobile = rs.getString(5);
                String betstake = rs.getString(6);//bet rm 
                String betbonusstake = rs.getString(7);//bet bm 
                String betgrossstake = rs.getString(8);//gross rm 
                String betpossiblewinning = rs.getString(9);//bet rm possible win
                String betgrosspossiblewinning = rs.getString(10);//bet rm possible win
                String betgroup_id = rs.getString(11);
                String betbonuspossiblewinning = rs.getString(12);//bet rm possible win
                String bettype = rs.getString(13);//bet type
                String game_bettype = rs.getString(14);//(prelive or live)


                String settled_rm="0.00",settled_bm="0.00",openbet_bm="0.00",ggr_rm="0.00",ngr_rm="0.00",ngrtax_rm="0.00",taxedngr_rm="0.00",bonusamountopen="0.00",bonusmoneysettled="0.00",openbet_rm="0.00",winamount_rm="0.00",winamount_bm="0.00",
                ggr_bm="0.00",ngr_bm="0.00",refund_rm="0.00",taxedwinamount_rm="0.00",refund_bm="0.00",winning_tax="0.00",ngr_tax="0.00";
                if (betstatus.equalsIgnoreCase("Placed")) 
                {
                    openbet_rm = String.valueOf(betstake); // open rm
                    settled_rm = "0.00"; // settled rm
                    double win_tax=( Double.valueOf(betgrosspossiblewinning)-Double.valueOf(betpossiblewinning) ) ;
                    double taxedamount_won=Double.valueOf(betgrosspossiblewinning);

                    taxedwinamount_rm=String.format("%.2f", taxedamount_won);
                    winning_tax=String.format("%.2f", win_tax);// win_tax rm
                    winamount_rm =String.format("%.2f", Double.valueOf(betpossiblewinning)); // win rm
                    ggr_rm = "0.00"; // ggr rm
                    openbet_bm = String.valueOf(betbonusstake); // open bm
                    settled_bm = "0.00"; // settled bm
                    winamount_bm = "0.00"; // win bm
                    ggr_bm = "0.00"; // ggr bm
                    ngr_rm = "0.00"; // ngr 
                    refund_rm = "0.00"; // rm refund
                    refund_bm = "0.00"; // bm refund
                } 
                else if (betstatus.equalsIgnoreCase("Won")) 
                {
                    openbet_rm = "0.00"; // open rm
                    settled_rm = String.valueOf(betstake); // settled rm

                    double win_tax=( Double.valueOf(betgrosspossiblewinning)-Double.valueOf(betpossiblewinning) ) ;
                    double taxedamount_won=Double.valueOf(betgrosspossiblewinning);

                    taxedwinamount_rm=String.format("%.2f", taxedamount_won);
                    winning_tax=String.format("%.2f", win_tax);// win_tax rm
                    winamount_rm =String.format("%.2f", Double.valueOf(betpossiblewinning)); // win rm
                    ggr_rm = String.valueOf(Double.valueOf(betstake)); // ggr rm
                    openbet_bm = "0.00"; // open bm
                    settled_bm = String.valueOf(betbonusstake); // settled bm
                    winamount_bm = String.valueOf(betbonuspossiblewinning); // win bm
                    ggr_bm = String.valueOf(Double.valueOf(betbonusstake)); // ggr bm
                    refund_rm = "0.00"; // rm refund
                    refund_bm = "0.00"; // bm refund
                } 
                else if (betstatus.equalsIgnoreCase("Lost"))
                {
                    openbet_rm = "0.00"; // open rm
                    settled_rm = String.valueOf(betstake); // settled rm
                    double win_tax=( Double.valueOf(betgrosspossiblewinning)-Double.valueOf(betpossiblewinning) ) ;
                    double taxedamount_won=Double.valueOf(betgrosspossiblewinning);

                    taxedwinamount_rm=String.format("%.2f", taxedamount_won);
                    winning_tax=String.format("%.2f", win_tax);// win_tax rm
                    winamount_rm =String.format("%.2f", Double.valueOf(betpossiblewinning)); // win rm
                    ggr_rm = String.valueOf(settled_rm); // ggr rm
                    ngr_rm = betstake; // ngr 
                    openbet_bm = "0.00"; // open bm
                    settled_bm = String.valueOf(betbonusstake); // settled bm
                    winamount_bm = "0.00"; // win bm
                    ggr_bm = String.valueOf(betbonusstake); // ggr bm
                    refund_rm = "0.00"; // rm refund
                    refund_bm = "0.00"; // bm refund
                } 
                else if (betstatus.equalsIgnoreCase("Rejected"))
                {
                    openbet_rm = "0.00"; // open rm
                    settled_rm = "0.00"; // settled rm
                    winamount_rm = "0.00"; // win rm
                    ggr_rm = "0.00"; // ggr rm
                    openbet_bm = "0.00"; // open bm
                    settled_bm = "0.00"; // settled bm
                    winamount_bm = "0.00"; // win bm
                    ggr_bm = "0.00"; // ggr bm
                    ngr_rm = "0.00"; // ngr 
                    refund_rm = String.valueOf(betstake); // rm refund
                    refund_bm = String.valueOf(betbonusstake);  // bm refund
                } 
                else if (betstatus.equalsIgnoreCase("Cancelled")) 
                {
                    openbet_rm = "0.00"; // open rm
                    settled_rm = "0.00"; // settled rm
                    winamount_rm = "0.00"; // win rm
                    ggr_rm = "0.00"; // ggr rm
                    openbet_bm = "0.00"; // open bm
                    settled_bm = "0.00"; // settled bm
                    winamount_bm = "0.00"; // win bm
                    ggr_bm = "0.00"; // ggr bm
                    ngr_rm = "0.00"; // ngr 
                    refund_rm = String.valueOf(betstake); // rm refund
                    refund_bm = String.valueOf(betbonusstake);  // bm refund
                } 
                else if (betstatus.equalsIgnoreCase("Pending")) 
                {
                    openbet_rm = "0.00"; // open rm
                    settled_rm = String.valueOf(betstake); // settled rm
                    winamount_rm = "0.00"; // win rm
                    ggr_rm = "0.00"; // ggr rm
                    openbet_bm = "0.00"; // open bm
                    settled_bm = String.valueOf(betstake); // settled bm
                    winamount_bm = "0.00"; // win bm
                    ggr_bm = "0.00"; // ggr bm
                    ngr_rm = "0.00"; // ngr 
                    refund_rm = String.valueOf(betstake); // rm refund
                    refund_bm = String.valueOf(betbonusstake);  // bm refund
                }
                
                double exciseTaxMultiplierValue=1.075;
                double exciseTax=(Double.valueOf(betstake)- (Double.valueOf(betstake)/exciseTaxMultiplierValue));

                dataObj.put("BetDate", betdate);
                dataObj.put("BetSlipID", betslip_id);
                dataObj.put("BetStatus", betstatus);
                dataObj.put("BetType", bettype);
                dataObj.put("GameBetType", game_bettype);
                dataObj.put("BetChannel", betchannel);
                dataObj.put("BetMobile", betmobile);
                dataObj.put("GrossStake", betgrossstake);
                dataObj.put("RMOpen", openbet_rm);
                dataObj.put("RMSettled", settled_rm);
                dataObj.put("RMWinAmountTax", winning_tax);
                dataObj.put("RMWinAmount", winamount_rm);
                dataObj.put("RMTaxedWinAmount", taxedwinamount_rm);
                dataObj.put("GGRRM", ggr_rm);
                dataObj.put("NGRRM", ngr_rm);
                dataObj.put("NGRRMTax", ngr_tax);
                dataObj.put("TaxedNGRRM", taxedngr_rm);
                dataObj.put("RefundRM", refund_rm);
                dataObj.put("BMOpen", openbet_bm);
                dataObj.put("BMSettled", settled_bm);
                dataObj.put("BMWinAmount", winamount_bm);
                dataObj.put("GGRBM", ggr_bm);
                dataObj.put("RefundBM", refund_bm);
                dataObj.put("ExciseTax", exciseTax);

                dataArray.put(dataObj);
            }

            if(dataArray.length()==0)
            {
                dataObj  = new JSONObject();
                dataObj.put("BetDate", "0");
                dataObj.put("BetSlipID", "0");
                dataObj.put("BetStatus", "0");
                dataObj.put("BetType", "0");
                dataObj.put("GameBetType", "0");
                dataObj.put("BetChannel", "0");
                dataObj.put("BetMobile", "0");
                dataObj.put("RMOpen", "0");
                dataObj.put("GrossStake", "0");
                dataObj.put("RMSettled", "0");
                dataObj.put("RMWinAmountTax", "0");
                dataObj.put("RMWinAmount", "0");
                dataObj.put("RMTaxedWinAmount", "0");
                dataObj.put("GGRRM", "0");
                dataObj.put("NGRRM", "0");
                dataObj.put("NGRRMTax", "0");
                dataObj.put("TaxedNGRRM", "0");
                dataObj.put("RefundRM", "0");
                dataObj.put("BMOpen", "0");
                dataObj.put("BMSettled", "0");
                dataObj.put("BMWinAmount", "0");
                dataObj.put("GGRBM", "0");
                dataObj.put("RefundBM", "0");
                dataArray.put(dataObj);
            }


        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getAllBettingReport=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
        
        
        
        
    public JSONArray getPlayerBettingReport(String fromDate,String toDate,String mobile)
    {
        String res="0.00";
        String dataQuery = "";
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();
        JSONArray respoArray = new JSONArray();
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        int totalBetStake=0;
        int totalWinnings=0;
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;

        try
        {
            if(fromDate.equals("")&& fromDate.equals(""))
            {
                dataQuery = "select Play_Bet_Timestamp, Play_Bet_Slip_ID, (case when Play_Bet_Status=200 then 'Pending'  when Play_Bet_Status=201 then 'Placed' when Play_Bet_Status=202 then 'Won' when Play_Bet_Status=203 then 'Lost' when Play_Bet_Status=204 then 'Rejected' when Play_Bet_Status=205 then 'Cancelled' when Play_Bet_Status=209 then 'Voided' end)as 'bet_status', "+
                        " Chan_Mode_Name , Play_Bet_Mobile,Play_Bet_Cash_Stake, Play_Bet_Bonus_Stake , Play_Bet_Possible_Winning, Play_Bet_Gross_Possible_Winning, " +
                        "Play_Bet_Group_ID, Play_Bet_Possible_BonusWinning,(Case when Play_Bet_Type=1 then 'Single Bet' when Play_Bet_Type=4 then 'Jackpot' when Play_Bet_Type=3 then 'Multi Bet' end),Play_Bet_BetType from player_bets ,channels_used where  Chan_Table_ID = play_bet_Channel  " +
                        "and play_Bet_Type in (0,1,2,3,4)  and Play_Bet_Status in (200,201, 202, 203, 204,205,209) and  player_bets.Play_Bet_Mobile='"+mobile+"' order by Play_Bet_Timestamp desc ";        
            }
            else
            {
                dataQuery = "select Play_Bet_Timestamp, Play_Bet_Slip_ID, (case when Play_Bet_Status=200 then 'Pending'  when Play_Bet_Status=201 then 'Placed' when Play_Bet_Status=202 then 'Won' when Play_Bet_Status=203 then 'Lost' when Play_Bet_Status=204 then 'Rejected' when Play_Bet_Status=205 then 'Cancelled'  when Play_Bet_Status=209 then 'Voided' end)as 'bet_status', "+
                        " Chan_Mode_Name , Play_Bet_Mobile, Play_Bet_Stake, Play_Bet_Bonus_Stake , Play_Bet_Possible_Winning, Play_Bet_Gross_Possible_Winning, " +
                        "Play_Bet_Group_ID, Play_Bet_Possible_BonusWinning,(Case when Play_Bet_Type=1 then 'Single Bet' when Play_Bet_Type=4 then 'Jackpot' when Play_Bet_Type=3 then 'Multi Bet' end),Play_Bet_BetType from player_bets ,channels_used where  Chan_Table_ID = play_bet_Channel and  date(Play_Bet_Timestamp) between   '"+fromDate+"' and '"+toDate+"'  " +
                        "and play_Bet_Type in (0,1,2,3,4)  and Play_Bet_Status in (200,201, 202, 203, 204,205,209) and  player_bets.Play_Bet_Mobile='"+mobile+"' order by Play_Bet_Timestamp desc ";                
            }
            System.out.println("getPlayerBettingReport==="+dataQuery);

            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(dataQuery);
            
            while (rs.next())
            {
                dataObj  = new JSONObject();

                String betdate = sdf.format(rs.getTimestamp(1));
                String betslip_id = rs.getString(2);
                String betstatus = rs.getString(3);
                String betchannel = rs.getString(4);
                String betmobile = rs.getString(5);
                String betstake = rs.getString(6);//bet rm 
                String betbonusstake = rs.getString(7);//bet bm 
                String betgrossstake = rs.getString(8);//gross rm 
                String betpossiblewinning = rs.getString(9);//bet rm possible win
                String betgrosspossiblewinning = rs.getString(10);//bet rm possible win
                String betgroup_id = rs.getString(11);
                String betbonuspossiblewinning = rs.getString(12);//bet rm possible win
                String bettype = rs.getString(13);//bet type
                String game_bettype = rs.getString(14);//(prelive or live)


                String settled_rm="0.00",settled_bm="0.00",openbet_bm="0.00",ggr_rm="0.00",ngr_rm="0.00",ngrtax_rm="0.00",taxedngr_rm="0.00",bonusamountopen="0.00",bonusmoneysettled="0.00",openbet_rm="0.00",winamount_rm="0.00",winamount_bm="0.00",
                ggr_bm="0.00",ngr_bm="0.00",refund_rm="0.00",taxedwinamount_rm="0.00",refund_bm="0.00",winning_tax="0.00",ngr_tax="0.00";

                if (betstatus.equalsIgnoreCase("Placed")) 
                {
                    openbet_rm = String.valueOf(betstake); // open rm
                    settled_rm = "0.00"; // settled rm
                    winamount_rm =String.format("%.2f", Double.valueOf(betpossiblewinning));// "0.00"; // win rm
                    ggr_rm = "0.00"; // ggr rm
                    openbet_bm = String.valueOf(betbonusstake); // open bm
                    settled_bm = "0.00"; // settled bm
                    winamount_bm = "0.00"; // win bm
                    ggr_bm = "0.00"; // ggr bm
                    ngr_rm = "0.00"; // ngr 
                    refund_rm = "0.00"; // rm refund
                    refund_bm = "0.00"; // bm refund

                    totalBetStake+=Double.valueOf(betstake);
                } 
                else if (betstatus.equalsIgnoreCase("Won")) 
                {
                    openbet_rm = "0.00"; // open rm
                    settled_rm = String.valueOf(betstake); // settled rm

                    double win_tax=( Double.valueOf(betgrosspossiblewinning)-Double.valueOf(betpossiblewinning) ) ;
                    double taxedamount_won=Double.valueOf(betgrosspossiblewinning);

                    taxedwinamount_rm=String.format("%.2f", taxedamount_won);
                    winning_tax=String.format("%.2f", win_tax);// win_tax rm
                    winamount_rm =String.format("%.2f", Double.valueOf(betpossiblewinning)); // win rm
                    ggr_rm = String.valueOf(Double.valueOf(betstake)); // ggr rm
                    openbet_bm = "0.00"; // open bm
                    settled_bm = String.valueOf(betbonusstake); // settled bm
                    winamount_bm = String.valueOf(betbonuspossiblewinning); // win bm
                    ggr_bm = String.valueOf(Double.valueOf(betbonusstake)); // ggr bm
                    refund_rm = "0.00"; // rm refund
                    refund_bm = "0.00"; // bm refund

                    totalWinnings+=(int) taxedamount_won;
                } 
                else if (betstatus.equalsIgnoreCase("Lost"))
                {
                    openbet_rm = "0.00"; // open rm
                    settled_rm = String.valueOf(betstake); // settled rm
                    winamount_rm = "0.00"; // win rm
                    ggr_rm = String.valueOf(settled_rm); // ggr rm
                    ngr_rm = betstake; // ngr 
                    openbet_bm = "0.00"; // open bm
                    settled_bm = String.valueOf(betbonusstake); // settled bm
                    winamount_bm = "0.00"; // win bm
                    ggr_bm = String.valueOf(betbonusstake); // ggr bm
                    refund_rm = "0.00"; // rm refund
                    refund_bm = "0.00"; // bm refund
                } 
                else if (betstatus.equalsIgnoreCase("Rejected"))
                {
                    openbet_rm = "0.00"; // open rm
                    settled_rm = "0.00"; // settled rm
                    winamount_rm = "0.00"; // win rm
                    ggr_rm = "0.00"; // ggr rm
                    openbet_bm = "0.00"; // open bm
                    settled_bm = "0.00"; // settled bm
                    winamount_bm = "0.00"; // win bm
                    ggr_bm = "0.00"; // ggr bm
                    ngr_rm = "0.00"; // ngr 
                    refund_rm = String.valueOf(betstake); // rm refund
                    refund_bm = String.valueOf(betbonusstake);  // bm refund
                } 
                else if (betstatus.equalsIgnoreCase("Cancelled")) 
                {
                    openbet_rm = "0.00"; // open rm
                    settled_rm = "0.00"; // settled rm
                    winamount_rm = "0.00"; // win rm
                    ggr_rm = "0.00"; // ggr rm
                    openbet_bm = "0.00"; // open bm
                    settled_bm = "0.00"; // settled bm
                    winamount_bm = "0.00"; // win bm
                    ggr_bm = "0.00"; // ggr bm
                    ngr_rm = "0.00"; // ngr 
                    refund_rm = String.valueOf(betstake); // rm refund
                    refund_bm = String.valueOf(betbonusstake);  // bm refund
                } 
                else if (betstatus.equalsIgnoreCase("Pending")) 
                {
                    openbet_rm = "0.00"; // open rm
                    settled_rm = String.valueOf(betstake); // settled rm
                    winamount_rm = "0.00"; // win rm
                    ggr_rm = "0.00"; // ggr rm
                    openbet_bm = "0.00"; // open bm
                    settled_bm = String.valueOf(betstake); // settled bm
                    winamount_bm = "0.00"; // win bm
                    ggr_bm = "0.00"; // ggr bm
                    ngr_rm = "0.00"; // ngr 
                    refund_rm = String.valueOf(betstake); // rm refund
                    refund_bm = String.valueOf(betbonusstake);  // bm refund
                }
                
                double exciseTaxMultiplierValue=1.075;
                double exciseTax=(Double.valueOf(betstake)- (Double.valueOf(betstake)/exciseTaxMultiplierValue));

                dataObj.put("BetDate", betdate);
                dataObj.put("BetSlipID", betslip_id);
                dataObj.put("BetStatus", betstatus);
                dataObj.put("BetType", bettype);
                dataObj.put("GameBetType", game_bettype);
                dataObj.put("BetChannel", betchannel);
                dataObj.put("BetMobile", betmobile);
                dataObj.put("GrossStake", betgrossstake);
                dataObj.put("RMOpen", openbet_rm);
                dataObj.put("RMSettled", settled_rm);
                dataObj.put("RMWinAmountTax", winning_tax);
                dataObj.put("RMWinAmount", winamount_rm);
                dataObj.put("RMTaxedWinAmount", taxedwinamount_rm);
                dataObj.put("GGRRM", ggr_rm);
                dataObj.put("NGRRM", ngr_rm);
                dataObj.put("NGRRMTax", ngr_tax);
                dataObj.put("TaxedNGRRM", taxedngr_rm);
                dataObj.put("RefundRM", refund_rm);
                dataObj.put("BMOpen", openbet_bm);
                dataObj.put("BMSettled", settled_bm);
                dataObj.put("BMWinAmount", winamount_bm);
                dataObj.put("GGRBM", ggr_bm);
                dataObj.put("RefundBM", refund_bm);
                dataObj.put("ExciseTax", exciseTax);

                dataArray.put(dataObj);
            }
            if(dataArray.length()==0)
            {
                dataObj  = new JSONObject();
                /*dataObj.put("BetDate", "0");
                dataObj.put("BetSlipID", "0");
                dataObj.put("BetStatus", "0");
                dataObj.put("BetType", "0");
                dataObj.put("GameBetType", "0");
                dataObj.put("BetChannel", "0");
                dataObj.put("BetMobile", "0");
                dataObj.put("RMOpen", "0");
                dataObj.put("RMSettled", "0");
                dataObj.put("RMWinAmountTax", "0");
                dataObj.put("RMWinAmount", "0");
                dataObj.put("RMTaxedWinAmount", "0");
                dataObj.put("GGRRM", "0");
                dataObj.put("NGRRM", "0");
                dataObj.put("NGRRMTax", "0");
                dataObj.put("TaxedNGRRM", "0");
                dataObj.put("RefundRM", "0");
                dataObj.put("BMOpen", "0");
                dataObj.put("BMSettled", "0");
                dataObj.put("BMWinAmount", "0");
                dataObj.put("GGRBM", "0");
                dataObj.put("RefundBM", "0");*/
                dataArray.put(dataObj);
            }


            JSONObject dataObj1  = new JSONObject();
            JSONArray dataArray1 = new JSONArray();
            String query1="select ifnull(sum(Acc_Amount),0),(select ifnull(sum(Acc_Bonus_Amount),0) from user_accounts where Acc_Mobile='"+mobile+"' and Acc_Bonus_Status=1 and Acc_Trans_Type in (1,4,7)),"
                        + "(select ifnull(sum(Acc_Bonus_Amount),0) from user_accounts where Acc_Mobile='"+mobile+"' and Acc_Bonus_Status=1 and Acc_Trans_Type=9),"
                        + "(select date(registration_date) from player where msisdn='"+mobile+"') as 'reg date' ,(select ifnull(sum(Acc_Amount),0) from user_accounts where Acc_Trans_Type=4  and Acc_Mobile='"+mobile+"' ) as 'total betstake' ," +
                          "(select ifnull(sum(Acc_Amount),0) from user_accounts where Acc_Mpesa_Trans_No like 'BET_WIN%' and Acc_Trans_Type in (3,9) and Acc_Mobile='"+mobile+"' ) as 'total win' from user_accounts where Acc_Mobile='"+mobile+"' ";// group by Acc_Mobile
            System.out.println("getPlayerAcc==="+query1);
            rs = stmt.executeQuery(query1);
            while (rs.next())
            {
                dataObj1.put("Balance", String.format("%.2f", Double.valueOf(rs.getString(1))));
                dataObj1.put("BonusBalance", rs.getString(2));
                dataObj1.put("RefferalBonusBalance", rs.getString(3));
                dataObj1.put("RegistrationDate", rs.getString(4));
                dataObj1.put("TotalBetStake", rs.getString(5).replace("-", "") );//String.format("%.2f", Double.valueOf(rs.getString(2)))
                dataObj1.put("TotalWinnings", rs.getString(6));//String.format("%.2f", Double.valueOf(rs.getString(3)))

                dataArray1.put(dataObj1);

            }

            respoArray.put(dataArray);
            respoArray.put(dataArray1);

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getPlayerBettingReport=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return respoArray;
    }



    public JSONArray getPlayerBettingReportByMobile(String mobile)
    {

        String res="0.00";
        String dataQuery = "";
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();
        JSONArray respoArray = new JSONArray();
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        int totalBetStake=0;
        int totalWinnings=0;
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;

        try
        {
            dataQuery = "select Play_Bet_Timestamp, Play_Bet_Slip_ID, (case when Play_Bet_Status=200 then 'Pending'  when Play_Bet_Status=201 then 'Placed' when Play_Bet_Status=202 then 'Won' when Play_Bet_Status=203 then 'Lost' when Play_Bet_Status=204 then 'Rejected' when Play_Bet_Status=205 then 'Cancelled'  when Play_Bet_Status=209 then 'Voided' end)as 'bet_status', "+
                     " Chan_Mode_Name , Play_Bet_Mobile,Play_Bet_Cash_Stake, Play_Bet_Bonus_Stake , Play_Bet_Possible_Winning, Play_Bet_Gross_Possible_Winning, " +
                     "Play_Bet_Group_ID, Play_Bet_Possible_BonusWinning,(Case when Play_Bet_Type=1 then 'Single Bet' when Play_Bet_Type=4 then 'Jackpot' when Play_Bet_Type=3 then 'Multi Bet' end),Play_Bet_BetType from player_bets ,channels_used where  Chan_Table_ID = play_bet_Channel  " +
                     "and play_Bet_Type in (0,1,2,3,4)  and Play_Bet_Status in (200,201, 202, 203, 204,205,209) and  player_bets.Play_Bet_Mobile='"+mobile+"' order by Play_Bet_Timestamp desc ";        
            System.out.println("getPlayerBettingReportByMobile==="+dataQuery);

            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(dataQuery);

            while (rs.next())
            {
                dataObj  = new JSONObject();

                String betdate = sdf.format(rs.getTimestamp(1));
                String betslip_id = rs.getString(2);
                String betstatus = rs.getString(3);
                String betchannel = rs.getString(4);
                String betmobile = rs.getString(5);
                String betstake = rs.getString(6);//bet rm 
                String betbonusstake = rs.getString(7);//bet bm 
                String betgrossstake = rs.getString(8);//gross rm 
                String betpossiblewinning = rs.getString(9);//bet rm possible win
                String betgrosspossiblewinning = rs.getString(10);//bet rm possible win
                String betgroup_id = rs.getString(11);
                String betbonuspossiblewinning = rs.getString(12);//bet rm possible win
                String bettype = rs.getString(13);//bet type
                String game_bettype = rs.getString(14);//(prelive or live)


                String settled_rm="0.00",settled_bm="0.00",openbet_bm="0.00",ggr_rm="0.00",ngr_rm="0.00",ngrtax_rm="0.00",taxedngr_rm="0.00",bonusamountopen="0.00",bonusmoneysettled="0.00",openbet_rm="0.00",winamount_rm="0.00",winamount_bm="0.00",
                ggr_bm="0.00",ngr_bm="0.00",refund_rm="0.00",taxedwinamount_rm="0.00",refund_bm="0.00",winning_tax="0.00",ngr_tax="0.00";

                if (betstatus.equalsIgnoreCase("Placed")) 
                {
                    openbet_rm = String.valueOf(betstake); // open rm
                    settled_rm = "0.00"; // settled rm
                    winamount_rm =String.format("%.2f", Double.valueOf(betpossiblewinning));// "0.00"; // win rm
                    ggr_rm = "0.00"; // ggr rm
                    openbet_bm = String.valueOf(betbonusstake); // open bm
                    settled_bm = "0.00"; // settled bm
                    winamount_bm = "0.00"; // win bm
                    ggr_bm = "0.00"; // ggr bm
                    ngr_rm = "0.00"; // ngr 
                    refund_rm = "0.00"; // rm refund
                    refund_bm = "0.00"; // bm refund

                    totalBetStake+=Double.valueOf(betstake);
                } 
                else if (betstatus.equalsIgnoreCase("Won")) 
                {
                    openbet_rm = "0.00"; // open rm
                    settled_rm = String.valueOf(betstake); // settled rm

                    double win_tax=( Double.valueOf(betgrosspossiblewinning)-Double.valueOf(betpossiblewinning) ) ;
                    double taxedamount_won=Double.valueOf(betgrosspossiblewinning);

                    taxedwinamount_rm=String.format("%.2f", taxedamount_won);
                    winning_tax=String.format("%.2f", win_tax);// win_tax rm
                    winamount_rm =String.format("%.2f", Double.valueOf(betpossiblewinning)); // win rm
                    ggr_rm = String.valueOf(Double.valueOf(betstake)); // ggr rm
                    openbet_bm = "0.00"; // open bm
                    settled_bm = String.valueOf(betbonusstake); // settled bm
                    winamount_bm = String.valueOf(betbonuspossiblewinning); // win bm
                    ggr_bm = String.valueOf(Double.valueOf(betbonusstake)); // ggr bm
                    refund_rm = "0.00"; // rm refund
                    refund_bm = "0.00"; // bm refund

                    totalWinnings+=(int) taxedamount_won;
                } 
                else if (betstatus.equalsIgnoreCase("Lost"))
                {
                    openbet_rm = "0.00"; // open rm
                    settled_rm = String.valueOf(betstake); // settled rm
                    winamount_rm = "0.00"; // win rm
                    ggr_rm = String.valueOf(settled_rm); // ggr rm
                    ngr_rm = betstake; // ngr 
                    openbet_bm = "0.00"; // open bm
                    settled_bm = String.valueOf(betbonusstake); // settled bm
                    winamount_bm = "0.00"; // win bm
                    ggr_bm = String.valueOf(betbonusstake); // ggr bm
                    refund_rm = "0.00"; // rm refund
                    refund_bm = "0.00"; // bm refund
                } 
                else if (betstatus.equalsIgnoreCase("Rejected"))
                {
                    openbet_rm = "0.00"; // open rm
                    settled_rm = "0.00"; // settled rm
                    winamount_rm = "0.00"; // win rm
                    ggr_rm = "0.00"; // ggr rm
                    openbet_bm = "0.00"; // open bm
                    settled_bm = "0.00"; // settled bm
                    winamount_bm = "0.00"; // win bm
                    ggr_bm = "0.00"; // ggr bm
                    ngr_rm = "0.00"; // ngr 
                    refund_rm = String.valueOf(betstake); // rm refund
                    refund_bm = String.valueOf(betbonusstake);  // bm refund
                } 
                else if (betstatus.equalsIgnoreCase("Cancelled")) 
                {
                    openbet_rm = "0.00"; // open rm
                    settled_rm = "0.00"; // settled rm
                    winamount_rm = "0.00"; // win rm
                    ggr_rm = "0.00"; // ggr rm
                    openbet_bm = "0.00"; // open bm
                    settled_bm = "0.00"; // settled bm
                    winamount_bm = "0.00"; // win bm
                    ggr_bm = "0.00"; // ggr bm
                    ngr_rm = "0.00"; // ngr 
                    refund_rm = String.valueOf(betstake); // rm refund
                    refund_bm = String.valueOf(betbonusstake);  // bm refund
                } 
                else if (betstatus.equalsIgnoreCase("Pending")) 
                {
                    openbet_rm = "0.00"; // open rm
                    settled_rm = String.valueOf(betstake); // settled rm
                    winamount_rm = "0.00"; // win rm
                    ggr_rm = "0.00"; // ggr rm
                    openbet_bm = "0.00"; // open bm
                    settled_bm = String.valueOf(betstake); // settled bm
                    winamount_bm = "0.00"; // win bm
                    ggr_bm = "0.00"; // ggr bm
                    ngr_rm = "0.00"; // ngr 
                    refund_rm = String.valueOf(betstake); // rm refund
                    refund_bm = String.valueOf(betbonusstake);  // bm refund
                }
                
                double exciseTaxMultiplierValue=1.075;
                double exciseTax=(Double.valueOf(betstake)- (Double.valueOf(betstake)/exciseTaxMultiplierValue));

                dataObj.put("BetDate", betdate);
                dataObj.put("BetSlipID", betslip_id);
                dataObj.put("BetStatus", betstatus);
                dataObj.put("BetType", bettype);
                dataObj.put("GameBetType", game_bettype);
                dataObj.put("BetChannel", betchannel);
                dataObj.put("BetMobile", betmobile);
                dataObj.put("GrossStake", betgrossstake);
                dataObj.put("RMOpen", openbet_rm);
                dataObj.put("RMSettled", settled_rm);
                dataObj.put("RMWinAmountTax", winning_tax);
                dataObj.put("RMWinAmount", winamount_rm);
                dataObj.put("RMTaxedWinAmount", taxedwinamount_rm);
                dataObj.put("GGRRM", ggr_rm);
                dataObj.put("NGRRM", ngr_rm);
                dataObj.put("NGRRMTax", ngr_tax);
                dataObj.put("TaxedNGRRM", taxedngr_rm);
                dataObj.put("RefundRM", refund_rm);
                dataObj.put("BMOpen", openbet_bm);
                dataObj.put("BMSettled", settled_bm);
                dataObj.put("BMWinAmount", winamount_bm);
                dataObj.put("GGRBM", ggr_bm);
                dataObj.put("RefundBM", refund_bm);
                dataObj.put("ExciseTax", exciseTax);

                dataArray.put(dataObj);
            }
            if(dataArray.length()==0)
            {
                dataObj  = new JSONObject();
                dataObj.put("BetDate", "0");
                dataObj.put("BetSlipID", "0");
                dataObj.put("BetStatus", "0");
                dataObj.put("BetType", "0");
                dataObj.put("GameBetType", "0");
                dataObj.put("BetChannel", "0");
                dataObj.put("BetMobile", "0");
                dataObj.put("GrossStake", "0");
                dataObj.put("RMOpen", "0");
                dataObj.put("RMSettled", "0");
                dataObj.put("RMWinAmountTax", "0");
                dataObj.put("RMWinAmount", "0");
                dataObj.put("RMTaxedWinAmount", "0");
                dataObj.put("GGRRM", "0");
                dataObj.put("NGRRM", "0");
                dataObj.put("NGRRMTax", "0");
                dataObj.put("TaxedNGRRM", "0");
                dataObj.put("RefundRM", "0");
                dataObj.put("BMOpen", "0");
                dataObj.put("BMSettled", "0");
                dataObj.put("BMWinAmount", "0");
                dataObj.put("GGRBM", "0");
                dataObj.put("RefundBM", "0");
                dataObj.put("ExciseTax", "0");
                dataArray.put(dataObj);
            }

            respoArray.put(dataArray);
        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getPlayerBettingReport=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return respoArray;
    }
    
    
}
