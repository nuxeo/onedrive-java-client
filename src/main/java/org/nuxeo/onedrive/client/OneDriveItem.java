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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.ParseException;

/**
 * @since 1.0
 */
public abstract class OneDriveItem extends OneDriveResource {

    private static final URLTemplate CREATE_SHARED_LINK_URL = new URLTemplate("/drive/items/%s/action.createLink");

    private static final URLTemplate CREATE_SHARED_LINK_ROOT_URL = new URLTemplate("/drive/root/action.createLink");

    private final ResourceIdentifierType resourceIdentifierType;

    OneDriveItem(OneDriveAPI api) {
        super(api);
        this.resourceIdentifierType = ResourceIdentifierType.Id;
    }

    public OneDriveItem(OneDriveAPI api, String resourceIdentifier, ResourceIdentifierType resourceIdentifierType) {
        super(api, resourceIdentifier);
        this.resourceIdentifierType = resourceIdentifierType;
    }

    public ResourceIdentifierType getResourceIdentifierType() {
        return resourceIdentifierType;
    }

    public abstract OneDriveItem.Metadata getMetadata(OneDriveExpand... expand) throws IOException;

    public OneDriveThumbnailSet.Metadata getThumbnailSet() throws OneDriveAPIException {
        try {
            Iterator<OneDriveThumbnailSet.Metadata> iterator = getThumbnailSets().iterator();
            if(iterator.hasNext()) {
                return iterator.next();
            }
        }
        catch(OneDriveRuntimeException e) {
            throw new OneDriveAPIException(e);
        }
        return null;
    }

    public OneDriveThumbnail.Metadata getThumbnail(OneDriveThumbnailSize size) throws IOException {
        return new OneDriveThumbnail(getApi(), getId(), size).getMetadata();
    }

    public InputStream downloadThumbnail(OneDriveThumbnailSize size) throws IOException {
        return new OneDriveThumbnail(getApi(), getId(), size).download();
    }

    Iterable<OneDriveThumbnailSet.Metadata> getThumbnailSets() {
        return () -> new OneDriveThumbnailSetIterator(getApi(), getId());
    }

    public OneDrivePermission.Metadata createSharedLink(OneDriveSharingLink.Type type) throws IOException {
        URL url;
        if(isRoot()) {
            url = CREATE_SHARED_LINK_ROOT_URL.build(getApi().getBaseURL());
        }
        else {
            url = CREATE_SHARED_LINK_URL.build(getApi().getBaseURL(), getId());
        }
        OneDriveJsonRequest request = new OneDriveJsonRequest(url, "POST",
                new JsonObject().add("type", type.getType()));
        OneDriveJsonResponse response = request.sendRequest(getApi().getExecutor());
        String permissionId = response.getContent().asObject().get("id").asString();
        OneDrivePermission permission;
        if(isRoot()) {
            permission = new OneDrivePermission(getApi(), permissionId);
        }
        else {
            permission = new OneDrivePermission(getApi(), getId(), permissionId);
        }
        return permission.new Metadata(response.getContent());
    }

    public enum ResourceIdentifierType {
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
                if("name".equals(memberName)) {
                    name = value.asString();
                }
                else if("eTag".equals(memberName)) {
                    eTag = value.asString();
                }
                else if("createdBy".equals(memberName)) {
                    createdBy = new OneDriveIdentitySet(value.asObject());
                }
                else if("createdDateTime".equals(memberName)) {
                    createdDateTime = ZonedDateTime.parse(value.asString());
                }
                else if("lastModifiedBy".equals(memberName)) {
                    lastModifiedBy = new OneDriveIdentitySet(value.asObject());
                }
                else if("lastModifiedDateTime".equals(memberName)) {
                    lastModifiedDateTime = ZonedDateTime.parse(value.asString());
                }
                else if("size".equals(memberName)) {
                    size = value.asLong();
                }
                else if("parentReference".equals(memberName)) {
                    JsonObject valueObject = value.asObject();
                    String id = valueObject.get("id").asString();
                    OneDriveFolder parentFolder = new OneDriveFolder(getApi(), id, ResourceIdentifierType.Id);
                    parentReference = parentFolder.new Reference(valueObject);
                }
                else if("webUrl".equals(memberName)) {
                    webUrl = value.asString();
                }
                else if("description".equals(memberName)) {
                    description = value.asString();
                }
                else if("deleted".equals(memberName)) {
                    deleted = true;
                }
                else if("thumbnailSets".equals(memberName)) {
                    parseThumbnailsMember(value.asArray());
                }
            }
            catch(ParseException e) {
                throw new OneDriveRuntimeException(new OneDriveAPIException(e.getMessage(), e));
            }
        }

        private void parseThumbnailsMember(JsonArray thumbnails) {
            thumbnailSets = new ArrayList<>(thumbnails.size());
            for(JsonValue value : thumbnails) {
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
