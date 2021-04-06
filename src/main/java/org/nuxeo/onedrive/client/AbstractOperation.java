package org.nuxeo.onedrive.client;

import com.eclipsesource.json.JsonObject;
import org.nuxeo.onedrive.client.types.Drive;
import org.nuxeo.onedrive.client.types.DriveItem;

public abstract class AbstractOperation {
    protected void createParentReference(DriveItem newParent, JsonObject root) {
        final JsonObject parentReference = new JsonObject();

        if (DriveItem.ItemIdentifierType.Id == newParent.getItemIdentifierType()) {
            final Drive rootDrive = newParent.getDrive();
            if (null != rootDrive && null != rootDrive.getId() && !"".equals(rootDrive.getId())) {
                parentReference.add("driveId", rootDrive.getId());
                parentReference.add("id", newParent.getId());
            }
        } else {
            parentReference.add("path", newParent.getPath());
        }

        root.add("parentReference", parentReference);
    }
}
