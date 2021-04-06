package org.nuxeo.onedrive.client;

import com.eclipsesource.json.JsonObject;
import org.nuxeo.onedrive.client.types.DriveItem;
import org.nuxeo.onedrive.client.types.Facet;

public class PatchOperation extends AbstractOperation {
    private final JsonObject jsonObject = new JsonObject();

    public void rename(String newName) {
        jsonObject.add("name", newName);
    }

    public void move(DriveItem newParent) {
        createParentReference(newParent, jsonObject);
    }

    public void facet(String property, Facet facet) {
        jsonObject.add(property, facet.toJson());
    }

    JsonObject build() {
        return jsonObject;
    }
}
