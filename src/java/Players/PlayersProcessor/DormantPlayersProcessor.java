/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Players.PlayersProcessor;

import Database.DBManager;
import static Players.PlayersAPIs.DormantPlayersAPI.sdf;
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
public class DormantPlayersProcessor {
    
    public DormantPlayersProcessor()
    {
        
    }
    
    
    public JSONArray getDormantPlayer(String from,String to)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String dataQuery = "SELECT msisdn,NAME,email,registration_date,Player_Balance,Bonus_Balance, "
                + "(SELECT ifnull(max(Play_Bet_Timestamp),'0') FROM player_bets WHERE Play_Bet_Mobile=msisdn and date(Play_Bet_Timestamp) not between '" + from + "' and '" + to + "' GROUP BY  Play_Bet_Mobile)"
                + "FROM player WHERE msisdn NOT  IN ( SELECT Play_Bet_Mobile FROM player_bets  where date(Play_Bet_Timestamp) between '" + from + "' and '" + to + "' ) and NAME is not null GROUP BY msisdn ";
        System.out.println("getDormantPlayer==="+dataQuery);

        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(dataQuery);

            while (rs.next())
            {
                String mobile =rs.getString(1);
                String name = rs.getString(2);
                String email = rs.getString(3);
                String regdate =sdf.format(rs.getTimestamp(4));
                String balanceRM = rs.getString(5);
                String balanceBM = rs.getString(6);
                String lastactivedate= rs.getString(7);

                dataObj  = new JSONObject();
                dataObj.put("Mobile", "0"+mobile.substring(3));
                dataObj.put("Name", name);
                dataObj.put("Email", email);
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
                dataObj.put("Name", "0");
                dataObj.put("Email", "0");
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
