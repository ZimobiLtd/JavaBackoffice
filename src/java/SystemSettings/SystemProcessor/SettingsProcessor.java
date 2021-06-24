/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemSettings.SystemProcessor;

import Database.DBManager;
import Utility.Utility;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author jac
 */
public class SettingsProcessor {
    
    public SettingsProcessor()
    {
        
    }
    
    
    public JSONArray getSecurityRoles()
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String query = "select id, name from securityrole where rolestatus = 1";
        System.out.println("getSecurityRoles==="+query);

        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next())
            {
               String id = rs.getString(1);
               String name = rs.getString(2);

               dataObj  = new JSONObject();
               dataObj.put("RoleID", id);
               dataObj.put("Name", name);
               dataArray.put(dataObj);
            }

            if(dataArray.length()==0)
            {
               dataObj  = new JSONObject();
               dataObj.put("RoleID", "0");
               dataObj.put("Name", "0");
               dataArray.put(dataObj);
            }
        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error filterEvents=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }




    public JSONArray getUserRoles(String user)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String query = "select Role from users where username like '%" + user + "%'";
        System.out.println("getUserRoles==="+query);

        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next())
            {
               String name = rs.getString(1);

               dataObj  = new JSONObject();
               dataObj.put("Name", name);
               dataArray.put(dataObj);
            }

            if(dataArray.length()==0)
            {
               dataObj  = new JSONObject();
               dataObj.put("Name", "0");
               dataArray.put(dataObj);
            }
        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getUserRoles=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }




    public JSONArray getSystemSettings()
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String query = "select id,settingname, settingvalue, description,createdon, modifiedon,createdby,modifiedby, "
                    + "(select username from users where userid = systemsetting.createdby LIMIT 1) as 'createdBy'," 
                    + "(select username from users where userid = systemsetting.modifiedby LIMIT 1) as 'modifieddBy'  from systemsetting order by createdon desc";
        System.out.println("getSystemSettings==="+query);

        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next())
            {
                String id = rs.getString(1);
               String name = rs.getString(2);
               String value = rs.getString(3);
               String description = rs.getString(4);
               String createdon = rs.getString(5);
               String modifiedon = rs.getString(6);
               String createdby = rs.getString(7);
               String modifiedby = rs.getString(8);
               String createdbyuser = rs.getString(9);
               String modifiedbyuser = rs.getString(10);

               dataObj  = new JSONObject();
               dataObj.put("ID", id);
               dataObj.put("Name", name);
               dataObj.put("Value", value);
               dataObj.put("Description", description);
               dataObj.put("Createdon", createdon);
               dataObj.put("Modifiedon", modifiedon);
               dataObj.put("CreatedBy", createdby);
               dataObj.put("ModifiedBy", modifiedby);
               dataObj.put("CreatedByUser", createdbyuser);
               dataObj.put("ModifiedByUser", modifiedbyuser);

               dataArray.put(dataObj);
            }

            if(dataArray.length()==0)
            {
               dataObj  = new JSONObject();
               dataObj.put("ID", "0");
               dataObj.put("Name", "0");
               dataObj.put("Value", "0");
               dataObj.put("Description", "0");
               dataObj.put("Createdon", "0");
               dataObj.put("Modifiedon", "0");
               dataObj.put("CreatedBy", "0");
               dataObj.put("ModifiedBy", "0");
               dataObj.put("CreatedByUser", "0");
               dataObj.put("ModifiedByUser", "0");
               dataArray.put(dataObj);
            }

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getSystemSettings=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }




    public JSONArray getSystemSettingsByID(String id)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String query = "select id,settingname, settingvalue, description from systemsetting where id=" + id + " ";
        System.out.println("getSystemSettingsByName==="+query);

        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next())
            {
               String rec_id = rs.getString(1);
               String name = rs.getString(2);
               String value = rs.getString(3);
               String description = rs.getString(4);

               dataObj  = new JSONObject();
               dataObj.put("ID", rec_id);
               dataObj.put("Name", name);
               dataObj.put("Value", value);
               dataObj.put("Description", description);

               dataArray.put(dataObj);
            }

            if(dataArray.length()==0)
            {
               dataObj  = new JSONObject();
               dataObj.put("ID", "0");
               dataObj.put("Name", "0");
               dataObj.put("Value", "0");
               dataObj.put("Description", "0");
               dataArray.put(dataObj);
            }

        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getSystemSettingsByID=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }




    public int saveSystemSettings(String name, String value, String descrition, String createdby, String modifiedby) 
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        int respo_id=0; 

        String query = "INSERT INTO systemsetting (settingname,settingvalue,description,createdon, modifiedon,createdby, modifiedby) VALUES(?,?,?,?,?,?,?)";
        java.sql.Timestamp timestamp = new java.sql.Timestamp(new Date().getTime());  
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();
        
        try
        {
            conn = new DBManager().getDBConnection();
            ps = conn.prepareStatement(query);
            
            ps.setString(1,name );
            ps.setString(2,value ); 
            ps.setString(3,descrition ); 
            ps.setTimestamp(4, timestamp);
            ps.setTimestamp(5, timestamp);
            ps.setInt(6,Integer.valueOf(createdby) ); 
            ps.setInt(7,Integer.valueOf(modifiedby) ); 
            respo_id=ps.executeUpdate();

        }
        catch (SQLException ex) 
        {
            System.out.println("Error saveSystemSettings=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }
    return respo_id;    
    }




    public int editSystemSettings(String id,String name, String value, String description,String modifiedby) 
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        int respo_id=0; 
        java.sql.Timestamp timestamp = new java.sql.Timestamp(new Date().getTime());
        String query = "update systemsetting set settingname='"+name+"',settingvalue='"+value+"',description='"+description+"',modifiedon='"+timestamp+"',modifiedby="+modifiedby+" where id= "+id+" ";
        System.out.println("editSystemSettings==="+query);
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();
        
        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            int i=stmt.executeUpdate(query);
            if(i > 0)
            {
               respo_id=1;  
            }            
        }
        catch (SQLException ex) 
        {
            System.out.println("Error editSystemSettings=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }
    return respo_id;    
    }



    public int validateSettings(String name)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        int count=0;
        String query = "select count(id) from systemsetting where settingname like '%" + name + "%' ";
        System.out.println("validateSettings==="+query);

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next())
            {
               count = rs.getInt(1);
            }
         }
        catch (SQLException ex) 
        {
            System.out.println("Error editSystemSettings=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return count;
    }
    
}
