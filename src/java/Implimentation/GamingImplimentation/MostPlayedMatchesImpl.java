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
public class MostPlayedMatchesImpl {
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    public MostPlayedMatchesImpl()
    {
        
    }
    
    // most played 
    public JSONArray getMostPlayed(String dateFrom,String dateTo)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String dataQuery = "",settledQuery="",placedQuery="";
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            dataQuery = "select A.Mul_Match_ID, A.Mul_EventTime, A.Mul_Sportname, A.Mul_Tournament, A.Mul_Event,sum(A.Mul_Bet_Odd * B.Play_Bet_Stake),ifnull(A.Mul_Finished_Timestamp,'0') " +
                        "from multibets A,player_bets B where A.Mul_Bet_Status = '201'  and  B.Play_Bet_Group_ID = A.Mul_Group_ID  and date(B.Play_Bet_Timestamp) between '" + dateFrom + "' and '" + dateTo + "'  GROUP BY A.Mul_Match_ID  " +
                        "order by count(A.Mul_Match_ID) desc";
            System.out.println("getMostPlayed==="+dataQuery);
            
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(dataQuery);

            while (rs.next())
            {
                dataObj  = new JSONObject();

                String matchid = rs.getString(1);
                String eventdate = sdf.format(rs.getTimestamp(2));
                String sport = rs.getString(3);
                String tornament = rs.getString(4);
                String event = rs.getString(5);
                String totalstake = rs.getString(6);
                String betsettleddate = rs.getString(7);
                settledQuery = "select count(B.Mul_Match_ID) from player_bets A inner join multibets B on A.Play_Bet_Group_ID = B.Mul_Group_ID "
                             + "where B.Mul_Match_ID = '" + matchid + "' and B.Mul_Bet_Status in ('202','203')  ";
                String settledBets = String.valueOf(getMostPlayedCount(conn,stmt,rs,settledQuery));
                placedQuery = "select count(B.Mul_Match_ID) from player_bets A inner join multibets B on A.Play_Bet_Group_ID = B.Mul_Group_ID "
                            + "where B.Mul_Match_ID = '" + matchid + "' and B.Mul_Bet_Status = '201'  ";
                String placedBets = String.valueOf(getMostPlayedCount(conn,stmt,rs,placedQuery));

                dataObj.put("MatchID", matchid);
                dataObj.put("EventDate", eventdate);
                dataObj.put("BetsSettledDate", betsettleddate);
                dataObj.put("Sport", sport);
                dataObj.put("Tornament", tornament);
                dataObj.put("Event", event);
                dataObj.put("TotalStake", totalstake);
                dataObj.put("SettledBets", settledBets);
                dataObj.put("placedBets", placedBets);

                dataArray.put(dataObj);
            }

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error filterMostPlayed=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }
    return dataArray;
    }



    // return record count in a result set
    public int getMostPlayedCount( Connection conn,Statement stmt,ResultSet rs, String query) 
    {
        int count = 0;

        try 
        {
            ResultSet resultSetCount = (ResultSet) stmt.executeQuery(query);
            while (resultSetCount.next()) 
            {
                count = resultSetCount.getInt(1);
            }
        }
        catch (SQLException ex) 
        {
            System.out.println("Error getMostPlayedCount=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(null,null,rs,null);
        }
        return count;
    } 
    
}
