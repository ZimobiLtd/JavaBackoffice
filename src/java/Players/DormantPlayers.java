package Players;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Database.DBManager;
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
@WebServlet(urlPatterns = {"/DormantPlayers"})
public class DormantPlayers extends HttpServlet {

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
                   
                   System.out.println("DormantPlayer===="+jb.toString());
                   jsonobj = new JSONObject(jb.toString());
                   function=jsonobj.getString("function");
                   maindata=jsonobj.getString("data");
                   
                   
                   if(function.equals("getDormantPlayers"))
                   {
                        String []respo=initDates();
                        String fromdate=respo[0];
                        String todate=respo[1];
                        responseobj=getDormantPlayer(fromdate ,todate);
                   }
                   
                   
                   if(function.equals("filterDormantPlayers"))
                   {
                        String [] data=maindata.split("#");
                        String from=data[0];
                        String to=data[1];
                        responseobj=getDormantPlayer(from ,to);                       
                   }
                   
                   
                   
             }catch (Exception ex) { ex.getMessage();}
            
             PrintWriter out = resp.getWriter(); 
            out.print(responseobj);
        }
    
    
        public JSONArray getDormantPlayer(String from,String to)
        {
                  
            String res="";
            String dataQuery = "SELECT msisdn,NAME,email,registration_date,Player_Balance,Bonus_Balance, "
                    + "(SELECT ifnull(max(Play_Bet_Timestamp),'0') FROM player_bets WHERE Play_Bet_Mobile=msisdn and date(Play_Bet_Timestamp) not between '" + from + "' and '" + to + "' GROUP BY  Play_Bet_Mobile)"
                    + "FROM player "
                             + "WHERE msisdn NOT  IN ( SELECT Play_Bet_Mobile FROM player_bets  where date(Play_Bet_Timestamp) between '" + from + "' and '" + to + "' ) and NAME is not null GROUP BY msisdn ";
            System.out.println("getDormantPlayer==="+dataQuery);
            
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
                     String regdate =sdf.format(rs.getTimestamp(4));
                     String balanceRM = rs.getString(5);
                     String balanceBM = rs.getString(6);
                     String lastactivedate= rs.getString(7);
                     
                     dataObj  = new JSONObject();
                     dataObj.put("Mobile", "0"+mobile.substring(3));
                     dataObj.put("Name", name);
                     dataObj.put("Email", email);
                     dataObj.put("Registration_Date", regdate);
                     dataObj.put("BalanceRM", balanceRM);
                     dataObj.put("BalanceBM", balanceBM);
                     dataObj.put("LastActiveDate", lastactivedate);
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
                     dataObj.put("BalanceRM", "0");
                     dataObj.put("BalanceBM", "0");
                     dataObj.put("LastActiveDate", "0");
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
            System.out.println("getDormantPlayer execute===="+query);
            try {

                ResultSet rs = (ResultSet) stmt.executeQuery(query);

                while (rs.next()) 
                {
                    data = rs.getString(1);
                }
             rs.close();    
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
