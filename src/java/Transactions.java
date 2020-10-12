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
@WebServlet(urlPatterns = {"/Transactions"})
public class Transactions extends HttpServlet {

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
        public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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
                   
                   System.out.println("Transactions===="+jb.toString());
                   jsonobj = new JSONObject(jb.toString());
                   function=jsonobj.getString("function");
                   maindata=jsonobj.getString("data");
                   
                   
                   if(function.equals("getTransactions"))
                   {
                        String []respo=initDates();
                        String fromdate=respo[0];
                        String todate=respo[1];
                        responseobj=getTransactions("2017-10-23" ,"2020-12-31");
                   }
                   
                   
                   if(function.equals("filterTransactions"))
                   {
                       String[]data=maindata.split("#");
                       String from=data[0];
                       String to=data[1];
                       String transtype=data[2];
                       String transstatus=data[3];  
                       
                       //1=Deposit,2=User Withdrawal,3=Jackpot Withdrawal,4=Bet Withdrawal//
                       String trans_type="",trans_status="";
                       if(transtype.equals("Deposit"))
                       {
                           trans_type="1";
                       }
                       else if(transtype.equals("User Withdrawal"))
                       {
                           trans_type="2";
                       }
                       else if(transtype.equals("Jackpot Withdrawal"))
                       {
                           trans_type="3";
                       }
                       else if(transtype.equals("Bet Withdrawal"))
                       {
                           trans_type="4";
                       }
                       else
                       {
                          trans_type="All"; 
                       }
                       
                       
                       if(transstatus.equalsIgnoreCase("Processed"))
                       {
                           trans_status="1";
                       }
                       else if(transstatus.equalsIgnoreCase("Pending"))
                       {
                           trans_status="2";
                       }
                       else if(transstatus.equalsIgnoreCase("Failed"))
                       {
                           trans_status="3";
                       }
                       else
                       {
                          trans_status="All"; 
                       }
                       
                       
                       
                       
                       if(trans_type.equals("All") && trans_status.equals("All"))
                       {
                           responseobj=getTransactions(from,to);
                       }
                       else if(!trans_type.equals("All") && trans_status.equals("All"))
                       {
                           trans_type="Acc_Trans_Type="+trans_type;
                           trans_status="Acc_Status in (1,2,3)";
                           responseobj=filterTransactions(from,to,trans_type,trans_status);
                       }
                       else if(trans_type.equals("All") && !trans_status.equals("All"))
                       {
                           trans_type="Acc_Trans_Type in(1,2,3)";
                           trans_status="Acc_Status ="+trans_status;
                           responseobj=filterTransactions(from,to,trans_type,trans_status);
                       }
                       else
                       {
                           trans_type="Acc_Trans_Type ="+trans_type;
                           trans_status="Acc_Status ="+trans_status;
                           responseobj=filterTransactions(from,to,trans_type,trans_status);
                       }
                       
                   }
                   
                   
             }catch (Exception ex) { ex.getMessage();}
            
             PrintWriter out = resp.getWriter(); 
            out.print(responseobj);
        }
    
    
        
        
      
      
        public JSONArray getTransactions(String from,String to)
        {
                  
            String res="";
            String dataQuery = "select Acc_Id, Acc_Date, Acc_Mobile, Acc_Amount, Acc_Mpesa_Trans_No, ifnull(Acc_Comment,'Success'),if(Acc_Status =0,'Processed','Pending'),"
                             + "(CASE when Acc_Trans_Type =1 then 'Deposit' when Acc_Trans_Type=2 then 'User Withdrawal' when Acc_Trans_Type=3 then 'Jackpot Withdrawal' "
                    + "when Acc_Trans_Type=4 then 'Bet Withdrawal' end) as 'Trans_Type',ifnull(Acc_Gateway,'Mpesa') from user_accounts where date(Acc_Date) between '"+from+"' and '"+to+"' order by Acc_Date desc ";
            System.out.println("getTransactions==="+dataQuery);
            
            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();
            
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

                ResultSet rs = stmt.executeQuery(dataQuery);

                while (rs.next())
                {
                    String trans_id = rs.getString(1);
                    String trans_date = rs.getString(2);
                    String trans_mobile = rs.getString(3);
                    String trans_amnt = rs.getString(4);
                    String mpesa_code = rs.getString(5);
                    String trans_comment = rs.getString(6);
                    String trans_status = rs.getString(7);
                    String transtype = rs.getString(8);
                    String transgateway = rs.getString(9);

                    dataObj  = new JSONObject();
                    dataObj.put("Trans_ID", trans_id);
                    dataObj.put("Trans_Date", trans_date);
                    dataObj.put("Trans_Mobile", trans_mobile);
                    dataObj.put("Trans_Amount", trans_amnt);
                    dataObj.put("Trans_MpesaCode", mpesa_code);
                    dataObj.put("Trans_Comment", trans_comment);
                    dataObj.put("Trans_Status", trans_status);
                    dataObj.put("Trans_Type", transtype);
                    dataObj.put("Trans_Gateway", transgateway);
                    dataArray.put(dataObj);
                }
                
                if(dataArray.length()==0)
                {
                    dataObj  = new JSONObject();
                    dataObj.put("Trans_ID", "0");
                    dataObj.put("Trans_Date", "0");
                    dataObj.put("Trans_Mobile", "0");
                    dataObj.put("Trans_Amount", "0");
                    dataObj.put("Trans_MpesaCode", "0");
                    dataObj.put("Trans_Comment", "0");
                    dataObj.put("Trans_Status", "0");
                    dataObj.put("Trans_Type", "0");
                    dataObj.put("Trans_Gateway", "0");
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
        
        
        
        
        
        
        public JSONArray filterTransactions(String from,String to,String transtype,String transstatus)
        {
                  
            String res="";
            String dataQuery = "select Acc_Id, Acc_Date, Acc_Mobile, Acc_Amount, Acc_Mpesa_Trans_No, ifnull(Acc_Comment,'Success'),if(Acc_Status =0,'Processed','Pending'),"
                             + "(CASE when Acc_Trans_Type =1 then 'Deposit' when Acc_Trans_Type=2 then 'User Withdrawal' when Acc_Trans_Type=3 then 'Jackpot Withdrawal' when Acc_Trans_Type=4 then 'Bet Withdrawal' end) as 'Trans_Type',ifnull(Acc_Gateway,'Mpesa') "
                             + "from user_accounts where date(Acc_Date) between '"+from+"' and '"+to+"' and "+transtype+" and "+transstatus+" order by Acc_Date desc ";
            
            System.out.println("filterTransactions==="+dataQuery);
            
            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();
            
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

                    ResultSet rs = stmt.executeQuery(dataQuery);
                   
                    if (rs.next() == false)
                    {
                        dataObj  = new JSONObject();
                        dataObj.put("Trans_ID", "0");
                        dataObj.put("Trans_Date", "0");
                        dataObj.put("Trans_Mobile", "0");
                        dataObj.put("Trans_Amount", "0");
                        dataObj.put("Trans_MpesaCode", "0");
                        dataObj.put("Trans_Comment", "0");
                        dataObj.put("Trans_Status", "0");
                        dataObj.put("Trans_Type", "0");
                        dataObj.put("Trans_Gateway", "0");
                        dataArray.put(dataObj);

                    }
                    else
                    {
                        while (rs.next())
                        {
                            String trans_id = rs.getString(1);
                            String trans_date = rs.getString(2);
                            String trans_mobile = rs.getString(3);
                            String trans_amnt = rs.getString(4);
                            String mpesa_code = rs.getString(5);
                            String trans_comment = rs.getString(6);
                            String trans_status = rs.getString(7);
                            String trans_type = rs.getString(8);
                            String trans_gateway = rs.getString(9);

                            dataObj  = new JSONObject();
                            dataObj.put("Trans_ID", trans_id);
                            dataObj.put("Trans_Date", trans_date);
                            dataObj.put("Trans_Mobile", trans_mobile);
                            dataObj.put("Trans_Amount", trans_amnt);
                            dataObj.put("Trans_MpesaCode", mpesa_code);
                            dataObj.put("Trans_Comment", trans_comment);
                            dataObj.put("Trans_Status", trans_status);
                            dataObj.put("Trans_Type", trans_type);
                            dataObj.put("Trans_Gateway", trans_gateway);
                            dataArray.put(dataObj);
                        }
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
