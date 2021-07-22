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
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 *
 * @author jac
 */
public class JackpotProcessor {
    
    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public JackpotProcessor()
    {
    }
    
    
    public int validateJackpotCreation()
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        int count=0;
        String query = "select count(id) from jackpot where  date(start_date)=curdate() ";
        System.out.println("validateJackpotCreation==="+query);

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
            System.out.println("Error validateJackpotCreation=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return count;
    }
    
    
    public int highlightJackpotGames(String []matchIDs)
    {
        int status=500;
        String matchIDsValues="0";
        StringBuilder sb= new StringBuilder();
        for(String matchID : matchIDs)
        {
            sb.append(","+matchID);
        }
        matchIDsValues=sb.substring(1);
        
        String []datesData=getMinMaxDate(matchIDsValues);
        String minDate=datesData[0];
        String maxDate=datesData[1];
        
        int resultID=createJackpotEntry(minDate,maxDate,matchIDs);
        if(resultID == 500500)
        {
            status=500;
        }
        else
        {
            int markStatus=markJackpotGames(resultID,matchIDsValues,matchIDs.length);
            if(markStatus == 200)
            {
                status=200;
            }
            else
            {
                status=500;
            }
        }
        
        return status;
    }
    
    
    
    
    public String[] getMinMaxDate(String matchIDsValues)
    {
        String []dates=null;
        
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String query = "select date_sub(min(Torna_Match_Event_Time),INTERVAL 15 MINUTE),max(Torna_Match_Event_Time) from tournament where Torna_Match_ID in ("+matchIDsValues+") limit 1";
        System.out.println("getMinMaxDate==="+query);


        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next())
            {
               String minDate = rs.getString(1);
               String maxDate = rs.getString(2);
               dates=new String []{minDate,maxDate};
            }

        }
        catch (SQLException ex) 
        {
            System.out.println("Error getMinMaxDate=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dates;
    }
    
    
    
    
    public int createJackpotEntry(String minDate,String maxDate, String []matchIDs) 
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        int status=500500; 

        String query = "insert into jackpot (display_name,start_date,end_date) value(?,?,?)";
        
        try
        {
            java.sql.Timestamp startDate = new java.sql.Timestamp(sdf.parse(minDate).getTime());  
            java.sql.Timestamp endDate = new java.sql.Timestamp(sdf.parse(maxDate).getTime()); 
            
            conn = new DBManager().getDBConnection();
            stmt=conn.createStatement();
            ps = conn.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
            
            ps.setString(1,"Daily Jackpot");
            ps.setTimestamp(2, startDate);
            ps.setTimestamp(3, endDate);
            
            int i=ps.executeUpdate();
            if(i > 0)
            {
                rs=ps.getGeneratedKeys();
                if(rs.next())
                {
                   status=rs.getInt(1);
                }
            }
        }
        catch (SQLException | ParseException ex) 
        {
            System.out.println("Error createJackpotEntry=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }
    return status;    
    }
    
    
    
    
    public int markJackpotGames(int jackpotID,String matchIDsValues,int limit) 
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        int status=500; 
        String query = "update tournament set jackpot_status=1,Jackpot_Ref_No="+jackpotID+" where Torna_Match_ID in ("+matchIDsValues+") limit "+limit+" ";
        System.out.println("markJackpotGames==="+query);
        
        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            
            int i=stmt.executeUpdate(query);
            if(i > 0)
            {
               status=200;  
            }            
        }
        catch (SQLException ex) 
        {
            System.out.println("Error markJackpotGames=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }
    return status;    
    }
    
    
    
    
    public int unMarkJackpotGames(int jackpotID) 
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        int status=500; 
        String query = "update tournament set jackpot_status=0,Jackpot_Ref_No=0  where Jackpot_Ref_No="+jackpotID+" ";
        System.out.println("unMarkJackpotGames==="+query);
        
        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();
            
            int i=stmt.executeUpdate(query);
            if(i > 0)
            {
               status=200;  
            }            
        }
        catch (SQLException ex) 
        {
            System.out.println("Error unMarkJackpotGames=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }
    return status;    
    }
    
    
}
