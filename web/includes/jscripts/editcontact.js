var oldFirstname,
    oldLastname,
    oldMi,
    oldCompany,
    oldJobPosition,
    oldAddress,
    oldYear,
    oldMonth,
    oldDay,
    oldIndustry,
    oldPlantAssociated,
    oldCity,
    oldProvince,
    oldCountry,
    oldFaxNumber,
    oldTelephone,
    oldMobileNumber,
    oldEmail,
    oldWebsite,
    oldEmergencyContact,
    oldEmergency,
    oldFaxAreaCode,
    oldAreaCode,
    oldZipCode,
    oldEr,
    oldMf,
    oldCalib,
    oldSpareParts,
    oldLatitude,
    oldLongitude;

function editContact(contactId) {
    Ext.create('Ext.Window', {
        id: 'editContactWindow',
        title: 'Edit Contact',
        width: 500,
        height: document.body.clientHeight * 0.95,
        minWidth: 500,
        minHeight: document.body.clientHeight * 0.95,
        layout: 'fit',
        plain: true,
        modal: true,
        items: [
            Ext.create('Ext.form.Panel', {
                region: 'center',
                id: 'editContactForm',
                autoScroll: true,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: 'Lastname',
                    name: 'lastname',
                    id: 'lastname',
                    inputType: 'textfield',
                    width: '90%',
                    margin: '10',
                    maskRe: /[A-Za-z\s+]/,
                    enforceMaxLength: true,
                    maxLength: 32
                },{
                    xtype: 'textfield',
                    fieldLabel: 'Firstname',
                    name: 'firstname',
                    id: 'firstname',
                    inputType: 'textfield',
                    width: '90%',
                    margin: '10',
                    maskRe: /[A-Za-z\s+]/,
                    enforceMaxLength: true,
                    maxLength: 64
                },{
                    xtype: 'textfield',
                    fieldLabel: 'Middle Initial',
                    name: 'mi',
                    id: 'mi',
                    inputType: 'textfield',
                    width: '30%',
                    margin: '10',
                    maskRe: /[A-Za-z]/,
                    enforceMaxLength: true,
                    maxLength: 1
                },{
                    xtype: 'textfield',
                    fieldLabel: 'Company',
                    name: 'company',
                    id: 'company',
                    inputType: 'textfield',
                    width: '90%',
                    margin: '10',
                    enforceMaxLength: true,
                    maxLength: 100,
                    minLength: 3
                },{
                    xtype: 'textfield',
                    fieldLabel: 'Job Position',
                    id: 'jobPosition',
                    name: 'jobPosition',
                    width: '75%',
                    margin: '10',
                    maxLength: 64,
                    enforceMaxLength: true
                },{
                    xtype: 'textareafield',
                    fieldLabel: 'Address',
                    grow: true,
                    name: 'address',
                    id: 'address',
                    inputType: 'textareafield',
                    width: '90%',
                    margin: '10'
                },{
                    xtype: 'datefield',
                    width: '50%',
                    margin: '10',
                    fieldLabel: 'Date of Birth',
                    id: 'dateofbirth',
                    format: 'Y-m-d',
                    value: new Date()
                },{
                    xtype: 'combo',
                    fieldLabel: 'Plant Associated',
                    id: 'plant',
                    name: 'plant',
                    width: '75%',
                    margin: '10',
                    editable: false,
                    store: plantStore,
                    queryMode: 'local',
                    displayField: 'plant_name',
                    valueField: 'plant_id',
                    listConfig: { maxHeight: 400 }
                },{
                    xtype: 'combo',
                    fieldLabel: 'City',
                    id: 'city',
                    name: 'city',
                    width: '75%',
                    margin: '10',
                    editable: false,
                    store: cityIdStore,
                    queryMode: 'local',
                    displayField:'cityname',
                    valueField: 'cityid',
                    listConfig: {
                        maxHeight: 400
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
                    width: '75%',
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
                    fieldLabel: 'Country',
                    id: 'country',
                    name: 'country',
                    width: '75%',
                    margin: '10',
                    editable: false,
                    store: countryIdStore,
                    queryMode: 'local',
                    displayField:'countryname',
                    valueField: 'countryid',
                    listConfig: { maxHeight: 400 }
                },{
                    xtype: 'numberfield',
                    fieldLabel: 'Fax Number',
                    name: 'fax',
                    id: 'fax',
                    width: '60%',
                    margin: '10',
                    stripCharsRe: /[^0-9]/,
                    maskRe: /[0-9]/,
                    maxLength: 7,
                    enforceMaxLength: true,
                    fieldCls: 'biggertext',
                    keyNavEnabled: false,
                    mouseWheelEnabled: false,
                    minValue: 0
                },{
                    xtype: 'numberfield',
                    fieldLabel: 'Telephone',
                    name: 'telephone',
                    id: 'telephone',
                    width: '60%',
                    margin: '10',
                    stripCharsRe: /[^0-9]/,
                    maskRe: /[0-9]/,
                    maxLength: 7,
                    enforceMaxLength: true,
                    minValue: 0,
                    keyNavEnabled: false,
                    mouseWheelEnabled: false
                },{
                    xtype: 'numberfield',
                    fieldLabel: 'Mobile Number',
                    name: 'mobile',
                    id: 'mobile',
                    width: '60%',
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
                    fieldLabel: 'Email',
                    name: 'email',
                    id: 'email',
                    inputType: 'email',
                    width: '75%',
                    margin: '10',
                    enforceMaxLength: true,
                    maxLength: 32,
                    vtype: 'email'
                },{
                    xtype: 'textfield',
                    fieldLabel: 'Website',
                    name: 'website',
                    id: 'website',
                    width: '75%',
                    margin: '10',
                    enforceMaxLength: true,
                    maxLength: 32
                },{
                    xtype: 'numberfield',
                    fieldLabel: 'Emergency Contact',
                    name: 'emergencyContact',
                    id: 'emergencyContact',
                    width: '75%',
                    margin: '10',
                    stripCharsRe: /[^0-9]/,
                    maskRe: /[0-9]/,
                    maxLength: 12,
                    enforceMaxLength: true,
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
                    maxLength: 96
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
                    valueField: 'areaCodeId',
                    listConfig: { maxHeight: 250 }
                },{
                    xtype: 'combo',
                    fieldLabel: 'Area Code',
                    id: 'areaCode',
                    name: 'areaCode',
                    width: '90%',
                    margin: '10',
                    editable: false,
                    store: areaCodeStore,
                    queryMode: 'local',
                    displayField:'area',
                    valueField: 'areaCodeId',
                    listConfig: { maxHeight: 400 }
                },{
                    xtype: 'textfield',
                    fieldLabel: 'Zip Code',
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
                    id: 'mf',
                    name: 'mf',
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
                    listConfig: { maxHeight: 400 }
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
                    fieldCls: 'biggertext',
                    listConfig: { maxHeight: 400 }
                },{
                    xtype: 'textfield',
                    fieldLabel: 'Latitude',
                    name: 'latitude',
                    id: 'latitude',
                    margin: '10',
                    width: '70%',
                    maskRe: /[0-9.]/,
                    enforceMaxLength: true,
                    maxLength: 20
                }, {
                    xtype: 'textfield',
                    fieldLabel: 'Longitude',
                    name: 'longitude',
                    id: 'longitude',
                    margin: '10',
                    width: '70%',
                    maskRe: /[0-9.]/,
                    enforceMaxLength: true,
                    maxLength: 20
                }],
                buttons:[{
                    text: 'Location',
                    handler: function () {
                        setMap(Ext.getCmp('latitude'), Ext.getCmp('longitude'));
                    }
                },{
                    text: 'Update Photo',
                    handler: function() {
                        Ext.getCmp('editContactForm').close();
                        Ext.getCmp('editContactWindow').close();
                        updateContactPhoto(contactId);
                    }
                },'-',{
                    text: 'Submit',
                    formBind: true,
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
                            var dateArray = Ext.getCmp('dateofbirth').getSubmitValue().split("-");

                            form.submit({
                                waitMsg: 'Saving new contact...',
                                method: 'post',
                                url: 'updatecontact',
                                params: {
                                    oldFirstname: oldFirstname,
                                    oldLastname: oldLastname,
                                    oldMi: oldMi,
                                    oldCompany: oldCompany,
                                    oldJobPosition: oldJobPosition,
                                    oldAddress: oldAddress,
                                    oldYear: oldYear,
                                    oldMonth: oldMonth,
                                    oldDay: oldDay,
                                    oldIndustry: oldIndustry,
                                    oldPlantAssociated: oldPlantAssociated,
                                    oldCity: oldCity,
                                    oldProvince: oldProvince,
                                    oldCountry: oldCountry,
                                    oldFaxNumber: oldFaxNumber,
                                    oldTelephone: oldTelephone,
                                    oldMobileNumber: oldMobileNumber,
                                    oldEmail: oldEmail,
                                    oldWebsite: oldWebsite,
                                    oldEmergencyContact: oldEmergencyContact,
                                    oldEmergency: oldEmergency,
                                    oldFaxAreaCode: oldFaxAreaCode,
                                    oldAreaCode: oldAreaCode,
                                    oldZipCode: oldZipCode,
                                    oldEr: oldEr,
                                    oldMf: oldMf,
                                    oldCalib: oldCalib,
                                    oldSpareParts: oldSpareParts,
                                    oldLatitude: oldLatitude,
                                    oldLongitude: oldLongitude,
                                    contactId: contactId,
                                    year: dateArray[0],
                                    month: dateArray[1],
                                    day: dateArray[2]
                                },
                                success: function (form, action) {
                                    var assoc = Ext.JSON.decode(action.response.responseText);

                                    Ext.MessageBox.hide();
                                    Ext.Msg.alert("Success", assoc['reason']);

                                    contactsStore.load({ url: 'getcontacts' });

                                    Ext.getCmp('editContactForm').close();
                                    Ext.getCmp('editContactWindow').close();
                                },
                                failure: function (form, action) {
                                    var assoc = Ext.JSON.decode(action.response.responseText);
                                    Ext.MessageBox.hide();
                                    Ext.Msg.alert("Failed", assoc['reason']);
                                }
                            });
                        }
                    }
                },{
                    text: 'Close',
                    handler: function () {
                        Ext.getCmp('editContactForm').close();
                        Ext.getCmp('editContactWindow').close();
                    }
                }]
            })
        ]
    }).show();

    Ext.Ajax.request({
        url: 'getcontactsbyparams',
        method: 'POST',
        params: { contactId: contactId },
        success: function(response, opts) {
            var contactJson = Ext.decode(response.responseText);
            var oldDateArray = contactJson.dateOfBirth.split("-");

            oldLastname = contactJson.lastname;
            oldFirstname = contactJson.firstname;
            oldMi = contactJson.mi;
            oldCompany = contactJson.company;
            oldJobPosition = contactJson.jobPosition;
            oldAddress = contactJson.address;
            oldPlant = contactJson.plantId;
            oldCity = contactJson.cityId;
            oldProvince = contactJson.provinceId;
            oldCountry = contactJson.countryId;
            oldFaxNumber = contactJson.faxNumber;
            oldTelephone = contactJson.telNum;
            oldMobileNumber = contactJson.mobile;
            oldEmail = contactJson.email;
            oldWebsite = contactJson.website;
            oldEmergencyContact = contactJson.emergencyContact;
            oldEmergency = contactJson.emergency;
            oldFaxAreaCode = contactJson.faxCode;
            oldAreaCode = contactJson.areaCode;
            oldZipCode = contactJson.zipCode;
            oldEr = contactJson.er;
            oldMf = contactJson.mf;
            oldCalib = contactJson.calib;
            oldSpareParts = contactJson.spareParts;
            oldLatitude = contactJson.latitude;
            oldLongitude = contactJson.longitude;
            oldYear = oldDateArray[0];
            oldMonth = oldDateArray[1];
            oldDay = oldDateArray[2];
            oldIndustry = contactJson.industryId;
            oldPlantAssociated = contactJson.plantAssociated;

            Ext.getCmp('lastname').setValue(oldLastname);
            Ext.getCmp('firstname').setValue(oldFirstname);
            Ext.getCmp('mi').setValue(oldMi);
            Ext.getCmp('company').setValue(oldCompany);
            Ext.getCmp('jobPosition').setValue(oldJobPosition);
            Ext.getCmp('address').setValue(oldAddress);
            Ext.getCmp('plant').setValue(oldPlant);
            Ext.getCmp('city').setValue(oldCity);
            Ext.getCmp('province').setValue(oldProvince);
            Ext.getCmp('country').setValue(oldCountry);
            Ext.getCmp('fax').setValue(oldFaxNumber);
            Ext.getCmp('telephone').setValue(oldTelephone);
            Ext.getCmp('mobile').setValue(oldMobileNumber);
            Ext.getCmp('email').setValue(oldEmail);
            Ext.getCmp('website').setValue(oldWebsite);
            Ext.getCmp('emergencyContact').setValue(oldEmergencyContact);
            Ext.getCmp('emergency').setValue(oldEmergency);
            Ext.getCmp('faxAreaCode').setValue(oldFaxAreaCode);
            Ext.getCmp('areaCode').setValue(oldAreaCode);
            Ext.getCmp('zip').setValue(oldZipCode);
            Ext.getCmp('er').setValue(oldEr);
            Ext.getCmp('mf').setValue(oldMf);
            Ext.getCmp('calib').setValue(oldCalib);
            Ext.getCmp('spareParts').setValue(oldSpareParts);
            Ext.getCmp('latitude').setValue(oldLatitude);
            Ext.getCmp('longitude').setValue(oldLongitude);
            Ext.getCmp('dateofbirth').setValue(contactJson.dateOfBirth);
        },
        failure: function(response) {
            var assoc = Ext.decode(response.responseText);
            Ext.Msg.alert("Failed", assoc['reason']);
        }
    });
}

function updateContactPhoto(contactId) {
    var updatePhotoForm = Ext.create('Ext.form.Panel', {
        region: 'center',
        id: 'updatePhotoForm',
        autoScroll: true,
        items: [{
            xtype: 'filefield',
            id: 'contactPhoto',
            name: 'contactPhoto',
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
                            Ext.getCmp('contactPhoto').inputEl.dom.value = '';
                        }
                    };

                    img.onerror = function() {
                        Ext.Msg.alert('Warning!', 'Chosen file is not an image.');
                        Ext.getCmp('contactPhoto').inputEl.dom.value = '';
                    };

                    img.src = _URL.createObjectURL(file);
                    var fileSize = file.size;

                    if (file.type !== 'image/jpeg')
                    {
                        Ext.Msg.alert('WARNING!', 'Photo should be jpeg.');
                        Ext.getCmp('contactPhoto').inputEl.dom.value = '';
                    }

                    if (fileSize > MAX_IMAGE_SIZE)
                    {
                        Ext.Msg.alert("Warning", "Selected image too big.");
                        Ext.getCmp('contactPhoto').inputEl.dom.value = '';
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
                        url: 'updatecontactphoto',
                        params: {'contactId': contactId},
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
    });

    Ext.create('Ext.Window', {
        id: 'updatePhotoWindow',
        title: 'Update Photo',
        width: 300,
        height: 170,
        layout: 'fit',
        plain: true,
        modal: true,
        items: [updatePhotoForm]
    }).show();
}