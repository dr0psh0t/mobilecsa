<%--
  Created by IntelliJ IDEA.
  User: wmdcprog
  Date: 8/17/2018
  Time: 10:11 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

    <meta charset="UTF-8">

    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">

    <title>combo remote</title>

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

    var csaId = '${sessionScope.csaId}';

    Ext.define('Country', {
        extend : 'Ext.data.Model',
        fields : [
            {name : 'country', type : 'string'},
            {name : 'countryId', type : 'int'}
        ]
    });

    var countryStore = Ext.create('Ext.data.Store', {

        model : 'Country',
        autoLoad : false,

        proxy : {

            type : 'ajax',
            url : 'comboremote',
            method : 'post',

            extraParams : {
                cid : csaId
            },

            actionMethods : {
                create : 'post',
                read : 'post',
                update : 'post',
                destroy : 'post'
            },

            reader : {
                type : 'json',
                root : 'countryList'
            }
        }
    });

    var myForm = Ext.create('Ext.form.Panel', {

        region : 'center',
        title : 'Initial Joborder',
        titleAlign : 'center',
        id : 'initialJoForm',
        autoScroll : true,

        defaults : {
            allowBlank : false
        },

        items : [
            {
                xtype : 'combobox',
                margin : '20 0 0 20',
                displayField : 'country',
                valueField : 'countryId',
                width : 400,
                store : countryStore,
                queryMode : 'remote',
                typeAhead : true,
                minChars : 2
            }
        ]
    });

    Ext.onReady(function(){

        Ext.QuickTips.init();

        Ext.create('Ext.container.Viewport', {
            layout : 'border',
            renderTo : Ext.getBody(),
            id : 'viewportJoborder',
            items : [myForm]
        });
    });

</script>

</head>
<body>

</body>
</html>
