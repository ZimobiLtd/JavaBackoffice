/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Implimentation.BCLBImplimentation;

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
public class BCLBReportImpl {
    
    public BCLBReportImpl()
    {
        
    }
    
    
    public JSONArray getBCLBReport(String datefrom,String dateto)
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
                dataQuery ="select ifnull(sum(Play_Bet_Cash_Stake),0) as 'betstakes' ,count(Play_Bet_ID) ,"
                         + "(select ifnull(sum(Play_Bet_Possible_Winning),0) from player_bets where DATE(Play_Bet_Timestamp) ='"+date+"' and Play_Bet_Status  in (202) and  Play_Bet_Cash_Stake > 0) as 'payouts' " 
                         + "from player_bets where DATE(Play_Bet_Timestamp) =  '"+date+"'  and Play_Bet_Status in (202,203) and  Play_Bet_Cash_Stake > 0 ";
                System.out.println("getBCLBReport==="+dataQuery);

                rs = stmt.executeQuery(dataQuery);
                while (rs.next())
                {
                    String rptdate = date;
                    String totalStake = rs.getString(1);//Deposits
                    String tickets = rs.getString(2);
                    String payouts = rs.getString(3);

                    String revenue = String .format("%.2f",(Double.valueOf(totalStake)-Double.valueOf(payouts)));//Revenue
                    double betting_tax=0.15*Double.valueOf(revenue);
                    String bettingTax = String.format("%.2f", betting_tax);

                    dataObj.put("Report_Date", rptdate);//Date
                    dataObj.put("Tickets", tickets);//Bets
                    dataObj.put("TotalSales", totalStake);//Deposits
                    dataObj.put("NetSales", totalStake);//Total Revenue
                    dataObj.put("GGR", revenue);//GGR(Net Result)
                    dataObj.put("BettingTax", bettingTax);
                    //dataObj.put("WitholdingTax", "0.00");
                }
                double []taxData=getTaxData(conn,stmt,date);
                 String totalPayout = String.format("%.2f", taxData[0]);
                String withholdingtax = String.format("%.2f", taxData[1]);
                dataObj.put("Payout", totalPayout);
                dataObj.put("WitholdingTax", withholdingtax);

                dataArray.put(dataObj);
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
        
        
        
    public double[] getTaxData(Connection conn,Statement stmt,String date)
    {
        ResultSet rs=null;
        String dataQuery = "";double [] respo=null;
        double betStake=0.0,possibleWinning=0.0,taxedpossibleWinning=0.0,withholdingTax=0.0;

        try
        {
            dataQuery = " select  Play_Bet_Cash_Stake,Play_Bet_Possible_Winning,Play_Bet_Possible_Winning, sum((Play_Bet_Gross_Possible_Winning - Play_Bet_Cash_Stake)*0.2) "
                    + " from player_bets where DATE(Play_Bet_Timestamp)='"+date+"' and Play_Bet_Status in (202) and  Play_Bet_Cash_Stake > 0 group by Play_Bet_ID";
            System.out.println("getTaxData==="+dataQuery);

            rs = stmt.executeQuery(dataQuery);
            
            while (rs.next())
            {
                betStake += Double.valueOf( rs.getString(1));
                possibleWinning += Double.valueOf( rs.getString(2));
                taxedpossibleWinning += Double.valueOf( rs.getString(3));
                withholdingTax += Double.valueOf( rs.getString(4));
            }
            
            double finPossibleWinning=possibleWinning;//-withholdingTax;
            respo=new double[] {finPossibleWinning,withholdingTax};
            
        } catch (SQLException ex) 
        {
            System.out.println("Error getTaxData=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(null,null,rs,null);
        }

    return respo;
    }


    
}
