var currentDate = new Date();

Ext.define('Industry', {
    extend : 'Ext.data.Model',
    fields : [
        { name : 'industryname', type : 'string' },
        { name : 'industryid', type : 'int' }
    ]
});

Ext.define('Plant', {
    extend : 'Ext.data.Model',
    fields : [
        { name : 'plant_id', type : 'int' },
        { name : 'plant_name', type : 'string' }
    ]
});

Ext.define('City', {
    extend : 'Ext.data.Model',
    fields : [
        { name : 'cityname', type : 'string'},
        { name : 'cityid', type : 'int' }
    ]
});

Ext.define('Province', {
    extend : 'Ext.data.Model',
    fields : [
        { name : 'provincename', type : 'string' },
        { name : 'provinceid', type : 'string' }
    ]
});

Ext.define('Country', {
    extend : 'Ext.data.Model',
    fields : [
        { name : 'countryname', type : 'string' },
        { name : 'countryid', type : 'int' }
    ]
});

Ext.define('CountryCode', {
    extend : 'Ext.data.Model',
    fields : [
        { name : 'country', type : 'string' },
        { name : 'code', type : 'int' },
        { name : 'countryCodeId', type : 'int' }
    ]
});

Ext.define('AreaCode', {
    extend : 'Ext.data.Model',
    fields : [
        { name : 'area', type : 'string' },
        { name : 'code', type : 'int' },
        { name : 'areaCodeId', type : 'int' }
    ]
});

var numberStore = Ext.create('Ext.data.Store', {
    fields : ['percent'],
    data : getNumberObjects()
});

function openEditCustomerMap() {
    try {
        var resizedWidth = document.body.clientWidth * 0.70;
        var resizedHeight = document.body.clientHeight * 0.95;

        //  this component is asynchronous
        Ext.define('Customer.Map', {
            extend: 'Ext.panel.Panel',
            alias : 'widget.smartcitymaps',
            itemId: 'map',
            width : '100%',
            //margin : '20',
            border: false,
            html : "<div style=\"width:"+(resizedWidth)+"px; height:"+(resizedHeight - 90)+"px\" id=\"myMap\"></div>",
            constructor: function(c) {
                var me = this;
                var marker;
                var loadMap = function(lat, lng) {
                    var me = this;
                    var location = { lat : lat, lng : lng};

                    try {
                        me.map = new google.maps.Map(document.getElementById("myMap"), {
                            clickableIcons : false,
                            zoom: 13,
                            center: new google.maps.LatLng(lat, lng),
                            mapTypeId: google.maps.MapTypeId.ROADMAP
                        });
                    } catch (e) {
                        return false;   //  important
                    }

                    me.infowindow = new google.maps.InfoWindow();
                    //me.infowindow.setContent(entity);
                    //me.infowindow.open(me.map, marker);

                    marker = new google.maps.Marker({
                        position : new google.maps.LatLng(lat, lng),
                        map : me.map
                    });

                    google.maps.event.addListener(me.map, 'click', function(e) {
                        marker.setMap(null);
                        marker = new google.maps.Marker({
                            position : new google.maps.LatLng(e.latLng.lat(), e.latLng.lng()),
                            map : me.map
                        });

                        Ext.getCmp('latitude').setValue(e.latLng.lat());
                        Ext.getCmp('longitude').setValue(e.latLng.lng());
                    });

                    google.maps.event.trigger(me.map, 'resize');
                };

                me.listeners = {
                    afterrender: function() {
                        loadMap(10.3157, 123.8854);
                    }
                };

                me.callParent(arguments);
            }
        });

        Ext.create('Ext.Window', {
            id : 'mapWindow',
            title : 'Google Maps',
            width : resizedWidth,
            height : resizedHeight,
            minWidth : resizedWidth,
            minHeight : resizedHeight,
            layout : 'fit',
            plain : true,
            modal : true,
            items : [Ext.create('Customer.Map', {
                width : '100%',
                height : '95%',
                id : 'mapPanel'
            })],
            buttons : [{
                text : 'Close',
                handler : function() {
                    Ext.getCmp('mapPanel').destroy();
                    Ext.getCmp('mapWindow').destroy();
                }
            }]
        }).show();

        return true;

    } catch (e) {

        Ext.getCmp('mapPanel').destroy();
        Ext.getCmp('mapWindow').destroy();

        console.log(e.message);
        return false;

    } finally {}
}

var plantStore;
var cityStore;
var provinceStore;
var countryStore;
var areaCodeStore;
var countryCodeStore;

//  Plant store
plantStore = Ext.create('Ext.data.Store', {
    model : 'Plant',
    autoLoad : true,
    proxy : {
        type : 'ajax',
        url : 'getplantassociated',
        method : 'post',
        actionMethods : {
            create : 'post',
            read : 'post',
            update : 'post',
            destroy : 'post'
        },
        reader : {
            type : 'json',
            rootProperty : 'plantStore'
        }
    }
});

//  City store
cityStore = Ext.create('Ext.data.Store', {
    model : 'City',
    autoLoad : true,
    proxy : {
        type : 'ajax',
        url : 'getcities',
        method : 'post',
        actionMethods : {
            create : 'post',
            read : 'post',
            update : 'post',
            destroy : 'post'
        },
        reader : {
            type : 'json',
            rootProperty : 'cityStore'
        }
    }
});

//  Province store
provinceStore = Ext.create('Ext.data.Store', {
    model : 'Province',
    autoLoad : true,
    proxy : {
        type : 'ajax',
        url : 'getprovinces',
        method : 'post',
        actionMethods : {
            create : 'post',
            read : 'post',
            update : 'post',
            destroy : 'post'
        },
        reader : {
            type : 'json',
            rootProperty : 'provinceStore'
        }
    }
});

//  country Store
countryStore = Ext.create('Ext.data.Store', {
    model : 'Country',
    autoLoad : true,
    proxy : {
        type : 'ajax',
        url : 'getcountries',
        method : 'post',
        actionMethods : {
            create : 'post',
            read : 'post',
            update : 'post',
            destroy : 'post'
        },
        reader : {
            type : 'json',
            rootProperty : 'countryStore'
        }
    }
});

areaCodeStore = Ext.create('Ext.data.Store', {
    model : 'AreaCode',
    autoLoad : true,
    proxy : {
        type : 'ajax',
        url : 'getareacode',
        method : 'post',
        actionMethods : {
            create : 'post',
            read : 'post',
            update : 'post',
            destroy : 'post'
        },
        reader : {
            type : 'json',
            rootProperty : 'areaCodeStore'
        }
    }
});

countryCodeStore = Ext.create('Ext.data.Store', {
    model : 'CountryCode',
    autoLoad : true,
    proxy : {
        type : 'ajax',
        url : 'getcountrycode',
        method : 'post',
        actionMethods : {
            create : 'post',
            read : 'post',
            update : 'post',
            destroy : 'post'
        },
        reader : {
            type : 'json',
            rootProperty : 'countryCodeStore'
        }
    }
});

var oldFirstname,
    oldLastname,
    oldMi,
    oldAddress,
    oldYear,
    oldMonth,
    oldDay,
    oldIndustry,
    oldPlant,
    oldCityId,
    oldProvince,
    oldCountryId,
    oldFax,
    oldTelephone,
    oldCellphone,
    oldEmail,
    oldWebsite,
    oldEmergency,
    oldFaxAreaCode,
    oldFaxCountryCode,
    oldAreaCode,
    oldCountryCode,
    oldZipCode,
    oldEr,
    oldMf,
    oldCalib,
    oldSpareParts,
    oldLatitude,
    oldLongitude;

function editCustomer (customerId) {

    Ext.create('Ext.Window', {
        id: 'editCustomerWindow',
        title: 'Edit Customer',
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
                id: 'editForm',
                autoScroll: true,
                items: [{
                    xtype: 'textfield',
                    fieldLabel : 'Lastname',
                    name: 'lastname',
                    id: 'lastname',
                    margin : '30 30 20 30',
                    width: '90%',
                    maskRe: /[A-Za-z\s+]/,
                    enforceMaxLength: true,
                    maxLength: 32
                },{
                    xtype: 'textfield',
                    fieldLabel : 'Firstname',
                    name: 'firstname',
                    id: 'firstname',
                    width: '90%',
                    margin : '20 30 20 30',
                    maskRe: /[A-Za-z\s+]/,
                    enforceMaxLength: true,
                    maxLength: 64
                },{
                    xtype : 'textfield',
                    fieldLabel : 'Middle Initial',
                    name : 'mi',
                    id : 'mi',
                    inputType : 'textfield',
                    width : '30%',
                    margin : '20 30 20 30',
                    maskRe : /[A-Za-z]/,
                    enforceMaxLength : true,
                    maxLength : 1,
                    fieldCls : 'biggertext'
                },{
                    xtype : 'textareafield',
                    grow : true,
                    fieldLabel : 'Address',
                    name : 'address',
                    id : 'address',
                    inputType : 'textareafield',
                    width : '90%',
                    margin : '20 30 20 30',
                    fieldCls : 'biggertext'
                },{
                    xtype: 'datefield',
                    width: '50%',
                    margin : '20 30 20 30',
                    fieldLabel: 'Date of Birth',
                    id: 'dateofbirth',
                    format: 'Y-m-d',
                    value: new Date()
                },{
                    xtype : 'combo',
                    fieldLabel : 'Plant',
                    id : 'plant',
                    name : 'plant',
                    width : '70%',
                    margin : '20 30 20 30',
                    editable : false,
                    store : plantStore,
                    queryMode : 'local',
                    displayField :'plant_name',
                    valueField : 'plant_id',
                    listConfig : { maxHeight : 250 }
                },{
                    xtype : 'combo',
                    fieldLabel : 'City',
                    id : 'cityId',
                    name : 'cityId',
                    width : '70%',
                    margin : '20 30 20 30',
                    editable : false,
                    store : cityStore,
                    queryMode : 'local',
                    displayField :'cityname',
                    valueField : 'cityid',
                    listConfig : { maxHeight : 250 }
                },{
                    xtype : 'combo',
                    fieldLabel : 'Province',
                    id : 'province',
                    name : 'province',
                    width : '70%',
                    margin : '20 30 20 30',
                    editable : false,
                    store : provinceStore,
                    queryMode : 'local',
                    displayField : 'provincename',
                    valueField : 'provinceid'
                },{
                    xtype : 'combo',
                    fieldLabel : 'Country',
                    id : 'countryId',
                    name : 'countryId',
                    width : '70%',
                    margin : '20 30 20 30',
                    editable : false,
                    store : countryStore,
                    queryMode : 'local',
                    displayField :'countryname',
                    valueField : 'countryid'
                },{
                    xtype: 'numberfield',
                    fieldLabel : 'Fax Number',
                    name : 'fax',
                    id : 'fax',
                    width : '65%',
                    margin : '20 30 20 30',
                    stripCharsRe: /[^0-9]/,
                    maskRe : /[0-9]/,
                    maxLength : 7,
                    enforceMaxLength : true,
                    keyNavEnabled : false,
                    mouseWheelEnabled : false,
                    minValue : 0
                },{
                    xtype: 'numberfield',
                    fieldLabel : 'Telephone',
                    name : 'telNum',
                    id : 'telNum',
                    width : '65%',
                    margin : '20 30 20 30',
                    stripCharsRe: /[^0-9]/,
                    maskRe : /[0-9]/,
                    maxLength : 7,
                    enforceMaxLength : true,
                    minValue : 0,
                    keyNavEnabled : false,
                    mouseWheelEnabled : false
                },{
                    xtype: 'numberfield',
                    fieldLabel : 'Cellphone',
                    name : 'cell',
                    id : 'cell',
                    width : '70%',
                    margin : '20 30 20 30',
                    stripCharsRe: /[^0-9]/,
                    maskRe : /[0-9]/,
                    maxLength : 12,
                    enforceMaxLength : true,
                    keyNavEnabled : false,
                    mouseWheelEnable : false,
                    minValue : 0
                },{
                    xtype : 'textfield',
                    fieldLabel : 'Email',
                    name : 'email',
                    id : 'email',
                    inputType : 'email',
                    width : '70%',
                    margin : '20 30 20 30',
                    vtype: 'email',
                    enforceMaxLength : true,
                    maxLength : 32
                },{
                    xtype : 'textfield',
                    fieldLabel : 'Website',
                    name : 'website',
                    id : 'website',
                    width : '70%',
                    margin : '20 30 20 30',
                    enforceMaxLength : true,
                    maxLength : 32
                },{
                    xtype : 'textfield',
                    fieldLabel : 'Emergency',
                    name : 'emergency',
                    id : 'emergency',
                    inputType : 'textfield',
                    width : '70%',
                    margin : '20 30 20 30',
                    maskRe : /[A-Za-z\s+.]/,
                    enforceMaxLength : true,
                    maxLength : 96
                },{
                    xtype : 'combo',
                    fieldLabel : 'Fax Area Code',
                    id : 'faxAreaCode',
                    name : 'faxAreaCode',
                    width : '70%',
                    margin : '20 30 20 30',
                    editable : false,
                    store : areaCodeStore,
                    queryMode : 'local',
                    displayField :'area',
                    valueField : 'areaCodeId'
                },{
                    xtype : 'combo',
                    fieldLabel : 'Fax Country Code',
                    id : 'faxCountryCode',
                    name : 'faxCountryCode',
                    width : '70%',
                    margin : '20 30 20 30',
                    editable : false,
                    store : countryCodeStore,
                    queryMode : 'local',
                    displayField :'country',
                    valueField : 'countryCodeId'
                },{
                    xtype : 'combo',
                    fieldLabel : 'Area Code',
                    id : 'areaCode',
                    name : 'areaCode',
                    width : '70%',
                    margin : '20 30 20 30',
                    editable : false,
                    store : areaCodeStore,
                    queryMode : 'local',
                    displayField :'area',
                    valueField : 'areaCodeId'
                },{
                    xtype : 'combo',
                    fieldLabel : 'Country Code',
                    id : 'countryCode',
                    name : 'countryCode',
                    width : '70%',
                    margin : '20 30 20 30',
                    editable : false,
                    store : countryCodeStore,
                    queryMode : 'local',
                    displayField :'country',
                    valueField : 'countryCodeId'
                },{
                    xtype: 'textfield',
                    fieldLabel : 'Zip Code',
                    name : 'zip',
                    id : 'zip',
                    width : '45%',
                    margin : '20 30 20 30',
                    editable : false
                },{
                    xtype : 'combo',
                    fieldLabel : 'ER (%)',
                    id : 'er',
                    name : 'er',
                    width : '45%',
                    margin : '20 30 20 30',
                    editable : false,
                    store : numberStore,
                    queryMode : 'local',
                    displayField :'percent',
                    valueField : 'percent'
                },{
                    xtype : 'combo',
                    fieldLabel : 'MF (%)',
                    id : 'mfspgm',
                    name : 'mfspgm',
                    width : '45%',
                    margin : '20 30 20 30',
                    editable : false,
                    store : numberStore,
                    queryMode : 'local',
                    displayField :'percent',
                    valueField : 'percent'
                },{
                    xtype : 'combo',
                    fieldLabel : 'Spare Parts (%)',
                    id : 'spareParts',
                    name : 'spareParts',
                    width : '45%',
                    margin : '20 30 20 30',
                    editable : false,
                    store : numberStore,
                    queryMode : 'local',
                    displayField :'percent',
                    valueField : 'percent'
                },{
                    xtype : 'combo',
                    fieldLabel : 'Calib (%)',
                    id : 'calib',
                    name : 'calib',
                    width : '45%',
                    margin : '20 30 20 30',
                    editable : false,
                    store : numberStore,
                    queryMode : 'local',
                    displayField :'percent',
                    valueField : 'percent'
                },{
                    xtype: 'textfield',
                    fieldLabel : 'Latitude',
                    name: 'latitude',
                    id: 'latitude',
                    margin : '20 30 20 30',
                    width : '80%',
                    maskRe: /[0-9.]/,
                    enforceMaxLength: true,
                    maxLength: 20
                },{
                    xtype: 'textfield',
                    fieldLabel : 'Longitude',
                    name: 'longitude',
                    id: 'longitude',
                    margin : '20 30 20 30',
                    width : '80%',
                    maskRe: /[0-9.]/,
                    enforceMaxLength: true,
                    maxLength: 20
                }],
                buttons : [{
                    text : 'Location',
                    handler : function() {
                        //  comment map edit because google now wants to enable billing
                        openEditCustomerMap();
                    }
                },{
                    text: 'Update Photo',
                    handler: function() {
                        Ext.getCmp('editForm').close();
                        Ext.getCmp('editCustomerWindow').close();
                        updateCustomerPhoto(customerId);
                    }
                },'->',{
                    text: 'Submit',
                    formBind: true,
                    handler: function() {

                        Ext.MessageBox.show({
                            msg : 'Update',
                            progressText : 'Updating...',
                            width : 300,
                            wait : true,
                            waitConfig :
                                {
                                    duration : 60000,
                                    text : 'Updating...',
                                    scope : this,
                                    fn : function() {
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
                                url: 'updatecustomer',
                                params: {
                                    oldFirstname : oldFirstname,
                                    oldLastname : oldLastname,
                                    oldMi : oldMi,
                                    oldAddress : oldAddress,
                                    oldYear : oldYear,
                                    oldMonth : oldMonth,
                                    oldDay : oldDay,
                                    oldIndustry : oldIndustry,
                                    oldPlant : oldPlant,
                                    oldCity : oldCityId,
                                    oldProvince : oldProvince,
                                    oldCountry : oldCountryId,
                                    oldFaxNumber : oldFax,
                                    oldTelephone : oldTelephone,
                                    oldCellphone : oldCellphone,
                                    oldEmail : oldEmail,
                                    oldWebsite : oldWebsite,
                                    oldEmergency : oldEmergency,
                                    oldFaxAreaCode : oldFaxAreaCode,
                                    oldFaxCountryCode : oldFaxCountryCode,
                                    oldAreaCode : oldAreaCode,
                                    oldCountryCode : oldCountryCode,
                                    oldZipCode : oldZipCode,
                                    oldEr : oldEr,
                                    oldMf : oldMf,
                                    oldCalib : oldCalib,
                                    oldSpareParts : oldSpareParts,
                                    oldLatitude : oldLatitude,
                                    oldLongitude : oldLongitude,
                                    customerId : customerId,
                                    year: dateArray[0],
                                    month: dateArray[1],
                                    day: dateArray[2]
                                },
                                success: function (form, action) {
                                    var assoc = Ext.JSON.decode(action.response.responseText);

                                    Ext.MessageBox.hide();
                                    Ext.Msg.alert("Success", assoc['reason']);

                                    customerPersonStore.load({ url : 'getcustomerperson' });

                                    Ext.getCmp('editForm').close();
                                    Ext.getCmp('editCustomerWindow').close();
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
                        Ext.getCmp('editForm').close();
                        Ext.getCmp('editCustomerWindow').close();
                    }
                }]
            })
        ]
    }).show();

    Ext.Ajax.request({
        url: 'getindividual',
        method: 'POST',
        params: { customerId: customerId },
        success: function(response) {

            var customerJson = Ext.decode(response.responseText);
            var oldDateArray = customerJson.dateofbirth.split("-");

            oldFirstname = customerJson.firstname;
            oldLastname = customerJson.lastname;
            oldMi = customerJson.mi;
            oldAddress = customerJson.address;
            oldYear = oldDateArray[0];
            oldMonth = oldDateArray[1];
            oldDay = oldDateArray[2];
            oldIndustry = oldIndustry;
            oldPlant = customerJson.plant;
            oldCityId = customerJson.cityId;
            oldProvince = customerJson.province;
            oldCountryId = customerJson.countryId;
            oldFax = customerJson.fax;
            oldTelephone = customerJson.telephone;
            oldCellphone = customerJson.cellphone;
            oldEmail = customerJson.email;
            oldWebsite = customerJson.website;
            oldEmergency = customerJson.emergency;
            oldFaxAreaCode = customerJson.faxareacode;
            oldFaxCountryCode = customerJson.faxcountrycode;
            oldAreaCode = customerJson.areacode;
            oldCountryCode = customerJson.countrycode;
            oldZipCode = customerJson.zip;
            oldEr = customerJson.er;
            oldMf = customerJson.mf;
            oldCalib = customerJson.calib;
            oldSpareParts = customerJson.spareParts;
            oldLatitude = customerJson.latitude;
            oldLongitude = customerJson.longitude;

            Ext.getCmp('firstname').setValue(customerJson.firstname);
            Ext.getCmp('lastname').setValue(customerJson.lastname);
            Ext.getCmp('mi').setValue(customerJson.mi);
            Ext.getCmp('address').setValue(customerJson.address);
            Ext.getCmp('dateofbirth').setValue(customerJson.dateofbirth);
            Ext.getCmp('plant').setValue(customerJson.plant);
            Ext.getCmp('cityId').setValue(customerJson.cityId);
            Ext.getCmp('province').setValue(customerJson.province);
            Ext.getCmp('countryId').setValue(customerJson.countryId);
            Ext.getCmp('fax').setValue(customerJson.fax);
            Ext.getCmp('telNum').setValue(customerJson.telephone);
            Ext.getCmp('cell').setValue(customerJson.cellphone);
            Ext.getCmp('email').setValue(customerJson.email);
            Ext.getCmp('website').setValue(customerJson.website);
            Ext.getCmp('emergency').setValue(customerJson.emergency);
            Ext.getCmp('faxAreaCode').setValue(customerJson.faxareacode);
            Ext.getCmp('faxCountryCode').setValue(customerJson.faxcountrycode);
            Ext.getCmp('areaCode').setValue(customerJson.areacode);
            Ext.getCmp('countryCode').setValue(customerJson.countrycode);
            Ext.getCmp('zip').setValue(customerJson.zip);
            Ext.getCmp('er').setValue(customerJson.er);
            Ext.getCmp('mfspgm').setValue(customerJson.mf);
            Ext.getCmp('spareParts').setValue(customerJson.spareParts);
            Ext.getCmp('calib').setValue(customerJson.calib);
            Ext.getCmp('latitude').setValue(customerJson.latitude);
            Ext.getCmp('longitude').setValue(customerJson.longitude);
        },
        failure: function(response, opts) {
            var assoc = Ext.decode(response.responseText);
            Ext.Msg.alert("Failed", assoc['reason']);
        }
    });
}

function getNumberObjects() {
    var numberArrayOfObjects = [];
    for (var x = 0; x < 101; ++x) {
        numberArrayOfObjects.push({'percent' : x});
    }
    return numberArrayOfObjects;
}

function setZipcodeByCityid(city) {
    if (city !== '' && city !== null) {
        sendRequest('getzipcode', 'post', { city : city },
            function (o, s, response) {
                var assoc = Ext.decode(response.responseText);
                if (assoc['success']) {
                    Ext.getCmp('zip').setValue(assoc['zipCode']);
                } else {
                    Ext.Msg.alert('Error', assoc['reason']);
                }
            }
        );
    }
}

function updateCustomerPhoto(customerId) {
    var updatePhotoForm = Ext.create('Ext.form.Panel', {
        region: 'center',
        id: 'updatePhotoForm',
        autoScroll: true,
        items: [{
            xtype : 'filefield',
            id : 'customerPhoto',
            name : 'customerPhoto',
            msgTarget : 'size',
            anchor : '100%',
            margin : '20 20 10 20',
            buttonText : 'Select',
            fieldCls : 'labeltextsize',
            clearOnSubmit : false,
            listeners : {
                afterrender : function () {
                    this.fileInputEl.set({
                        accept : 'image/*'
                    });
                },
                change : function() {
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
            margin : '0 20 20 20',
            anchor : '100%',
            height : 30,
            text : 'Submit',
            formBind : true,
            handler : function() {
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