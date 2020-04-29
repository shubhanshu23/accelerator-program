/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.aem.accelerator.program.core.models;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import mockhelper.AppAemContext;
import mockhelper.AppAemContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.adobe.aem.accelerator.program.core.constants.Constants.BUTTON_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class ButtonTest {

    public final AemContext context = AppAemContext.newAemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

    private Button button;


    @BeforeEach
    public void setup() {

        MockSlingHttpServletRequest request = AppAemContextUtil.getMockRequestFromContext(
                context,BUTTON_PATH, StringUtils.EMPTY);
        button = request.adaptTo(Button.class);
    }

    /**
     * Test the button link
     */
    @Test
    public void testGetLinkTo() {
        assertEquals(button.getLinkTo(), "/content/we-retail/us/en/products/men");
    }

    /**
     * Test the button CSS class
     */
    @Test
    public void testGetCssClass() {
        assertEquals(button.getCssClass(), "myClass");
    }

}
