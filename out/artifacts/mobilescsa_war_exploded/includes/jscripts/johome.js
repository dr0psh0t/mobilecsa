/**
 * Created by wmdcprog on 3/11/2017.
 */

sendRequest('scanloggedinsession', 'post', { source: '5' }, function(o, s, response) {
    var assoc = Ext.decode(response.responseText);

    if (assoc['success']) {
        if (assoc['isAdmin']) {
            location.assign('csamanagement.jsp');
        }
    } else {
        location.assign('index.jsp');
    }
});

Ext.onReady(function() {
    Ext.QuickTips.init();

    Ext.create('Ext.container.Viewport', {
        layout: 'fit',
        renderTo: Ext.getBody(),
        items: [
            Ext.create('Ext.panel.Panel', {
                xtype: 'panel',
                region: 'center',
                layout: 'border',
                title: 'JO Home',
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
                        html: '<span class="bigBtn">Initial JO</span>',
                        listeners: {
                            click: function() {
                                location.assign('initialjoborder.jsp');
                            }
                        }
                    },{
                        xtype: 'button',
                        iconCls: 'contacts',
                        iconAlign: 'top',
                        cls: 'x-button',
                        width: 140,
                        height: 140,
                        margin: '5 5 5 5',
                        html: '<span class="bigBtn">Initial JO List</span>',
                        listeners: {
                            click: function() {
                                location.assign('initialjolist.jsp');
                            }
                        }
                    }]
                }]
            })
        ]
    });

    Ext.get('back').on('touchstart', function(){
        location.assign('home.jsp');
    });

    Ext.get('menuId').on('touchstart', function() {
        location.assign('home.jsp');
    });
});