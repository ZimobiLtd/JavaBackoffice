package Controllers.SigninController;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Implimentation.SigninImplimentation.AuthenticationImpl;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author jac
 */
@WebServlet(urlPatterns = {"/user/signin"})
public class SigninAPI extends HttpServlet {

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
    private JSONObject jsonobj=null;JSONObject responseObj  = null;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException, JSONException 
    {
        String method = req.getMethod();
        switch (method) 
        {
            case "METHOD_GET":
                doGet(req, resp);
                break;
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
            
            jsonobj = new JSONObject(jb.toString());
            String username=jsonobj.getString("username");
            String password=jsonobj.getString("password");
            String response=new AuthenticationImpl().authenticateUser(username,password);

            if(!response.startsWith("Invalid"))
            {
                String []data=response.split("#");
                responseObj  = new JSONObject();
                responseObj.put("username", data[0]);
                responseObj.put("mobile", data[1]);
                responseObj.put("email", data[2]);
                responseObj.put("role", data[3]);
                responseObj.put("userid", data[4]);
                responseObj.put("userstatus", data[5]);

                new AuthenticationImpl().saveLogTrail(data[0],data[1],data[2],data[3],"success");
                resp.setStatus(200); 
           }
           else
           {
                responseObj  = new JSONObject();
                responseObj.put("error", response);
                resp.setStatus(401);
            }
        }
        catch (IOException | JSONException ex) 
        { 
            System.out.println("Error=== "+ex.getMessage());
        }
        
        out.print(responseObj);
    }

}
