<!--
This file is deprecated
-->

<!DOCTYPE html>
<html lang="en">
<head>

    <meta charset="UTF-8">

    <meta name=viewport content="width=device-width, initial-scale=1, maximum-scale=1">

    <title>Add Customer</title>

    <!-- extjs triton theme -->
    <link rel="stylesheet" href="includes/css/themes/theme-triton-all.css">

    <style>

        .photo-field {
            min-height: 43px;
        }

        /*
        .x-form-text-default {
            min-height: 43px;
        }*/

        /*  the css config of the combobox list */
        .x-boundlist-item {
            font: normal 25px 'Open Sans', 'Helvetica Neue', helvetica, arial, verdana, sans-serif;
            padding-top: 15px;
            padding-right: 10px;
            padding-bottom: 0px;
            padding-left: 15px;
        }

        /* the style for textfield text */
        .biggertext {
            font-size: 22px;
        }

        .labeltextsize {
            font-size: 20px;
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

        .block-text {
            display: block;
        }

        .bold-text {
            font-weight: bold;
        }
        
        .margin-up {
            margin-top: 20px;
        }
    </style>

    <!-- mobilecsa stylesheet -->
    <link rel="stylesheet" href="includes/css/style.css">

    <!-- extjs script -->
    <script src="includes/jscripts/extjs6/ext-all.js"></script>

    <style>
        .x-form-label {

            display : block;
            color : red;
            font-weight: bold;
            font-size: 100%;
            /*margin-left : 20px;*/
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
        var onlineUserFromAddCustomer = '${sessionScope.username}';
        var csaId = '${sessionScope.csaId}';
    </script>

    <!-- signature lib -->
    <script src="includes/jscripts/signaturepad.js"></script>

    <!-- google maps -->
    <script async defer
            src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCiyeaao7ku6pPuv_MBuJ8HwujLdJSHnYk&callback=initMap">
        function initMap() {
            console.log('google maps initialized.');
        }
    </script>

    <!-- utility scripts -->
    <script src="includes/jscripts/jsfunctions.js"></script>

    <!-- data connection -->
    <script src="includes/jscripts/dataconnection.js"></script>

    <!-- internal script -->
    <script src="includes/jscripts/addcustomerperson.js"></script>

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