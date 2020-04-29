package com.adobe.aem.accelerator.program.core.service.email;

import com.adobe.aem.accelerator.program.core.service.email.impl.EmailService;
import com.adobe.aem.accelerator.program.core.service.email.impl.EmailServiceConfiguration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataSource;
import java.util.List;
import java.util.Map;


@Component(service = EmailService.class, configurationPolicy = ConfigurationPolicy.REQUIRE, immediate = true, enabled = true)
@Designate(ocd = EmailServiceConfiguration.class)
public class EmailServiceImpl implements EmailService {

    public static final String SENDER_EMAIL_ADDRESS = "senderEmailAddress";
    public static final String SUBJECT = "subject";
    public static final String CC_EMAIL = "ccEmail";
    public static final String EMAIL_BODY = "emailBody";
    public static final String SENDER_NAME = "senderName";
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Activate
    EmailServiceConfiguration config;

    @Reference
    com.adobe.acs.commons.email.EmailService emailService;

    private String[] recipients;
    private String[] cc;
    private String[] bcc;
    private String templatePath;
    private String subject;
    private String senderEmailAddress;
    private String senderName;
    private String emailBody;

    @Activate
    protected void activate(EmailServiceConfiguration config) {
        this.cc = config.cc();
        this.bcc = config.bcc();
        this.recipients = config.recipients();
        this.templatePath = config.templatePath();
        this.subject = config.subject();
        this.senderEmailAddress = config.senderEmailAddress();
        this.senderName = config.senderName();
        this.emailBody = config.emailBody();
    }

    public List<String> sendEmail(Map<String, String> params) {
        List<String> failedList = null;
        try {
            params.put(SENDER_EMAIL_ADDRESS,senderEmailAddress);
            //params.put("bccEmail", getDynamicList(bcc));
            params.put(SUBJECT, subject);
            params.put(CC_EMAIL, getDynamicList(cc));
            params.put(EMAIL_BODY,emailBody);
            params.put(SENDER_NAME,senderName);
            failedList = emailService.sendEmail(templatePath, params, recipients);
        } catch (Exception e) {
            LOGGER.error("Exception {}", e.getMessage(), e);
        }
        //list size 0 = 100% success of email delivery
        return failedList;
    }

    @Override
    public List<String> sendEmail(String templatePath, Map<String,String> params, EmailServiceModel emailServiceModel) {
        List<String> failedList = null;
        try{
            params.put(SENDER_EMAIL_ADDRESS,emailServiceModel.getSenderEmailAddress());
            //params.put("bccEmail", getDynamicList(emailServiceModel.getBcc()));
            params.put(SUBJECT, emailServiceModel.getSubject());
            params.put(CC_EMAIL, getDynamicList(emailServiceModel.getCc()));
            params.put(EMAIL_BODY,emailServiceModel.getEmailBody());
            params.put(SENDER_NAME,emailServiceModel.getSenderName());
            failedList = emailService.sendEmail(templatePath, params, emailServiceModel.getRecipients());
        }catch (Exception e){
            LOGGER.error("Exception {}", e.getMessage(), e);
        }
        return failedList;
    }

    @Override
    public List<String> sendEmail(String templatePath, Map<String, String> emailParams, Map<String, DataSource> attachments, String... recipients) {
        return emailService.sendEmail(templatePath,emailParams,attachments,recipients);
    }

    public static String getDynamicList(String[] cc) {
        return String.join("\nCC:", cc);
    }


}


