/**
 * Created by wmdcprog on 9/5/2017.
 */

sendRequest('confirmmomentarysessions', 'post', { source : '9' }, function(o, s, response) {

    var assoc = Ext.decode(response.responseText);

    if (!assoc['success']) {
        location.assign('index.jsp');
    }
});

var verifyCodeForm = Ext.create('Ext.form.Panel', {
    region : 'center',
    title : 'Security Key',
    titleAlign : 'center',
    bodyStyle : 'padding:6px',
    width : 200,
    height : 200,
    frame : true,
    defaults : {
        allowBlank : false
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

    items : [{
        xtype: 'textfield',
        inputType : 'number',
        emptyText : 'Security Key',
        id : 'totp',
        name: 'securityKey',
        anchor: '100%',
        width : '100%',
        margin: '0 0 0 0',
        maskRe : /[0-9]/,
        enforceMaxLength : true,
        maxLength : 6,
        enableKeyEvents : true,
        listeners : {
            keypress : function(textfield, eo) {
                if (eo.getCharCode() == Ext.EventObject.ENTER) {
                    Ext.getCmp('totpButton').handler();
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
    }/*,{
        xtype : 'button',
        disabled : true,
        formBind : true,
        text : 'Submit',
        id : 'totpButton',
        width : 200,
        height : 40,
        padding : '10 10 10 10',
        style : {
            display : 'block',
            margin : 'auto'
        },
        handler : function() {
            var form = this.up('form').getForm();

            if(form.isValid()) {

                form.submit({
                    url : 'securitykey',
                    method : 'post',
                    success: function(form, action) {
                        var assoc = Ext.JSON.decode(action.response.responseText);

                        if (assoc['isAdmin']) {
                            location.assign("csamanagement.jsp");
                        } else {
                            location.assign("homeuser.jsp");
                        }
                    },
                    failure: function(form, action) {
                        var assoc = Ext.JSON.decode(action.response.responseText);

                        Ext.MessageBox.show({
                            title : 'Fail',
                            message : assoc['reason'] + ". Go back to login page to generate the code.",
                            buttons : Ext.Msg.YESNO,
                            buttonText : {
                                yes : 'OK',
                                no : 'Back to login'
                            },
                            fn: function(btn) {
                                if (btn === 'no') {
                                    location.assign('index.jsp');
                                }
                            }
                        });
                    }
                });
            }
        }
    }*/],

    buttons: [{
        disabled : true,
        formBind : true,
        text : 'Submit',
        id : 'totpButton',
        handler: function() {
            var form = this.up('form').getForm();

            if(form.isValid()) {

                form.submit({
                    url : 'securitykey',
                    method : 'post',
                    success: function(form, action) {
                        var assoc = Ext.JSON.decode(action.response.responseText);

                        if (assoc['isAdmin']) {
                            location.assign("csamanagement.jsp");
                        } else {
                            location.assign("homeuser.jsp");
                        }
                    },
                    failure: function(form, action) {
                        var assoc = Ext.JSON.decode(action.response.responseText);

                        Ext.MessageBox.show({
                            title : 'Fail',
                            message : assoc['reason'] + ". Go back to login page to generate the code.",
                            buttons : Ext.Msg.YESNO,
                            buttonText : {
                                yes : 'OK',
                                no : 'Back to login'
                            },
                            fn: function(btn) {
                                if (btn === 'no') {
                                    location.assign('index.jsp');
                                }
                            }
                        });
                    }
                });
            }
        }
    }]
});

Ext.onReady(function () {
    Ext.QuickTips.init();

    Ext.create('Ext.container.Viewport', {
        layout : 'border',
        renderTo : Ext.getBody(),
        layout : {
            type : 'vbox',
            align : 'center',
            pack : 'center'
        },
        items : [verifyCodeForm]
    });

    Ext.get('back').on('touchstart', function(){
        location.assign('homeuser.jsp');
    });
});