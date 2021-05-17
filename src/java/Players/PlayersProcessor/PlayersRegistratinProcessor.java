/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Players.PlayersProcessor;

import Database.DBManager;
import static Players.PlayersAPIs.PlayersRegistrationAPI.sdf;
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
public class PlayersRegistratinProcessor {
    
    public PlayersRegistratinProcessor()
    {
        
    }
    
    
    public JSONArray getPlayerRegistrations(String from,String to)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String dataQuery = "select id, msisdn, ifnull(`name`,'no name'), ifnull(email,'no email'), registration_date, (select ifnull(sum(Acc_Amount),0) from user_accounts where Acc_Mobile = msisdn ), Bonus_Balance,"
                + "(case when User_Channel=1 then'USSD' when User_Channel=2 then 'SMS' when User_Channel=3 then 'Computer Web' when User_Channel=4 then 'Mobile Web' end), "
                + "(case when `status`=1 then 'Inactive' when `status`=0 then 'Active' end), " +
                " (select ifnull(max(Acc_Date),'0') from user_accounts where Acc_Mobile = msisdn and Acc_Trans_Type=1) as 'Last Deposit', " +
                " (select count(Play_Bet_ID) from player_bets where Play_Bet_Mobile = msisdn and Play_Bet_Status <> 206) as 'Bets Count' " +
                " from player where date(registration_date) between '"+from+"' and '"+to+"' order by registration_date desc ";
        System.out.println("getPlayerRegistrations==="+dataQuery);

        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(dataQuery);

            while (rs.next())
            {
                String id = rs.getString(1);
                String mobile = "0"+rs.getString(2).substring(3);
                String name = rs.getString(3);
                String email = rs.getString(4);
                String regdate = sdf.format(rs.getTimestamp(5));
                String balanceRM = rs.getString(6);
                String balanceBM = rs.getString(7);
                String channel = rs.getString(8);
                String status = rs.getString(9);
                String lastdepositdate = rs.getString(10);
                String betscount = rs.getString(11);

                dataObj  = new JSONObject();
                dataObj.put("ID", id);
                dataObj.put("Mobile", mobile);
                dataObj.put("Name", name);
                dataObj.put("Email", email);
                dataObj.put("Registration_Date", regdate);
                dataObj.put("BalanceRM", balanceRM);
                dataObj.put("BalanceBM", balanceBM);
                dataObj.put("Registration_Channel", channel);
                dataObj.put("LastDeposit_Date", lastdepositdate);
                dataObj.put("BetsCount", betscount);
                dataObj.put("Status", status);
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
            System.out.println("Error getPlayerRegistrations=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
        
        
        
    public JSONArray filterPlayerRegistrationsByDate(String from,String to)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String dataQuery = "select id, msisdn, ifnull(`name`,'no name'), ifnull(email,'no email'), registration_date, (select ifnull(sum(Acc_Amount),0) from user_accounts where Acc_Mobile = msisdn ), Bonus_Balance,"
                        + "(case when User_Channel=1 then'USSD' when User_Channel=2 then 'SMS' when User_Channel=3 then 'Computer Web' when User_Channel=4 then 'Mobile Web' end), "
                        + "(case when `status`=1 then 'Inactive' when `status`=0 then 'Active' end), " +
                        " (select ifnull(max(Acc_Date),'0') from user_accounts where Acc_Mobile = msisdn and Acc_Trans_Type=1) as 'Last Deposit', " +
                        " (select count(Play_Bet_ID) from player_bets where Play_Bet_Mobile = msisdn and Play_Bet_Status <> 206) as 'Bets Count' " +
                        " from player where date(registration_date) between '"+from+"' and '"+to+"' order by registration_date desc ";
        System.out.println("getPlayerRegistrations==="+dataQuery);

        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(dataQuery);

            while (rs.next())
            {
                String id = rs.getString(1);
                String mobile = "0"+rs.getString(2).substring(3);
                String name = rs.getString(3);
                String email = rs.getString(4);
                String regdate = sdf.format(rs.getTimestamp(5));
                String balanceRM = rs.getString(6);
                String balanceBM = rs.getString(7);
                String channel = rs.getString(8);
                String status = rs.getString(9);
                String lastdepositdate = rs.getString(10);
                String betscount = rs.getString(11);

                dataObj  = new JSONObject();
                dataObj.put("ID", id);
                dataObj.put("Mobile", mobile);
                dataObj.put("Name", name);
                dataObj.put("Email", email);
                dataObj.put("Registration_Date", regdate);
                dataObj.put("BalanceRM", balanceRM);
                dataObj.put("BalanceBM", balanceBM);
                dataObj.put("Registration_Channel", channel);
                dataObj.put("LastDeposit_Date", lastdepositdate);
                dataObj.put("BetsCount", betscount);
                dataObj.put("Status", status);
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
            System.out.println("Error getPlayerRegistrations=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }



    public JSONArray filterPlayerRegistrationsByMobile(String mobile_no)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String dataQuery = "select id, msisdn, ifnull(`name`,'no name'), ifnull(email,'no email'), registration_date, (select ifnull(sum(Acc_Amount),0) from user_accounts where Acc_Mobile = msisdn ), Bonus_Balance,"
                + "(case when User_Channel=1 then'USSD' when User_Channel=2 then 'SMS' when User_Channel=3 then 'Computer Web' when User_Channel=4 then 'Mobile Web' end), "
                + "(case when `status`=1 then 'Inactive' when `status`=0 then 'Active' end), " +
                " (select ifnull(max(Acc_Date),'0') from user_accounts where Acc_Mobile = msisdn and Acc_Trans_Type=1) as 'Last Deposit', " +
                " (select count(Play_Bet_ID) from player_bets where Play_Bet_Mobile = msisdn and Play_Bet_Status <> 206) as 'Bets Count' " +
                " from player where msisdn='"+mobile_no+"' order by registration_date desc ";
        System.out.println("getPlayerRegistrations==="+dataQuery);

        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(dataQuery);

            while (rs.next())
            {
                String id = rs.getString(1);
                String mobile = "0"+rs.getString(2).substring(3);
                String name = rs.getString(3);
                String email = rs.getString(4);
                String regdate = sdf.format(rs.getTimestamp(5));
                String balanceRM = rs.getString(6);
                String balanceBM = rs.getString(7);
                String channel = rs.getString(8);
                String status = rs.getString(9);
                String lastdepositdate = rs.getString(10);
                String betscount = rs.getString(11);

                dataObj  = new JSONObject();
                dataObj.put("ID", id);
                dataObj.put("Mobile", mobile);
                dataObj.put("Name", name);
                dataObj.put("Email", email);
                dataObj.put("Registration_Date", regdate);
                dataObj.put("BalanceRM", balanceRM);
                dataObj.put("BalanceBM", balanceBM);
                dataObj.put("Registration_Channel", channel);
                dataObj.put("LastDeposit_Date", lastdepositdate);
                dataObj.put("BetsCount", betscount);
                dataObj.put("Status", status);
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
            System.out.println("Error filterPlayerRegistrationsByMobile=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }




    public JSONArray setDeactivatePlayer(String mobile)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String dataQuery = "";String Query="";
        JSONObject dataObj  = new JSONObject();
        JSONArray dataArray = new JSONArray();

        try
        {
            dataQuery= " update player set `status` =1 where msisdn = '"+mobile+"' ";
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(dataQuery);

            dataObj  = new JSONObject();
            dataObj.put("message", "Player deactivated");
            dataArray.put(dataObj);

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error setDeactivatePlayer=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }



    public JSONArray setActivatePlayer(String mobile)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String dataQuery = "";String Query="";
        JSONObject dataObj  = new JSONObject();
        JSONArray dataArray = new JSONArray();

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();

            dataQuery= " update player set  `status` =0 where msisdn = '"+mobile+"' ";
            stmt.executeUpdate(dataQuery);


            dataObj  = new JSONObject();
            dataObj.put("message", "Player activated");
            dataArray.put(dataObj);

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error setActivatePlayer=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
        
    
}
