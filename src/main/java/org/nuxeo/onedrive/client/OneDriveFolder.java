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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.ParseException;

/**
 * @since 1.0
 */
public class OneDriveFolder extends OneDriveItem implements Iterable<OneDriveItem.Metadata> {
    private static final URLTemplate SEARCH_IN_ROOT_URL = new URLTemplate("/drive/root/view.search");
    private static final URLTemplate DELTA_IN_ROOT_URL = new URLTemplate("/drive/root/view.delta");
    private static final URLTemplate SEARCH_IN_FOLDER_URL_BY_ID = new URLTemplate("/drive/items/%s/view.search");
    private static final URLTemplate DELTA_IN_FOLDER_URL_BY_ID = new URLTemplate("/drive/items/%s/view.delta");

    OneDriveFolder(OneDriveAPI api) {
        super(api);
    }

    OneDriveFolder(OneDriveAPI api, OneDriveDrive drive) {
        super(api, drive);
    }

    public OneDriveFolder(OneDriveAPI api, String id) {
        super(api, id);
    }

    public OneDriveFolder(OneDriveAPI api, String resourceIdentifier, ResourceIdentifierType resourceIdentifierType) {
        super(api, resourceIdentifier, resourceIdentifierType);
    }

    public OneDriveFolder(OneDriveAPI api, OneDriveDrive drive, String path) {
        super(api, drive, path);
    }

    public OneDriveFolder(OneDriveAPI api, OneDriveDrive drive, String path, ResourceIdentifierType resourceIdentifierType) {
        super(api, drive, path, resourceIdentifierType);
    }

    public OneDriveFolder.Metadata create(String directory) throws IOException {
        final URL url = getChildrenURL().build(getApi().getBaseURL(), getResourceIdentifier());
        final JsonObject rootObject = new JsonObject();
        rootObject.add("name", directory);
        rootObject.add("folder", new JsonObject());
        final OneDriveJsonRequest request = new OneDriveJsonRequest(url, "POST", rootObject);
        final OneDriveJsonResponse response = request.sendRequest(getApi().getExecutor());
        final OneDriveFolder folder = new OneDriveFolder(getApi());
        final OneDriveFolder.Metadata metadata = folder.new Metadata(response.getContent());
        response.close();
        return metadata;
    }

    @Override
    public OneDriveFolder.Metadata getMetadata(OneDriveExpand... expands) throws IOException {
        QueryStringBuilder query = new QueryStringBuilder().set("expand", expands);
        final URL url = getMetadataURL().build(getApi().getBaseURL(), getResourceIdentifier());
        OneDriveJsonRequest request = new OneDriveJsonRequest(url, "GET");
        OneDriveJsonResponse response = request.sendRequest(getApi().getExecutor());
        return new OneDriveFolder.Metadata(response.getContent());
    }

    public static OneDriveFolder getRoot(OneDriveAPI api) {
        return new OneDriveFolder(api);
    }

    public Iterable<OneDriveItem.Metadata> getChildren() {
        return this;
    }

    public Iterable<OneDriveItem.Metadata> getChildren(OneDriveExpand... expands) {
        return () -> iterator(expands);
    }

    @Override
    public Iterator<OneDriveItem.Metadata> iterator() {
        return iterator(new OneDriveExpand[]{});
    }

    public Iterator<OneDriveItem.Metadata> iterator(OneDriveExpand... expands) {
        QueryStringBuilder query = new QueryStringBuilder().set("top", 200);
        final URL url = getChildrenURL().build(getApi().getBaseURL(), getResourceIdentifier());
        return new OneDriveItemIterator(getApi(), url);
    }

    public Iterable<OneDriveItem.Metadata> search(String search, OneDriveExpand... expands) {
        QueryStringBuilder query = new QueryStringBuilder().set("q", search).set("expand", expands);
        URL url;
        if (isRoot()) {
            url = SEARCH_IN_ROOT_URL.build(getApi().getBaseURL(), query);
        } else {
            url = SEARCH_IN_FOLDER_URL_BY_ID.build(getApi().getBaseURL(), query, getResourceIdentifier());
        }
        return () -> new OneDriveItemIterator(getApi(), url);
    }

    public URLTemplate getChildrenURL() {
        StringBuilder urlBuilder = new StringBuilder();
        appendDriveItemAction(urlBuilder, "children");

        return new URLTemplate(urlBuilder.toString());
    }

    /**
     * @since 1.1
     */
    public OneDriveDeltaItemIterator delta() {
        URL url;
        if (isRoot()) {
            url = DELTA_IN_ROOT_URL.build(getApi().getBaseURL());
        } else {
            url = DELTA_IN_FOLDER_URL_BY_ID.build(getApi().getBaseURL(), getResourceIdentifier());
        }
        return new OneDriveDeltaItemIterator(getApi(), url);
    }

    /**
     * @since 1.1
     */
    public OneDriveItemIterator delta(String deltaLink) {
        if (deltaLink == null) {
            return delta();
        }
        try {
            URL url = new URL(deltaLink);
            return new OneDriveDeltaItemIterator(getApi(), url);
        } catch (MalformedURLException e) {
            throw new OneDriveRuntimeException(new OneDriveAPIException(e.getMessage(), e));
        }
    }

    @Override
    public Iterable<OneDriveThumbnailSet.Metadata> getThumbnailSets() {
        if (isRoot()) {
            return () -> new OneDriveThumbnailSetIterator(getApi());
        }
        return super.getThumbnailSets();
    }

    /**
     * See documentation at https://dev.onedrive.com/resources/item.htm.
     */
    public class Metadata extends OneDriveItem.Metadata {

        private long childCount;

        @Override
        public List<OneDriveThumbnailSet.Metadata> getThumbnailSets() {
            return super.getThumbnailSets();
        }

        public Metadata(JsonObject json) {
            super(json);
        }

        public long getChildCount() {
            return childCount;
        }

        @Override
        protected void parseMember(JsonObject.Member member) {
            super.parseMember(member);
            try {
                JsonValue value = member.getValue();
                String memberName = member.getName();
                if ("folder".equals(memberName)) {
                    parseMember(value.asObject(), this::parseChildMember);
                }
            } catch (ParseException e) {
                throw new OneDriveRuntimeException(new OneDriveAPIException(e.getMessage(), e));
            }
        }

        private void parseChildMember(JsonObject.Member member) {
            JsonValue value = member.getValue();
            String memberName = member.getName();
            if ("childCount".equals(memberName)) {
                childCount = value.asLong();
            }
        }

        @Override
        public OneDriveFolder getResource() {
            return OneDriveFolder.this;
        }

        @Override
        public boolean isFolder() {
            return true;
        }

        @Override
        public OneDriveFolder.Metadata asFolder() {
            return this;
        }
    }

    /**
     * See documentation at https://dev.onedrive.com/resources/itemReference.htm.
     */
    public class Reference extends OneDriveResource.Metadata {

        /**
         * Unique identifier for the Drive that contains the item.
         */
        private String driveId;

        /**
         * Path that used to navigate to the item.
         */
        private String path;

        public Reference(JsonObject json) {
            super(json);
        }

        public String getDriveId() {
            return driveId;
        }

        public String getPath() {
            return path;
        }

        @Override
        protected void parseMember(JsonObject.Member member) {
            super.parseMember(member);
            try {
                JsonValue value = member.getValue();
                String memberName = member.getName();
                if ("driveId".equals(memberName)) {
                    driveId = value.asString();
                } else if ("path".equals(memberName)) {
                    path = value.asString();
                }
            } catch (ParseException e) {
                throw new OneDriveRuntimeException(new OneDriveAPIException(e.getMessage(), e));
            }
        }

        @Override
        public OneDriveFolder getResource() {
            return OneDriveFolder.this;
        }

    }

}
