
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

    items : [{
        xtype : 'panel',
        region : 'center',
        layout : {
            type : 'hbox',
            align : 'center',
            pack : 'center'
        },
        items : [{
            xtype : 'button',
            iconCls : 'customer',
            iconAlign : 'top',
            cls : 'x-button',
            width : 110,
            height : 110,
            margin : '0 5 0 0',
            html : '<span class="bigBtn">Customer</span>',
            listeners : {
                click : function() {
                    //location.assign('customer.jsp');
                }
            }
        },{
            xtype : 'button',
            iconCls : 'contacts',
            iconAlign : 'top',
            cls : 'x-button',
            width : 110,
            height : 110,
            margin : '0 0 0 5',
            html : '<span class="bigBtn">Contacts</span>',
            listeners  : {
                click : function() {
                    //location.assign('addcontacts.jsp');
                }
            }
        },{
            xtype : 'button',
            iconCls : 'contacts',
            iconAlign : 'top',
            cls : 'x-button',
            width : 110,
            height : 110,
            margin : '0 0 0 5',
            html : '<span class="bigBtn">TEST</span>',
            listeners  : {
                click : function() {
                    //location.assign('addcontacts.jsp');
                }
            }
        }]
    }]
});

Ext.onReady(function() {

    Ext.QuickTips.init();

    Ext.create('Ext.container.Viewport', {
        layout : 'fit',
        renderTo : Ext.getBody(),
        items : [
            Ext.create('Ext.container.Container', {
                layout: {
                    type: 'vbox',
                    align: 'center'
                },
                items: [{
                    xtype: 'container',
                    flex: 1,
                    layout: {
                        type: 'hbox',
                        //align: 'stretch'
                        align: 'center'
                    },
                    items: [{
                        xtype : 'button',
                        iconCls : 'customer',
                        iconAlign : 'top',
                        cls : 'x-button',
                        width : 140,
                        height : 140,
                        margin : '5 5 5 5',
                        html : '<span class="bigBtn">Customer</span>',
                        listeners : {
                            click : function() {
                                location.assign('customer.jsp');
                            }
                        }
                    },{
                        xtype : 'button',
                        iconCls : 'contacts',
                        iconAlign : 'top',
                        cls : 'x-button',
                        width : 140,
                        height : 140,
                        margin : '5 5 5 5',
                        html : '<span class="bigBtn">Contacts</span>',
                        listeners  : {
                            click : function() {
                                location.assign('addcontacts.jsp');
                            }
                        }
                    }]
                },{
                    xtype: 'container',
                    flex: 1,
                    layout: {
                        type: 'hbox',
                        //align: 'stretch'
                        align: 'center'
                    },
                    items: [{
                        xtype : 'button',
                        iconCls : 'customer',
                        iconAlign : 'top',
                        cls : 'x-button',
                        width : 140,
                        height : 140,
                        margin : '5 5 5 5',
                        html : '<span class="bigBtn">Customer</span>',
                        listeners : {
                            click : function() {
                            //    location.assign('customer.jsp');
                            }
                        }
                    },{
                        xtype : 'button',
                        iconCls : 'contacts',
                        iconAlign : 'top',
                        cls : 'x-button',
                        width : 140,
                        height : 140,
                        margin : '5 5 5 5',
                        html : '<span class="bigBtn">Contacts</span>',
                        listeners  : {
                            click : function() {
                             //   location.assign('addcontacts.jsp');
                            }
                        }
                    }]
                },{
                    xtype: 'container',
                    flex: 1,
                    layout: {
                        type: 'hbox',
                        //align: 'stretch'
                        align: 'center'
                    },
                    items: [{
                        xtype : 'button',
                        iconCls : 'customer',
                        iconAlign : 'top',
                        cls : 'x-button',
                        width : 140,
                        height : 140,
                        margin : '5 5 5 5',
                        html : '<span class="bigBtn">Customer</span>',
                        listeners : {
                            click : function() {
                             //   location.assign('customer.jsp');
                            }
                        }
                    },{
                        xtype : 'button',
                        iconCls : 'contacts',
                        iconAlign : 'top',
                        cls : 'x-button',
                        width : 140,
                        height : 140,
                        margin : '5 5 5 5',
                        html : '<span class="bigBtn">Contacts</span>',
                        listeners  : {
                            click : function() {
                             //   location.assign('addcontacts.jsp');
                            }
                        }
                    }]
                }]
            })
        ]
    });
});