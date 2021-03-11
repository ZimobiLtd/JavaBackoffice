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
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
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
@WebServlet(urlPatterns = {"/ActivePlayers"})
public class ActivePlayers extends HttpServlet {

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
                   
                   System.out.println("ActivePlayer===="+jb.toString());
                   jsonobj = new JSONObject(jb.toString());
                   function=jsonobj.getString("function");
                   maindata=jsonobj.getString("data");
                   
                   
                   if(function.equals("getActivePlayers"))
                   {
                        String filters=" and Play_Bet_Type in(1,2,3)";
                        String []respo=initDates();
                        String fromdate=respo[0];
                        String todate=respo[1];
                        responseobj=getActivePlayer(fromdate ,todate,filters);
                   }
                   
                   
                   if(function.equals("filterActivePlayers"))
                   {
                       String filters="";
                        String [] data=maindata.split("#");
                        String from=data[0];
                        String to=data[1];
                        String type=data[2];
                        
                        if(type.equals("Single Bet"))
                        {
                            filters="and Play_Bet_Type=1";
                        }
                        else if(type.equals("Multi Bet"))
                        {
                            filters="and Play_Bet_Type=3";
                        }
                        else if(type.equals("Jackpot"))
                        {
                            filters="and Play_Bet_Type=1";
                        }
                        else if(type.equals("All"))
                        {
                            filters="and Play_Bet_Type in(1,2,3)";
                        }
                        
                        responseobj=getActivePlayer(from ,to,filters);                       
                   }
                   
                   
                   
             }catch (Exception ex) { ex.getMessage();}
            
             PrintWriter out = resp.getWriter(); 
            out.print(responseobj);
        }
    
    
        public JSONArray getActivePlayer(String from,String to,String filters)
        {
                  
            String res=""; 
            String dataQuery = "SELECT DISTINCT msisdn,Name,email,registration_date,(SELECT count(Play_Bet_ID) FROM player_bets WHERE Play_Bet_Mobile= msisdn " + filters + ")AS Bet_Counts , "
                + "(SELECT sum(Play_Bet_Stake) FROM player_bets WHERE Play_Bet_Mobile=msisdn and Play_Bet_Status not in(200,204,205,206)) as stake, "
                + "(SELECT  ifnull(sum(Play_Bet_Possible_Winning),0) FROM player_bets WHERE Play_Bet_Mobile=msisdn and Play_Bet_Status =202) as payout, "
                + "(select ifnull(((SELECT sum(Play_Bet_Stake) FROM player_bets WHERE Play_Bet_Mobile=msisdn and Play_Bet_Status  in(201,202,203)) - "
                + "(SELECT sum(Play_Bet_Possible_Winning) FROM player_bets WHERE Play_Bet_Mobile=msisdn and Play_Bet_Status =202) ),0) )as net "
                + "FROM player WHERE msisdn IN (SELECT Play_Bet_Mobile FROM player_bets "
                + "where date(Play_Bet_Timestamp) BETWEEN '" + from + "' and '" + to + "'  " + filters + ")ORDER BY  Bet_Counts desc";
                    
                    System.out.println("getActivePlayer==="+dataQuery);
            
            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();
            
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

                ResultSet rs = stmt.executeQuery(dataQuery);

                while (rs.next())
                {
                     String mobile =rs.getString(1);
                     String name = rs.getString(2);
                     String email = rs.getString(3);
                     String regdate = sdf.format(rs.getTimestamp(4));
                     String betscount = rs.getString(5);
                     String totalstake = rs.getString(6);
                     String payout= rs.getString(7);
                     String net= rs.getString(8);
                  
                     dataObj  = new JSONObject();
                     dataObj.put("Mobile", "0"+mobile.substring(3));
                     dataObj.put("Name", name);
                     dataObj.put("Email", email);
                     dataObj.put("Registration_Date", regdate);
                     dataObj.put("BetsCount", betscount);
                     dataObj.put("TotalStake", totalstake);
                     dataObj.put("Payout", payout);
                     dataObj.put("Net", net);
                     dataArray.put(dataObj);
                }

                if(dataArray.length()==0)
                {
                    dataObj  = new JSONObject();
                     dataObj.put("ID", "0");
                     dataObj.put("Mobile", "0");
                     dataObj.put("Name", "0");
                     dataObj.put("Email", "0");
                     dataObj.put("Registration_Date", "0");
                     dataObj.put("BetsCount", "0");
                     dataObj.put("TotalStake", "0");
                     dataObj.put("Payout", "0");
                     dataObj.put("Net", "0");
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
        
        
        
        // return record count in a result set
        public String execute( Connection conn,Statement stmt, String query) 
        {
            String data="";
            System.out.println("getActivePlayer execute===="+query);
            try {

                ResultSet rst = (ResultSet) stmt.executeQuery(query);

                while (rst.next()) 
                {
                    data = rst.getString(1);
                }
            //rst.close();
            } catch (Exception ex) {
                System.err.println("execute error .... " + ex.getMessage());
            }
           
            return data;
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
