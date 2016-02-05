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
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.nuxeo.onedrive.client.OneDriveEmailAccount.OneDriveBusinessEmailRequest;
import org.nuxeo.onedrive.client.OneDriveEmailAccount.OneDriveBusinessEmailResponse;
import org.powermock.core.classloader.annotations.PrepareForTest;

/**
 * @since 1.0
 */
@PrepareForTest(OneDriveEmailAccount.class)
public class TestOneDriveEmailAccount extends OneDriveTestCase {

    @Test
    public void testGetBusinessEmail() throws Exception {
        // Mock
        mockBusinessEmailRequest("onedrive_business_email.xml");

        // Test
        String email = OneDriveEmailAccount.getCurrentUserEmailAccount(businessApi);
        assertEquals("nuxeo@nuxeofr.onmicrosoft.com", email);

    }

    protected void mockBusinessEmailRequest(String xmlResponseFile) throws Exception {
        OneDriveBusinessEmailRequest emailRequest = mock(OneDriveBusinessEmailRequest.class);
        whenNew(OneDriveBusinessEmailRequest.class).withAnyArguments().thenReturn(emailRequest);

        OneDriveBusinessEmailResponse emailResponse = mock(OneDriveBusinessEmailResponse.class);
        when(emailRequest.send()).thenReturn(emailResponse);

        InputStream xmlString = IOUtils.toInputStream(IOUtils.toString(getClass().getResource(xmlResponseFile)));
        when(emailResponse.getBody()).thenReturn(xmlString);
        when(emailResponse.getContent()).thenCallRealMethod();
    }

}
