package Players;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Database.DBManager;
import Utility.Utility;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
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
@WebServlet(urlPatterns = {"/PlayerPassword"})
public class PlayerPassword extends HttpServlet {

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

            jsonobj = new JSONObject(jb.toString());
            function=jsonobj.getString("function");
            maindata=jsonobj.getString("data");


            if(function.equals("resetPassword"))
            {
                String mobile=maindata;
                String pin=new Utility().generateCode(4);
                
                if(mobile.startsWith("07") || mobile.startsWith("01"))
                {
                   mobile="254"+mobile.substring(1);
                }
                String newMobile=mobile;
                String respo=updatePlayerPassword(mobile,pin);
                String sms="Dear customer, your new StarBet password is: "+pin;
                if(respo.equals("200"))
                {
                    Thread thread=new Thread(new Runnable()
                    {
                        @Override
                        public void run() {
                           new Utility().sendSMS(newMobile,sms);
                        }

                    });
                    thread.start();

                    JSONObject dataObj  = new JSONObject();
                    JSONArray dataArray = new JSONArray();
                    dataObj.put("message", "Password reset successful");
                    dataArray.put(dataObj);
                    responseobj=dataArray;

                }
                else
                {
                    JSONObject dataObj  = new JSONObject();
                    JSONArray dataArray = new JSONArray();
                    dataObj.put("error", "Password reset failed");
                    dataArray.put(dataObj);
                    responseobj=dataArray;
                }
            }
            
            if(function.equals("getPassword"))
            {
                String mobile=maindata;
                
                if(mobile.startsWith("07") || mobile.startsWith("01"))
                {
                   mobile="254"+mobile.substring(1);
                }
                String newMobile=mobile;
                
                String password=getPlayerPassword(newMobile);
                JSONObject dataObj  = new JSONObject();
                JSONArray dataArray = new JSONArray();
                dataObj.put("message", "Player password: "+password);
                dataArray.put(dataObj);
                responseobj=dataArray;
            }

        }catch (Exception ex) { ex.getMessage();}

        PrintWriter out = resp.getWriter(); 
        out.print(responseobj);
    }


    public String updatePlayerPassword(String mobile,String pin)
    {

        String res="404"; 
        String dataQuery = "update player set password='"+pin+"' where msisdn='"+mobile+"' limit 1 ";

        try( Connection conn = new DBManager(type).getDBConnection();
        Statement stmt = conn.createStatement();)
        {

            int i = stmt.executeUpdate(dataQuery);
            if(i>0)
            {
               res="200"; 
            }

        stmt.close();
        conn.close();
        }
        catch(Exception e)
        {

        }

    return res;
    }


    
    
    public String getPlayerPassword(String mobile)
    {

        String data="";
        String query="select password from player where msisdn = '" + mobile + "'";

        try( Connection conn = new DBManager(type).getDBConnection();
        Statement stmt = conn.createStatement();)
        {

        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) 
        {

            data = rs.getString(1);

        }

        rs.close();
        conn.close();
        } catch (SQLException ex) {
         System.out.println("Error getPlayerPassword=== "+ex.getMessage());
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
