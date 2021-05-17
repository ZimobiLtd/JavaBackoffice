package Authentication;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Authentication.AuthenticationProcessor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

/**
 *
 * @author jac
 */
@WebServlet(urlPatterns = {"/Login"})
public class AuthenticateAPI extends HttpServlet {

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
    String response,username ,password;
    JSONObject jsonobj=null;JSONObject responseobj  = null;
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

        // Get client's origin
        String clientOrigin = req.getHeader("origin");
        resp.setContentType("text/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");//http://167.86.68.102:7074
        //resp.setHeader("Access-Control-Allow-Headers","origin, content-type, accept, authorization");
        //resp.setHeader("Access-Control-Allow-Credentials", "true");
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
            username=jsonobj.getString("username");
            password=jsonobj.getString("password");
            response=new AuthenticationProcessor().authenticateUser(username,password);

            if(!response.startsWith("Invalid"))
            {
                String []data=response.split("#");//user+"#"+mobile+"#"+email+"#"+role+"#"+token;
                responseobj  = new JSONObject();
                responseobj.put("username", data[0]);
                responseobj.put("mobile", data[1]);
                responseobj.put("email", data[2]);
                responseobj.put("role", data[3]);
                responseobj.put("userid", data[4]);
                responseobj.put("userstatus", data[5]);

                new AuthenticationProcessor().saveLogTrail(data[0],data[1],data[2],data[3],"success");
                resp.setStatus(200); 
           }
           else
           {
                responseobj  = new JSONObject();
                responseobj.put("error", response);
                resp.setStatus(401);
            }

         }catch (Exception ex) { ex.getMessage();}

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
