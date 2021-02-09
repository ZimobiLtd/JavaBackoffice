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
@WebServlet(urlPatterns = {"/GamingPlayerLiability"})
public class Gaming_PlayerLiability extends HttpServlet {

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

        System.out.println("GamingPlayerLiability===="+jb.toString());
        jsonobj = new JSONObject(jb.toString());
        function=jsonobj.getString("function");
        maindata=jsonobj.getString("data");


    if(function.equals("getGamingPlayerLiability"))
    {
        String []respo=initDates();
        String fromdate=respo[0];
        String todate=respo[1];
        System.out.println(fromdate+"==intidates=="+todate);
        responseobj=getPlayerLiability("2017-09-30" ,"2020-10-31");
    }
                   
                   
    if(function.equals("filterGamingPlayerLiability"))
    {
        String[]data=maindata.split("#");
        String from=data[0];
        String to=data[1];  

        responseobj=getPlayerLiability(from ,to);
    }
                   
                   
    }catch (Exception ex) { ex.getMessage();}

    PrintWriter out = resp.getWriter(); 
    out.print(responseobj);
    }
    
    
        
        
      
      
public JSONArray getPlayerLiability(String fromDate,String toDate)
{

    String res="";
    String dataQuery = "";
    JSONObject dataObj  = null;
    JSONArray dataArray = new JSONArray();

    try( Connection conn = new DBManager(type).getDBConnection();
    Statement stmt = conn.createStatement();)
    {

        ResultSet rs=null;

        dataQuery = "select B.Mul_Match_ID,B.Mul_EventTime, B.Mul_Sportname, B.Mul_Tournament, B.Mul_Event, " +
                    "(select Mar_Name from Active_markets where Mar_ID = B.Mul_Market_ID),B.Mul_Prediction, " +
                    "count(Mul_ID),sum(Mul_Bet_Odd * -Play_Bet_Stake) from player_bets A inner join multibets B on A.Play_Bet_Group_ID = B.Mul_Group_ID " +
                    "and A.Play_Bet_Status = 201  where B.Mul_Market_ID <> 0 and B.Mul_Match_ID <> 0 and A.Play_Bet_Timestamp " +
                    "between '"+fromDate+"' and '"+toDate+"' and Mul_EventTime is not null group by B.Mul_Prediction, B.Mul_Match_ID " +
                    "order by Mul_EventTime   desc ";
        System.out.println("getGamingPlayerLiability==="+dataQuery);

        rs = stmt.executeQuery(dataQuery);


        while (rs.next())
        {
            dataObj  = new JSONObject();
            
            String matchid = rs.getString(1);
            String eventdate =sdf.format(rs.getTimestamp(2));
            String sport = rs.getString(3);
            String championship = rs.getString(4);
            String event = rs.getString(5);
            String market = rs.getString(6);
            String outcome = rs.getString(7);
            String betscount = rs.getString(8);
            String exposure = rs.getString(9);

            dataObj.put("MatchID", matchid);
            dataObj.put("EventDate", eventdate);
            dataObj.put("Sport", sport);
            dataObj.put("Championship", championship);
            dataObj.put("Event", event);
            dataObj.put("Market", market);
            dataObj.put("Outcome", outcome);
            dataObj.put("Betscount", betscount);
            dataObj.put("Exposure", exposure);

            dataArray.put(dataObj);
        }
                   
                  
        //System.out.println("\n\n\n"+dataArray);

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
