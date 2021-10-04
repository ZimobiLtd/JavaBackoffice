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
public class ActivePlayersImpl {
    
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    public ActivePlayersImpl()
    {
        
    }
    
    
    public JSONArray getActivePlayer(String from,String to,String filters,String betsCount)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        String dataQuery = "SELECT DISTINCT msisdn,registration_date,(SELECT count(Play_Bet_ID) FROM player_bets WHERE Play_Bet_Mobile= msisdn " + filters + ") as Bet_Counts , "
            + "(SELECT sum(Play_Bet_Stake) FROM player_bets WHERE Play_Bet_Mobile=msisdn and Play_Bet_Status in(201,202,203)) as stake, "
            + "(SELECT  ifnull(sum(Play_Bet_Possible_Winning),0) FROM player_bets WHERE Play_Bet_Mobile=msisdn and Play_Bet_Status =202) as payout, "
            + "(select ifnull(((SELECT sum(Play_Bet_Stake) FROM player_bets WHERE Play_Bet_Mobile=msisdn and Play_Bet_Status  in(201,202,203)) - "
            + "(SELECT sum(Play_Bet_Possible_Winning) FROM player_bets WHERE Play_Bet_Mobile=msisdn and Play_Bet_Status =202) ),0) )as net "
            + "FROM player WHERE msisdn IN (SELECT Play_Bet_Mobile FROM player_bets "
            + "where date(Play_Bet_Timestamp) BETWEEN '" + from + "' and '" + to + "'  " + filters + ") having (SELECT count(Play_Bet_ID) FROM player_bets WHERE Play_Bet_Mobile= msisdn )>20  ORDER BY  Bet_Counts desc";

            System.out.println("getActivePlayer==="+dataQuery);

        try
        {
            conn = DBManager.getInstance().getDBConnection("read");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(dataQuery);

            while (rs.next())
            {
                String mobile =rs.getString(1);
                String regdate = sdf.format(rs.getTimestamp(2));
                String betscount = rs.getString(3);
                String totalstake = rs.getString(4);
                String payout= rs.getString(5);
                String net= rs.getString(6);

                dataObj  = new JSONObject();
                dataObj.put("Mobile", "0"+mobile.substring(3));
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
