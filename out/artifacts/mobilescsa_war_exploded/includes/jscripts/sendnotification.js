/**
 * Created by wmdcprog on 4/10/2019.
 */

Ext.onReady(function() {
    Ext.QuickTips.init();
    Ext.create('Ext.container.Viewport', {
        renderTo : Ext.getBody(),
        items : [formNotif],
        layout : {
            type : 'vbox',
            align : 'center',
            pack : 'center'
        }
    });
});

Ext.define('Device', {
    extend : 'Ext.data.Model',
    fields : [
        { name : 'devicename', type : 'string' },
        { name : 'devicetoken', type : 'string' }
    ]
});

var devicesStore = Ext.create('Ext.data.Store', {
    model : 'Device',
    autoLoad : true,
    proxy : {
        type : 'ajax',
        url : 'getdevicetokens',
        method : 'post',
        //  actionMethods config for post request
        actionMethods : {
            create : 'post',
            read : 'post',
            update : 'post',
            destroy : 'post'
        },
        reader : {
            type : 'json',
            rootProperty : 'devicesStore'
        }
    }
});

//	create login form
var formNotif = Ext.create('Ext.form.Panel', {

    frame : true,
    title : 'Send Notification',
    titleAlign : 'center',
    url	: 'sendnotification',
    method : 'post',
    bodyStyle : 'padding:30px',
    width : '40%',
    height : '60%',
    frame : true,

    defaults : {
        allowBlank: false
    },

    layout : {
        type : 'vbox',
        align : 'center',
        pack : 'center'
    },

    style: {
        marginLeft	: 'auto',
        marginRight	: 'auto'
    },

    items : [
        {
            xtype : 'combo',
            inputType : 'combo',
            emptyText: 'Select User',
            id : 'devices',
            name : 'deviceToken',
            anchor: '100%',
            width : '100%',
            margin: '10 0 0 0',
            editable : false,
            store : devicesStore,
            queryMode : 'local',
            displayField :'devicename',
            valueField : 'devicetoken'
        },
        {
            xtype: 'textfield',
            emptyText: 'Title',
            name: 'title',
            id : 'title',
            inputType: 'textfield',
            anchor: '100%',
            width : '100%',
            margin: '10 0 0 0',
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
        },
        {
            xtype: 'textareafield',
            maxRows: 5,
            emptyText: 'Message',
            name: 'body',
            id : 'body',
            inputType: 'textfield',
            anchor: '100%',
            width : '100%',
            margin: '10 0 0 0',
            enforceMaxLength: true,
            maxLength: 64,
            triggers : {
                clears : {
                    cls : 'x-form-clear-trigger',
                    handler : function() {
                        this.setValue('');
                    }
                }
            }
        },
        {
            xtype : 'button',
            text : 'Send',
            id : 'sendButton',
            width : 150,
            height : 40,
            formBind : true,
            disabled : true,
            margin: '20 0 0 0',
            style : {
                display : 'block',
                margin : 'auto'
            },
            handler : function() {
                var form = this.up('form').getForm();
                if(form.isValid())
                {
                    form.submit({

                        waitMsg: 'Sending notification...',
                        method: 'post',

                        success	: function(form, action) {
                            var assoc = Ext.JSON.decode(action.response.responseText);
                            Ext.MessageBox.alert('Success', assoc["reason"]);
                        },

                        failure	: function(form, action) {
                            var assoc = Ext.JSON.decode(action.response.responseText);
                            Ext.MessageBox.alert('Failed', assoc["reason"]);
                        }
                    });
                }
            }
        }
    ]
});