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
@WebServlet(urlPatterns = {"/Communication"})
public class Communication extends HttpServlet {

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

                System.out.println("ActivePlayer===="+jb.toString());
                jsonobj = new JSONObject(jb.toString());
                function=jsonobj.getString("function");
                maindata=jsonobj.getString("data");


                if(function.equals("getCommunication"))
                {
                     String []respo=initDates();
                     String fromdate=respo[0];
                     String todate=respo[1];
                     responseobj=getCommunication("2017-01-23" ,"2020-12-30");
                }


                if(function.equals("filterCommunication"))
                {
                     String [] data=maindata.split("#");
                     String from=data[0];
                     String to=data[1];
                     String type=data[2];
                     responseobj=filterCommunication(from ,to,type);                       
                }
                   
                   
                   
             }catch (Exception ex) { ex.getMessage();}
            
             PrintWriter out = resp.getWriter(); 
            out.print(responseobj);
        }
    
    
        public JSONArray getCommunication(String from,String to)
        {
                  
            String res=""; 
            String dataQuery = "SELECT  Out_Mobile_No,Out_Date as 'Date' ,'SMS'as'Type',Out_Msg as 'Message', 'Outgoing', 'Sent' as 'Status' from outgoing_msg  where "
                + " Out_Mobile_No=Out_Mobile_No  and Out_Date between '" + from + "' and '" + to + "' UNION all select `from`,datecreated as 'Date','SMS'as'Type' , message as 'Message', 'Incomming', 'Received' as 'Status' "
                + " from messages_incomming_amazon where `from`=`from` and  datecreated between '" + from + "' and '" + to + "' union all "
                + "select Loop_Mobile,Loop_Date,'USSD'as'Type', Loop_Step,'Incoming' as direction,'Received' as Status from ussd_loop_steps where  Loop_Mobile=Loop_Mobile and Loop_Date between '" + from + "' and '" + to + "'";

            
            
            System.out.println("getCommunication==="+dataQuery);
            
            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();
            
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

                ResultSet rs = stmt.executeQuery(dataQuery);

                while (rs.next())
                {
                     String mobile =rs.getString(1);
                     String date = rs.getString(2);
                     String type = rs.getString(3);
                     String message = rs.getString(4);
                     String direction = rs.getString(5);
                     String status = rs.getString(6);
                  
                     dataObj  = new JSONObject();
                     dataObj.put("Mobile", "0"+mobile.substring(3));
                     dataObj.put("Date", date);
                     dataObj.put("Type", type);
                     dataObj.put("Message", message);
                     dataObj.put("Direction", direction);
                     dataObj.put("Status", status);
                     dataArray.put(dataObj);
                }

                if(dataArray.length()==0)
                {
                    dataObj  = new JSONObject();
                    dataObj.put("Mobile", "0");
                    dataObj.put("Date", "0");
                    dataObj.put("Type", "0");
                    dataObj.put("Message", "0");
                    dataObj.put("Direction", "0");
                    dataObj.put("Status", "0");
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
        
        
        
        public JSONArray filterCommunication(String from,String to,String searchtype)
        {
            String dataQuery="";
            if(searchtype.equalsIgnoreCase("SMS")) 
            {
               dataQuery = "SELECT  Out_Mobile_No,Out_Date as 'Date','SMS'as'Type',Out_Msg as 'Message', 'Outgoing', 'Sent' as 'Status' from outgoing_msg   "
                + "where  Out_Mobile_No=Out_Mobile_No   and Out_Date between '" + from + "' and '" + to + "'  UNION all select `from`,datecreated as 'Date' ,'SMS' AS 'Type', message as 'Message', 'Incomming', 'Received' as 'Status'  from messages_incomming_amazon where `from`=`from` and datecreated between '" + from + "' and '" + to + "'";

            }
            else if(searchtype.equalsIgnoreCase("USSD")) 
            {
                dataQuery = "SELECT Loop_Mobile,Loop_Date,'USSD' AS 'Type',Loop_Step,'Incoming' AS direction,'Received' AS STATUS "
                + "FROM ussd_loop_steps where  Loop_Mobile=Loop_Mobile and Loop_Date between '" + from + "' and '" + to + "' ";
            }
            else
            {
                dataQuery = "SELECT  Out_Mobile_No,Out_Date as 'Date' ,'SMS'as'Type',Out_Msg as 'Message', 'Outgoing', 'Sent' as 'Status' from outgoing_msg  where "
                + " Out_Mobile_No=Out_Mobile_No  and Out_Date between '" + from + "' and '" + to + "' UNION all select `from`,datecreated as 'Date','SMS'as'Type' , message as 'Message', 'Incomming', 'Received' as 'Status' "
                + " from messages_incomming_amazon where `from`=`from` and  datecreated between '" + from + "' and '" + to + "' union all "
                + "select Loop_Mobile,Loop_Date,'USSD'as'Type', Loop_Step,'Incoming' as direction,'Received' as Status from ussd_loop_steps where  Loop_Mobile=Loop_Mobile and Loop_Date between '" + from + "' and '" + to + "'";

            }
            
            System.out.println("filterCommunication==="+dataQuery);
            
            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();
            
            try( Connection conn = new DBManager(type).getDBConnection();
            Statement stmt = conn.createStatement();)
            {

                ResultSet rs = stmt.executeQuery(dataQuery);

                while (rs.next())
                {
                     String mobile =rs.getString(1);
                     String date = rs.getString(2);
                     String type = rs.getString(3);
                     String message = rs.getString(4);
                     String direction = rs.getString(5);
                     String status = rs.getString(6);
                  
                     dataObj  = new JSONObject();
                     dataObj.put("Mobile", "0"+mobile.substring(3));
                     dataObj.put("Date", date);
                     dataObj.put("Type", type);
                     dataObj.put("Message", message);
                     dataObj.put("Direction", direction);
                     dataObj.put("Status", status);
                     dataArray.put(dataObj);
                }

                if(dataArray.length()==0)
                {
                    dataObj  = new JSONObject();
                    dataObj.put("Mobile", "0");
                    dataObj.put("Date", "0");
                    dataObj.put("Type", "0");
                    dataObj.put("Message", "0");
                    dataObj.put("Direction", "0");
                    dataObj.put("Status", "0");
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
