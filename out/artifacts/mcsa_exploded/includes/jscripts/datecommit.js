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

Ext.define('DateCommit', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'joId', type: 'int'},
        {name: 'joNum', type: 'string'},
        {name: 'customerId', type: 'string'},
        {name: 'customer', type: 'string'},
        {name: 'isCsaApproved', type: 'boolean'},
        {name: 'isPnmApproved', type: 'boolean'},
        {name: 'dateCommit', type: 'string'},
        {name: 'dateReceive', type: 'string'}
    ]
});

var dateCommitStore = Ext.create('Ext.data.Store', {
    model: 'DateCommit',
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: 'getdclist',
        reader: {
            type: 'json',
            rootProperty: 'joborders'
        },
        actionMethods: {
            create: 'post',
            read: 'post',
            update: 'post',
            destroy: 'post'
        },
        extraParams: {
            cid: cId,
            source: 'mcsa'
        }
    }
});

var dateCommitGrid = Ext.create('Ext.grid.Panel', {
    title: 'Date Commit',
    store: dateCommitStore,
    id: 'dcGrid',
    frame: true,
    height: '100%',
    listeners: {
        itemclick: function (selModel, record) {
            var data = record.data;

            if (data === null) {
                console.log('record is null.');
            } else {
                if (!data.isCsaApproved) {

                    Ext.Msg.show({
                        title: 'Approve',
                        msg: data.joNum+' - '+data.customer+'?',
                        buttons: Ext.Msg.YESNO,
                        callback: function(btn) {

                            if (btn === 'yes') {
                                Ext.MessageBox.show({
                                    msg: 'Date Commit',
                                    progressText: 'Approving Joborder...',
                                    width: 300,
                                    wait: true,
                                    waitConfig: {
                                        duration: 60000,
                                        text: 'Approving Joborder...',
                                        scope: this,
                                        fn: function() {
                                            Ext.MessageBox.hide();
                                        }
                                    }
                                });

                                sendRequest('approvemcsadatecommit', 'post',
                                    {joid: data.joId, cid: cId, source: 'mcsa'}, function(o, s, response) {

                                    console.log(response.responseText);
                                    var assoc = Ext.decode(response.responseText);
                                    Ext.MessageBox.hide();

                                    if (assoc['success']) {
                                        dateCommitStore.load({url: 'getdclist'});
                                        Ext.Msg.alert('Date Commit', 'Joborder approved.');
                                    } else {
                                        Ext.Msg.alert('Fail', assoc['reason']);
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }
    },
    columns: [{
        text: '<b>JO #</b>',
        dataIndex: 'joNum',
        flex: 0.60
    },{
        text: '<b>Customer</b>',
        dataIndex: 'customer',
        flex: 1.8
    },{
        text: '<b>CSA</b>',
        dataIndex: 'isCsaApproved',
        flex: 0.55,
        renderer: function(value) {
            return (value === true) ? '<img style="width: 20px;"src="includes/images/icons/check2.png" />'
                : '<img style="width: 20px;"src="includes/images/icons/cross.png" />';
        }
    },{
        text: '<b>PnM</b>',
        dataIndex: 'isPnmApproved',
        flex: 0.55,
        renderer: function(value) {
            return (value === true) ? '<img style="width: 20px;"src="includes/images/icons/check2.png" />'
                : '<img style="width: 20px;"src="includes/images/icons/cross.png" />';
        }
    }],
    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',
        items: [{
            xtype: 'textfield',
            fieldLabel: '<b>Search</b>',
            id: 'searchFieldDC',
            width: '50%',
            labelWidth: 50,
            enableKeyEvents: true,
            triggers: {
                clears: {
                    cls: 'x-form-clear-trigger',
                    handler: function() {
                        if (Ext.getCmp('searchFieldDC').getValue() !== '') {
                            this.setValue('');
                            dateCommitGrid.setStore(dateCommitStore);
                        }
                    }
                }
            },
            listeners: {
                keypress: function(textfield, eo) {
                    if (eo.getCharCode() === Ext.EventObject.ENTER) {
                        search();
                    }
                }
            }
        },{
            xtype: 'button',
            text: '<b>Search</b>',
            iconCls: 'search-icon',
            handler: function() {
                search();
            }
        }]
    },{
        xtype: 'toolbar',
        dock: 'bottom',
        buttonAlign: 'right',
        items: [{
            xtype: 'button',
            text: '<b>Refresh</b>',
            iconCls: 'refresh2',
            handler: function() {
                dateCommitStore.load({url: 'getdclist'});
            }
        },{
            xtype: 'button',
            text: '<b>Back</b>',
            iconCls: 'rewind',
            handler: function() {
                location.assign('johome.jsp')
            }
        }]
    }]
});

function search() {
    var query = Ext.getCmp('searchFieldDC').getValue();

    if (query.length > 1) {

        //  alert(query);
        //  var store = dateCommitGrid.getStore();
        //  array
        //  var dcItems = store.data.items;

        var rowNumber = dateCommitGrid.getStore().find('joNum', query);

        //console.log(rowNumber);
        //dateCommitGrid.getView().focusRow(rowNumber);
        //dateCommitGrid.getSelectionModel().select(rowNumber);

        if (rowNumber < 0) {
            Ext.Msg.alert(query, 'No joborder found.');
        } else {
            var dcItems = dateCommitGrid.getStore().data.items;

            //console.log(dcItems[rowNumber].data);

            dateCommitGrid.setStore(Ext.create('Ext.data.Store', {
                model: 'DateCommit',
                data: [{
                    'joId': dcItems[rowNumber].data.joId,
                    'joNum': dcItems[rowNumber].data.joNum,
                    'customerId': dcItems[rowNumber].data.customerId,
                    'customer': dcItems[rowNumber].data.customer,
                    'isCsaApproved': dcItems[rowNumber].data.isCsaApproved,
                    'isPnmApproved': dcItems[rowNumber].data.isPnmApproved,
                    'dateCommit': dcItems[rowNumber].data.dateCommit,
                    'dateReceive': dcItems[rowNumber].data.dateReceive
                }]
            }));

            dateCommitGrid.getSelectionModel().select(0);
        }
    }
}

Ext.onReady(function() {
    Ext.QuickTips.init();

    Ext.create('Ext.container.Viewport', {
        layout: 'fit',
        renderTo: Ext.getBody(),
        items: [dateCommitGrid]
    });
});