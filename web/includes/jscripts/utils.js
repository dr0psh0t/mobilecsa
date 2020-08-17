var utilSignature = 'data:image/png;base64,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';

function setMap(latComponent, lngComponent) {
    try {
        var resizedWidth = document.body.clientWidth * 0.70;
        var resizedHeight = document.body.clientHeight * 0.95;

        Ext.define('Customer.Map', {
            extend: 'Ext.panel.Panel',
            alias: 'widget.smartcitymaps',
            itemId: 'map',
            item: 'map',
            width: '100%',
            border: false,
            html: "<div style=\"width:"+(resizedWidth)+"px; height:"+(resizedHeight - 90)+"px\" id=\"myMap\"></div>",
            constructor: function(c) {
                var me = this;
                var marker;
                var loadMap = function(lat, lng) {
                    var me = this;
                    var location = { lat: lat, lng: lng};

                    try {
                        me.map = new google.maps.Map(document.getElementById("myMap"), {
                            clickableIcons: false,
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
                        position: new google.maps.LatLng(lat, lng),
                        map: me.map
                    });

                    google.maps.event.addListener(me.map, 'click', function(e) {
                        marker.setMap(null);
                        marker = new google.maps.Marker({
                            position: new google.maps.LatLng(e.latLng.lat(), e.latLng.lng()),
                            map: me.map
                        });

                        latComponent.setValue(e.latLng.lat());
                        lngComponent.setValue(e.latLng.lng());
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
            id: 'mapWindow',
            title: 'Google Maps',
            width: resizedWidth,
            height: resizedHeight,
            minWidth: resizedWidth,
            minHeight: resizedHeight,
            layout: 'fit',
            plain: true,
            modal: true,
            items: [Ext.create('Customer.Map', {
                width: '100%',
                height: '95%',
                id: 'mapPanel'
            })],
            buttons: [{
                text: 'Close',
                handler: function() {
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

Ext.define('Industry', {
    extend: 'Ext.data.Model',
    fields: [
        { name: 'industryname', type: 'string' },
        { name: 'industryid', type: 'int' }
    ]
});

var industryStore = Ext.create('Ext.data.Store', {
    model: 'Industry',
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: 'getindustries',
        method: 'post',
        //  actionMethod config for post request
        actionMethods: {
            create: 'post',
            read: 'post',
            update: 'post',
            destroy: 'post'
        },
        reader: {
            type: 'json',
            rootProperty: 'industryStore'
        }
    }
});

Ext.define('Plant', {
    extend: 'Ext.data.Model',
    fields: [
        { name: 'plant_id', type: 'int' },
        { name: 'plant_name', type: 'string' }
    ]
});

var plantStore = Ext.create('Ext.data.Store', {
    model: 'Plant',
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: 'getplantassociated',
        method: 'post',
        actionMethods: {
            create: 'post',
            read: 'post',
            update: 'post',
            destroy: 'post'
        },
        reader: {
            type: 'json',
            rootProperty: 'plantStore'
        }
    }
});

Ext.define('CityId', {
    extend: 'Ext.data.Model',
    fields: [
        { name: 'cityname', type: 'string'},
        { name: 'cityid', type: 'int' }
    ]
});

var cityIdStore = Ext.create('Ext.data.Store', {
    model: 'CityId',
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: 'getcities',
        method: 'post',
        actionMethods: {
            create: 'post',
            read: 'post',
            update: 'post',
            destroy: 'post'
        },
        reader: {
            type: 'json',
            rootProperty: 'cityStore'
        }
    }
});

Ext.define('Province', {
    extend: 'Ext.data.Model',
    fields: [
        { name: 'provincename', type: 'string' },
        { name: 'provinceid', type: 'string' }
    ]
});

var provinceStore = Ext.create('Ext.data.Store', {
    model: 'Province',
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: 'getprovinces',
        method: 'post',
        actionMethods: {
            create: 'post',
            read: 'post',
            update: 'post',
            destroy: 'post'
        },
        reader: {
            type: 'json',
            rootProperty: 'provinceStore'
        }
    }
});

Ext.define('CountryId', {
    extend: 'Ext.data.Model',
    fields: [
        { name: 'countryname', type: 'string' },
        { name: 'countryid', type: 'int' }
    ]
});

var countryIdStore = Ext.create('Ext.data.Store', {
    model: 'CountryId',
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: 'getcountries',
        method: 'post',
        actionMethods: {
            create: 'post',
            read: 'post',
            update: 'post',
            destroy: 'post'
        },
        reader: {
            type: 'json',
            rootProperty: 'countryStore'
        }
    }
});

Ext.define('AreaCode', {
    extend: 'Ext.data.Model',
    fields: [
        { name: 'area', type: 'string' },
        { name: 'areaCodeId', type: 'int' }
    ]
});

var areaCodeStore = Ext.create('Ext.data.Store', {
    model: 'AreaCode',
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: 'getareacode',
        method: 'post',
        actionMethods: {
            create: 'post',
            read: 'post',
            update: 'post',
            destroy: 'post'
        },
        reader: {
            type: 'json',
            rootProperty: 'areaCodeStore'
        }
    }
});

Ext.define('CountryCode', {
    extend: 'Ext.data.Model',
    fields: [
        { name: 'country', type: 'string' },
        { name: 'countryCode', type: 'int' },
        { name: 'countryCodeId', type: 'int' }
    ]
});

var countryCodeStore = Ext.create('Ext.data.Store', {
    model: 'CountryCode',
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: 'getcountrycode',
        method: 'post',
        actionMethods: {
            create: 'post',
            read: 'post',
            update: 'post',
            destroy: 'post'
        },
        reader: {
            type: 'json',
            rootProperty: 'countryCodeStore'
        }
    }
});

var numberStore = Ext.create('Ext.data.Store', {
    fields: ['percent'],
    data: getNumberObjects()
});

function setZip(city) {
    if (city !== null) {
        if (city !== '') {
            sendRequest('getzipcode', 'post', { city: city },
                function (o, s, response) {

                    var assoc = Ext.JSON.decode(response.responseText);
                    Ext.getCmp('zip').setValue(assoc['zipCode']);

                    if (!assoc['success']) {
                        Ext.Msg.alert('Fail', assoc['reason']);
                    }
                });
        }
    }
}

function getNumberObjects() {
    var numberArrayOfObjects = [];

    for (var x = 0; x < 101; ++x) {
        numberArrayOfObjects.push({'percent': x});
    }

    return numberArrayOfObjects;
}