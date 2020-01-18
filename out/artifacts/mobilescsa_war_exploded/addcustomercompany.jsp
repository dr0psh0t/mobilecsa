<!DOCTYPE html>
<html lang="en">
<head>

    <meta charset="UTF-8">

    <meta name=viewport content="width=device-width, initial-scale=1, maximum-scale=1">

    <title>Add Company</title>

    <!-- extjs triton theme -->
    <link rel="stylesheet" href="includes/css/themes/theme-triton-all.css">

    <!-- mobilecsa stylesheet -->
    <link rel="stylesheet" href="includes/css/style.css">

    <!-- extjs script -->
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

        /* the style for textfield text */
        .button-text {
            font-size: 30px;
        }

        .biggertext {
            font-size: 22px;
        }

        .labeltextsize {
            font-size: 20px;
        }

        .x-btn-inner {
            font-size : 20px;
            padding: 5px;
        }

        .x-panel-header-text-default {
            font-size : 25px !important;
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

    </style>

    <style>
        .x-form-label {

            display : block;
            color : red;
            font-weight: bold;
            font-size: 100%;
        }

        .wrapper {
            position: relative;
            /*width: 400px;  <-- make signature pad fit it's width */
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
        var onlineUserFromAddCompany = '${sessionScope.username}';
        var csaId = '${sessionScope.csaId}';
        var bodyWidth = window.innerWidth;
    </script>

    <!-- signature lib -->
    <script src="includes/jscripts/signaturepad.js"></script>

    <!-- google map -->
    <script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDYFvxckMZhyF9gTARyejjvT_rTKShiwG4&callback=initMap"></script>

    <!-- utility scripts --->
    <script src="includes/jscripts/jsfunctions.js"></script>

    <!-- data connection request -->
    <script src="includes/jscripts/dataconnection.js"></script>

    <!-- page script -->
    <script src="includes/jscripts/addcustomercompany.js"></script>

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