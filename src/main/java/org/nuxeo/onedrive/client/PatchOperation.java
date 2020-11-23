package org.nuxeo.onedrive.client;

import com.eclipsesource.json.JsonObject;
import org.nuxeo.onedrive.client.types.Drive;
import org.nuxeo.onedrive.client.types.DriveItem;
import org.nuxeo.onedrive.client.types.Facet;

public class PatchOperation {
    private final JsonObject jsonObject = new JsonObject();

    public void rename(String newName) {
        jsonObject.add("name", newName);
    }

    public void move(DriveItem newParent) {
        final JsonObject parentReference = new JsonObject();

        final Drive rootDrive = newParent.getDrive();
        if (null != rootDrive) {
            parentReference.add("driveId", rootDrive.getId());
            parentReference.add("id", newParent.getId());
        } else if (DriveItem.ItemIdentifierType.Path == newParent.getItemIdentifierType()) {
            parentReference.add("path", newParent.getPath());
        }
        jsonObject.add("parentReference", parentReference);
    }

    public void facet(String property, Facet facet) {
        jsonObject.add(property, facet.toJson());
    }

    JsonObject build() {
        return jsonObject;
    }
}
