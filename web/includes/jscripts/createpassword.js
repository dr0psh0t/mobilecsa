/**
 * Created by wmdcprog on 3/11/2017.
 */

if (interimUsername === '') {
    location.assign('index.jsp');
}

Ext.onReady(function() {
    Ext.QuickTips.init();

    Ext.apply(Ext.form.field.VTypes, {
        password: function(val, field) {
            if(field.initialPassField) {
                var pwd = Ext.getCmp(field.initialPassField);
                return (val === pwd.getValue());
            }
            return true;
        },
        passwordText: 'Password and Retype Password do not match'
    });

    Ext.create('Ext.container.Viewport', {
        renderTo: Ext.getBody(),
        layout: {
            type: 'vbox',
            align: 'center',
            pack: 'center'
        },
        items: [Ext.create('Ext.form.Panel', {
            frame: true,
            title: 'Create Password',
            titleAlign: 'center',
            defaultType: 'textfield',
            bodyStyle: 'padding: 6px',
            url: 'activateuser',
            method: 'post',
            width: 300,
            height: 295,
            defaults: {
                allowBlank: false
            },
            layout: {
                type: 'vbox',
                align: 'center',
                pack: 'center'
            },
            style: {
                marginLeft: 'auto',
                marginRight: 'auto'
            },
            items: [{
                xtype: 'image',
                id: 'loginIcon',
                src: 'includes/images/icons/lock_2.png',
                width: 80,
                height: 80,
                style: {
                    display: 'block',
                    marginBottom: 20
                }
            },{
                xtype: 'hidden',
                inputType: 'hidden',
                name: 'lockedUsername',
                value: interimUsername
            },{
                fieldLabel: 'Password',
                name: 'password',
                id: 'password',
                inputType: 'password',
                anchor: '100%',
                width: '100%',
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
                fieldLabel: 'Retype Password',
                name: 'retypepassword',
                id: 'retypepassword',
                inputType: 'password',
                anchor: '100%',
                width: '100%',
                enforceMaxLength: true,
                maxLength: 64,
                initialPassField: 'password',
                vtype: 'password',
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
                text: 'Submit',
                formBind: true,
                disabled: true,
                handler: function() {

                    Ext.MessageBox.show({
                        msg: 'Password',
                        progressText: 'Creating password...',
                        width: 300,
                        wait: true,
                        waitConfig:{
                            duration: 60000,
                            text: 'Creating password...',
                            scope: this,
                            fn: function() {
                                Ext.MessageBox.hide();
                            }
                        }
                    });

                    var form = this.up('form').getForm();

                    if (form.isValid()) {
                        form.submit({
                            success: function() {
                                Ext.MessageBox.hide();

                                setTimeout(function() {
                                    location.assign('home.jsp');
                                }, 250);
                            },
                            failure: function(form, action) {
                                var assoc = Ext.decode(action.response.responseText);
                                Ext.MessageBox.hide();
                                Ext.Msg.alert('Failed', assoc["reason"]);
                            }
                        });
                    }
                }
            }]
        })]
    });

    /*
    Ext.create('Ext.container.Viewport', {
        layout: 'border',
        renderTo: Ext.getBody(),
        items: [Ext.create('Ext.form.Panel', {
            region: 'center',
            title: 'Create Password for ' + interimUsername,
            titleAlign: 'center',
            autoScroll: true,
            defaultType: 'textfield',
            defaults: {
                allowBlank: false
            },
            layout: {
                type: 'vbox',
                align: 'center',
                pack: 'center'
            },
            header: {
                titlePosition: 1,
                defaults: {
                    xtype: 'tool'
                },
                items: [{
                    xtype: 'image',
                    //src: 'includes/images/icons/editicon.png',
                    width: 25,
                    height: 25,
                    cls: ['my-field-cls'],
                    id: 'menuId'
                },{
                    xtype: 'image',
                    src: 'includes/images/icons/backarrow.png',
                    width: 25,
                    height: 25,
                    cls: ['my-field-cls'],
                    id: 'back'
                }]
            },
            items: [{
                    xtype: 'hidden',
                    inputType: 'hidden',
                    name: 'lockedUsername',
                    value: interimUsername
                },{
                    emptyText: 'Password',
                    name: 'password',
                    id: 'password',
                    inputType: 'password',
                    width: '80%',
                    margin: '0 0 25 0',
                    enforceMaxLength: true,
                    maxLength: 32,
                    fieldCls: 'biggertext',
                    triggers: {
                        clears: {
                            cls: 'x-form-clear-trigger',
                            handler: function() {
                                this.setValue('');
                            }
                        }
                    }
                },{
                    emptyText: 'Retype Password',
                    name: 'retypepassword',
                    id: 'retypepassword',
                    inputType: 'password',
                    width: '80%',
                    margin: '0 0 25 0',
                    enforceMaxLength: true,
                    maxLength: 64,
                    fieldCls: 'biggertext',
                    initialPassField: 'password',
                    vtype: 'password',
                    triggers: {
                        clears: {
                            cls: 'x-form-clear-trigger',
                            handler: function() {
                                this.setValue('');
                            }
                        }
                    }
                },{
                    xtype: 'button',
                    disabled: true,
                    formBind: true,
                    text: 'Submit',
                    id: 'totpButton',
                    width: 200,
                    height: 50,
                    padding: '10 10 10 10',
                    cls: 'x-btn-inner',
                    style: {
                        display: 'block',
                        margin: 'auto'
                    },
                    handler: function() {
                        var form = this.up('form').getForm();

                        if(form.isValid()) {
                            form.submit({
                                waitMsg: 'Please wait...',
                                url: 'activateuser',
                                method: 'post',
                                success: function() {
                                    location.assign('home.jsp');
                                },
                                failure: function(form, action) {
                                    var assoc = Ext.JSON.decode(action.response.responseText);
                                    Ext.Msg.alert('Failed', assoc["reason"]);
                                }
                            });
                        }
                    }
                }
            ]
        })]
    });*/
});