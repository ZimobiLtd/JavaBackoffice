package Controllers.UtilityController;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Implimentation.UtilityImplimentation.ManualGamesResolve;
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

/**
 *
 * @author jac
 */
@WebServlet(urlPatterns = {"/manualresolve"})
public class ManualResolveAPI extends HttpServlet {

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
    JSONObject jsonobj=null;JSONArray responseObj  = null;
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
        resp.setContentType("text/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST");
      
        String matchID=req.getParameter("match_id").trim();
        String market=req.getParameter("market").trim();
        
        if(market.equalsIgnoreCase("1") || market.equalsIgnoreCase("2") || market.equalsIgnoreCase("x") || market.equalsIgnoreCase("gg")|| market.equalsIgnoreCase("ng") || 
            market.equalsIgnoreCase("x2") || market.equalsIgnoreCase("x1")  || market.equalsIgnoreCase("12") || market.equalsIgnoreCase("o2.5") || market.equalsIgnoreCase("u2.5") 
            || market.equalsIgnoreCase("o1.5") || market.equalsIgnoreCase("u1.5")|| market.equalsIgnoreCase("o3.5") || market.equalsIgnoreCase("u3.5"))
        {
            int matchIDStatus=new ManualGamesResolve().checkMatchID(matchID);
            if(matchIDStatus == 200)
            {
                int dataStatus=new ManualGamesResolve().checkBetEventData(matchID,market);
                if(dataStatus == 404)
                {
                    int status=new ManualGamesResolve().resolveGames(matchID,market);
                    if(status == 200)
                    {
                        response="======\n\n "+matchID+" Market "+market+" Saved Successfully \n\n===========================================================";
                    }
                    else
                    {
                        response="======\n\n "+matchID+" Failed To Save "+market+" Market \n\n===========================================================";
                    }
                }
                else
                {
                    response="======\n\n Match ID "+matchID+" Already Exist In Bet_Event_Winers \n\n===========================================================";
                }
            }
            else
            {
                response="======\n\n Match ID "+matchID+" Not Found In Bets \n\n===========================================================";
            }  
        }
        else
        {
            response="======\n\n Invalid Market "+market+" \n\n===========================================================";
        }
        
        
        PrintWriter out = resp.getWriter(); 
        out.print(response);
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
