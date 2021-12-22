package Controllers.ReferralBonusController;

import Implimentation.promotionsImplimentation.ReferralBonusReport;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@WebServlet(urlPatterns =
{
    "/promotion"
})
public class ReferralBonusAPI extends HttpServlet {

    private String requestData;

    private JSONObject payload = null;

    JSONArray responseObject = null;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, JSONException
    {
        String method = req.getMethod();
        switch (method)
        {
            case "METHOD_GET":
                doGet(req, resp);
                return;
            case "METHOD_POST":
                doPost(req, resp);
                return;
        }
        String errMsg = "Method Not Supported";
        resp.sendError(501, errMsg);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = resp.getWriter();
        this.responseObject = (new ReferralBonusReport()).getPromotions();
        out.print(this.responseObject);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = resp.getWriter();
        StringBuilder jb = new StringBuilder();
        String line = null;
        try
        {
            String fromDate, toDate, mobile, mobileNumber, data[], phone, referreeMobile, response[];
            BufferedReader reader = req.getReader();
            while ((line = reader.readLine()) != null)
            {
                jb.append(line);
            }
            this.payload = new JSONObject(jb.toString());
            String searchKey = this.payload.getString("search_key");
            this.requestData = this.payload.getString("data");
            switch (searchKey)
            {
                case "mobile":
                    mobile = this.requestData;
                    mobileNumber = (mobile.startsWith("07") || mobile.startsWith("01")) ? ("254" + mobile.substring(1)) : mobile;
                    this.responseObject = (new ReferralBonusReport()).getReferalBonusByMobile(mobileNumber);
                    break;
                case "bonus_winner_referrals":
                    phone = this.requestData;
                    referreeMobile = (phone.startsWith("07") || phone.startsWith("01")) ? ("254" + phone.substring(1)) : phone;
                    this.responseObject = (new ReferralBonusReport()).getReferalsAndBetsCount(referreeMobile);
                    break;
                case "promotion":
                    data = this.requestData.split("#");
                    fromDate = data[0];
                    toDate = data[1];
                    String promotionID = data[2];
                    this.responseObject = (new ReferralBonusReport()).getReferalBonusByDateRange(fromDate, toDate, promotionID);
                    break;
                default:
                    response = (new Utility()).getDatesRange(-1);
                    fromDate = response[0];
                    toDate = response[1];
                    this.responseObject = (new ReferralBonusReport()).getReferalBonusByDateRange(fromDate, toDate,"1");
                    break;
            }
        }
        catch (IOException | JSONException ex)
        {
            ex.getMessage();
        }
        out.print(this.responseObject);
    }
}
