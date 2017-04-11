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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

/**
 * @since 1.0
 */
public class OneDriveJsonResponse extends AbstractResponse<JsonObject> {

    public OneDriveJsonResponse(final int responseCode, final String responseMessage, final InputStream inputStream) throws IOException {
        super(responseCode, responseMessage, inputStream);
    }

    /**
     * Gets the body as JSON object. Once this method is called, the response will be disconnected.
     */
    @Override
    public JsonObject getContent() throws IOException {
        try (InputStreamReader in = new InputStreamReader(this.getBody(), StandardCharsets.UTF_8)) {
            return Json.parse(in).asObject();
        }
    }
}
