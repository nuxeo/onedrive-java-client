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

import org.junit.Test;
import org.nuxeo.onedrive.client.types.Drive;
import org.nuxeo.onedrive.client.types.DriveItem;
import org.powermock.core.classloader.annotations.PrepareForTest;

import static org.junit.Assert.*;

/**
 * @since 1.0
 */
@PrepareForTest(DriveItem.class)
public class TestOneDriveFolder extends OneDriveTestCase {

    @Test
    public void testGetMetadata() throws Exception {
        // Mock
        mockJsonRequest("onedrive_folder.json");

        // Test
        Drive drive = new Drive(api, "5FGB3N6V49HN725573R8LG588VRDPMJV36");
        DriveItem folder = new DriveItem(drive);
        DriveItem.Metadata metadata = folder.getMetadata();
        assertTrue(metadata.isFolder());
        assertEquals("Test", metadata.getName());
        assertNotNull(metadata.getParentReference());
        assertEquals("4K4Q87486LMKRP9X82Y2YJ2N2NFVD2F596", metadata.getParentReference().getId());
        assertEquals("/drive/root:", metadata.getParentReference().getPath());
        assertNotNull(metadata.getCreatedBy());
        assertNull(metadata.getCreatedBy().getApplication());
        assertNull(metadata.getCreatedBy().getDevice());
        assertNotNull(metadata.getCreatedBy().getUser());
        assertEquals("Nuxeo User", metadata.getCreatedBy().getUser().getDisplayName());
        assertEquals(3, metadata.getFolder().getChildCount().intValue());
        assertEquals(0L, metadata.getSize().intValue());
    }

}
