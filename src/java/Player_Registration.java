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
@WebServlet(urlPatterns = {"/PlayerRegistrations"})
public class Player_Registration extends HttpServlet {

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
                   
                   System.out.println("PlayerRegistrations===="+jb.toString());
                   jsonobj = new JSONObject(jb.toString());
                   function=jsonobj.getString("function");
                   maindata=jsonobj.getString("data");
                   
                   
                   if(function.equals("getPlayerRegistrations"))
                   {
                        String []respo=initDates();
                        String fromdate=respo[0];
                        String todate=respo[1];
                        responseobj=getPlayerRegistrations(fromdate,todate);
                   }
                   
                   
                   if(function.equals("filterPlayerRegistrations"))
                   {
                       String mobile=maindata;
                       String mobile_no="";
                       
                       if(mobile.startsWith("07"))
                       {
                           mobile_no="254"+mobile.substring(1);
                       }
                       else
                       {
                           mobile_no=mobile;
                       }
                       
                       responseobj=filterPlayerRegistrations(mobile_no);
                       
                   }
                   
                   if(function.equals("deactivatePlayer"))
                   {
                       String mobile=maindata;
                       String mobile_no="";
                       
                       if(mobile.startsWith("07"))
                       {
                           mobile_no="254"+mobile.substring(1);
                       }
                       else
                       {
                           mobile_no=mobile;
                       }
                       
                       responseobj=setDeactivatePlayer(mobile_no);
                       
                   }
                   
                   if(function.equals("activatePlayer"))
                   {
                       String mobile=maindata;
                       String mobile_no="";
                       
                       if(mobile.startsWith("07"))
                       {
                           mobile_no="254"+mobile.substring(1);
                       }
                       else
                       {
                           mobile_no=mobile;
                       }
                       
                       responseobj=setActivatePlayer(mobile_no);
                       
                   }
                   
                   
             }catch (Exception ex) { ex.getMessage();}
            
             PrintWriter out = resp.getWriter(); 
            out.print(responseobj);
        }
    
    
        public JSONArray getPlayerRegistrations(String from,String to)
        {
                  
            String res="";
            String dataQuery = "select id, msisdn, ifnull(`name`,'no name'), ifnull(email,'no email'), registration_date, (select ifnull(sum(Acc_Amount),0) from user_accounts where Acc_Mobile = msisdn ), Bonus_Balance,"
                    + "(case when User_Channel=1 then'USSD' when User_Channel=2 then 'SMS' when User_Channel=3 then 'Web' end), "
                    + "(case when `status`=1 then 'Inactive' when `status`=0 then 'Active' end), " +
                    " (select ifnull(max(Acc_Date),'0') from user_accounts where Acc_Mobile = msisdn) as 'Last Deposit', " +
                    " (select count(Play_Bet_ID) from player_bets where Play_Bet_Mobile = msisdn and Play_Bet_Status <> 206) as 'Bets Count' " +
                    " from player where date(registration_date) between '"+from+"' and '"+to+"' order by registration_date desc ";
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
                             String regdate = sdf.format(rs.getTimestamp(5));
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
                            //dataObj.put("error", "Player not found");
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
            String dataQuery = "select id, msisdn, ifnull(`name`,'no name'), ifnull(email,'no email'), registration_date, (select ifnull(sum(Acc_Amount),0) from user_accounts where Acc_Mobile = msisdn ), Bonus_Balance,"
                    + "(case when User_Channel=1 then'USSD' when User_Channel=2 then 'SMS' when User_Channel=3 then 'Web' end), "
                    + "(case when `status`=1 then 'Inactive' when `status`=0 then 'Active' end), " +
                    " (select ifnull(max(Acc_Date),'0') from user_accounts where Acc_Mobile = msisdn) as 'Last Deposit', " +
                    " (select count(Play_Bet_ID) from player_bets where Play_Bet_Mobile = msisdn and Play_Bet_Status <> 206) as 'Bets Count' " +
                    " from player where msisdn='"+mobile_no+"' order by registration_date desc ";
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
                             String regdate = sdf.format(rs.getTimestamp(5));
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
                            //dataObj.put("error", "Player not found");
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
                dataObj.put("message", "Player deactivated");
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
                dataObj.put("message", "Player activated");
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

                Date startDate = new Date();//sdf.parse(fromDate);
                long curTime = startDate.getTime();
                long interval = (24 * 1000 * 60 * 60)*10;
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
