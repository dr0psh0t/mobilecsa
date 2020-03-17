Ext.onReady(function(){
    Ext.create('Ext.container.Viewport', {
        layout: 'fit',
        items: [
            Ext.create('Ext.container.Container', {
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                items: [{
                    xtype: 'container',
                    flex: 1,
                    layout: {
                        type: 'hbox',
                        align: 'stretch'
                    },
                    items: [{
                        xtype: 'panel',
                        title: '1',
                        flex: 1
                    },{
                        xtype: 'panel',
                        title: 2,
                        flex: 1
                    }]
                },{
                    xtype: 'container',
                    flex: 1,
                    layout: {
                        type: 'hbox',
                        align: 'stretch'
                    },
                    items: [{
                        xtype: 'panel',
                        title: '1',
                        flex: 1
                    },{
                        xtype: 'panel',
                        title: 2,
                        flex: 1
                    }]
                },{
                    xtype: 'container',
                    flex: 1,
                    layout: {
                        type: 'hbox',
                        align: 'stretch'
                    },
                    items: [{
                        xtype: 'panel',
                        title: '1',
                        flex: 1
                    },{
                        xtype: 'panel',
                        title: 2,
                        flex: 1
                    }]
                }]
            })
        ],
        renderTo: Ext.getBody()
    });
});