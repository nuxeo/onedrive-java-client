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
public class OneDriveBusinessAPI extends AbstractOneDriveAPI {

    private final String baseUrl;
    private final String emailUrl;

    public OneDriveBusinessAPI(final RequestExecutor executor, String resourceURL) {
        super(executor);
        this.baseUrl = resourceURL + "_api/v2.0";
        this.emailUrl = resourceURL + "_api/SP.UserProfiles.PeopleManager/GetMyProperties";
    }

    @Override
    public boolean isBusinessConnection() {
        return true;
    }

    @Override
    public boolean isGraphConnection() {
        return false;
    }

    @Override
    public String getBaseURL() {
        return baseUrl;
    }

    @Override
    public String getEmailURL() {
        return emailUrl;
    }
}
