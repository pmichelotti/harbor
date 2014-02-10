Harbor.Components.Tabs = function(jQuery){
    return {
        addTab: function(component){
            var currentEditable = component;
            jQuery.post(
                currentEditable.path + '/*',
                {
                    'sling:resourceType' : 'harbor/components/content/tabs/tab',
                    'jcr:primaryType' : 'nt:unstructured',
                    ':nameHint' : 'tab'
                },
                function( data ) { currentEditable.refreshSelf(); }
            );
        }
    }
}(jQuery)