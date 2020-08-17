<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
 <html lang="en">
<head>

    <meta charset="UTF-8">

    <meta name=viewport content="width=device-width, initial-scale=1, maximum-scale=1">

    <title>Search Customer</title>

    <!-- extjs triton theme -->
    <link rel="stylesheet" href="includes/css/themes/theme-triton-all.css">

    <!-- mobilecsa stylesheet -->
    <link rel="stylesheet" href="includes/css/style.css">

    <!-- extjs script -->
    <script src="includes/jscripts/extjs6/ext-all.js"></script>

    <script async defer
            src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCiyeaao7ku6pPuv_MBuJ8HwujLdJSHnYk&callback=initMap">
        function initMap() {
            console.log('google maps initialized.');
        }
    </script>

    <script>
        var onlineCsaFromResultPage = '${sessionScope.username}';
        var salesman = '${sessionScope.username}';
    </script>

    <script src="includes/jscripts/dataconnection.js"></script>

    <script src="includes/jscripts/jsfunctions.js"></script>

    <script src="includes/jscripts/searchcustomer.js"></script>

</head>
<body>

</body>
</html>