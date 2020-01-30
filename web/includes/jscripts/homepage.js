/**
 * Created by wmdcprog on 7/14/2018.
 */

var navOpen = false;
var navClose = true;

/*
sendRequest('scanloggedinsession', 'post', { source : '5' }, function(o, s, response) {

    var assoc = Ext.decode(response.responseText);

    if (assoc['success'])
    {
        if (assoc['isAdmin']) {
            location.assign('csamanagement.jsp');
        }
    }
    else {
        location.assign('index.jsp');
    }
});*/

var topMostPanel = Ext.create('Ext.panel.Panel', {

    region : 'center',
    layout : 'border',
    title : 'Home Page',
    titleAlign : 'center',
    headerCls : 'x-panel-header',

    listeners : {
        'render' : function (panel) {
            panel.body.on('click', function(){
                closeNav();
            });
        }
    },

    header : {
        titlePosition : 1,
        defaults : {
            xtype : 'tool'
        },

        items : [
            {
                xtype : 'image',
                src : 'includes/images/icons/menu_icon.png',
                id : 'menuId',
                width : 25,
                height : 25,
                cls : ['my-field-cls']
            }
        ]
    },

    items : [
        {
            xtype : 'panel',
            region : 'center',

            layout : {
                type : 'hbox',
                align : 'center',
                pack : 'center'
            },

            items : [
                {
                    xtype : 'button',
                    iconCls : 'crm',
                    iconAlign : 'top',
                    cls : 'x-button',
                    width: 140,
                    height : 140,
                    margin : '0 10 0 0',
                    html : '<span class="bigBtn">CRM</span>',

                    listeners : {
                        click : function() {
                            console.log('test');
                        }
                    }
                },
                {
                    xtype : 'button',
                    iconCls : 'joborder',
                    iconAlign : 'top',
                    cls : 'x-button',
                    width : 140,
                    height : 140,
                    margin : '0 0 0 10',
                    html : '<span class="bigBtn">Job Order</span>',

                    listeners : {
                        click : function() {
                            console.log('testing');
                        }
                    }
                }
            ]
        }
    ]
});

Ext.onReady(function(){
    Ext.QuickTips.init();

    Ext.create('Ext.container.Viewport', {
        layout : 'border',
        renderTo : Ext.getBody(),
        items : [topMostPanel]
    });

    Ext.get('menuId').on('touchstart', function() {
        if (navClose) {
            openNav();
        }
        else {
            closeNav();
        }
    });

    window.addEventListener('orientationchange', function(){
        document.getElementById('mySidenav').style.paddingTop = '76px';
    });

    window.addEventListener('resize', function(){
        document.getElementById('mySidenav').style.paddingTop = '76px';
    });

    document.getElementById('mySidenav').style.paddingTop = '76px';
});