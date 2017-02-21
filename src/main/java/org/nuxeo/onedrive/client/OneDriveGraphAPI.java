/*
 * (C) Copyright 2015-2016 Nuxeo SA (http://nuxeo.com/) and others.
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
 *
 */
package org.nuxeo.onedrive.client;

/**
 * @since 1.1
 */
public abstract class OneDriveGraphAPI implements OneDriveAPI {

    protected String userId;

    public OneDriveGraphAPI(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean isBusinessConnection() {
        return false;
    }

    @Override
    public boolean isGraphConnection() {
        return true;
    }

    @Override
    public String getBaseURL() {
        return "https://graph.microsoft.com/v1.0/" +
                (userId == null ? "me" : "/users/" + userId);
    }

    @Override
    public String getEmailURL() {
        return "https://graph.microsoft.com/v1.0/" +
                (userId == null ? "me" : "/users/" + userId);
    }
}
