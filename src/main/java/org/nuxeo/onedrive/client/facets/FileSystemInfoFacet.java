package org.nuxeo.onedrive.client.facets;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.time.ZonedDateTime;

public class FileSystemInfoFacet extends Facet {
    private ZonedDateTime createdDateTime;

    private ZonedDateTime lastAccessedDateTime;

    private ZonedDateTime lastModifiedDateTime;

    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public ZonedDateTime getLastAccessedDateTime() {
        return lastAccessedDateTime;
    }

    public ZonedDateTime getLastModifiedDateTime() {
        return lastModifiedDateTime;
    }

    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public void setLastAccessedDateTime(ZonedDateTime lastAccessedDateTime) {
        this.lastAccessedDateTime = lastAccessedDateTime;
    }

    public void setLastModifiedDateTime(ZonedDateTime lastModifiedDateTime) {
        this.lastModifiedDateTime = lastModifiedDateTime;
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

    @Override
    protected void parseMember(JsonObject.Member member) {
        JsonValue value = member.getValue();
        String memberName = member.getName();
        switch (memberName)
        {
            case "createdDateTime":
                createdDateTime = ZonedDateTime.parse(value.asString());
                break;
            case "lastModifiedDateTime":
                lastModifiedDateTime = ZonedDateTime.parse(value.asString());
                break;
            case "lastAccessedDateTime":
                lastAccessedDateTime = ZonedDateTime.parse(value.asString());
                break;
        }
    }
}
