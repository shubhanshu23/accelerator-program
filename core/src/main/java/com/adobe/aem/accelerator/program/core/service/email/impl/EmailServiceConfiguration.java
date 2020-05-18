package com.adobe.aem.accelerator.program.core.service.email.impl;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Email Service Configuration", description = "Configuration for Email Service")
public @interface EmailServiceConfiguration {

    @AttributeDefinition(name = "Recipients List", description = "List of Email Recipients")
    String[] recipients() default "";

    @AttributeDefinition(name = "CC List", description = "List of CC recipients")
    String[] cc() default "";

    @AttributeDefinition(name = "BCC List", description = "List of BCC recipients")
    String[] bcc() default "";

    @AttributeDefinition(name = "Template Path", description = "Email Template under etc/notification")
    String templatePath() default "";

    @AttributeDefinition(name = "Subject", description = "Subject of Email")
    String subject() default "";

    @AttributeDefinition(name = "Email Body", description = "Body of the Email")
    String emailBody() default "";

    @AttributeDefinition(name = "Sender Name", description = "Sender Name")
    String senderName() default "";

    @AttributeDefinition(name = "Sender Email Address", description = "Sender Email Address")
    String senderEmailAddress() default "";
}
