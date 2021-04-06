package org.nuxeo.onedrive.client;

import com.eclipsesource.json.JsonObject;
import org.nuxeo.onedrive.client.types.DriveItem;

public class CopyOperation extends AbstractOperation {
    private final JsonObject jsonObject = new JsonObject();

    public void rename(String newName) {
        jsonObject.add("name", newName);
    }

    public void copy(DriveItem newParent) {
        createParentReference(newParent, jsonObject);
    }

    JsonObject build() {
        return jsonObject;
    }
}
