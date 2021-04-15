package Sporting;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Database.DBManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import java.util.Base64; 
import java.util.Date;
import org.json.JSONArray;

/**
 *
 * @author jac
 */
@WebServlet(urlPatterns = {"/MarketsSettings"})
public class MarketsSetttings extends HttpServlet {

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
        String type="betting";JSONObject jsonobj=null;JSONArray responseobj  = null;
        public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
            resp.setContentType("text/json;charset=UTF-8");
            resp.setHeader("Access-Control-Allow-Origin", "*");
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

                
                jsonobj = new JSONObject(jb.toString());
                function=jsonobj.getString("function");
                maindata=jsonobj.getString("data");


                if(function.equals("getMarkets_Sports"))
                {
                    responseobj=getMarkets();
                }


                
                
                if(function.equals("saveMarkets"))
                {
                    JSONObject dataObj  = new JSONObject();
                    JSONArray dataArray = new JSONArray();
                    
                    String []data=maindata.split("#");
                    String market_id=data[0];
                    String market_name=data[1];
                    String sport_id=data[2];
                    
                    int respo_id=saveActiveMarkets(market_id,market_name,sport_id);
                    if(respo_id>0)
                    {
                        dataObj  = new JSONObject();
                        dataObj.put("message", "Market saved successfully"); 
                        dataArray.put(dataObj);
                        resp.setStatus(200);
                        responseobj=dataArray;
                    }
                    else
                    {
                        dataObj  = new JSONObject();
                        dataObj.put("error", "request failed");
                        dataArray.put(dataObj);
                        resp.setStatus(500);
                    }
                       
                }
                
                
                
                if(function.equals("removeMarket"))
                {
                    JSONObject dataObj  = new JSONObject();
                    JSONArray dataArray = new JSONArray();
                    
                    String []data=maindata.split("#");
                    String market_id=data[0];
                    String sport_id=data[1];
                    
                    int status=removeActiveMarkets(market_id,sport_id);
                    if(status>0)
                    {
                        dataObj  = new JSONObject();
                        dataObj.put("message", "market removed successfully"); 
                        dataArray.put(dataObj);
                        resp.setStatus(200);
                        responseobj=dataArray;
                    }
                    else
                    {
                        dataObj  = new JSONObject();
                        dataObj.put("error", "request failed");
                        dataArray.put(dataObj);
                        resp.setStatus(500);
                        responseobj=dataArray;  
                    }
                    
                }
                
                
                if(function.equals("getActiveMarkets"))
                {
                    responseobj=getActiveMarkets();
                }
                   
                   
             }catch (Exception ex) { ex.getMessage();}
            
             PrintWriter out = resp.getWriter(); 
            out.print(responseobj);
        }
    
    
        
        
        
        
        public JSONArray getMarkets()
        {
                  
            String dataQuery="";

            dataQuery = "select Mar_Code,Mar_Name from markets";
           
            System.out.println("getgetMarkets==="+dataQuery);

            JSONObject dataObj  = null;
            JSONArray dataArray1 = new JSONArray();
            JSONArray dataArray2 = new JSONArray();
            JSONArray respoArray = new JSONArray();

            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

                ResultSet rs = stmt.executeQuery(dataQuery);

                while (rs.next())
                {
                   String code = rs.getString(1);
                   String name = rs.getString(2);

                   dataObj  = new JSONObject();
                   dataObj.put("MarketCode", code);
                   dataObj.put("MarketName", name);
                   dataArray1.put(dataObj);                   
                }
                respoArray.put(dataArray1);
                
                
                
                dataQuery = "select Sport_Code,Sport_Name from sports";
                System.out.println("getgetSports==="+dataQuery);
                rs = stmt.executeQuery(dataQuery);
                while (rs.next())
                {
                    
                   String sport_code = rs.getString(1);
                   String sport_name = rs.getString(2);

                   dataObj  = new JSONObject();
                   dataObj.put("SportCode", sport_code);
                   dataObj.put("SportName", sport_name);
                   dataArray2.put(dataObj);
                   
                }
                respoArray.put(dataArray2);

                

            rs.close();
            stmt.close();
            conn.close();
            }
            catch(Exception e)
            {

            }
                    
        return respoArray;
        }
        
        
        
        
        public int saveActiveMarkets(String market_id, String market_name, String sport_id) 
        {
           int respo_id=0; 
            String dataQuery="";

            dataQuery = "INSERT INTO Active_markets (Mar_Name,Mar_ID,Sport_ID) VALUES(?,?,?)";
           
            java.sql.Timestamp timestamp = new java.sql.Timestamp(new Date().getTime());  
            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();
            PreparedStatement ps=null;
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

                ps = conn.prepareStatement(dataQuery);
                ps.setInt(1,Integer.valueOf(market_id) );
                ps.setString(2,market_name ); 
                ps.setInt(3,Integer.valueOf(sport_id) ); 
                respo_id=ps.executeUpdate();
                
                
            ps.close();
            stmt.close();
            conn.close();
            }
            catch(Exception e)
            {

            }
        return respo_id;    
        }
        
        
        
        
         public JSONArray getActiveMarkets()
        {
                  
            String dataQuery="";

            dataQuery = "select Mar_ID,Mar_Name,Sport_Name from Active_markets inner join sports on sports.Sport_Code=Active_markets.Sport_ID";
           
            System.out.println("getgetMarkets==="+dataQuery);

            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();

            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

                ResultSet rs = stmt.executeQuery(dataQuery);

                while (rs.next())
                {
                   String market_id = rs.getString(1);
                   String market_name = rs.getString(2);
                   String sport_name = rs.getString(3);

                   dataObj  = new JSONObject();
                   dataObj.put("MarketCode", market_id);
                   dataObj.put("MarketName", market_name);
                   dataObj.put("SportName", sport_name);
                   dataArray.put(dataObj);                   
                }
                
                

            rs.close();
            stmt.close();
            conn.close();
            }
            catch(Exception e)
            {

            }
                    
        return dataArray;
        }
        
         
         
        
        public int removeActiveMarkets(String market_id,String sport_id) 
        {
           int respo_id=0; 
            String dataQuery="";

            java.sql.Timestamp timestamp = new java.sql.Timestamp(new Date().getTime());  
            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();
            PreparedStatement ps=null;
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {
                
                dataQuery = "delete from Active_markets where Mar_ID="+market_id+" and Sport_ID="+sport_id+" ";
                stmt.executeUpdate(dataQuery);
                
                respo_id=1;
               
            stmt.close();
            conn.close();
            }
            catch(Exception e)
            {

            }
        return respo_id;    
        }
        
        
        
        
        public String[] initDates() 
        {
            String []data=null;

            try 
            {

                Date startDate = new Date();//sdf.parse(fromDate);
                long curTime = startDate.getTime();
                long interval = (24 * 1000 * 60 * 60)*5;
                curTime -= interval;
                String fromdate=sdf.format(new Date(curTime));
                String todate=sdf.format(startDate);

                data=new String[]{fromdate,todate};//fromdate+"#"+todate ;

            } catch (Exception ex) {

            }

        return data;
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
