/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import java.util.Base64; 
import java.util.Date;

/**
 *
 * @author jac
 */
@WebServlet(urlPatterns = {"/Login"})
public class Login extends HttpServlet {

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
        String type="betting";JSONObject jsonobj=null;JSONObject responseobj  = null;
        protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
            
            System.out.println("===vipi");
            // Get client's origin
            String clientOrigin = req.getHeader("origin");
            System.out.println("clientOrigin==="+clientOrigin);
            resp.setContentType("text/json;charset=UTF-8");
            resp.setHeader("Access-Control-Allow-Origin", "*");//http://167.86.68.102:7074
            //resp.setHeader("Access-Control-Allow-Headers","origin, content-type, accept, authorization");
            //resp.setHeader("Access-Control-Allow-Credentials", "true");
            resp.setHeader("Access-Control-Allow-Methods", "POST");
            
            
            StringBuffer jb = new StringBuffer();
            String line = null;

            try 
            {

                   BufferedReader reader = req.getReader();
                   while ((line = reader.readLine()) != null)
                   {
                       jb.append(line);
                   }
                   
                   System.out.println("Login Data===="+jb.toString());
                   jsonobj = new JSONObject(jb.toString());
                   username=jsonobj.getString("username");
                   password=jsonobj.getString("password");
                   System.out.println(username+"===="+password);
                   response=login(username,password);
                   System.out.println("respo===="+response);
                   
                   if(!response.startsWith("Invalid"))
                   {
                        String []data=response.split("#");//user+"#"+mobile+"#"+email+"#"+role+"#"+token;
                        responseobj  = new JSONObject();
                        responseobj.put("username", data[0]);
                        responseobj.put("mobile", data[1]);
                        responseobj.put("email", data[2]);
                        responseobj.put("role", data[3]);
                        responseobj.put("userid", data[4]);
                        
                        logTrail(data[0],data[1],data[2],data[3],"success");
                        
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
    
    
    
      
      
        public String login(String username,String password)
        {
                  
            String res="";
            String response="";
            int usercount =0;
            int passcount =0;
            ResultSet rs=null;
            String userquery="select count(userid) from users where username='"+username+"' and blocked=1  limit 1 ";
            String passquery="select count(userid) from users where password = '"+password+"' and username='"+username+"' and blocked=1  limit 1 ";
            String query="select username,phonenumber,emailaddress,Role,userid from users where username='"+username+"' and password='"+password+"' and blocked=1  limit 1 ";
            
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

                   rs = stmt.executeQuery(userquery);
                   
                   while (rs.next())
                   {
                       usercount =rs.getInt(1);
                   }
                   if(usercount==1)
                   {
                        rs = stmt.executeQuery(passquery);
                        while (rs.next())
                        {
                            passcount =rs.getInt(1);
                        }
                         
                        if(passcount==1)
                        {
                            rs = stmt.executeQuery(query);
                            while (rs.next())
                            {
                                String user =rs.getString(1);
                                String mobile =rs.getString(2);
                                String email =rs.getString(3);
                                String role =rs.getString(4);
                                String userid =rs.getString(5);
                                String seed=new Date().getTime()+username+password;
                                String token=generateTocken(seed);
                                response=user+"#"+mobile+"#"+email+"#"+role+"#"+userid+"#"+token;
                            }
                        }
                        else
                        {
                            response="Invalid Username or Password";
                        }
                   }
                   else
                   {
                         response="Invalid Username or Password";
                   }

            rs.close();
            stmt.close();
            conn.close();
            }
            catch(Exception e)
            {

            }
                    
        return response;
        }
        
        
        
        public String logTrail(String username,String mobile,String email,String role,String comment)
        {
            String status="",success_code="Log404";
            String query = "INSERT INTO logintrail (username,mobile,email,role,comment) values(?,?,?,?,?) ";

             PreparedStatement ps=null;
            try(Connection conn = new DBManager(type).getDBConnection();
                Statement stmt=  conn.createStatement();)

            {

            ps = conn.prepareStatement(query);
            ps.setString(1,username );
            ps.setString(2,mobile ); 
            ps.setString(3,email ); 
            ps.setString(4,role );
            ps.setString(5,comment); 
            int i=ps.executeUpdate();
            if(i>0)
            {
                success_code="Log200";                
            }

            ps.close();
            stmt.close();
            conn.close();
            }
            catch(Exception e)
            {

            }

        return success_code;
       }
      
      
      
        public String generateTocken(String seed)
        {
           String token = null;
           Base64.Encoder encoder = Base64.getEncoder();
           token = encoder.encodeToString(seed.getBytes());
           //save token
        return token;
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
