/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Implimentation.SystemSettingsImplimentation;

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
public class SystemUsersImpl {
    
    public SystemUsersImpl()
    {
        
    }
    
    
    public JSONArray getUsers(String user)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;      
        String query="";

        if (user.equalsIgnoreCase("")) 
        {
            query = "select username, firstname, lastname, emailaddress, (case when blocked=1 then 'Active' when blocked=0 then 'Inactive' end ), createdon, "
                    + "(select username from users where userid = A.createdby LIMIT 1) as 'createdBy', modifiedOn,"
                    + "(select username from users where userid = A.modifiedBy LIMIT 1) as 'modifieddBy',ifnull(Role,'No Role'),phonenumber "
                    + "from users A inner join recordstatus  "
                    + "group by userid order by createdon desc,blocked desc";//on statusid = userstatus
        } 
        else
        {
            query = "select username, firstname, lastname, emailaddress, (case when blocked=1 then 'Active' when blocked=0 then 'Inactive' end), createdon, "
                    + "(select username from users where userid = A.createdby LIMIT 1) as 'createdBy', modifiedOn,"
                    + "(select username from users where userid = A.modifiedBy LIMIT 1) as 'modifieddBy',ifnull(Role,'No Role'),phonenumber "
                    + "from users A inner join recordstatus where username like '%" + user + "%' "
                    + "group by userid order by createdon desc,blocked desc";//on statusid = userstatus and
        }

        System.out.println("getUsers==="+query);

        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            conn = DBManager.getInstance().getDBConnection("read");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next())
            {
               String username = rs.getString(1);
               String fname = rs.getString(2);
               String lname = rs.getString(3);
               String email = rs.getString(4);
               String status = rs.getString(5);
               String createdon = rs.getString(6);
               String createdby = rs.getString(7);
               String modifiedon = rs.getString(8);
               String modifiedby = rs.getString(9);
               String role = rs.getString(10);
               String mobile = rs.getString(11);

               dataObj  = new JSONObject();
               dataObj.put("UserName", username);
               dataObj.put("FirstName", fname);
               dataObj.put("LastName", lname);
               dataObj.put("Email", email);
               dataObj.put("Status", status);
               dataObj.put("CreatedOn", createdon);
               dataObj.put("CreatedBy", createdby);
               dataObj.put("ModifiedOn", modifiedon);
               dataObj.put("ModifiedBy", modifiedby);
               dataObj.put("Role", role);
               dataObj.put("Mobile", mobile);
               dataArray.put(dataObj);
            }

            if(dataArray.length()==0)
            {
               dataObj  = new JSONObject();
               dataObj.put("UserName", "0");
               dataObj.put("FirstName", "0");
               dataObj.put("LastName", "0");
               dataObj.put("Email", "0");
               dataObj.put("Status", "0");
               dataObj.put("CreatedOn", "0");
               dataObj.put("CreatedBy", "0");
               dataObj.put("ModifiedOn", "0");
               dataObj.put("ModifiedBy", "0");
               dataObj.put("Role", "0");
               dataObj.put("Mobile", "0");
               dataArray.put(dataObj);
            }


        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getUsers=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }
        
        
        
        
    public JSONArray getUser(String user)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;       
        String query = "select username, firstname, lastname, emailaddress, phonenumber, userstatus, blocked, "
                  + "ifnull(Role,'No Role') from users where username like '%" + user + "%'";
        System.out.println("getUser==="+query);

        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            conn = DBManager.getInstance().getDBConnection("read");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next())
            {
               String username = rs.getString(1);
               String fname = rs.getString(2);
               String lname = rs.getString(3);
               String email = rs.getString(4);
               String mobile = rs.getString(5);
               String status = rs.getString(6);
               String isactive = rs.getString(7);
               String role = rs.getString(8);

               dataObj  = new JSONObject();
               dataObj.put("UserName", username);
               dataObj.put("FirstName", fname);
               dataObj.put("LastName", lname);
               dataObj.put("Email", email);
               dataObj.put("Status", status);
               dataObj.put("Mobile", mobile);
               dataObj.put("IsActive", isactive);
               dataObj.put("Role", role);
               dataArray.put(dataObj);
            }

            if(dataArray.length()==0)
            {
               dataObj  = new JSONObject();
               dataObj.put("UserName", "0");
               dataObj.put("FirstName", "0");
               dataObj.put("LastName", "0");
               dataObj.put("Email", "0");
               dataObj.put("Status", "0");
               dataObj.put("Mobile", "0");
               dataObj.put("IsActive", "0");
               dataObj.put("Role", "0");
               dataArray.put(dataObj);
            }
        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getUser=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }





    public int validateUser(String username,String email)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;      
        int count=0;

        String query = "select count(userid) from users where username='" + username + "'  and emailaddress='" + email + "' ";
        System.out.println("validateUser==="+query);

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
            System.out.println("Error validateUser=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return count;
    }




    public int validateOldPassword (String email,String oldPassword)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;      
        int count=0;
        String query = "select count(userid) from users where password='" + oldPassword + "'  and emailaddress='" + email + "' ";

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
            System.out.println("Error validateOldPassword=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return count;
    }





    public int saveSystemUser(String username, String emailaddress, String phoneNumber, String firstname, String lastname, String password,
        Integer createdBy, Integer modifiedBy, Integer userStatus, Integer isactive, String role) 
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        int respo_id=0; 
        String query = "insert into users (username,password,phonenumber,emailaddress,firstname, "
                    + "lastname,userstatus,blocked,createdby,modifiedby, modifiedon,Role) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";

        java.sql.Timestamp timestamp = new java.sql.Timestamp(new Date().getTime());  
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            conn = DBManager.getInstance().getDBConnection("write");
            stmt = conn.createStatement();

            ps = conn.prepareStatement(query);
            ps.setString(1,username );
            ps.setString(2,password ); 
            ps.setString(3,phoneNumber ); 
            ps.setString(4,emailaddress);               
            ps.setString(5, firstname);
            ps.setString(6,lastname );
            ps.setInt(7,userStatus ); 
            ps.setInt(8,isactive ); 
            ps.setInt(9,createdBy);               
            ps.setInt(10, modifiedBy);
            ps.setTimestamp(11, timestamp);
            ps.setString(12,role );
            respo_id=ps.executeUpdate();

        }
        catch (SQLException ex) 
        {
            System.out.println("Error saveSystemUser=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }
    return respo_id;    
    }





    public int updateSystemUser(String username, String emailaddress, String phoneNumber, String firstname, String lastname,Integer modifiedBy, Integer userStatus, Integer isactive, String role) 
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        int status=0; 
        java.sql.Timestamp timestamp = new java.sql.Timestamp(new Date().getTime());
        String query = "update users set username='"+username+"',emailaddress='"+emailaddress+"',emailaddress='"+emailaddress+"',firstname='"+firstname+"', "
                      + "lastname='"+lastname+"',userstatus="+userStatus+",blocked="+isactive+",modifiedby="+modifiedBy+", modifiedon='"+timestamp+"',Role='"+role+"' where phonenumber='"+phoneNumber+"' ";

        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            conn = DBManager.getInstance().getDBConnection("read");
            stmt = conn.createStatement();
            int i=stmt.executeUpdate(query);
            if(i > 0)
            {
                status=200;
            } 
        }
        catch (SQLException ex) 
        {
            System.out.println("Error updateUserAccess=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }
    return status;    
    }




    public int updateUserAccess(String phoneNumber, Integer userStatus) 
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        int status=500; 
        String query = "update users set blocked="+userStatus+" where phonenumber='"+phoneNumber+"' ";

        java.sql.Timestamp timestamp = new java.sql.Timestamp(new Date().getTime());  
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            conn = DBManager.getInstance().getDBConnection("write");
            stmt = conn.createStatement();
            int i=stmt.executeUpdate(query);
            if(i > 0)
            {
                status=200;
            }
        }
        catch (SQLException ex) 
        {
            System.out.println("Error updateUserAccess=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }
    return status;    
    }




    public int saveCode(String email, String code)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        int status=500;
        String query = "INSERT INTO Verification_Codes (Email,Code) values(?,?) ";

        try
        {
            conn = DBManager.getInstance().getDBConnection("write");
            stmt = conn.createStatement();

            ps = conn.prepareStatement(query);
            ps.setString(1, email);                
            ps.setString(2, code);
            int i=ps.executeUpdate();
            if(i > 0)
            {
                status=200;
            }
        }
        catch (SQLException ex) 
        {
            System.out.println("Error savecode=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

     return status;
    }




    public int verifyCode(String code) 
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        int count =0;
        String query = "select count(ID)  from  Verification_Codes  where  Code='"+code+"' and TIMESTAMPDIFF(MINUTE,now(),`date`)>= -15   ";

        try
        {
            conn = DBManager.getInstance().getDBConnection("read");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) 
            {
               count = rs.getInt(1);
            }

            if(count==1)
            {
                String query1="update Verification_Codes set Status=1 WHERE  Code='"+code+"' ";
                stmt.executeUpdate(query1);
            }
        }
        catch (SQLException ex) 
        {
            System.out.println("Error verifyCode=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }
    return count;
    }




    public String getCodeEmail(String code) 
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String email="";
        String query = "select Email  from  Verification_Codes  where  Code='"+code+"' order by `date` desc limit 1 ";

        try
        {
            conn = DBManager.getInstance().getDBConnection("read");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) 
            {
                email = rs.getString(1);
            }

            if(email.equals(""))
            {
                email="0";
            }
        }
        catch (SQLException ex) 
        {
            System.out.println("Error getCodeEmail=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }
    return email;
    }




    public int changePassword(String email,String newpass,int taskType)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        int success_code=500;String query="";
        try
        {
            conn = DBManager.getInstance().getDBConnection("write");
            stmt = conn.createStatement();
            if(taskType ==0 )
            {
                query = "UPDATE users set  password='"+newpass+"',userstatus=2 where emailaddress='"+email+"'  ";
            }
            else if(taskType ==1 )
            {
                query = "UPDATE users set  password='"+newpass+"',userstatus=1 where emailaddress='"+email+"'  ";
            }
            stmt.executeUpdate(query);

            success_code=200;
        }
        catch (SQLException ex) 
        {
            System.out.println("Error changePassword=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }
    return success_code;
    }




    public int validateEmail(String email)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        int count=0;
        try
        {
            String query = "select count(userid) from users where emailaddress='"+email+"'  ";
            conn = DBManager.getInstance().getDBConnection("read");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {

            count = rs.getInt(1);
        }

        }
        catch (SQLException ex) 
        {
            System.out.println("Error validateEmail=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }
    return count;
    }
    
}
