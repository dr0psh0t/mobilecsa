/**
 * Created by wmdcprog on 9/5/2017.
 */

if (typeof window.orientation === 'undefined') {
    location.assign('login.jsp');
}

sendRequest('scanloggedinsession', 'post', { source : '9' }, function (o, s, response) {

    var assoc = Ext.JSON.decode(response.responseText);

    if (assoc['success'])
    {
        if (assoc['isAdmin']) {
            location.assign('csamanagement.jsp');
        }
        else {
            location.assign('homeuser.jsp');
        }
    }
});

Ext.onReady(function() {

    Ext.QuickTips.init();

    Ext.create('Ext.container.Viewport', {

        renderTo : Ext.getBody(),
        id : 'viewportLogin',
        items : [formLogin],

        layout : {
            type : 'vbox',
            align : 'center',
            pack : 'center'
        }
    });

    window.addEventListener('orientationchange', function() {

        setTimeout(function() {

            Ext.getCmp('viewportLogin').setWidth(document.body.clientWidth);
            Ext.getCmp('viewportLogin').setHeight(document.body.clientHeight);

            dimenSubmit(document.body.clientWidth, document.body.clientHeight, window.innerWidth, window.innerHeight);

        }, 200);
    });
});

//	create login form
var formLogin = Ext.create('Ext.form.Panel', {

    labelWidth : 85,
    frame : true,
    title : 'Login',
    titleAlign : 'center',
    defaultType : 'textfield',
    url	: 'Login',
    method : 'post',
    headerCls : 'x-panel-header',
    width : '100%',
    height : '100%',

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
            xtype : 'image',
            id : 'loginIcon',
            src : 'includes/images/icons/lock.png',
            width : 80,
            height : 80,

            listeners : {
                afterrender : function() {

                    var width = Ext.getCmp('loginIcon').getWidth();
                    var height = Ext.getCmp('loginIcon').getHeight();

                    Ext.getCmp('loginIcon').setWidth(width + (width / 4));
                    Ext.getCmp('loginIcon').setHeight(height + (height / 4));
                }
            },

            style : {
                display : 'block',
                marginLeft : 'auto',
                marginRight : 'auto'
            }
        },
        {
            emptyText : 'Username',
            name: 'username',
            id : 'username',
            inputType: 'textfield',
            anchor: '100%',
            width : '75%',
            margin: '23 0 23 0',
            maskRe: /[A-Za-z0-9]/,
            enforceMaxLength: true,
            maxLength: 40,
            padding : '100 100 100 100',
            fieldCls : 'biggertext',

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
            name: 'password',
            id : 'password',
            inputType: 'password',
            emptyText : 'Password',
            anchor: '100%',
            width : '75%',
            margin: '0 0 23 0',
            enforceMaxLength: true,
            maxLength: 32,
            fieldCls : 'biggertext',
            enableKeyEvents : true,

            listeners : {

                keypress : function(textfield, eo) {

                    if (eo.getCharCode() == Ext.EventObject.ENTER) {
                        Ext.getCmp('loginButton').handler();
                    }
                }
            },

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
            text : 'Login',
            id : 'loginButton',
            width : '40%',
            //height : 60,
            formBind : true,
            disabled : true,
            //cls : ['x-btn-inner', 'x-button'],
            cls : 'x-btn-inner',

            style : {
                display : 'block',
                margin : 'auto'
            },

            handler : function() {

                var form = this.up('form').getForm();

                if(form.isValid())
                {
                    form.submit({

                        success : function(form, action) {
                            location.assign('mverifycode.jsp');
                        },

                        failure	: function(form, action){

                            var assoc = Ext.JSON.decode(action.response.responseText);

                            if (assoc['isLocked']) {
                                location.assign('createpassword.jsp');
                            }
                            else {
                                Ext.MessageBox.alert('Failed', assoc["reason"]);
                            }
                        }
                    });
                }
            }
        }
    ]
});