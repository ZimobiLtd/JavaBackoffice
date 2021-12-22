package Implimentation.promotionsImplimentation;

import Database.DBManager;
import Utility.Utility;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;

public class ReferralBonusReport {

    public JSONArray getPromotions()
    {
        String dataQuery = "";
        JSONObject dataObj = null;
        JSONArray dataArray = new JSONArray();
        ResultSet rs = null;
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement ps = null;
        try
        {
            dataQuery = "select distinct(Bonus_Type),if(Bonus_Type=1,'Mbogi Bonus','Weka Promotion') from bonus_winners";
            conn = DBManager.getInstance().getDBConnection("read");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(dataQuery);
            rs = stmt.executeQuery(dataQuery);
            while (rs.next())
            {
                dataObj = new JSONObject();
                int promotionID = rs.getInt(1);
                String promotionName = rs.getString(2);
                dataObj.put("PromotionID", promotionID);
                dataObj.put("promotionName", promotionName);
                dataArray.put(dataObj);
            }
            if (dataArray.length() == 0)
            {
                dataObj = new JSONObject();
                dataObj.put("PromotionID", 0);
                dataObj.put("promotionName", "0");
                dataArray.put(dataObj);
            }
        }
        catch (SQLException | org.json.JSONException ex)
        {
            System.out.println("Error getPromotions=== " + ex.getMessage());
        }
        finally
        {
            (new Utility()).doFinally(conn, stmt, rs, ps);
        }
        return dataArray;
    }
    
    public JSONArray getReferalBonusByDateRange(String fromDate, String toDate,String promotionID)
    {
        String dataQuery = "";
        JSONObject dataObj = null;
        JSONArray dataArray = new JSONArray();
        ResultSet rs = null;
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement ps = null;
        try
        {
            dataQuery = "select distinct(Winners),date(Trans_Timestamp),count(refered) as'referred','0'as 'bonus gained',(select count(Acc_ID) from user_accounts where Acc_Trans_Type=4 and "
                    + "Acc_Amount>0 and Acc_Mobile in (select refered from refferafriend where referee=Winners)) as 'referred_and_place_bet' from bonus_winners,refferafriend where "
                    + "date(Trans_Timestamp) between '" + fromDate + "' and '" + toDate + "' and Bonus_Type="+promotionID+" and referee=Winners group by Winners";
            conn = DBManager.getInstance().getDBConnection("read");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(dataQuery);
            rs = stmt.executeQuery(dataQuery);
            while (rs.next())
            {
                dataObj = new JSONObject();
                String refereeMobile = rs.getString(1);
                String dateCreated = rs.getString(2);
                String referralsCount = rs.getString(3);
                String bonusGained = rs.getString(4);
                String referredAndPlacedBetCount = rs.getString(5);
                dataObj.put("Referee", refereeMobile);
                dataObj.put("ReferralsCount", referralsCount);
                dataObj.put("BonusGained", bonusGained);
                dataObj.put("DateCreated", dateCreated);
                dataObj.put("ReferredAndPlacedBetCount", referredAndPlacedBetCount);
                dataArray.put(dataObj);
            }
            if (dataArray.length() == 0)
            {
                dataObj = new JSONObject();
                dataObj.put("Referee", "0");
                dataObj.put("ReferralsCount", "0");
                dataObj.put("BonusGained", "0");
                dataObj.put("DateCreated", "0");
                dataObj.put("ReferredAndPlacedBetCount", "0");
                dataArray.put(dataObj);
            }
        }
        catch (SQLException | org.json.JSONException ex)
        {
            System.out.println("Error getReferalBonusByDateRange=== " + ex.getMessage());
        }
        finally
        {
            (new Utility()).doFinally(conn, stmt, rs, ps);
        }
        return dataArray;
    }

    public JSONArray getReferalBonusByMobile(String refereeMobileNumber)
    {
        String dataQuery = "";
        JSONObject dataObj = null;
        JSONArray dataArray = new JSONArray();
        ResultSet rs = null;
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement ps = null;
        try
        {
            dataQuery = "select distinct(Winners),date(Trans_Timestamp),count(refered) as'referred','0'as 'bonus gained',(select count(Acc_ID) from user_accounts where "
                    + "Acc_Trans_Type=4 and Acc_Amount>0 and Acc_Mobile in (select refered from refferafriend where referee='" + refereeMobileNumber + "')) as 'referred_and_place_bet', "
                    + "(case when Bonus_Type =1 then 'Mbogi Promotion' when Bonus_Type =2 then 'Weka Promotion' end ) as 'promotion' from bonus_winners,refferafriend "
                    + "where referee='" + refereeMobileNumber + "' group by Winners";
            
            conn = DBManager.getInstance().getDBConnection("read");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(dataQuery);
            while (rs.next())
            {
                dataObj = new JSONObject();
                String refereeMobile = rs.getString(1);
                String dateCreated = rs.getString(2);
                String referralsCount = rs.getString(3);
                String bonusGained = rs.getString(4);
                String referredAndPlacedBetCount = rs.getString(5);
                String promotionType = rs.getString(6);
                dataObj.put("Referee", refereeMobile);
                dataObj.put("ReferralsCount", referralsCount);
                dataObj.put("BonusGained", bonusGained);
                dataObj.put("DateCreated", dateCreated);
                dataObj.put("ReferredAndPlacedBetCount", referredAndPlacedBetCount);
                dataObj.put("PromotionType", promotionType);
                dataArray.put(dataObj);
            }
            if (dataArray.length() == 0)
            {
                dataObj = new JSONObject();
                dataObj.put("Referee", "0");
                dataObj.put("ReferralsCount", "0");
                dataObj.put("BonusGained", "0");
                dataObj.put("DateCreated", "0");
                dataObj.put("ReferredAndPlacedBetCount", "0");
                dataObj.put("PromotionType", "0");
                dataArray.put(dataObj);
            }
        }
        catch (SQLException | org.json.JSONException ex)
        {
            System.out.println("Error getReferalBonusByMobile=== " + ex.getMessage());
        }
        finally
        {
            (new Utility()).doFinally(conn, stmt, rs, ps);
        }
        return dataArray;
    }

    public JSONArray getReferalsAndBetsCount(String refereeMobileNumber)
    {
        String dataQuery = "";
        JSONObject dataObj = null;
        JSONArray dataArray = new JSONArray();
        ResultSet rs = null;
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement ps = null;
        try
        {
            dataQuery = "select referee,refered, (select count(Acc_ID) from user_accounts where Acc_Trans_Type=4 and Acc_Amount>0 and Acc_Mobile=refered) as 'bets count'from "
                    + "refferafriend where referee='" + refereeMobileNumber + "' ";
            conn = DBManager.getInstance().getDBConnection("read");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(dataQuery);
            while (rs.next())
            {
                dataObj = new JSONObject();
                String refereeMobile = "0" + rs.getString(1).substring(3);
                String referred = "0" + rs.getString(2).substring(3);
                String betsCount = rs.getString(3);
                dataObj.put("Referee", refereeMobile);
                dataObj.put("Referred", referred);
                dataObj.put("BetsCount", betsCount);
                dataArray.put(dataObj);
            }
            if (dataArray.length() == 0)
            {
                dataObj = new JSONObject();
                dataObj.put("Referee", "0");
                dataObj.put("Referred", "0");
                dataObj.put("BetsCount", "0");
                dataArray.put(dataObj);
            }
        }
        catch (SQLException | org.json.JSONException ex)
        {
            System.out.println("Error getReferalsAndBetsCount=== " + ex.getMessage());
        }
        finally
        {
            (new Utility()).doFinally(conn, stmt, rs, ps);
        }
        return dataArray;
    }
}
