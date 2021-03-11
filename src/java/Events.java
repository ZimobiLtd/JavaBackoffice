/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import java.util.Base64; 
import java.util.Date;
import org.json.JSONArray;

/**
 *
 * @author jac
 */
@WebServlet(urlPatterns = {"/Events"})
public class Events extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
Connection conn;
String response,username ,password,function,maindata;
String type="betting";JSONObject jsonobj=null;JSONArray responseobj  = null;
public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
throws ServletException, IOException {
    resp.setContentType("text/json;charset=UTF-8");
    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.setHeader("Access-Control-Allow-Methods", "POST");


    StringBuffer jb = new StringBuffer();
    String line = null;

    try 
    {

    BufferedReader reader = req.getReader();
    while ((line = reader.readLine()) != null)
    {
        jb.append(line);
    }

        System.out.println("Events===="+jb.toString());
        jsonobj = new JSONObject(jb.toString());
        function=jsonobj.getString("function");
        maindata=jsonobj.getString("data");


    if(function.equals("getEvents"))
    {
        String []respo=initDates();
        String fromdate=respo[0];
        String todate=respo[1];
        System.out.println(fromdate+"==intidates=="+todate);
        responseobj=getEvents(fromdate ,todate);
    }
                   
                   
    if(function.equals("filterEvents"))
    {
        String filters="";
        String[]data=maindata.split("#");
        String from=data[0];
        String to=data[1];  
        String sport=data[2].trim();
        String tornament=data[3].trim();
        
        if (sport.equalsIgnoreCase("any")&& tornament.equalsIgnoreCase("any"))
        {
          responseobj=getEvents(from ,to);  
        }
        else if (sport.equalsIgnoreCase("any")&& !tornament.equalsIgnoreCase("any"))
        {
            filters = " and Torna_Name like '" + tornament + "%' ";
            responseobj=filterEvents(from ,to,filters);
        }
        else if (!sport.equalsIgnoreCase("any")&& tornament.equalsIgnoreCase("any"))
        {
            filters = " and Torna_Sport_Name like '" + sport + "%' ";
            responseobj=filterEvents(from ,to,filters);
        }
        else
        {
            if (!sport.equalsIgnoreCase("null")&& !tornament.equalsIgnoreCase("null")) 
            {
                filters = " and Torna_Name like '" + tornament + "%' and Torna_Sport_Name like '" + sport + "%' ";
            }
            else if (!sport.equalsIgnoreCase("null")&& tornament.equalsIgnoreCase("null") ) 
            {
                filters = " and Torna_Sport_Name like '" + sport + "%' ";
            }
            else if (sport.equalsIgnoreCase("null")&& !tornament.equalsIgnoreCase("null") ) 
            {
                filters = " and Torna_Name like '" + tornament + "%' ";
            }
            
            responseobj=filterEvents(from ,to,filters);
        }
        
        
        
    }
    
    if(function.equals("getOdds"))
    {
        String matchId=maindata;
        
        responseobj=getEventOdds(matchId);
    }
    
    if(function.equals("getTornaments"))
    {
        
        responseobj=getTornaments();
    }
                   
                   
    }catch (Exception ex) { ex.getMessage();}

    
    PrintWriter out = resp.getWriter(); 
    out.print(responseobj);
    }
    
    
        
        
      
      
public JSONArray getEvents(String fromDate,String toDate)
{

    String res="";
    String dataQuery = "";
    JSONObject dataObj  = null;
    JSONArray dataArray = new JSONArray();

    try( Connection conn = new DBManager(type).getDBConnection();
    Statement stmt = conn.createStatement();)
    {

        ResultSet rs=null;

        dataQuery = "select  Torna_Sport_Name, Torna_Cat_Name, Torna_Name, Torna_Match_Event, Torna_Match_Event_Time,  "
                + "Torna_Match_ID, Torna_Sys_Game_ID, (case when Torna_Match_Status=0 then 'Active' when Torna_Match_Status=1 then 'Inactive' end),  "
                + "(select Odd_Mark_Odd from the_odds where Odd_Mark_Match_ID = Torna_Match_ID  "
                + "and Odd_Mark_Desc='Team1' and Odd_Market_ID=1 limit 1) as 'team1', "
                + "(select Odd_Mark_Odd from the_odds where Odd_Mark_Match_ID = Torna_Match_ID  "
                + "and Odd_Mark_Desc='draw' and Odd_Market_ID=1 limit 1) as 'draw', "
                + "(select Odd_Mark_Odd from the_odds where Odd_Mark_Match_ID = Torna_Match_ID  "
                + "and Odd_Mark_Desc='Team2' and Odd_Market_ID=1 limit 1) as 'team2', "
                + "(select Odd_Mark_Odd from the_odds where Odd_Mark_Match_ID = Torna_Match_ID  "
                + "and Odd_Mark_Desc='over 2.5' and Odd_Market_ID=18 limit 1) as 'ov25', "
                + "(select Odd_Mark_Odd from the_odds where Odd_Mark_Match_ID = Torna_Match_ID  "
                + "and Odd_Mark_Desc='under 2.5' and Odd_Market_ID=18 limit 1) as 'un25', "
                + "(select Odd_Mark_Odd from the_odds where Odd_Mark_Match_ID = Torna_Match_ID  "
                + "and Odd_Mark_Desc='yes' and Odd_Market_ID=29 limit 1) as 'gg', "
                + "(select Odd_Mark_Odd from the_odds where Odd_Mark_Match_ID = Torna_Match_ID  "
                + "and Odd_Mark_Desc='no' and Odd_Market_ID=29 limit 1) as 'ng' from tournament "
                + "where date(Torna_Match_Event_Time) between '" + fromDate + "' and '" + toDate + "' and Torna_Match_Status = 0 and "
                + "(select count(Odd_Mark_Table_ID) from the_odds where Odd_Mark_Match_ID = Torna_Match_ID ) > 0 "
                + "order by Torna_Match_Event_Time desc";
        
        System.out.println("getevents==="+dataQuery);

        rs = stmt.executeQuery(dataQuery);


        while (rs.next())
        {
            dataObj  = new JSONObject();
            
            String sportname = rs.getString(1);
            String countryname = rs.getString(2);
            String torna_name = rs.getString(3);
            String event = rs.getString(4);
            String eventtime =sdf.format(rs.getTimestamp(5));
            String torna_match_id = rs.getString(6);
            String torna_sys_game_id = rs.getString(7);
            String matchstatus = rs.getString(8);
            String odd_hometeam = rs.getString(9);
            String odd_draw = rs.getString(10);
            String odd_awayteam = rs.getString(11);
            String odd_ov25 = rs.getString(12);
            String odd_un25 = rs.getString(13);
            String odd_gg = rs.getString(14);
            String odd_ng = rs.getString(15);

            dataObj.put("TornamentMatchID", torna_match_id);
            dataObj.put("GameID", torna_sys_game_id);
            dataObj.put("Country", countryname);
            dataObj.put("Sport", sportname);
            dataObj.put("Tornament", torna_name);
            dataObj.put("Event", event);
            dataObj.put("EventTime", eventtime);
            dataObj.put("MatchStatus", matchstatus);
            dataObj.put("HomeTeamOdd", odd_hometeam);
            dataObj.put("DrawOdd", odd_draw);
            dataObj.put("AwayTeamOdd", odd_awayteam);
            dataObj.put("Ov25Odd", odd_ov25);
            dataObj.put("Un25", odd_un25);
            dataObj.put("GGOdd", odd_gg);
            dataObj.put("NGOdd", odd_ng);

            dataArray.put(dataObj);
        }
                   
         
    rs.close();
    stmt.close();
    conn.close();
    }
    catch(Exception e)
    {

    }

return dataArray;
}
        
        




public JSONArray filterEvents(String fromDate,String toDate,String filters)
{


    
    String res="";
    String dataQuery = "";
    JSONObject dataObj  = null;
    JSONArray dataArray = new JSONArray();

    try( Connection conn = new DBManager(type).getDBConnection();
    Statement stmt = conn.createStatement();)
    {

        ResultSet rs=null;

        dataQuery = "select  Torna_Sport_Name, Torna_Cat_Name, Torna_Name, Torna_Match_Event, Torna_Match_Event_Time,  "
                + "Torna_Match_ID, Torna_Sys_Game_ID, Torna_Match_Status,  "
                + "(select Odd_Mark_Odd from the_odds where Odd_Mark_Match_ID = Torna_Match_ID  "
                + "and Odd_Mark_Desc='Team1' and Odd_Market_ID=1 limit 1) as 'team1', "
                + "(select Odd_Mark_Odd from the_odds where Odd_Mark_Match_ID = Torna_Match_ID  "
                + "and Odd_Mark_Desc='draw' and Odd_Market_ID=1 limit 1) as 'draw', "
                + "(select Odd_Mark_Odd from the_odds where Odd_Mark_Match_ID = Torna_Match_ID  "
                + "and Odd_Mark_Desc='Team2' and Odd_Market_ID=1 limit 1) as 'team2', "
                + "(select Odd_Mark_Odd from the_odds where Odd_Mark_Match_ID = Torna_Match_ID  "
                + "and Odd_Mark_Desc='over 2.5' and Odd_Market_ID=18 limit 1) as 'ov25', "
                + "(select Odd_Mark_Odd from the_odds where Odd_Mark_Match_ID = Torna_Match_ID  "
                + "and Odd_Mark_Desc='under 2.5' and Odd_Market_ID=18 limit 1) as 'un25', "
                + "(select Odd_Mark_Odd from the_odds where Odd_Mark_Match_ID = Torna_Match_ID  "
                + "and Odd_Mark_Desc='yes' and Odd_Market_ID=29 limit 1) as 'gg', "
                + "(select Odd_Mark_Odd from the_odds where Odd_Mark_Match_ID = Torna_Match_ID  "
                + "and Odd_Mark_Desc='no' and Odd_Market_ID=29 limit 1) as 'ng' from tournament "
                + "where Torna_Match_Event_Time between '" + fromDate + "' and '" + toDate + "' and Torna_Match_Status = 0 and "
                + "(select count(Odd_Mark_Table_ID) from the_odds where Odd_Mark_Match_ID = Torna_Match_ID ) > 0  "+filters+" "
                + "order by Torna_Match_Event_Time desc";
        
        System.out.println("getevents==="+dataQuery);

        rs = stmt.executeQuery(dataQuery);


        while (rs.next())
        {
            dataObj  = new JSONObject();
            
            String sportname = rs.getString(1);
            String countryname = rs.getString(2);
            String torna_name = rs.getString(3);
            String event = rs.getString(4);
            String eventtime = sdf.format(rs.getTimestamp(5));
            String torna_match_id = rs.getString(6);
            String torna_sys_game_id = rs.getString(7);
            String matchstatus = rs.getString(8);
            String odd_hometeam = rs.getString(9);
            String odd_draw = rs.getString(10);
            String odd_awayteam = rs.getString(11);
            String odd_ov25 = rs.getString(12);
            String odd_un25 = rs.getString(13);
            String odd_gg = rs.getString(14);
            String odd_ng = rs.getString(15);

            dataObj.put("TornamentMatchID", torna_match_id);
            dataObj.put("GameID", torna_sys_game_id);
            dataObj.put("Sport", sportname);
            dataObj.put("Country", countryname);
            dataObj.put("Tornament", torna_name);
            dataObj.put("Event", event);
            dataObj.put("EventTime", eventtime);
            dataObj.put("MatchStatus", matchstatus);
            dataObj.put("HomeTeamOdd", odd_hometeam);
            dataObj.put("DrawOdd", odd_draw);
            dataObj.put("AwayTeamOdd", odd_awayteam);
            dataObj.put("Ov25Odd", odd_ov25);
            dataObj.put("Un25", odd_un25);
            dataObj.put("GGOdd", odd_gg);
            dataObj.put("NGOdd", odd_ng);

            dataArray.put(dataObj);
        }
                   
         
    rs.close();
    stmt.close();
    conn.close();
    }
    catch(Exception e)
    {

    }

return dataArray;
}






public JSONArray getEventOdds(String matchId)
{

    String res="";
    String dataQuery = "";
    JSONObject dataObj  = null;
    JSONArray dataArray = new JSONArray();

    try( Connection conn = new DBManager(type).getDBConnection();
    Statement stmt = conn.createStatement();)
    {

        ResultSet rs=null;

        dataQuery = "select Odd_Mark_Table_ID, Odd_Mark_ID, Odd_Mark_Desc, Odd_Mark_Odd from the_odds where Odd_Mark_Match_ID = '" + matchId + "'";
        
        System.out.println("getEventOdds==="+dataQuery);

        rs = stmt.executeQuery(dataQuery);


        while (rs.next())
        {
            dataObj  = new JSONObject();
            
            String id = rs.getString(1);
            String parentmarket = rs.getString(2);
            String market = rs.getString(3);
            String odd = rs.getString(4);

            dataObj.put("ID", id);
            dataObj.put("ParentMarket", parentmarket);
            dataObj.put("Market", market);
            dataObj.put("Odd", odd);

            dataArray.put(dataObj);
        }
                   
         
    rs.close();
    stmt.close();
    conn.close();
    }
    catch(Exception e)
    {

    }

return dataArray;
}





public JSONArray getTornaments()
{

    String res="";
    String dataQuery = "";
    JSONObject dataObj  = null;
    JSONArray dataArray = new JSONArray();

    try( Connection conn = new DBManager(type).getDBConnection();
    Statement stmt = conn.createStatement();)
    {

        ResultSet rs=null;

        dataQuery = "select  group_concat( distinct Torna_Name  separator '#') from tournament ";
        
        System.out.println("getTornamets==="+dataQuery);

        rs = stmt.executeQuery(dataQuery);


        while (rs.next())
        {
            dataObj  = new JSONObject();
            
            String tornaments = rs.getString(1);
            dataObj.put("Tornaments", tornaments);

            dataArray.put(dataObj);
        }
                   
         
    rs.close();
    stmt.close();
    conn.close();
    }
    catch(Exception e)
    {

    }

return dataArray;
}
        
        
        
        
public  ArrayList<String> loopdate(String fromDate, String toDate) 
{
    ArrayList<String> dates = new ArrayList<>();
    try 
    {

        Date startDate = sdf.parse(fromDate);
        Date endDate = sdf.parse(toDate);
        long interval = 24 * 1000 * 60 * 60; // 1 hour in millis

        long endTime = startDate.getTime(); // create your endtime here, possibly using Calendar or Date
        long curTime = endDate.getTime();
        while (endTime <= curTime) 
        {
System.out.println("==="+new Date(endTime));
dates.add(sdf.format(new Date(endTime)));
endTime += interval;
        }

    } catch (Exception ex) {

    }

return dates;
}




public String[] initDates() 
{
    String []data=null;

    try 
    {

        Date startDate = new Date();//sdf.parse(fromDate);
        long curTime = startDate.getTime();
        long interval = (24 * 1000 * 60 * 60)*5;
        curTime -= interval;
        String fromdate=sdf.format(new Date(curTime));
        String todate=sdf.format(startDate);

        data=new String[]{fromdate,todate};//fromdate+"#"+todate ;

    } catch (Exception ex) {

    }

return data;
}
      
      

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
   @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
