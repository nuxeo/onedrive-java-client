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

import org.junit.Test;

/**
 * @since 1.0
 */
public class TestURLTemplate extends OneDriveTestCase {

    @Test
    public void testBuild() {
        URLTemplate template = new URLTemplate("/drive/%s");
        assertEquals("https://api.onedrive.com/v1.0/drive/root", template.build(api.getBaseURL(), "root").toString());
    }

    @Test(expected = OneDriveRuntimeException.class)
    public void testBuildWithError() {
        URLTemplate template = new URLTemplate("/drive/%s");
        assertEquals("https://api.onedrive.com/v1.0/drive/root", template.build("", "root").toString());
    }

    @Test
    public void testBuildWithQueryString() {
        URLTemplate template = new URLTemplate("/drive/%s");
        QueryStringBuilder queryString = new QueryStringBuilder().set("top", 100);
        assertEquals("https://api.onedrive.com/v1.0/drive/root?top=100",
                template.build(api.getBaseURL(), queryString, "root").toString());
    }

    @Test(expected = OneDriveRuntimeException.class)
    public void testBuildWithQueryStringAndError() {
        URLTemplate template = new URLTemplate("/drive/%s");
        QueryStringBuilder queryString = new QueryStringBuilder().set("top", 100);
        assertEquals("https://api.onedrive.com/v1.0/drive/root?top=100", template.build("", queryString, "root")
                                                                                 .toString());
    }

}
