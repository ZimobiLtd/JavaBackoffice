/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Players.PlayersProcessor;

import Database.DBManager;
import Utility.Utility;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.json.JSONException;

/**
 *
 * @author jac
 */
public class PlayerPasswordProcessor {
    
    public PlayerPasswordProcessor()
    {
        
    }
    
    
    public int updatePlayerPassword(String mobile,String pin)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        int status=500; 
        String dataQuery = "update player set password='"+pin+"' where msisdn='"+mobile+"' limit 1 ";

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(dataQuery);

            int i = stmt.executeUpdate(dataQuery);
            if(i>0)
            {
               status=200; 
            }

        }
        catch (SQLException ex) 
        {
            System.out.println("Error monitorAccounts=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return status;
    }


    
    
    public String getPlayerPassword(String mobile)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String data="";
        String query="select password from player where msisdn = '" + mobile + "'";

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) 
            {
                data = rs.getString(1);
            }

        }
        catch (SQLException ex) 
        {
            System.out.println("Error monitorAccounts=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return data;
    }
    
}
