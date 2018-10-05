package org.nuxeo.onedrive.client.facets;

import com.eclipsesource.json.JsonObject;
import org.nuxeo.onedrive.client.OneDriveJsonObject;

import java.util.function.Consumer;

public abstract class Facet {
    public JsonObject toJson() {
        final JsonObject jsonObject = new JsonObject();

        populateJsonObject(jsonObject);

        return jsonObject;
    }

    public void fromJson(JsonObject jsonObject) {
        parseMember(jsonObject, this::parseMember);
    }

    protected void populateJsonObject(JsonObject jsonObject) {
    }

    protected void parseMember(JsonObject.Member member) {
    }

    protected static void parseMember(JsonObject json, Consumer<JsonObject.Member> consumer) {
        for (JsonObject.Member member : json) {
            if (member.getValue().isNull()) {
                continue;
            }
            consumer.accept(member);
        }
    }
}
