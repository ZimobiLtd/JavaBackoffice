package Players.PlayersAPIs;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Players.PlayersProcessor.PlayerPasswordProcessor;
import Utility.Utility;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

/**
 *
 * @author jac
 */
@WebServlet(urlPatterns = {"/PlayerPassword"})
public class PlayerPasswordAPI extends HttpServlet {

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
    JSONObject jsonobj=null;JSONArray responseobj  = null;
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
        resp.setContentType("text/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST");


        StringBuilder jb = new StringBuilder();
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
                int status=new PlayerPasswordProcessor().updatePlayerPassword(mobile,pin);
                String sms="Dear customer, your new StarBet password is: "+pin+".Visit www.starbet.co.ke. Call: 0709758000 or Whatsapp 0114029659 for Assistance ";
                if(status== 200)
                {
                    Thread thread=new Thread(new Runnable()
                    {
                        @Override
                        public void run() 
                        {
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
                
                String password=new PlayerPasswordProcessor().getPlayerPassword(newMobile);
                JSONObject dataObj  = new JSONObject();
                JSONArray dataArray = new JSONArray();
                dataObj.put("message", "PLAYER PASSWORD: "+password);
                dataArray.put(dataObj);
                responseobj=dataArray;
            }

        }catch (IOException | JSONException ex) { ex.getMessage();}

    PrintWriter out = resp.getWriter(); 
    out.print(responseobj);
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
