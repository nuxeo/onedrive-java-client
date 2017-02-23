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
    private final OneDriveDrive resourceDrive;
    private final ResourceIdentifierType resourceIdentifierType;

    public OneDriveResource(OneDriveAPI api, String id) {
        this(api, id, ResourceIdentifierType.Id);
    }

    public OneDriveResource(OneDriveAPI api, OneDriveDrive drive, String path) {
        this(api, drive, path, ResourceIdentifierType.Path);
    }

    OneDriveResource(OneDriveAPI api) {
        this.api = Objects.requireNonNull(api);
        this.resourceDrive = null;
        this.resourceIdentifier = null;
        this.resourceIdentifierType = ResourceIdentifierType.Id;
    }

    OneDriveResource(OneDriveAPI api, OneDriveDrive drive) {
        this.api = Objects.requireNonNull(api);
        this.resourceDrive = Objects.requireNonNull(drive);
        this.resourceIdentifier = null;
        this.resourceIdentifierType = ResourceIdentifierType.Path;
    }

    public OneDriveResource(OneDriveAPI api, String resourceIdentifier, ResourceIdentifierType resourceIdentifierType) {
        this.api = Objects.requireNonNull(api);
        this.resourceIdentifier = Objects.requireNonNull(resourceIdentifier);
        this.resourceIdentifierType = resourceIdentifierType;
        this.resourceDrive = null;
    }

    public OneDriveResource(OneDriveAPI api, OneDriveDrive drive, String resourceIdentifier, ResourceIdentifierType resourceIdentifierType) {
        this.api = Objects.requireNonNull(api);
        this.resourceDrive = Objects.requireNonNull(drive);
        this.resourceIdentifier = Objects.requireNonNull(resourceIdentifier);
        this.resourceIdentifierType = resourceIdentifierType;

    }

    public OneDriveAPI getApi() {
        return api;
    }

    public boolean isRoot() {
        return resourceIdentifier == null;
    }

    public String getResourceIdentifier() {
        return resourceIdentifier;
    }

    public OneDriveDrive getResourceDrive() {
        return resourceDrive;
    }

    public ResourceIdentifierType getResourceIdentifierType() {
        return resourceIdentifierType;
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
        return getResourceIdentifier().equals(oDObj.getResourceIdentifier());
    }

    @Override
    public int hashCode() {
        return getResourceIdentifier().hashCode();
    }

    public enum ResourceIdentifierType {
        Id,
        Path
    }

    public abstract class Metadata extends OneDriveJsonObject {

        public Metadata(JsonObject json) {
            super(json);
        }

        public String getId() {
            return OneDriveResource.this.getResourceIdentifier();
        }

        public abstract OneDriveResource getResource();

    }

}
