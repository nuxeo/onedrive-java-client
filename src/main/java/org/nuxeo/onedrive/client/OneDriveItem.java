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

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @since 1.0
 */
public abstract class OneDriveItem extends OneDriveResource {
    OneDriveItem(OneDriveAPI api) {
        super(api);
    }

    OneDriveItem(OneDriveAPI api, OneDriveDrive drive) {
        super(api, drive);
    }

    public OneDriveItem(OneDriveAPI api, String id) {
        super(api, id);
    }

    public OneDriveItem(OneDriveAPI api, String resourceIdentifier, ResourceIdentifierType resourceIdentifierType) {
        super(api, resourceIdentifier, resourceIdentifierType);
    }

    public OneDriveItem(OneDriveAPI api, OneDriveDrive drive, String path) {
        super(api, drive, path, ResourceIdentifierType.Path);
    }

    public OneDriveItem(OneDriveAPI api, OneDriveDrive drive, String path, ResourceIdentifierType resourceIdentifierType) {
        super(api, drive, path, resourceIdentifierType);
    }

    protected void appendDriveResourceResolve(StringBuilder urlBuilder) {
        if (getResourceDrive() != null) {
            urlBuilder.append(String.format("/drives/%1$s", getResourceDrive().getResourceIdentifier()));
        } else {
            urlBuilder.append("/drive");
        }
    }

    protected void appendItemReferenceResolve(StringBuilder urlBuilder) {
        if (getResourceIdentifierType() == ResourceIdentifierType.Id) {
            urlBuilder.append("/items");
        } else {
            urlBuilder.append("/root");
        }
    }

    protected void appendItemReference(StringBuilder urlBuilder) {
        if (isRoot()) {
            return;
        }
        if (getResourceIdentifierType() == ResourceIdentifierType.Path) {
            urlBuilder.append(':');
        }

        urlBuilder.append("/%1$s");
    }

    protected void appendAction(StringBuilder urlBuilder, String action) {
        if (!isRoot() && getResourceIdentifierType() == ResourceIdentifierType.Path) {
            urlBuilder.append(':');
        }
        urlBuilder.append(String.format("/%s", action));
    }

    protected void appendDriveItem(StringBuilder urlBuilder) {
        appendDriveResourceResolve(urlBuilder);
        appendItemReferenceResolve(urlBuilder);
        appendItemReference(urlBuilder);
    }

    protected void appendDriveItemAction(StringBuilder urlBuilder, String action) {
        appendDriveItem(urlBuilder);
        appendAction(urlBuilder, action);
    }

    public URLTemplate getMetadataURL() {
        StringBuilder urlBuilder = new StringBuilder();
        appendDriveItem(urlBuilder);

        return new URLTemplate(urlBuilder.toString());
    }

    public URLTemplate getSharedLinkUrl() {
        StringBuilder urlBuilder = new StringBuilder();
        appendDriveItemAction(urlBuilder, "action.createLink");

        return new URLTemplate(urlBuilder.toString());
    }

    public abstract OneDriveItem.Metadata getMetadata(OneDriveExpand... expand) throws IOException;

    public OneDriveThumbnailSet.Metadata getThumbnailSet() throws OneDriveAPIException {
        try {
            Iterator<OneDriveThumbnailSet.Metadata> iterator = getThumbnailSets().iterator();
            if (iterator.hasNext()) {
                return iterator.next();
            }
        } catch (OneDriveRuntimeException e) {
            throw new OneDriveAPIException(e);
        }
        return null;
    }

    public OneDriveThumbnail.Metadata getThumbnail(OneDriveThumbnailSize size) throws IOException {
        return new OneDriveThumbnail(getApi(), getResourceIdentifier(), size).getMetadata();
    }

    public InputStream downloadThumbnail(OneDriveThumbnailSize size) throws IOException {
        return new OneDriveThumbnail(getApi(), getResourceIdentifier(), size).download();
    }

    Iterable<OneDriveThumbnailSet.Metadata> getThumbnailSets() {
        return () -> new OneDriveThumbnailSetIterator(getApi(), getResourceIdentifier());
    }

    public OneDrivePermission.Metadata createSharedLink(OneDriveSharingLink.Type type) throws IOException {
        final URL url = getSharedLinkUrl().build(getApi().getBaseURL(), getResourceIdentifier());
        OneDriveJsonRequest request = new OneDriveJsonRequest(url, "POST",
                new JsonObject().add("type", type.getType()));
        OneDriveJsonResponse response = request.sendRequest(getApi().getExecutor());
        String permissionId = response.getContent().asObject().get("id").asString();
        OneDrivePermission permission;
        if (isRoot()) {
            permission = new OneDrivePermission(getApi(), permissionId);
        } else {
            permission = new OneDrivePermission(getApi(), getResourceIdentifier(), permissionId);
        }
        return permission.new Metadata(response.getContent());
    }

    protected OneDriveJsonResponse executeRequest(JsonObject jsonObject) throws IOException {
        final URL metadataUrl = getMetadataURL().build(getApi().getBaseURL(), getResourceIdentifier());
        OneDriveJsonRequest request = new OneDriveJsonRequest(metadataUrl, "PATCH", jsonObject);
        return request.sendRequest(getApi().getExecutor());
    }

    public Metadata move(OneDriveFolder newParent) throws IOException {
        /*
        Builds a JSON Object

        {
            "parentReference": { "path": newParent-DriveItem }
        }
        */
        final JsonObject rootObject = new JsonObject();
        final JsonObject parentReferenceObject = new JsonObject();
        final StringBuilder builder = new StringBuilder();
        newParent.appendDriveItem(builder);
        parentReferenceObject.set("path", builder.toString());
        rootObject.set("parentReference", parentReferenceObject);

        OneDriveJsonResponse response = executeRequest(rootObject);
        try {
            return parseResponse(response);
        } finally {
            response.close();
        }
    }

    public Metadata rename(String newFilename) throws IOException {
        /*
        Builds a JSON Object

        {
            "name": "$newFilename"
        }
        */
        final JsonObject rootObject = new JsonObject();
        rootObject.set("name", newFilename);

        OneDriveJsonResponse response = executeRequest(rootObject);
        try {
            return parseResponse(response);
        } finally {
            response.close();
        }
    }

    private OneDriveItem.Metadata parseResponse(OneDriveJsonResponse response) throws IOException {
        JsonObject nextObject = response.getContent();
        String id = nextObject.get("id").asString();

        OneDriveItem.Metadata nextMetadata = null;
        if (nextObject.get("folder") != null && !nextObject.get("folder").isNull()) {
            OneDriveFolder folder = new OneDriveFolder(getApi(), id);
            nextMetadata = folder.new Metadata(nextObject);
        } else if (nextObject.get("file") != null && !nextObject.get("file").isNull()) {
            OneDriveFile file = new OneDriveFile(getApi(), id);
            nextMetadata = file.new Metadata(nextObject);
        }
        return nextMetadata;
    }

    /**
     * See documentation at https://dev.onedrive.com/resources/item.htm.
     */
    public abstract class Metadata extends OneDriveResource.Metadata {

        private String name;

        private String eTag;

        private OneDriveIdentitySet createdBy;

        private ZonedDateTime createdDateTime;

        private OneDriveIdentitySet lastModifiedBy;

        private ZonedDateTime lastModifiedDateTime;

        private long size;

        private OneDriveFolder.Reference parentReference;

        private String webUrl;

        private String description;

        private boolean deleted;

        private List<OneDriveThumbnailSet.Metadata> thumbnailSets = Collections.emptyList();

        public Metadata(JsonObject json) {
            super(json);
        }

        public String getName() {
            return name;
        }

        public String getETag() {
            return eTag;
        }

        public OneDriveIdentitySet getCreatedBy() {
            return createdBy;
        }

        public ZonedDateTime getCreatedDateTime() {
            return createdDateTime;
        }

        public OneDriveIdentitySet getLastModifiedBy() {
            return lastModifiedBy;
        }

        public ZonedDateTime getLastModifiedDateTime() {
            return lastModifiedDateTime;
        }

        public long getSize() {
            return size;
        }

        public OneDriveFolder.Reference getParentReference() {
            return parentReference;
        }

        public String getWebUrl() {
            return webUrl;
        }

        public String getDescription() {
            return description;
        }

        public boolean isDeleted() {
            return deleted;
        }

        public OneDriveThumbnailSet.Metadata getThumbnailSet() {
            return thumbnailSets.stream().findFirst().orElse(null);
        }

        List<OneDriveThumbnailSet.Metadata> getThumbnailSets() {
            return Collections.unmodifiableList(thumbnailSets);
        }

        @Override
        protected void parseMember(JsonObject.Member member) {
            super.parseMember(member);
            try {
                JsonValue value = member.getValue();
                String memberName = member.getName();
                if ("name".equals(memberName)) {
                    name = value.asString();
                } else if ("eTag".equals(memberName)) {
                    eTag = value.asString();
                } else if ("createdBy".equals(memberName)) {
                    createdBy = new OneDriveIdentitySet(value.asObject());
                } else if ("createdDateTime".equals(memberName)) {
                    createdDateTime = ZonedDateTime.parse(value.asString());
                } else if ("lastModifiedBy".equals(memberName)) {
                    lastModifiedBy = new OneDriveIdentitySet(value.asObject());
                } else if ("lastModifiedDateTime".equals(memberName)) {
                    lastModifiedDateTime = ZonedDateTime.parse(value.asString());
                } else if ("size".equals(memberName)) {
                    size = value.asLong();
                } else if ("parentReference".equals(memberName)) {
                    JsonObject valueObject = value.asObject();
                    String id = valueObject.get("id").asString();
                    OneDriveFolder parentFolder = new OneDriveFolder(getApi(), id);
                    parentReference = parentFolder.new Reference(valueObject);
                } else if ("webUrl".equals(memberName)) {
                    webUrl = value.asString();
                } else if ("description".equals(memberName)) {
                    description = value.asString();
                } else if ("deleted".equals(memberName)) {
                    deleted = true;
                } else if ("thumbnailSets".equals(memberName)) {
                    parseThumbnailsMember(value.asArray());
                }
            } catch (ParseException e) {
                throw new OneDriveRuntimeException(new OneDriveAPIException(e.getMessage(), e));
            }
        }

        private void parseThumbnailsMember(JsonArray thumbnails) {
            thumbnailSets = new ArrayList<>(thumbnails.size());
            for (JsonValue value : thumbnails) {
                JsonObject thumbnail = value.asObject();
                int id = Integer.parseInt(thumbnail.get("id").asString());
                OneDriveThumbnailSet thumbnailSet = new OneDriveThumbnailSet(getApi(), getId(), id);
                thumbnailSets.add(thumbnailSet.new Metadata(thumbnail));
            }
        }

        public boolean isFolder() {
            return false;
        }

        public boolean isFile() {
            return false;
        }

        public OneDriveFolder.Metadata asFolder() {
            throw new UnsupportedOperationException("Not a folder.");
        }

        public OneDriveFile.Metadata asFile() {
            throw new UnsupportedOperationException("Not a file.");
        }

    }

}
