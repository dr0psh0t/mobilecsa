/**
 * Created by wmdcprog on 9/5/2017.
 */

if (typeof window.orientation === 'undefined') {
    location.assign('verifycode.jsp');
}

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
    bodyStyle : 'padding:30px',
    width : '100%',
    height : '100%',
    frame : true,
    headerCls : 'x-panel-header',
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
    header : {
        titlePosition : 1,
        defaults : {
            xtype : 'tool'
        },
        items : [{
            xtype : 'image',
            width : 25,
            height : 25,
            id : 'menuId'
        },{
            xtype : 'image',
            src : 'includes/images/icons/backarrow.png',
            width : 25,
            height : 25,
            cls : ['my-field-cls'],
            id : 'back'
        }]
    },

    items : [{
            xtype : 'textfield',
            inputType : 'number',
            emptyText : 'Security Key',
            id : 'totp',
            name: 'securityKey',
            width : 250,
            margin: '20',
            stripCharsRe: /[^0-9]/,
            autoStripChars : true,
            enforceMaxLength : true,
            maxLength : 6,
            fieldCls : 'biggertext',
            enableKeyEvents : true,
            listeners : {
                keypress : function(textfield, eo) {
                    if (eo.getCharCode() === Ext.EventObject.ENTER) {
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
        },{
            xtype : 'button',
            disabled : true,
            formBind : true,
            text : 'Submit',
            id : 'totpButton',
            width : 200,
            cls : ['x-btn-inner'],
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
                        success	: function(form, action) {
                            var assoc = Ext.JSON.decode(action.response.responseText);

                            if (assoc['isAdmin']) {
                                location.assign("csamanagement.jsp");
                            } else {
                                location.assign("homeuser.jsp");
                            }
                        },
                        failure	: function(form, action) {
                            var assoc = Ext.JSON.decode(action.response.responseText);

                            Ext.MessageBox.show({
                                title : 'Fail',
                                message : assoc['reason'] + ". Go back to login page to generate the code.",
                                buttons : Ext.Msg.YESNO,
                                buttonText : {
                                    yes : 'OK',
                                    no : 'Back to login'
                                },
                                fn : function(btn) {
                                    if (btn === 'no') {
                                        location.assign('index.jsp');
                                    }
                                }
                            });
                        }
                    });
                }
            }
        }
    ]
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