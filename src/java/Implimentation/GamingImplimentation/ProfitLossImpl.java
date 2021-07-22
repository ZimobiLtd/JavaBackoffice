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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author jac
 */
public class ProfitLossImpl {
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    public ProfitLossImpl()
    {
        
    }
    
    
    public JSONArray getProfitLost(String toDate)
    {
        ResultSet rs=null;Connection conn=null;Statement stmt=null;PreparedStatement ps=null;
        String querySettledBets,queryWinnings;
        JSONObject dataObj  = null;
        JSONArray dataArray = new JSONArray();

        try
        {
            conn = new DBManager().getDBConnection();
            stmt = conn.createStatement();

            ArrayList<String> dates= loopDate(toDate);
            for (int i=0;i<dates.size();i++) 
            {
                String date=dates.get(i);
                //convert String to LocalDate
                LocalDate localDate = LocalDate.parse(date);
                Month month = localDate.getMonth();

                querySettledBets = "select ifnull(sum(Play_Bet_Stake),0) from player_bets where Play_Bet_Status in(201,202) "
                    + " and date(Play_Bet_Timestamp) between '"+subDate(date) +"' and '" + date + "' and  Play_Bet_Settle_Bet_Time is not null  ";

                queryWinnings = "select ifnull(sum(Play_Bet_Possible_Winning),0) from player_bets where Play_Bet_Status = 202 "
                    + " and  date(Play_Bet_Timestamp) between '"+subDate(date) +"' and '" + date + "' and  Play_Bet_Settle_Bet_Time is not null ";

                System.out.println("querySettledBets=== "+querySettledBets);
                System.out.println("queryWinnings=== "+queryWinnings);

                int settledBets=getProfitLostData( conn,stmt,rs,querySettledBets) ;
                int winnings=getProfitLostData( conn,stmt,rs,queryWinnings) ;
                int profit=settledBets-winnings;

                dataObj  = new JSONObject();
                dataObj.put(String.valueOf(month), profit+"#"+winnings);
                dataArray.put(dataObj);
            }
        }
        catch (SQLException | JSONException ex) 
        {
            System.out.println("Error getGamingSummary=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(conn,stmt,rs,ps);
        }

    return dataArray;
    }



    // return record count in a result set
    public int getProfitLostData( Connection conn,Statement stmt,ResultSet rs, String query) 
    {
        int count = 0;
        try 
        {
            rs = (ResultSet) stmt.executeQuery(query);

            while (rs.next()) 
            {
                count = rs.getInt(1);
            }
        }
        catch (SQLException ex) 
        {
            System.out.println("Error getProfitLostData=== "+ex.getMessage());
        }
        finally
        {
            new Utility().doFinally(null,null,rs,null);
        }

        return count;
    } 
    
    
    
    public ArrayList<String> loopDate(String toDate)
    {
        int count=1;int value=0;String newToDate="",newdate="";
        if(newToDate.equals(""))
        {
            newToDate= toDate;
        }
        else
        {
            newToDate =newdate;
        }

        ArrayList<String> dates = new ArrayList<>();
        try
        {
            Date todays_date = sdf.parse(newToDate);
            // convert date to calendar
            Calendar c = Calendar.getInstance();
            c.setTime(todays_date);
            // manipulate date
            while (count <= 12)
            {
                if(count==1)
                {
                    value=0;
                }
                else
                {
                    value=-1;
                }
                c.add(Calendar.MONTH, value);                        
                // convert calendar to date
                Date dt = c.getTime();
                newdate= sdf.format(dt);
                newToDate=newdate;

                dates.add(newdate);
                count++;
            }
            count=0;
        }
        catch (ParseException ex)
        {
            System.out.println("Error loopDate=== "+ex.getMessage());
        }

    return dates;
    }
        
   

    public String subDate(String date)
    {
        String newdate="";
        ArrayList<String> dates = new ArrayList<>();
        try
        {
            Date todays_date = sdf.parse(date);
            // convert date to calendar
            Calendar c = Calendar.getInstance();
            c.setTime(todays_date);
            // manipulate date
            c.add(Calendar.MONTH, -1);                        
            // convert calendar to date
            Date dt = c.getTime();
            newdate= sdf.format(dt);
        }
        catch (ParseException ex)
        {
            System.out.println("Error subDate=== "+ex.getMessage());
        }
    return newdate;
    }


    
    public String subDates(String date) 
    {
        String data=null;
        try 
        {
            Date startDate = sdf.parse(date);
            long curTime = startDate.getTime();
            long interval = (24 * 1000 * 60 * 60)*30;
            curTime -= interval;
            String findate=sdf.format(new Date(curTime));

            data=findate;
            
        } catch (ParseException ex) 
        {
            System.out.println("Error subDates==="+ex.getMessage());
        }

    return data;
    }
    
}
