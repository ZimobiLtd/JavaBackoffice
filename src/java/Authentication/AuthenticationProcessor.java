/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Authentication;

import Database.DBManager;
import Utility.Utility;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Date;

/**
 *
 * @author jac
 */
public class AuthenticationProcessor {
    
    
    public void AuthenticationProcessor()
    {
        
    }
    
    
    public String authenticateUser(String username,String password)
    {
        String response="";
        int usercount =0, passcount =0;
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String userquery="select count(userid) from users where username='"+username+"' and blocked=1  limit 1 ";
        String passquery="select count(userid) from users where password = '"+password+"' and username='"+username+"' and blocked=1  limit 1 ";
        String query="select username,phonenumber,emailaddress,Role,userid,userstatus from users where username='"+username+"' and password='"+password+"' and blocked=1  limit 1 ";

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(userquery);
            while (rs.next())
            {
               usercount =rs.getInt(1);
            }
            if(usercount==1)
            {
                rs = stmt.executeQuery(passquery);
                while (rs.next())
                {
                    passcount =rs.getInt(1);
                }

                if(passcount==1)
                {
                    rs = stmt.executeQuery(query);
                    while (rs.next())
                    {
                        String user =rs.getString(1);
                        String mobile =rs.getString(2);
                        String email =rs.getString(3);
                        String role =rs.getString(4);
                        String userid =rs.getString(5);
                        String userstatus =rs.getString(6);
                        String seed=new Date().getTime()+username+password;
                        String token=generateTocken(seed);
                        response=user+"#"+mobile+"#"+email+"#"+role+"#"+userid+"#"+userstatus+"#"+token;
                    }
                }
                else
                {
                    response="Invalid Username or Password";
                }
           }
           else
           {
                 response="Invalid Username or Password";
           }
        } catch (SQLException ex) 
        {
            System.out.println("Error authenticateUser=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return response;
    }



    public int saveLogTrail(String username,String mobile,String email,String role,String comment)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        int status=500;
        String query = "INSERT INTO logintrail (username,mobile,email,role,comment) values(?,?,?,?,?) ";

        try
        { 
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();

            ps = conn.prepareStatement(query);
            ps.setString(1,username );
            ps.setString(2,mobile ); 
            ps.setString(3,email ); 
            ps.setString(4,role );
            ps.setString(5,comment); 
            int i=ps.executeUpdate();
            if(i>0)
            {
                status=200;                
            }

        } catch (SQLException ex) 
        {
            System.out.println("Error saveLogTrail=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return status;
   }



    public String generateTocken(String seed)
    {
       String token = null;
       Base64.Encoder encoder = Base64.getEncoder();
       token = encoder.encodeToString(seed.getBytes());
       //save token
    return token;
    }
    
}
