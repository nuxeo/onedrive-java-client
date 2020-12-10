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
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

/**
 * See documentation at https://dev.onedrive.com/resources/thumbnail.htm
 *
 * @since 1.0
 */
public class OneDriveThumbnail extends OneDriveResource {

    private static final URLTemplate GET_THUMBNAIL_URL = new URLTemplate("/drive/items/%s/thumbnails/%s/%s");
    private static final URLTemplate GET_THUMBNAIL_ROOT_URL = new URLTemplate("/drive/root/thumbnails/%s/%s");
    private static final URLTemplate GET_THUMBNAIL_CONTENT_URL = new URLTemplate(
            "/drive/items/%s/thumbnails/%s/%s/content");
    private static final URLTemplate GET_THUMBNAIL_CONTENT_ROOT_URL = new URLTemplate(
            "/drive/root/thumbnails/%s/%s/content");

    private final String itemId;
    private final int thumbId;
    private final OneDriveThumbnailSize size;

    OneDriveThumbnail(OneDriveAPI api, int thumbId, OneDriveThumbnailSize size) {
        super(api, "root$$" + thumbId + "$$" + size.getKey());
        this.itemId = null;
        this.thumbId = thumbId;
        this.size = Objects.requireNonNull(size);
    }

    public OneDriveThumbnail(OneDriveAPI api, String itemId, int thumbId, OneDriveThumbnailSize size) {
        super(api, itemId + "$$" + thumbId + "$$" + size.getKey());
        this.itemId = Objects.requireNonNull(itemId);
        this.thumbId = thumbId;
        this.size = Objects.requireNonNull(size);
    }

    public OneDriveThumbnail(OneDriveAPI api, String itemId, OneDriveThumbnailSize size) {
        this(api, itemId, 0, size);
    }

    public OneDriveThumbnail.Metadata getMetadata() throws IOException {
        URL url;
        if(isRoot()) {
            url = GET_THUMBNAIL_ROOT_URL.build(getApi().getBaseURL(), thumbId, size.getKey());
        }
        else {
            url = GET_THUMBNAIL_URL.build(getApi().getBaseURL(), itemId, thumbId, size.getKey());
        }
        OneDriveJsonRequest request = new OneDriveJsonRequest(url, "GET");
        OneDriveJsonResponse response = request.sendRequest(getApi().getExecutor());
        return new OneDriveThumbnail.Metadata(response.getContent());
    }

    public InputStream download() throws IOException {
        URL url;
        if(isRoot()) {
            url = GET_THUMBNAIL_CONTENT_ROOT_URL.build(getApi().getBaseURL(), thumbId, size.getKey());
        }
        else {
            url = GET_THUMBNAIL_CONTENT_URL.build(getApi().getBaseURL(), itemId, thumbId, size.getKey());
        }
        OneDriveRequest request = new OneDriveRequest(url, "GET");
        OneDriveResponse response = request.sendRequest(getApi().getExecutor());
        return response.getContent();
    }

    @Override
    public boolean isRoot() {
        return itemId == null;
    }

    public class Metadata extends OneDriveResource.Metadata {

        private int height;

        private int width;

        private String url;

        public Metadata(JsonObject json) {
            super(json);
        }

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }

        public String getUrl() {
            return url;
        }

        @Override
        protected void parseMember(JsonObject.Member member) {
            super.parseMember(member);
            try {
                JsonValue value = member.getValue();
                String memberName = member.getName();
                if("height".equals(memberName)) {
                    height = value.asInt();
                }
                else if("width".equals(memberName)) {
                    width = value.asInt();
                }
                else if("url".equals(memberName)) {
                    url = value.asString();
                }
            }
            catch(ParseException e) {
                throw new OneDriveRuntimeException(new OneDriveAPIException(e.getMessage(), e));
            }
        }

        @Override
        public OneDriveThumbnail getResource() {
            return OneDriveThumbnail.this;
        }

    }

}
