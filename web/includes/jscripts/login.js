/**
 * Created by wmdcprog on 9/5/2017.
 */

// Stephanie Emma Jahboy Pilones

sendRequest('scanloggedinsession', 'post', { source : '9' }, function (o, s, response) {
    var assoc = Ext.JSON.decode(response.responseText);

    if (assoc['success']) {
        if (assoc['isAdmin']) {
            location.assign('csamanagement.jsp');
        } else {
            location.assign('homeuser.jsp');
        }
    }
});

Ext.onReady(function() {
    Ext.QuickTips.init();
    Ext.create('Ext.container.Viewport', {
        renderTo : Ext.getBody(),
        items : [formLogin],
        layout : {
            type : 'vbox',
            align : 'center',
            pack : 'center'
        }
    });
});

//	create login form
var formLogin = Ext.create('Ext.form.Panel', {
    frame : true,
    title : 'Login',
    titleAlign : 'center',
    defaultType : 'textfield',
    bodyStyle : 'padding:6px',
    url	: 'Login',
    method : 'post',
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
    width : 300,
    height : 295,
    items : [{
        xtype : 'image',
        id : 'loginIcon',
        src : 'includes/images/icons/lock.png',
        width : 80,
        height : 80,
        style : {
            display : 'block',
            marginBottom: 20
        }
    },{
        fieldLabel : 'Username:',
        name: 'username',
        id : 'username',
        inputType: 'textfield',
        anchor: '100%',
        width : '100%',
        maskRe: /[A-Za-z0-9]/,
        enforceMaxLength: true,
        maxLength: 40
    },{
        fieldLabel : 'Password:',
        name: 'password',
        id : 'password',
        inputType: 'password',
        anchor: '100%',
        width : '100%',
        enforceMaxLength: true,
        maxLength: 32,
        enableKeyEvents : true,
        listeners : {
            keypress : function(textfield, eo) {
                if (eo.getCharCode() === Ext.EventObject.ENTER) {
                    Ext.getCmp('loginButton').handler();
                }
            }
        }
    },{
        xtype: 'label',
        text: '',
        id: 'msgLabel',
        style: {
            'color':'red',
            'style': 'Helvetica',
            'font-size': '9px'
        }
    }],

    buttons: [{
        text: 'Login',
        id: 'loginButton',
        formBind: true,
        disabled: true,
        handler: function() {
            var form = this.up('form').getForm();
            if(form.isValid()) {
                form.submit({
                    success	: function() {
                        location.assign('verifycode.jsp');
                    },
                    failure	: function(form, action) {
                        var assoc = Ext.JSON.decode(action.response.responseText);
                        if (assoc['isLocked']) {
                            location.assign('createpassword.jsp');
                        } else {
                            Ext.MessageBox.alert('Failed', assoc["reason"]);
                        }
                    }
                });
            }
        }
    }]
});