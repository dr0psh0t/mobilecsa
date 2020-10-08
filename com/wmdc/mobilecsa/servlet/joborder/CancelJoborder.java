package wmdc.mobilecsa.servlet.joborder;

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

@WebServlet("/canceljoborder")

public class CancelJoborder extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject resJson = new JSONObject();
        ServletContext ctx = getServletContext();

        if (!Utils.isOnline(request, ctx)) {
            Utils.printJsonException(resJson, "Login first.", out);
            return;
        }

        String initialJoborderIdStr = request.getParameter("initialJoborderId");

        if (initialJoborderIdStr == null) {
            Utils.logError("\"initialJoborderIdStr\" parameter is null", ctx);
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
            return;
        } else if (initialJoborderIdStr.isEmpty()) {
            Utils.logError("\"initialJoborderIdStr\" parameter is empty", ctx);
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
            return;
        }

        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            int initialJoborderId = Integer.parseInt(initialJoborderIdStr);

            prepStmt = conn.prepareStatement("SELECT cancelled FROM initial_joborder WHERE initial_joborder_id = ?");
            prepStmt.setInt(1, initialJoborderId);

            resultSet = prepStmt.executeQuery();

            int cancelled = 0;

            if (resultSet.next()) {
                cancelled = resultSet.getInt("cancelled");
            }

            if (cancelled > 0) {
                Utils.printJsonException(resJson, "Initial Joborder is already cancelled.", out);
            } else {
                prepStmt = conn.prepareStatement(
                        "UPDATE initial_joborder SET cancelled = ? WHERE initial_joborder_id = ?");
                prepStmt.setInt(1, 1);
                prepStmt.setInt(2, initialJoborderId);

                if (prepStmt.executeUpdate() > 0) {
                    Utils.printSuccessJson(resJson, "Initial joborder is cancelled.", out);
                } else {
                    Utils.printSuccessJson(resJson, "No initial joborder/s cancelled.", out);
                }
            }

        } catch (SQLException sqe) {
            Utils.printJsonException(resJson, "Cannot cancel joborder at this time.", out);
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.JOBORDER_PACKAGE, "SQLException",
                    sqe.toString(), ctx, conn);

        } catch (ClassNotFoundException classE) {
            Utils.printJsonException(resJson, "Cannot cancel joborder at this time.", out);
            Utils.displayStackTraceArray(classE.getStackTrace(), Utils.JOBORDER_PACKAGE, "ClassException",
                    classE.toString(), ctx, conn);

        } catch (Exception e) {
            Utils.printJsonException(resJson, "Cannot cancel joborder at the moment.", out);
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.JOBORDER_PACKAGE, "Exception", e.toString(), ctx,
                    conn);

        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet, ctx);
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.illegalRequest(response);
    }
}
