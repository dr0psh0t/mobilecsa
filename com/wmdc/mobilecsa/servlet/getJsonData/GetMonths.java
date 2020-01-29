package wmdc.mobilecsa.servlet.getJsonData;

import org.json.JSONObject;
import wmdc.mobilecsa.utils.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by wmdcprog on 8/26/2017.
 */
@WebServlet("/getmonths")
public class GetMonths extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.illegalRequest(response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject responseJson = new JSONObject();

        if (!Utils.isOnline(request)) {
            Utils.printJsonException(responseJson, "Login first.", out);
            return;
        }

        ArrayList<JSONObject> arrayList = new ArrayList<>();
        JSONObject obj = new JSONObject();

        obj.put("month", "January");
        obj.put("id", 1);
        arrayList.add(obj);

        obj = new JSONObject();
        obj.put("month", "February");
        obj.put("id", 2);
        arrayList.add(obj);

        obj = new JSONObject();
        obj.put("month", "March");
        obj.put("id", 3);
        arrayList.add(obj);

        obj = new JSONObject();
        obj.put("month", "April");
        obj.put("id", 4);
        arrayList.add(obj);

        obj = new JSONObject();
        obj.put("month", "May");
        obj.put("id", 5);
        arrayList.add(obj);

        obj = new JSONObject();
        obj.put("month", "June");
        obj.put("id", 6);
        arrayList.add(obj);

        obj = new JSONObject();
        obj.put("month", "July");
        obj.put("id", 7);
        arrayList.add(obj);

        obj = new JSONObject();
        obj.put("month", "August");
        obj.put("id", 8);
        arrayList.add(obj);

        obj = new JSONObject();
        obj.put("month", "September");
        obj.put("id", 9);
        arrayList.add(obj);

        obj = new JSONObject();
        obj.put("month", "October");
        obj.put("id", 10);
        arrayList.add(obj);

        obj = new JSONObject();
        obj.put("month", "November");
        obj.put("id", 11);
        arrayList.add(obj);

        obj = new JSONObject();
        obj.put("month", "December");
        obj.put("id", 12);
        arrayList.add(obj);

        responseJson.put("store", arrayList);
        out.println(responseJson);
    }
}