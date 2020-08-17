/**
 * Created by wmdcprog on 3/11/2017.
 * This file is deprecated
 */

const MAX_IMAGE_SIZE = 3000000;

pressed = false;
var date = new Date();
var currentDate = new Date();

sendRequest('scanloggedinsession', 'post', { source : '5' }, function(o, s, response) {
    var assoc = Ext.decode(response.responseText);

    if (assoc['success']) {
        if (assoc['isAdmin']) {
            location.assign('csamanagement.jsp');
        }
    } else {
        location.assign('index.jsp');
    }
});

//  ****************************** store for province combo box ******************************

Ext.define('Province', {
    extend : 'Ext.data.Model',
    fields : [
        { name : 'provincename', type : 'string' },
        { name : 'provinceid', type : 'string' }
    ]
});

var provinceStore = Ext.create('Ext.data.Store', {
    model : 'Province',
    autoLoad : true,
    proxy : {
        type : 'ajax',
        url : 'getprovinces',
        method : 'post',
        //  actionMethod config for post request
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

//  ****************************** store for Country Id ******************************

Ext.define('CountryId', {
    extend : 'Ext.data.Model',
    fields : [
        { name : 'countryname', type : 'string' },
        { name : 'countryid', type : 'int' }
    ]
});

var countryIdStore = Ext.create('Ext.data.Store', {
    model : 'CountryId',
    autoLoad : true,
    proxy : {
        type : 'ajax',
        url : 'getcountries',
        method : 'post',
        //  actionMethod config for post request
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

//  ****************************** store for City Id ******************************

Ext.define('CityId', {
    extend : 'Ext.data.Model',
    fields : [
        { name : 'cityname', type : 'string'},
        { name : 'cityid', type : 'int' }
    ]
});

var cityIdStore = Ext.create('Ext.data.Store', {
    model : 'CityId',
    autoLoad : true,
    proxy : {
        type : 'ajax',
        url : 'getcities',
        method : 'post',
        //  actionMethod config for post request
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

//  ****************************** store for Area Code ******************************

Ext.define('AreaCode', {
    extend : 'Ext.data.Model',
    fields : [
        { name : 'area', type : 'string' },
        { name : 'areaCodeId', type : 'int' }
    ]
});

var areaCodeStore = Ext.create('Ext.data.Store', {
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

//  ****************************** store for Country Code ******************************

Ext.define('CountryCode', {
    extend : 'Ext.data.Model',
    fields : [
        { name : 'country', type : 'string' },
        { name : 'countryCode', type : 'int' },
        { name : 'countryCodeId', type : 'int' }
    ]
});

var countryCodeStore = Ext.create('Ext.data.Store', {
    model : 'CountryCode',
    autoLoad : true,
    proxy : {
        type : 'ajax',
        url : 'getcountrycode',
        method : 'post',
        //  actionMethod config for post request
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

//  ****************************** store for Industry combo box ******************************

Ext.define('Industry', {
    extend : 'Ext.data.Model',
    fields : [
        { name : 'industryname', type : 'string' },
        { name : 'industryid', type : 'int' }
    ]
});

var industryStore = Ext.create('Ext.data.Store', {
    model : 'Industry',
    autoLoad : true,
    proxy : {
        type : 'ajax',
        url : 'getindustries',
        method : 'post',
        //  actionMethod config for post request
        actionMethods : {
            create : 'post',
            read : 'post',
            update : 'post',
            destroy : 'post'
        },
        reader : {
            type : 'json',
            rootProperty : 'industryStore'
        }
    }
});

//  ****************************** store for 1-100 ******************************

var numberStore = Ext.create('Ext.data.Store', {
    fields : ['percent'],
    data : getNumberObjects()
});

//  ****************************** store for plant ******************************

Ext.define('Plant', {
    extend : 'Ext.data.Model',
    fields : [
        { name : 'plant_id', type : 'int' },
        { name : 'plant_name', type : 'string' }
    ]
});

var plantStore = Ext.create('Ext.data.Store', {
    model : 'Plant',
    autoLoad : true,
    proxy : {
        type : 'ajax',
        url : 'getplantassociated',
        method : 'post',
        //  actionMethod config for post request
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

//  ****************************** store for years ******************************

var yearsStore = Ext.create('Ext.data.Store', {
    fields : ['year', 'id'],
    data : getYears()
});

//  ****************************** store for months *****************************

var monthsStore = Ext.create('Ext.data.Store', {
    fields : ['month', 'id'],
    data : getMonths()
});

//  ****************************** store for Days *******************************

var daysStore = Ext.create('Ext.data.Store', {
    fields : ['day', 'id'],
    data : getDays()
});

//  ***************************************  Signpad component **********************************************

Ext.define('SignpadComponent', {
    extend: 'Ext.Component',
    cls: 'hopscotch-bubble-container',
    margin : '20',
    //width : '100%',
    height : 350,
    id: 'test',
    html : "<div class='wrapper'>" +
    //"<img src='includes/images/signarea.jpg' id='signImage' width="+(window.innerWidth - 50)+" height=350' />" +
    "<img src='includes/images/signpad.png' id='signImage' width="+(window.innerWidth - 50)+" height=350' />" +
    "<canvas id='signature-pad' class='signature-pad' width="+(window.innerWidth - 50)+" height='350' style='touch-action: none;'></canvas>" +
    "</div>"
});

var signPad = Ext.create('SignpadComponent');
var signaturePad;

//  do not erase this function
function initMap() {}

var navOpen = false;
var navClose = true;

var addCustomerForm = Ext.create('Ext.form.Panel', {

    region : 'center',
    title : 'Add Customer',
    titleAlign : 'center',
    id : 'formId',
    autoScroll : true,
    url : 'addcustomerperson',

    listeners : {
        'render' : function (panel) {
            panel.body.on('click', function() {
                closeNav();
            });
        }
    },

    header : {

        titlePosition : 1,
        defaults : {
            xtype : 'tool'
        },

        items : [
            {
                xtype : 'image',
                src : 'includes/images/icons/menu_icon.png',
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

    defaults : {
        allowBlank : false
    },

    items : [
        {
            xtype : 'label',
            html : '<p style="display: inline"><span style="color: red">*</span>Lastname:</p>',
            anchor : '100%',
            margin : '20',
            cls : ['labeltextsize', 'block-text', 'bold-text']
        },
        {
            xtype : 'textfield',
            name : 'lastname',
            id : 'lastname',
            inputType : 'textfield',
            anchor : '100%',
            margin : '20',
            maskRe : /[A-Za-z\s+]/,
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
            xtype : 'label',
            html : '<p style="display: inline"><span style="color: red">*</span>Firstname:</p>',
            anchor : '100%',
            margin : '20',
            cls : ['labeltextsize', 'bold-text']
        },
        {
            xtype : 'textfield',
            name : 'firstname',
            id : 'firstname',
            inputType : 'textfield',
            anchor : '100%',
            margin : '20',
            maskRe : /[A-Za-z\s+]/,
            enforceMaxLength : true,
            maxLength : 64,
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
            xtype : 'label',
            html : '<p style="display: inline"><span style="color: red">*</span>Middle Initial:</p>',
            anchor : '100%',
            margin : '20',
            cls : ['labeltextsize', 'bold-text']
        },
        {
            xtype : 'textfield',
            name : 'mi',
            id : 'mi',
            inputType : 'textfield',
            anchor : '45%',
            margin : '20',
            maskRe : /[A-Za-z]/,
            enforceMaxLength : true,
            maxLength : 1,
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
            xtype : 'label',
            html : '<p style="display: inline"><span style="color: red">*</span>Address:</p>',
            anchor : '100%',
            margin : '20',
            cls : ['labeltextsize', 'bold-text']
        },
        {
            xtype : 'textareafield',
            grow : true,
            name : 'address',
            id : 'address',
            inputType : 'textareafield',
            anchor : '100%',
            margin : '20',
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
            xtype : 'label',
            html : '<p style="display: inline"><span style="color: red">*</span>Date of Birth:</p>',
            anchor : '100%',
            margin : '20',
            cls : ['labeltextsize', 'block-text', 'bold-text']
        },
        {
            layout : 'hbox',
            margin : '20',

            defaults : {
                flex : 1
            },

            items : [
                {
                    xtype : 'combo',
                    id : 'year',
                    name : 'year',
                    width : '32%',
                    margin : '0 5 0 0',
                    editable : false,
                    store : yearsStore,
                    queryMode : 'local',
                    displayField : 'year',
                    valueField : 'id',
                    fieldCls : 'biggertext',
                    forceSelection : true,

                    listConfig : {
                        maxHeight : 400
                    },

                    listeners : {

                        'select' : function(combo, record, eOpts) {

                            var yearValue = record.data.id + '';
                            var monthValue = Ext.getCmp('month').getValue() + '';
                            var dayValue = Ext.getCmp('day').lastValue + '';

                            if (monthValue != 'null' && dayValue != 'null') {
                                reconDate('year', 'month', 'day', new Date(yearValue+' '+monthValue+' '+dayValue));
                            }
                        }
                    }
                },
                {
                    xtype : 'combo',
                    id : 'month',
                    name : 'month',
                    with : '47%',
                    margin : '0 5 0 0',
                    editable : false,
                    store : monthsStore,
                    queryMode : 'local',
                    displayField : 'month',
                    valueField : 'id',
                    fieldCls : 'biggertext',
                    cls : 'labeltextsize',

                    listConfig : {
                        maxHeight : 400
                    },

                    listeners : {

                        'select' : function(combo, record, eOpts) {

                            var yearValue = Ext.getCmp('year').getValue() + '';
                            var monthValue = record.data.id + '';
                            var dayValue = Ext.getCmp('day').lastValue + '';

                            if (yearValue != 'null' && dayValue != 'null') {
                                reconDate('year', 'month', 'day', new Date(yearValue+' '+monthValue+' '+dayValue));
                            }
                        }
                    }
                },
                {
                    xtype : 'combo',
                    id : 'day',
                    name : 'day',
                    width : '21%',
                    margin : '0 0 0 0',
                    editable : false,
                    store : daysStore,
                    //value : currentDate.getDate(),
                    queryMode : 'local',
                    displayField : 'id',
                    fieldCls : 'biggertext',
                    cls : 'labeltextsize',

                    listConfig : {
                        maxHeight : 400
                    },

                    listeners : {

                        'select' : function(combo, record, eOpts) {

                            var yearValue = Ext.getCmp('year').getValue() + '';
                            var monthValue = Ext.getCmp('month').getValue() + '';
                            var dayValue = record.data.id + '';

                            if (yearValue != 'null' && monthValue != 'null') {
                                reconDate('year', 'month', 'day', new Date(yearValue+' '+monthValue+' '+dayValue));
                            }
                        }
                    }
                }
            ]
        },
        {
            xtype : 'label',
            html : '<p style="display: inline"><span style="color: red">*</span>Industry:</p>',
            anchor : '100%',
            margin : '20',
            cls : ['labeltextsize', 'bold-text']
        },
        {
            xtype : 'combo',
            id : 'industry',
            name : 'industry',
            anchor : '100%',
            margin : '20',
            editable : false,
            store : industryStore,
            queryMode : 'local',
            displayField :'industryname',
            valueField : 'industryid',
            fieldCls : 'biggertext',

            listConfig : {
                maxHeight : 400
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
            xtype : 'label',
            html : '<p style="display: inline"><span style="color: red">*</span>Plant:</p>',
            anchor : '100%',
            margin : '20',
            cls : ['labeltextsize', 'bold-text']
        },
        {
            xtype : 'combo',
            id : 'plant',
            name : 'plant',
            anchor : '100%',
            margin : '20',
            editable : false,
            store : plantStore,
            queryMode : 'local',
            displayField :'plant_name',
            valueField : 'plant_id',
            fieldCls : 'biggertext',

            listConfig : {
                maxHeight : 400
            }
        },
        {
            xtype : 'label',
            html : '<p style="display: inline"><span style="color: red">*</span>City:</p>',
            anchor : '100%',
            margin : '20',
            cls : ['labeltextsize', 'bold-text']
        },
        {
            xtype : 'combo',
            id : 'city',
            name : 'city',
            anchor : '80%',
            margin : '20',
            editable : false,
            store : cityIdStore,
            queryMode : 'local',
            displayField :'cityname',
            valueField : 'cityid',
            fieldCls : 'biggertext',

            listeners : {

                'change' : function(field, selectedValue) {

                    setZipcodeByCityid(selectedValue)
                }
            },

            listConfig : {
                maxHeight : 400
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
            xtype : 'label',
            html : '<p style="display: inline"><span style="color: red">*</span>Province:</p>',
            anchor : '100%',
            margin : '20',
            cls : ['labeltextsize', 'bold-text']
        },
        {
            xtype : 'combo',
            id : 'province',
            name : 'province',
            anchor : '80%',
            margin : '20',
            editable : false,
            store : provinceStore,
            queryMode : 'local',
            displayField : 'provincename',
            valueField : 'provinceid',
            fieldCls : 'biggertext',

            listConfig : {
                maxHeight : 400
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
            xtype : 'label',
            text : 'Country:',
            anchor : '100%',
            margin : '20',
            cls : ['labeltextsize', 'bold-text']
        },
        {
            xtype : 'combo',
            emptyText : 'Optional',
            id : 'country',
            name : 'country',
            anchor : '100%',
            margin : '20',
            editable : false,
            store : countryIdStore,
            queryMode : 'local',
            displayField :'countryname',
            valueField : 'countryid',
            fieldCls : 'biggertext',
            allowBlank : true,

            listeners : {
                scope : this,
                afterRender : function(me) {
                    me.setValue(9);
                }
            },

            listConfig : {
                maxHeight : 400
            },

            triggers : {
                clears : {
                    cls : 'x-form-clear-trigger',
                    handler : function() {
                        this.setValue(9);
                    }
                }
            }
        },
        {
            xtype : 'label',
            html : '<p style="display: inline">Fax Number:</p>',
            anchor : '100%',
            margin : '20',
            cls : ['labeltextsize', 'bold-text']
        },
        {
            xtype: 'textfield',
            inputType : 'number',
            name : 'fax',
            id : 'fax',
            anchor : '65%',
            margin : '20',
            stripCharsRe: /[^0-9]/,
            maskRe : /[0-9]/,
            maxLength : 7,
            enforceMaxLength : true,
            fieldCls : 'biggertext',
            allowBlank : true,
            keyNavEnabled : false,
            mouseWheelEnabled : false,
            minValue : 0,

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
            xtype : 'label',
            html : '<p style="display: inline">Telephone:</p>',
            anchor : '100%',
            margin : '20',
            cls : ['labeltextsize', 'bold-text']
        },
        {
            xtype: 'textfield',
            inputType : 'number',
            name : 'telephone',
            id : 'telephone',
            anchor : '65%',
            margin : '20',
            stripCharsRe: /[^0-9]/,
            maskRe : /[0-9]/,
            maxLength : 7,
            enforceMaxLength : true,
            fieldCls : 'biggertext',
            allowBlank : true,
            minValue : 0,
            keyNavEnabled : false,
            mouseWheelEnabled : false,

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
            xtype : 'label',
            html : '<p style="display: inline"><span style="color: red">*</span>Cellphone:</p>',
            anchor : '100%',
            margin : '20',
            cls : ['labeltextsize', 'bold-text']
        },
        {
            xtype: 'textfield',
            inputType : 'number',
            name : 'mobile',
            id : 'Mobile',
            anchor : '75%',
            margin : '20',
            stripCharsRe: /[^0-9]/,
            maskRe : /[0-9]/,
            maxLength : 12,
            enforceMaxLength : true,
            allowBlank : true,
            fieldCls : 'biggertext',
            keyNavEnabled : false,
            mouseWheelEnable : false,
            minValue : 0,

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
            xtype : 'label',
            text : 'Email:',
            anchor : '100%',
            margin : '20',
            cls : ['labeltextsize', 'bold-text']
        },
        {
            xtype : 'textfield',
            name : 'email',
            id : 'email',
            inputType : 'email',
            anchor : '100%',
            margin : '20',
            vtype: 'email',
            enforceMaxLength : true,
            maxLength : 32,
            allowBlank : true,
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
            xtype : 'label',
            text : 'Website:',
            anchor : '100%',
            margin : '20',
            cls : ['labeltextsize', 'bold-text']
        },
        {
            xtype : 'textfield',
            name : 'website',
            id : 'website',
            anchor : '100%',
            margin : '20',
            //maskRe : /[A-Za-z\s+]/,
            enforceMaxLength : true,
            maxLength : 32,
            allowBlank : true,
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
            xtype : 'label',
            text : 'Emergency:',
            anchor : '100%',
            margin : '20',
            cls : ['labeltextsize', 'bold-text']
        },
        {
            xtype : 'textfield',
            name : 'emergency',
            id : 'emergency',
            inputType : 'textfield',
            anchor : '100%',
            margin : '20',
            maskRe : /[A-Za-z\s+.]/,
            enforceMaxLength : true,
            maxLength : 96,
            allowBlank : true,
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
            xtype : 'label',
            html : '<p style="display: inline"><span style="color: red">*</span>Fax Area Code:</p>',
            anchor : '100%',
            margin : '20',
            cls : ['labeltextsize', 'bold-text']
        },
        {
            xtype : 'combo',
            id : 'faxCode',
            name : 'faxCode',
            anchor : '100%',
            margin : '20',
            editable : false,
            store : areaCodeStore,
            queryMode : 'local',
            displayField :'area',
            valueField : 'areaCodeId',
            fieldCls : 'biggertext',

            listeners : {
                afterrender : function() {
                    this.setValue('2');
                }
            },

            listConfig : {
                maxHeight : 400
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
            xtype : 'label',
            html : '<p style="display: inline"><span style="color: red">*</span>Fax Country Code:</p>',
            anchor : '100%',
            margin : '20',
            cls : ['labeltextsize', 'bold-text']
        },
        {
            xtype : 'combo',
            id : 'faxCountryCode',
            name : 'faxCountryCode',
            anchor : '65%',
            margin : '20',
            editable : false,
            store : countryCodeStore,
            queryMode : 'local',
            displayField :'country',
            valueField : 'countryCodeId',
            fieldCls : 'biggertext',
            value : 1,

            listeners : {
                afterrender : function() {
                }
            },

            listConfig : {
                maxHeight : 400
            }
        },
        {
            xtype : 'label',
            html : '<p style="display: inline"><span style="color: red">*</span>Area Code:</p>',
            anchor : '100%',
            margin : '20',
            cls : ['labeltextsize', 'bold-text']
        },
        {
            xtype : 'combo',
            id : 'areaCode',
            name : 'areaCode',
            anchor : '100%',
            margin : '20',
            editable : false,
            store : areaCodeStore,
            queryMode : 'local',
            displayField :'area',
            valueField : 'areaCodeId',
            fieldCls : 'biggertext',

            listeners : {
                afterrender : function() {
                    this.setValue('2');
                }
            },

            listConfig : {
                maxHeight : 400
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
            xtype : 'label',
            html : '<p style="display: inline"><span style="color: red">*</span>Country Code:</p>',
            anchor : '100%',
            margin : '20',
            cls : ['labeltextsize', 'bold-text']
        },
        {
            xtype : 'combo',
            id : 'countryCode',
            name : 'countryCode',
            anchor : '65%',
            margin : '20',
            editable : false,
            store : countryCodeStore,
            queryMode : 'local',
            displayField :'country',
            valueField : 'countryCodeId',
            fieldCls : 'biggertext',
            value : 1,

            listeners : {
                scope : this,
                afterRender : function(me) {
                    //me.setValue(63);
                }
            }
        },
        {
            xtype : 'label',
            html : '<p style="display: inline"><span style="color: red">*</span>Zip Code:</p>',
            anchor : '100%',
            margin : '20',
            cls : ['labeltextsize', 'bold-text']
        },
        {
            xtype: 'textfield',
            inputType : 'number',
            name : 'zip',
            id : 'zip',
            anchor : '45%',
            margin : '20',
            stripCharsRe: /[^0-9]/,
            maskRe : /[0-9]/,
            maxLength : 4,
            enforceMaxLength : true,
            fieldCls : 'biggertext',
            keyNavEnabled : false,
            mouseWheelEnable : false,
            minValue : 0,
            editable : false
        },
        {
            xtype : 'label',
            html : '<p style="display: inline"><span style="color: red">*</span>ER (%):</p>',
            anchor : '100%',
            margin : '20',
            cls : ['labeltextsize', 'bold-text']
        },
        {
            xtype : 'combo',
            id : 'er',
            name : 'er',
            anchor : '45%',
            margin : '20',
            editable : false,
            store : numberStore,
            queryMode : 'local',
            displayField :'percent',
            valueField : 'percent',
            fieldCls : 'biggertext',
            value : 0,

            listConfig : {
                maxHeight : 400
            },

            triggers : {
                clears: {
                    cls: 'x-form-clear-trigger',
                    handler: function () {
                        this.setValue(0);
                    }
                }
            }
        },
        {
            xtype : 'label',
            html : '<p style="display: inline"><span style="color: red">*</span>MF (%):</p>',
            anchor : '100%',
            margin : '20',
            cls : ['labeltextsize', 'bold-text']
        },
        {
            xtype : 'combo',
            id : 'mf',
            name : 'mf',
            anchor : '45%',
            margin : '20',
            editable : false,
            store : numberStore,
            queryMode : 'local',
            displayField :'percent',
            valueField : 'percent',
            fieldCls : 'biggertext',
            value : 0,

            listConfig : {
                maxHeight : 400
            },

            triggers : {
                clears: {
                    cls: 'x-form-clear-trigger',
                    handler: function () {
                        this.setValue(0);
                    }
                }
            }
        },
        {
            xtype : 'label',
            html : '<p style="display: inline"><span style="color: red">*</span>Spare Parts (%):</p>',
            anchor : '100%',
            margin : '20',
            cls : ['labeltextsize', 'bold-text']
        },
        {
            xtype : 'combo',
            id : 'spareParts',
            name : 'spareParts',
            anchor : '45%',
            margin : '20',
            editable : false,
            store : numberStore,
            queryMode : 'local',
            displayField :'percent',
            valueField : 'percent',
            fieldCls : 'biggertext',
            value : 0,

            listConfig : {
                maxHeight : 400
            },

            triggers : {
                clears: {
                    cls: 'x-form-clear-trigger',
                    handler: function () {
                        this.setValue(0);
                    }
                }
            }
        },
        {
            xtype : 'label',
            html : '<p style="display: inline"><span style="color: red">*</span>Calibration (%):</p>',
            anchor : '100%',
            margin : '20',
            cls : ['labeltextsize', 'bold-text']
        },
        {
            xtype : 'combo',
            id : 'calib',
            name : 'calib',
            anchor : '45%',
            margin : '20',
            editable : false,
            store : numberStore,
            queryMode : 'local',
            displayField :'percent',
            valueField : 'percent',
            fieldCls : 'biggertext',
            value : 0,

            listConfig : {
                maxHeight : 400
            },

            triggers : {
                clears : {
                    cls : 'x-form-clear-trigger',
                    handler : function() {
                        this.setValue(0);
                    }
                }
            }
        },
        {
            xtype: 'hidden',
            name: 'username',
            id : 'username',
            value : onlineUserFromAddCustomer
        },
        {
            xtype: 'hidden',
            name: 'csaId',
            id : 'csaId',
            value : csaId
        },
        {
            xtype : 'label',
            html : '<p style="display: inline"><span style="color: red">*</span>Photo:</p>',
            anchor : '100%',
            margin : '20',
            cls : ['labeltextsize', 'bold-text']
        },
        {
            xtype : 'filefield',
            name : 'photo',
            id : 'photo',
            msgTarget : 'size',
            anchor : '100%',
            margin : '20',
            height : 40,
            buttonText : 'Select',
            fieldCls : 'labeltextsize',
            cls : 'photo-field',
            clearOnSubmit : false,

            listeners : {
                afterrender : function() {
                    this.fileInputEl.set({
                        accept : 'image/*'
                    });
                },

                change : function() {

                    var form = this.up('form');

                    //  traverse
                    var file = form.getEl().down('input[type=file]').dom.files[0];

                    var _URL = window.URL || window.webkitURL;
                    var img = new Image();

                    img.onload = function() {

                        if (this.width < 400 || this.height < 400)
                        {
                            Ext.Msg.alert('Warning!', 'Photo is too small. Select a bigger one.');
                            Ext.getCmp('photo').inputEl.dom.value = '';
                        }
                    };

                    img.onerror = function() {
                        Ext.Msg.alert('Warning!', 'Chosen file is not an image.');
                        Ext.getCmp('photo').inputEl.dom.value = '';
                    };

                    img.src = _URL.createObjectURL(file);

                    //  get size
                    var fileSize = file.size;

                    if (file.type != 'image/jpeg')
                    {
                        Ext.Msg.alert('WARNING!', 'Photo shouldbe jpeg. Select another one.');
                        Ext.getCmp('photo').inputEl.dom.value = '';
                    }

                    if (fileSize > MAX_IMAGE_SIZE)
                    {
                        Ext.Msg.alert("Warning", "Selected image too big. Select another.");
                        Ext.getCmp('photo').inputEl.dom.value = '';
                    }
                }
            }
        },
        {
            xtype : 'label',
            html : '<p style="display: inline"><span style="color: red">*</span>Customer Location:</p>',
            anchor : '100%',
            margin : '20',
            cls : ['labeltextsize', 'bold-text']
        },
        {
            xtype: 'textfield',
            name: 'lat',
            id : 'lat',
            margin : '20',
            anchor : '75%',
            editable : false,
            value : ''
        },
        {
            xtype : 'textfield',
            name: 'lng',
            id : 'lng',
            margin : '20',
            anchor : '75%',
            editable : false,
            value : ''
        },
        {
            xtype : 'button',
            id : 'mapButton',
            text : 'Find Contact Location',
            cls : 'x-button',
            height : 60,
            margin : '0 0 0 20',
            padding : '1 1 1 1',
            disabled : true,

            handler : function() {
                openMap();
            }
        },
        {
            xtype : 'label',
            //text : 'Signature:',
            html : '<p style="display: inline"><span style="color: red">*</span>Signature:</p>',
            anchor : '100%',
            margin : '20',
            cls : ['labeltextsize', 'bold-text', 'block-text']
        },
        signPad,
        {
            xtype : 'button',
            text : 'Clear Signature',
            margin : '0 0 0 20',
            height : 60,
            cls : 'x-button',
            handler : function() {

                Ext.getCmp('lng').focus(false, 200);
                signIsClicked = false;
                signaturePad.clear();
            }
        },
        {
            xtype : 'label',
            id : 'errorWarning',
            margin : '20',
            cls : 'x-form-label'
        },
        {
            disabled : true,
            xtype : 'button',
            margin : '20',
            anchor : '100%',
            text : 'ADD',
            cls : 'x-button',
            height : 60,
            itemId : 'buttonToBind',
            formBind : true,

            handler : function() {

                var form = this.up('form');

                var year = Ext.getCmp('year').getValue();
                var month = Ext.getCmp('month').getValue();
                var day = Ext.getCmp('day').getValue();

                if (year == null)
                {
                    Ext.Msg.alert('Required', 'Include a year in birthdate.');
                }
                else if (month == null)
                {
                    Ext.Msg.alert('Required', 'Include a month in birthdate.');
                }
                else if (day == null) {
                    Ext.Msg.alert('Required', 'Include a day in birthdate.');
                }
                else
                {
                    if (form.isValid())
                    {
                        form.submit({

                            waitMsg : 'Saving new customer...',

                            params : {
                                signStatus : signIsClicked,
                                signature : signaturePad.toDataURL('image/png'),
                                birthDate : year+'-'+month+'-'+day
                            },

                            success : function(form, action) {

                                addCustomerForm.reset();
                                signIsClicked = false;
                                signaturePad.clear();
                                initializeDefaults();

                                //  clear filefield on success form submit
                                Ext.getCmp('photo').inputEl.dom.value = '';

                                var assoc = Ext.JSON.decode(action.response.responseText);
                                Ext.Msg.alert('Success', assoc['reason']);
                            },

                            failure : function(form, action) {

                                //  try-catch-finally is necessary to display error message in chrome browser.
                                //  mozilla displays well

                                var msg;
                                var assoc;

                                try
                                {
                                    assoc = Ext.JSON.decode(action.response.responseText);
                                    msg = assoc['reason'];
                                }
                                catch(err) {
                                    msg = 'Choose images with size below 3 MB';
                                }
                                finally {
                                    Ext.Msg.alert('Fail', msg);
                                }
                            }

                        });
                    }
                }
            }
        },
        {
            xtype : 'button',
            margin : '0 20 20 20',
            anchor : '100%',
            text : 'CLEAR',
            cls : 'x-button',
            height : 60,
            handler : function() {

                addCustomerForm.reset();
                signIsClicked = false;
                signaturePad.clear();
                initializeDefaults();
            }
        }
    ]
});

function openMap()
{
    var mapStatus = true;

    try
    {
        var resizedWidth = document.body.clientWidth * 0.90;
        var resizedHeight = document.body.clientHeight * 0.90;

        //  this component is asynchronous
        Ext.define('Customer.Map', {

            extend: 'Ext.panel.Panel',
            alias : 'widget.smartcitymaps',
            itemId: 'map',
            anchor : '100%',
            //margin : '20',
            border: false,
            html : "<div style=\"width:"+(resizedWidth)+"px; height:"+(resizedHeight - 100)+"px\" id=\"myMap\"></div>",

            constructor: function(c) {

                var me = this;

                var marker;

                var loadMap = function(lat, lng) {

                    var me = this;
                    var location = { lat : lat, lng : lng};

                    try
                    {
                        me.map = new google.maps.Map(document.getElementById("myMap"), {
                            clickableIcons : false,
                            zoom: 13,
                            center: new google.maps.LatLng(lat, lng),
                            mapTypeId: google.maps.MapTypeId.ROADMAP
                        });
                    }
                    catch (e)
                    {
                        Ext.getCmp('lat').setValue('0');
                        Ext.getCmp('lng').setValue('0');
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

                        Ext.getCmp('lat').setValue(e.latLng.lat());
                        Ext.getCmp('lng').setValue(e.latLng.lng());
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

        var mapPanel = Ext.create('Customer.Map', {

            width : '100%',
            height : '95%',
            id : 'mapPanelId'
        });

        Ext.create('Ext.Window', {

            id : 'mapId',
            title : 'Google Maps',
            width : resizedWidth,
            height : resizedHeight,
            minWidth : resizedWidth,
            minHeight : resizedHeight,
            layout : 'fit',
            plain : true,
            modal : true,
            items : [mapPanel],

            listeners : {
                show : function() {
                },
                destroy : function() {
                }
            },

            buttons : [
                {
                    text : 'Close',
                    handler : function() {
                        Ext.getCmp('mapPanelId').destroy();
                        Ext.getCmp('mapId').destroy();
                    }
                }
            ]
        }).show();
    }
    catch (e)   //  google is not defined
    {
        mapStatus = false;

        //  destroy window and map panel
        Ext.getCmp('mapPanelId').destroy();
        Ext.getCmp('mapId').destroy();
    }
    finally {
        return mapStatus;
    }
}

function initializeDefaults()
{
    Ext.getCmp('country').setValue(9);
    Ext.getCmp('countryCode').setValue(1);
    Ext.getCmp('areaCode').setValue('2');
    Ext.getCmp('faxCode').setValue('2');
    Ext.getCmp('faxCountryCode').setValue(1);
}

var signIsClicked = false;

Ext.onReady(function() {
    Ext.QuickTips.init();

    Ext.create('Ext.container.Viewport', {
        layout : 'border',
        renderTo : Ext.getBody(),
        id : 'viewportCustomer',
        items : [addCustomerForm]
    });

    signaturePad = new SignaturePad(Ext.get('signature-pad').dom, {

        backgroundColor: 'rgba(255, 255, 255, 0)',
        penColor: 'rgb(0, 0, 0)'
    });

    //  mobile browser orientation change listener.
    window.addEventListener('orientationchange', function() {
        setTimeout(function() {

            var base64String = signaturePad.toDataURL('image/png');

            Ext.get('signImage').dom.width = document.body.clientWidth - 50;
            Ext.get('signature-pad').dom.width = document.body.clientWidth - 50;

            var canvas = document.getElementById('signature-pad');
            var ctx = canvas.getContext('2d');

            var image = new Image();

            image.onload = function() {
                ctx.drawImage(image, 0, 0);
            };
            image.src = base64String;

        }, 1000);

        document.getElementById('mySidenav').style.paddingTop = '76px';
    });

    //  desktop browser resize listener
    window.addEventListener('resize', function() {
        setTimeout(function() {

            var base64String = signaturePad.toDataURL('image/png');

            Ext.get('signImage').dom.width = document.body.clientWidth - 50;
            Ext.get('signature-pad').dom.width = document.body.clientWidth - 50;

            var canvas = document.getElementById('signature-pad');
            var ctx = canvas.getContext('2d');

            var image = new Image();

            image.onload = function() {
                ctx.drawImage(image, 0, 0);
            };
            image.src = base64String;

            dimenSubmit(document.body.clientWidth, document.body.clientHeight, window.innerWidth, window.innerHeight);

            Ext.getCmp('viewportCustomer').setWidth(document.body.clientWidth);
            Ext.getCmp('viewportCustomer').setHeight(document.body.clientHeight);

        }, 1000);

        document.getElementById('mySidenav').style.paddingTop = '76px';
    });

    document.getElementById('mySidenav').style.paddingTop = '76px';

    //  drag gesture for drag in signature
    Ext.get('signature-pad').on('drag', function(){
        signIsClicked = true;
        Ext.getCmp('lng').focus(false, 200);
    });

    //  gesture listener for menu icon
    Ext.get('menuId').on('touchstart', function() {

        if (navClose) {
            openNav();
        }
        else {
            closeNav();
        }
    });
});

function enableOnPageLoad()
{
    Ext.getCmp('mapButton').setDisabled(false);

    if (openMap())
    {
        Ext.getCmp('mapPanelId').destroy();
        Ext.getCmp('mapId').destroy();
    }
    else
    {
        Ext.getCmp('lat').setValue('0');
        Ext.getCmp('lng').setValue('0');
    }
}

function setZipcodeByCityid(city)
{
    if (city != '' && city != null)
    {
        sendRequest('getzipcode', 'post', { city : city },
            function (o, s, response) {

                var assoc = Ext.JSON.decode(response.responseText);
                Ext.getCmp('zip').setValue(assoc['zipCode']);

                if (!assoc['success']) {
                    Ext.Msg.alert('Fail', assoc['reason']);
                }
        });
    }
}

function reconDate(yearComp, monthComp, dayComp, myDate)
{
    Ext.getCmp(yearComp).lastValue = myDate.getFullYear();
    Ext.getCmp(monthComp).lastValue = myDate.getMonth() + 1;
    Ext.getCmp(dayComp).lastValue = myDate.getDate();

    Ext.getCmp(yearComp).setValue(Ext.getCmp(yearComp).lastValue);
    Ext.getCmp(monthComp).setValue(Ext.getCmp(monthComp).lastValue);
    Ext.getCmp(dayComp).setValue(Ext.getCmp(dayComp).lastValue);
}

function getNumberObjects()
{
    var numberArrayOfObjects = [];

    for (var x = 0; x < 101; ++x) {
        numberArrayOfObjects.push({'percent' : x});
    }

    return numberArrayOfObjects;
}

function getDays()
{
    var dayArrayOfObjects = [];

    for (var x = 1; x <= 31; ++x) {
        dayArrayOfObjects.push({'day' : ''+x, 'id' : x});
    }

    return dayArrayOfObjects;
}

function getMonths()
{
    var monthObjectArray = [];

    monthObjectArray.push({'month' : 'January', 'id' : 1});
    monthObjectArray.push({'month' : 'February', 'id' : 2});
    monthObjectArray.push({'month' : 'March', 'id' : 3});
    monthObjectArray.push({'month' : 'April', 'id' : 4});
    monthObjectArray.push({'month' : 'May', 'id' : 5});
    monthObjectArray.push({'month' : 'June', 'id' : 6});
    monthObjectArray.push({'month' : 'July', 'id' : 7});
    monthObjectArray.push({'month' : 'August', 'id' : 8});
    monthObjectArray.push({'month' : 'September', 'id' : 9});
    monthObjectArray.push({'month' : 'October', 'id' : 10});
    monthObjectArray.push({'month' : 'November', 'id' : 11});
    monthObjectArray.push({'month' : 'December', 'id' : 12});

    return monthObjectArray;
}

function getYears()
{
    var yearObjectArray = [];

    for (var x = currentDate.getFullYear(); x >= currentDate.getFullYear() - 100; --x) {
        yearObjectArray.push({'year' : ''+x, 'id' : x});
    }

    return yearObjectArray;
}