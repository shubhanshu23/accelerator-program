package mockhelper;

import com.adobe.cq.sightly.SightlyWCMMode;
import com.adobe.cq.sightly.WCMBindings;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.scripting.WCMBindingsConstants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import mockhelper.mock.MockStyle;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.servlet.MockRequestDispatcherFactory;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.resourceresolver.MockValueMap;
import org.apache.sling.xss.XSSAPI;

import javax.servlet.RequestDispatcher;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

public class AppAemContextUtil {
    /**
     * set RequestDispatcherFactory to the given AEM context
     * @param context
     * @param xssApi
     * @param requestDispatcher
     */
    public static void setUpServletRequestDispatcherContext(AemContext context, XSSAPI xssApi, RequestDispatcher requestDispatcher) {

        lenient().when(xssApi.getValidInteger(anyString(), anyInt())).then(i -> Integer.parseInt((String) i.getArguments()[0]));
        context.request().setContextPath("");
        context.request().setRequestDispatcherFactory(new MockRequestDispatcherFactory() {
            @Override
            public RequestDispatcher getRequestDispatcher(String path, RequestDispatcherOptions options) {
                return requestDispatcher;
            }

            @Override
            public RequestDispatcher getRequestDispatcher(Resource resource, RequestDispatcherOptions options) {
                return requestDispatcher;
            }
        });
    }

    /**
     * SlingBindings for explicit objects
     * @param context
     * @param slingScriptBinderHelperModel
     */
    public static void setUpSlingScriptBindings(AemContext context, SlingScriptBinderHelperModel slingScriptBinderHelperModel) {
        SlingBindings slingBindings = (SlingBindings) context.request().getAttribute(SlingBindings.class.getName());
        if (slingScriptBinderHelperModel.getResource() != null) {
            slingBindings.put(WCMBindingsConstants.NAME_PROPERTIES , slingScriptBinderHelperModel.getResource().getValueMap());
        } else if (slingScriptBinderHelperModel.getMockPage() != null) {
            slingBindings.put(WCMBindingsConstants.NAME_CURRENT_PAGE, slingScriptBinderHelperModel.getMockPage());
        } else if (slingScriptBinderHelperModel.getPageManager() != null) {
            slingBindings.put(WCMBindingsConstants.NAME_PAGE_MANAGER, slingScriptBinderHelperModel.getPageManager());
        } else if (slingScriptBinderHelperModel.getStyle() != null) {
            slingBindings.put(WCMBindingsConstants.NAME_CURRENT_STYLE, slingScriptBinderHelperModel.getStyle());
        } else if (slingScriptBinderHelperModel.getRequest() != null) {
            slingBindings.put(WCMBindings.WCM_MODE, new SightlyWCMMode(slingScriptBinderHelperModel.getRequest()));
        }
        /*else if(slingScriptBinderHelperModel.getValueMap()!=null){
            slingBindings.put(WCMBindingsConstants.NAME_PROPERTIES, slingScriptBinderHelperModel.getValueMap());
        }*/
    }

    /**
     *
     * @param context
     * @param resourcePath - path of the resource to bind to context
     * @param pagePath - path of page to bind to context
     * @return
     */
    public static MockSlingHttpServletRequest getMockRequestFromContext(AemContext context, String resourcePath, String pagePath){
        Resource resource = StringUtils.isNotEmpty(resourcePath)?context.currentResource(resourcePath):null;
        Page page = StringUtils.isNotEmpty(pagePath)?context.currentPage(pagePath):null;
        PageManager pageManager = context.pageManager();
        SlingScriptBinderHelperModel slingScriptBinderHelperModel = new SlingScriptBinderHelperModel();
        slingScriptBinderHelperModel.setResource(resource);
        slingScriptBinderHelperModel.setMockPage(page);
        slingScriptBinderHelperModel.setPageManager(pageManager);
        slingScriptBinderHelperModel.setStyle(mockStyleFromCurrentResource(resource));
        //slingScriptBinderHelperModel.setValueMap(resource.getValueMap());
        setUpSlingScriptBindings(context,slingScriptBinderHelperModel);
        return context.request();
    }

    private static Style mockStyleFromCurrentResource(Resource resource){
        return new MockStyle(resource, new MockValueMap(resource, new HashMap()));
    }


}
