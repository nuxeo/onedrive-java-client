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
import com.eclipsesource.json.JsonValue;

/**
 * @since 1.1
 */
public class OneDriveDeltaItemIterator extends OneDriveItemIterator {

    private String deltaLink;

    public OneDriveDeltaItemIterator(OneDriveAPI api, URL url) {
        super(api, url);
    }

    public String getDeltaLink() {
        return deltaLink;
    }

    @Override
    protected void onResponse(JsonObject response) {
        JsonValue delta = response.get("@odata.deltaLink");
        deltaLink = delta != null && !delta.isNull() ? delta.asString() : null;
    }
}
