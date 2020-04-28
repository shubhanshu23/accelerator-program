package com.adobe.aem.accelerator.program.core.servlets;

import java.security.AccessControlException;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.osgi.framework.Constants;

/**
* This servlet will validate that the requested page uri is accessible or not and then accordingly set the response header.
*
*/
@Component(service = Servlet.class, property = { 
		Constants.SERVICE_DESCRIPTION + "= AuthcheckerServlet Permission Sensitivity Caching",
		"sling.servlet.methods=" + HttpConstants.METHOD_HEAD,
		"sling.servlet.paths=" + "/bin/permissioncheckCustom"})
public class AuthcheckerServlet extends SlingSafeMethodsServlet {
 
  private static final long serialVersionUID = 7993325149700224588L;

	/** The Constant LOGGER. */
  private static final Logger logger = LoggerFactory.getLogger(AuthcheckerServlet.class);
 
  /**
   * Method to handle the HEAD request for the servlet.
   * 
   * @param request - The request object.
   * @param response - The response object.
   *
   */
  @Override
  public void doHead(SlingHttpServletRequest request, SlingHttpServletResponse response) {
      logger.debug("Start of doHead Method");
      // retrieve the requested URL
      String uri = request.getParameter("uri");
      uri = uri.replace(".html", "");
      // obtain the session from the request
      Session session = request.getResourceResolver().adaptTo(javax.jcr.Session.class);
      if (session != null) {
        try {
     // perform the permissions check
        session.checkPermission(uri, Session.ACTION_READ);
        response.setStatus(SlingHttpServletResponse.SC_OK);
      } catch (AccessControlException | RepositoryException e) {
          response.setStatus(SlingHttpServletResponse.SC_FORBIDDEN);
        }
      }
      else {
        response.setStatus(SlingHttpServletResponse.SC_FORBIDDEN);
      }
      logger.debug("End of doHead Method"); 
  }
}