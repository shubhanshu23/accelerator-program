package com.adobe.aem.accelerator.program.core.servlets;

import com.adobe.aem.accelerator.program.core.service.email.EmailServiceModel;
import com.adobe.aem.accelerator.program.core.service.email.impl.EmailService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(service = Servlet.class, property = {
        Constants.SERVICE_DESCRIPTION + "= TEst email service",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.paths=" + "/apps/sendemail"},immediate = true,enabled = true)
public class SendEmailServlet extends SlingAllMethodsServlet {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Reference
    EmailService emailService;

    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        List<String> emailList = null;
        String subject = request.getParameter("subject");
        String[] recipients = request.getParameter("recipients").split(",");
        String[] cc = request.getParameter("cc").split(",");
        String message = request.getParameter("message");
        String sender = request.getParameter("sender");
        try {
            String templatepath = "/etc/notification/email/emailtemplate.txt";
            EmailServiceModel emailServiceModel = new EmailServiceModel();
            emailServiceModel.setSubject(subject);
            emailServiceModel.setRecipients(recipients);
            emailServiceModel.setCc(cc);
            emailServiceModel.setSenderEmailAddress("shubhanshu.j.singh@gmail.com");
            emailServiceModel.setSenderName(sender);
            emailServiceModel.setEmailBody(message);
            //emailList =emailService.sendEmail(new HashMap<>());
            Map<String,String> map = new HashMap<>();
            emailList = emailService.sendEmail(templatepath, new HashMap<String, String>(), emailServiceModel);
        }catch(Exception e){
            LOGGER.error("Exception {}",e.getMessage(),e);
        }
        response.getWriter().print(CollectionUtils.isEmpty(emailList)?"Email Sent":"Email Sending Failed");
    }


}
