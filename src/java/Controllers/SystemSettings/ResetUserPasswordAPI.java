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
            
            System.out.println("UserPassword==="+jb.toString());
            jsonobj = new JSONObject(jb.toString());
            String action=jsonobj.getString("action");
            maindata=jsonobj.getString("data");

            if(action.equals("verify_code"))
            {
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
            else if(action.equals("reset_password"))
            {
                String email=maindata;
                String new_pass=new Utility().generateCode(7);

                JSONObject dataObj;
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
            else if(action.equals("update_password"))
            {
                String []data=maindata.split("#");
                String email=data[0];
                String newPassword=data[1];
                String currentPassword=data[2];

                JSONObject dataObj;
                JSONArray dataArray = new JSONArray();

                int status=new SystemUsersImpl().validateOldPassword (email,currentPassword);
                if(status == 1)
                {
                    int respo=new SystemUsersImpl().changePassword(email,newPassword,1);
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
                                String text="Hi,<br>Your starbet password was changed.New password is: "+newPassword+".Thanks,<br>StarBet Team.";
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
                else
                {
                    dataObj  = new JSONObject();
                    dataObj.put("error", "Password not verified");
                    dataArray.put(dataObj);
                    resp.setStatus(500);
                    responseObj=dataArray; 
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
