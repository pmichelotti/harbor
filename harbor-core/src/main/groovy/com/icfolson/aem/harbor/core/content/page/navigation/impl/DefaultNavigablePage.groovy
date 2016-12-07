package com.icfolson.aem.harbor.core.content.page.navigation.impl

import com.google.common.collect.Lists
import com.icfolson.aem.harbor.api.content.page.navigation.NavigablePage
import com.icfolson.aem.harbor.core.content.page.navigation.NavigablePages
import com.icfolson.aem.harbor.core.content.page.navigation.NavigationElementConfiguration
import com.icfolson.aem.library.api.link.NavigationLink
import com.icfolson.aem.library.api.page.PageDecorator

class DefaultNavigablePage implements NavigablePage {

    @Delegate
    private final PageDecorator pageDecorator
    private final NavigationElementConfiguration navigationElementConfiguration

    private List<NavigablePage> navigablePageList

    DefaultNavigablePage(PageDecorator pageDecorator, NavigationElementConfiguration navigationElementConfiguration) {
        this.pageDecorator = pageDecorator
        this.navigationElementConfiguration = navigationElementConfiguration
    }

    @Override
    List<NavigablePage> getNavigableChildren() {

        if (navigablePageList == null) {
            navigablePageList = Lists.newArrayList()

            if (navigationElementConfiguration.navigationDepth > 0) {
                pageDecorator.getChildren(navigationElementConfiguration.respectHideInNavigation).each {
                    PageDecorator currentPageDecorator ->

                        if (navigationElementConfiguration.currentPage.isPresent()) {
                            this.navigablePageList.add(
                                NavigablePages.forPageAndDepthAndChildPolicyAndCurrentPage(
                                    currentPageDecorator,
                                    navigationElementConfiguration.getNavigationDepth() - 1,
                                    navigationElementConfiguration.getRespectHideInNavigation(),
                                    navigationElementConfiguration.getCurrentPage().get()))
                        } else {
                            this.navigablePageList.add(
                                NavigablePages.forPageAndDepthAndChildPolicy(
                                    currentPageDecorator,
                                    navigationElementConfiguration.getNavigationDepth() - 1,
                                    navigationElementConfiguration.getRespectHideInNavigation()))
                        }
                }
            }
        }

        return navigablePageList
    }

    @Override
    boolean isHasChildNodes() {
        return !getNavigableChildren().isEmpty()
    }

    @Override
    Iterable<NavigablePage> getChildNodes() {

        return getNavigableChildren()
    }

    @Override
    Iterator<NavigablePage> getChildNodesIterator() {
        return getNavigableChildren().iterator()
    }

    @Override
    NavigationLink getNavigationLink() {
        if (navigationElementConfiguration.getCurrentPage().isPresent()) {
            if (navigationElementConfiguration.currentPage.get().path.startsWith(pageDecorator.path)) {
                return pageDecorator.getNavigationLink(true)
            }
        }

        return pageDecorator.getNavigationLink()
    }
}
