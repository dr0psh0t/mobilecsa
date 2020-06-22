/**
 * Created by wmdcprog on 6/14/2018.
 */

var navOpen = false;
var navClose = true;

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
});

var topMostPanel = Ext.create('Ext.panel.Panel', {

    region : 'center',
    layout : 'border',
    title : user,
    titleAlign : 'center',
    headerCls : 'x-panel-header',

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
                src : 'includes/images/icons/editicon.png',
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
                    xtype : 'panel',
                    region : 'center',

                    layout : {
                        type : 'vbox',
                        align : 'center',
                        pack : 'center'
                    },

                    items : [
                        {
                            xtype : 'button',
                            iconCls : 'customer',
                            iconAlign : 'top',
                            cls : 'x-button',
                            width : 160,
                            height : 140,
                            //margin : '0 10 0 0',
                            html : '<span class="bigBtn">Initial Joborder</span>',

                            listeners : {
                                click : function() {
                                    //location.assign('customer.jsp');
                                }
                            }
                        },
                        {
                            xtype : 'button',
                            iconCls : 'contacts',
                            iconAlign : 'top',
                            cls : 'x-button',
                            width : 160,
                            height : 140,
                            //margin : '0 0 0 10',
                            html : '<span class="bigBtn">Quotation List</span>',

                            listeners  : {
                                click : function() {
                                    //location.assign('addcontacts.jsp');
                                }
                            }
                        }
                    ]
                },
                {
                    xtype : 'panel',
                    region : 'center',

                    layout : {
                        type : 'vbox',
                        align : 'center',
                        pack : 'center'
                    },

                    items : [
                        {
                            xtype : 'button',
                            iconCls : 'customer',
                            iconAlign : 'top',
                            cls : 'x-button',
                            width : 160,
                            height : 140,
                            //margin : '0 10 0 0',
                            html : '<span class="bigBtn">Search Quotation</span>',

                            listeners : {
                                click : function() {
                                    //location.assign('customer.jsp');
                                }
                            }
                        },
                        {
                            xtype : 'button',
                            iconCls : 'customer',
                            iconAlign : 'top',
                            cls : 'x-button',
                            width : 160,
                            height : 140,
                            //margin : '0 10 0 0',
                            html : '<span class="bigBtn">Approval</span>',

                            listeners : {
                                click : function() {
                                    //location.assign('customer.jsp');
                                }
                            }
                        }
                    ]
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

    Ext.get('menuId').on('touchstart', function() {

        if (navClose) {
            openNav();
        }
        else {
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
