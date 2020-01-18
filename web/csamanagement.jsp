<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>

    <meta charset="UTF-8">
    <meta name=viewport content="width=device-width, initial-scale=1, maximum-scale=1">

    <title>CSA Management</title>

    <link rel="stylesheet" href="includes/css/themes/theme-triton-all.css">

    <script src="includes/jscripts/extjs6/ext-all.js"></script>

    <style>

        .my-field-cls:hover, .my-field-cls label:hover {
            cursor: pointer;
        }

        .white-label {
            color : white;
        }
        
        .jo-icon {
            background-image: url('includes/images/icons/jo_icon.png');
        }

        .add-icon {
            background-image : url('includes/images/icons/note_add_black_18dp.png');
        }

        .edit-icon {
            background-image : url('includes/images/icons/editpencil.png');
        }

        .refresh-icon {
            background-image : url('includes/images/icons/refresh.png');
        }

        .transfer-icon {
            background-image: url('includes/images/icons/trans-arrow.png');
        }

        .changepass-icon {
            background-image : url('includes/images/icons/changepass.png');
        }

        .lock-icon {
            background-image : url('includes/images/icons/lockicon.png');
        }

        .contact-icon {
            background-image: url("includes/images/icons/contacts_icon.png");
        }

        .customer-icon {
            background-image: url("includes/images/icons/customer_icon.png");
        }

        .company-icon {
            background-image: url("includes/images/icons/domain_icon.png");
        }

        .settings-icon {
            background-image : url('includes/images/icons/settings_icon.png');
        }

        .admin-icon {
            background-image : url('includes/images/icons/admin_icon.png');
        }

        .active {
            background:url(includes/images/icons/check2.png) 15px no-repeat !important;
        }

        .locked {
            background:url(includes/images/icons/cross.png) 15px no-repeat !important;
        }

        .delete {
            background-image : url('includes/images/icons/delete_black_icon_18dp.png');
        }
        
        .small-search {
            background-image: url('includes/images/icons/smallsearch.png');
        }

        .grid-column-align-center {
            text-align: center;
        }

    </style>

    <script>
        pressed = false;
        const onlineUserFromAdmin = '${sessionScope.username}'; //  to be used by csamanagement.js
    </script>

    <script src="includes/jscripts/dataconnection.js"></script>

    <script src="includes/jscripts/signaturepad.js"></script>

    <script src="includes/jscripts/editcustomerperson.js"></script>

    <script src="includes/jscripts/editcustomercompany.js"></script>

    <script src="includes/jscripts/editcontact.js"></script>

    <script src="includes/jscripts/jsfunctions.js"></script>

    <script src="includes/jscripts/csamanagement.js"></script>

    <!--
    <script async defer
            src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDYFvxckMZhyF9gTARyejjvT_rTKShiwG4&callback=initMap">
        function initMap() {
            console.log('google maps initialized.');
        }
    </script>
    -->

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