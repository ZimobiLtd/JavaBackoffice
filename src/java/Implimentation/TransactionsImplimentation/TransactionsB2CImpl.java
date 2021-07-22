/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Implimentation.TransactionsImplimentation;

import Database.DBManager;
import Utility.Utility;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author jac
 */
public class TransactionsB2CImpl {
    
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    public TransactionsB2CImpl()
    {
        
    }
    
    
    
    
    public JSONArray getTransactionsB2C(String from,String to)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String query = "select Trans_Id, Trans_Timestamp, Trans_Mobile, Trans_Amount, Trans_Mpesa_No,Trans_Disburse_Ref_No,if(Trans_Disburse_Status=1,'Successful','Failed') from mpesa_out where date(Trans_Timestamp) between '"+from+"' and '"+to+"' order by Trans_Timestamp desc ";
        System.out.println("getTransactionsB2C==="+query);

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
                String trans_mobile ="0"+rs.getString(3).substring(3);
                String trans_amnt = rs.getString(4);
                String mpesa_code = rs.getString(5);
                String trans_disburse_no = rs.getString(6);
                String trans_status = rs.getString(7);
                String withdrawCharge="0";
                if(Integer.valueOf(trans_amnt) <1000 )
                {
                    withdrawCharge="16";
                }
                else
                {
                    withdrawCharge="23";
                }

                dataObj  = new JSONObject();
                dataObj.put("Trans_ID", trans_id);
                dataObj.put("Trans_Date", trans_date);
                dataObj.put("Trans_Mobile", trans_mobile);
                dataObj.put("Trans_Amount", trans_amnt);
                dataObj.put("Withdraw_Charge", withdrawCharge);
                dataObj.put("Trans_MpesaCode", mpesa_code);
                dataObj.put("Trans_Disburse_Number", trans_disburse_no);
                dataObj.put("Trans_Status", trans_status);
                dataArray.put(dataObj);
            }
            if(dataArray.length()==0)
            {
                dataObj  = new JSONObject();
                dataObj.put("Trans_ID", "0");
                dataObj.put("Trans_Date", "0");
                dataObj.put("Trans_Mobile", "0");
                dataObj.put("Trans_Amount", "0");
                dataObj.put("Withdraw_Charge", "0");
                dataObj.put("Trans_MpesaCode", "0");
                dataObj.put("Trans_Disburse_Number", "0");
                dataObj.put("Trans_Status", "0");
                dataArray.put(dataObj);
            }

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getTransactionsB2C=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }






    public JSONArray filterTransactionsB2C(String from,String to,String mobile,String mpesaCode)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String query = "";
        if(!mobile.equals("0") && mpesaCode.equals("0"))
        {
           query = "select Trans_Id, Trans_Timestamp, Trans_Mobile, Trans_Amount, Trans_Mpesa_No,Trans_Disburse_Ref_No,if(Trans_Disburse_Status=1,'Successful','Failed') from mpesa_out where date(Trans_Timestamp) between '"+from+"' and '"+to+"' and Trans_Mobile='"+mobile+"' order by Trans_Timestamp desc ";
        }
        else if(mobile.equals("0") && !mpesaCode.equals("0"))
        {
           query = "select Trans_Id, Trans_Timestamp, Trans_Mobile, Trans_Amount, Trans_Mpesa_No,Trans_Disburse_Ref_No,if(Trans_Disburse_Status=1,'Successful','Failed') from mpesa_out where date(Trans_Timestamp) between '"+from+"' and '"+to+"' and Trans_Mpesa_No='"+mpesaCode+"' order by Trans_Timestamp desc ";
        }
        else if(!mobile.equals("0") && !mpesaCode.equals("0"))
        {
           query = "select Trans_Id, Trans_Timestamp, Trans_Mobile, Trans_Amount, Trans_Mpesa_No,Trans_Disburse_Ref_No,if(Trans_Disburse_Status=1,'Successful','Failed') from mpesa_out where date(Trans_Timestamp) between '"+from+"' and '"+to+"' and Trans_Mobile='"+mobile+"' and Trans_Mpesa_No='"+mpesaCode+"' order by Trans_Timestamp desc ";
        }
        else if(mobile.equals("0") && mpesaCode.equals("0"))
        {
           query = "select Trans_Id, Trans_Timestamp, Trans_Mobile, Trans_Amount, Trans_Mpesa_No,Trans_Disburse_Ref_No,if(Trans_Disburse_Status=1,'Successful','Failed') from mpesa_out where date(Trans_Timestamp) between '"+from+"' and '"+to+"' order by Trans_Timestamp desc ";
        }
        
        System.out.println("filterTransactionsB2C==="+query);

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
                String trans_disburse_no = rs.getString(6);
                String trans_status = rs.getString(7);

                dataObj  = new JSONObject();
                dataObj.put("Trans_ID", trans_id);
                dataObj.put("Trans_Date", trans_date);
                dataObj.put("Trans_Mobile", trans_mobile);
                dataObj.put("Trans_Amount", trans_amnt);
                dataObj.put("Trans_MpesaCode", mpesa_code);
                dataObj.put("Trans_Disburse_Number", trans_disburse_no);
                dataObj.put("Trans_Status", trans_status);
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
                dataObj.put("Trans_Disburse_Number", "0");
                dataObj.put("Trans_Status", "0");
                dataArray.put(dataObj);
            }

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error filterTransactionsB2C=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
    
}
