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

Ext.onReady(function() {
    Ext.QuickTips.init();

    Ext.create('Ext.container.Viewport', {
        layout: 'fit',
        renderTo: Ext.getBody(),
        items: [
            Ext.create('Ext.form.Panel', {
                region: 'center',
                title: 'New Company',
                titleAlign: 'center',
                id: 'formId',
                autoScroll: true,
                url: 'addcustomercompany',
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
                    },{
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
                    fieldLabel: 'Company *',
                    name: 'company',
                    id: 'company',
                    inputType: 'textfield',
                    anchor: '100%',
                    margin: '10',
                    enforceMaxLength: true,
                    maxLength: 100,
                    minLength: 3,
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
                    fieldLabel: 'Address *',
                    name: 'address',
                    id: 'address',
                    inputType: 'textfield',
                    anchor: '100%',
                    margin: '10',
                    enforceMaxLength: true,
                    maxLength: 128,
                    triggers: {
                        clears: {
                            cls: 'x-form-clear-trigger',
                            handler: function() {
                                this.setValue('');
                            }
                        }
                    }
                },{
                    xtype: 'combo',
                    fieldLabel: 'Industry *',
                    id: 'industry',
                    name: 'industry',
                    anchor: '100%',
                    margin: '10',
                    editable: false,
                    store: industryStore,
                    queryMode: 'local',
                    displayField: 'industryname',
                    valueField: 'industryid',
                    listConfig: {
                        maxHeight: 400
                    }
                },{
                    xtype: 'combo',
                    fieldLabel: 'Plant *',
                    id: 'plant',
                    name: 'plant',
                    anchor: '100%',
                    margin: '10',
                    editable: false,
                    store: plantStore,
                    queryMode: 'local',
                    displayField: 'plant_name',
                    valueField: 'plant_id',
                    listConfig: {
                        maxHeight: 400
                    }
                },{
                    xtype: 'combo',
                    fieldLabel: 'City *',
                    id: 'city',
                    name: 'city',
                    anchor: '100%',
                    margin: '10',
                    editable: false,
                    store: cityIdStore,
                    queryMode: 'local',
                    displayField:'cityname',
                    valueField: 'cityid',
                    listeners: {
                        'change': function(field, selectedValue) {
                            setZip(selectedValue);
                        }
                    },
                    listConfig: {
                        maxHeight: 400
                    }
                },{
                    xtype: 'combo',
                    fieldLabel: 'Province *',
                    id: 'province',
                    name: 'province',
                    anchor: '100%',
                    margin: '10',
                    editable: false,
                    store: provinceStore,
                    queryMode: 'local',
                    displayField: 'provincename',
                    valueField: 'provinceid',
                    listConfig: {
                        maxHeight: 400
                    }
                },{
                    xtype: 'combo',
                    fieldLabel: 'Country *',
                    id: 'country',
                    name: 'country',
                    anchor: '100%',
                    margin: '10',
                    editable: false,
                    store: countryIdStore,
                    queryMode: 'local',
                    displayField: 'countryname',
                    valueField: 'countryid',
                    allowBlank: true,
                    listeners: {
                        scope: this,
                        afterRender: function(me) {
                            me.setValue(9);
                        }
                    },
                    listConfig: {
                        maxHeight: 400
                    }
                },{
                    xtype: 'textfield',
                    fieldLabel: 'Fax',
                    inputType: 'number',
                    name: 'fax',
                    id: 'fax',
                    anchor: '100%',
                    margin: '10',
                    stripCharsRe: /[^0-9]/,
                    maskRe: /[0-9]/,
                    maxLength: 7,
                    enforceMaxLength: true,
                    allowBlank: true,
                    keyNavEnabled: false,
                    mouseWheelEnabled: false,
                    minValue: 0,
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
                    fieldLabel: 'Telephone',
                    inputType: 'number',
                    name: 'telephone',
                    id: 'telephone',
                    anchor: '100%',
                    margin: '10',
                    stripCharsRe: /[^0-9]/,
                    maskRe: /[0-9]/,
                    maxLength: 7,
                    enforceMaxLength: true,
                    allowBlank: true,
                    minValue: 0,
                    keyNavEnabled: false,
                    mouseWheelEnabled: false,
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
                    fieldLabel: 'Cellphone *',
                    inputType: 'number',
                    name: 'mobile',
                    id: 'Mobile',
                    anchor: '100%',
                    margin: '10',
                    stripCharsRe: /[^0-9]/,
                    maskRe: /[0-9]/,
                    maxLength: 12,
                    enforceMaxLength: true,
                    keyNavEnabled: false,
                    mouseWheelEnable: false,
                    minValue: 0,
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
                    fieldLabel: 'Email',
                    name: 'email',
                    id: 'email',
                    inputType: 'email',
                    anchor: '100%',
                    margin: '10',
                    vtype: 'email',
                    enforceMaxLength: true,
                    maxLength: 32,
                    allowBlank: true,
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
                    fieldLabel: 'Website',
                    name: 'website',
                    id: 'website',
                    anchor: '100%',
                    margin: '10',
                    enforceMaxLength: true,
                    maxLength: 32,
                    allowBlank: true,
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
                    fieldLabel: 'Contact Person *',
                    name: 'contactPerson',
                    id: 'contactPerson',
                    anchor: '100%',
                    margin: '10',
                    maskRe: /[A-Za-z\s+]/,
                    maxLength: 64,
                    enforceMaxLength: true,
                    triggers: {
                        clears: {
                            cls: 'x-form-clear-trigger',
                            handler: function() {
                                this.setValue('');
                            }
                        }
                    }
                },{
                    xtype: 'numberfield',
                    fieldLabel: 'Contact Number *',
                    inputType: 'number',
                    name: 'contactNumber',
                    id: 'contactNumber',
                    anchor: '100%',
                    margin: '10',
                    stripCharsRe: /[^0-9]/,
                    maskRe: /[0-9]/,
                    maxLength: 12,
                    enforceMaxLength: true,
                    keyNavEnabled: false,
                    mouseWheelEnabled: false,
                    minValue: 0
                },{
                    xtype: 'textfield',
                    fieldLabel: 'Emergency',
                    name: 'emergency',
                    id: 'emergency',
                    inputType: 'textfield',
                    anchor: '100%',
                    margin: '10',
                    maskRe: /[A-Za-z\s+.]/,
                    enforceMaxLength: true,
                    maxLength: 96,
                    allowBlank: true,
                    triggers: {
                        clears: {
                            cls: 'x-form-clear-trigger',
                            handler: function() {
                                this.setValue('');
                            }
                        }
                    }
                },{
                    xtype: 'combo',
                    fieldLabel: 'Fax Area Code *',
                    id: 'faxCode',
                    name: 'faxCode',
                    anchor: '100%',
                    margin: '10',
                    editable: false,
                    store: areaCodeStore,
                    queryMode: 'local',
                    displayField: 'area',
                    valueField: 'areaCodeId',
                    listeners: {
                        afterrender: function() {
                            this.setValue('2');
                        }
                    },
                    listConfig: {
                        maxHeight: 400
                    }
                },{
                    xtype: 'combo',
                    fieldLabel: 'Fax Country Code *',
                    id: 'faxCountryCode',
                    name: 'faxCountryCode',
                    anchor: '100%',
                    margin: '10',
                    editable: false,
                    store: countryCodeStore,
                    queryMode: 'local',
                    displayField:'country',
                    valueField: 'countryCodeId',
                    value: 1,
                    listeners: {
                        afterrender: function() {
                        }
                    },
                    listConfig: {
                        maxHeight: 400
                    }
                },{
                    xtype: 'combo',
                    fieldLabel: 'Area Code *',
                    id: 'areaCode',
                    name: 'areaCode',
                    anchor: '100%',
                    margin: '10',
                    editable: false,
                    store: areaCodeStore,
                    queryMode: 'local',
                    displayField: 'area',
                    valueField: 'areaCodeId',
                    listeners: {
                        afterrender: function() {
                            this.setValue('2');
                        }
                    },
                    listConfig: {
                        maxHeight: 400
                    }
                },{
                    xtype: 'combo',
                    fieldLabel: 'Country Code * ',
                    id: 'countryCode',
                    name: 'countryCode',
                    anchor: '100%',
                    margin: '10',
                    editable: false,
                    store: countryCodeStore,
                    queryMode: 'local',
                    displayField:'country',
                    valueField: 'countryCodeId',
                    value: 1,
                    listeners: {
                        scope: this,
                        afterRender: function(me) {
                            //me.setValue(63);
                        }
                    }
                },{
                    xtype: 'textfield',
                    fieldLabel: 'Zip',
                    inputType: 'number',
                    name: 'zip',
                    id: 'zip',
                    anchor: '70%',
                    margin: '10',
                    stripCharsRe: /[^0-9]/,
                    maskRe: /[0-9]/,
                    maxLength: 4,
                    enforceMaxLength: true,
                    keyNavEnabled: false,
                    mouseWheelEnable: false,
                    minValue: 0,
                    editable: false
                },{
                    xtype: 'combo',
                    fieldLabel: 'ER',
                    id: 'er',
                    name: 'er',
                    anchor: '90%',
                    margin: '10',
                    editable: false,
                    store: numberStore,
                    queryMode: 'local',
                    displayField:'percent',
                    valueField: 'percent',
                    value: 0,
                    listConfig: {
                        maxHeight: 400
                    }
                },{
                    xtype: 'combo',
                    fieldLabel: 'MF',
                    id: 'mf',
                    name: 'mf',
                    anchor: '90%',
                    margin: '10',
                    editable: false,
                    store: numberStore,
                    queryMode: 'local',
                    displayField: 'percent',
                    valueField: 'percent',
                    value: 0,
                    listConfig: {
                        maxHeight: 400
                    }
                },{
                    xtype: 'combo',
                    fieldLabel: 'Spare Parts',
                    id: 'spareParts',
                    name: 'spareParts',
                    anchor: '90%',
                    margin: '10',
                    editable: false,
                    store: numberStore,
                    queryMode: 'local',
                    displayField:'percent',
                    valueField: 'percent',
                    value: 0,
                    listConfig: {
                        maxHeight: 400
                    }
                },{
                    xtype: 'combo',
                    fieldLabel: 'Calib',
                    id: 'calib',
                    name: 'calib',
                    anchor: '90%',
                    margin: '10',
                    editable: false,
                    store: numberStore,
                    queryMode: 'local',
                    displayField: 'percent',
                    valueField: 'percent',
                    value: 0,
                    listConfig: {
                        maxHeight: 400
                    }
                },{
                    xtype: 'hidden',
                    name: 'username',
                    id: 'username',
                    value: onlineUserFromAddCustomer
                },{
                    xtype: 'hidden',
                    name: 'csaId',
                    id: 'csaId',
                    value: csaId
                },{
                    xtype: 'textfield',
                    emptyText: 'Set Company Location',
                    fieldLabel: 'Lat *',
                    name: 'lat',
                    id: 'lat',
                    margin: '10',
                    anchor: '100%',
                    editable: false,
                    value: '',
                    inputType: 'number'
                },{
                    xtype: 'textfield',
                    emptyText: 'Set Company Location',
                    fieldLabel: 'Lng *',
                    name: 'lng',
                    id: 'lng',
                    margin: '10',
                    anchor: '100%',
                    editable: false,
                    value: '',
                    inputType: 'number'
                },{
                    xtype: 'button',
                    id: 'mapButton',
                    text: 'Set Company Location',
                    cls: 'x-button',
                    height: 40,
                    anchor: '100%',
                    margin: '10',
                    padding: '1 1 1 1',
                    handler: function() {
                        setMap(Ext.getCmp('lat'), Ext.getCmp('lng'));
                    }
                },{
                    xtype: 'filefield',
                    fieldLabel: 'Photo *',
                    name: 'photo',
                    id: 'photo',
                    msgTarget: 'size',
                    anchor: '100%',
                    margin: '10',
                    buttonText: 'Select',
                    fieldCls: 'labeltextsize',
                    cls: 'photo-field',
                    clearOnSubmit: false,
                    listeners: {
                        afterrender: function() {
                            this.fileInputEl.set({
                                accept: 'image/*'
                            });
                        },

                        change: function() {
                            var form = this.up('form');

                            //  traverse
                            var file = form.getEl().down('input[type=file]').dom.files[0];

                            var _URL = window.URL || window.webkitURL;
                            var img = new Image();

                            img.onerror = function() {
                                Ext.Msg.alert('Warning!', 'Chosen file is not an image.');
                                Ext.getCmp('photo').inputEl.dom.value = '';
                            };

                            img.src = _URL.createObjectURL(file);

                            var fileSize = file.size;

                            if (file.type !== 'image/jpeg') {
                                Ext.Msg.alert('WARNING!', 'Photo should be jpeg.');
                                Ext.getCmp('photo').inputEl.dom.value = '';
                            }

                            if (fileSize > 512000) {
                                Ext.Msg.alert('Warning', 'Image too large. Image should be under 512 KB.');
                                Ext.getCmp('photo').inputEl.dom.value = '';
                            }
                        }
                    }
                },{
                    disabled: true,
                    xtype: 'button',
                    margin: '10',
                    anchor: '100%',
                    height: 40,
                    text: 'Add Company',
                    cls: 'x-button',
                    itemId: 'buttonToBind',
                    formBind: true,
                    handler: function() {
                        var form = this.up('form');

                        if (form.isValid()) {
                            form.submit({
                                waitMsg: 'Adding new company...',
                                params: {
                                    signStatus: true,
                                    signature: utilSignature
                                },
                                success: function(form, action) {
                                    try {
                                        var assoc = Ext.decode(action.response.responseText);
                                        Ext.Msg.alert('Success', assoc['reason']);
                                    } catch (err) {
                                        Ext.Msg.alert('Fail', err.toString());
                                    }

                                    form.reset();
                                    Ext.getCmp('photo').inputEl.dom.value = '';
                                },
                                failure: function(form, action) {
                                    try {
                                        var assoc = Ext.decode(action.response.responseText);
                                        Ext.Msg.alert('Fail', assoc['reason']);
                                    } catch(err) {
                                        Ext.Msg.alert('Fail', err.toString());
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
        location.assign('customer.jsp');
    });

    Ext.get('menuId').on('touchstart', function() {
        location.assign('home.jsp');
    });
});