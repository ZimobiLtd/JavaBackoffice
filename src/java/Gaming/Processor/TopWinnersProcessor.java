/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Gaming.Processor;

import Database.DBManager;
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
public class TopWinnersProcessor {
    
    
    public TopWinnersProcessor()
    {
        
    }
    
    
    public JSONArray getTopWinners(String fromDate,String toDate)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String dataQuery = "";
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            dataQuery = "SELECT DISTINCT Play_Bet_Mobile,Play_Bet_Stake,MAX(Play_Bet_Gross_Possible_Winning) AS 'Payout' ," +
                        "MAX(Play_Bet_Possible_Winning) FROM player_bets " +
                        "WHERE Play_Bet_Status = 202 AND date(Play_Bet_Timestamp) BETWEEN   '"+fromDate+"' AND  '"+toDate+"' " +
                        "GROUP BY Play_Bet_Mobile ORDER BY Play_Bet_Possible_Winning desc  ";
            System.out.println("getTopWinners==="+dataQuery);

            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(dataQuery);

            while (rs.next())
            {
                dataObj  = new JSONObject();

                String mobile = rs.getString(1);
                String stake = rs.getString(2);
                String payout = rs.getString(3);
                String netamount = rs.getString(4);

                dataObj.put("Mobile", mobile);
                dataObj.put("Stake", stake);
                dataObj.put("Payout", payout);
                dataObj.put("NetAmout", netamount);

                dataArray.put(dataObj);
            }

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getTopWinners=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
    
}
