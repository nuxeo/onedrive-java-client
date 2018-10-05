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

/**
 * @since 1.0
 */
public class OneDriveBasicAPI extends AbstractOneDriveAPI {

    private static final String BASE_URL = "https://api.onedrive.com/v1.0";
    private static final String EMAIL_URL = "https://apis.live.net/v5.0/me";

    public OneDriveBasicAPI(final RequestExecutor executor) {
        super(executor);
    }

    @Override
    public boolean isBusinessConnection() {
        return false;
    }

    @Override
    public boolean isGraphConnection() {
        return false;
    }

    @Override
    public String getBaseURL() {
        return BASE_URL;
    }

    @Override
    public String getEmailURL() {
        return EMAIL_URL;
    }
}
