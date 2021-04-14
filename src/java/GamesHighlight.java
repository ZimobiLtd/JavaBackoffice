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
import java.time.LocalDate;
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
@WebServlet(urlPatterns = {"/GamesHighlight"})
public class GamesHighlight extends HttpServlet {

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
                   
                   System.out.println("GamesHighlight===="+jb.toString());
                   jsonobj = new JSONObject(jb.toString());
                   function=jsonobj.getString("function");
                   maindata=jsonobj.getString("data");
                   
                   
                   if(function.equals("getGamesHighlight"))
                   {
                       String []respo=initDates();
                       String fromdate=respo[0];
                       String todate=respo[1];
                       System.out.println(fromdate+"==intidates=="+todate);
                       responseobj=getgamesHighlits(fromdate,todate);
                   }
                   
                   
                   if(function.equals("filterGamesHighlight"))
                   {
                        String filters="";
                        String[]data=maindata.split("#");
                        String from=data[0];
                        String to=data[1];  
                        String teamname=data[2].trim();
                        String sport=data[3].trim();
                        String gameid=data[3].trim();
                        
                        if (teamname.equalsIgnoreCase("any")&& gameid.equalsIgnoreCase("any"))
                        {
                          responseobj=getgamesHighlits(from ,to);  
                        }
                        else if (teamname.equalsIgnoreCase("any")&& !gameid.equalsIgnoreCase("any"))
                        {
                            filters = "and Torna_Sys_Game_ID like '" + gameid + "%' ";
                            responseobj=filtergamesHighlits(from ,to,filters);
                        }
                        else if (!teamname.equalsIgnoreCase("any")&& gameid.equalsIgnoreCase("any"))
                        {
                            filters = " and Torna_Match_Event like '%" + teamname + "%' ";
                            responseobj=filtergamesHighlits(from ,to,filters);
                        }
                        else
                        {
                            if (!teamname.equals("null") && !gameid.equals("null")) 
                            {
                                filters = " and Torna_Match_Event like '%" + teamname + "%'  and Torna_Sys_Game_ID like '" + gameid + "%' ";
                            }
                            else if (!teamname.equals("null")&& gameid.equals("null") ) 
                            {
                                filters = " and Torna_Match_Event like '%" + teamname + "%' ";
                            }
                            else if (teamname.equals("null")&& !gameid.equals("null") ) 
                            {
                                filters = "and Torna_Sys_Game_ID like '" + gameid + "%' ";
                            }
                            responseobj=filtergamesHighlits(from ,to,filters);
                        }
                        
                   }
                   
                   
                   if(function.equals("setGamesHighlight"))
                   {
                        String [] highlights=null;
                        String data=maindata;
                        responseobj=setHighlights(data);
                   } 
                   
                   if(function.equals("unHighlightGames"))
                   {
                        String [] highlights=null;
                        String data=maindata;
                        
                        responseobj=setunHighlights(data);
                   } 
                   
                   
             }catch (Exception ex) { ex.getMessage();}
            
             PrintWriter out = resp.getWriter(); 
            out.print(responseobj);
        }
    
    
        
        
      
      
        public JSONArray getgamesHighlits(String fromDate,String toDate)
        {
                  
            String res="";
            String dataQuery = "";
            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();
            
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

                ResultSet rs=null;
                    
                dataQuery = "select  Torna_Sport_Name, Torna_Cat_Name, Torna_Name, Torna_Match_Event, Torna_Match_Event_Time, "
                         + "Torna_Match_ID, Torna_Sys_Game_ID, (case when Torna_Match_Status=0 then 'Active' when Torna_Match_Status=1 then 'Inactive' end), "
                         + "(case when Torna_Cat_Game_Mode=1 then 'Todays Games' when Torna_Cat_Game_Mode=3 then 'Jackpot Games' when Torna_Cat_Game_Mode=2 then 'Todays Highlights' end),"
                         + "Torna_Cat_Game_Mode from tournament where date(Torna_Match_Event_Time) between '" + fromDate + "' and '" + fromDate + "' and "
                         + "Torna_Match_Status = '0' and Torna_Match_Status='0' and Torna_Match_Stage !='Suspended' and Torna_Match_Stage !='Ended' "
                         + "and Torna_Match_Stage !='Deactivated' and date(Torna_Match_Event_Time)>=curdate() order by Torna_Cat_Game_Mode desc";//Torna_Cat_Game_Mode desc
                
                System.out.println("getgamesHighlits==="+dataQuery);

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
                    String matchmode = rs.getString(9);
                    String matchmodestatus = rs.getString(10);

                    dataObj.put("TornamentMatchID", torna_match_id);
                    dataObj.put("GameID", torna_sys_game_id);
                    dataObj.put("Sport", sportname);
                    dataObj.put("Country", countryname);
                    dataObj.put("Tornament", torna_name);
                    dataObj.put("Event", event);
                    dataObj.put("EventTime", eventtime);
                    dataObj.put("MatchStatus", matchstatus);
                    dataObj.put("MatchMode", matchmode);
                    dataObj.put("MatchModeStatus", matchmodestatus);

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
        
        
        
        
        
        
        public JSONArray filtergamesHighlits(String fromDate,String toDate ,String filters)
        {
                  
            String res="";
            String dataQuery = "";
            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();
            
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

                ResultSet rs=null;
                    
                dataQuery = "select  Torna_Sport_Name, Torna_Cat_Name, Torna_Name, Torna_Match_Event, Torna_Match_Event_Time, "
                            + "Torna_Match_ID, Torna_Sys_Game_ID, (case when Torna_Match_Status=0 then 'Active' when Torna_Match_Status=1 then 'Inactive' end), "
                        + "(case when Torna_Cat_Game_Mode=1 then 'Todays Games' when Torna_Cat_Game_Mode=3 then 'Jackpot Games' when Torna_Cat_Game_Mode=3 then 'Todays Highlights' end),Torna_Cat_Game_Mode "
                            + "from tournament where Torna_Match_Event_Time between '" + fromDate + "' and '" + toDate + "' and "
                            + "Torna_Match_Status = '0'  "+filters+" and Torna_Match_Stage !='Suspended' and Torna_Match_Stage !='Ended' and Torna_Match_Stage !='Deactivated' and date(Torna_Match_Event_Time)>=curdate() order by Torna_Cat_Game_Mode desc ";//,Torna_Match_Event_Time 

                System.out.println("filtersgamesHighlits==="+dataQuery);
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
                    String matchmode = rs.getString(9);
                    String matchmodestatus = rs.getString(10);

                    dataObj.put("TornamentMatchID", torna_match_id);
                    dataObj.put("GameID", torna_sys_game_id);
                    dataObj.put("Country", countryname);
                    dataObj.put("Sport", sportname);
                    dataObj.put("Tornament", torna_name);
                    dataObj.put("Event", event);
                    dataObj.put("EventTime", eventtime);
                    dataObj.put("MatchStatus", matchstatus);
                    dataObj.put("MatchMode", matchmode);
                    dataObj.put("MatchModeStatus", matchmodestatus);

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
      
        
        
        
        
        public JSONArray setHighlights(String matchId)
        {
            int count=0;
            String dataQuery = "",dataQuery1="";String Query="";
            JSONObject dataObj  = new JSONObject();
            JSONArray dataArray = new JSONArray();
   
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {
                dataQuery1 = " update tournament set Torna_Cat_Game_Mode ='2' where Torna_Match_ID like '" + matchId + "%'  ";//and Torna_Match_Status = '0'
                //dataQuery = " update All_Games set Game_Mode = 2 where Game_Betradar_ID like '" + matchId + "%'  ";//and Torna_Match_Status = '0'
                //System.out.println("setHighlights==="+dataQuery);
                stmt.executeUpdate(dataQuery1);
                //stmt.executeUpdate(dataQuery);
                
                
                dataObj.put("message", "highlight successful");
                dataArray.put(dataObj);

            stmt.close();
            conn.close();
            }
            catch(Exception e)
            {

            }

        return dataArray;
        }
        
        
        
        public JSONArray setunHighlights(String matchId)
        {
            int count=0;
            String dataQuery = "",dataQuery1="";String Query="";
            JSONObject dataObj  = new JSONObject();
            JSONArray dataArray = new JSONArray();
   
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {
                
                dataQuery = " update tournament set Torna_Cat_Game_Mode ='1' where Torna_Match_ID  like '" + matchId + "%'  ";
                //System.out.println("setHighlights==="+dataQuery);
                //dataQuery1 = " update All_Games set Game_Mode = 0 where Game_Betradar_ID like '" + matchId + "%'  ";
                //System.out.println("setHighlights==="+dataQuery);
                stmt.executeUpdate(dataQuery);
                //stmt.executeUpdate(dataQuery1);
                
                dataObj.put("message", "unhighlight successful");
                dataArray.put(dataObj);

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

                String todate=LocalDate.now().plusDays(+1).toString();

                String fromdate=LocalDate.now().toString();

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
