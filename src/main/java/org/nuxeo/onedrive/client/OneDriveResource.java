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

import com.eclipsesource.json.JsonObject;

import java.util.Objects;

/**
 * @since 1.0
 */
public class OneDriveResource {

    private final OneDriveAPI api;
    private final String resourceIdentifier;

    public OneDriveResource(OneDriveAPI api) {
        this.api = Objects.requireNonNull(api);
        this.resourceIdentifier = null;
    }

    public OneDriveResource(OneDriveAPI api, String resourceIdentifier) {
        this.api = Objects.requireNonNull(api);
        this.resourceIdentifier = Objects.requireNonNull(resourceIdentifier);
    }

    public OneDriveAPI getApi() {
        return api;
    }

    public boolean isRoot() {
        return resourceIdentifier == null;
    }

    public String getItemIdentifier() {
        return resourceIdentifier;
    }

    public String getFullyQualifiedPath() {
        return "";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !getClass().equals(obj.getClass())) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        OneDriveResource oDObj = (OneDriveResource) obj;
        return getItemIdentifier().equals(oDObj.getItemIdentifier());
    }

    @Override
    public int hashCode() {
        return getItemIdentifier().hashCode();
    }

    public abstract class Metadata extends OneDriveJsonObject {

        public Metadata(JsonObject json) {
            super(json);
        }

        public String getId() {
            return OneDriveResource.this.getItemIdentifier();
        }

        public abstract OneDriveResource getResource();

    }

}
