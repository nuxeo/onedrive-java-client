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

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.URL;

import com.eclipsesource.json.JsonObject;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * @since 1.0
 */
@RunWith(PowerMockRunner.class)
public class OneDriveTestCase {

    protected OneDriveAPI api = new OneDriveBasicAPI(
            new JavaNetRequestExecutor("ACCESS_TOKEN_TEST")
    );

    protected OneDriveAPI businessApi = new OneDriveBusinessAPI(
            new JavaNetRequestExecutor("ACCESS_TOKEN_TEST"),
            "https://nuxeofr-my.sharepoint.com/"
    );

    protected void mockJsonRequest(String jsonResponseFile) throws Exception {
        OneDriveJsonRequest jsonRequest = mock(OneDriveJsonRequest.class);
        whenNew(OneDriveJsonRequest.class).withAnyArguments().thenReturn(jsonRequest);

        OneDriveJsonResponse jsonResponse = mock(OneDriveJsonResponse.class);
        when(jsonRequest.sendRequest(api.getExecutor())).thenReturn(jsonResponse);

        String jsonString = IOUtils.toString(getClass().getResource(jsonResponseFile));
        when(jsonResponse.getContent()).thenReturn(JsonObject.readFrom(jsonString));
    }

    protected void mockJsonRequest(URL url, String jsonResponseFile) throws Exception {
        OneDriveJsonRequest jsonRequest = mock(OneDriveJsonRequest.class);
        whenNew(OneDriveJsonRequest.class).withArguments(any(), eq(url), any()).thenReturn(jsonRequest);

        OneDriveJsonResponse jsonResponse = mock(OneDriveJsonResponse.class);
        when(jsonRequest.sendRequest(api.getExecutor())).thenReturn(jsonResponse);

        String jsonString = IOUtils.toString(getClass().getResource(jsonResponseFile));
        when(jsonResponse.getContent()).thenReturn(JsonObject.readFrom(jsonString));
    }

}
