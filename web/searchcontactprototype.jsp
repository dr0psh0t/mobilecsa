<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>

    <meta charset="UTF-8">

    <meta name=viewport content="width=device-width, initial-scale=1, maximum-scale=1">

    <title>Search Contact</title>

    <!-- extjs triton theme -->
    <link rel="stylesheet" href="includes/css/themes/theme-triton-all.css">

    <!-- mobilecsa stylesheet -->
    <link rel="stylesheet" href="includes/css/style.css">

    <!-- extjs script -->
    <script src="includes/jscripts/extjs6/ext-all.js"></script>

    <!-- map key -->
    <script src="https://maps.google.com/maps/api/js?sensor=false&key=AIzaSyDYFvxckMZhyF9gTARyejjvT_rTKShiwG4"></script>

    <!-- internal style -->
    <style>
        .x-form-field {
            margin : 0 0 0 0;
            font : normal 15px Tahoma, arial, helvetica, sans-serif;
            color: #111111;
        }

        .labeltextsize {
            font-size: 17px;
        }

        /* the style for textfield text */
        .biggertext {
            font-size: 22px;
        }

        /* the style for textfields in login.jsp */
        x-form-field {
            margin: 0 0 0 0;
            font:normal 20px tahoma, arial, helvetica, sans-serif;
        }

        /* the style for buttons in login.jsp */
        .x-btn-inner {
            font-size : 20px;
            padding: 7px;
        }

        .x-panel-header-text-default {
            font-size : 25px !important;
        }

        .x-panel-header {
            padding : 15px;
            font-size : 20px;
        }

        .x-grid3-cell-inner {
            font-size: 15px;
        }

        /* This changes the CSS for all cells*/
        .x-grid-cell {
            /*color: #FF0000;*/
        }
        /* This changes the CSS for the Value Column*/
        .x-grid-cell-value {
            text-align: right;
            font-size: 18px;
            line-height: normal;
        }
        /* This changes the CSS for the Name Column*/
        .x-grid-cell-name {
            font-size: 18px;
            text-align: left;
        }

    </style>

    <script> var onlineCsaFromResultPage = '${sessionScope.username}'; </script>

    <script src="includes/jscripts/dataconnection.js"></script>

    <script src="includes/jscripts/searchcontactprototype.js"></script>

    <script src="includes/jscripts/jsfunctions.js"></script>

</head>
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