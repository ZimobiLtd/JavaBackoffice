package Betting;

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
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
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
@WebServlet(urlPatterns = {"/BetDetails"})
public class BetDetails extends HttpServlet {

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
        JSONObject jsonobj=null;JSONArray responseobj  = null;
        public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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

                System.out.println("BetDetails===="+jb.toString());
                jsonobj = new JSONObject(jb.toString());
                function=jsonobj.getString("function");
                maindata=jsonobj.getString("data");


                if(function.equals("getBetDetails"))
                {
                     String betslipID=getBetGroupID(maindata);
                     responseobj=getAllMultibets( betslipID);
                }
                     
             }catch (Exception ex) { ex.getMessage();}
            
             PrintWriter out = resp.getWriter(); 
            out.print(responseobj);
        }
    
    
        
        
        public JSONArray getAllMultibets(String mulGroupID)
        {
                  
            String res="";
            /*String dataQuery = "select A.Mul_ID, ifnull(C.Match_Type_Desc,'no match type'), ifnull(Torna_Match_Event_Time,'no event'), "
                    + "ifnull(concat(Torna_Sport_Name,'->',Torna_Cat_Name,'->',Torna_Name,'->',Torna_Match_Event),'no event'), A.Mul_Game_ID, A.Mul_Match_Name,"
                    + " A.Mul_Prediction, B.Bet_Status_Name, A.Mul_Bet_Odd, A.Mul_Bet_Winning_Pred  from multibets A ,tournament, bet_status B ,match_type C  "
                    + "where A.Mul_Group_ID = "+betslipID+"  and Torna_Match_ID=A.Mul_Match_ID and A.Mul_Bet_Status = B.Bet_Status_Code and C.Match_Type_Status_ID =1 ";*/
            String query="select A.Mul_ID, ifnull( A.Mul_Sportname,'no sport'), ifnull( A.Mul_EventTime,'no event time'), ifnull(Mul_Event,'no event'), A.Mul_Game_ID, A.Mul_Match_Name,"
                    + " A.Mul_Prediction, B.Bet_Status_Name, A.Mul_Bet_Odd, A.Mul_Bet_Winning_Pred  from multibets A , bet_status B  where A.Mul_Group_ID ="+mulGroupID+"  and A.Mul_Bet_Status = B.Bet_Status_Code ";
            System.out.println("getAllMultibets==="+query);
            
            JSONObject dataObj  = null;
            JSONArray dataArray = new JSONArray();
            
            try( Connection conn = new DBManager().getDBConnection();
            Statement stmt = conn.createStatement();)
            {
                
                ResultSet rs = stmt.executeQuery(query);
                
                    while (rs.next())
                    {
                        
                        dataObj  = new JSONObject();
                        String bet_id = rs.getString(1);
                        String match_type = rs.getString(2);
                        String event_date="";
                        String val = rs.getString(3);//rs.getTimestamp(3);
                        if(val.equals("no event time"))
                        {
                            event_date="no event time";
                        }
                        else
                        {
                            event_date =val.substring(0,val.length() -3);
                        }
                        String event = rs.getString(4);
                        String bet_game_id = rs.getString(5);
                        String bet_market_id = rs.getString(6);
                        String prediction = rs.getString(7);
                        String bet_status = rs.getString(8);
                        String odds = rs.getString(9);
                        String outcome = rs.getString(10);


                        dataObj.put("Bet_ID", bet_id);
                        dataObj.put("Match_Type", match_type);
                        dataObj.put("Event_Date", event_date);
                        dataObj.put("Event", event);
                        dataObj.put("Bet_Game_ID", bet_game_id);
                        dataObj.put("Bet_Market_ID", bet_market_id);
                        dataObj.put("Bet_Prediction", prediction);
                        dataObj.put("Bet_Status", bet_status);
                        dataObj.put("Odds", odds);
                        dataObj.put("Outcome", outcome);
                        dataArray.put(dataObj);
                    }
                    
                    if(dataArray.length()==0)
                    {
                        dataObj  = new JSONObject();
                        dataObj.put("Bet_ID", "0");
                        dataObj.put("Match_Type", "0");
                        dataObj.put("Event_Date", "0");
                        dataObj.put("Event", "0");
                        dataObj.put("Bet_Game_ID", "0");
                        dataObj.put("Bet_Market_ID", "0");
                        dataObj.put("Bet_Prediction", "0");
                        dataObj.put("Bet_Status", "0");
                        dataObj.put("Odds", "0");
                        dataObj.put("Outcome", "0");
                        dataArray.put(dataObj);
                    }
                
            
            stmt.close();
            conn.close();
            }
            catch(Exception e)
            {

            }
                    
        return dataArray;
        }
        
        
        
        public String getBetGroupID(String betslipid)
        {

            String data="";
            String query="select Play_Bet_Group_ID from player_bets where Play_Bet_Slip_ID = '" + betslipid + "'";

            try( Connection conn = new DBManager().getDBConnection();
            Statement stmt = conn.createStatement();)
            {

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {

            data = rs.getString(1);


            }

             rs.close();
             conn.close();
            } catch (SQLException ex) {

             System.out.println(ex.getMessage());
            }

        return data;
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
