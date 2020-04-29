package com.adobe.aem.accelerator.program.core.service.email;

import com.day.cq.dam.commons.util.DamUtil;
import com.day.cq.wcm.api.Page;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Value;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

public class EmailServiceUtils {
    private static final Logger log = LoggerFactory.getLogger(EmailServiceUtils.class);

    protected static final Map<String, String> getPayloadProperties(Resource payloadRes) {
        Map<String, String> emailParams = new HashMap();
        if (payloadRes == null) {
            return emailParams;
        } else {
            Map pageContent;
            if (DamUtil.isAsset(payloadRes)) {
                Resource mdRes = payloadRes.getChild("jcr:content/metadata");
                pageContent = getJcrKeyValuePairs(mdRes);
                emailParams.putAll(pageContent);
            } else {
                Page payloadPage = (Page) payloadRes.adaptTo(Page.class);
                if (payloadPage != null) {
                    pageContent = getJcrKeyValuePairs(payloadPage.getContentResource());
                    emailParams.putAll(pageContent);
                }
            }
            return emailParams;
        }
    }

    protected static final String[] getEmailAddrsFromPathOrName(ResourceResolver resourceResolver, String principleOrPath) {
        if (StringUtils.startsWith(principleOrPath, "/")) {
            return getEmailAddrsFromUserPath(resourceResolver, principleOrPath);
        } else {
            try {
                UserManager userManager = (UserManager) resourceResolver.adaptTo(UserManager.class);
                Authorizable auth = userManager.getAuthorizable(principleOrPath);
                return getEmailAddrsFromUserPath(resourceResolver, auth.getPath());
            } catch (RepositoryException e) {
                log.warn("Could not load repository paths for users. {}", e);
                return new String[0];
            }
        }
    }

    protected static final String[] getEmailAddrsFromUserPath(ResourceResolver resourceResolver, String principlePath) {
        LinkedList emailList = new LinkedList();

        try {
            Resource authRes = resourceResolver.getResource(principlePath);
            if (authRes != null) {
                Authorizable authorizable = (Authorizable) authRes.adaptTo(Authorizable.class);
                if (authorizable != null) {
                    if (authorizable.isGroup()) {
                        Group authGroup = (Group) authRes.adaptTo(Group.class);
                        Iterator memberIt = authGroup.getMembers();

                        while (memberIt.hasNext()) {
                            String currEmail = getAuthorizableEmail((Authorizable) memberIt.next());
                            if (currEmail != null) {
                                emailList.add(currEmail);
                            }
                        }
                    } else {
                        String authEmail = getAuthorizableEmail(authorizable);
                        if (authEmail != null) {
                            emailList.add(authEmail);
                        }
                    }
                }
            }
        } catch (RepositoryException e) {
            log.warn("Could not get list of email(s) for users. {}", e);
        }

        String[] emailReturn = new String[emailList.size()];
        return (String[]) emailList.toArray(emailReturn);
    }

    private static Map<String, String> getJcrKeyValuePairs(Resource resource) {
        Map<String, String> returnMap = new HashMap();
        if (resource == null) {
            return returnMap;
        } else {
            ValueMap resMap = resource.getValueMap();
            Iterator iterator = resMap.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<String, Object> entry = (Entry) iterator.next();
                Object value = entry.getValue();
                String strValue;
                if (value instanceof String[]) {
                    strValue = StringUtils.join((String[]) value, ", ");
                    returnMap.put((String) entry.getKey(), strValue);
                } else {
                    returnMap.put((String) entry.getKey(), value.toString());
                }
            }

            return returnMap;
        }
    }

    private static String getAuthorizableEmail(Authorizable authorizable) throws RepositoryException {
        if (authorizable.hasProperty("profile/email")) {
            Value[] emailVal = authorizable.getProperty("profile/email");
            return emailVal[0].getString();
        } else {
            return null;
        }
    }

}

