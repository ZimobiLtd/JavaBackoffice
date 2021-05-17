/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Players.PlayersAPIs;

import Database.DBManager;
import static Players.PlayersAPIs.PlayerLiabilityAPI.sdf;
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
public class PlayersLiabilityProcessor {
    
    public PlayersLiabilityProcessor()
    {
        
    }
    
    
    public JSONArray getPlayerLiability(String fromDate,String toDate)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String dataQuery = "";
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            dataQuery = "SELECT msisdn,`name`,email,registration_date,Bonus_Balance, "+
            "(select ifnull(sum(Acc_Amount),0) from user_accounts where Acc_Mobile = msisdn ), "+
            "(select count(Acc_ID) from user_accounts where Acc_Mobile = msisdn and Acc_Trans_Type = '1'), "+
            "(select ifnull(sum(Acc_Amount),0) from user_accounts where Acc_Mobile = msisdn and Acc_Trans_Type = '1'), "+
            "(select ifnull(sum(Acc_Amount),0) from user_accounts where Acc_Mobile = msisdn and Acc_Trans_Type = '2'), "+
            "(select ifnull(sum(Acc_Amount),0) from user_accounts where Acc_Mobile = msisdn and Acc_Trans_Type = '7'), "+
            "(select ifnull(sum(Acc_Amount),0) from user_accounts where Acc_Mobile = msisdn and Acc_Trans_Type = '100') "+
            "FROM player WHERE date(registration_date) BETWEEN  '" + fromDate + "' and '" + toDate + "' order by registration_date desc ";
             System.out.println("getPlayerLiability==="+dataQuery);
             
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(dataQuery);

            while (rs.next())
            {
                dataObj  = new JSONObject();
                String playermobile = rs.getString(1);
                String playername = rs.getString(2);
                String playeremail = rs.getString(3);
                String playerregdate = sdf.format(rs.getTimestamp(4));
                String accbonusbalance = rs.getString(5);
                String accbalance = rs.getString(6);
                String depositcount = rs.getString(7);
                String depositamount = rs.getString(8);
                String withdrawals = rs.getString(9);
                String totalbonusawarded = rs.getString(10);
                String totalbonusconvertedtoRM = rs.getString(11);

                dataObj.put("PlayerMobile", playermobile);
                dataObj.put("PlayerName", playername);
                dataObj.put("PlayerEmail", playeremail);
                dataObj.put("RegistrationDate", playerregdate);
                dataObj.put("AccountBalance", accbalance);
                dataObj.put("AccBonusBalance", accbonusbalance);
                dataObj.put("DepositCount", depositcount);
                dataObj.put("Deposits", depositamount);
                dataObj.put("Withdrawals", withdrawals);
                dataObj.put("BonusAwarded", totalbonusawarded);
                dataObj.put("BonusConvertedRM", totalbonusconvertedtoRM);

                dataArray.put(dataObj);
            }

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getPlayerLiability=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
    
}
