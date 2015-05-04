<%@include file="/libs/harbor/components/global.jsp"%>

<%--
    * The CTA modal.jsp script expects an object named 'cta' to be in the current page context.  Overriding implementations
    * need not maintain this requirement.
--%>

<div class="modal fade cta-modal" data-keyboard="false" role="dialog" id="${cta.modalId}" <c:if test="${isAuthor}">data-path="${cta.path}"</c:if>>
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-body">
                <cq:include path="container-par-${cta.id}" resourceType="foundation/components/parsys"/>
            </div>
        </div>
    </div>
</div>

<c:if test="${isEditMode || isDesignMode}">
    <script type="text/javascript">
        //TODO: Rework so this whole block is not included in Touch UI mode
        if ( typeof CQ !== 'undefined' ) {
            jQuery( document ).ready( function( $ ) {
                $( '#${cta.modalId}' ).on('show.bs.modal', function() {
                    Harbor.Components.editables.changeEditableContext( '${cta.path}/container-par-${cta.id}' );
                } );
                $( '#${cta.modalId}' ).on('hide.bs.modal', function() {
                    Harbor.Components.editables.resetEditableContext();
                } );
            } );
        }
    </script>
</c:if>
