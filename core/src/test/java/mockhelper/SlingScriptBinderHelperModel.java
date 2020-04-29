package mockhelper;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.Style;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;

public class SlingScriptBinderHelperModel {

    private Resource resource;

    private Page mockPage;

    private PageManager pageManager;

    private Style style;

    private ValueMap valueMap;

    private MockSlingHttpServletRequest request;

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Page getMockPage() {
        return mockPage;
    }

    public void setMockPage(Page mockPage) {
        this.mockPage = mockPage;
    }

    public PageManager getPageManager() {
        return pageManager;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public MockSlingHttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(MockSlingHttpServletRequest request) {
        this.request = request;
    }

    public ValueMap getValueMap() {
        return valueMap;
    }

    public void setValueMap(ValueMap valueMap) {
        this.valueMap = valueMap;
    }
}
