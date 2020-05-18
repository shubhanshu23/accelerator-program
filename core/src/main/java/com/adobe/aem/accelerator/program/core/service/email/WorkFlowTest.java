package com.adobe.aem.accelerator.program.core.service.email;

import com.adobe.aem.accelerator.program.core.service.email.impl.EmailService;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*Custom Workflow To Test the service
PROCESS_ARGS of Workflow Model contains templatepath,<username>
*/
@Component(service = WorkflowProcess.class, property = {"process.label=My Email Custom Step"})
public class WorkFlowTest implements WorkflowProcess {

    private static final Logger log = LoggerFactory.getLogger(WorkFlowTest.class);

    @Reference
    EmailService emailService;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;


    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        WorkflowData workflowData = workItem.getWorkflowData();
        String type = workflowData.getPayloadType();
        if (StringUtils.equals(type, "JCR_PATH")) {
            String[] args = this.buildArguments(metaDataMap);
            String emailTemplate = args[0];
            if (emailTemplate != null) {
                String payloadPath = workflowData.getPayload().toString();
                Map<String, Object> authInfo = new HashMap();
                authInfo.put("user.jcr.session", workflowSession.getSession());
                try {
                    ResourceResolver resourceResolver = this.resourceResolverFactory.getResourceResolver(authInfo);
                    getPayloadAndParams(args, emailTemplate, payloadPath, resourceResolver);
                } catch (Exception e) {
                    log.error("Could not acquire a ResourceResolver object from the Workflow Session's JCR Session: {}", e);
                }

            }
        }
    }

    private void getPayloadAndParams(String[] args, String emailTemplate, String payloadPath, ResourceResolver resourceResolver) {
        try {
            Resource payloadRes = resourceResolver.getResource(payloadPath);
            Map<String, String> emailParams = new HashMap();
            emailParams.put("jcr:Path", payloadPath);
            Map<String, String> payloadProp = EmailServiceUtils.getPayloadProperties(payloadRes);
            if (payloadProp != null) {
                emailParams.putAll(payloadProp);
            }

            String[] emailTo = this.getEmailAddrs(payloadRes, args);
            EmailServiceModel emailServiceModel = new EmailServiceModel();
            emailServiceModel.setCc(emailTo);
            emailServiceModel.setEmailBody("Test Workflow");
            emailServiceModel.setRecipients(emailTo);
            emailServiceModel.setSenderEmailAddress(EmailServiceUtils.getEmailAddrsFromPathOrName(resourceResolver,
                    "admin")[0]);
            emailServiceModel.setSenderName("admin");
            emailServiceModel.setSubject("Test");
            List<String> failureList = emailService.sendEmail(emailTemplate, emailParams, emailServiceModel);

            if (failureList.isEmpty()) {
                log.info("Email sent successfully to {} recipients", emailTo.length);
            } else {
                log.error("Email sent failed");
            }
        } catch (Exception e) {
            log.error("Exception : {}", e);
        } finally {
            if (resourceResolver != null) {
                resourceResolver.close();
            }
        }
    }

    protected String[] getEmailAddrs(Resource payloadResource, String[] args) {
        ResourceResolver resolver = payloadResource.getResourceResolver();
        String sendToUser = args[1];
        return EmailServiceUtils.getEmailAddrsFromPathOrName(resolver, sendToUser);
    }


    private String[] buildArguments(MetaDataMap metaData) {
        String processArgs = (String) metaData.get("PROCESS_ARGS", String.class);
        return StringUtils.isNotEmpty(processArgs) ? processArgs.split(",") : new String[0];
    }


}
