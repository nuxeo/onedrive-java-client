package org.nuxeo.onedrive.client;

import com.eclipsesource.json.JsonObject;
import org.nuxeo.onedrive.client.types.Drive;
import org.nuxeo.onedrive.client.types.DriveItem;

public abstract class AbstractOperation {
    protected void createParentReference(DriveItem newParent, JsonObject root) {
        root.add("parentReference", newParent.createParentReferenceObject());
    }
}
