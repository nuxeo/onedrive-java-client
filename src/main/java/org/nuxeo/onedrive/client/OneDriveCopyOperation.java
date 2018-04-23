package org.nuxeo.onedrive.client;

import com.eclipsesource.json.JsonObject;

public class OneDriveCopyOperation {
    private final JsonObject jsonObject = new JsonObject();

    public void rename(String newName) {
        jsonObject.add("name", newName);
    }

    public void copy(OneDriveFolder newParent) {
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

    JsonObject build() {
        return jsonObject;
    }
}
