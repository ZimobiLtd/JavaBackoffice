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
public class SlotGamesImpl {
    
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    public SlotGamesImpl()
    {
        
    }
    
    
    public JSONArray getSlotGames()
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String query = "";
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();
        query = "select slot_game_name,slot_game_id,slot_game_category,slot_category,if(slot_status=0,'Inactive','Active') as status from slot_games order by slot_id asc";
        System.out.println("getSlotGames==="+query);

        try
        {
            conn = DBManager.getInstance().getDBConnection("read");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next())
            {
                dataObj  = new JSONObject();
                String slotGameName = rs.getString(1);
                String slotGameID = rs.getString(2);
                String slotGameCategory = rs.getString(3);
                String slotCategory = rs.getString(4);
                String status = rs.getString(5);

                dataObj.put("SlotGameName", slotGameName);
                dataObj.put("SlotGameID", slotGameID);
                dataObj.put("SlotGameCategory", slotGameCategory);
                dataObj.put("SlotCategory", slotCategory);
                dataObj.put("SlotGameStatus", status);

                dataArray.put(dataObj);
            }
            
            if(dataArray.length()==0)
            {
                dataObj  = new JSONObject();
                dataObj.put("SlotGameName", "0");
                dataObj.put("SlotGameID", "0");
                dataObj.put("SlotGameCategory", "0");
                dataObj.put("SlotCategory", "0");
                dataObj.put("SlotGameStatus", "0");
                dataArray.put(dataObj);
            }

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getSlotGames=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
    
    
    
    public int updateSlotGameStatus(String slotGameID,String status) 
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        int responseStatus=500;
        String query = "update slot_games set slot_status="+status+" where slot_game_id="+slotGameID+" limit 1";
        System.out.println("updateSlotGameStatus==="+query);
        
        try
        {
            conn = DBManager.getInstance().getDBConnection("write");
            stmt = conn.createStatement();
            int i=stmt.executeUpdate(query);
            if(i > 0)
            {
               responseStatus=200;  
            }            
        }
        catch (SQLException ex) 
        {
            System.out.println("Error updateSlotGameStatus=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }
        return responseStatus;    
    }
    
    
    public int addSlotGame(String slotGameName, String slotGameID, String slotGameCategory, String slotCategory) 
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        int status=500; 
        String query = "insert into slot_games (slot_game_name,slot_game_id,slot_game_category,slot_category) values(?,?,?,?)";

        try
        {
            conn = DBManager.getInstance().getDBConnection("write");
            stmt = conn.createStatement();

            ps = conn.prepareStatement(query);
            ps.setString(1,slotGameName );
            ps.setInt(2,Integer.valueOf(slotGameID)); 
            ps.setString(3,slotGameCategory ); 
            ps.setString(4,slotCategory);  
            int i=ps.executeUpdate();
            if(i > 0)
            {
                status=200;
            }
        }
        catch (SQLException ex) 
        {
            System.out.println("Error addSlotGame=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }
    return status;    
    }
    
    
    public int validateSlotGame(String slotGameID)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        int count=0;
        String query = "select count(slot_id) from slote_games where slot_game_id like ="+slotGameID+" ";
        System.out.println("validateSlotGame==="+query);

        try
        {
            conn = DBManager.getInstance().getDBConnection("read");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next())
            {
               count = rs.getInt(1);
            }
         }
        catch (SQLException ex) 
        {
            System.out.println("Error validateSlotGame=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return count;
    }
     
}
