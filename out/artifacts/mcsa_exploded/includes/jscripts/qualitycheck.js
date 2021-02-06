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

var pageCounter = 1;

Ext.define('QualityCheck', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'joId', type: 'int'},
        {name: 'serialNum', type: 'string'},
        {name: 'dateCommit', type: 'string'},
        {name: 'joNum', type: 'string'},
        {name: 'customerId', type: 'string'},
        {name: 'model', type: 'string'},
        {name: 'category', type: 'string'},
        {name: 'make', type: 'string'},
        {name: 'customer', type: 'string'},
        {name: 'isPending', type: 'boolean'}
    ]
});

var qcStore = Ext.create('Ext.data.Store', {
    model: 'QualityCheck',
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: 'getqclist',
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
            source: 'mcsa',
            page: pageCounter+''
        }
    }
});

var qcGrid = Ext.create('Ext.grid.Panel', {
    title: 'Quality Check',
    store: qcStore,
    id: 'qcGrid',
    frame: true,
    height: '100%',
    listeners: {
        itemclick: function (selMode, record) {
            var data = record.data;

            if (data === null) {
                alert('Error occured');
            } else {

                /*
                sendRequest('getworkorderqclist', 'post', {joid: data.joId, cid: cId, source: 'mcsa'},
                    function(o, s, response) {
                        var woAssoc = Ext.decode(response.responseText);
                        Ext.MessageBox.hide();

                        if (woAssoc['success']) {
                            displayWoWindow(data, Ext.create('Ext.data.Store', {
                                model: 'WorkOrder',
                                data: woAssoc['workOrders']
                            }));
                        } else {
                            Ext.Msg.alert('Fail', woAssoc['reason']);
                        }
                    });*/

                displayWoWindow(data);
            }
        }
    },
    columns: [{
        text: '<b>JO #</b>',
        dataIndex: 'joNum',
        flex: 1
    },{
        text: '<b>Cust ID</b>',
        dataIndex: 'customerId',
        flex: 1
    },{
        text: '<b>Customer</b>',
        dataIndex: 'customer',
        flex: 2
    }],
    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',
        items: [{
            xtype: 'textfield',
            fieldLabel: '<b>Search</b>',
            id: 'searchFieldQc',
            width: '50%',
            labelWidth: 50,
            enableKeyEvents: true,
            triggers: {
                clears: {
                    cls: 'x-form-clear-trigger',
                    handler: function() {
                        if (Ext.getCmp('searchFieldQc').getValue() !== '') {
                            this.setValue('');
                            qcGrid.setStore(qcStore);
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
            iconCls: 'refresh-icon',
            handler: function() {
                search();
            }
        },'-',{
            xtype: 'label',
            id: 'pageId'
        }]
    },{
        xtype: 'toolbar',
        dock: 'bottom',
        //buttonAlign: 'right',
        items: [{
            xtype: 'button',
            text: '<b>Refresh</b>',
            iconCls: 'refresh-icon',
            handler: function() {
                qcGrid.setStore(qcStore);
            }
        },{
            xtype: 'button',
            text: '<b>Back</b>',
            iconCls: 'refresh-icon',
            handler: function() {
                location.assign('johome.jsp')
            }
        },'-',{
            xtype: 'button',
            text: '<b>Prev</b>',
            iconCls: 'refresh-icon',
            handler: function() {
                if (pageCounter > 1) {
                    --pageCounter;
                    Ext.getCmp('pageId').setText(pageCounter);

                    qcStore.load({
                        url: 'getqclist',
                        params: {
                            cid: cId,
                            source: 'mcsa',
                            page: pageCounter + ''
                        }
                    });
                }
            }
        },{
            xtype: 'button',
            text: '<b>Next</b>',
            iconCls: 'refresh-icon',
            handler: function() {
                ++pageCounter;
                Ext.getCmp('pageId').setText(pageCounter);

                qcStore.load({
                    url: 'getqclist',
                    params: {
                        cid: cId,
                        source: 'mcsa',
                        page: pageCounter+''
                    }
                });
            }
        }]
    }]
});

function search() {
    var query = Ext.getCmp('searchFieldQc').getValue();

    if (query.length > 1) {
        var rowNumber = qcGrid.getStore().find('joNum', query);

        if (rowNumber < 0) {
            Ext.Msg.alert(query, 'No joborder found.');
        } else {
            var qcItems = qcGrid.getStore().data.items;

            qcGrid.setStore(Ext.create('Ext.data.Store', {
                model: 'QualityCheck',
                data: [{
                    'joId': qcItems[rowNumber].data.joId,
                    'serialNum': qcItems[rowNumber].data.serialNum,
                    'dateCommit': qcItems[rowNumber].data.dateCommit,
                    'joNum': qcItems[rowNumber].data.joNum,
                    'customerId': qcItems[rowNumber].data.customerId,
                    'model': qcItems[rowNumber].data.model,
                    'category': qcItems[rowNumber].data.category,
                    'make': qcItems[rowNumber].data.make,
                    'customer': qcItems[rowNumber].data.customer,
                    'isPending': qcItems[rowNumber].data.isPending
                }]
            }));

            qcGrid.getSelectionModel().select(0);
        }
    }
}

Ext.define('WorkOrder', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'scopeOfWork', type: 'string'},
        {name: 'isSupervisorId', type: 'boolean'},
        {name: 'isCsaQc', type: 'boolean'},
        {name: 'workOrderId', type: 'int'},
        {name: 'joId', type: 'int'},
        {name: 'isCompleted', type: 'int'}
    ]
});

function initQc(param) {

    Ext.create('Ext.Window', {
        id: 'initQcWindow',
        title: 'Quality Check',
        width: 450,
        height: 180,
        minWidth: 450,
        minHeight: 180,
        layout: 'fit',
        plain: true,
        modal: true,
        items: [
            Ext.create('Ext.form.Panel', {
                id: 'qcForm',
                region: 'center',
                bodyStyle: 'padding: 5px',
                width: 450,
                height: 180,
                items: [{
                    xtype: 'filefield',
                    name: 'wophoto',
                    id: 'photo',
                    msgTarget: 'side',
                    anchor: '100%',
                    margin: '10',
                    buttonText: 'Select',
                    listeners: {
                        afterrender: function() {
                            this.fileInputEl.set({
                                accept: 'image/*'
                            });
                        },
                        change: function () {
                            var form = this.up('form');
                            var file = form.getEl().down('input[type=file]').dom.files[0];
                            var _URL = window.URL || window.webkitURL;
                            var img = new Image();

                            img.onerror = function() {
                                Ext.Msg.alert('Warning', 'Chosen file is not an image.');
                                Ext.getCmp('photo').inputEl.com.value = '';
                            };

                            img.src = _URL.createObjectURL(file);

                            var fileSize = file.size;

                            if (file.type !== 'image/jpeg') {
                                Ext.Msg.alert('Warning', 'Photo should be jpeg');
                                Ext.getCmp('photo').inputEl.dom.value = '';
                            }

                            if (fileSize > 512000) {
                                Ext.Msg.alert('Warning', "Photo should be under 512 KB.");
                                Ext.getCmp('photo').inputEl.dom.value = '';
                            }
                        }
                    }
                }],
                buttons: [{
                    text: 'Submit',
                    disabled: true,
                    formBind: true,
                    handler: function() {
                        var form = this.up('form').getForm();

                        if (form.isValid()) {
                            form.submit({
                                waitMsg: 'Quality Checking...',
                                url: 'approvemcsaqc',
                                method: 'post',
                                params: param,
                                success: function() {
                                    woStore.reload();

                                    Ext.getCmp('qcForm').destroy();
                                    Ext.getCmp('initQcWindow').destroy();

                                    Ext.Msg.alert('Success', 'Quality Check success');
                                },
                                failure: function(form, action) {
                                    var assoc = Ext.decode(action.response.responseText);
                                    Ext.Msg.alert('Fail', assoc['reason']);
                                }
                            });
                        }
                    }
                },{
                    text: 'Cancel',
                    handler: function() {
                        Ext.getCmp('qcForm').destroy();
                        Ext.getCmp('initQcWindow').destroy();
                    }
                }]
            })
        ]
    }).show();
}

Ext.define('Workorder', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'isCompleted', type: 'int'},
        {name: 'isCsaQc', type: 'boolean'},
        {name: 'isSupervisorId', type: 'boolean'},
        {name: 'scopeOfWork', type: 'string'},
        {name: 'woId', type: 'int'}
    ]
});

var woStore;

function displayWoWindow(dataItems) {

    woStore = Ext.create('Ext.data.Store', {
        model: 'Workorder',
        autoLoad: true,
        proxy: {
            type: 'ajax',
            url: 'getworkorderqclist',
            reader: {
                type: 'json',
                rootProperty: 'workOrders'
            },
            actionMethods: {
                create: 'post',
                read: 'post',
                update: 'post',
                destroy: 'post'
            },
            extraParams: {
                joid: dataItems.joId,
                cid: cId,
                source: 'mcsa'
            }
        }
    });

    Ext.create('Ext.window.Window', {
        id: 'woWindow',
        title: dataItems.joNum+' workorders',
        width: Ext.getBody().getViewSize().width - 5,
        height: Ext.getBody().getViewSize().height * 0.85,
        layout: 'fit',
        items: [
            Ext.create('Ext.grid.Panel', {
                store: woStore,
                id: 'woGrid',
                height: '100%',
                listeners: {
                    itemclick: function (selMode, record) {
                        var data = record.data;

                        if (data.isCompleted === 1) {
                            if (!data.isCsaQc) {
                                initQc({
                                    'joid': dataItems.joId,
                                    'cid': cId,
                                    'source': 'mcsa',
                                    'woid': data.woId
                                });
                            }
                        }
                    }
                },
                columns: [{
                    text: '<b>Scope</b>',
                    dataIndex: 'scopeOfWork',
                    flex: 1
                },{
                    text: '<b>Done</b>',
                    dataIndex: 'isCompleted',
                    flex: 0.30,
                    renderer: function(value) {
                        return (value === 1) ? '<img style="width: 20px;"src="includes/images/icons/check2.png" />'
                            : '<img style="width: 20px;"src="includes/images/icons/cross.png" />';
                    }
                },{
                    text: '<b>CSA</b>',
                    dataIndex: 'isCsaQc',
                    flex: 0.30,
                    renderer: function(value) {
                        return (value === true) ? '<img style="width: 20px;"src="includes/images/icons/check2.png" />'
                            : '<img style="width: 20px;"src="includes/images/icons/cross.png" />';
                    }
                },{
                    text: '<b>P&M</b>',
                    dataIndex: 'isSupervisorId',
                    flex: 0.30,
                    renderer: function(value) {
                        return (value === true) ? '<img style="width: 20px;"src="includes/images/icons/check2.png" />'
                            : '<img style="width: 20px;"src="includes/images/icons/cross.png" />';
                    }
                }]
            })
        ],
        buttons: [{
            xtype: 'button',
            text: 'Close',
            handler: function() {
                Ext.getCmp('woGrid').destroy();
                Ext.getCmp('woWindow').destroy();
            }
        }]
    }).show();
}



Ext.onReady(function() {
    Ext.QuickTips.init();

    Ext.create('Ext.container.Viewport', {
        layout: 'fit',
        renderTo: Ext.getBody(),
        items: [qcGrid]
    });
});