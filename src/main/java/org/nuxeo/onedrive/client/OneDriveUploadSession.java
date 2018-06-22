package org.nuxeo.onedrive.client;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.ParseException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Set;

public class OneDriveUploadSession extends OneDriveJsonObject {
    private final OneDriveAPI api;
    private URL uploadUrl;
    private ZonedDateTime expirationDateTime;
    private String[] nextExpectedRanges;

    public OneDriveUploadSession(OneDriveAPI api, JsonObject json) {
        super(json);
        this.api = api;
    }

    public OneDriveAPI getApi() {
        return api;
    }

    public URL getUploadUrl() {
        return uploadUrl;
    }

    public ZonedDateTime getExpirationDateTime() {
        return expirationDateTime;
    }

    public String[] getNextExpectedRanges() {
        return nextExpectedRanges;
    }

    public String getNextExpectedRange() {
        if (nextExpectedRanges.length == 0) {
            return null;
        }
        return nextExpectedRanges[0];
    }

    public OneDriveUploadSession getUploadStatus() throws IOException {
        OneDriveJsonRequest request = new OneDriveJsonRequest(getUploadUrl(), "GET");
        OneDriveJsonResponse response = request.sendRequest(api.getExecutor());
        JsonObject jsonObject = response.getContent();
        response.close();
        return new OneDriveUploadSession(api, jsonObject);
    }

    public OneDriveJsonObject uploadFragment(String contentRange, byte[] content) throws IOException {
        OneDriveJsonRequest request = new OneDriveJsonRequest(getUploadUrl(), "PUT") {
            @Override
            protected void addAuthorizationHeader(final RequestExecutor executor, final Set<RequestHeader> headers) {
                // PUT requests for fragment uploads are pre-authenticated and cannot have an Authorization header
            }
        };
        request.addHeader("Content-Length", String.valueOf(content.length));
        request.addHeader("Content-Range", String.format("bytes %s", contentRange));
        OneDriveJsonResponse response = request.sendRequest(getApi().getExecutor(), new ByteArrayInputStream(content));
        JsonObject jsonObject = response.getContent();
        response.close();
        if (response.getResponseCode() == 202) {
            return new OneDriveUploadSession(getApi(), jsonObject);
        } else if (response.getResponseCode() == 201 || response.getResponseCode() == 200) {
            return OneDriveFile.parseJson(api, jsonObject);
        }
        return null;
    }

    public void cancelUpload() throws IOException {
        OneDriveJsonRequest request = new OneDriveJsonRequest(getUploadUrl(), "DELETE") {
            @Override
            protected void addAuthorizationHeader(RequestExecutor executor, Set<RequestHeader> headers) {
                // DELETE requests for upload session are pre-authenticated and cannot have an Authorization header
            }
        };
        OneDriveJsonResponse response = request.sendRequest(api.getExecutor());
        response.close();
    }

    @Override
    protected void parseMember(JsonObject.Member member) {
        super.parseMember(member);
        try {
            JsonValue value = member.getValue();
            String memberName = member.getName();
            switch (memberName) {
                case "uploadUrl":
                    uploadUrl = new URL(value.asString());
                    break;
                case "expirationDateTime":
                    expirationDateTime = ZonedDateTime.parse(value.asString());
                    break;
                case "nextExpectedRanges":
                    parseNextExpectedRanges(value.asArray());
                    break;
            }
        } catch (ParseException | MalformedURLException e) {
            throw new OneDriveRuntimeException(new OneDriveAPIException(e.getMessage(), e));
        }
    }

    private void parseNextExpectedRanges(JsonArray jsonValues) {
        nextExpectedRanges = new String[jsonValues.size()];
        for (int i = 0; i < nextExpectedRanges.length; i++) {
            nextExpectedRanges[i] = jsonValues.get(i).asString();
        }
    }
}
