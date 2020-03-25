var conn = Ext.data.Connection();

function displayAdminGrid(adminId) {

    Ext.create('Ext.Window', {
        id : 'adminPropGridWindow',
        title : 'Admin details',
        width : 400,
        height : 262,
        minWidth : 400,
        minHeight : 262,
        layout : 'fit',
        plain : true,
        modal : true,
        items : [
            Ext.create('Ext.grid.property.Grid', {
                id : 'adminPropGrid',
                sortableColumns : false,
                style : { fontSize : '20px' }
            })
        ],
        buttons : [{
            xtype : 'button',
            text : 'Close',
            handler : function() {
                Ext.getCmp('adminPropGrid').destroy();
                Ext.getCmp('adminPropGridWindow').destroy();
            }
        }]
    }).show();

    Ext.Ajax.request({
        url: 'getadministratorbyparams',
        method: 'POST',
        params: { adminId: adminId },
        success: function (response, opts) {
            var assoc = Ext.decode(response.responseText);

            Ext.getCmp('adminPropGrid').setSource({
                'Username': assoc.username,
                'Creator': assoc.creator,
                'Date Created': assoc.dateStamp,
                'Secret Key': assoc.secretKey
            });

            Ext.getCmp('adminPropGrid').setListeners({
                beforeedit : function(){
                    return false;
                },
                rowdblclick : function(dis, record, element, rowIndex, e, eOpts) {
                }
            });
        },
        failure: function (response) {
            var assoc = Ext.decode(response.responseText);
            Ext.Msg.alert("Failed", assoc['reason']);
        }
    });
}

function displayCsaGrid(csaId) {

    Ext.create('Ext.Window', {
        id : 'csaGridWindow',
        title : 'User details',
        width : 400,
        height : 262,
        minWidth : 400,
        minHeight : 262,
        layout : 'fit',
        plain : true,
        modal : true,
        items : [
            Ext.create('Ext.grid.property.Grid', {
                id : 'csaGrid',
                sortableColumns : false,
                style : { fontSize : '20px' }
            })
        ],
        buttons : [{
            xtype : 'button',
            text : 'Close',
            handler : function() {
                Ext.getCmp('csaGrid').destroy();
                Ext.getCmp('csaGridWindow').destroy();
            }
        }]
    }).show();

    Ext.Ajax.request({
        url: 'getcsabyparams',
        method: 'POST',
        params: { csaId: csaId },
        success: function (response, opts) {
            var assoc = Ext.decode(response.responseText);

            Ext.getCmp('csaGrid').setSource({
                'Firstname' : assoc.firstname,
                'Lastname' : assoc.lastname,
                'Username' : assoc.username,
                'Secret Key' : assoc.secretKey
            });

            Ext.getCmp('csaGrid').setListeners({
                beforeedit : function(){
                    return false;
                }
            });
        },
        failure: function (response, opts) {
            var assoc = Ext.decode(response.responseText);
            Ext.Msg.alert("Failed", assoc['reason']);
        }
    });
}

function displayCompanyInfo(customerId, stretched) {
    var height;
    var width;

    if (stretched) {
        height = '100%';
        width = '100%';
    } else {
        height = 630;
        width = 700;
    }

    Ext.create('Ext.Window', {
        id: 'companyGridWindow',
        title: 'Company Details',
        width: width,
        height: height,
        minWidth: width,
        minHeight: height,
        layout: 'fit',
        plain: true,
        modal: true,
        items: [
            Ext.create('Ext.grid.property.Grid', {
                id: 'companyGrid',
                sortableColumns: false,
                source: {},
                listeners: {}
            })
        ],
        buttons: [{
            xtype: 'button',
            text: 'Close',
            handler: function () {
                Ext.getCmp('companyGrid').destroy();
                Ext.getCmp('companyGridWindow').destroy();
            }
        }]
    }).show();

    Ext.Ajax.request({
        url: 'getcustomercompanybyparams',
        method: 'POST',
        params: {customerId: customerId},
        success: function (response) {
            var assoc = Ext.decode(response.responseText);

            Ext.getCmp('companyGrid').setSource({
                'Photo' : 'Double-click to display photo',
                'Company' : assoc.company,
                'Address' : assoc.address,
                'Location' : 'Double-click to display location',
                'City' : assoc.city,
                'Country' : assoc.country,
                'Zip' : assoc.zip,
                'Telephone' : assoc.telnum,
                'Fax Number' : assoc.faxnum,
                'Contact Person' : assoc.contactperson,
                'Contact Number' : assoc.contactnumber,
                'Date Added' : assoc.dateadded,
                'Assigned CSA' : assoc.assignedCsa,
                'Signature' : 'Double-click to display signature'
            });

            Ext.getCmp('companyGrid').setListeners({
                beforeedit : function(){
                    return false;
                },
                rowdblclick : function (dis, record) {
                    if (record.id === 'Location') {
                        showMap(assoc.latitude, assoc.longitude, assoc.company, stretched);
                        //window.open('https://www.google.com/maps?q='+rec.latitude+',+'+rec.longitude, '_blank');
                    } else if (record.id === 'Photo') {
                        displayPhoto('getcustomerphoto?customerId='+assoc.customerId);
                    } else if (record.id === 'Signature') {
                        displayPhoto('getcustomersignature?customerId='+assoc.customerId);
                    }
                }
            });
        },
        failure: function (response) {
            var assoc = Ext.decode(response.responseText);
            Ext.Msg.alert("Failed", assoc['reason']);
        }
    });
}

function displayCustomerInfo(customerId, stretched) {
    var height;
    var width;

    if (stretched) {
        height = '100%';
        width = '100%'
    } else {
        height = 630;
        width = 700;
    }

    Ext.create('Ext.Window', {
        id : 'customerGridWindow',
        title : 'Customer Details',
        width : width,
        height : height,
        minWidth : width,
        minHeight : height,
        layout : 'fit',
        plain : true,
        modal : true,
        items : [
            Ext.create('Ext.grid.property.Grid', {
                id : 'customerGrid',
                sortableColumns : false,
                source : { },
                listeners: { }
            })
        ],
        buttons: [{
            xtype: 'button',
            text: 'Close',
            handler: function() {
                Ext.getCmp('customerGrid').destroy();
                Ext.getCmp('customerGridWindow').destroy();
            }
        }]
    }).show();

    Ext.Ajax.request({
        url: 'getindividual',
        method: 'POST',
        params : { customerId : customerId },
        success: function(response) {
            var assoc = Ext.decode(response.responseText);

            Ext.getCmp('customerGrid').setSource({
                'Photo' : 'Double-click to display photo',
                'Lastname' : assoc.lastname,
                'Firstname' : assoc.firstname,
                'Address' : assoc.address,
                'City' : assoc.city,
                'Country' : assoc.country,
                'Zip' : assoc.zip,
                'Location' : 'Double-click to display location',
                'Telephone' : assoc.telnum,
                'Fax' : assoc.faxnum,
                'Date of birth' : assoc.dateofbirth,
                'Date added' : assoc.dateadded,
                'Assigned CSA' : assoc.assignedCsa,
                'Signature' : 'Double-click to display signature'
            });

            Ext.getCmp('customerGrid').setListeners({
                beforeedit: function() {
                    return false;
                },
                rowdblclick: function (dis, record) {
                    if (record.id === 'Location') {
                        showMap(assoc.latitude, assoc.longitude, assoc.lastname+', '+assoc.firstname, stretched);
                        //window.open('https://www.google.com/maps?q='+assoc.latitude+',+'+assoc.longitude, '_blank');
                    } else if(record.id === 'Photo') {
                        displayPhoto('getcustomerphoto?customerId='+assoc.customerId, stretched);
                    } else if(record.id === 'Signature') {
                        displayPhoto('getcustomersignature?customerId='+assoc.customerId, stretched);
                    }
                }
            });
        },
        failure: function(response) {
            var assoc = Ext.decode(response.responseText);
            Ext.Msg.alert("Failed", assoc['reason']);
        }
    });
}

function displayContacts(contactId, stretched) {
    var height;
    var width;

    if (stretched) {
        height = '100%';
        width = '100%';
    } else {
        height = 630;
        width = 700;
    }

    Ext.create('Ext.Window', {
        id : 'contactGridWindow',
        title : 'Contact Details',
        width : width,
        height : height,
        minWidth : width,
        minHeight : height,
        layout : 'fit',
        plain : true,
        modal : true,
        items : [
            Ext.create('Ext.grid.property.Grid', {
                id : 'contactGrid',
                sortableColumns : false,
                source : {},
                listeners : {}
            })
        ],
        buttons : [{
            xtype : 'button',
            text : 'Close',
            handler : function() {
                Ext.getCmp('contactGrid').destroy();
                Ext.getCmp('contactGridWindow').destroy();
            }
        }]
    }).show();

    Ext.Ajax.request({
        url: 'getcontactsbyparams',
        method: 'POST',
        params : { contactId : contactId },
        success: function(response) {
            var assoc = Ext.decode(response.responseText);

            console.log(assoc);

            Ext.getCmp('contactGrid').setSource({
                'Photo' : 'Double-click to see photo',
                'Lastname' : assoc.lastname,
                'Firstname' : assoc.firstname,
                'M.I.' : assoc.mi,
                'Address' : assoc.address,
                'City' : assoc.city,
                'Province' : assoc.province,
                'Country' : assoc.country,
                'Zip Code' : assoc.zipCode,
                'Location' : 'Double-click to display location',
                'Industry' : assoc.industry,
                'Plant' : assoc.plantAssociated,
                'Date Of Birth' : assoc.dateOfBirth,
                'CSA' : assoc.csa,
                'Job Position' : assoc.jobPosition,
                'Telephone' : assoc.phone,
                'Mobile' : assoc.mobile,
                'Emergency Contact' : assoc.emergencyContact,
                'Signature' : 'Double-click to display signature',
                'ER' : assoc.er + ' %',
                'MF' : assoc.mf + ' %',
                'Calib' : assoc.calib + ' %'
            });

            Ext.getCmp('contactGrid').setListeners({
                beforeedit : function(){
                    return false;
                },
                rowdblclick : function(dis, record){
                    if (record.id === 'Location') {
                        showMap(assoc.latitude, assoc.longitude, assoc.lastname + ', ' + assoc.firstname, stretched);
                    } else if (record.id === 'Signature') {
                        displayPhoto('getcontactsignature?contactId='+assoc.contactId, stretched);
                    } else if (record.id === 'Photo') {
                        displayPhoto('getcontactsphoto?contactId='+assoc.contactId, stretched);
                    }
                }
            });
        },
        failure: function(response) {
            var assoc = Ext.decode(response.responseText);
            Ext.Msg.alert("Failed", assoc['reason']);
        }
    });
}

function displayPhoto(link, sign) {
    Ext.create('Ext.Window', {
        id : 'pictureWindow',
        title : 'Photo',
        width : 800,
        height : 600,
        minWidth : 800,
        minHeight : 600,
        layout : 'fit',
        plain : true,
        modal : true,
        items : [
            Ext.create('Ext.panel.Panel', {
                region : 'center',
                id : 'picture',
                items: [
                    Ext.create('Ext.Img', {
                        src : link,
                        height : 600,
                        width : 800
                    })
                ]
            })
        ],
        buttons: [{
            text: 'Open in new tab',
            handler: function() {
                window.open(link, '_blank');
            }
        },{
            text: 'Close',
            handler: function() {
                Ext.getCmp('pictureWindow').destroy();
            }
        }]
    }).show();

    /*
    var bodyWidth = document.body.clientWidth;
    var bodyHeight = document.body.clientHeight;
    var diff;

    var img2 = new Image();
    img2.onload = function() {

        var imgHeight = img2.height;
        var imgWidth = img2.width;

        var imageHeight;
        var resizedHeight;
        var resizedWidth;
        var diff;
        var percent;

        if (bodyHeight > bodyWidth) {
            if (imgWidth > bodyWidth) {
                diff = imgWidth - bodyWidth;
                percent = imgWidth / diff;

                resizedWidth = imgWidth - (imgWidth / percent);
                resizedHeight = imgHeight - (imgHeight / percent);
            } else {
                if (imgHeight > bodyHeight) {
                    diff = imgHeight - bodyHeight;
                    percent = imgHeight / diff;

                    resizedHeight = imgHeight - (imgHeight / percent);
                    resizedWidth = imgWidth - (imgWidth / percent);
                } else {
                    resizedHeight = imgHeight;
                    resizedWidth = imgWidth;
                }
            }
        } else {
            if (imgHeight > bodyHeight) {
                diff = imgHeight - bodyHeight;
                percent = imgHeight / diff;

                resizedHeight = imgHeight - (imgHeight / percent);
                resizedWidth = imgWidth - (imgWidth / percent);
            } else {
                if (imgWidth > bodyWidth) {
                    diff = imgWidth - bodyWidth;
                    percent = imgWidth / diff;

                    resizedWidth = imgWidth - (imgWidth / percent);
                    resizedHeight = imgHeight - (imgHeight / percent);
                } else {
                    resizedHeight = imgHeight;
                    resizedWidth = imgWidth;
                }
            }
        }

        resizedHeight = resizedHeight * 0.85;
        resizedWidth = resizedWidth * 0.85;
        resizedHeight += 90;

        if (bodyHeight <= resizedHeight) {
            var origResizedHeight = resizedHeight;
            resizedHeight = bodyHeight * 0.90;
            percent = origResizedHeight / (origResizedHeight - resizedHeight);
            resizedWidth = resizedWidth - (resizedWidth / percent);
            imageHeight = resizedHeight - 90;
        }

        if (bodyWidth <= resizedWidth) {
            var origResizedWidth = resizedWidth;
            resizedWidth = bodyWidth * 0.90;
            percent = origResizedWidth / (origResizedWidth - resizedWidth);
            resizedHeight = resizedHeight - (resizedHeight / percent);
            imageHeight = resizedHeight - 90;
        }

        Ext.create('Ext.Window', {
            id : 'pictureWindow',
            title : 'Photo',
            width : resizedWidth,
            height : resizedHeight,
            minWidth : resizedWidth,
            minHeight : resizedHeight,
            layout : 'fit',
            plain : true,
            modal : true,
            items : [
                Ext.create('Ext.panel.Panel', {
                    region : 'center',
                    id : 'picture',
                    width : resizedWidth,
                    height : resizedHeight,
                    items: [
                        Ext.create('Ext.Img', {
                            src : link,
                            height : imageHeight,
                            width : resizedWidth
                        })
                    ]
                })
            ],
            buttons: [{
                text: 'Close',
                handler: function() {
                    Ext.getCmp('pictureWindow').destroy();
                }
            }]
        }).show();
    };

    img2.src = link;*/
}

function showMap(latitude, longitude, entity, stretched) {
    try {
        var resizedHeight;
        var resizedWidth;

        if (stretched) {
            resizedWidth = document.body.clientWidth * 0.85;
            resizedHeight = document.body.clientHeight * 0.85;
        } else {
            resizedWidth = document.body.clientWidth * 0.60;
            resizedHeight = document.body.clientHeight * 0.95;
        }

        Ext.define('Customer.Map', {
            extend: 'Ext.panel.Panel',
            alias : 'widget.smartcitymaps',
            itemId: 'map',
            border: false,
            autoScroll: true,
            html : "<div style=\"width:"+(resizedWidth-7)+"px; height:"+(resizedHeight-93)+"px\" id=\"myMap\"></div>",
            constructor: function(c) {

                var me = this;
                var loadMap = function(lat, lng) {

                    var me = this;
                    //var location = { lat : lat, lng : lng};

                    try {
                        me.map = new google.maps.Map(document.getElementById("myMap"), {
                            zoom: 13,
                            center: new google.maps.LatLng(lat, lng),
                            mapTypeId: google.maps.MapTypeId.ROADMAP
                        });
                    } catch (e) {
                        return false;
                    }

                    /*var marker = new google.maps.Marker({
                        position : new google.maps.LatLng(lat, lng),
                        map : me.map,
                        //label : entity
                    });*/

                    me.infowindow = new google.maps.InfoWindow();
                    //me.infowindow.setContent(entity);
                    //me.infowindow.open(me.map, marker);
                };

                me.listeners = {
                    afterrender: function() {
                        loadMap(latitude, longitude);
                    }
                };

                me.callParent(arguments);
            }
        });

        var mapPanel = Ext.create('Customer.Map', {
            width : resizedWidth,
            height : resizedHeight,
            id : 'mapPanelId'
        });

        Ext.create('Ext.Window', {
            id : 'mapId',
            title : entity,
            width : resizedWidth,
            height : resizedHeight,
            minWidth : resizedWidth,
            minHeight : resizedHeight,
            layout : 'fit',
            plain : true,
            modal : true,
            items : [mapPanel],
            listeners : {
                show : function() {},
                destroy : function() {}
            },
            buttons : [{
                text: 'Open in new tab',
                handler: function() {
                    //window.open("https://www.google.com/maps/search/?api=1&query=47.5951518,-122.3316393", '_blank');
                    window.open("https://www.google.com/maps/search/?api=1&query="+latitude+","+longitude, '_blank');
                }
            },{
                text : 'Close',
                handler : function() {
                    Ext.getCmp('mapPanelId').destroy();
                    Ext.getCmp('mapId').destroy();
                }
            }]
        }).show();
    } catch (e) {
        //  destroy window and map panel
        Ext.getCmp('mapPanelId').destroy();
        Ext.getCmp('mapId').destroy();

        reportMapCrash(e.message + '. Origin : jsfunction.js');

        Ext.Msg.alert('Error', e.message);
        //location.reload();
    }
}

function displayJoborder(initialJOId) {

    Ext.create('Ext.Window', {
        id : 'joborderWindow',
        title : 'notitle',
        width : 400,
        height : 550,
        minWidth : 400,
        minHeight : 550,
        layout : 'fit',
        plain : true,
        modal : true,
        items : [
            Ext.create('Ext.grid.property.Grid', {
                id : 'joGrid',
                sortableColumns : false,
                style : { fontSize : '20px' }
            })
        ],
        buttons : [{
            text : 'Close',
            handler : function() {
                Ext.getCmp('joGrid').destroy();
                Ext.getCmp('joborderWindow').destroy();
            }
        }]
    }).show();

    Ext.Ajax.request({
        url: 'getinitialjoborder',
        method: 'POST',
        params: { initialJoborderId: initialJOId },
        success: function(response, opts) {
            var assoc = Ext.decode(response.responseText);

            Ext.getCmp('joGrid').setSource({
                'Image' : 'Tap to view image',
                'Category' : assoc['quotation']['category'],
                'Customer' : assoc['quotation']['customer'],
                'Date Committed' : assoc['quotation']['dateCommitted'],
                'Date Received' : assoc['quotation']['dateReceived'],
                'Make' : assoc['quotation']['mak'],
                'Mobile' : assoc['quotation']['mobile'],
                'Model' : assoc['quotation']['model'],
                'PO Date' : assoc['quotation']['poDate'],
                'Purchase Order' : assoc['quotation']['purchaseOrder'],
                'Reference No' : assoc['quotation']['referenceNo'],
                'Remarks' : assoc['quotation']['remarks'],
                'Serial No' : assoc['quotation']['serialNo'],
                'Source' : assoc['quotation']['source'],
                'Signature' : 'Tap to view signature'
            });

            Ext.getCmp('joGrid').setListeners({
                beforeedit : function() {
                    return false;
                },
                rowdblclick : function(dis, record) {
                    if (record.id === 'Image') {
                        displayPhoto(
                            'getinitialjoborderphoto?initialJoborderId='+assoc['quotation']['initialJoborderId'],
                            false);
                    } else if (record.id === 'Signature') {
                        displayPhoto(
                            'getinitialjobordersignature?initialJoborderId='+assoc['quotation']['initialJoborderId'],
                            false);
                    }
                }
            });
        },
        failure: function(response) {
            var assoc = Ext.decode(response.responseText);
            Ext.Msg.alert("Failed", assoc['reason']);
        }
    });
}

function sendData(url, method, params, callback) {
    conn.request({
        url : url,
        method : method,
        params : params,
        callback : callback
    });
}

function reportMapCrash(description) {
    var conn = new Ext.data.Connection();

    conn.request({
        url : 'crashreport',
        method : 'post',
        params : { description : description }
    });
}

function openNav() {
    document.getElementById("mySidenav").style.width = "235px";
    navClose = false;
    navOpen = true;
}

function closeNav() {
    document.getElementById("mySidenav").style.width = "0";
    navClose = true;
    navOpen = false;
}

function logout() {
    closeNav();

    setTimeout(function() {
        var conn = new Ext.data.Connection();
        conn.request({
            url : 'logout',
            method : 'post',
            callback : function () {
                location.assign('index.jsp');
            }
        });
    }, 500);
}

function navigate(page) {
    closeNav();

    setTimeout(function() {
        location.assign(page);
    }, 600);
}

function addContacts() {
    navigate('addcontacts.jsp');
}

function addChoices() {
    navigate('addchoices.jsp');
}

function searchChoices() {
    navigate('searchchoices.jsp');
}

function gotoHome() {
    navigate('homeuser.jsp');
}

function gotoJoborder() {
    navigate('initialjoborder.jsp');
}