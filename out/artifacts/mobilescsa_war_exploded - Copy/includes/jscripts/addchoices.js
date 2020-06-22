/**
 * Created by wmdcprog on 9/5/2017.
 */

var navOpen = false;
var navClose = true;

sendRequest('scanloggedinsession', 'post', { source : '5' }, function(o, s, response) {
    var assoc = Ext.decode(response.responseText);

    if (assoc['success']) {
        if (assoc['isAdmin']) {
            location.assign('csamanagement.jsp');
        }
    } else {
        location.assign('index.jsp');
    }
});

var topMostPanel = Ext.create('Ext.panel.Panel', {
    xtype : 'panel',
    region : 'center',
    layout : 'border',
    title : 'Select to add',
    titleAlign : 'center',
    listeners : {
        'render' : function(panel) {
            panel.body.on('click', function() {
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
                width : 25,
                height : 25,
                cls : ['my-field-cls'],
                id : 'menuId'
            },
            {
                xtype : 'image',
                src : 'includes/images/icons/backarrow.png',
                width : 25,
                height : 25,
                cls : ['my-field-cls'],
                id : 'back'
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
                    iconCls : 'customer',
                    iconAlign : 'top',
                    cls : 'x-button',
                    width : 140,
                    height : 140,
                    margin : '0 10 0 0',
                    html : '<span class="bigBtn">Customer</span>',
                    listeners : {
                        click : function() {
                            location.assign('addcustomerperson.jsp');
                        }
                    }
                },
                {
                    xtype : 'button',
                    iconCls : 'company',
                    iconAlign : 'top',
                    cls : 'x-button',
                    width : 140,
                    height : 140,
                    margin : '0 0 0 10',
                    html : '<span class="bigBtn">Company</span>',
                    listeners  : {
                        click : function() {
                            location.assign('addcustomercompany.jsp');
                        }
                    }
                }
            ]
        }
    ]
});

Ext.onReady(function() {
    Ext.QuickTips.init();

    Ext.create('Ext.container.Viewport', {
        layout : 'border',
        renderTo : Ext.getBody(),
        items : [topMostPanel]
    });

    Ext.get('back').on('touchstart', function(){
        location.assign('customer.jsp');
    });

    Ext.get('menuId').on('touchstart', function() {
        if (navClose) {
            openNav();
        } else {
            closeNav();
        }
    });

    window.addEventListener('orientationchange', function() {
        document.getElementById('mySidenav').style.paddingTop = '76px';
    });

    window.addEventListener('resize', function() {
        document.getElementById('mySidenav').style.paddingTop = '76px';
    });

    document.getElementById('mySidenav').style.paddingTop = '76px';
});