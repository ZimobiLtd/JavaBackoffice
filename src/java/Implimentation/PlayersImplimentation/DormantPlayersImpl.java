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
public class DormantPlayersImpl {
    
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    public DormantPlayersImpl()
    {
        
    }
    
    
    public JSONArray getDormantPlayer(String from,String to)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String dataQuery = "select msisdn,registration_date,(select ifnull(sum(Acc_Amount),0) from user_accounts where Acc_Mobile=msisdn ) as 'account balance', "+
                           "(select ifnull(sum(Acc_Bonus_Amount),0) from user_accounts where Acc_Mobile=msisdn and Acc_Bonus_Status=1 and Acc_Trans_Type in (1,4,7)) as 'bonus balance', " +
                           "(select ifnull(max(Play_Bet_Timestamp),'0') from player_bets where Play_Bet_Mobile=msisdn and date(Play_Bet_Timestamp) not between '"+from+"' and '"+to+"' ) as 'last bet date' " +
                           "from player where msisdn not  in ( select Play_Bet_Mobile from player_bets  where date(Play_Bet_Timestamp) between '"+from+"' and '"+to+"' )  group by msisdn ";
        System.out.println("getDormantPlayer==="+dataQuery);

        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            conn = DBManager.getInstance().getDBConnection("read");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(dataQuery);

            while (rs.next())
            {
                String mobile =rs.getString(1);
                String mobile_no;
                if(mobile.startsWith("254") || mobile.startsWith("+254"))
                {
                    mobile_no="0"+mobile.substring(3);
                }
                else
                {
                    mobile_no=mobile;
                }
                String regdate =sdf.format(rs.getTimestamp(2));
                String balanceRM = rs.getString(3);
                String balanceBM = rs.getString(4);
                String lastactivedate= rs.getString(5);

                dataObj  = new JSONObject();
                dataObj.put("Mobile", mobile_no);
                dataObj.put("Registration_Date", regdate);
                dataObj.put("BalanceRM", balanceRM);
                dataObj.put("BalanceBM", balanceBM);
                dataObj.put("LastActiveDate", lastactivedate);
                dataArray.put(dataObj);
            }

            if(dataArray.length()==0)
            {
                dataObj  = new JSONObject();
                dataObj.put("ID", "0");
                dataObj.put("Mobile", "0");
                dataObj.put("Registration_Date", "0");
                dataObj.put("BalanceRM", "0");
                dataObj.put("BalanceBM", "0");
                dataObj.put("LastActiveDate", "0");
                dataArray.put(dataObj);
            }
        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getDormantPlayer=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
    
}
