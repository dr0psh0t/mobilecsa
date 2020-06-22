package wmdc.mobilecsa.servlet.getJsonData;

import org.json.JSONObject;
import wmdc.mobilecsa.utils.Utils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/getlogs")

public class GetLogs extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        ServletContext ctx = getServletContext();
        PrintWriter out = response.getWriter();

        if (!Utils.isOnline(request, ctx)) {
            out.println("Login first");
            return;
        }

        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet result = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            prepStmt = conn.prepareStatement("SELECT error_id, error_message FROM error_logs ORDER BY error_id DESC");
            result = prepStmt.executeQuery();

            while (result.next()) {
                out.println(result.getInt("error_id")+"|"+result.getString("error_message"));
                out.print("\n\n");
            }

        } catch (ClassNotFoundException | SQLException sqe) {

            Utils.printJsonException(new JSONObject(), "Database error occurred.", out);
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "DBException",
                    sqe.toString(), getServletContext(), conn);

        } catch (Exception e) {
            Utils.printJsonException(new JSONObject(), "Exception has occurred.", out);
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "Exception", e.toString(),
                    getServletContext(), conn);

        } finally {
            Utils.closeDBResource(conn, prepStmt, result, getServletContext());
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
