package wmdc.mobilecsa.servlet.joborder;

import org.json.JSONObject;
import wmdc.mobilecsa.utils.Utils;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;

/*** Created by wmdcprog on 4/27/2018.*/

@MultipartConfig(fileSizeThreshold = 1024*1024*6, maxFileSize=1024*1024*3, maxRequestSize = 1024*1024*50)

@WebServlet("/initialjoborder")

public class InitialJobOrder extends HttpServlet {
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

        Connection conn = null;
        PreparedStatement prepStmt = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            String customer = request.getParameter("customer");
            String source = request.getParameter("source");
            String mobile = request.getParameter("mobile");
            String serialNo = request.getParameter("serialNo");

            String purchaseOrder = request.getParameter("purchaseOrder");
            String referenceNo = request.getParameter("refNo");
            String poDate = request.getParameter("poDate");
            String dateReceive = request.getParameter("dateReceive");
            String remarks = request.getParameter("remarks");
            String imageType = request.getParameter("imageType");
            String category = request.getParameter("cat");
            String make = request.getParameter("make");
            String joSignature = request.getParameter("joSignature");
            String joNumber = request.getParameter("joNumber");
            Part photoPart = request.getPart("photo");

            checkParameter(customer, mobile, serialNo, poDate, dateReceive, category, remarks, make,
                    request.getParameter("customerId"), request.getParameter("modelId"),
                    request.getParameter("preparedBy"), referenceNo, joSignature, source, purchaseOrder, joNumber,
                    photoPart, out);

            int customerId = Integer.parseInt(request.getParameter("customerId"));
            int modelId = Integer.parseInt(request.getParameter("modelId"));
            int preparedBy = Integer.parseInt(request.getParameter("preparedBy"));

            if (isJoNumberExists(joNumber, conn)) {
                Utils.printJsonException(responseJson, "JO Number already exists.", out);
                Utils.logError("Joborder is already saved in mcsa database");
                return;
            }

            if (make.equals("-Select Engine-") || category.equals("-Select Engine-")) {
                Utils.printJsonException(responseJson, "Include Engine Model.", out);
                return;
            }

            InputStream photoStream = photoPart.getInputStream();
            InputStream signatureStream = Utils.getSignatureInputStream(joSignature);

            if (photoPart.getSize() > (Utils.REQUIRED_IMAGE_BYTES-100_000)) {
                photoStream = Utils.reduceImage(photoStream);
            }

            if (photoStream == null) {
                Utils.printJsonException(responseJson, "File stream is null. Cannot upload your photo.", out);
                return;
            }

            if (!referenceNo.equals(joNumber)) {
                Utils.printJsonException(responseJson, "Reference number should be the same with jo number.", out);
                return;
            }

            prepStmt = conn.prepareStatement("INSERT INTO initial_joborder(jo_number, customer_id, customer_source, " +
                            "mobile, model_id, serial_num, po_date, date_received, date_committed, category, " +
                            "prepared_by, remarks, purchase_order, reference_number, make, date_stamp, signature, " +
                            "csa_id, customer, is_added) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            prepStmt.setString(1, joNumber);
            prepStmt.setInt(2, customerId);
            prepStmt.setString(3, source);
            prepStmt.setString(4, mobile);
            prepStmt.setInt(5, modelId);
            prepStmt.setString(6, serialNo);
            prepStmt.setString(7, poDate);
            prepStmt.setString(8, dateReceive);
            prepStmt.setString(9, dateReceive);
            prepStmt.setString(10, category);
            prepStmt.setInt(11, preparedBy);
            prepStmt.setString(12, remarks);
            prepStmt.setString(13, purchaseOrder);
            prepStmt.setString(14, referenceNo);
            prepStmt.setString(15, make);
            prepStmt.setBlob(16, signatureStream);
            prepStmt.setInt(17, preparedBy);
            prepStmt.setString(18, customer);
            prepStmt.setInt(19, 0);     //  0 as initial
            prepStmt.execute();

            int[] dimen = getImageDimension(photoPart.getInputStream());
            int recentId = getRecentId(prepStmt);

            prepStmt = conn.prepareStatement("INSERT INTO initial_joborder_image (image, initial_joborder_id, " +
                    "prepared_by, date_stamp, image_type, width, height) VALUES (?, ?, ?, NOW(), ?, ?, ?)");

            prepStmt.setBlob(1, photoStream);
            prepStmt.setInt(2, recentId);
            prepStmt.setInt(3, preparedBy);
            prepStmt.setString(4, imageType);
            prepStmt.setInt(5, dimen[0]);
            prepStmt.setInt(6, dimen[1]);
            prepStmt.execute();

            if (isPictureSaved(recentId, conn)) {
                Utils.printSuccessJson(responseJson, "Initial Joborder added.", out);
            } else {
                Utils.printSuccessJson(responseJson,
                        "Initial Joborder added but no picture was saved. Must update the joborder with photo.", out);
            }
            photoStream.close();
        } catch (SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.JOBORDER_PACKAGE, "SQLException", sqe.toString());
            Utils.printJsonException(responseJson, "Database error occurred.", out);
        } catch (ClassNotFoundException classE) {
            Utils.displayStackTraceArray(classE.getStackTrace(), Utils.JOBORDER_PACKAGE, "ClassException", classE.toString());
            Utils.printJsonException(responseJson, "Database error occurred.", out);
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.JOBORDER_PACKAGE, "Exception", e.toString());
            Utils.printJsonException(responseJson, "Exception has occurred.", out);
        } finally {
            Utils.closeDBResource(conn, prepStmt, null);
            out.close();
        }
    }

    private int[] getImageDimension(InputStream inputStream) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        return new int[]{bufferedImage.getWidth(), bufferedImage.getHeight()};
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.illegalRequest(response);
    }

    private int getRecentId(PreparedStatement prepStmt) throws SQLException {
        ResultSet recentIdRS = prepStmt.getGeneratedKeys();
        int recentId = 0;

        if (recentIdRS.next()) {
            recentId = recentIdRS.getInt(1);
        }

        return recentId;
    }

    private boolean isPictureSaved(int initialJoId, Connection conn) throws SQLException {

        PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT COUNT (*) AS pictureCount FROM initial_joborder_image WHERE initial_joborder_id = ?");

        prepStmt.setInt(1, initialJoId);
        ResultSet resultSet = prepStmt.executeQuery();

        int pictureCount = 0;
        if (resultSet.next()) {
            pictureCount = resultSet.getInt("pictureCount");
        }

        prepStmt.close();
        resultSet.close();

        return pictureCount > 0;
    }

    private boolean isJoNumberExists(String joNo, Connection conn) throws SQLException {

        PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT COUNT (*) AS joNoCount FROM initial_joborder WHERE jo_number = ?");

        prepStmt.setString(1, joNo);
        ResultSet resultSet = prepStmt.executeQuery();

        int joNoCount = 0;
        if (resultSet.next()) {
            joNoCount = resultSet.getInt("joNoCount");
        }

        prepStmt.close();
        resultSet.close();

        return joNoCount > 0;
    }

    private void checkParameter(String customer, String mobile, String serialNo, String poDate, String dateReceive,
                               String category, String remarks, String make, String customerId,
                               String modelId, String preparedBy, String referenceNo, String joSignature,
                               String source, String purchaseOrder, String joNumber, Part photoPart, PrintWriter out) {
        try {
            if (customer == null) {
                Utils.logError("\"customer\" parameter is null.");
                Utils.printJsonException(new JSONObject(), "Customer is required. See logs or try again.", out);
                return;
            } else if (customer.isEmpty()) {
                Utils.logError("\"customer\" parameter is empty.");
                Utils.printJsonException(new JSONObject(), "Customer is required. See logs or try again.", out);
                return;
            }

            if (mobile == null) {
                Utils.logError("\"mobile\" parameter is null.");
                Utils.printJsonException(new JSONObject(), "Mobile is required. See logs or try again.", out);
                return;
            } else if (mobile.isEmpty()) {
                Utils.logError("\"mobile\" parameter is empty.");
                Utils.printJsonException(new JSONObject(), "Mobile is required. See logs or try again.", out);
                return;
            }

            if (serialNo == null) {
                Utils.logError("\"serialNo\" parameter is null.");
                Utils.printJsonException(new JSONObject(), "Serial is required. See logs or try again.", out);
                return;
            } else if (serialNo.isEmpty()) {
                Utils.logError("\"serialNo\" parameter is empty.");
                Utils.printJsonException(new JSONObject(), "Serial is required. See logs or try again.", out);
                return;
            }

            if (poDate == null) {
                Utils.logError("\"poDate\" parameter is null.");
                Utils.printJsonException(new JSONObject(), "PO Date is required. See logs or try again.", out);
                return;
            } else if (poDate.isEmpty()) {
                Utils.logError("\"poDate\" parameter is empty.");
                Utils.printJsonException(new JSONObject(), "PO Date is required. See logs or try again.", out);
                return;
            }

            if (dateReceive == null) {
                Utils.logError("\"dateReceive\" parameter is null.");
                Utils.printJsonException(new JSONObject(), "Date receive is required. See logs or try again.", out);
                return;
            } else if (dateReceive.isEmpty()) {
                Utils.logError("\"dateReceive\" parameter is empty.");
                Utils.printJsonException(new JSONObject(), "Date receive is required. See logs or try again.", out);
                return;

            }

            if (category == null) {
                Utils.logError("\"category\" parameter is null.");
                Utils.printJsonException(new JSONObject(), "Category is required. See logs or try again.", out);
                return;
            } else if (category.isEmpty()) {
                Utils.logError("\"category\" parameter is empty.");
                Utils.printJsonException(new JSONObject(), "Category is required. See logs or try again.", out);
                return;
            }

            if (remarks == null) {
                Utils.logError("\"remarks\" parameter is null.");
                Utils.printJsonException(new JSONObject(), "Remarks is required. See logs or try again.", out);
                return;
            } else if (remarks.isEmpty()) {
                Utils.logError("\"remarks\" parameter is empty.");
                Utils.printJsonException(new JSONObject(), "Remarks is required. See logs or try again.", out);
                return;
            }

            if (make == null) {
                Utils.logError("\"make\" parameter is null.");
                Utils.printJsonException(new JSONObject(), "Make is required. See logs or try again.", out);
                return;
            } else if (make.isEmpty()) {
                Utils.logError("\"make\" parameter is empty.");
                Utils.printJsonException(new JSONObject(), "Make is required. See logs or try again.", out);
                return;
            }

            if (customerId == null) {
                Utils.logError("\"customerId\" parameter is null.");
                Utils.printJsonException(new JSONObject(), "Customer required. See logs or try again.", out);
                return;
            } else if (customerId.isEmpty()) {
                Utils.logError("\"customerId\" parameter is empty.");
                Utils.printJsonException(new JSONObject(), "Customer required. See logs or try again.", out);
                return;
            }

            if (modelId == null) {
                Utils.logError("\"modelId\" parameter is null.");
                Utils.printJsonException(new JSONObject(), "Model is required. See logs or try again.", out);
                return;
            } else if (modelId.isEmpty()) {
                Utils.logError("\"modelId\" parameter is empty.");
                Utils.printJsonException(new JSONObject(), "Model is required. See logs or try again.", out);
                return;
            }

            if (preparedBy == null) {
                Utils.logError("\"preparedBy\" parameter is null.");
                Utils.printJsonException(new JSONObject(), "Prepared by is required. See logs or try again.", out);
                return;
            } else if (preparedBy.isEmpty()) {
                Utils.logError("\"preparedBy\" parameter is empty.");
                Utils.printJsonException(new JSONObject(), "Prepared by is required. See logs or try again.", out);
                return;
            }

            if (referenceNo == null) {
                Utils.logError("\"referenceNo\" parameter is null.");
                Utils.printJsonException(new JSONObject(), "Reference is required. See logs or try again.", out);
                return;
            } else if (referenceNo.isEmpty()) {
                Utils.logError("\"referenceNo\" parameter is empty.");
                Utils.printJsonException(new JSONObject(), "Reference is required. See logs or try again.", out);
                return;
            }

            if (joSignature == null) {
                Utils.logError("\"joSignature\" parameter is null.");
                Utils.printJsonException(new JSONObject(), "Signature is required. See logs or try again.", out);
                return;
            } else if (joSignature.isEmpty()) {
                Utils.logError("\"joSignature\" parameter is empty.");
                Utils.printJsonException(new JSONObject(), "Signature is required. See logs or try again.", out);
                return;
            }

            if (source == null) {
                Utils.logError("\"source\" parameter is null.");
                Utils.printJsonException(new JSONObject(), "Source is required. See logs or try again.", out);
                return;
            } else if (source.isEmpty()) {
                Utils.logError("\"source\" parameter is empty.");
                Utils.printJsonException(new JSONObject(), "Source is required. See logs or try again.", out);
                return;
            }

            if (purchaseOrder == null) {
                Utils.logError("\"purchaseOrder\" parameter is null.");
                Utils.printJsonException(new JSONObject(), "Purchase order is required. See logs or try again.",
                        out);
                return;
            } else if (purchaseOrder.isEmpty()) {
                Utils.logError("\"purchaseOrder\" parameter is empty.");
                Utils.printJsonException(new JSONObject(), "Purchase order is required. See logs or try again.",
                        out);
                return;
            }

            if (joNumber == null) {
                Utils.logError("\"joNumber\" parameter is null.");
                Utils.printJsonException(new JSONObject(), "JO Number is required. See logs or try again.", out);
                return;
            } else if (joNumber.isEmpty()) {
                Utils.logError("\"joNumber\" parameter is empty.");
                Utils.printJsonException(new JSONObject(), "JO Number is required. See logs or try again.", out);
                return;
            }

            if (photoPart == null) {
                Utils.logError("\"photoPart\" parameter is null.");
                Utils.printJsonException(new JSONObject(), "Photo is required. See logs or try again.", out);
            } else if (photoPart.getSize() < 1) {
                Utils.logError("\"photoPart\" parameter is empty.");
                Utils.printJsonException(new JSONObject(), "Photo is required. See logs or try again.", out);
            }
        } catch (IOException ie) {
            System.err.println(ie.toString());
        }
    }
}