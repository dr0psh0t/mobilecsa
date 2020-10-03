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

Ext.define('Customer', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'customerId', type: 'string'},
        {name: 'source', type: 'string'},
        {name: 'cId', type: 'int'},
        {name: 'customer', type: 'string'}
    ]
});

var customerStore = Ext.create('Ext.data.Store', {
    model: 'Customer',
    autoLoad: false/*,
    proxy: {
        type: 'ajax',
        url: 'getmcsacustomerlist',
        method: 'post',
        extraParams: {
            cid: csaId,
            filter: customerFilter
        },
        actionMethods: {
            create: 'post',
            read: 'post',
            update: 'post',
            destroy: 'post'
        },
        reader: {
            type: 'json',
            rootProperty: 'customers'
        }
    }*/
});

Ext.define('Engine', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'makeId', type: 'int'},
        {name: 'modelId', type: 'int'},
        {name: 'year', type: 'int'},
        {name: 'model', type: 'string'},
        {name: 'category', type: 'string'},
        {name: 'make', type: 'string'}
    ]
});

var engineStore = Ext.create('Ext.data.Store', {
    model: 'Engine',
    autoLoad: false,
    /*proxy: {
        type: 'ajax',
        url: 'getmcsaenginemodellist',
        method: 'post',
        actionMethods: {
            create: 'post',
            read: 'post',
            update: 'post',
            destroy: 'post'
        },
        reader: {
            type: 'json',
            rootProperty: 'models'
        }
    }*/
});

var customer;
var source;
var engineModelId;
var customerId;

Ext.onReady(function() {
    Ext.QuickTips.init();

    Ext.create('Ext.container.Viewport', {
        layout: 'fit',
        renderTo: Ext.getBody(),
        items: [
            Ext.create('Ext.form.Panel', {
                region: 'center',
                title: 'Initial Joborder',
                titleAlign: 'center',
                id: 'formId',
                autoScroll: true,
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
                    }, {
                        xtype: 'image',
                        src: 'includes/images/icons/backarrow.png',
                        width: 25,
                        height: 25,
                        cls: ['my-field-cls'],
                        id: 'back'
                    }]
                },
                defaults: {
                    allowBlank: false
                },
                items: [{
                    xtype: 'textfield',
                    fieldLabel: 'JO Number *',
                    name: 'joNumber',
                    id: 'joNumber',
                    anchor: '100%',
                    maskRe: /[A-Za-z0-9]/,
                    margin: '10',
                    enforceMaxLength: true,
                    maxLength: 9,
                    triggers: {
                        clears: {
                            cls: 'x-form-clear-trigger',
                            handler: function() {
                                this.setValue('');
                            }
                        }
                    }
                },{
                    xtype: 'combobox',
                    fieldLabel: 'Customer *',
                    anchor: '100%',
                    margin: '10',
                    //name: 'customerId',
                    id: 'customer',
                    displayField: 'customer',
                    //valueField: 'cId',
                    width: 300,
                    store: customerStore,
                    queryMode: 'remote',
                    typeAhead: true,
                    minChars: 2,
                    listeners: {
                        select: function(combo, record, eOpts) {
                            customer = record.data.customer;
                            source = record.data.source;
                            customerId = record.data.cId;
                        },
                        change: function(newValue, oldValue, eOpts) {
                            customerStore.setProxy({
                                type: 'ajax',
                                url: 'getmcsacustomerlist',
                                method: 'post',
                                actionMethods: {
                                    create: 'post',
                                    read: 'post',
                                    update: 'post',
                                    destroy: 'post'
                                },
                                reader: {
                                    type: 'json',
                                    rootProperty: 'customers'
                                }
                            });

                            customerStore.load({
                                params: {cid: csaId, filter: oldValue.toString()}
                            });
                        }
                    }
                },{
                    xtype: 'textfield',
                    fieldLabel: 'Mobile *',
                    name: 'mobile',
                    id: 'mobile',
                    inputType: 'number',
                    anchor: '100%',
                    margin: '10',
                    enforceMaxLength: true,
                    maxLength: 11,
                    triggers: {
                        clears: {
                            cls: 'x-form-clear-trigger',
                            handler: function() {
                                this.setValue('');
                            }
                        }
                    }
                },{
                    xtype: 'textfield',
                    fieldLabel: 'Purchase Order *',
                    name: 'purchaseOrder',
                    id: 'purchaseOrder',
                    inputType: 'textfield',
                    maskRe: /[A-Za-z0-9]/,
                    anchor: '100%',
                    margin: '10',
                    enforceMaxLength: true,
                    maxLength: 16,
                    triggers: {
                        clears: {
                            cls: 'x-form-clear-trigger',
                            handler: function() {
                                this.setValue('');
                            }
                        }
                    }
                },{
                    xtype: 'datefield',
                    fieldLabel: 'PO Date *',
                    anchor: '100%',
                    margin: '10',
                    name: 'poDate',
                    id: 'poDate',
                    format: 'Y-m-d',
                    value: new Date()
                },{
                    xtype: 'combobox',
                    fieldLabel: 'Engine Model *',
                    anchor: '100%',
                    margin: '10',
                    //name: 'modelId',
                    id: 'engineModel',
                    displayField: 'model',
                    //valueField: 'modelId',
                    store: engineStore,
                    queryMode: 'remote',
                    typeAhead: true,
                    minChars: 1,
                    width: 300,
                    listConfig: {
                        maxHeight: 400
                    },
                    listeners: {
                        select: function(combo, record, eOpts) {
                            Ext.getCmp('make').setValue(record.data.make);
                            Ext.getCmp('cat').setValue(record.data.category);

                            engineModelId = record.data.modelId;
                        },
                        change: function(newValue, oldValue, eOpts) {
                            Ext.getCmp('make').setValue('');
                            Ext.getCmp('cat').setValue('');

                            engineStore.setProxy({
                                type: 'ajax',
                                url: 'getmcsaenginemodellist',
                                method: 'post',
                                actionMethods: {
                                    create: 'post',
                                    read: 'post',
                                    update: 'post',
                                    destroy: 'post'
                                },
                                reader: {
                                    type: 'json',
                                    rootProperty: 'models'
                                }
                            });

                            engineStore.load({
                                params: {filter: oldValue.toString()}
                            });
                        }
                    }
                },{
                    xtype: 'textfield',
                    fieldLabel: 'Make *',
                    anchor: '100%',
                    margin: '10',
                    id: 'make',
                    name: 'make',
                    editable: false
                },{
                    xtype: 'textfield',
                    fieldLabel: 'Category *',
                    anchor: '100%',
                    margin: '10',
                    id: 'cat',
                    name: 'cat',
                    editable: false
                },{
                    xtype: 'textfield',
                    fieldLabel: 'Serial No *',
                    name: 'serialNo',
                    id: 'serialNo',
                    inputType: 'textfield',
                    maskRe: /[A-Za-z0-9]/,
                    anchor: '100%',
                    margin: '10',
                    enforceMaxLength: true,
                    maxLength: 32,
                    triggers: {
                        clears: {
                            cls: 'x-form-clear-trigger',
                            handler: function() {
                                this.setValue('');
                            }
                        }
                    }
                },{
                    xtype: 'datefield',
                    fieldLabel: 'Date Received',
                    anchor: '100%',
                    margin: '10',
                    name: 'dateReceive',
                    id: 'dateReceive',
                    format: 'Y-m-d',
                    value: new Date()
                },{
                    xtype: 'datefield',
                    fieldLabel: 'Date Commit',
                    anchor: '100%',
                    margin: '10',
                    name: 'dateCommit',
                    id: 'dateCommit',
                    format: 'Y-m-d',
                    value: new Date()
                },{
                    xtype: 'textfield',
                    fieldLabel: 'Reference No',
                    name: 'refNo',
                    id: 'refNo',
                    inputType: 'textfield',
                    maskRe: /[A-Za-z0-9]/,
                    anchor: '100%',
                    margin: '10',
                    enforceMaxLength: true,
                    maxLength: 9,
                    triggers: {
                        clears: {
                            cls: 'x-form-clear-trigger',
                            handler: function() {
                                this.setValue('');
                            }
                        }
                    }
                },{
                    xtype: 'textareafield',
                    fieldLabel: 'Remarks',
                    anchor: '100%',
                    margin: '10',
                    maxRows: 4,
                    name: 'remarks',
                    id: 'remarks'
                },{
                    xtype: 'filefield',
                    fieldLabel: 'Photo',
                    name: 'photo',
                    id: 'photo',
                    msgTarget: 'side',
                    anchor: '100%',
                    margin: '10',
                    buttonText: 'Select',
                    clearOnSubmit: false,
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
                                Ext.Msg.alert('Warning', 'Photo should be jpeg. Select another one');
                                Ext.getCmp('photo').inputEl.dom.value = '';
                            }

                            if (fileSize > 512000) {
                                Ext.Msg.alert('Warning', "Image is too large. Should be under 512 KB.");
                                Ext.getCmp('photo').inputEl.dom.value = '';
                            }
                        }
                    }
                },{
                    xtype: 'button',
                    disabled: true,
                    margin: '10',
                    anchor: '100%',
                    height: 40,
                    text: 'Add Initial Joborder',
                    itemId: 'buttonToBind',
                    formBind: true,
                    cls: 'block-text',
                    handler: function() {
                        var form = this.up('form');

                        if (form.isValid()) {

                            if (Ext.getCmp('joNumber').getValue() !== Ext.getCmp('refNo').getValue()) {
                                Ext.Msg.alert('Warning', 'Reference number should be the same with JO number.');
                                return;
                            }

                            form.submit({
                                waitMsg: 'Adding initial joborder...',
                                url: 'initialjoborder',
                                method: 'post',
                                params: {
                                    joSignature: utilSignature,
                                    preparedBy: csaId,
                                    source: source,
                                    customer: customer,
                                    imageType: 'jpeg',
                                    customerId: customerId,
                                    modelId: engineModelId
                                },
                                success: function(form, action) {
                                    Ext.MessageBox.show({
                                        title: 'Message',
                                        msg: 'Successfully added initial joborder',
                                        icon: Ext.MessageBox.INFO,
                                        buttons: Ext.MessageBox.OK,
                                        fn: function() {
                                            window.location.reload(true);
                                        }
                                    });
                                },
                                failure: function(form, action) {
                                    try {
                                        var assoc = Ext.JSON.decode(action.response.responseText);
                                        Ext.Msg.alert('', assoc['reason']);
                                    } catch (err) {
                                        Ext.Msg.alert("Exception", msg);
                                    }
                                }
                            });
                        }
                    }
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