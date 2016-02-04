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

import java.time.ZonedDateTime;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.ParseException;

/**
 * @since 1.0
 */
public abstract class OneDriveItem extends OneDriveResource {

    public OneDriveItem(OneDriveAPI api, String id) {
        super(api, id);
    }

    public abstract OneDriveItem.Metadata getMetadata() throws OneDriveAPIException;

    /** See documentation at https://dev.onedrive.com/resources/item.htm. */
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
                }
            } catch (ParseException e) {
                throw new OneDriveRuntimeException("Parse failed, maybe a bug in client.", e);
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
