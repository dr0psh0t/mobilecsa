<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

    <meta name=viewport content="width=device-width, initial-scale=1">

    <title>Create Password</title>

    <meta charset="utf-8">

    <link href="includes/css/themes/theme-triton-all.css" rel="stylesheet">

    <style>

        /* the style for textfield text */
        .biggertext {
            font-size: 25px;
        }

        /* the style for textfields in login.jsp */
        x-form-field {
            margin: 0 0 0 0;
            font:normal 20px tahoma, arial, helvetica, sans-serif;
        }

        /* the style for buttons in login.jsp */
        .x-btn-inner {
            font-size : 25px;
            padding: 5px;
        }

        .x-panel-header-text-default {
            font-size : 25px !important;
        }

        .x-panel-header {
            padding : 20px;
            font-size : 25px;
        }

        .labeltextsize {
            font-size: 20px;
        }

        .block-text {
            display: block;
        }

        .bold-text {
            font-weight: bold;
        }

        .alignment {
            text-align: left;
        }

    </style>

    <script src="includes/jscripts/extjs6/ext-all.js"></script>

    <script> var interimUsername = '${sessionScope.interimUsername}'; </script>

    <script src="includes/jscripts/createpassword.js"></script>

</head>
<body>

</body>
</html>