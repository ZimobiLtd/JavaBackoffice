package Gaming.APIs;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Database.DBManager;
import Gaming.Processor.GamingSummaryProcessor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import org.json.JSONException;

/**
 *
 * @author jac
 */
@WebServlet(urlPatterns = {"/GamingSummary"})
public class GamingSummaryAPI extends HttpServlet {

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

        
        System.out.println("getGamingSummaryData==="+jb.toString());
        jsonobj = new JSONObject(jb.toString());
        function=jsonobj.getString("function");
        maindata=jsonobj.getString("data");

        if(function.equals("getGamingSummary"))
        {
            responseobj=new GamingSummaryProcessor().getGamingSummary();
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