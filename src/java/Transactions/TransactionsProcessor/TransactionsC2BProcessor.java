/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Transactions.TransactionsProcessor;

import Database.DBManager;
import static Transactions.APIs.TransactionsC2BAPI.sdf;
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
public class TransactionsC2BProcessor {
    
    
    public TransactionsC2BProcessor()
    {
        
    }
    
    
    public JSONArray getTransactionsC2B(String from,String to)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String query = "select Trans_Id, Trans_Timestamp, Trans_Mobile, Trans_Amount, Trans_Mpesa_No,Trans_Acc_No from transactions_c2b where date(Trans_Timestamp) between '"+from+"' and '"+to+"' order by Trans_Timestamp desc ";
        System.out.println("getTransactionsC2B==="+query);

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
                String trans_mobile = "0"+rs.getString(3).substring(3);
                String trans_amnt = rs.getString(4);
                String mpesa_code = rs.getString(5);
                String trans_acc = rs.getString(6);

                dataObj  = new JSONObject();
                dataObj.put("Trans_ID", trans_id);
                dataObj.put("Trans_Date", trans_date);
                dataObj.put("Trans_Mobile", trans_mobile);
                dataObj.put("Trans_Amount", trans_amnt);
                dataObj.put("Trans_MpesaCode", mpesa_code);
                dataObj.put("Trans_Account", trans_acc);
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
                dataObj.put("Trans_Account", "0");
                dataArray.put(dataObj);
            }


        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getTransactionsC2B=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }




    public JSONArray filterTransactionsC2B(String from,String to,String mobile,String mpesaCode)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String query ="";
        if(!mobile.equals("0") && mpesaCode.equals("0"))
        {
           query = "select Trans_Id, Trans_Timestamp, Trans_Mobile, Trans_Amount, Trans_Mpesa_No,Trans_Acc_No from transactions_c2b where date(Trans_Timestamp) between '"+from+"' and '"+to+"' and Trans_Mobile='"+mobile+"' order by Trans_Timestamp desc ";
        }
        else if(mobile.equals("0") && !mpesaCode.equals("0"))
        {
           query = "select Trans_Id, Trans_Timestamp, Trans_Mobile, Trans_Amount, Trans_Mpesa_No,Trans_Acc_No from transactions_c2b where date(Trans_Timestamp) between '"+from+"' and '"+to+"' and Trans_Mpesa_No='"+mpesaCode+"' order by Trans_Timestamp desc ";
        }
        else if(!mobile.equals("0") && !mpesaCode.equals("0"))
        {
           query = "select Trans_Id, Trans_Timestamp, Trans_Mobile, Trans_Amount, Trans_Mpesa_No,Trans_Acc_No from transactions_c2b where date(Trans_Timestamp) between '"+from+"' and '"+to+"' and Trans_Mobile='"+mobile+"' and Trans_Mpesa_No='"+mpesaCode+"' order by Trans_Timestamp desc ";
        }
        else if(mobile.equals("0") && mpesaCode.equals("0"))
        {
           query = "select Trans_Id, Trans_Timestamp, Trans_Mobile, Trans_Amount, Trans_Mpesa_No,Trans_Acc_No from transactions_c2b where date(Trans_Timestamp) between '"+from+"' and '"+to+"' order by Trans_Timestamp desc ";
        }
        System.out.println("filterTransactionsC2B==="+query);

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
                String trans_mobile = "0"+rs.getString(3).substring(3);
                String trans_amnt = rs.getString(4);
                String mpesa_code = rs.getString(5);
                String trans_acc = rs.getString(6);

                dataObj  = new JSONObject();
                dataObj.put("Trans_ID", trans_id);
                dataObj.put("Trans_Date", trans_date);
                dataObj.put("Trans_Mobile", trans_mobile);
                dataObj.put("Trans_Amount", trans_amnt);
                dataObj.put("Trans_MpesaCode", mpesa_code);
                dataObj.put("Trans_Account", trans_acc);
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
                dataObj.put("Trans_Account", "0");
                dataArray.put(dataObj);
            }

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error filterTransactionsC2B=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
    
}
