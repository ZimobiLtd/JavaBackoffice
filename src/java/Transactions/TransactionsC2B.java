package Transactions;

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
@WebServlet(urlPatterns = {"/TransactionsC2B"})
public class TransactionsC2B extends HttpServlet {

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
                   
                   System.out.println("TransactionsC2B===="+jb.toString());
                   jsonobj = new JSONObject(jb.toString());
                   function=jsonobj.getString("function");
                   maindata=jsonobj.getString("data");
                   
                   
                   if(function.equals("getTransactionsC2B"))
                   {
                        String []respo=initDates();
                        String fromdate=respo[0];
                        String todate=respo[1];
                        responseobj=getTransactionsC2B(fromdate ,todate);
                   }
                   
                   
                   if(function.equals("filterTransactionsC2B"))
                   {
                        String[]data=maindata.split("#");
                        String from=data[0];
                        String to=data[1];
                        String mobile=data[2];
                        if(mobile.startsWith("07") || mobile.startsWith("01"))
                        {
                           mobile="254"+mobile.substring(1);
                        }
                       
                       responseobj=filterTransactionsC2B(from,to,mobile);
                   }
                   
             }catch (Exception ex) { ex.getMessage();}
            
             PrintWriter out = resp.getWriter(); 
             out.print(responseobj);
        }
    
    
        
        
      
      
        public JSONArray getTransactionsC2B(String from,String to)
        {
                  
            String res="";
            String dataQuery = "select Trans_Id, Trans_Timestamp, Trans_Mobile, Trans_Amount, Trans_Mpesa_No,Trans_Acc_No from transactions_c2b where date(Trans_Timestamp) between '"+from+"' and '"+to+"' order by Trans_Timestamp desc ";
            System.out.println("getTransactionsC2B==="+dataQuery);
            
            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();
            
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

                ResultSet rs = stmt.executeQuery(dataQuery);
                while (rs.next())
                {
                    String trans_id = rs.getString(1);
                    String trans_date = sdf.format(rs.getTimestamp(2));
                    String trans_mobile = "0"+rs.getString(3).substring(3);
                    String trans_amnt = rs.getString(4);
                    String mpesa_code = rs.getString(5);
                    String trans_acc = rs.getString(6);

                    dataObj  = new JSONObject();
                    dataObj.put("Trans_ID", trans_id);
                    dataObj.put("Trans_Date", trans_date);
                    dataObj.put("Trans_Mobile", trans_mobile);
                    dataObj.put("Trans_Amount", trans_amnt);
                    dataObj.put("Trans_MpesaCode", mpesa_code);
                    dataObj.put("Trans_Account", trans_acc);
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
                    dataObj.put("Trans_Account", "0");
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
        
        
        
        
        
        
        public JSONArray filterTransactionsC2B(String from,String to,String mobile)
        {
                  
            String res="";
            String dataQuery = "select Trans_Id, Trans_Timestamp, Trans_Mobile, Trans_Amount, Trans_Mpesa_No,Trans_Acc_No from transactions_c2b where date(Trans_Timestamp) between '"+from+"' and '"+to+"' and Trans_Mobile='"+mobile+"' order by Trans_Timestamp desc ";
            
            System.out.println("filterTransactionsC2B==="+dataQuery);
            
            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();
            
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

                    ResultSet rs = stmt.executeQuery(dataQuery);
                    while (rs.next())
                    {
                        String trans_id = rs.getString(1);
                        String trans_date = sdf.format(rs.getTimestamp(2));
                        String trans_mobile = "0"+rs.getString(3).substring(3);
                        String trans_amnt = rs.getString(4);
                        String mpesa_code = rs.getString(5);
                        String trans_acc = rs.getString(6);

                        dataObj  = new JSONObject();
                        dataObj.put("Trans_ID", trans_id);
                        dataObj.put("Trans_Date", trans_date);
                        dataObj.put("Trans_Mobile", trans_mobile);
                        dataObj.put("Trans_Amount", trans_amnt);
                        dataObj.put("Trans_MpesaCode", mpesa_code);
                        dataObj.put("Trans_Account", trans_acc);
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
                        dataObj.put("Trans_Account", "0");
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
      
      
        
        public String[] initDates() 
        {
            String []data=null;

            try 
            {

                String todate=LocalDate.now().toString();
                
                String fromdate=LocalDate.now().plusDays(-1).toString();

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
