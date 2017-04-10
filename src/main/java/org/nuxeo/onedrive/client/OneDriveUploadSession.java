package org.nuxeo.onedrive.client;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.ParseException;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;

public class OneDriveUploadSession extends OneDriveJsonObject {
    private URL uploadUrl;
    private ZonedDateTime expirationDateTime;
    private String[] nextExpectedRanges;

    public OneDriveUploadSession(JsonObject json) {
        super(json);
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
