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
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.ParseException;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

/**
 * @since 1.0
 */
public class OneDriveDrive extends OneDriveResource implements Iterable<OneDriveItem.Metadata> {
    private OneDriveDrive(OneDriveAPI api) {
        super(api);
    }

    public OneDriveDrive(OneDriveAPI api, String id) {
        super(api, id);
    }

    public static OneDriveDrive getDefaultDrive(OneDriveAPI api) {
        return new OneDriveDrive(api);
    }

    public Metadata getMetadata(OneDriveExpand... expands) throws IOException {
        QueryStringBuilder query = new QueryStringBuilder().set("expand", expands);
        final URL url = getMetadataUrl().build(getApi().getBaseURL(), query, null);
        OneDriveJsonRequest request = new OneDriveJsonRequest(url, "GET");
        OneDriveJsonResponse response = request.sendRequest(getApi().getExecutor());
        JsonObject jsonObject = response.getContent();
        response.close();
        return new OneDriveDrive.Metadata(jsonObject);
    }

    public URLTemplate getMetadataUrl() {
        return new URLTemplate(getDrivePath());
    }

    public String getDrivePath() {
        if (isRoot()) {
            return "/drive";
        } else {
            return String.format("/drives/%s", getResourceIdentifier());
        }
    }

    public OneDriveFolder getRoot() {
        return new OneDriveFolder(getApi(), this);
    }

    public Iterator<OneDriveItem.Metadata> iterator() {
        return iterator();
    }

    public Iterator<OneDriveItem.Metadata> iterator(OneDriveExpand... expands) {
        return getRoot().iterator(expands);
    }

    public class Metadata extends OneDriveResource.Metadata {
        private Long total;
        private Long used;
        private Long remaining;
        private String id;
        private DriveType driveType;

        public Metadata(final JsonObject json) {
            super(json);
        }

        @Override
        public OneDriveResource getResource() {
            return OneDriveDrive.this;
        }

        @Override
        protected void parseMember(JsonObject.Member member) {
            super.parseMember(member);
            try {
                JsonValue value = member.getValue();
                String memberName = member.getName();
                if ("id".equals(memberName)) {
                    id = value.asString();
                } else if ("driveType".equals(memberName)) {
                    try {
                        driveType = DriveType.valueOf(value.asString());
                    } catch (IllegalArgumentException e) {
                        driveType = DriveType.unknown;
                    }
                } else if ("quota".equals(memberName)) {
                    parseMember(value.asObject(), this::parseQuotaMember);
                }
            } catch (ParseException e) {
                throw new OneDriveRuntimeException(new OneDriveAPIException(e.getMessage(), e));
            }
        }

        private void parseQuotaMember(JsonObject.Member member) {
            JsonValue value = member.getValue();
            String memberName = member.getName();
            if ("total".equals(memberName)) {
                total = value.asLong();
            } else if ("used".equals(memberName)) {
                used = value.asLong();
            } else if ("remaining".equals(memberName)) {
                remaining = value.asLong();
            }
        }

        public Long getTotal() {
            return total;
        }

        public Long getUsed() {
            return used;
        }

        public Long getRemaining() {
            return remaining;
        }

        public DriveType getDriveType() {
            return driveType;
        }

        @Override
        public String getId() {
            return id;
        }
    }

    public enum DriveType {
        unknown,
        personal,
        business,
        documentLibrary
    }
}
