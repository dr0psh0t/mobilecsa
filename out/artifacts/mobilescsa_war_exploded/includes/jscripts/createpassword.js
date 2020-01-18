/**
 * Created by wmdcprog on 9/5/2017.
 */

if (interimUsername === '') {
    location.assign('index.jsp');
} else {
    Ext.onReady(function() {
        Ext.QuickTips.init();
        Ext.apply(Ext.form.field.VTypes, {
            password: function(val, field) {
                if(field.initialPassField) {
                    var pwd = Ext.getCmp(field.initialPassField);
                    return (val == pwd.getValue());
                }
                return true;
            },
            passwordText: 'PASSWORD AND RETYPE PASSWORD DO NOT MATCH!'
        });

        var passwordForm = Ext.create('Ext.form.Panel', {
            region : 'center',
            title : 'Create Password for ' + interimUsername,
            titleAlign : 'center',
            autoScroll : true,
            defaultType : 'textfield',
            defaults : {
                allowBlank : false
            },
            layout : {
                type : 'vbox',
                align : 'center',
                pack : 'center'
            },
            header : {
                titlePosition : 1,
                defaults : {
                    xtype : 'tool'
                },
                items : [
                    {
                        xtype : 'image',
                        //src : 'includes/images/icons/editicon.png',
                        width : 25,
                        height : 25,
                        cls : ['my-field-cls'],
                        id : 'menuId'
                    },
                    {
                        xtype : 'image',
                        src : 'includes/images/icons/backarrow.png',
                        width : 25,
                        height : 25,
                        cls : ['my-field-cls'],
                        id : 'back'
                    }
                ]
            },
            items : [
                {
                    xtype : 'hidden',
                    inputType : 'hidden',
                    name : 'lockedUsername',
                    value : interimUsername
                },
                {
                    emptyText : 'Password',
                    name : 'password',
                    id : 'password',
                    inputType : 'password',
                    width : '80%',
                    margin : '0 0 25 0',
                    enforceMaxLength : true,
                    maxLength : 32,
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
                    emptyText : 'Retype Password',
                    name : 'retypepassword',
                    id : 'retypepassword',
                    inputType : 'password',
                    width : '80%',
                    margin : '0 0 25 0',
                    enforceMaxLength : true,
                    maxLength : 64,
                    fieldCls : 'biggertext',
                    initialPassField: 'password',
                    vtype : 'password',
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
                    disabled : true,
                    formBind : true,
                    text : 'Submit',
                    id : 'totpButton',
                    width : 200,
                    height : 50,
                    padding : '10 10 10 10',
                    cls : 'x-btn-inner',
                    style : {
                        display : 'block',
                        margin : 'auto'
                    },
                    handler : function() {
                        var form = this.up('form').getForm();
                        if(form.isValid()) {
                            form.submit({
                                waitMsg : 'Please wait...',
                                url : 'activateuser',
                                method : 'post',
                                success	: function() {
                                    location.assign('homeuser.jsp');
                                },
                                failure	: function(form, action) {
                                    var assoc = Ext.JSON.decode(action.response.responseText);
                                    Ext.Msg.alert('Failed', assoc["reason"]);
                                }
                            });
                        }
                    }
                }
            ]
        });

        Ext.create('Ext.container.Viewport', {
            layout: 'border',
            renderTo: Ext.getBody(),
            items: [passwordForm]
        });

        Ext.get('back').on('touchstart', function(){
            location.assign('index.jsp');
        });
    });
}