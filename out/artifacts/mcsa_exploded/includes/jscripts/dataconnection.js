/**
 * Created by wmdcprog on 9/11/2017.
 */

function sendRequest(url, method, params, callback) {
    var connect = new Ext.data.Connection();

    connect.request({
        url: url,
        method: method,
        params: params,
        callback: callback
    });
}