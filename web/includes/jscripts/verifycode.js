/**
 * Created by wmdcprog on 9/5/2017.
 */

sendRequest('confirmmomentarysessions', 'post', { source: '9' }, function(o, s, response) {
    var assoc = Ext.decode(response.responseText);

    if (!assoc['success']) {
        location.assign('index.jsp');
    }
});

var verifyCodeForm = Ext.create('Ext.form.Panel', {
    region: 'center',
    title: 'Security Key',
    titleAlign: 'center',
    bodyStyle: 'padding:6px',
    width: 200,
    height: 200,
    frame: true,
    defaults: {
        allowBlank: false
    },
    layout: {
        type: 'vbox',
        align: 'center',
        pack: 'center'
    },
    style: {
        marginLeft	: 'auto',
        marginRight	: 'auto'
    },
    items: [{
        xtype: 'textfield',
        //inputType: 'number',
        emptyText: 'Security Key',
        id: 'totp',
        name: 'securityKey',
        anchor: '100%',
        width: '100%',
        margin: '0 0 0 0',
        maskRe: /[0-9]/,
        enforceMaxLength: true,
        maxLength: 6,
        enableKeyEvents: true,
        listeners: {
            keypress: function(textfield, eo) {
                if (eo.getCharCode() === Ext.EventObject.ENTER) {
                    Ext.getCmp('totpButton').handler();
                }
            }
        },
        triggers: {
            clears: {
                cls: 'x-form-clear-trigger',
                handler: function() {
                    this.setValue('');
                }
            }
        }
    }],
    buttons: [{
        disabled: true,
        formBind: true,
        text: 'Submit',
        id: 'totpButton',
        handler: function() {

            Ext.MessageBox.show({
                msg: 'Security Key',
                progressText: 'Verifying...',
                width: 300,
                wait: true,
                waitConfig: {
                    duration: 60000,
                    text: 'Verifying...',
                    scope: this,
                    fn: function() {
                        Ext.MessageBox.hide();
                    }
                }
            });

            var form = this.up('form').getForm();

            if(form.isValid()) {
                form.submit({
                    url: 'securitykey',
                    method: 'post',
                    success: function(form, action) {
                        var assoc = Ext.decode(action.response.responseText);

                        Ext.MessageBox.hide();

                        setTimeout(function() {
                            if (assoc['isAdmin']) {
                                location.assign("csamanagement.jsp");
                            } else {
                                location.assign("home.jsp");
                            }
                        }, 250);
                    },
                    failure: function(form, action) {
                        var assoc = Ext.decode(action.response.responseText);

                        Ext.MessageBox.hide();
                        Ext.MessageBox.show({
                            title: 'Fail',
                            message: assoc['reason'] + ". Go back to login page to generate the code.",
                            buttons: Ext.Msg.YESNO,
                            buttonText: {
                                yes: 'OK',
                                no: 'Back to login'
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
        renderTo: Ext.getBody(),
        layout: {
            type: 'vbox',
            align: 'center',
            pack: 'center'
        },
        items: [verifyCodeForm]
    });
});