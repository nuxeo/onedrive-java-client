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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.eclipsesource.json.JsonObject;

/**
 * @since 1.0
 */
public class OneDriveJsonRequest extends AbstractRequest<OneDriveJsonResponse> {

    private JsonObject body;

    public OneDriveJsonRequest(final URL url, final String method) {
        super(url, method);
    }

    public OneDriveJsonRequest(final URL url, final String method, final JsonObject body) {
        super(url, method);
        this.body = body;
        this.addHeader("Content-Type", "application/json");
        this.addHeader("Accept", "application/json");
    }

    @Override
    public OneDriveJsonResponse sendRequest(final RequestExecutor sender) throws IOException {
        if(body != null) {
            byte[] bytes = body.toString().getBytes(StandardCharsets.UTF_8);
            return this.sendRequest(sender, new ByteArrayInputStream(bytes));
        }
        return super.sendRequest(sender);
    }

    @Override
    protected OneDriveJsonResponse createResponse(final RequestExecutor.Response response) throws IOException {
        return new OneDriveJsonResponse(response.getStatusCode(), response.getStatusMessage(), response.getInputStream());
    }
}
