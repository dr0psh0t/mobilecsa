setInterval(function() {
    //alert('Hello');
    //console.log('test');

    var assoc;

    Ext.Ajax.request({
        url: 'mcdrester',
        method: 'POST',
        params: {
            uId: 2,
            h: 3,
            fw: 4,
            timestamp: 5,
            mId: 10007
        },
        success: function(response, otps) {
            assoc = Ext.decode(response.responseText);
            console.log(assoc);
        },
        failure: function(response, opts) {
            assoc = Ext.decode(response.responseText);
            console.log(assoc);
        }
    });

}, 2000);