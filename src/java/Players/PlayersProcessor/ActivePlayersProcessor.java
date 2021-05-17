/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Players.PlayersProcessor;

import Database.DBManager;
import static Players.PlayersAPIs.ActivePlayersAPI.sdf;
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
public class ActivePlayersProcessor {
    
    public ActivePlayersProcessor()
    {
        
    }
    
    
    public JSONArray getActivePlayer(String from,String to,String filters)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        String dataQuery = "SELECT DISTINCT msisdn,Name,email,registration_date,(SELECT count(Play_Bet_ID) FROM player_bets WHERE Play_Bet_Mobile= msisdn " + filters + ")AS Bet_Counts , "
            + "(SELECT sum(Play_Bet_Stake) FROM player_bets WHERE Play_Bet_Mobile=msisdn and Play_Bet_Status not in(200,204,205,206)) as stake, "
            + "(SELECT  ifnull(sum(Play_Bet_Possible_Winning),0) FROM player_bets WHERE Play_Bet_Mobile=msisdn and Play_Bet_Status =202) as payout, "
            + "(select ifnull(((SELECT sum(Play_Bet_Stake) FROM player_bets WHERE Play_Bet_Mobile=msisdn and Play_Bet_Status  in(201,202,203)) - "
            + "(SELECT sum(Play_Bet_Possible_Winning) FROM player_bets WHERE Play_Bet_Mobile=msisdn and Play_Bet_Status =202) ),0) )as net "
            + "FROM player WHERE msisdn IN (SELECT Play_Bet_Mobile FROM player_bets "
            + "where date(Play_Bet_Timestamp) BETWEEN '" + from + "' and '" + to + "'  " + filters + ")ORDER BY  Bet_Counts desc";

            System.out.println("getActivePlayer==="+dataQuery);

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
                String regdate = sdf.format(rs.getTimestamp(4));
                String betscount = rs.getString(5);
                String totalstake = rs.getString(6);
                String payout= rs.getString(7);
                String net= rs.getString(8);

                dataObj  = new JSONObject();
                dataObj.put("Mobile", "0"+mobile.substring(3));
                dataObj.put("Name", name);
                dataObj.put("Email", email);
                dataObj.put("Registration_Date", regdate);
                dataObj.put("BetsCount", betscount);
                dataObj.put("TotalStake", totalstake);
                dataObj.put("Payout", payout);
                dataObj.put("Net", net);
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
                dataObj.put("BetsCount", "0");
                dataObj.put("TotalStake", "0");
                dataObj.put("Payout", "0");
                dataObj.put("Net", "0");
                dataArray.put(dataObj);
            }

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getActivePlayer=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
        
        
}
