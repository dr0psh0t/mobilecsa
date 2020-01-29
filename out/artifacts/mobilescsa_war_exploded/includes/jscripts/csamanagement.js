/**
 * this is the script for the page csamanagement.jsp
 */

var conn = new Ext.data.Connection();

sendRequest('scanloggedinsession', 'post', { source : '8' }, function (o, s, response) {
    var assoc = Ext.decode(response.responseText);

    if (assoc['success']) {
        if (!assoc['isAdmin']) {
            location.assign('homeuser.jsp');
        }
    } else {
        location.assign('index.jsp');
    }
});

Ext.onReady(function() {
    Ext.QuickTips.init();

    Ext.create('Ext.container.Viewport', {
        layout : 'border',
        id : 'viewportId',
        renderTo : Ext.getBody(),
        items : [{
            xtype : 'panel',
            region : 'center',
            layout : 'border',
            title : 'Wellmade - MCSA CRM',
            bodyPadding : '4 4 4 4',
            margin : 0,
            items: [{
                xtype : 'panel',
                id : 'parentPanelId',
                region : 'center',
                layout : 'fit',     //  setting layout to fit will stretch the grids height to match parent
                padding : 3,
                items : [customerGrid, customerCompanyGrid, gridPanel, contactsGrid, adminGrid, joborderGridPanel]
            },{
                xtype : 'panel',
                id : 'westPanel',
                region : 'west',
                layout : 'fit',
                width : '13%',
                flex : 0,
                padding : 3,
                items : [ menuTree ]
            }],
            tools : [{
                xtype : 'image',
                src : 'includes/images/icons/logged_user_icon.png',
                width : 20,
                height : 20,
                margin: '0 10 0 0'
            },{
                xtype : 'label',
                html : '<p style="font-size:12.5px">' + onlineUserFromAdmin + '</p>',
                cls : [ 'white-label' ],
                margin: '0 20 0 0'
            },{
                xtype : 'image',
                src : 'includes/images/icons/logout_icon.png',
                width : 20,
                height : 20,
                margin: '0 10 0 0'
            },{
                xtype : 'label',
                html : '<p style="font-size:12.5px">Logout</p>',
                cls : [ 'my-field-cls', 'white-label' ],
                margin: '0 20 0 0',
                listeners : {
                    element : 'el',
                    click : function () {
                        sendRequest('logout', 'post', { source : '82' }, function() {
                            location.assign('index.jsp');
                        });
                    }
                }
            }]
        }]
    });
});

Ext.define('Contacts', {
    extend : 'Ext.data.Model',
    fields : [
        { name : 'lastname', type : 'string' },
        { name : 'firstname', type : 'string' },
        { name : 'jobPosition', type : 'string' },
        { name : 'er', type : 'float'},
        { name : 'mf', type : 'float' },
        { name : 'calib', type : 'float' },
        { name : 'isDeleted', type : 'int' },
        { name : 'isTransferred', type : 'int' }
    ]
});

var contactsStore = Ext.create('Ext.data.Store', {
    model : 'Contacts',
    autoLoad : true,
    proxy : {
        type : 'ajax',
        url : 'getcontacts',
        reader : {
            type : 'json',
            rootProperty : 'store'
        },
        actionMethods : {
            create : 'post',
            read : 'post',
            update : 'post',
            destroy : 'post'
        }
    }
});

var contactsGrid = Ext.create('Ext.grid.Panel', {
    title : 'Contacts',
    store : contactsStore,
    hidden : true,
    frame : true,
    iconCls : 'contact-icon',
    listeners : {
        beforeitemdblclick : function (selModel, record, index, options) {
            displayContacts(record.data.contactId, false);
        }
    },
    columns : [{
        text : '<b>Lastname</b>',
        autoSizeColumn : true,
        dataIndex : 'lastname',
        cls : ['grid-column-align-center'],
        flex: 1
    },{
        text : '<b>Firstname</b>',
        dataIndex : 'firstname',
        cls : ['grid-column-align-center'],
        flex : 1
    },{
        text : '<b>Job Position</b>',
        dataIndex : 'jobPosition',
        cls : ['grid-column-align-center'],
        flex : 1
    },{
        text : '<b>ER</b>',
        dataIndex : 'er',
        cls : ['grid-column-align-center'],
        flex : 1
    },{
        text: '<b>MF_GM</b>',
        dataIndex: 'mf',
        cls : ['grid-column-align-center'],
        flex: 1
    },{
        text: '<b>Calibration</b>',
        dataIndex: 'calib',
        cls : ['grid-column-align-center'],
        flex: 1
    },{
        text : '<b>Transferred</b>',
        dataIndex : 'isTransferred',
        align : 'center',
        flex : 1,
        renderer : function(value, metaData, record, rowIndex, colIndex, store) {
            if (value === 1) {
                return '<img style="width: 20px;"src="includes/images/icons/check2.png" />';
            } else {
                return '<img style="width: 20px;"src="includes/images/icons/cross.png" />';
            }
        }
    },{
        text : '<b>Deleted</b>',
        dataIndex : 'isDeleted',
        align : 'center',
        flex : 1,
        renderer : function(value, metaData, record, rowIndex, colIndex, store) {
            if (value === 1) {
                return '<img style="width: 20px;"src="includes/images/icons/check2.png" />';
            } else {
                return '<img style="width: 20px;"src="includes/images/icons/cross.png" />';
            }
        }
    }],
    dockedItems : [{
        xtype : 'toolbar',
        dock : 'top',
        items : [{
            xtype : 'button',
            text : '<b>Edit</b>',
            iconCls : 'edit-icon',
            handler : function() {
                var records = contactsGrid.getSelectionModel().getSelection();

                if (records.length < 1 && records != null) {
                    Ext.Msg.alert('Warning', 'Select a contact to edit');
                    return;
                }

                if (records[0].data.isDeleted === 1) {
                    Ext.Msg.alert('Warning', 'Cannot edit the deleted contact.');
                    return;
                }

                editContact(records[0].data.contactId);
            }
        },{
            xtype : 'button',
            text : '<b>Refresh</b>',
            iconCls : 'refresh-icon',
            handler : function() {
                contactsStore.load({ url : 'getcontacts' });
            }
        },{
            xtype : 'button',
            text : '<b>Transfer</b>',
            iconCls : 'transfer-icon',
            handler : function() {
                var records = contactsGrid.getSelectionModel().getSelection();

                if (records.length < 1 && records != null) {
                    Ext.Msg.alert('Warning', 'Select a contact to transfer');
                    return;
                }

                var myHeight = 170;
                var myWidth = 350;

                Ext.create('Ext.Window', {
                    id: 'passwordWindow',
                    title : 'Enter Password',
                    width: myWidth,
                    height: myHeight,
                    minWidth: myWidth,
                    minHeight: myHeight,
                    layout: 'fit',
                    plain: true,
                    modal: true,
                    items: [
                        Ext.create('Ext.form.Panel', {
                            id : 'passwordForm',
                            height : myHeight,
                            width : myWidth,
                            items : [{
                                xtype : 'textfield',
                                fieldLabel: 'PASSWORD',
                                name: 'password',
                                id : 'password',
                                inputType: 'password',
                                anchor: '100%',
                                width : '85%',
                                margin: '20',
                                enforceMaxLength: true,
                                maxLength: 32,
                                enableKeyEvents : true,
                                allowBlank : false,
                                listeners : {
                                    keypress : function(textfield, eo) {
                                        if (eo.getCharCode() == Ext.EventObject.ENTER) {
                                            Ext.getCmp('submitButton').handler();
                                        }
                                    }
                                }
                            }],

                            buttons : [{
                                text : 'Submit',
                                id : 'submitButton',
                                formBind : true,
                                disabled : true,
                                handler : function() {
                                    var form = this.up('form');

                                    if(form.isValid()) {
                                        form.submit({
                                            method : 'get',
                                            url : 'authorizetransfer',
                                            params : { username : onlineUserFromAdmin },
                                            success	: function(form, action) {  //  transfer contact
                                                Ext.getCmp('passwordForm').close();
                                                Ext.getCmp('passwordWindow').close();

                                                Ext.Msg.show({
                                                    title : 'Transfer Customer',
                                                    msg : 'Do you really want to transfer contact?',
                                                    buttons : Ext.Msg.YESNO,
                                                    callback : function(btn) {

                                                        if (btn === 'yes') {
                                                            sendRequest('transfercontact', 'post', { contactId : records[0].data.contactId },
                                                                function(o, s, response) {
                                                                    var assoc = Ext.decode(response.responseText);

                                                                    if (assoc['success']) {
                                                                        contactsStore.load({ url : 'getcontacts' });
                                                                        Ext.Msg.alert('Success', assoc['reason']);
                                                                    } else {
                                                                        Ext.Msg.alert('Fail', assoc['reason']);
                                                                    }
                                                                });
                                                        }
                                                    }
                                                });
                                            },
                                            failure	: function(form, action) {  //  not authorized
                                                var assoc = Ext.JSON.decode(action.response.responseText);
                                                Ext.Msg.alert('Warning', assoc['reason']);
                                            }
                                        });
                                    }
                                }
                            }, {
                                text : 'Close',
                                handler : function() {
                                    Ext.getCmp('passwordForm').close();
                                    Ext.getCmp('passwordWindow').close();
                                }
                            }]
                        })
                    ]

                }).show();
            }
        }, '-', {
            xtype : 'button',
            text : '<b>Delete</b>',
            iconCls : 'delete',
            handler : function() {
                var records = contactsGrid.getSelectionModel().getSelection();

                if (records.length < 1 && records != null) {
                    Ext.Msg.alert('Warning', 'Select a contact to delete');
                    return;
                }

                if (records[0].data.isDeleted === 1) {
                    Ext.Msg.alert('Warning', 'Contact is already deleted');
                    return;
                }

                Ext.Msg.show({
                    title : 'Delete Contact',
                    msg : 'Do you really want to delete contact?',
                    buttons : Ext.Msg.YESNO,
                    callback : function(btn) {

                        if (btn === 'yes') {
                            sendRequest('deletecontact', 'post', { contactId : records[0].data.contactId },

                                function(o, s, response) {
                                    var assoc = Ext.decode(response.responseText);

                                    if (assoc['success']) {
                                        contactsStore.load({ url : 'getcontacts' });
                                        Ext.Msg.alert('Success', assoc['reason']);
                                    } else {
                                        Ext.Msg.alert('Fail', assoc['reason']);
                                    }
                            });
                        }
                    }
                });
            }
        }, '-', {
            xtype : 'textfield',
            fieldLabel : '<b>Search</b>',
            id : 'queryContact',
            width : '30%',
            labelWidth : 70,
            enableKeyEvents : true,
            triggers : {
                clears : {
                    cls : 'x-form-clear-trigger',
                    handler : function() {
                        this.setValue('');
                    }
                }
            },
            listeners : {
                keypress : function(textfield, eo) {

                    if (eo.getCharCode() == Ext.EventObject.ENTER) {
                        var query = Ext.getCmp('queryContact').getValue();

                        if (query.length > 2) {
                            contactsStore.load({
                                url : 'searchcontactfromadmin',
                                params : { query : query }
                            });
                        }
                    }
                }
            }
        }]
    }, {
        xtype : 'pagingtoolbar',
        store : contactsStore,
        dock : 'bottom',
        displayInfo : true
    }]
});

Ext.define('CustomerCompany', {
    extend : 'Ext.data.Model',
    fields : [
        { name : 'company', type : 'string' },
        { name : 'contactPerson', type : 'string'},
        { name : 'contactNumber', type : 'string'},
        { name : 'telNum', type : 'string' },
        { name : 'faxNum', type : 'string' },
        { name : 'isDeleted', type : 'int' },
        { name : 'isTransferred', type : 'int' }
    ]
});

var customerCompanyStore = Ext.create('Ext.data.Store', {
    model : 'CustomerCompany',
    autoLoad : true,
    proxy : {
        type : 'ajax',
        url : 'getcustomercompany',
        reader : {
            type : 'json',
            rootProperty : 'store'
        },
        actionMethods : {
            create : 'post',
            read : 'post',
            update : 'post',
            destroy : 'post'
        }
    }
});

var customerCompanyGrid = Ext.create('Ext.grid.Panel', {
    title : 'Company',
    store : customerCompanyStore,
    hidden : true,
    frame : true,
    iconCls : 'company-icon',
    listeners : {
        beforeitemdblclick : function (selModel, record, index, options) {
            displayCompanyInfo(record.data.customerId, false);
        }
    },
    columns : [{
        text : '<b>Company</b>',
        autoSizeColumn : true,
        dataIndex : 'company',
        cls : ['grid-column-align-center'],
        flex: 1
    },{
        text : '<b>Contact Person</b>',
        dataIndex : 'contactPerson',
        cls : ['grid-column-align-center'],
        flex : 1
    },{
        text : '<b>Contact Number</b>',
        dataIndex : 'contactNumber',
        cls : ['grid-column-align-center'],
        flex : 1
    },{
        text : '<b>Tel. Num.</b>',
        dataIndex : 'telNum',
        cls : ['grid-column-align-center'],
        flex : 1
    },{
        text : '<b>Fax. Num</b>',
        dataIndex : 'faxNum',
        cls : ['grid-column-align-center'],
        flex : 1
    },{
        text : '<b>Transferred</b>',
        dataIndex : 'isTransferred',
        align : 'center',
        flex : 1,
        renderer : function(value, metaData, record, rowIndex, colIndex, store) {
            if (value === 1) {
                return '<img style="width: 20px;"src="includes/images/icons/check2.png" />';
            } else {
                return '<img style="width: 20px;"src="includes/images/icons/cross.png" />';
            }
        }
    },{
        text : '<b>Deleted</b>',
        dataIndex : 'isDeleted',
        align : 'center',
        flex : 1,
        renderer : function(value, metaData, record, rowIndex, colIndex, store) {
            if (value === 1) {
                return '<img style="width: 20px;"src="includes/images/icons/check2.png" />';
            } else {
                return '<img style="width: 20px;"src="includes/images/icons/cross.png" />';
            }
        }
    }],
    dockedItems : [{
        xtype : 'toolbar',
        dock : 'top',
        items : [{
            xtype : 'button',
            text : '<b>Edit</b>',
            iconCls : 'edit-icon',
            handler : function() {
                var records = customerCompanyGrid.getSelectionModel().getSelection();

                if (records.length < 1 && records != null) {
                    Ext.Msg.alert('Warning', 'Select a customer to edit');
                    return;
                }

                if (records[0].data.isDeleted === 1){
                    Ext.Msg.alert('Warning', 'Cannot edit the deleted customer.');
                    return;
                }

                editCompany(records[0].data.customerId);
            }
        },{
            xtype : 'button',
            text : '<b>Refresh</b>',
            iconCls : 'refresh-icon',
            handler : function() {
                customerCompanyStore.load({ url : 'getcustomercompany' });
            }
        },{
            xtype : 'button',
            text : '<b>Transfer</b>',
            iconCls : 'transfer-icon',
            handler : function() {
                var records = customerCompanyGrid.getSelectionModel().getSelection();

                if (records.length < 1 && records != null) {   //  no contacts selected
                    Ext.Msg.alert('Warning', 'Select a customer to transfer');
                    return;
                }

                var myHeight = 170;
                var myWidth = 350;

                var passwordForm = Ext.create('Ext.form.Panel', {
                    id : 'passwordForm',
                    height : myHeight,
                    width : myWidth,
                    items : [{
                        xtype : 'textfield',
                        fieldLabel: 'PASSWORD',
                        name: 'password',
                        id : 'password',
                        inputType: 'password',
                        anchor: '100%',
                        width : '85%',
                        margin: '20',
                        enforceMaxLength: true,
                        maxLength: 32,
                        enableKeyEvents : true,
                        allowBlank : false,
                        listeners : {
                            keypress : function(textfield, eo) {
                                if (eo.getCharCode() == Ext.EventObject.ENTER) {
                                    Ext.getCmp('submitButton').handler();
                                }
                            }
                        }
                    }],

                    buttons : [{
                        text : 'Submit',
                        id : 'submitButton',
                        formBind : true,
                        disabled : true,
                        handler : function() {
                            var form = this.up('form');

                            if(form.isValid()) {
                                form.submit({
                                    method : 'get',
                                    url : 'authorizetransfer',
                                    params : { username : onlineUserFromAdmin },
                                    success	: function(form, action) {  //  transfer contact
                                        Ext.getCmp('passwordForm').close();
                                        Ext.getCmp('passwordWindow').close();

                                        Ext.Msg.show({
                                            title : 'Customer Transfer',
                                            msg : 'Do you really want to transfer the customer?',
                                            buttons : Ext.Msg.YESNO,
                                            callback : function(btn) {
                                                if (btn === 'yes') {
                                                    sendRequest('transfercompany', 'post', { customerId : records[0].data.customerId },
                                                        function(o, s, response) {
                                                        var assoc = Ext.decode(response.responseText);

                                                        if (assoc['success']) {
                                                            customerCompanyStore.load({ url : 'getcustomercompany' });
                                                            Ext.Msg.alert('Success', assoc['reason']);
                                                        } else {
                                                            Ext.Msg.alert('Failed', assoc['reason']);
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    },
                                    failure	: function(form, action) {  //  not authorized
                                        var assoc = Ext.JSON.decode(action.response.responseText);
                                        Ext.Msg.alert('Warning', assoc['reason']);
                                    }
                                });
                            }
                        }
                    },{
                        text : 'Close',
                        handler : function() {
                            Ext.getCmp('passwordForm').close();
                            Ext.getCmp('passwordWindow').close();
                        }
                    }]
                });

                Ext.create('Ext.Window', {
                    id: 'passwordWindow',
                    title : 'Enter Password',
                    width: myWidth,
                    height: myHeight,
                    minWidth: myWidth,
                    minHeight: myHeight,
                    layout: 'fit',
                    plain: true,
                    modal: true,
                    items: [passwordForm]
                }).show();
            }
        }, '-', {
            xtype : 'button',
            text : '<b>Delete</b>',
            iconCls : 'delete',
            handler : function() {
                var records = customerCompanyGrid.getSelectionModel().getSelection();

                if (records.length < 1 && records != null) {
                    Ext.Msg.alert('Warning', 'Select a customer to delete');
                    return;
                }

                if (records[0].data.isDeleted == 1) {
                    Ext.Msg.alert('Warning', 'Customer already deleted.');
                    return;
                }

                Ext.Msg.show({
                    title : 'Delete Customer',
                    msg : 'Do you really want to delete customer?',
                    buttons : Ext.Msg.YESNO,
                    callback : function(btn) {
                        if (btn === 'yes') {
                            sendRequest('deletecustomer', 'post', { customerId : records[0].data.customerId },
                                function(o, s, response) {
                                    var assoc = Ext.decode(response.responseText);

                                    if (assoc['success']) {
                                        customerCompanyStore.load({ url : 'getcustomercompany' });
                                        Ext.Msg.alert('Success', assoc['reason']);
                                    } else {
                                        Ext.Msg.alert('Fail', assoc['reason']);
                                    }
                            });
                        }
                    }
                });
            }
        }, '-', {
            xtype : 'textfield',
            fieldLabel : '<b>Search</b>',
            id : 'queryCompany',
            width : '30%',
            labelWidth : 70,
            enableKeyEvents : true,
            triggers : {
                clears : {
                    cls : 'x-form-clear-trigger',
                    handler : function() {
                        this.setValue('');
                    }
                }
            },
            listeners : {
                keypress : function(textfield, eo) {
                    if (eo.getCharCode() == Ext.EventObject.ENTER) {
                        var query = Ext.getCmp('queryCompany').getValue();

                        if (query.length > 2) {
                            customerCompanyStore.load({
                                url : 'searchcompanyfromadmin',
                                params : { query : query },
                                callback : function(record, operation, success) {
                                }
                            });
                        }
                    }
                }
            }
        }]
    },{
        xtype : 'pagingtoolbar',
        store : customerCompanyStore,
        dock : 'bottom',
        displayInfo : true
    }]
});

Ext.define('CustomerPerson', {
    extend : 'Ext.data.Model',
    fields : [
        { name : 'lastname', type : 'string' },
        { name : 'firstname', type : 'string' },
        { name : 'telNum', type : 'string' },
        { name : 'faxNum', type : 'string' },
        { name : 'dateOfBirth', type : 'string'},
        { name : 'dateAdded', type : 'string'},
        { name : 'assignedCsa', type : 'string'},
        { name : 'isDeleted', type : 'int'},
        { name : 'isTransferred', type : 'int' }
    ]
});

var customerPersonStore = Ext.create('Ext.data.Store', {
    model : 'CustomerPerson',
    autoLoad : true,
    proxy : {
        type : 'ajax',
        url : 'getcustomerperson',
        reader : {
            type : 'json',
            rootProperty : 'store'
        },
        actionMethods : {
            create : 'post',
            read : 'post',
            update : 'post',
            destroy : 'post'
        }
    }
});

var customerGrid = Ext.create('Ext.grid.Panel', {
    title : 'Individual',
    store : customerPersonStore,
    hidden : true,
    frame : true,
    iconCls : 'customer-icon',
    listeners : {
        beforeitemdblclick : function (selModel, record, index, options) {
            displayCustomerInfo(record.data.customerId, false);
        }
    },
    columns : [{
        text : '<b>Lastname</b>',
        autoSizeColumn : true,
        dataIndex : 'lastname',
        cls : ['grid-column-align-center'],
        flex: 1
    },{
        text : '<b>Firstname</b>',
        dataIndex : 'firstname',
        cls : ['grid-column-align-center'],
        flex : 1
    },{
        text : '<b>Birthdate</b>',
        dataIndex : 'dateOfBirth',
        cls : ['grid-column-align-center'],
        flex : 1
    },{
        text : '<b>Date added</b>',
        dataIndex : 'dateAdded',
        cls : ['grid-column-align-center'],
        flex : 1
    },{
        text : '<b>Added By</b>',
        dataIndex : 'assignedCsa',
        cls : ['grid-column-align-center'],
        flex : 1
    },{
        text : '<b>Tel. Num.</b>',
        dataIndex : 'telNum',
        cls : ['grid-column-align-center'],
        flex : 1
    },{
        text : '<b>Fax. Num</b>',
        dataIndex : 'faxNum',
        cls : ['grid-column-align-center'],
        flex : 1
    },{
        text : '<b>Transferred</b>',
        dataIndex : 'isTransferred',
        align : 'center',
        flex : 1,
        renderer : function(value, metaData, record, rowIndex, colIndex, store) {
            if (value === 1) {
                return '<img style="width: 20px;"src="includes/images/icons/check2.png" />';
            } else {
                return '<img style="width: 20px;"src="includes/images/icons/cross.png" />';
            }
        }
    },{
        text : '<b>Deleted</b>',
        dataIndex : 'isDeleted',
        align : 'center',
        flex : 1,
        renderer : function(value, metaData, record, rowIndex, colIndex, store) {
            if (value === 1) {
                return '<img style="width: 20px;"src="includes/images/icons/cross.png" />';
            } else {
                return '<img style="width: 20px;"src="includes/images/icons/cross.png" />';
            }
        }
    }],
    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',
        items: [{
            xtype: 'button',
            text: '<b>Edit</b>',
            iconCls: 'edit-icon',
            handler: function() {
                var records = customerGrid.getSelectionModel().getSelection();

                if (records.length < 1 && records != null) {
                    Ext.Msg.alert('Warning', 'Select a customer to edit.');
                    return;
                }

                if (records[0].data.isDeleted === 1) {
                    Ext.Msg.alert('Warning', 'Cannot edit the deleted customer');
                    return;
                }

                editCustomer(records[0].data.customerId);
            }
        },{
            xtype : 'button',
            text : '<b>Refresh</b>',
            iconCls : 'refresh-icon',
            handler : function() {
                customerPersonStore.load({ url : 'getcustomerperson' });
            }
        },{
            xtype : 'button',
            text : '<b>Transfer</b>',
            iconCls : 'transfer-icon',
            handler : function() {
                var records = customerGrid.getSelectionModel().getSelection();

                if (records.length < 1 && records != null) {   //  no customer selected
                    Ext.Msg.alert('Warning', 'Select a customer to transfer');
                    return;
                }

                var myHeight = 170;
                var myWidth = 350;
                var passwordForm = Ext.create('Ext.form.Panel', {
                    id : 'passwordForm',
                    height : myHeight,
                    width : myWidth,
                    items : [{
                        xtype : 'textfield',
                        fieldLabel: 'PASSWORD',
                        name: 'password',
                        id : 'password',
                        inputType: 'password',
                        anchor: '100%',
                        width : '85%',
                        margin: '20',
                        enforceMaxLength: true,
                        maxLength: 32,
                        enableKeyEvents : true,
                        allowBlank : false,
                        listeners : {
                            keypress : function(textfield, eo) {
                                if (eo.getCharCode() == Ext.EventObject.ENTER) {
                                    Ext.getCmp('submitButton').handler();
                                }
                            }
                        }
                    }],
                    buttons : [{
                        text : 'Submit',
                        id : 'submitButton',
                        formBind : true,
                        disabled : true,
                        handler : function() {
                            var form = this.up('form');

                            if(form.isValid()) {
                                form.submit({
                                    method : 'get',
                                    url : 'authorizetransfer',
                                    params : { username : onlineUserFromAdmin },
                                    success	: function(form, action) {  //  transfer contact
                                        Ext.getCmp('passwordForm').close();
                                        Ext.getCmp('passwordWindow').close();
                                        Ext.Msg.show({
                                            title : 'Customer Transfer',
                                            msg : 'Do you really want to transfer the customer?',
                                            buttons : Ext.Msg.YESNO,
                                            callback : function(btn) {
                                                if ('yes' === btn) {
                                                    sendRequest('transfercustomer', 'post', { customerId : records[0].data.customerId },
                                                        function(o, s, response) {
                                                            var assoc = Ext.decode(response.responseText);
                                                            if (assoc['success']) {
                                                                customerPersonStore.load({ url : 'getcustomerperson' });
                                                                Ext.Msg.alert('Success', assoc['reason']);
                                                            } else {
                                                                Ext.Msg.alert('Fail', assoc['reason']);
                                                            }
                                                    });
                                                }
                                            }
                                        });
                                    },
                                    failure	: function(form, action) {  //  not authorized
                                        var assoc = Ext.JSON.decode(action.response.responseText);
                                        Ext.Msg.alert('Warning', assoc['reason']);
                                    }
                                });
                            }
                        }
                    }, {
                        text : 'Close',
                        handler : function() {
                            Ext.getCmp('passwordForm').close();
                            Ext.getCmp('passwordWindow').close();
                        }
                    }]
                });

                Ext.create('Ext.Window', {
                    id: 'passwordWindow',
                    title : 'Enter Password',
                    width: myWidth,
                    height: myHeight,
                    minWidth: myWidth,
                    minHeight: myHeight,
                    layout: 'fit',
                    plain: true,
                    modal: true,
                    items: [passwordForm]
                }).show();
            }
        },'-',{
            xtype : 'button',
            text : '<b>Delete</b>',
            iconCls : 'delete',
            handler : function() {
                var records = customerGrid.getSelectionModel().getSelection();

                if (records.length < 1 && records != null) {
                    Ext.Msg.alert('Warning', 'Select a customer to delete');
                    return;
                }

                if (records[0].data.isDeleted == 1) {
                    Ext.Msg.alert('Warning', 'Customer already deleted.');
                    return;
                }

                Ext.Msg.show({
                    title : 'Delete Customer',
                    msg : 'Do you really want to delete customer?',
                    buttons : Ext.Msg.YESNO,
                    callback : function(btn) {
                        if (btn === 'yes') {
                            sendRequest('deletecustomer', 'post', { customerId : records[0].data.customerId },
                                function(o, s, response) {
                                    var assoc = Ext.decode(response.responseText);

                                    if (assoc['success']) {
                                        customerPersonStore.load({ url : 'getcustomerperson' });
                                        Ext.Msg.alert('Success', assoc['reason']);
                                    } else {
                                        Ext.Msg.alert('Fail', assoc['reason']);
                                    }
                            });
                        }
                    }
                });
            }
        },'-',{
            xtype : 'textfield',
            fieldLabel : '<b>Search</b>',
            id : 'queryCustomer',
            width : '30%',
            labelWidth : 70,
            enableKeyEvents : true,
            triggers : {
                clears : {
                    cls : 'x-form-clear-trigger',
                    handler : function() {
                        this.setValue('');
                    }
                }
            },
            listeners : {
                keypress : function(textfield, eo) {
                    if (eo.getCharCode() == Ext.EventObject.ENTER) {
                        var query = Ext.getCmp('queryCustomer').getValue();

                        if (query.length > 2) {
                            customerPersonStore.load({
                                url : 'searchcustomerfromadmin',
                                params : { query : query },
                                callback : function (record, operation, success) {
                                }
                            });
                        }
                    }
                }
            }
        }]
    }, {
        xtype : 'pagingtoolbar',
        store : customerPersonStore,
        dock : 'bottom',
        displayInfo : true
    }]
});

Ext.define('CSAs', {
    extend : 'Ext.data.Model',
    fields : [
        { name : 'username', type : 'string' },
        { name : 'lastname', type : 'string' },
        { name : 'firtname', type : 'string' },
        { name : 'passwordStatus', type : 'string'},
        { name : 'status', type : 'int'}
    ]
});

var csaStore = Ext.create('Ext.data.Store', {
    model : 'CSAs',
    autoLoad : true,
    proxy : {
        type : 'ajax',
        url		: 'getcsa',
        reader : {
            type : 'json',
            rootProperty : 'store'
        },
        actionMethods : {
            create : 'post',
            read : 'post',
            update : 'post',
            destroy : 'post'
        }
    }
});

var menuTree = Ext.create('Ext.tree.Panel', {
    height : '100%',
    rootVisible : false,
    listeners : {
        itemclick : function(s, r) {
            switch (r.data.text) {
                case 'CSA':
                    joborderGridPanel.hide();
                    customerCompanyGrid.hide();
                    customerGrid.hide();
                    contactsGrid.hide();
                    adminGrid.hide();
                    gridPanel.show();
                    break;
                case 'People':
                    joborderGridPanel.hide();
                    gridPanel.hide();
                    customerCompanyGrid.hide();
                    contactsGrid.hide();
                    adminGrid.hide();
                    customerGrid.show();
                    break;
                case 'Company':
                    joborderGridPanel.hide();
                    gridPanel.hide();
                    customerGrid.hide();
                    contactsGrid.hide();
                    adminGrid.hide();
                    customerCompanyGrid.show();
                    break;
                case 'Contacts':
                    joborderGridPanel.hide();
                    gridPanel.hide();
                    customerGrid.hide();
                    customerCompanyGrid.hide();
                    adminGrid.hide();
                    contactsGrid.show();
                    break;
                case 'Administrator':
                    joborderGridPanel.hide();
                    gridPanel.hide();
                    customerGrid.hide();
                    customerCompanyGrid.hide();
                    contactsGrid.hide();
                    adminGrid.show();
                    break;
                case 'Job Order':
                    joborderGridPanel.show();
                    gridPanel.hide();
                    customerGrid.hide();
                    customerCompanyGrid.hide();
                    contactsGrid.hide();
                    adminGrid.hide();
                    break;
            }
        }
    },
    viewConfig : { toggleOnDblClick: false },
    root : {
        text : 'FakeRoot',
        expanded : true,
        children : [{
            text : 'Customer',
            expanded : true,
            iconCls : '',
            children : [{
                text : 'People',
                leaf : true
            },{
                text : 'Company',
                leaf : true
            }]
        },{
            text : 'Job Order',
            expanded : true,
            iconCls : ''
        },{
            text : 'CSA',
            expanded : true,
            iconCls : ''
        },{
            text : 'Contacts',
            expanded : true,
            iconCls : ''
        },{
            text : 'Administrator',
            expanded : true,
            iconCls : ''
        }]
    }
});

Ext.define('Administrator', {
    extend : 'Ext.data.Model',
    fields : [
        { name : 'username', type : 'string' },
        { name : 'adminId', type : 'int' },
        { name : 'creator', type : 'string' },
        { name : 'dateStamp', type : 'string'},
        { name : 'status', type : 'int'}
    ]
});

var adminStore = Ext.create('Ext.data.Store', {
    model : 'Administrator',
    autoLoad : true,
    proxy : {
        type : 'ajax',
        url		: 'getadministrators',
        reader : {
            type : 'json',
            rootProperty : 'store'
        },
        actionMethods : {
            create : 'post',
            read : 'post',
            update : 'post',
            destroy : 'post'
        }
    }
});

var adminGrid = Ext.create('Ext.grid.Panel', {
    title : 'Administrator',
    id : 'adminGrid',
    store : adminStore,
    iconCls : 'admin-icon',
    frame : true,
    hidden : true,
    height : '100%',
    listeners : {
        beforeitemdblclick : function (selModel, record, index, options) {
            displayAdminGrid(record.data.adminId);
        }
    },
    columns : [{
        text : '<b>Username</b>',
        autoSizeColumn : true,
        dataIndex : 'username',
        cls : ['grid-column-align-center'],
        flex : 1
    },{
        text : '<b>Creator</b>',
        dataIndex : 'creator',
        cls : ['grid-column-align-center'],
        flex : 1
    },{
        text : '<b>Date Created</b>',
        dataIndex : 'dateStamp',
        align : 'center',
        flex : 1
    },{
        text: '<b>Status</b>',
        dataIndex: 'status',
        flex : 1,
        align : 'center',
        renderer : function(value, metaData, record, rowIndex, colIndex, store) {
            if (value === 1) {
                return '<img style="width: 20px;"src="includes/images/icons/cross.png" />';
            } else {
                return '<img style="width: 20px;"src="includes/images/icons/check2.png" />';
            }
        }
    }],
    dockedItems : [{
        xtype: 'toolbar',
        dock: 'top',
        buttonAlign: 'right',
        items: [{
            xtype : 'button',
            text : '<b>Create Administrator</b>',
            id : 'createAdminButton',
            iconCls : 'add-icon',
            handler : function () {
                createAdministrator();
            }
        },{
            xtype : 'button',
            text : '<b>Change Password</b>',
            id : 'changeAdminPasswordButton',
            iconCls : 'edit-icon',
            handler : function(){
                changeAdminPass();
            }
        },{
            xtype : 'button',
            text : '<b>Refresh</b>',
            id : 'refreshAdminButton',
            iconCls : 'refresh-icon',
            handler : function(){
                adminStore.reload();
            }
        }]
    }]
});

Ext.define('Joborder', {
    extend : 'Ext.data.Model',
    fields : [
        { name : 'initialJoborderId', type : 'int' },
        { name : 'customer', type : 'string' },
        { name : 'dateAdded', type : 'string' },
        { name : 'serialNo', type : 'string' },
        { name : 'model', type : 'int' },
        { name : 'make', type : 'string' },
        { name : 'joNumber', type : 'string' },
        { name : 'added', type : 'int' }
    ]
});

var joborderStore;
try {
    joborderStore = Ext.create('Ext.data.Store', {
        model : 'Joborder',
        autoLoad : true,
        proxy : {
            type : 'ajax',
            url : 'getallinitialjoborderlist',
            actionMethods : {
                create : 'POST',
                read : 'POST',
                update : 'PUT',
                destroy : 'DELETE'
            },
            reader : {
                type : 'json',
                rootProperty : 'initialJoborderList'
            }
        }
    });
} catch (err) {
    console.log(err.toString());
}

var joborderGridPanel = Ext.create('Ext.grid.Panel', {
    title : 'Joborder',
    id : 'joborderGrid',
    store : joborderStore,
    iconCls : 'jo-icon',
    frame : true,
    height : '100%',
    listeners : {
        beforeitemdblclick : function (selectionModel, record, index, options) {
            displayJoborder(record.data.initialJoborderId);
        }
    },
    columns : [{
        text : '<b>JO Number</b>',
        autoSizeColumn : true,
        dataIndex : 'joNumber',
        cls : ['grid-column-align-center'],
        flex : 0.5
    },{
        text : '<b>Customer</b>',
        autoSizeColumn : true,
        dataIndex : 'customer',
        cls : ['grid-column-align-center'],
        flex : 1
    },{
        text : '<b>Date Added</b>',
        dataIndex : 'dateAdded',
        cls : ['grid-column-align-center'],
        flex : 0.5
    },{
        text : '<b>Serial No</b>',
        dataIndex : 'serialNo',
        cls : ['grid-column-align-center'],
        flex : 0.5
    },{
        text : '<b>Model</b>',
        dataIndex : 'model',
        cls : ['grid-column-align-center'],
        flex : 0.5
    },{
        text : '<b>Make</b>',
        dataIndex : 'make',
        cls : ['grid-column-align-center'],
        flex : 0.5
    },{
        text : '<b>Added</b>',
        dataIndex : 'added',
        cls : ['grid-column-align-center'],
        flex : 0.3,
        renderer : function(value, metaData, record, rowIndex, colIndex, store) {
            if (value === 0) {
                return '<img style="width: 20px;"src="includes/images/icons/cross.png" />';
            } else {
                return '<img style="width: 20px;"src="includes/images/icons/check2.png" />';
            }
        }
    }],
    dockedItems : [{
        xtype : 'toolbar',
        dock : 'top',
        buttonAlign : 'right',
        items : [{
            xtype : 'button',
            text : '<b>Refresh</b>',
            id : 'refreshJoList',
            iconCls : 'refresh-icon',
            handler : function() {
                joborderStore.reload();
            }
        },{
            xtype : 'button',
            text : '<b>Delete</b>',
            id : 'deleteJoborder',
            iconCls : 'delete',
            handler : function() {
                var records = Ext.getCmp('joborderGrid').getSelectionModel().getSelection();

                if (records.length < 1 && records != null) {
                    Ext.Msg.alert('Warning', 'Select joborder to delete.');
                    return;
                }

                Ext.Msg.show({
                    title : 'Delete',
                    msg : 'Do you really want to delete initial joborder?',
                    buttons : Ext.Msg.YESNO,
                    callback : function(btn) {

                        if ('yes' === btn) {
                            sendRequest(
                                'deleteinitialjoborder',
                                'post', {
                                    initialJoborderId : records[0].data.initialJoborderId
                                },
                                function(o, s, response) {
                                    var assoc = Ext.decode(response.responseText);
                                    Ext.Msg.alert(assoc['success'] ? 'Success' : 'Failed', assoc['reason']);
                                    joborderStore.reload();
                                }
                            );
                        }
                    }
                });
            }
        }, '-', {
            xtype : 'textfield',
            fieldLabel : '<b>Search</b>',
            id : 'queryJoborder',
            width : '30%',
            labelWidth : 70,
            enableKeyEvents : true,
            triggers : {
                clears : {
                    cls : 'x-form-clear-trigger',
                    handler : function() {
                        this.setValue('');
                        joborderStore.load({ url : 'getallinitialjoborderlist' });
                    }
                }
            },
            listeners : {
                keypress : function(textfield, eo) {
                    if (eo.getCharCode() == Ext.EventObject.ENTER) {
                        var query = Ext.getCmp('queryJoborder').getValue();
                        if (query.length > 1) {
                            joborderStore.load({
                                url : 'searchjoborderfromadmin',
                                params : { query : query },
                                callback : function (record, operation, success) { }
                            });
                        }
                    }
                }
            }
        }]
    },{
        xtype : 'pagingtoolbar',
        store : joborderStore,
        dock : 'bottom',
        displayInfo : true
    }]
});

var gridPanel = Ext.create('Ext.grid.Panel', {
    title : 'CSA',
    id : 'csaGridId',
    store : csaStore,
    iconCls : 'settings-icon',
    frame : true,
    height : '100%',
    listeners : {
        beforeitemdblclick : function (selModel, record, index, options) {
            displayCsaGrid(record.data.csaId);
        }
    },
    columns : [{
        text: '<b>Username</b>',
        autoSizeColumn : true,
        dataIndex: 'username',
        cls : ['grid-column-align-center'],
        flex : 1
    },{
        text: '<b>Lastname</b>',
        dataIndex: 'lastname',
        cls : ['grid-column-align-center'],
        flex : 1
    },{
        text: '<b>Firstname</b>',
        dataIndex: 'firstname',
        cls : ['grid-column-align-center'],
        flex : 1
    },{
        text: '<b>Password Status</b>',
        dataIndex: 'passwordStatus',
        flex : 1,
        align : 'center',
        renderer : function(value, metaData, record, rowIndex, colIndex, store) {
            if (value === '1') {
                return '<img style="width: 20px;"src="includes/images/icons/cross.png" />';
            } else {
                return '<img style="width: 20px;"src="includes/images/icons/check2.png" />';
            }
        }
    },{
        text: '<b>Status</b>',
        dataIndex: 'status',
        flex : 1,
        align : 'center',
        renderer : function(value, metaData, record, rowIndex, colIndex, store) {
            if (value === 1) {
                return '<img style="width: 20px;"src="includes/images/icons/cross.png" />';
            } else {
                return '<img style="width: 20px;"src="includes/images/icons/check2.png" />';
            }
        }
    }],
    dockedItems : [{
        xtype : 'toolbar',
        dock : 'top',
        buttonAlign : 'right',
        items : [{
            xtype : 'button',
            text : '<b>Add User</b>',
            id : 'activateButton',
            iconCls : 'add-icon',
            handler : function() {
                displayAddForm();
            }
        },{
            xtype : 'button',
            text : '<b>Edit</b>',
            id : 'editButton',
            iconCls : 'edit-icon',
            handler : function() {
                var records = Ext.getCmp('csaGridId').getSelectionModel().getSelection();

                console.log(records);

                if (records.length < 1 && records != null) {
                    Ext.MessageBox.alert("Warning", "Select a user to edit");
                    return;
                }

                edit(records[0].data.csaId, records[0].data.lastname,
                    records[0].data.firstname, records[0].data.username);
            }
        },{
            xtype : 'button',
            text : '<b>Refresh</b>',
            id : 'refreshButton',
            iconCls : 'refresh-icon',
            handler : function(){
                csaStore.reload();
            }
        }, '-', {
            xtype : 'button',
            text : '<b>Lock</b>',
            id : 'lockButton',
            iconCls : 'lock-icon',
            handler : function() {
                var records = Ext.getCmp('csaGridId').getSelectionModel().getSelection();

                if (records.length < 1 && records != null) {
                    Ext.MessageBox.alert("Warning", "Select a user to lock");
                    return;
                }

                if (records[0].data.status === 1) {   //  if the selected row is already locked
                    Ext.MessageBox.alert("Warning", "User is already locked");
                } else {
                    Ext.Msg.show({
                        title : 'Lock user',
                        msg : 'Do you really want to lock user?',
                        buttons : Ext.Msg.YESNO,
                        callback : function(btn) {
                            if ('yes' === btn) {
                                sendRequest('lock', 'post', { csaId : records[0].data.csaId },
                                    function(o, s, response) {
                                        var assoc = Ext.decode(response.responseText);

                                        if (assoc['success']) {
                                            csaStore.reload();
                                            Ext.Msg.alert('Success', assoc['reason']);
                                        } else {
                                            Ext.Msg.alert('Fail', assoc['reason']);
                                        }
                                });
                            }
                        }
                    });
                }
            }
        }]
    }]
});

function changeAdminPass() {
    var changeAdminPassForm = Ext.create('Ext.form.Panel', {
        region : 'center',
        id : 'changeAdminPassForm',
        bodyStyle : 'padding:5px',
        width : 400,
        height : 180,
        items : [{
            xtype : 'textfield',
            fieldLabel : 'Password',
            id : 'password',
            name : 'password',
            inputType : 'password',
            anchor : '100%',
            margin : '20',
            allowBlank : false,
            enableKeyEvents : true,
            listeners : {
                keypress : function(textfield, eo) {
                    if (eo.getCharCode() == Ext.EventObject.ENTER) {
                        Ext.getCmp('changeAdminPassButton').handler();
                    }
                }
            }
        }],
        buttons: [{
            text : 'Add',
            id : 'changeAdminPassButton',
            formBind : true,
            disabled : true,
            handler	: function() {
                var form = this.up('form').getForm();

                if(form.isValid()) {
                    form.submit({
                        waitMsg : 'Please wait...',
                        url : 'changeadminpass',
                        method : 'post',
                        params : { admin : onlineUserFromAdmin },
                        success	: function(form, action) {
                            adminStore.reload();
                            Ext.getCmp('changeAdminPassWindow').close();
                            var assoc = Ext.JSON.decode(action.response.responseText);
                            Ext.Msg.alert('Success', assoc["reason"]);
                        },
                        failure	: function(form, action) {
                            var assoc = Ext.JSON.decode(action.response.responseText);
                            Ext.Msg.alert('Failed', assoc["reason"]);
                        }
                    });
                }
            }
        },{
            text : 'Close',
            handler	: function() {
                Ext.getCmp('changeAdminPassWindow').close();
            }
        }]
    });

    Ext.create('Ext.Window', {
        id : 'changeAdminPassWindow',
        title : 'Change ' +onlineUserFromAdmin+ ' Password',
        width : 400,
        height : 180,
        minWidth : 400,
        minHeight : 180,
        layout : 'fit',
        plain : true,
        modal : true,
        items : [changeAdminPassForm]
    }).show();
}

function createAdministrator() {
    var createAdminForm = Ext.create('Ext.form.Panel', {
        region : 'center',
        id : 'createAdminForm',
        bodyStyle : 'padding:5px',
        width : 400,
        height : 230,
        items : [{
            xtype: 'textfield',
            fieldLabel : 'Username',
            id : 'username',
            name : 'username',
            anchor : '100%',
            margin : '20',
            allowBlank : false
        },{
            xtype : 'textfield',
            fieldLabel : 'password',
            id : 'password',
            name : 'password',
            inputType : 'password',
            anchor : '100%',
            margin : '20',
            allowBlank : false,
            enableKeyEvents : true,
            listeners : {
                keypress : function(textfield, eo) {
                    if (eo.getCharCode() == Ext.EventObject.ENTER) {
                        Ext.getCmp('createAdministratorButton').handler();
                    }
                }
            }
        }],
        buttons: [{
            text : 'Add',
            id : 'createAdministratorButton',
            formBind : true,
            disabled : true,
            handler	: function() {
                var form = this.up('form').getForm();

                if(form.isValid()) {
                    form.submit({
                        waitMsg : 'Please wait...',
                        url : 'createadministrator',
                        method : 'post',
                        params : { creator : onlineUserFromAdmin },
                        success	: function(form, action) {
                            adminStore.reload();
                            Ext.getCmp('createAdminWindow').close();
                            var assoc = Ext.JSON.decode(action.response.responseText);
                            Ext.Msg.alert('Success', assoc["reason"]);
                        },
                        failure	: function(form, action) {
                            var assoc = Ext.JSON.decode(action.response.responseText);
                            Ext.Msg.alert('Failed', assoc["reason"]);
                        }
                    });
                }
            }
        },{
            text : 'Close',
            handler	: function() {
                Ext.getCmp('createAdminWindow').close();
            }
        }]
    });

    Ext.create('Ext.Window', {
        id : 'createAdminWindow',
        title : 'Create Administrator',
        width : 400,
        height : 230,
        minWidth : 400,
        minHeight : 230,
        minHeight : 230,
        layout : 'fit',
        plain : true,
        modal : true,
        items : [createAdminForm]
    }).show();
}

function edit(csaId, lastname, firstname, username) {
    var editForm = Ext.create('Ext.form.Panel', {
        region : 'center',
        bodyStyle : 'padding : 5px',
        width : 450,
        height : 225,
        items : [{
            fieldLabel: 'Lastname',
            name: 'lastname',
            id: 'lastname',
            xtype: 'textfield',
            anchor: '100%',
            margin: '20',
            maskRe: /[A-Za-z\s+]/,
            enforceMaxLength: true,
            maxLength: 32,
            allowBlank : false,
            value: lastname,
            triggers : {
                clears : {
                    cls : 'x-form-clear-trigger',
                    handler : function() {
                        this.setValue('');
                    }
                }
            }
        },{
            fieldLabel: 'Firstname',
            name: 'firstname',
            id: 'firstname',
            xtype: 'textfield',
            anchor: '100%',
            margin: '20',
            maskRe: /[A-Za-z\s+]/,
            enforceMaxLength: true,
            maxLength: 64,
            allowBlank : false,
            value: firstname,
            triggers : {
                clears : {
                    cls : 'x-form-clear-trigger',
                    handler : function() {
                        this.setValue('');
                    }
                }
            }
        }],
        buttons : [{
            text : 'Change Password',
            handler : function() {
                Ext.getCmp('csauseredit').close();
                changePassword(csaId);
            }
        }, '-', {
            text: 'OK',
            formBind: true,
            disabled: true,
            handler: function() {
                var form = this.up('form').getForm();

                if(form.isValid()) {
                    form.submit({
                        waitMsg: 'Please wait...',
                        url: 'edit',
                        method: 'post',
                        params: { csaId : csaId },
                        success: function(form, action) {
                            Ext.getCmp('csauseredit').close();
                            var assoc = Ext.JSON.decode(action.response.responseText);
                            Ext.Msg.alert('Success', assoc["reason"]);
                            csaStore.reload();
                        },
                        failure: function(form, action) {
                            var assoc = Ext.JSON.decode(action.response.responseText);
                            Ext.Msg.alert('Failed', assoc["reason"]);
                        }
                    });
                }
            }
        },{
            text: 'Cancel',
            handler: function() {
                Ext.getCmp('csauseredit').close();
            }
        }]
    });

    Ext.create('Ext.Window', {
        id : 'csauseredit',
        title : 'Edit details for ' + username,
        width : 450,
        height : 225,
        minWidth : 450,
        minHeight : 225,
        layout : 'fit',
        plain : true,
        modal : true,
        items : [editForm]
    }).show();
}

function displayAddForm() {
    Ext.define('Username', {
        extend: 'Ext.data.Model',
        fields: [
            {name: 'text', type: 'string'},
            {name: 'id', type: 'int'}
        ]
    });

    var usernameStore = Ext.create('Ext.data.Store', {
        model: 'Username',
        autoLoad : true,
        proxy: {
            type: 'ajax',
            url: 'getusernamefromcrm',
            method : 'post',
            reader: {
                type: 'json',
                rootProperty: 'usernames'
            }
        }
    });

    var addForm = Ext.create('Ext.form.Panel', {
        region : 'center',
        id : 'addForm',
        bodyStyle : 'padding:5px',
        width : 400,
        height : 230,
        items : [{
            xtype: 'combo',
            fieldLabel: 'Name',
            name: 'name',
            id: 'name',
            queryMode: 'local',
            anchor: '100%',
            margin: '20',
            autoSelect: true,
            forceSelection: true,
            editable: false,
            store: usernameStore,
            displayField: 'text',
            valueField: 'id'
        },{
            fieldLabel: 'Username',
            name: 'username',
            id: 'username',
            xtype : 'textfield',
            anchor: '100%',
            margin: '20',
            enforceMaxLength: true,
            maxLength: 32
        },{
            fieldLabel: 'Password',
            name: 'password',
            id: 'password',
            xtype : 'textfield',
            anchor: '100%',
            margin: '20',
            enforceMaxLength: true,
            maxLength: 32,

            triggers : {
                clears : {
                    cls : 'x-form-clear-trigger',
                    handler : function() {
                        this.setValue('');
                    }
                }
            }
        }],
        buttons: [{
            text: 'ADD',
            formBind: true,
            disabled: true,
            handler: function() {
                var form = this.up('form').getForm();

                if(form.isValid()) {
                    form.submit({
                        waitMsg : 'Please wait...',
                        url : 'adduser',
                        method : 'post',
                        params : { adminUsername : onlineUserFromAdmin },
                        success	: function(form, action) {
                            Ext.getCmp('csauseradd').close();
                            var assoc = Ext.JSON.decode(action.response.responseText);
                            Ext.Msg.alert('Success', assoc["reason"]);
                            csaStore.reload();
                        },
                        failure	: function(form, action) {
                            var assoc = Ext.JSON.decode(action.response.responseText);
                            Ext.Msg.alert('Failed', assoc["reason"]);
                        }
                    });
                }
            }
        },{
            text: 'CLOSE',
            handler: function() {
                Ext.getCmp('csauseradd').close();
            }
        }]
    });

    Ext.create('Ext.Window', {
        id : 'csauseradd',
        title : 'Activate User',
        width : 380,
        height : 275,
        minWidth : 400,
        minHeight : 275,
        layout : 'fit',
        plain : true,
        modal : true,
        items : [addForm]
    }).show();
}

function initMap() {}

function changePassword(csaId) {

    Ext.create('Ext.Window', {
        id : 'tempPassWindow',
        title : 'Add Temporary Password',
        width : 450,
        height : 180,
        minWidth : 450,
        minHeight : 180,
        layout : 'fit',
        plain : true,
        modal : true,
        items : [
            Ext.create('Ext.form.Panel', {
                region: 'center',
                bodyStyle: 'padding : 5px',
                width: 450,
                height: 180,
                items: [{
                    fieldLabel: 'Temporary Password',
                    name: 'tempPassword',
                    id: 'tempPassword',
                    xtype: 'textfield',
                    anchor: '100%',
                    margin: '20',
                    enforceMaxLength: true,
                    maxLength: 32,
                    allowBlank : false,
                    triggers : {
                        clears : {
                            cls : 'x-form-clear-trigger',
                            handler : function() {
                                this.setValue('');
                            }
                        }
                    }
                }],
                buttons : [{
                    text : 'Submit',
                    disabled : true,
                    formBind : true,
                    handler : function() {
                        var form = this.up('form').getForm();

                        if(form.isValid()) {
                            form.submit({
                                waitMsg : 'Please wait...',
                                url : 'changepass',
                                method : 'post',
                                params : { csaId : csaId },
                                success	: function(form, action) {
                                    Ext.getCmp('tempPassWindow').close();
                                    csaStore.reload();
                                    var assoc = Ext.JSON.decode(action.response.responseText);
                                    Ext.Msg.alert('Success', assoc["reason"]);
                                },
                                failure	: function(form, action) {
                                    var assoc = Ext.JSON.decode(action.response.responseText);
                                    Ext.Msg.alert('Failed', assoc["reason"]);
                                }
                            });
                        }
                    }
                },{
                    text : 'Cancel',
                    handler : function() {
                        Ext.getCmp('tempPassWindow').close();
                    }
                }]
            })
        ]
    }).show();
}

function sendRequest(url, method, params, callback) {
    conn.request({
        url: url,
        method: method,
        params: params,
        callback: callback
    });
}