<%--
  Created by IntelliJ IDEA.
  User: wmdcprog
  Date: 8/13/2020
  Time: 1:10 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">

    <meta name=viewport content="width=device-width, initial-scale=1, maximum-scale=1">

    <title>New Company</title>

    <!-- extjs triton theme -->
    <link rel="stylesheet" href="includes/css/themes/theme-triton-all.css">

    <!-- mobilecsa stylesheet -->
    <link rel="stylesheet" href="includes/css/style.css">

    <!-- extjs script -->
    <script src="includes/jscripts/extjs6/ext-all.js"></script>

    <script src="includes/jscripts/jsfunctions.js"></script>

    <script src="includes/jscripts/dataconnection.js"></script>

    <script>
        var onlineUserFromAddCustomer = '${sessionScope.username}';
        var csaId = '${sessionScope.csaId}';
    </script>

    <script src="includes/jscripts/utils.js"></script>

    <script src="includes/jscripts/newcompany.js"></script>

    <script async defer
            src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCiyeaao7ku6pPuv_MBuJ8HwujLdJSHnYk&callback=initMap">
        function initMap() {
            console.log('google maps initialized.');
        }
    </script>

</head>
<body>

</body>
</html>
