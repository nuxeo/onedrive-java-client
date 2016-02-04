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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.NoSuchElementException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.powermock.core.classloader.annotations.PrepareForTest;

import com.eclipsesource.json.JsonObject;

/**
 * @since 1.0
 */
@PrepareForTest(JsonObjectIterator.class)
public class TestJsonObjectIterator extends OneDriveTestCase {

    @Rule
    private final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testIterator() throws Exception {
        // Mock
        String stringUrl = "https://nuxeofr-my.sharepoint.com/_api/v2.0";
        URL url = new URL(stringUrl);
        mockJsonRequest(url, "onedrive_children_page_1.json");
        mockJsonRequest(new URL(stringUrl + "?p=2"), "onedrive_children_page_2.json");

        // Test
        JsonObjectIterator iterator = new JsonObjectIterator(api, url);

        assertTrue(iterator.hasNext());
        JsonObject item = iterator.next();
        assertEquals("01YOWJ6CQSJ5S3PYEERFBLEDREVCLCBNPU", item.get("id").asString());

        assertTrue(iterator.hasNext());
        item = iterator.next();
        assertEquals("01YOWJ6CT42I3ABB3ZFBGJ2E5ZRUGXXVJ4", item.get("id").asString());

        assertTrue(iterator.hasNext());
        item = iterator.next();
        assertEquals("01YOWJ6CSJ32VK4TN5PRCIR3Y2KDWQA4ZZ", item.get("id").asString());

        assertTrue(iterator.hasNext());
        item = iterator.next();
        assertEquals("01YOWJ6CRVBPTPEKUL6JFIGM7D6V3UZN2N", item.get("id").asString());

        assertFalse(iterator.hasNext());
        expectedException.expect(NoSuchElementException.class);
        iterator.next();
    }

}
