package com.icfolson.aem.harbor.core.components.page.metapage.v1;

import com.citytechinc.cq.component.annotations.DialogField;
import com.citytechinc.cq.component.annotations.Option;
import com.citytechinc.cq.component.annotations.widgets.PathField;
import com.citytechinc.cq.component.annotations.widgets.Selection;
import com.citytechinc.cq.component.annotations.widgets.Switch;
import com.citytechinc.cq.component.annotations.widgets.TextField;
import com.day.cq.commons.Externalizer;
import com.google.common.collect.Lists;
import com.icfolson.aem.harbor.api.components.page.metapage.MetaPage;
import com.icfolson.aem.harbor.api.content.page.HierarchicalPage;
import com.icfolson.aem.harbor.core.components.page.global.v1.DefaultGlobalPage;
import com.icfolson.aem.library.api.page.PageDecorator;
import com.icfolson.aem.library.core.constants.PathConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;
import java.util.List;

@Model(adaptables = SlingHttpServletRequest.class, adapters = MetaPage.class, resourceType = {"wcm/foundation/components/page", DefaultGlobalPage.RESOURCE_TYPE})
public class DefaultMetaPage implements MetaPage {

    @Inject
    private PageDecorator currentPage;

    @Inject
    private Externalizer externalizer;

    @Inject
    private SlingHttpServletRequest request;

    @Override
    public boolean isDisableSchemaOrg() {
        return getCurrentPage().getInherited("disableSchemaOrg", false);
    }

    @Override
    public boolean isDisableTwitterCard() {
        return StringUtils.isBlank(getTwitterPublisherHandle());
    }

    @Override
    public boolean isDisableFacebookOpenGraph() {
        return StringUtils.isBlank(getFacebookOpenGraphType()) || "none".equals(getFacebookOpenGraphType());
    }

    @Override
    public String getPageName() {
        return StringUtils.isNotBlank(getCurrentPage().getPageTitle()) ? getCurrentPage().getPageTitle() : getCurrentPage().getName();
    }

    @Override
    public String getDescription() {
        return getCurrentPage().getDescription();
    }

    @Override
    public String getFullyQualifiedPageImage() {
        //TODO: See what this actually returns - it might need externalization
        return getCurrentPage().isHasImage() ? getCurrentPage().getImageSource().or("") : "";
    }

    @Override
    public String getFullyQualifiedPageUrl() {

        return getExternalUrl(getRequest(), getCurrentPage().adaptTo(Resource.class), "html");
    }

    @DialogField(fieldLabel = "Twitter Publisher Handle",
        fieldDescription = "e.g. @yourhandle.  If this value is present Twitter metadata will be included on the page.  Configuration is inherited by child pages.",
        ranking = 10)
    @TextField
    public String getTwitterPublisherHandle() {
        return getCurrentPage().getInherited("twitterPublisherHandle", "");
    }

    @DialogField(fieldLabel = "Open Graph Type",
        fieldDescription = "Select an og:Type value and Open Graph metadata will be included for the page",
        ranking = 20)
    @Selection(
        type = Selection.SELECT,
        options = {
            @Option(text = "None", value = "none"),
            @Option(text = "Article", value = "article"),
            @Option(text = "Book", value = "book"),
            @Option(text = "Profile", value = "profile"),
            @Option(text = "Website", value = "website"),
            @Option(text = "Movie", value = "video.movie"),
            @Option(text = "Episode", value = "video.episode"),
            @Option(text = "TV Show", value = "video.tv_show"),
            @Option(text = "Video", value = "video.other"),
            @Option(text = "Song", value = "music.song"),
            @Option(text = "Album", value = "music.album"),
            @Option(text = "Playlist", value = "music.playlist"),
            @Option(text = "Radio Station", value = "music.radio_station")
        }
    )
    public String getFacebookOpenGraphType() {
        return getCurrentPage().getInherited("ogType", "");
    }

    public String getCanonicalUrl() {
        return getCurrentPage().get("canonicalUrl", String.class).transform(canonicalUrl -> {
            final String url;

            if (canonicalUrl.startsWith("http:") || canonicalUrl.startsWith("https:")) {
                url = canonicalUrl;
            } else {
                url = getExternalUrl(request, getCurrentPage().adaptTo(Resource.class), "html");
            }

            return url;
        }).or("");
    }

    public String getHomePageTitle() {
        final HierarchicalPage hierarchicalPage = getCurrentPage().getContentResource().adaptTo(HierarchicalPage.class);

        String title = "";

        if (hierarchicalPage != null) {
            title = hierarchicalPage.getHomePage().transform(homePage -> {
                final String pageTitle = homePage.getPageTitle();

                return StringUtils.isNotBlank(pageTitle) ? pageTitle : homePage.getName();
            }).or("");
        }

        return title;
    }

    public boolean isNoIndex() {
        return getCurrentPage().get("noIndex", false);
    }

    public boolean isNoFollow() {
        return getCurrentPage().get("noFollow", false);
    }

    public List<String> getRobotsTags() {
        final List<String> robotsTags = Lists.newArrayList();

        if (isNoIndex()) {
            robotsTags.add("NOINDEX");
        }

        if (isNoFollow()) {
            robotsTags.add("NOFOLLOW");
        }

        return robotsTags;
    }

    public String getRobotsContent() {
        final StringBuilder content = new StringBuilder();

        final boolean noIndexIndicator = getCurrentPage().get("noindex", false);
        final boolean noFollowIndicator = getCurrentPage().get("nofollow", false);

        if (noIndexIndicator) {
            content.append("NOINDEX");
            content.append(noFollowIndicator ? ", NOFOLLOW" : ", FOLLOW");
        } else {
            if (noFollowIndicator) {
                content.append("INDEX, NOFOLLOW");
            }
        }

        return content.toString();
    }

    public String getExternalizerName() {
        return "publish";
    }

    private String getExternalUrl(SlingHttpServletRequest requestContext, Resource resource, String extension) {
        final ResourceResolver resourceResolver = requestContext.getResourceResolver();

        String externalLink = getExternalizer().externalLink(resourceResolver, getExternalizerName(),
                resourceResolver.map(requestContext, resource.getPath()));

        if (StringUtils.isNotBlank(extension)) {
            return externalLink + "." + extension;
        }

        return externalLink;
    }

    protected PageDecorator getCurrentPage() {
        return currentPage;
    }

    protected Externalizer getExternalizer() {
        return externalizer;
    }

    protected SlingHttpServletRequest getRequest() {
        return request;
    }
}
