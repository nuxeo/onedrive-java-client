package org.nuxeo.onedrive.client;

import com.eclipsesource.json.JsonObject;
import org.nuxeo.onedrive.client.facets.Facet;

public class OneDrivePatchOperation {
    private final JsonObject jsonObject = new JsonObject();

    public void rename(String newName) {
        jsonObject.add("name", newName);
    }

    public void move(OneDriveFolder newParent) {
        final JsonObject parentReference = new JsonObject();

        final OneDriveDrive rootDrive = newParent.getDrive();
        if (null != rootDrive) {
            parentReference.add("driveId", newParent.getDrive().getItemIdentifier());
            parentReference.add("id", newParent.getItemIdentifier());
        }
        else if (OneDriveItem.ItemIdentifierType.Path == newParent.getItemIdentifierType()) {
            parentReference.add("path", newParent.getFullyQualifiedPath());
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
