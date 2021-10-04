/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Implimentation.PlayersImplimentation;

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
public class PlayersRegistrationImpl {
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    public PlayersRegistrationImpl()
    {
        
    }
    
    
    public JSONArray getPlayerRegistrations(String from,String to)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String dataQuery = "select id, msisdn, ifnull(`name`,'no name'), ifnull(email,'no email'), registration_date, (select ifnull(sum(Acc_Amount),0) from user_accounts "
                        + "where Acc_Mobile = msisdn ), (select ifnull(sum(Acc_Bonus_Amount),0) from user_accounts where Acc_Mobile=msisdn and Acc_Bonus_Status=1 and Acc_Trans_Type in (1,4,7)),"
                        + "(select ifnull(sum(Acc_Bonus_Amount),0) from user_accounts where Acc_Mobile=msisdn and Acc_Bonus_Status=12),"
                        + "(case when User_Channel=1 then'USSD' when User_Channel=2 then 'SMS' when User_Channel=3 then 'Computer Web' when User_Channel=4 then 'Mobile Web' when User_Channel=0 then 'No Channel'  end), "
                        + "(case when `status`=1 then 'Inactive' when `status`=0 then 'Active' end), " +
                        " (select ifnull(max(Acc_Date),'0') from user_accounts where Acc_Mobile = msisdn and Acc_Trans_Type=1) as 'Last Deposit', " +
                        " (select count(Play_Bet_ID) from player_bets where Play_Bet_Mobile = msisdn and Play_Bet_Status <> 206) as 'Bets Count', " +
                        "(select referee  from refferafriend where refered=msisdn limit 1) as 'Referee',Reg_Keyword "+
                        " from player where date(registration_date) between '"+from+"' and '"+to+"' order by registration_date desc ";
        System.out.println("getPlayerRegistrations==="+dataQuery);

        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            conn = DBManager.getInstance().getDBConnection("read");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(dataQuery);

            while (rs.next())
            {
                String id = rs.getString(1);
                String mobile="";
                String val= rs.getString(2);
                if(val.startsWith("254"))
                {
                    mobile= "0"+rs.getString(2).substring(3);
                }
                String name = rs.getString(3);
                String email = rs.getString(4);
                String regdate = sdf.format(rs.getTimestamp(5));
                String balanceRM = rs.getString(6);
                String balanceBM = rs.getString(7);
                String refferalBM = rs.getString(8);
                String channel = rs.getString(9);
                String status = rs.getString(10);
                String lastdepositdate = rs.getString(11);
                String betscount = rs.getString(12);
                String referee = rs.getString(13);
                String promoterCode = rs.getString(14);
                String referalStatus="0";
                if(referee == null)
                {
                    referee="No referee";
                    referalStatus="Not refered";
                }
                else
                {
                    referalStatus="Referred";
                }

                dataObj  = new JSONObject();
                dataObj.put("ID", id);
                dataObj.put("Mobile", mobile);
                dataObj.put("Name", name);
                dataObj.put("Email", email);
                dataObj.put("Registration_Date", regdate);
                dataObj.put("BalanceRM", String.format("%.2f", Double.valueOf(balanceRM)));
                dataObj.put("BalanceBM", String.format("%.2f", Double.valueOf(balanceBM)));
                dataObj.put("RefferalBM", String.format("%.2f", Double.valueOf(refferalBM)));
                dataObj.put("Registration_Channel", channel);
                dataObj.put("LastDeposit_Date", lastdepositdate);
                dataObj.put("BetsCount", betscount);
                dataObj.put("Status", status);
                dataObj.put("Referee", referee);
                dataObj.put("ReferalStatus", referalStatus);
                dataObj.put("PromoterCode", promoterCode);
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
        String dataQuery = "select id, msisdn, ifnull(`name`,'no name'), ifnull(email,'no email'), registration_date, (select ifnull(sum(Acc_Amount),0) from user_accounts "
                        + "where Acc_Mobile = msisdn ), (select ifnull(sum(Acc_Bonus_Amount),0) from user_accounts where Acc_Mobile=msisdn and Acc_Bonus_Status=1 and Acc_Trans_Type in (1,4,7)),"
                        + "(select ifnull(sum(Acc_Bonus_Amount),0) from user_accounts where Acc_Mobile=msisdn and Acc_Bonus_Status=12),"
                        + "(case when User_Channel=1 then'USSD' when User_Channel=2 then 'SMS' when User_Channel=3 then 'Computer Web' when User_Channel=4 then 'Mobile Web' when User_Channel=0 then 'No Channel'  end), "
                        + "(case when `status`=1 then 'Inactive' when `status`=0 then 'Active' end), " +
                        " (select ifnull(max(Acc_Date),'0') from user_accounts where Acc_Mobile = msisdn and Acc_Trans_Type=1) as 'Last Deposit', " +
                        " (select count(Play_Bet_ID) from player_bets where Play_Bet_Mobile = msisdn and Play_Bet_Status <> 206) as 'Bets Count', " +
                        "(select referee  from refferafriend where refered=msisdn limit 1) as 'Referee',Reg_Keyword "+
                        " from player where msisdn='"+mobile_no+"' order by registration_date desc ";
        System.out.println("getPlayerRegistrations==="+dataQuery);

        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            conn = DBManager.getInstance().getDBConnection("read");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(dataQuery);

            while (rs.next())
            {
                String id = rs.getString(1);
                String mobile="";
                String val= rs.getString(2);
                if(val.startsWith("254"))
                {
                    mobile= "0"+rs.getString(2).substring(3);
                }
                String name = rs.getString(3);
                String email = rs.getString(4);
                String regdate = sdf.format(rs.getTimestamp(5));
                String balanceRM = rs.getString(6);
                String balanceBM = rs.getString(7);
                String refferalBM = rs.getString(8);
                String channel = rs.getString(9);
                String status = rs.getString(10);
                String lastdepositdate = rs.getString(11);
                String betscount = rs.getString(12);
                String referee = rs.getString(13);
                String promoterCode = rs.getString(14);
                String referalStatus="0";
                if(referee == null)
                {
                    referee="No referee";
                    referalStatus="Not refered";
                }
                else
                {
                    referalStatus="Referred";
                }

                dataObj  = new JSONObject();
                dataObj.put("ID", id);
                dataObj.put("Mobile", mobile);
                dataObj.put("Name", name);
                dataObj.put("Email", email);
                dataObj.put("Registration_Date", regdate);
                dataObj.put("BalanceRM", String.format("%.2f", Double.valueOf(balanceRM)));
                dataObj.put("BalanceBM", String.format("%.2f", Double.valueOf(balanceBM)));
                dataObj.put("RefferalBM", String.format("%.2f", Double.valueOf(refferalBM)));
                dataObj.put("Registration_Channel", channel);
                dataObj.put("LastDeposit_Date", lastdepositdate);
                dataObj.put("BetsCount", betscount);
                dataObj.put("Status", status);
                dataObj.put("Referee", referee);
                dataObj.put("ReferalStatus", referalStatus);
                dataObj.put("PromoterCode", promoterCode);
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
    
    
    
    public JSONArray filterPlayerRegistrationsByKeyword(String keyword)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String dataQuery = "select id, msisdn, ifnull(`name`,'no name'), ifnull(email,'no email'), registration_date, (select ifnull(sum(Acc_Amount),0) from user_accounts "
                        + "where Acc_Mobile = msisdn ), (select ifnull(sum(Acc_Bonus_Amount),0) from user_accounts where Acc_Mobile=msisdn and Acc_Bonus_Status=1 and Acc_Trans_Type in (1,4,7)),"
                        + "(select ifnull(sum(Acc_Bonus_Amount),0) from user_accounts where Acc_Mobile=msisdn and Acc_Bonus_Status=12),"
                        + "(case when User_Channel=1 then'USSD' when User_Channel=2 then 'SMS' when User_Channel=3 then 'Computer Web' when User_Channel=4 then 'Mobile Web' when User_Channel=0 then 'No Channel'  end), "
                        + "(case when `status`=1 then 'Inactive' when `status`=0 then 'Active' end), " +
                        " (select ifnull(max(Acc_Date),'0') from user_accounts where Acc_Mobile = msisdn and Acc_Trans_Type=1) as 'Last Deposit', " +
                        " (select count(Play_Bet_ID) from player_bets where Play_Bet_Mobile = msisdn and Play_Bet_Status <> 206) as 'Bets Count', " +
                        "(select referee  from refferafriend where refered=msisdn limit 1) as 'Referee',Reg_Keyword "+
                        " from player where Reg_Keyword='"+keyword+"' order by registration_date desc ";
        System.out.println("getPlayerRegistrations==="+dataQuery);

        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            conn = DBManager.getInstance().getDBConnection("read");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(dataQuery);

            while (rs.next())
            {
                String id = rs.getString(1);
                String mobile="";
                String val= rs.getString(2);
                if(val.startsWith("254"))
                {
                    mobile= "0"+rs.getString(2).substring(3);
                }
                String name = rs.getString(3);
                String email = rs.getString(4);
                String regdate = sdf.format(rs.getTimestamp(5));
                String balanceRM = rs.getString(6);
                String balanceBM = rs.getString(7);
                String refferalBM = rs.getString(8);
                String channel = rs.getString(9);
                String status = rs.getString(10);
                String lastdepositdate = rs.getString(11);
                String betscount = rs.getString(12);
                String referee = rs.getString(13);
                String promoterCode = rs.getString(14);
                String referalStatus="0";
                if(referee == null)
                {
                    referee="No referee";
                    referalStatus="Not refered";
                }
                else
                {
                    referalStatus="Referred";
                }

                dataObj  = new JSONObject();
                dataObj.put("ID", id);
                dataObj.put("Mobile", mobile);
                dataObj.put("Name", name);
                dataObj.put("Email", email);
                dataObj.put("Registration_Date", regdate);
                dataObj.put("BalanceRM", String.format("%.2f", Double.valueOf(balanceRM)));
                dataObj.put("BalanceBM", String.format("%.2f", Double.valueOf(balanceBM)));
                dataObj.put("RefferalBM", String.format("%.2f", Double.valueOf(refferalBM)));
                dataObj.put("Registration_Channel", channel);
                dataObj.put("LastDeposit_Date", lastdepositdate);
                dataObj.put("BetsCount", betscount);
                dataObj.put("Status", status);
                dataObj.put("Referee", referee);
                dataObj.put("ReferalStatus", referalStatus);
                dataObj.put("PromoterCode", promoterCode);
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




    public JSONArray setDeactivatePlayer(String mobile,String narration,String deactivatedBy)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String dataQuery = "";String Query="";
        JSONObject dataObj  = new JSONObject();
        JSONArray dataArray = new JSONArray();

        try
        {
            dataQuery= " update player set `status` =2,narration='"+narration+"',action_by='"+deactivatedBy+"' where msisdn = '"+mobile+"' ";
            conn = DBManager.getInstance().getDBConnection("write");
            stmt = conn.createStatement();
            stmt.executeUpdate(dataQuery);

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


    
    public JSONArray getDeactivationNarration(String mobile)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String dataQuery = "select narration,action_by from player where msisdn='"+mobile+"'";
        //System.out.println("getDeactivationNarration==="+dataQuery);

        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            conn = DBManager.getInstance().getDBConnection("read");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(dataQuery);

            while (rs.next())
            {
                String deactivationNarration = rs.getString(1);
                String actionBy = rs.getString(2);

                dataObj  = new JSONObject();
                dataObj.put("Narration", deactivationNarration);
                dataObj.put("ActionBy", actionBy);
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
            System.out.println("Error getDeactivationNarration=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
    
    public String getPlayerPassword(String mobile)
    {
        String response = null;
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String dataQuery = "select password from player where msisdn='"+mobile+"'";

        try
        {
            conn = DBManager.getInstance().getDBConnection("read");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(dataQuery);

            while (rs.next())
            {
                String password = rs.getString(1);
                response=password;
            }
        }
        catch (SQLException ex) 
        {
            System.out.println("Error getPlayerPassword=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return response;
    }

    public JSONArray setActivatePlayer(String mobile,String activatedBy)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String dataQuery = "";String Query="";
        JSONObject dataObj  = new JSONObject();
        JSONArray dataArray = new JSONArray();
        dataQuery= " update player set  `status` =0,narration='0',action_by='"+activatedBy+"' where msisdn = '"+mobile+"' ";

        try
        {
            conn = DBManager.getInstance().getDBConnection("write");
            stmt = conn.createStatement();
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
