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

Ext.define('Info', {
    extend: 'Ext.data.Model',
    fields: [
        { name: 'wordQuery', type: 'string' },
        { name: 'searchCount', type: 'int' },
        { name: 'widthAuthority', type: 'boolean' }
    ]
});

var infoStore = Ext.create('Ext.data.Store', {
    model: 'Info',
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: 'searchcontactfromuser',
        method: 'get',
        extraParams: { query: '-123' },
        reader: {
            type: 'json',
            rootProperty: 'info'
        }
    }
});

Ext.define('Result', {
    extend: 'Ext.data.Model',
    fields: [
        { name: 'firstname', type: 'string' },
        { name: 'lastname', type: 'string' },
        { name: 'signaturesource', type: 'string' },
        { name: 'src', type: 'string' },
        { name: 'isAPerson', type: 'boolean' },
        { name: 'label', type: 'string'}
    ]
});

var resultStore = Ext.create('Ext.data.Store', {
    model: 'Result',
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: 'searchcontactfromuser',
        method: 'get',
        extraParams: { query: '-123' },
        reader: {
            type: 'json',
            rootProperty: 'result'
        }
    }
});

var wordQuery = '';
var searchCount = 0;
var withAuthorityToView = false;

function searchContact(query) {
    resultStore.load({
        url: 'searchcontactfromuser',
        method: 'get',
        params: { query: query }
    });

    infoStore.load({
        url: 'searchcontactfromuser',
        method: 'get',
        params: { query: query }
    });

    infoStore.on('load', function(store, records, successful) {

        wordQuery = records[0].data.wordQuery;
        searchCount = records[0].data.searchCount;
        withAuthorityToView = records[0].data.withAuthority
    });

    Ext.getCmp('containerPanel').setActiveItem(1);
    topMostPanel.setTitle(query);

    setTimeout(function() {
        Ext.getCmp('countLabel').setText('About ' + resultStore.totalCount + ' results');
    }, 400);
}

//var resultGrid = ;

//var searchForm = ;

//var topMostPanel = ;

Ext.onReady(function() {
    Ext.QuickTips.init();

    Ext.create('Ext.container.Viewport', {
        layout: 'border',
        id: 'viewportPage',
        renderTo: Ext.getBody(),
        items: [Ext.create('Ext.panel.Panel', {
            region: 'center',
            layout: 'border',
            title: 'Search',
            titleAlign: 'center',
            itemId: 'parentPanel',
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
            items: [
                Ext.create('Ext.Panel', {
                    layout: 'card',
                    region: 'center',
                    id: 'containerPanel',
                    width: window.innerWidth,
                    height: window.innerHeight,
                    items: [Ext.create('Ext.form.Panel', {
                        region: 'center',
                        id: 'searchForm',
                        layout: {
                            type: 'vbox',
                            pack: 'center',
                            align: 'middle'
                        },
                        items: [{
                            xtype: 'textfield',
                            emptyText: 'Search Contact',
                            id: 'query',
                            name: 'query',
                            width: '60%',
                            fieldCls: 'biggertext',
                            enableKeyEvents: true,
                            triggers: {
                                clears: {
                                    cls: 'x-form-clear-trigger',
                                    handler: function() {
                                        this.setValue('');
                                    }
                                }
                            },
                            listeners: {
                                keypress: function (textfield, eo) {
                                    if (eo.getCharCode() === Ext.EventObject.ENTER) {
                                        var query = Ext.getCmp('query').getValue();

                                        if (query.length > 1) {
                                            searchContact(query);
                                        } else {
                                            Ext.Msg.alert('Warning', 'Must be atleast 3 characters in length to search');
                                        }
                                    }
                                }
                            }
                        },{
                            xtype: 'button',
                            text: 'Search',
                            width: '30%',
                            handler: function() {
                                var query = Ext.getCmp('query').getValue();

                                if (query.length > 1) {
                                    searchContact(query);
                                } else {
                                    Ext.Msg.alert('Warning', 'Must be at least 3 characters in length to search');
                                }
                            }
                        }]
                    }),
                    Ext.create('Ext.grid.Panel', {
                        store: resultStore,
                        height: '100%',
                        cls: 'x-grid3-cell-inner',
                        listeners: {
                            beforeitemdblclick: function(selModel, record, index, options) {
                                if (record.data.addedby === onlineCsaFromResultPage) {
                                    displayContacts(record.data.contactId, true);
                                } else {
                                    Ext.Msg.alert('Warning', 'This is not your contact.');
                                }
                            }
                        },
                        dockedItems: [{
                            xtype: 'toolbar',
                            dock: 'bottom',
                            items: [{
                                xtype: 'label',
                                id: 'countLabel',
                                text: ''
                            },'->',{
                                xtype: 'button',
                                text: 'Search Again',
                                handler: function () {
                                    Ext.getCmp('containerPanel').setActiveItem(0);
                                    topMostPanel.setTitle('Search Contact');
                                }
                            }]
                        }],
                        columns: [{
                            dataIndex: 'src',
                            flex: 1,
                            renderer: function(value, meta, record) {
                                return '<img src="' + value + '" height="100" width="100" style="float:left">' +
                                    '<span style="font-size: 20px">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp' + record.data.label + '</span>' +
                                    '<br><br><br><br>' +
                                    '<div style="font-size: 20px">&nbsp;&nbsp;&nbsp;&nbsp;&nbspAdded by: ' + record.data.addedby + '</div>';
                            }
                        }]
                    })]
                })
            ]
        })]
    });

    Ext.get('back').on('touchstart', function(){
        location.assign('contacts.jsp');
    });

    Ext.get('menuId').on('touchstart', function() {
        location.assign('home.jsp');
    });
});