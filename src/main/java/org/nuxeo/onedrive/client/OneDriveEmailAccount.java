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

import java.net.URL;

import com.eclipsesource.json.JsonObject;

/**
 * @since 1.0
 */
public class OneDriveEmailAccount {

    private static final URLTemplate EMAIL_URL_TEMPLATE = new URLTemplate("");

    public static String getCurrentUserEmailAccount(OneDriveAPI api) throws OneDriveAPIException {
        URL url = EMAIL_URL_TEMPLATE.build(api.getEmailURL());
        if (api.isBusinessConnection()) {

        }
        OneDriveJsonRequest request = new OneDriveJsonRequest(api, url, "GET");
        OneDriveJsonResponse response = request.send();
        JsonObject jsonObject = response.getContent();
        return jsonObject.get("emails").asObject().get("account").asString();
    }

}
