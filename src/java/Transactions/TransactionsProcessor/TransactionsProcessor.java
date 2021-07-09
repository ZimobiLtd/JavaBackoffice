/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Transactions.TransactionsProcessor;

import Database.DBManager;
import static Transactions.APIs.TransactionsAPI.sdf;
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
public class TransactionsProcessor {
    
    
    public TransactionsProcessor()
    {
        
    }
    
    
    public JSONArray getTransactions(String from,String to)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String query = "select Acc_Id, Acc_Date, Acc_Mobile,if(Acc_Amount=0,Acc_Bonus_Amount,Acc_Amount), Acc_Mpesa_Trans_No, ifnull(Acc_Comment,'Success'),if(Acc_Status =0,'Processed','Pending'),"
                         + "(CASE when Acc_Trans_Type =1 then 'Deposit' when Acc_Trans_Type=2 then 'User Withdrawal'  when Acc_Trans_Type=8 then 'Withdrawal Charge' when Acc_Trans_Type=9 then 'Bonus Winning' when Acc_Trans_Type=3 then 'Bet Win' when Acc_Trans_Type=4 then 'Bet Withdrawal'  end) as 'Trans_Type'"
                         + ",ifnull(Acc_Gateway,'SPORTBET') from user_accounts where date(Acc_Date) between '"+from+"' and '"+to+"' order by Acc_Date desc ";
        System.out.println("getTransactions==="+query);

        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next())
            {
                String trans_id = rs.getString(1);
                String trans_date = sdf.format(rs.getTimestamp(2));
                String trans_mobile = rs.getString(3);
                String trans_amnt = rs.getString(4);
                String mpesa_code = rs.getString(5);
                String trans_comment = rs.getString(6);
                String trans_status = rs.getString(7);
                String transtype = rs.getString(8);
                String transgateway = rs.getString(9);

                dataObj  = new JSONObject();
                dataObj.put("Trans_ID", trans_id);
                dataObj.put("Trans_Date", trans_date);
                dataObj.put("Trans_Mobile", trans_mobile);
                dataObj.put("Trans_Amount", trans_amnt);
                dataObj.put("Trans_MpesaCode", mpesa_code);
                dataObj.put("Trans_Comment", trans_comment);
                dataObj.put("Trans_Status", trans_status);
                dataObj.put("Trans_Type", transtype);
                dataObj.put("Trans_Gateway", transgateway);
                dataArray.put(dataObj);
            }

            if(dataArray.length()==0)
            {
                dataObj  = new JSONObject();
                dataObj.put("Trans_ID", "0");
                dataObj.put("Trans_Date", "0");
                dataObj.put("Trans_Mobile", "0");
                dataObj.put("Trans_Amount", "0");
                dataObj.put("Trans_MpesaCode", "0");
                dataObj.put("Trans_Comment", "0");
                dataObj.put("Trans_Status", "0");
                dataObj.put("Trans_Type", "0");
                dataObj.put("Trans_Gateway", "0");
                dataArray.put(dataObj);
            }

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getTransactions=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
        
        
        
        
        
        
    public JSONArray filterTransactions(String from,String to,String transtype,String transstatus)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String query = "select Acc_Id, Acc_Date, Acc_Mobile,if(Acc_Amount=0,Acc_Bonus_Amount,Acc_Amount), Acc_Mpesa_Trans_No, ifnull(Acc_Comment,'Success'),if(Acc_Status =0,'Processed','Pending'),"
                         + "(CASE when Acc_Trans_Type =1 then 'Deposit' when Acc_Trans_Type=2 then 'User Withdrawal'  when Acc_Trans_Type=8 then 'Withdrawal Charge' when Acc_Trans_Type=9 then 'Bonus Winning' when Acc_Trans_Type=3 then 'Bet Win' when Acc_Trans_Type=4 then 'Bet Withdrawal'   end) as 'Trans_Type',ifnull(Acc_Gateway,'Mpesa') "
                         + "from user_accounts where date(Acc_Date) between '"+from+"' and '"+to+"' and "+transtype+" and "+transstatus+" order by Acc_Date desc ";
        System.out.println("filterTransactions==="+query);

        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next())
            {
                String trans_id = rs.getString(1);
                String trans_date = sdf.format(rs.getTimestamp(2));
                String trans_mobile = rs.getString(3);
                String trans_amnt = rs.getString(4);
                String mpesa_code = rs.getString(5);
                String trans_comment = rs.getString(6);
                String trans_status = rs.getString(7);
                String trans_type = rs.getString(8);
                String trans_gateway = rs.getString(9);

                dataObj  = new JSONObject();
                dataObj.put("Trans_ID", trans_id);
                dataObj.put("Trans_Date", trans_date);
                dataObj.put("Trans_Mobile", trans_mobile);
                dataObj.put("Trans_Amount", trans_amnt);
                dataObj.put("Trans_MpesaCode", mpesa_code);
                dataObj.put("Trans_Comment", trans_comment);
                dataObj.put("Trans_Status", trans_status);
                dataObj.put("Trans_Type", trans_type);
                dataObj.put("Trans_Gateway", trans_gateway);
                dataArray.put(dataObj);
            }
            if(dataArray.length()==0)
            {
                dataObj  = new JSONObject();
                dataObj.put("Trans_ID", "0");
                dataObj.put("Trans_Date", "0");
                dataObj.put("Trans_Mobile", "0");
                dataObj.put("Trans_Amount", "0");
                dataObj.put("Trans_MpesaCode", "0");
                dataObj.put("Trans_Comment", "0");
                dataObj.put("Trans_Status", "0");
                dataObj.put("Trans_Type", "0");
                dataObj.put("Trans_Gateway", "0");
                dataArray.put(dataObj);
            }

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error filterTransactions=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
    
}
