/**
 * Created by wmdcprog on 7/14/2018.
 */

const MAX_IMAGE_SIZE = 3000000;

pressed = false;
var date = new Date();
var currentDate = new Date();
var navOpen = false;
var navClose = true;

sendRequest('scanloggedinsession', 'post', { source : '11' }, function(o, s, response) {

    var assoc = Ext.decode(response.responseText);

    if (assoc['success']) {
        if (assoc['isAdmin']) {
            location.assign('csamanagement.jsp');
        }
    } else {
        location.assign('index.jsp');
    }
});

/** SIGNATURE PAD COMPONENT*/
Ext.define('SignpadComponent', {
    extend: 'Ext.Component',
    cls: 'hopscotch-bubble-container',
    margin : '20',
    height : 225,
    id: 'test',
    html : "<div class='wrapper'>" +
    "<img src='includes/images/signarea.jpg' id='signImage' width="+500+" height=225' />" +
    "<canvas id='signature-pad' class='signature-pad' width="+500+" height='225' style='touch-action: none;'></canvas>" +
    "</div>"
});
var signPad = Ext.create('SignpadComponent');
var signaturePad;

/** CUSTOMER STORE*/
Ext.define('Customer', {
    extend : 'Ext.data.Model',
    fields : [
        {name : 'customerId', type : 'string'},
        {name : 'source', type : 'string'},
        {name : 'cId', type : 'int'},
        {name : 'customer', type : 'string'}
    ]
});

var customerStore = Ext.create('Ext.data.Store', {
    model : 'Customer',
    autoLoad : false/*,
    proxy : {
        type : 'ajax',
        url : 'getmcsacustomerlist',
        method : 'post',
        extraParams : {
            cid : csaId,
            filter: customerFilter
        },
        actionMethods : {
            create : 'post',
            read : 'post',
            update : 'post',
            destroy : 'post'
        },
        reader : {
            type : 'json',
            rootProperty : 'customers'
        }
    }*/
});

/** ENGINE STORE*/
Ext.define('Engine', {
    extend : 'Ext.data.Model',
    fields : [
        {name : 'makeId', type : 'int'},
        {name : 'modelId', type : 'int'},
        {name : 'year', type : 'int'},
        {name : 'model', type : 'string'},
        {name : 'category', type : 'string'},
        {name : 'make', type : 'string'}
    ]
});

var engineStore = Ext.create('Ext.data.Store', {
    model : 'Engine',
    autoLoad : false,
    /*proxy : {
        type : 'ajax',
        url : 'getmcsaenginemodellist',
        method : 'post',
        actionMethods : {
            create : 'post',
            read : 'post',
            update : 'post',
            destroy : 'post'
        },
        reader : {
            type : 'json',
            rootProperty : 'models'
        }
    }*/
});

var customer;
var source;
var imageType = 'jpeg';
var preparedBy = csaId;

var initialJoborderForm = Ext.create('Ext.form.Panel', {
    region : 'center',
    title : 'Initial Joborder',
    titleAlign : 'center',
    id : 'initialJoForm',
    autoScroll : true,
    listener : {
        'render' : function (panel) {
            panel.body.on('click', function(){
                closeNav();
            });
        }
    },
    defaults : {
        allowBlank : false
    },
    header : {
        titlePosition : 1,
        defaults : {
            xtype : 'tool'
        },
        items : [{
            xtype : 'image',
            src : 'includes/images/icons/menu_icon.png',
            id : 'menuId',
            width : 25,
            height : 25,
            cls : ['my-field-cls']
        }]
    },

    items : [{
            xtype : 'label',
            text: 'JO Number',
            margin : '20 0 5 20',
            cls : ['block-text', 'bold-text']
        },{
            xtype : 'textfield',
            name : 'joNumber',
            id : 'joNumber',
            margin : '0 0 0 20',
            inputType : 'number',
            enforceMaxLength : true,
            maxLength : 9,
            width : 200,
            triggers : {
                clears : {
                    cls : 'x-form-clear-trigger',
                    handler : function() {
                        this.setValue('');
                    }
                }
            }
        },{
            xtype : 'label',
            text: 'Customer',
            margin : '10 0 5 20',
            cls : ['block-text', 'bold-text']
        },{
            xtype : 'combobox',
            margin : '0 0 0 20',
            name : 'customerId',
            id : 'customer',
            displayField : 'customer',
            //valueField : 'cId',
            width : 300,
            store : customerStore,
            queryMode : 'remote',
            typeAhead : true,
            minChars : 2,
            listeners : {
                select: function(combo, record, eOpts) {
                    customer = record.data.customer;
                    source = record.data.source;
                    console.log(record.data);
                },
                change: function(newValue, oldValue, eOpts) {

                    customerStore.setProxy({
                        type : 'ajax',
                        url : 'getmcsacustomerlist',
                        method : 'post',
                        actionMethods : {
                            create : 'post',
                            read : 'post',
                            update : 'post',
                            destroy : 'post'
                        },
                        reader : {
                            type : 'json',
                            rootProperty : 'customers'
                        }
                    });

                    customerStore.load({
                        params: {cid : csaId, filter: oldValue.toString()}
                    });
                }
            }
        },{
            xtype : 'label',
            text: 'Mobile',
            margin : '10 0 5 20',
            cls : ['block-text', 'bold-text']
        },{
            xtype : 'textfield',
            name : 'mobile',
            id : 'mobile',
            inputType : 'number',
            margin : '0 0 0 20',
            enforceMaxLength : true,
            maxLength : 11,
            width : 200,
            triggers : {
                clears : {
                    cls : 'x-form-clear-trigger',
                    handler : function() {
                        this.setValue('');
                    }
                }
            }
        },{
            xtype : 'label',
            text: 'Purchase Order',
            margin : '10 0 5 20',
            cls : ['block-text', 'bold-text']
        },{
            xtype : 'textfield',
            name : 'purchaseOrder',
            id : 'purchaseOrder',
            inputType : 'textfield',
            maskRe : /[A-Za-z0-9]/,
            margin : '0 0 0 20',
            enforceMaxLength : true,
            maxLength : 16,
            width : 200,
            triggers : {
                clears : {
                    cls : 'x-form-clear-trigger',
                    handler : function() {
                        this.setValue('');
                    }
                }
            }
        },{
            xtype : 'label',
            text: 'PO Date',
            margin : '10 0 5 20',
            cls : ['block-text', 'bold-text']
        },{
            xtype : 'datefield',
            width : 200,
            margin : '0 0 0 20',
            name : 'poDate',
            id : 'poDate',
            format : 'Y-m-d',
            value : new Date()
        },{
            xtype : 'label',
            text: 'Engine Model',
            margin : '10 0 5 20',
            cls : ['block-text', 'bold-text']
        },{
            xtype : 'combobox',
            margin : '0 0 0 20',
            name : 'modelId',
            id : 'engineModel',
            displayField : 'model',
            valueField : 'modelId',
            width : 300,
            store : engineStore,
            queryMode : 'remote',
            typeAhead : true,
            minChars : 1,
            listeners : {
                'select' : function(combo, record, eOpts) {
                    Ext.getCmp('make').setValue(record.data.make);
                    Ext.getCmp('cat').setValue(record.data.category);
                }
            }
        },{
            xtype : 'label',
            text : 'Make / Category',
            margin : '10 0 5 20',
            cls : ['block-text', 'bold-text']
        },{
            layout : 'hbox',
            margin : '0 0 0 20',
            width : 300,
            items : [{
                xtype: 'textfield',
                id : 'make',
                name : 'make',
                width : 150,
            },{
                xtype: 'textfield',
                id : 'cat',
                name : 'cat',
                width : 150,
            }]
        },{
            xtype : 'label',
            text : 'Serial Number',
            margin : '10 0 5 20',
            cls : ['block-text', 'bold-text']
        },{
            xtype : 'textfield',
            name : 'serialNo',
            id : 'serialNo',
            inputType : 'textfield',
            maskRe : /[A-Za-z0-9]/,
            margin : '0 0 0 20',
            enforceMaxLength : true,
            maxLength : 32,
            width : 300,
            triggers : {
                clears : {
                    cls : 'x-form-clear-trigger',
                    handler : function() {
                        this.setValue('');
                    }
                }
            }
        },{
            xtype : 'label',
            text : 'Date Receive',
            margin : '10 0 5 20',
            cls : ['block-text', 'bold-text']
        },{
            xtype : 'datefield',
            width : 200,
            margin : '0 0 0 20',
            name : 'dateReceive',
            id : 'dateReceive',
            format : 'Y-m-d',
            value : new Date()
        },{
            xtype : 'label',
            text : 'Date Commit',
            margin : '10 0 5 20',
            cls : ['block-text', 'bold-text']
        },{
            xtype : 'datefield',
            width : 200,
            margin : '0 0 0 20',
            name : 'dateCommit',
            id : 'dateCommit',
            format : 'Y-m-d',
            value : new Date()
        },{
            xtype : 'label',
            text : 'Reference Number',
            margin : '10 0 5 20',
            cls : ['block-text', 'bold-text']
        },{
            xtype : 'textfield',
            name : 'refNo',
            id : 'refNo',
            inputType : 'textfield',
            maskRe : /[A-Za-z0-9]/,
            margin : '0 0 0 20',
            enforceMaxLength : true,
            maxLength : 32,
            width : 300,
            triggers : {
                clears : {
                    cls : 'x-form-clear-trigger',
                    handler : function() {
                        this.setValue('');
                    }
                }
            }
        },{
            xtype : 'label',
            text : 'Remarks',
            margin : '10 0 5 20',
            cls : ['block-text', 'bold-text']
        },{
            xtype : 'textareafield',
            margin : '0 0 0 20',
            width : 300,
            maxRows : 4,
            name : 'remarks',
            id : 'remarks'
        },{
            xtype : 'label',
            text : 'Photo',
            margin : '10 0 5 20',
            cls : ['block-text', 'bold-text']
        },{
            xtype : 'filefield',
            name : 'photo',
            id : 'photo',
            msgTarget : 'side',
            width : 300,
            margin : '0 0 0 20',
            buttonText : 'Select',
            clearOnSubmit : false,
            listeners : {
                afterrender : function() {
                    this.fileInputEl.set({
                        accept : 'image/*'
                    });
                },

                change : function () {
                    var form = this.up('form');
                    var file = form.getEl().down('input[type=file]').dom.files[0];
                    var _URL = window.URL || window.webkitURL;
                    var img = new Image();

                    img.onload = function() {
                        if (this.width < 400 || this.height < 400) {
                            Ext.Msg.alert('Warning', 'Photo is too small. Select a bigger one.');
                            Ext.getCmp('photo').inputEl.dom.value = '';
                        }
                    };

                    img.onerror = function() {
                        Ext.Msg.alert('Warning', 'Chosen file is not an image.');
                        Ext.getCmp('photo').inputEl.com.value = '';
                    };

                    img.src = _URL.createObjectURL(file);

                    var fileSize = file.size;

                    if (file.type != 'image/jpeg') {
                        Ext.Msg.alert('Warning', 'Photo should be jpeg. Select another one');
                        Ext.getCmp('photo').inputEl.dom.value = '';
                    }

                    if (fileSize > MAX_IMAGE_SIZE) {
                        Ext.Msg.alert('Warning', "Selected image is too big.");
                        Ext.getCmp('photo').inputEl.dom.value = '';
                    }
                }
            }
        },{
            xtype : 'label',
            text : 'Signature',
            margin : '10 0 5 20',
            cls : ['block-text', 'bold-text']
        },
        signPad,{
            xtype : 'button',
            text : 'Clear Signature',
            margin : '0 0 0 20',
            handler : function() {
                signIsClicked = false;
                signaturePad.clear();
            }
        },{
            xtype : 'button',
            disabled : true,
            margin : '20 0 0 20',
            width : 500,
            text : 'Add Initial Joborder',
            itemId : 'buttonToBind',
            formBind : true,
            cls : 'block-text',
            handler : function() {
                var form = this.up('form');

                if (form.isValid()) {

                    form.submit({
                        waitMsg : 'Adding initial joborder...',
                        url : 'initialjoborder',
                        method : 'post',
                        params : {
                            joSignature : signaturePad.toDataURL('image/png'),
                            preparedBy : preparedBy,
                            source : source,
                            customer : customer,
                            imageType : imageType
                        },
                        success : function(form, action) {
                            Ext.MessageBox.show({
                                title : 'Message',
                                msg : 'Successfully added initial joborder',
                                icon : Ext.MessageBox.WARNING,
                                buttons : Ext.MessageBox.OKCANCEL,
                                fn : function() {
                                    window.location.reload(true);
                                }
                            });
                        },
                        failure : function(form, action) {
                            try {
                                var assoc = Ext.JSON.decode(action.response.responseText);
                                Ext.Msg.alert('', assoc['reason']);
                            } catch (err) {
                                Ext.Msg.alert("Exception", msg);
                            }
                        }
                    });
                }
            }
        }
    ]
});

var signIsClicked = false;

Ext.onReady(function(){

    Ext.QuickTips.init();

    Ext.create('Ext.container.Viewport', {
        layout : 'border',
        renderTo : Ext.getBody(),
        id : 'viewportJoborder',
        items : [initialJoborderForm]
    });

    signaturePad = new SignaturePad(Ext.get('signature-pad').dom, {
        backgroundColor: 'rgba(255, 255, 255, 0)',
        penColor: 'rgb(0, 0, 0)'
    });

    Ext.get('menuId').on('touchstart', function() {
        if (navClose) {
            openNav();
        } else {
            closeNav();
        }
    });

    window.addEventListener('orientationchange', function() {
        document.getElementById('mySidenav').style.paddingTop = '76px';
    });

    window.addEventListener('resize', function() {
        document.getElementById('mySidenav').style.paddingTop = '76px';
    });

    document.getElementById('mySidenav').style.paddingTop = '76px';
});