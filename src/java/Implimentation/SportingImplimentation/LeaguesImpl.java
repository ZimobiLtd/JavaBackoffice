/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Implimentation.SportingImplimentation;

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
public class LeaguesImpl {
    
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    public LeaguesImpl()
    {
        
    }
    
    
    public JSONArray getLeagues()
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String query = "";
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();
        query = "select league_id,league,if(league_status=0,'Inactive','Active') from leagues order by league_status desc";
        System.out.println("getLeagues==="+query);

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next())
            {
                dataObj  = new JSONObject();
                String leagueID = rs.getString(1);
                String league = rs.getString(2);
                String leagueStatus = rs.getString(3);

                dataObj.put("LeagueID", leagueID);
                dataObj.put("League", league);
                dataObj.put("LeagueStatus", leagueStatus);

                dataArray.put(dataObj);
            }

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getLeagues=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
    
    
    
    public int updateLeagueStatus(String id,String status) 
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        int responseStatus=500;
        String query = "update leagues set league_status="+status+" where league_id="+id+" limit 1";
        System.out.println("updateLeagueStatus==="+query);
        
        
        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            int i=stmt.executeUpdate(query);
            if(i > 0)
            {
               responseStatus=200;  
            }            
        }
        catch (SQLException ex) 
        {
            System.out.println("Error updateLeagueStatus=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }
        return responseStatus;    
    }
     
}
