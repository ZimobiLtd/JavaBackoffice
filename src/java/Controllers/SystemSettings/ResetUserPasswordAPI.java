package Controllers.SystemSettings;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Implimentation.SystemSettingsImplimentation.SystemUsersImpl;
import Utility.EmailSender;
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
@WebServlet(urlPatterns = {"/system/reset/password"})
public class ResetUserPasswordAPI extends HttpServlet {

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
            case "METHOD_PUT":
                doPost(req, resp);
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

        StringBuilder jb = new StringBuilder();
        String line = null;

        try 
        {
            BufferedReader reader = req.getReader();
            while ((line = reader.readLine()) != null)
            {
                jb.append(line);
            }

            System.out.println("getVerificationCodeStatus==="+jb.toString());
            jsonobj = new JSONObject(jb.toString());
            maindata=jsonobj.getString("data");

            String []data=maindata.split("#");
            String code=data[0];

            int status=new SystemUsersImpl().verifyCode(code) ;
            if(status==1)
            {
                JSONObject dataObj  = new JSONObject();
                JSONArray dataArray = new JSONArray();

                dataObj.put(",message", "Valid code");
                dataArray.put(dataObj);
                resp.setStatus(500);
                responseObj=dataArray;   
            }
            else
            {
                JSONObject dataObj  = new JSONObject();
                JSONArray dataArray = new JSONArray();

                dataObj.put("error", "Invalid code");
                dataArray.put(dataObj);
                resp.setStatus(500);
                responseObj=dataArray;   
            }
        }
        catch (IOException | JSONException ex) 
        { 
            ex.getMessage();
        }
        
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
            
            System.out.println("resetUserPassword==="+jb.toString());
            jsonobj = new JSONObject(jb.toString());
            maindata=jsonobj.getString("data");

            JSONObject dataObj;
            JSONArray dataArray = new JSONArray();
            String email=maindata;

            int email_status=new SystemUsersImpl().validateEmail(email);
            if(email_status==1)
            {
                String code=new Utility().generateCode(7);
                String text="We received a request to reset the password on your StarBet Back Office Account.<br>Kindly click on the link below.<br><br>" +
                            "http://167.86.68.102:7074/resetPass/"+code+"<br><br>" +
                            "Regards,<br>" +
                            "StartBet Team";
                int status=new SystemUsersImpl().saveCode(email, code);
                
                if(status>0)
                {
                    dataObj  = new JSONObject();
                    dataObj.put("message", "Password reset link has been sent to your email"); 
                    dataArray.put(dataObj);
                    resp.setStatus(200);
                    responseObj=dataArray;
                }
                else
                {
                    dataObj  = new JSONObject();
                    dataObj.put("error", "request failed");
                    dataArray.put(dataObj);
                    resp.setStatus(500);
                    responseObj=dataArray;                            
                }
                
                Runnable myrunnable=() -> {
                    new EmailSender().postEmail(email,text);
                };
                new Thread(myrunnable).start();
            }
            else
            {
                dataObj  = new JSONObject();
                dataObj.put("error", "Invalid email address");
                dataArray.put(dataObj);
                resp.setStatus(500);
                responseObj=dataArray;  
            }
        }
        catch (IOException | JSONException ex) 
        { 
            ex.getMessage();
        }
        
        out.print(responseObj);
    }
    
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
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
            
            System.out.println("updateUserPassword==="+jb.toString());
            jsonobj = new JSONObject(jb.toString());
            maindata=jsonobj.getString("data");

            String []data=maindata.split("#");
            String email=data[0];
            //String new_pass=data[1];
            String new_pass=new Utility().generateCode(7);

            JSONObject dataObj  = new JSONObject();
            JSONArray dataArray = new JSONArray();

            int respo=new SystemUsersImpl().changePassword(email,new_pass,0);
            if(respo==200)
            {
                dataObj  = new JSONObject();
                dataObj.put("message", "Password changed successfully"); 
                dataArray.put(dataObj);
                resp.setStatus(200);
                responseObj=dataArray;

                Runnable myrunnable=new Runnable()
                {
                    public void run()
                    {
                        String text="Hi,<br>Your starbet password was changed.New password is: "+new_pass+".Thanks,<br>StarBet Team.";
                        new EmailSender().postEmail(email,text);
                    }
                };
                new Thread(myrunnable).start();
            }
            else
            {
                dataObj  = new JSONObject();
                dataObj.put("error", "Password change failed");
                dataArray.put(dataObj);
                resp.setStatus(500);
                responseObj=dataArray;                            
            } 
        }
        catch (IOException | JSONException ex) 
        { 
            ex.getMessage();
        }
        
        out.print(responseObj);
    }
    
    

}
