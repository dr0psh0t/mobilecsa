<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

    <meta charset="UTF-*">

    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">

    <title>Home Page</title>

    <link rel="stylesheet" href="includes/css/themes/theme-triton-all.css">

    <link rel="stylesheet" href="includes/css/style.css">

    <style>
        .x-panel-header {
            padding : 15px;
            font-size : 20px;
        }

        .icon-menu {
            background-image: url("includes/images/icons/editicon.png");
        }
    </style>

    <script src="includes/jscripts/extjs6/ext-all.js"></script>

    <script> var user = '${sessionScope.username}'</script>

    <script src="includes/jscripts/dataconnection.js"></script>

    <script src="includes/jscripts/jsfunctions.js"></script>

    <script src="includes/jscripts/homepage.js"></script>

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
