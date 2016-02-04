/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and others.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *  
 * Contributors:
 *     Kevin Leturc
 */
package org.nuxeo.onedrive.client;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

/**
 * @since 1.0
 */
@PrepareForTest(QueryStringBuilder.class)
public class TestQueryStringBuilder extends OneDriveTestCase {

    @Test
    public void testWithParameters() {
        String queryString = new QueryStringBuilder().set("param1", "value1")
                                                     .set("param2", true)
                                                     .set("param3", 10L)
                                                     .set("param4", 20)
                                                     .set("param1", "value2")
                                                     .toString();
        assertEquals("?param3=10&param4=20&param1=value2&param2=true", queryString);
    }

    @Test
    public void testWithoutParameters() {
        String queryString = new QueryStringBuilder().toString();
        assertEquals("", queryString);
    }

    @Test(expected = OneDriveRuntimeException.class)
    public void testURLEncoderError() throws Exception {
        mockStatic(URLEncoder.class);
        doThrow(new UnsupportedEncodingException()).when(URLEncoder.class);
        URLEncoder.encode("error", StandardCharsets.UTF_8.name());
        new QueryStringBuilder().set("param1", "test").set("param2", "error").toString();
    }

}
