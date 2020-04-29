package mockhelper;


import com.adobe.cq.export.json.SlingModelFilter;
import com.adobe.granite.workflow.WorkflowSession;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.msm.api.MSMNameConstants;
import com.google.common.base.Function;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import mockhelper.mock.MockSlingModelFilter;
import mockhelper.mock.MockStyle;
import mockhelper.mock.MockUserManager;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.mime.MimeTypeService;
import org.apache.sling.settings.SlingSettingsService;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.services.MockMimeTypeService;
import org.apache.sling.testing.mock.sling.services.MockSlingSettingService;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.adobe.aem.accelerator.program.core.constants.Constants.*;
import static io.wcm.testing.mock.wcmio.caconfig.ContextPlugins.WCMIO_CACONFIG;
import static org.apache.sling.testing.mock.caconfig.ContextPlugins.CACONFIG;

/**
 * AppAem Context - fetches the common context throughtout the application
 * Helps to make components context aware
 */
public class AppAemContext {


    private AppAemContext() {
        // only static methods
    }

    /**
     *
     * @param resourceResolverType - RESOURCERESOLVER_MOCK, JCR_MOCK, SLING_MOCK
     * @return AemContext as per the resourceResolvertype
     * registers Adapter and Services
     */
    public static AemContext newAemContext(ResourceResolverType resourceResolverType) {
        return new AemContextBuilder()
                .plugin(CACONFIG)
                .plugin(WCMIO_CACONFIG)
                .resourceResolverType(resourceResolverType)
                .<AemContext>afterSetUp(context -> {
                    //register sling models
                    context.addModelsForPackage("com.adobe.aem.accelerator.program.core");
                    context.registerAdapter(ResourceResolver.class, UserManager.class, new Function<ResourceResolver, UserManager>() {
                        @Nullable
                        @Override
                        public UserManager apply(@Nullable ResourceResolver resolver) {
                            return new MockUserManager();
                        }
                    });
                    context.registerService(SlingModelFilter.class, new MockSlingModelFilter() {
                        private final Set<String> IGNORED_NODE_NAMES = new HashSet<String>() {{
                            add(NameConstants.NN_RESPONSIVE_CONFIG);
                            add(MSMNameConstants.NT_LIVE_SYNC_CONFIG);
                            add("cq:annotations");
                        }};

                        @Override
                        public Map<String, Object> filterProperties(Map<String, Object> map) {
                            return map;
                        }

                        @Override
                        public Iterable<Resource> filterChildResources(Iterable<Resource> childResources) {
                            return StreamSupport
                                    .stream(childResources.spliterator(), false)
                                    .filter(r -> !IGNORED_NODE_NAMES.contains(r.getName()))
                                    .collect(Collectors.toList());
                        }
                    });
                    context.registerService(SlingSettingsService.class, new MockSlingSettingService());
                    context.registerService(MimeTypeService.class, new MockMimeTypeService());
                    loadContext(context);
                })
                .build();

    }

    /**
     * Loads Resources to context
     * @param context
     */
    private static void loadContext(AemContext context) {
        context.load().json("/sample-content.json", CONTENT_ROOT);
        context.load().json("/button/sample-content.json", BUTTON_PATH);
        context.load().json("/teaser/sample-content.json", TEASERS_PATH);
        //sample for HelloWorldModelTest
        context.load().json("/sample-content.json", "/content/mypage");
    }

    /**
     * For WorkflowSession
     * Registers WorkflowSession with Application Context
     * @param context
     * @param workflowSessionMock
     */
    public void registerWorkflowSessionAdapter(AemContext context, WorkflowSession workflowSessionMock) {
        context.registerAdapter(ResourceResolver.class, WorkflowSession.class, new Function<ResourceResolver, WorkflowSession>() {
            @Nullable
            @Override
            public WorkflowSession apply(@Nullable ResourceResolver input) {
                return workflowSessionMock;
            }
        });
    }

}
