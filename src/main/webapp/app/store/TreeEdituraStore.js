Ext.define('BM.store.TreeEdituraStore', {
            extend: 'Ext.data.TreeStore',
            model: 'BM.model.TreeEdituraModel',
            autoLoad: true,
            proxy: {
                type: 'ajax',
                url: 'editura/tree-load',
                reader: {
                    type: 'json',
                    method: 'POST'
                }
            },
            root: {
                expanded: true,
                name: 'EDITURI'
            },
            sorters: [
                {
                    property: 'treeItemName',
                    direction: 'ASC'
                }
            ]
        });