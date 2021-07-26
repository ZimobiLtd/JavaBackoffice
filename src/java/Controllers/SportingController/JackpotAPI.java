package Controllers.SportingController;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Implimentation.SportingImplimentation.JackpotProcessor;
import Implimentation.SportingImplimentation.JackpotImpl;
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
@WebServlet(urlPatterns = {"/jackpot/data"})
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
                doGet(req, resp);
                break;
            case "METHOD_DELETE":
                doDelete(req, resp);
                break;
            default:
                String errMsg = "Method Not Supported";
                resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, errMsg);
                break;
        }
    }
    
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException 
    {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = resp.getWriter(); 

        responseObj=new JackpotImpl().getJackpots();
        
        out.print(responseObj);
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

            System.out.println("getJackpotData==="+jb.toString());
            jsonobj = new JSONObject(jb.toString());
            String action=jsonobj.getString("action");
            maindata=jsonobj.getString("data");

            if(action.equals("jackpot_games"))
            {
                String jackpotID=maindata;
                String matchIDs=new JackpotImpl().getJackpotMatchIDs(jackpotID);
                responseObj=new JackpotImpl().getJackpotGames(matchIDs);
            }
            else if(action.equals("set_winners"))
            {
                String []data=maindata.split("#");
                String jackpotID=data[0];
                String winners=data[1];
                responseObj=new JackpotImpl().setJackpotWinners(jackpotID,winners); 
            }
            else if(action.equals("jackpot_winners"))
            {
                String jackpotID=maindata;
                responseObj=new JackpotImpl().getJackpotWinners(Integer.valueOf(jackpotID));
            }
            else if(action.equals("delete_jackpot"))
            {
                String jackpotID=maindata;
                int status=new JackpotProcessor().unMarkJackpotGames(Integer.valueOf(jackpotID));
                if(status == 200)
                {
                    responseObj=new JackpotImpl().getDeleteJackpot(jackpotID);
                }
                else
                {
                    JSONObject dataObj  = new JSONObject();
                    JSONArray dataArray = new JSONArray();

                    dataObj.put("error", "Request failed");
                    dataArray.put(dataObj);
                    responseObj = dataArray;
                } 
            }
        }
        catch (IOException | JSONException ex) 
        { 
            ex.getMessage();
        }
        
        out.print(responseObj);
    }
    
    
    

}
