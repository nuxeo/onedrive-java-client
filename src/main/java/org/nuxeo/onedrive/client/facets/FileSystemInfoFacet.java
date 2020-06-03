package org.nuxeo.onedrive.client.facets;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

public class FileSystemInfoFacet extends Facet<FileSystemInfoFacet> {
    // The UTC date and time the file was created on a client.
    private OffsetDateTime createdDateTime;

    // The UTC date and time the file was last accessed.
    private OffsetDateTime lastAccessedDateTime;

    // The UTC date and time the file was last modified on a client.
    private OffsetDateTime lastModifiedDateTime;

    public OffsetDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public OffsetDateTime getLastAccessedDateTime() {
        return lastAccessedDateTime;
    }

    public OffsetDateTime getLastModifiedDateTime() {
        return lastModifiedDateTime;
    }

    public void setCreatedDateTime(OffsetDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public void setLastAccessedDateTime(OffsetDateTime lastAccessedDateTime) {
        this.lastAccessedDateTime = lastAccessedDateTime;
    }

    public void setLastModifiedDateTime(OffsetDateTime lastModifiedDateTime) {
        this.lastModifiedDateTime = lastModifiedDateTime;
    }

    @Override
    protected void parseMember(JsonObject.Member member) {
        JsonValue value = member.getValue();
        String memberName = member.getName();
        switch (memberName) {
            case "createdDateTime":
                createdDateTime = OffsetDateTime.parse(value.asString());
                break;
            case "lastModifiedDateTime":
                lastModifiedDateTime = OffsetDateTime.parse(value.asString());
                break;
            case "lastAccessedDateTime":
                lastAccessedDateTime = OffsetDateTime.parse(value.asString());
                break;
        }
    }

    @Override
    protected void populateJsonObject(JsonObject jsonObject) {
        if (null != createdDateTime) {
            jsonObject.add("createdDateTime", createdDateTime.toString());
        }
        if (null != lastAccessedDateTime) {
            jsonObject.add("lastAccessedDateTime", lastAccessedDateTime.toString());
        }
        if (null != lastModifiedDateTime) {
            jsonObject.add("lastModifiedDateTime", lastModifiedDateTime.toString());
        }
    }
}
