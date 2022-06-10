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
public class SlotGamesTransactionsProcessorImpl {
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    public SlotGamesTransactionsProcessorImpl()
    {
        
    }
    
    
    
    public JSONArray getSlotGamesTransactions(String from,String to,String mobile)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String query ="";
        if(mobile.equals("0"))
        {
            query = "select Acc_Id, Acc_Date, Acc_Mobile, Acc_Amount, Acc_Mpesa_Trans_No, ifnull(Acc_Comment,'Success'),if(Acc_Status =0,'Processed','Pending'),"
                         + "(CASE when Golden_Race_Trans_Type ='bet' then 'Bet' when Golden_Race_Trans_Type='win' then 'Bet Win'  when Golden_Race_Trans_Type='cancelbet' then 'Cancel Bet'  end) as 'Trans_Type'"
                         + ",ifnull(Acc_Gateway,'Mpesa') from user_accounts where date(Acc_Date) between '"+from+"' and '"+to+"' and Golden_Race_Trans_Type in('bet','win','cancelbet') and Acc_Company_ID = 3 order by Acc_Date desc ";
         
        }
        else
        {
            query = "select Acc_Id, Acc_Date, Acc_Mobile, Acc_Amount, Acc_Mpesa_Trans_No, ifnull(Acc_Comment,'Success'),if(Acc_Status =0,'Processed','Pending'),"
                         + "(CASE when Golden_Race_Trans_Type ='bet' then 'Bet' when Golden_Race_Trans_Type='win' then 'Bet Win'  when Golden_Race_Trans_Type='cancelbet' then 'Cancel Bet'  end) as 'Trans_Type'"
                         + ",ifnull(Acc_Gateway,'Mpesa') from user_accounts where date(Acc_Date) between '"+from+"' and '"+to+"' and Acc_Mobile='"+mobile+"' and Golden_Race_Trans_Type in('bet','win','cancelbet') and Acc_Company_ID = 3 order by Acc_Date desc ";         
        }
        System.out.println("getSlotGamesTransactions==="+query);

        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            conn = DBManager.getInstance().getDBConnection("read");
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
                dataArray.put(dataObj);
            }

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getSlotGamesTransactions=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }






    public JSONArray filterSlotGamesTransactions(String from,String to,String transtype,String transstatus,String mobile)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String query ="";
        System.out.println("mobile==="+mobile);
        if(mobile.equals("0"))
        {
            query = "select Acc_Id, Acc_Date, Acc_Mobile, Acc_Amount, Acc_Mpesa_Trans_No, ifnull(Acc_Comment,'Success'),if(Acc_Status =0,'Processed','Pending'),"
                         + "(CASE when Golden_Race_Trans_Type ='bet' then 'Bet' when Golden_Race_Trans_Type='win' then 'Bet Win'  when Golden_Race_Trans_Type='cancelbet' then 'Cancel Bet'  end) as 'Trans_Type',ifnull(Acc_Gateway,'Mpesa') "
                         + "from user_accounts where date(Acc_Date) between '"+from+"' and '"+to+"' and "+transtype+" and "+transstatus+" and Acc_Company_ID = 3 order by Acc_Date desc ";
        }
        else
        {
            query = "select Acc_Id, Acc_Date, Acc_Mobile, Acc_Amount, Acc_Mpesa_Trans_No, ifnull(Acc_Comment,'Success'),if(Acc_Status =0,'Processed','Pending'),"
                         + "(CASE when Golden_Race_Trans_Type ='bet' then 'Bet' when Golden_Race_Trans_Type='win' then 'Bet Win'  when Golden_Race_Trans_Type='cancelbet' then 'Cancel Bet'  end) as 'Trans_Type',ifnull(Acc_Gateway,'Mpesa') "
                         + "from user_accounts where date(Acc_Date) between '"+from+"' and '"+to+"' and "+transtype+" and "+transstatus+" and Acc_Mobile='"+mobile+"' and Acc_Company_ID = 3 order by Acc_Date desc ";
        }
        
        System.out.println("filterSlotGamesTransactions==="+query);

        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {

            conn = DBManager.getInstance().getDBConnection("read");
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
                dataArray.put(dataObj);
            }

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error filterSlotGamesTransactions=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
    
}
