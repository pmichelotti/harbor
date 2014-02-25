<%@include file="/libs/harbor/components/global.jsp" %>

<div class="navbar navbar-default" role="navigation">
    <div class="container-fluid">
        <div class="navbar-collapse collapse">
            <ul class="nav navbar-nav">

                <c:choose>
                    <%-- Here we test to see if the user has configured the navigation to
                        be built manually, or automatically. If manual, we populate the nav
                        with nav elements.

                        Each nav element will have its own configuration as well.--%>
                    <c:when test="${globalNavigation.isAutoGenerated}">
                        <%-- Show autogenerated navigation elements--%>
                        <c:if test="${globalNavigation.hasRootNode}">
                            <c:forEach var="currentNode" items="${globalNavigation.rootChildrenAsRenderable}" varStatus="status">
                                ${currentNode.renderedTreeNodeValue}
                            </c:forEach>
                        </c:if>
                    </c:when>
                    <c:otherwise>
                        <%--li is inserted due to CQ's insertion of divs for editing the nav elements. This breaks the navbar.--%>
                        <c:forEach var="currentElement" items="${globalNavigation.navigationElementList}" varStatus="status">
                            <li <c:if test="${currentElement.hasDropdown}">class="dropdown"</c:if>>
                                 <cq:include path="${currentElement.name}" resourceType="harbor/components/content/navigationelement" />
                            </li>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </div>
</div>