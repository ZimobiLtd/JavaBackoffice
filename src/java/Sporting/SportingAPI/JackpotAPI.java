package Sporting.SportingAPI;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Sporting.SportingProcessor.EventsProcessor;
import Sporting.SportingProcessor.JackpotHighlights;
import Sporting.SportingProcessor.JackpotProcessor;
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
@WebServlet(urlPatterns = {"/api/jackpot/data"})
public class JackpotAPI extends HttpServlet {

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

        System.out.println("jackpotData===="+jb.toString());
        jsonobj = new JSONObject(jb.toString());
        function=jsonobj.getString("function");
        maindata=jsonobj.getString("data");

        if(function.equals("getJackpots"))
        {
            String []respo=new Utility().getDatesRange(0);
            String fromdate=respo[0];
            String todate=respo[1];
            System.out.println(fromdate+"==intidates=="+todate);
            responseobj=new JackpotProcessor().getJackports();
        }

        if(function.equals("getJackpotGames"))
        {
            String jackpotID=maindata;
            String matchIDs=new JackpotProcessor().getJackpotMatchIDs(jackpotID);

            responseobj=new JackpotProcessor().getJackpotGames(matchIDs);
        }

        if(function.equals("deleteJackpot"))
        {
            String jackpotID=maindata;
            int status=new JackpotHighlights().unMarkJackpotGames(Integer.valueOf(jackpotID));
            if(status == 200)
            {
                responseobj=new JackpotProcessor().getDeleteJackpot(jackpotID);
            }
            else
            {
                JSONObject dataObj  = new JSONObject();
                JSONArray dataArray = new JSONArray();
                
                dataObj.put("error", "Request failed");
                dataArray.put(dataObj);
                responseobj = dataArray;
            }
        }
        
        
        if(function.equals("setJackpotWinners"))
        {
            String []data=maindata.split("#");
            String jackpotID=data[0];
            String winners=data[1];
            
            responseobj=new JackpotProcessor().setJackpotWinners(jackpotID,winners);
        }
        
        if(function.equals("getJackpotWinners"))
        {
            String jackpotID=maindata;
            responseobj=new JackpotProcessor().getJackpotWinners(Integer.valueOf(jackpotID));
        }

    }
    catch (IOException | JSONException ex) 
    { 
        ex.getMessage();
    }

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
