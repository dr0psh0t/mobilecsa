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

Ext.define('InitialJo', {
    extend: 'Ext.data.Model',
    fields: [
        { name: 'dateAdded', type: 'string' },
        { name: 'initialJoborderId', type: 'int' },
        { name: 'source', type: 'string' },
        { name: 'customer', type: 'string' },
        { name: 'serialNo', type: 'string' },
        { name: 'model', type: 'int' },
        { name: 'make', type: 'string' },
        { name: 'isAdded', type: 'int' },
        { name: 'joNumber', type: 'string' }
    ]
});

var initialJoStore = Ext.create('Ext.data.Store', {
    model: 'InitialJo',
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: 'getinitialjoborderlist',
        method: 'post',
        //  actionMethods config for post request
        actionMethods: {
            create: 'post',
            read: 'post',
            update: 'post',
            destroy: 'post'
        },
        reader: {
            type: 'json',
            rootProperty: 'initialJoborderList'
        }
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
                title: 'Initial JO List',
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
                    items: [Ext.create('Ext.grid.Panel', {
                        title: 'Initial JO List',
                        store: initialJoStore,
                        frame: true,
                    })]
                }]
            })
        ]
    });

    Ext.get('back').on('touchstart', function(){
        location.assign('johome.jsp');
    });

    Ext.get('menuId').on('touchstart', function() {
        location.assign('home.jsp');
    });
});