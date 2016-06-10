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
import java.util.Optional;
import java.util.stream.StreamSupport;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * @since 1.0
 */
public class OneDriveEmailAccount {

    public static String getCurrentUserEmailAccount(OneDriveAPI api) throws OneDriveAPIException {
        URL url = URLTemplate.EMPTY_TEMPLATE.build(api.getEmailURL());
        OneDriveJsonRequest request = new OneDriveJsonRequest(api, url, "GET");
        OneDriveJsonResponse response = request.send();
        JsonObject jsonObject = response.getContent();
        if (api.isBusinessConnection()) {
            return Optional.ofNullable(jsonObject.get("Email"))
                           .filter(JsonValue::isString)
                           .map(JsonValue::asString)
                           .orElseGet(() -> searchBusinessEmail(jsonObject.get("UserProfileProperties").asArray()));
        } else if (api.isGraphConnection()) {
            return jsonObject.get("userPrincipalName").asString();
        }
        return jsonObject.get("emails").asObject().get("account").asString();
    }

    private static String searchBusinessEmail(JsonArray properties) {
        return StreamSupport.stream(properties.spliterator(), false)
                            .map(JsonValue::asObject)
                            .filter(obj -> "UserName".equals(obj.get("Key").asString()))
                            .map(obj -> obj.get("Value").asString())
                            .findFirst()
                            .get();
    }

}
