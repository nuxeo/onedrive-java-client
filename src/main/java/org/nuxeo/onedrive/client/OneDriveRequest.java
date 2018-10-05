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

import java.io.IOException;
import java.net.URL;

/**
 * @since 1.0
 */
public class OneDriveRequest extends AbstractRequest<OneDriveResponse> {

    public OneDriveRequest(final URL url, final String method) {
        super(url, method);
    }

    @Override
    protected OneDriveResponse createResponse(final RequestExecutor.Response response) throws IOException {
        return new OneDriveResponse(response.getStatusCode(), response.getStatusMessage(), response.getLocation(), response.getInputStream());
    }
}
