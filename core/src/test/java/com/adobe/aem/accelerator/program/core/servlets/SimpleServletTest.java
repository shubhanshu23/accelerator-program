/*
 *  Copyright 2018 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.adobe.aem.accelerator.program.core.servlets;

import java.io.IOException;

import javax.servlet.ServletException;

import mockhelper.AppAemContext;
import mockhelper.AppAemContextUtil;
import mockhelper.mock.MockStyle;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static com.adobe.aem.accelerator.program.core.constants.Constants.CONTENT_ROOT;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AemContextExtension.class)
class SimpleServletTest {

    private SimpleServlet fixture = new SimpleServlet();

    AemContext context = AppAemContext.newAemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

    @Test
    void doGet(AemContext context) throws ServletException, IOException {
        MockSlingHttpServletRequest request = AppAemContextUtil.getMockRequestFromContext(context,
                CONTENT_ROOT, CONTENT_ROOT);
        MockSlingHttpServletResponse response = context.response();

        fixture.doGet(request, response);
        assertEquals("Title = We.Retail", response.getOutputAsString());
    }
}
