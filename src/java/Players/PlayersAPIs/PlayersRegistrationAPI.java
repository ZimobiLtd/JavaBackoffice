package Players.PlayersAPIs;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Players.PlayersProcessor.PlayersRegistratinProcessor;
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
@WebServlet(urlPatterns = {"/PlayerRegistrations"})
public class PlayersRegistrationAPI extends HttpServlet {

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
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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

            System.out.println("playerRegistrationsData===="+jb.toString());
            jsonobj = new JSONObject(jb.toString());
            function=jsonobj.getString("function");
            maindata=jsonobj.getString("data");


            if(function.equals("getPlayerRegistrations"))
            {
                String []respo=new Utility().getDatesRange(-1);
                String fromdate=respo[0];
                String todate=respo[1];
                responseobj=new PlayersRegistratinProcessor().getPlayerRegistrations(fromdate,todate);
            }


            if(function.equals("filterPlayerRegistrationsByMobile"))
            {
                String mobile=maindata;
                String mobile_no="";

                if(mobile.startsWith("07") || mobile.startsWith("01"))
                {
                    mobile_no="254"+mobile.substring(1);
                }
                else
                {
                    mobile_no=mobile;
                }

                responseobj=new PlayersRegistratinProcessor().filterPlayerRegistrationsByMobile(mobile_no);
            }

            if(function.equals("filterPlayerRegistrationsByDate"))
            {
                String []data=maindata.split("#");
                String fromdate=data[0];
                String todate=data[1];
                responseobj=new PlayersRegistratinProcessor().getPlayerRegistrations(fromdate,todate);
            }

            if(function.equals("deactivatePlayer"))
            {
                String mobile=maindata;
                String mobile_no="";

                if(mobile.startsWith("07") || mobile.startsWith("01"))
                {
                    mobile_no="254"+mobile.substring(1);
                }
                else
                {
                    mobile_no=mobile;
                }

                responseobj=new PlayersRegistratinProcessor().setDeactivatePlayer(mobile_no);

            }


            if(function.equals("activatePlayer"))
            {
                String mobile=maindata;
                String mobile_no="";

                if(mobile.startsWith("07") || mobile.startsWith("01"))
                {
                    mobile_no="254"+mobile.substring(1);
                }
                else
                {
                    mobile_no=mobile;
                }

                responseobj=new PlayersRegistratinProcessor().setActivatePlayer(mobile_no);

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
