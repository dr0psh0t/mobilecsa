<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>

    <meta charset="UTF-8">

    <meta name=viewport content="width=device-width, initial-scale=1, maximum-scale=1">

    <title>Specimen</title>

    <!-- triton theme -->
    <link rel="stylesheet" href="includes/css/themes/theme-triton-all.css">

    <!-- mobilecsa stylesheet -->
    <link rel="stylesheet" href="includes/css/style.css">

    <!-- extjs lib -->
    <script src="includes/jscripts/extjs6/ext-all.js"></script>

    <style>

        /*  the css config of the combobox list */
        .x-boundlist-item {
            font: normal 25px 'Open Sans', 'Helvetica Neue', helvetica, arial, verdana, sans-serif;
            padding-top: 15px;
            padding-right: 10px;
            padding-bottom: 0px;
            padding-left: 15px;
        }

        /* the style for buttons in login.jsp */
        .x-btn-inner {
            font-size : 20px;
            padding: 7px;
        }

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
            height:350px;
        }
    </style>

    <script>
        var onlineCSAUser = '${sessionScope.username}';
        var csaId = '${sessionScope.csaId}';
    </script>

    <!-- signature pad library -->
    <script src="includes/jscripts/signaturepad.js"></script>

    <!-- google maps js api -->
    <script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDYFvxckMZhyF9gTARyejjvT_rTKShiwG4&callback=initMap"></script>

    <!-- utility functions -->
    <script src="includes/jscripts/jsfunctions.js"></script>

    <script src="includes/jscripts/dataconnection.js"></script>

    <!-- this page's script -->
    <script src="includes/jscripts/addcontacts.js"></script>

</head>
<body onload="enableOnPageLoad()">

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