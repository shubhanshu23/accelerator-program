package com.adobe.aem.accelerator.program.core.service.email.impl;

import com.adobe.aem.accelerator.program.core.service.email.EmailServiceModel;

import javax.activation.DataSource;
import java.util.List;
import java.util.Map;

public interface EmailService {

    public List<String> sendEmail(Map<String, String> params);

    public List<String> sendEmail(String templatePath, Map<String, String> params, EmailServiceModel emailServiceModel);

    public List<String> sendEmail(String templatePath, Map<String, String> emailParams, Map<String, DataSource> attachments, String... recipients);
}
