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
import org.nuxeo.onedrive.client.facets.FileSystemInfoFacet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * @since 1.0
 */
public abstract class OneDriveItem extends OneDriveResource {
    private final OneDriveResource parent;
    private final ItemIdentifierType itemIdentifierType;

    OneDriveItem(OneDriveAPI api) {
        super(api);
        parent = null;
        itemIdentifierType = null;
    }

    public OneDriveItem(OneDriveAPI api, OneDriveResource parent) {
        super(api);
        this.parent = Objects.requireNonNull(parent);
        this.itemIdentifierType = null;
    }

    public OneDriveItem(OneDriveAPI api, OneDriveResource parent, String resourceIdentifier, ItemIdentifierType itemIdentifierType) {
        super(api, resourceIdentifier);
        this.parent = Objects.requireNonNull(parent);
        this.itemIdentifierType = itemIdentifierType;
    }

    public ItemIdentifierType getItemIdentifierType() {
        return itemIdentifierType;
    }

    public void delete() throws IOException {
        final URL url = getMetadataURL().build(getApi().getBaseURL());
        new OneDriveRequest(url, "DELETE").sendRequest(getApi().getExecutor()).close();
    }

    public void patch(OneDrivePatchOperation patchOperation) throws IOException {
        final URL url = getMetadataURL().build(getApi().getBaseURL());
        new OneDriveJsonRequest(url, "PATCH", patchOperation.build()).sendRequest(getApi().getExecutor()).close();
    }

    public OneDriveLongRunningAction copy(OneDriveCopyOperation copyOperation) throws IOException {
        final URL url = getCopyURL().build(getApi().getBaseURL());
        OneDriveJsonResponse jsonResponse = new OneDriveJsonRequest(url, "POST", copyOperation.build()).sendRequest(getApi().getExecutor());
        final URL locationUrl = new URL(jsonResponse.getLocation());
        return new OneDriveLongRunningAction(locationUrl, getApi());
    }

    public URLTemplate getMetadataURL() {
        return new URLTemplate(getFullyQualifiedPath());
    }

    public URLTemplate getCopyURL() {
        return new URLTemplate(getActionPath("copy"));
    }

    public URLTemplate getSharedLinkUrl() {
        final String action = getApi().isGraphConnection() ? "createLink" : "oneDrive.createLink";
        return new URLTemplate(getActionPath(action));
    }

    @Override
    public String getFullyQualifiedPath() {
        final StringBuilder builder = new StringBuilder();
        builder.append(parent.getFullyQualifiedPath());

        final String identifier = getItemIdentifier();
        if (identifier != null) {
            final ItemIdentifierType itemIdentifierType = getItemIdentifierType();
            if (itemIdentifierType == ItemIdentifierType.Id) {
                builder.append("/items");
            } else if (itemIdentifierType == ItemIdentifierType.Path) {
                if (parent instanceof OneDriveDrive) {
                    builder.append("/root");
                }
                builder.append(':');
            }
            builder.append(String.format("/%s", getItemIdentifier()));
        } else {
            builder.append("/root");
        }

        return builder.toString();
    }

    protected String getActionPath(String action) {
        final StringBuilder builder = new StringBuilder();
        builder.append(getFullyQualifiedPath());

        if (getItemIdentifierType() == ItemIdentifierType.Path) {
            builder.append(':');
        }
        builder.append(String.format("/%s", action));

        return builder.toString();
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
        return new OneDriveThumbnail(getApi(), getItemIdentifier(), size).getMetadata();
    }

    public InputStream downloadThumbnail(OneDriveThumbnailSize size) throws IOException {
        return new OneDriveThumbnail(getApi(), getItemIdentifier(), size).download();
    }

    Iterable<OneDriveThumbnailSet.Metadata> getThumbnailSets() {
        return () -> new OneDriveThumbnailSetIterator(getApi(), getItemIdentifier());
    }

    public OneDrivePermission.Metadata createSharedLink(OneDriveSharingLink.Type type) throws IOException {
        final URL url = getSharedLinkUrl().build(getApi().getBaseURL(), getItemIdentifier());
        OneDriveJsonRequest request = new OneDriveJsonRequest(url, "POST",
                new JsonObject().add("type", type.getType()));
        OneDriveJsonResponse response = request.sendRequest(getApi().getExecutor());
        final JsonObject json = response.getContent();
        String permissionId = json.asObject().get("id").asString();
        OneDrivePermission permission;
        if (isRoot()) {
            permission = new OneDrivePermission(getApi(), permissionId);
        } else {
            permission = new OneDrivePermission(getApi(), getItemIdentifier(), permissionId);
        }
        return permission.new Metadata(json);
    }

    public static OneDriveItem.Metadata parseJson(OneDriveAPI api, final JsonObject nextObject) {
        final String id = nextObject.get("id").asString();
        final OneDriveDrive drive = new OneDriveDrive(api, nextObject.get("parentReference").asObject().get("driveId").asString());

        OneDriveItem.Metadata nextMetadata;
        if (nextObject.get("folder") != null && !nextObject.get("folder").isNull()) {
            OneDriveFolder folder = new OneDriveFolder(api, drive, id, OneDriveItem.ItemIdentifierType.Id);
            nextMetadata = folder.new Metadata(nextObject);
        } else if (nextObject.get("file") != null && !nextObject.get("file").isNull()) {
            OneDriveFile file = new OneDriveFile(api, drive, id, OneDriveItem.ItemIdentifierType.Id);
            nextMetadata = file.new Metadata(nextObject);
        } else if (nextObject.get("package") != null && !nextObject.get("package").isNull()) {
            OneDrivePackageItem packageItem = new OneDrivePackageItem(api, drive, id, OneDriveItem.ItemIdentifierType.Id);
            nextMetadata = packageItem.new Metadata(nextObject);
        } else {
            throw new OneDriveRuntimeException(new OneDriveAPIException("The object type is currently not handled"));
        }

        return nextMetadata;
    }

    public enum ItemIdentifierType {
        Id,
        Path
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

        private FileSystemInfoFacet fileSystemInfo;

        private List<OneDriveThumbnailSet.Metadata> thumbnailSets = Collections.emptyList();

        private OneDriveItem.Metadata remoteItem;

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

        public OneDriveItem.Metadata getRemoteItem() {
            return remoteItem;
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

        public FileSystemInfoFacet getFileSystemInfo() {
            return fileSystemInfo;
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
                    OneDriveDrive drive = new OneDriveDrive(getApi(), valueObject.get("driveId").asString());

                    if (valueObject.names().contains("id")) {
                        String id = valueObject.get("id").asString();
                        OneDriveFolder parentFolder = new OneDriveFolder(getApi(), drive, id, ItemIdentifierType.Id);
                        parentReference = parentFolder.new Reference(valueObject);
                    } else {
                        parentReference = drive.getRoot().new Reference(valueObject);
                    }
                } else if ("fileSystemInfo".equals(memberName)) {
                    fileSystemInfo = new FileSystemInfoFacet();
                    fileSystemInfo.fromJson(value.asObject());
                } else if ("remoteItem".equals(memberName)) {
                    remoteItem = OneDriveItem.parseJson(getApi(), value.asObject());
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

        @Override
        public OneDriveItem getResource() {
            return OneDriveItem.this;
        }

        public OneDriveFolder.Metadata asFolder() {
            throw new UnsupportedOperationException("Not a folder.");
        }

        public OneDriveFile.Metadata asFile() {
            throw new UnsupportedOperationException("Not a file.");
        }

    }

}
