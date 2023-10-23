package Controllers.SigninController;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
@WebServlet(urlPatterns =
{
    "/test"
})
public class TetsController extends HttpServlet {

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
    private JSONObject jsonobj = null;
    JSONObject responseObj = null;
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

        out.print("ok");
    }

}
