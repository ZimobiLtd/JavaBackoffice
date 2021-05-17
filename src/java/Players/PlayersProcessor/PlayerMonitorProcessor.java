/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Players.PlayersProcessor;

import Database.DBManager;
import static Players.PlayersAPIs.PlayerMonitorAPI.sdf;
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
public class PlayerMonitorProcessor {
    
    
    public PlayerMonitorProcessor()
    {
        
    }
    
    
    
    public JSONArray monitorAccounts()
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String dataQuery = "select   ifnull(sum(Acc_Amount),0) ,msisdn,registration_date,(case when User_Channel=1 then'USSD' when User_Channel=2 then 'SMS' when User_Channel=3 then 'Computer Web' when User_Channel=4 then 'Mobile Web' end),"
                         + "(case when `status`=1 then 'Inactive' when `status`=0 then 'Active' end) from user_accounts,player   where msisdn=Acc_Mobile  group by Acc_Mobile  having  sum(Acc_Amount) <0 ";
        System.out.println("monitorAccounts==="+dataQuery);

        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(dataQuery);

            while (rs.next())
            {
                String AccBalance = rs.getString(1);
                String mobile = "0"+rs.getString(2).substring(3);
                String regdate = sdf.format(rs.getTimestamp(3));
                String regChannel = rs.getString(4);
                String AccStatus = rs.getString(5);

                dataObj  = new JSONObject();
                dataObj.put("Account_Balance", AccBalance);
                dataObj.put("Mobile", mobile);
                dataObj.put("Registration_Date", regdate);
                dataObj.put("Registration_Channel", regChannel);
                dataObj.put("Account_Status", AccStatus);
                dataArray.put(dataObj);
            }

            if(dataArray.length()==0)
            {
                dataObj  = new JSONObject();
                dataObj.put("Account_Balance", "0");
                dataObj.put("Mobile", "0");
                dataObj.put("Registration_Date", "0");
                dataObj.put("Registration_Channel", "0");
                dataObj.put("Account_Status", "0");
                dataArray.put(dataObj);
            }

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error monitorAccounts=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
    
}
