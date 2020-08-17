var oldCompany,
    oldAddress,
    oldIndustry,
    oldPlant,
    oldCity,
    oldProvince,
    oldCountry,
    oldFax,
    oldTelephone,
    oldEmail,
    oldWebsite,
    oldContactPerson,
    oldContactNumber,
    oldEmergency,
    oldFaxAreaCode,
    oldFaxCountryCode,
    oldCountryCode,
    oldAreaCode,
    oldZip,
    oldEr,
    oldMf,
    oldCalib,
    oldSpareParts,
    oldLatitude,
    oldLongitude;

function editCompany(customerId) {
    Ext.create('Ext.Window', {
        id: 'editCustomerWindow',
        title: 'Edit Customer',
        width: 550,
        height: document.body.clientHeight * 0.95,
        minWidth: 550,
        minHeight: document.body.clientHeight * 0.95,
        layout: 'fit',
        plain: true,
        modal: true,
        items: [
            Ext.create('Ext.form.Panel', {
                region: 'center',
                id: 'editForm',
                autoScroll: true,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: 'Company',
                    name: 'company',
                    id: 'company',
                    inputType: 'textfield',
                    width: '90%',
                    margin: 10,
                    enforceMaxLength: true,
                    maxLength: 100,
                    minLength: 3
                },{
                    xtype: 'textareafield',
                    grow: true,
                    fieldLabel: 'Address',
                    name: 'address',
                    id: 'address',
                    inputType: 'textareafield',
                    width: '90%',
                    margin: '10'
                },{
                    xtype: 'combo',
                    fieldLabel: 'Plant',
                    id: 'plant',
                    name: 'plant',
                    width: '90%',
                    margin: '10',
                    editable: false,
                    store: plantStore,
                    queryMode: 'local',
                    displayField:'plant_name',
                    valueField: 'plant_id',
                    listConfig: { maxHeight: 400 }
                },{
                    xtype: 'combo',
                    fieldLabel: 'City',
                    id: 'cityId',
                    name: 'cityId',
                    width: '90%',
                    margin: '10',
                    editable: false,
                    store: cityIdStore,
                    queryMode: 'local',
                    displayField:'cityname',
                    valueField: 'cityid',
                    listConfig: {
                        maxHeight: 250
                    },
                    listeners: {
                        'change': function(field, selectedValue) {
                            setZip(selectedValue);
                        }
                    }
                },{
                    xtype: 'combo',
                    fieldLabel: 'Province',
                    id: 'province',
                    name: 'province',
                    width: '90%',
                    margin: '10',
                    editable: false,
                    store: provinceStore,
                    queryMode: 'local',
                    displayField: 'provincename',
                    valueField: 'provinceid',
                    listConfig: { maxHeight: 250 }
                },{
                    xtype: 'combo',
                    fieldLabel: 'Country',
                    id: 'countryId',
                    name: 'countryId',
                    width: '90%',
                    margin: '10',
                    editable: false,
                    store: countryIdStore,
                    queryMode: 'local',
                    displayField:'countryname',
                    valueField: 'countryid',
                    listConfig: { maxHeight: 250 }
                },{
                    xtype: 'numberfield',
                    fieldLabel: 'Fax Number',
                    name: 'fax',
                    id: 'fax',
                    width: '65%',
                    margin: '10',
                    stripCharsRe: /[^0-9]/,
                    maskRe: /[0-9]/,
                    maxLength: 7,
                    enforceMaxLength: true,
                    allowBlank: true,
                    keyNavEnabled: false,
                    mouseWheelEnabled: false,
                    minValue: 0
                },{
                    xtype: 'numberfield',
                    fieldLabel: 'Telephone',
                    name: 'telNum',
                    id: 'telNum',
                    width: '65%',
                    margin: '10',
                    stripCharsRe: /[^0-9]/,
                    maskRe: /[0-9]/,
                    maxLength: 7,
                    enforceMaxLength: true,
                    allowBlank: true,
                    keyNavEnabled: false,
                    mouseWheelEnabled: false,
                    minValue: 0
                },{
                    xtype: 'textfield',
                    fieldLabel: 'Email',
                    name: 'email',
                    id: 'email',
                    inputType: 'email',
                    width: '70%',
                    margin: '10',
                    enforceMaxLength: true,
                    maxLength: 32,
                    allowBlank: true,
                    vtype: 'email'
                },{
                    xtype: 'textfield',
                    fieldLabel: 'Website',
                    name: 'website',
                    id: 'website',
                    width: '70%',
                    margin: '10',
                    enforceMaxLength: true,
                    maxLength: 32,
                    allowBlank: true
                },{
                    xtype: 'textfield',
                    fieldLabel: 'Contact Person',
                    name: 'contactPerson',
                    id: 'contactPerson',
                    width: '90%',
                    margin: '10',
                    maskRe: /[A-Za-z\s+]/,
                    maxLength: 64,
                    enforceMaxLength: true
                },{
                    xtype: 'textfield',
                    fieldLabel: 'Contact Number',
                    inputType: 'number',
                    name: 'contactNumber',
                    id: 'contactNumber',
                    width: '65%',
                    margin: '10',
                    stripCharsRe: /[^0-9]/,
                    maskRe: /[0-9]/,
                    maxLength: 12,
                    enforceMaxLength: true,
                    fieldCls: 'biggertext',
                    allowBlank: true,
                    keyNavEnabled: false,
                    mouseWheelEnabled: false,
                    minValue: 0
                },{
                    xtype: 'textfield',
                    fieldLabel: 'Emergency',
                    name: 'emergency',
                    id: 'emergency',
                    inputType: 'textfield',
                    width: '90%',
                    margin: '10',
                    maskRe: /[A-Za-z\s+.]/,
                    enforceMaxLength: true,
                    maxLength: 96,
                    allowBlank: true
                },{
                    xtype: 'combo',
                    fieldLabel: 'Fax Area Code',
                    id: 'faxAreaCode',
                    name: 'faxAreaCode',
                    width: '90%',
                    margin: '10',
                    editable: false,
                    store: areaCodeStore,
                    queryMode: 'local',
                    displayField:'area',
                    valueField: 'areaCodeId'
                },{
                    xtype: 'combo',
                    fieldLabel: 'Fax Country Code',
                    id: 'faxCountryCode',
                    name: 'faxCountryCode',
                    width: '70%',
                    margin: '10',
                    editable: false,
                    store: countryCodeStore,
                    queryMode: 'local',
                    displayField:'country',
                    valueField: 'countryCodeId'
                },{
                    xtype: 'combo',
                    fieldLabel: 'Country Code',
                    id: 'countryCode',
                    name: 'countryCode',
                    width: '70%',
                    margin: '10',
                    editable: false,
                    store: countryCodeStore,
                    queryMode: 'local',
                    displayField:'country',
                    valueField: 'countryCodeId'
                },{
                    xtype: 'combo',
                    fieldLabel: 'Area Code',
                    id: 'areaCode',
                    name: 'areaCode',
                    width: '70%',
                    margin: '10',
                    editable: false,
                    store: areaCodeStore,
                    queryMode: 'local',
                    displayField:'area',
                    valueField: 'areaCodeId',
                    fieldCls: 'biggertext',
                    listConfig: { maxHeight: 250 }
                },{
                    xtype: 'textfield',
                    fieldLabel: 'Zip',
                    name: 'zip',
                    id: 'zip',
                    width: '45%',
                    margin: '10',
                    editable: false
                },{
                    xtype: 'combo',
                    fieldLabel: 'ER (%)',
                    id: 'er',
                    name: 'er',
                    width: '45%',
                    margin: '10',
                    editable: false,
                    store: numberStore,
                    queryMode: 'local',
                    displayField:'percent',
                    valueField: 'percent',
                    listConfig: { maxHeight: 400 }
                },{
                    xtype: 'combo',
                    fieldLabel: 'MF (%)',
                    id: 'mfspgm',
                    name: 'mfspgm',
                    width: '45%',
                    margin: '10',
                    editable: false,
                    store: numberStore,
                    queryMode: 'local',
                    displayField:'percent',
                    valueField: 'percent',
                    listConfig: { maxHeight: 250 }
                },{
                    xtype: 'combo',
                    fieldLabel: 'Spare Parts (%)',
                    id: 'spareParts',
                    name: 'spareParts',
                    width: '45%',
                    margin: '10',
                    editable: false,
                    store: numberStore,
                    queryMode: 'local',
                    displayField:'percent',
                    valueField: 'percent',
                    listConfig: { maxHeight: 250 }
                },{
                    xtype: 'combo',
                    fieldLabel: 'Calib (%)',
                    id: 'calib',
                    name: 'calib',
                    width: '45%',
                    margin: '10',
                    editable: false,
                    store: numberStore,
                    queryMode: 'local',
                    displayField:'percent',
                    valueField: 'percent',
                    listConfig: { maxHeight: 250 }
                },{
                    xtype: 'textfield',
                    fieldLabel: 'Latitude',
                    name: 'latitude',
                    id: 'latitude',
                    margin: '10',
                    width: '80%',
                    maskRe: /[0-9.]/,
                    enforceMaxLength: true,
                    maxLength: 20
                },{
                    xtype: 'textfield',
                    fieldLabel: 'Longitude',
                    name: 'longitude',
                    id: 'longitude',
                    margin: '10',
                    width: '80%',
                    maskRe: /[0-9.]/,
                    enforceMaxLength: true,
                    maxLength: 20
                }],
                buttons: [{
                    text: 'Location',
                    id: 'location',
                    handler: function() {
                        setMap(Ext.getCmp('latitude'), Ext.getCmp('longitude'));
                    }
                },{
                    text: 'Update Photo',
                    handler: function() {
                        Ext.getCmp('editForm').close();
                        Ext.getCmp('editCustomerWindow').close();
                        updateCompanyPhoto(customerId);
                    }
                }, '->', {
                    text: 'Submit',
                    formBind: true,
                    disabled: true,
                    handler: function() {

                        Ext.MessageBox.show({
                            msg: 'Update',
                            progressText: 'Updating...',
                            width: 300,
                            wait: true,
                            waitConfig:
                                {
                                    duration: 60000,
                                    text: 'Updating...',
                                    scope: this,
                                    fn: function() {
                                        Ext.MessageBox.hide();
                                    }
                                }
                        });

                        var form = this.up('form');

                        if (form.isValid()) {
                            form.submit({
                                waitMsg: 'Saving new contact...',
                                method: 'post',
                                url: 'updatecompany',
                                params: {
                                    oldCompany: oldCompany,
                                    oldAddress: oldAddress,
                                    oldIndustry: oldIndustry,
                                    oldPlant: oldPlant,
                                    oldCity: oldCity,
                                    oldProvince: oldProvince,
                                    oldCountry: oldCountry,
                                    oldFaxNumber: oldFax,
                                    oldTelephone: oldTelephone,
                                    oldEmail: oldEmail,
                                    oldWebsite: oldWebsite,
                                    oldContactPerson: oldContactPerson,
                                    oldContactNumber: oldContactNumber,
                                    oldEmergency: oldEmergency,
                                    oldFaxAreaCode: oldFaxAreaCode,
                                    oldFaxCountryCode: oldFaxCountryCode,
                                    oldCountryCode: oldCountryCode,
                                    oldAreaCode: oldAreaCode,
                                    oldZipCode: oldZip,
                                    oldEr: oldEr,
                                    oldMf: oldMf,
                                    oldCalib: oldCalib,
                                    oldSpareParts: oldSpareParts,
                                    oldLatitude: oldLatitude,
                                    oldLongitude: oldLongitude,
                                    customerId: customerId
                                },
                                success: function (form, action) {
                                    var assoc = Ext.JSON.decode(action.response.responseText);

                                    Ext.MessageBox.hide();
                                    Ext.Msg.alert("Success", assoc['reason']);

                                    customerCompanyStore.load({ url: 'getcustomercompany' });

                                    Ext.getCmp('editForm').destroy();
                                    Ext.getCmp('editCustomerWindow').destroy();
                                },
                                failure: function (form, action) {
                                    var assoc = Ext.JSON.decode(action.response.responseText);
                                    Ext.MessageBox.hide();
                                    Ext.Msg.alert("Failed", assoc['reason']);
                                }
                            });
                        }
                    }
                }, {
                    text: 'Close',
                    handler: function () {
                        Ext.getCmp('editForm').destroy();
                        Ext.getCmp('editCustomerWindow').destroy();
                    }
                }]
            })
        ]
    }).show();

    Ext.Ajax.request({
        url: 'getcustomercompanybyparams',
        method: 'POST',
        params: { customerId: customerId },
        success: function(response, opts) {
            var companyJson = Ext.decode(response.responseText);
            console.log(companyJson);

            oldCompany = companyJson.company;
            oldAddress = companyJson.address;
            oldIndustry = companyJson.industr;
            oldPlant = companyJson.rawPlant;
            oldCity = companyJson.rawCity;
            oldProvince = companyJson.province;
            oldCountry = companyJson.rawCountry;
            oldFax = companyJson.rawFax;
            oldTelephone = companyJson.rawTelephone;
            oldEmail = companyJson.email;
            oldWebsite = companyJson.website;
            oldContactPerson = companyJson.contactperson;
            oldContactNumber = companyJson.contactnumber;
            oldEmergency = companyJson.emergency;
            oldFaxAreaCode = companyJson.faxareacode;
            oldFaxCountryCode = companyJson.faxcountrycode;
            oldCountryCode = companyJson.countrycode;
            oldAreaCode = companyJson.areacode;
            oldZip = companyJson.zip;
            oldEr = companyJson.er;
            oldMf = companyJson.mf;
            oldCalib = companyJson.calib;
            oldSpareParts = companyJson.spareParts;
            oldLatitude = companyJson.latitude;
            oldLongitude = companyJson.longitude;

            Ext.getCmp('company').setValue(oldCompany);
            Ext.getCmp('address').setValue(oldAddress);
            Ext.getCmp('plant').setValue(oldPlant);
            Ext.getCmp('cityId').setValue(oldCity);
            Ext.getCmp('province').setValue(oldProvince);
            Ext.getCmp('countryId').setValue(oldCountry);
            Ext.getCmp('fax').setValue(oldFax);
            Ext.getCmp('telNum').setValue(oldTelephone);
            Ext.getCmp('email').setValue(oldEmail);
            Ext.getCmp('website').setValue(oldWebsite);
            Ext.getCmp('contactPerson').setValue(oldContactPerson);
            Ext.getCmp('contactNumber').setValue(oldContactNumber);
            Ext.getCmp('emergency').setValue(oldEmergency);
            Ext.getCmp('faxAreaCode').setValue(oldFaxAreaCode);
            Ext.getCmp('faxCountryCode').setValue(oldFaxCountryCode);
            Ext.getCmp('countryCode').setValue(oldCountryCode);
            Ext.getCmp('areaCode').setValue(oldAreaCode);
            Ext.getCmp('zip').setValue(oldZip);
            Ext.getCmp('er').setValue(oldEr);
            Ext.getCmp('mfspgm').setValue(oldMf);
            Ext.getCmp('spareParts').setValue(oldSpareParts);
            Ext.getCmp('calib').setValue(oldCalib);
            Ext.getCmp('latitude').setValue(oldLatitude);
            Ext.getCmp('longitude').setValue(oldLongitude);
        },
        failure: function(response, opts) {
            var failJson = Ext.decode(response.responseText);
            Ext.MessageBox.show('Failed', failJson['reason']);
        }
    });
}

function updateCompanyPhoto(customerId) {
    Ext.create('Ext.Window', {
        id: 'updatePhotoWindow',
        title: 'Update Photo',
        width: 300,
        height: 170,
        layout: 'fit',
        plain: true,
        modal: true,
        items: [
            Ext.create('Ext.form.Panel', {
                region: 'center',
                id: 'updatePhotoForm',
                autoScroll: true,
                items: [{
                    xtype: 'filefield',
                    id: 'customerPhoto',
                    name: 'customerPhoto',
                    msgTarget: 'size',
                    anchor: '100%',
                    margin: '20 20 10 20',
                    buttonText: 'Select',
                    fieldCls: 'labeltextsize',
                    clearOnSubmit: false,
                    listeners: {
                        afterrender: function () {
                            this.fileInputEl.set({
                                accept: 'image/*'
                            });
                        },
                        change: function() {
                            var form = this.up('form');
                            var file = form.getEl().down('input[type=file]').dom.files[0];
                            var _URL = window.URL || window.webkitURL;
                            var img = new Image();

                            img.onload = function() {
                                if (this.width < 400 || this.height < 400) {
                                    Ext.Msg.alert('Warning!', 'Photo is too small.');
                                    Ext.getCmp('customerPhoto').inputEl.dom.value = '';
                                }
                            };

                            img.onerror = function() {
                                Ext.Msg.alert('Warning!', 'Chosen file is not an image.');
                                Ext.getCmp('customerPhoto').inputEl.dom.value = '';
                            };

                            img.src = _URL.createObjectURL(file);
                            var fileSize = file.size;

                            if (file.type !== 'image/jpeg') {
                                Ext.Msg.alert('WARNING!', 'Photo should be jpeg.');
                                Ext.getCmp('customerPhoto').inputEl.dom.value = '';
                            }

                            if (fileSize > MAX_IMAGE_SIZE) {
                                Ext.Msg.alert("Warning", "Selected image too big.");
                                Ext.getCmp('customerPhoto').inputEl.dom.value = '';
                            }
                        }
                    }
                },{
                    xtype: 'button',
                    margin: '0 20 20 20',
                    anchor: '100%',
                    height: 30,
                    text: 'Submit',
                    formBind: true,
                    handler: function() {
                        var form = this.up('form');
                        if (form.isValid()) {
                            form.submit({
                                waitMsg: 'Updating Photo...',
                                method: 'post',
                                url: 'updatecustomerphoto',
                                params: {'customerId': customerId},
                                success: function(form, action) {
                                    var assoc = Ext.JSON.decode(action.response.responseText);
                                    Ext.getCmp('updatePhotoForm').close();
                                    Ext.getCmp('updatePhotoWindow').close();
                                    Ext.Msg.alert('Success', assoc['reason']);
                                },
                                failure: function(form, action) {
                                    var assoc = Ext.JSON.decode(action.response.responseText);
                                    Ext.Msg.alert('Fail', assoc['reason']);
                                }
                            });
                        } else {
                            Ext.Msg.alert("Error", 'Form is not valid.');
                            console.log('Form is not valid.');
                        }
                    }
                }]
            })
        ]
    }).show();
}