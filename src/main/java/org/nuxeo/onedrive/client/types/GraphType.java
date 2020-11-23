package org.nuxeo.onedrive.client.types;

import com.eclipsesource.json.JsonObject;
import org.nuxeo.onedrive.client.OneDriveJsonObject;

public abstract class GraphType<T extends GraphType<T>> extends OneDriveJsonObject {
    public JsonObject toJson() {
        final JsonObject jsonObject = new JsonObject();

        populateJsonObject(jsonObject);

        return jsonObject;
    }

    public T fromJson(JsonObject jsonObject) {
        parseMember(jsonObject, this::parseMember);
        return (T) this;
    }

    protected void populateJsonObject(JsonObject jsonObject) {
    }
}
