package Controllers.SportingController;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Implimentation.SportingImplimentation.EventsImpl;
import Implimentation.SportingImplimentation.GamesHighlightsImpl;
import Implimentation.SportingImplimentation.JackpotProcessor;
import Utility.Utility;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
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
@WebServlet(urlPatterns = {"/sporting/games/jackpothighlights"})
public class JackpotGamesHighlightsAPI extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private String maindata;
    private JSONObject jsonobj=null;JSONArray responseObj  = null;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException, JSONException 
    {
        String method = req.getMethod();
        switch (method) 
        {
            case "METHOD_POST":
                doPost(req, resp);
                break;
            default:
                String errMsg = "Method Not Supported";
                resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, errMsg);
                break;
        }
    }
    
    

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException 
    {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = resp.getWriter(); 

        StringBuilder jb = new StringBuilder();
        String line = null;

        try 
        {
            BufferedReader reader = req.getReader();
            while ((line = reader.readLine()) != null)
            {
                jb.append(line);
            }
            
            System.out.println("bannerGamesHighlights==="+jb.toString());
            jsonobj = new JSONObject(jb.toString());
            String action=jsonobj.getString("action");
            maindata=jsonobj.getString("data");

            if(action.equals("jackpot_highlight_games"))
            {
                JSONObject dataObj  = new JSONObject();
                JSONArray dataArray = new JSONArray();
                String []data=maindata.trim().split("#");

                int validateJackpot=new JackpotProcessor().validateJackpotCreation();
                if(validateJackpot != 10)
                {
                    int status=new JackpotProcessor().highlightJackpotGames(data);
                    if(status == 200)
                    {
                        dataObj.put("message", "highlight successful");
                        dataArray.put(dataObj);
                    }
                    else
                    {
                        dataObj.put("error", "highlight failed");
                        dataArray.put(dataObj);
                    }
                }
                else
                {
                   dataObj.put("error", "Jackpot exist");
                   dataArray.put(dataObj);
                }
                responseObj=dataArray;
            }
            else if(action.equals("jackpot_unhighlight_games"))
            {
                String data=maindata.trim();
                responseObj=new GamesHighlightsImpl().setunHighlightJackpot(data);
            }
        }
        catch (IOException | JSONException ex) 
        { 
            ex.getMessage();
        }
        
        out.print(responseObj);
    }

}