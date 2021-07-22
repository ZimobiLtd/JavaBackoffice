/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Implimentation.GamingImplimentation;

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
public class GamesExposerImpl {
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public GamesExposerImpl()
    {
        
    }
    
    public JSONArray getPlayersLiability(String fromDate,String toDate)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String dataQuery = "";
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            dataQuery = "select B.Mul_Match_ID,B.Mul_EventTime, B.Mul_Sportname, B.Mul_Tournament, B.Mul_Event, " +
                        "(select Mar_Name from Active_markets where Mar_ID = B.Mul_Market_ID group by Mar_ID),B.Mul_Prediction, " +
                        "count(Mul_ID),sum(Play_Bet_Possible_Winning) from player_bets A inner join multibets B on A.Play_Bet_Group_ID = B.Mul_Group_ID " +
                        "and A.Play_Bet_Status = 201  where B.Mul_Market_ID <> 0 and B.Mul_Match_ID <> 0 and date(A.Play_Bet_Timestamp) " +
                        "between '"+fromDate+"' and '"+toDate+"' and Mul_EventTime is not null group by B.Mul_Prediction, B.Mul_Match_ID order by Mul_EventTime   desc ";
            System.out.println("getGamingPlayerLiability==="+dataQuery);

            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(dataQuery);

            while (rs.next())
            {
                dataObj  = new JSONObject();

                String matchid = rs.getString(1);
                String eventdate =sdf.format(rs.getTimestamp(2));
                String sport = rs.getString(3);
                String championship = rs.getString(4);
                String event = rs.getString(5);
                String market = rs.getString(6);
                String outcome = rs.getString(7);
                String betscount = rs.getString(8);
                String exposure = rs.getString(9);

                dataObj.put("MatchID", matchid);
                dataObj.put("EventDate", eventdate);
                dataObj.put("Sport", sport);
                dataObj.put("Championship", championship);
                dataObj.put("Event", event);
                dataObj.put("Market", market);
                dataObj.put("Outcome", outcome);
                dataObj.put("Betscount", betscount);
                dataObj.put("Exposure", String.format("%.2f", Double.valueOf(exposure)));

                dataArray.put(dataObj);
            }

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getPlayerLiability=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
    
}
