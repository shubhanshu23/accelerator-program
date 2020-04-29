/*
 *   Copyright 2018 Adobe Systems Incorporated
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.adobe.aem.accelerator.program.core.models;


import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import mockhelper.AppAemContext;
import mockhelper.AppAemContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.servlethelpers.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.adobe.aem.accelerator.program.core.constants.Constants.TEASERS_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AemContextExtension.class)
public class TeaserTest {

    public final AemContext context = AppAemContext.newAemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);



    @Test
    public void testWithLabel() {
        MockSlingHttpServletRequest request = AppAemContextUtil.getMockRequestFromContext(
                context,TEASERS_PATH + "/with-label", StringUtils.EMPTY);
        Teaser teaser = request.adaptTo(Teaser.class);
        assertEquals("/content/we-retail/us/en/products/men", teaser.getButtonLinkTo());
        assertEquals("All men's products", teaser.getButtonLabel());
    }

    @Test
    public void testWithoutLabel() {
        MockSlingHttpServletRequest request = AppAemContextUtil.getMockRequestFromContext(
                context,TEASERS_PATH + "/without-label", StringUtils.EMPTY);
        Teaser teaser = request.adaptTo(Teaser.class);
        assertEquals("/content/we-retail/us/en/products/men", teaser.getButtonLinkTo());
        assertEquals("Men", teaser.getButtonLabel());
    }
}
