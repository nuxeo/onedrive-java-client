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
import org.apache.commons.io.input.NullInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @since 1.0
 */
public class OneDriveFile extends OneDriveItem {
    OneDriveFile(OneDriveAPI api) {
        super(api);
    }

    public OneDriveFile(OneDriveAPI api, String fileId) {
        super(api, fileId);
    }

    public OneDriveFile(OneDriveAPI api, OneDriveDrive drive, String path) {
        super(api, drive, path);
    }

    public OneDriveFile.Metadata create(String mimeType) throws IOException {
        final URL url = getContentURL().build(getApi().getBaseURL(), getResourceIdentifier());
        final OneDriveRequest request = new OneDriveRequest(url, "PUT");
        final OneDriveResponse response = request.sendRequest(getApi().getExecutor(), new NullInputStream(0));
        final OneDriveJsonResponse jsonResponse = new OneDriveJsonResponse(response.getResponseCode(), response.getResponseMessage(), response.getContent());
        JsonObject jsonObject = jsonResponse.getContent();
        jsonResponse.close();
        return new Metadata(jsonObject);
    }

    @Override
    public OneDriveFile.Metadata getMetadata(OneDriveExpand... expands) throws IOException {
        QueryStringBuilder query = new QueryStringBuilder().set("expand", expands);
        final URL url = getMetadataURL().build(getApi().getBaseURL(), getResourceIdentifier());
        OneDriveJsonRequest request = new OneDriveJsonRequest(url, "GET");
        OneDriveJsonResponse response = request.sendRequest(getApi().getExecutor());
        return new OneDriveFile.Metadata(response.getContent());
    }

    public InputStream download() throws IOException {
        final URL url = getContentURL().build(getApi().getBaseURL(), getResourceIdentifier());
        OneDriveRequest request = new OneDriveRequest(url, "GET");
        OneDriveResponse response = request.sendRequest(getApi().getExecutor());
        return response.getContent();
    }

    public URLTemplate getContentURL() {
        StringBuilder urlBuilder = new StringBuilder();
        appendDriveItemAction(urlBuilder, "content");

        return new URLTemplate(urlBuilder.toString());
    }

    public URLTemplate getUploadSessionURL() {
        StringBuilder urlBuilder = new StringBuilder();
        appendDriveItemAction(urlBuilder, "createUploadSession");

        return new URLTemplate(urlBuilder.toString());
    }

    public OneDriveUploadSession getUploadSession() throws IOException {
        final URL url = getUploadSessionURL().build(getApi().getBaseURL(), getResourceIdentifier());
        OneDriveRequest request = new OneDriveRequest(url, "GET");
        OneDriveResponse genericResponse = request.sendRequest(getApi().getExecutor());
        OneDriveJsonResponse jsonResponse = new OneDriveJsonResponse(genericResponse.getResponseCode(), genericResponse.getResponseMessage(), genericResponse.getContent());
        try {
            return new OneDriveUploadSession(jsonResponse.getContent());
        } finally {
            jsonResponse.close();
        }
    }

    /**
     * See documentation at https://dev.onedrive.com/resources/item.htm.
     */
    public class Metadata extends OneDriveItem.Metadata {

        private String cTag;

        private String mimeType;

        private String downloadUrl;

        /**
         * Not available for business.
         */
        private String crc32Hash;

        /**
         * Not available for business.
         */
        private String sha1Hash;

        public Metadata(JsonObject json) {
            super(json);
        }

        public String getCTag() {
            return cTag;
        }

        /**
         * Returns the current version of OneDrive file.
         * CAUTION: this value is known from cTag field, it doesn't rely on public field from OneDrive API.
         *
         * @return the current version of OneDrive file
         */
        public String getVersion() {
            return cTag == null ? null : cTag.substring(cTag.lastIndexOf(',') + 1);
        }

        public String getMimeType() {
            return mimeType;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public String getCrc32Hash() {
            return crc32Hash;
        }

        public String getSha1Hash() {
            return sha1Hash;
        }

        @Override
        protected void parseMember(JsonObject.Member member) {
            super.parseMember(member);
            try {
                JsonValue value = member.getValue();
                String memberName = member.getName();
                if ("cTag".equals(memberName)) {
                    cTag = value.asString();
                } else if ("@content.downloadUrl".equals(memberName)) {
                    downloadUrl = value.asString();
                } else if ("file".equals(memberName)) {
                    parseMember(value.asObject(), this::parseFileMember);
                }
            } catch (ParseException e) {
                throw new OneDriveRuntimeException(new OneDriveAPIException(e.getMessage(), e));
            }
        }

        private void parseFileMember(JsonObject.Member member) {
            JsonValue value = member.getValue();
            String memberName = member.getName();
            if ("mimeType".equals(memberName)) {
                mimeType = value.asString();
            } else if ("hashes".equals(memberName)) {
                parseMember(value.asObject(), this::parseHashesMember);
            }
        }

        private void parseHashesMember(JsonObject.Member member) {
            JsonValue value = member.getValue();
            String memberName = member.getName();
            if ("crc32Hash".equals(memberName)) {
                crc32Hash = value.asString();
            } else if ("sha1Hash".equals(memberName)) {
                sha1Hash = value.asString();
            }
        }

        @Override
        public OneDriveFile getResource() {
            return OneDriveFile.this;
        }

        @Override
        public boolean isFile() {
            return true;
        }

        @Override
        public OneDriveFile.Metadata asFile() {
            return this;
        }

    }

}
