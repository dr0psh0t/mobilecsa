/**
 * Created by wmdcprog on 3/11/2017.
 */

sendRequest('scanloggedinsession', 'post', { source: '8' }, function(o, s, response) {
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
    xtype: 'panel',
    region: 'center',
    layout: 'border',
    title: 'Customer',
    titleAlign: 'center',
    header: {
        titlePosition: 1,
        defaults: {
            xtype: 'tool'
        },
        items: [{
            xtype: 'image',
            src: 'includes/images/icons/menu_icon.png',
            width: 25,
            height: 25,
            cls: ['my-field-cls'],
            id: 'menuId'
        },{
            xtype: 'image',
            src: 'includes/images/icons/backarrow.png',
            width: 25,
            height: 25,
            cls: ['my-field-cls'],
            id: 'back'
        }]
    },
    items: [{
        xtype: 'panel',
        region: 'center',
        layout: {
            type: 'vbox',
            align: 'center',
            pack: 'center'
        },
        items: [{
            xtype: 'button',
            iconCls: 'customer',
            iconAlign: 'top',
            cls: 'x-button',
            width: 140,
            height: 140,
            margin: '5 5 5 5',
            html: '<span class="bigBtn">Individual</span>',
            listeners: {
                click: function() {
                    location.assign('newcustomer.jsp');
                }
            }
        },{
            xtype: 'button',
            iconCls: 'company',
            iconAlign: 'top',
            cls: 'x-button',
            width: 140,
            height: 140,
            margin: '5 5 5 5',
            html: '<span class="bigBtn">Company</span>',
            listeners: {
                click: function() {
                    location.assign('newcompany.jsp');
                }
            }
        },{
            xtype: 'button',
            iconCls: 'search',
            iconAlign: 'top',
            cls: 'x-button',
            width: 140,
            height: 140,
            margin: '5 5 5 5',
            html: '<span class="bigBtn">Search</span>',
            listeners: {
                click: function() {
                    location.assign('searchcustomer.jsp');
                }
            }
        }]
    }]
});

Ext.onReady(function() {
    Ext.QuickTips.init();

    Ext.create('Ext.container.Viewport', {
        layout: 'border',
        renderTo: Ext.getBody(),
        items: [topMostPanel]
    });

    Ext.get('back').on('touchstart', function(){
        location.assign('home.jsp');
    });

    Ext.get('menuId').on('touchstart', function() {
        location.assign('home.jsp');
    });
});