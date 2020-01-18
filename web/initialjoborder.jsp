<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

    <meta charset="UTF-8">

    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">

    <title>Initial Joborder</title>

    <link rel="stylesheet" href="includes/css/themes/theme-triton-all.css">

    <link rel="stylesheet" href="includes/css/style.css">

    <script src="includes/jscripts/extjs6/ext-all.js"></script>

    <style>

        /* the style for buttons in login.jsp */

        .labeltextsize {
            font-size: 20px;
        }

        /* the style for textfield text */
        .biggertext {
            font-size: 22px;
        }

        .x-panel-header {
            padding : 15px;
            font-size : 20px;
        }

        .block-text {
            display: block;
        }

        .inline-text {
            display: inline;
        }

        .bold-text {
            font-weight: bold;
        }

        .x-form-label {

            display : block;
            color : red;
            font-weight : bold;
            font-size : 100%;
        }

        .wrapper {
            position: relative;
            /*width:400px;  <-- make signature pad fit it's width */
            height: 350px;
            -moz-user-select: none;
            -webkit-user-select: none;
            -ms-user-select: none;
            user-select: none;
        }
        img {
            position: absolute;
            left: 0;
            top: 0;
        }

        .signature-pad {
            position: absolute;
            left: 0;
            top: 0;
            /*width:400px;  <-- make signature pad fit it's width */
            height:225px;
        }
    </style>

    <script>
        var onlineCSAUser = '${sessionScope.username}';
        var csaId = '${sessionScope.csaId}'
    </script>

    <script src="includes/jscripts/signaturepad.js"></script>

    <!--
    <script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDYFvxckMZhyF9gTARyejjvT_rTKShiwG4&callback=initMap"></script>
    -->

    <script src="includes/jscripts/jsfunctions.js"></script>

    <script src="includes/jscripts/dataconnection.js"></script>

    <script src="includes/jscripts/initialjoborder.js"></script>

</head>
<!--<body onload="enableOnPageLoad()">-->
<body>

<div id="mySidenav" class="sidenav">
    <a href="#" onclick="gotoHome()">Home</a>
    <a href="#" onclick="searchChoices()">Search</a>
    <a href="#" onclick="addChoices()">Add customer</a>
    <a href="#" onclick="addContacts()">Add contact</a>
    <a href="#" onclick="gotoJoborder()">Job Order</a>
    <a href="#" onclick="help()">Help</a>
    <a href="#" onclick="logout()">Sign out</a>
</div>

</body>
</html>
