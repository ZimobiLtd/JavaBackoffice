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
import java.time.LocalDate;
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
@WebServlet(urlPatterns = {"/LogTrails"})
public class LogTrails extends HttpServlet {

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
                   
                   System.out.println("LogTrails===="+jb.toString());
                   jsonobj = new JSONObject(jb.toString());
                   function=jsonobj.getString("function");
                   maindata=jsonobj.getString("data");
                   
                   
                   if(function.equals("getLogTrails"))
                   {
                        String []respo=initDates();
                        String fromdate=respo[0];
                        String todate=respo[1];
                        responseobj=getLogTrails(fromdate ,todate,"");
                   }
                   
                   
                   if(function.equals("filterLogTrails"))
                   {
                       String []respo=maindata.split("#");
                       String fromdate=respo[0];
                       String todate=respo[1];
                       String username=respo[2];
                       
                       responseobj=getLogTrails(fromdate,todate,username);
                       
                   }
                   
                   
                   
             }catch (Exception ex) { ex.getMessage();}
            
             PrintWriter out = resp.getWriter(); 
            out.print(responseobj);
        }
    
    
        public JSONArray getLogTrails(String from,String to,String user)
        {
                  
            String dataQuery="";
            if (user.equalsIgnoreCase("")) 
            {
                dataQuery = "select username,mobile,email,role,ifnull(comment,'no comment'),logdate from logintrail where date(logdate)  between '"+from+"' and '"+to+"' order by logdate desc";
            } 
            else
            {
                dataQuery = "select username,mobile,email,role,ifnull(comment,'no comment'),logdate from logintrail where username like '%" + user + "%' and  date(logdate)  between '"+from+"' and '"+to+"' order by logdate desc ";
            }
            System.out.println("getLogTrails==="+dataQuery);
            
            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();
            
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

                   ResultSet rs = stmt.executeQuery(dataQuery);
                  
                        while (rs.next())
                        {
                             String username = rs.getString(1);
                             String mobile = "0"+rs.getString(2).substring(3);
                             String email = rs.getString(3);
                             String role = rs.getString(4);
                             String comment = rs.getString(5);
                             String date = rs.getString(6);

                             dataObj  = new JSONObject();
                             dataObj.put("Username", username);
                             dataObj.put("Mobile", mobile);
                             dataObj.put("Email", email);
                             dataObj.put("Date", date);
                             dataObj.put("Role", role);
                             dataObj.put("Comment", comment);
                             dataArray.put(dataObj);
                        }
                        
                        if(dataArray.length()==0)
                        {
                            dataObj  = new JSONObject();
                            dataObj.put("Username", "0");
                            dataObj.put("Mobile", "0");
                            dataObj.put("Email", "0");
                            dataObj.put("Date", "0");
                            dataObj.put("Role", "0");
                            dataObj.put("Comment", "0");
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
        
        
        
        public JSONArray filterPlayerRegistrations(String mobile_no)
        {
                  
            String res="";
            String dataQuery = "select id, msisdn, ifnull(`name`,'no name'), ifnull(email,'no email'), registration_date, Player_Balance, Bonus_Balance,"
                    + "(case when User_Channel=1 then'SMS' when User_Channel=2 then 'USSD' when User_Channel=3 then 'Web' end), "
                    + "(case when `status`=1 then 'Active' when `status`=0 then 'Inactive' end), " +
                    " (select ifnull(max(Acc_Date),'0') from user_accounts where Acc_Mobile = msisdn) as 'Last Deposit', " +
                    " (select count(Play_Bet_ID) from player_bets where Play_Bet_Mobile = msisdn and Play_Bet_Status <> 206) as 'Bets Count' " +
                    " from player where msisdn='"+mobile_no+"' order by registration_date desc limit 100 ";
            System.out.println("getPlayerRegistrations==="+dataQuery);
            
            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();
            
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

                   ResultSet rs = stmt.executeQuery(dataQuery);
                  
                        while (rs.next())
                        {
                             String id = rs.getString(1);
                             String mobile = "0"+rs.getString(2).substring(3);
                             String name = rs.getString(3);
                             String email = rs.getString(4);
                             String regdate = rs.getString(5);
                             String balanceRM = rs.getString(6);
                             String balanceBM = rs.getString(7);
                             String channel = rs.getString(8);
                             String status = rs.getString(9);
                             String lastdepositdate = rs.getString(10);
                             String betscount = rs.getString(11);

                             dataObj  = new JSONObject();
                             dataObj.put("ID", id);
                             dataObj.put("Mobile", mobile);
                             dataObj.put("Name", name);
                             dataObj.put("Email", email);
                             dataObj.put("Registration_Date", regdate);
                             dataObj.put("BalanceRM", balanceRM);
                             dataObj.put("BalanceBM", balanceBM);
                             dataObj.put("Registration_Channel", channel);
                             dataObj.put("LastDeposit_Date", lastdepositdate);
                             dataObj.put("BetsCount", betscount);
                             dataObj.put("Status", status);
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
                             dataObj.put("Registration_Channel", "0");
                             dataObj.put("LastDeposit_Date", "0");
                             dataObj.put("BetsCount", "0");
                             dataObj.put("Status", "0");
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
        
        
        
        
        public JSONArray setDeactivatePlayer(String mobile)
        {
            int count=0;
            String dataQuery = "";String Query="";
            JSONObject dataObj  = new JSONObject();
            JSONArray dataArray = new JSONArray();
   
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {
                
                dataQuery= " update player set `status` =1 where msisdn = '"+mobile+"' ";
                stmt.executeUpdate(dataQuery);
                
                
                dataObj  = new JSONObject();
                dataObj.put("message", "deactivate successful");
                dataArray.put(dataObj);

            stmt.close();
            conn.close();
            }
            catch(Exception e)
            {

            }

        return dataArray;
        }
        
        
        
         public JSONArray setActivatePlayer(String mobile)
        {
            int count=0;
            String dataQuery = "";String Query="";
            JSONObject dataObj  = new JSONObject();
            JSONArray dataArray = new JSONArray();
   
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {
                
                dataQuery= " update player set  `status` =0 where msisdn = '"+mobile+"' ";
                stmt.executeUpdate(dataQuery);
                
                
                dataObj  = new JSONObject();
                dataObj.put("message", "activate successful");
                dataArray.put(dataObj);

            stmt.close();
            conn.close();
            }
            catch(Exception e)
            {

            }

        return dataArray;
        }
        
        
      
        
        public String[] initDates() 
        {
            String []data=null;

            try 
            {

                String todate=LocalDate.now().toString();

                String fromdate=LocalDate.now().plusDays(-30).toString();

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
