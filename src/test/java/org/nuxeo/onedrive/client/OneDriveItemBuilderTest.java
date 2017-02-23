package org.nuxeo.onedrive.client;

import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class OneDriveItemBuilderTest extends OneDriveTestCase {
    @Test
    public void testDriveRootResolve() {
        OneDriveDrive testDrive = new OneDriveDrive(api, "DRIVEID");
        OneDriveFolder folder = new OneDriveFolder(api, testDrive);
        assertEquals("/v1.0/drives/DRIVEID/root", folder.getMetadataURL().build(api.getBaseURL(), folder.getResourceIdentifier()).getPath());
        assertEquals("/v1.0/drives/DRIVEID/root/children", folder.getChildrenURL().build(api.getBaseURL(), folder.getResourceIdentifier()).getPath());
    }

    @Test
    public void testRootPathResolve() {
        OneDriveFolder folder = new OneDriveFolder(api, "FOLDERPATH", OneDriveResource.ResourceIdentifierType.Path);
        assertEquals("/v1.0/drive/root:/FOLDERPATH", folder.getMetadataURL().build(api.getBaseURL(), folder.getResourceIdentifier()).getPath());
        assertEquals("/v1.0/drive/root:/FOLDERPATH:/children", folder.getChildrenURL().build(api.getBaseURL(), folder.getResourceIdentifier()).getPath());
    }

    @Test
    public void testRootIdResolve() {
        OneDriveFolder folder = new OneDriveFolder(api, "FOLDERID");
        assertEquals("/v1.0/drive/items/FOLDERID", folder.getMetadataURL().build(api.getBaseURL(), folder.getResourceIdentifier()).getPath());
        assertEquals("/v1.0/drive/items/FOLDERID/children", folder.getChildrenURL().build(api.getBaseURL(), folder.getResourceIdentifier()).getPath());
    }

    @Test
    public void testDriveIdResolve() {
        OneDriveDrive testDrive = new OneDriveDrive(api, "DRIVEID");
        OneDriveFolder folder = new OneDriveFolder(api, testDrive, "FOLDERID", OneDriveResource.ResourceIdentifierType.Id);
        assertEquals("/v1.0/drives/DRIVEID/items/FOLDERID", folder.getMetadataURL().build(api.getBaseURL(), folder.getResourceIdentifier()).getPath());
        assertEquals("/v1.0/drives/DRIVEID/items/FOLDERID/children", folder.getChildrenURL().build(api.getBaseURL(), folder.getResourceIdentifier()).getPath());
    }

    @Test
    public void testDriveSubdirectoryResolve() {
        OneDriveDrive testDrive = new OneDriveDrive(api, "DRIVEID");
        OneDriveFolder folder = new OneDriveFolder(api, testDrive, "FOLDERPATH");
        assertEquals("/v1.0/drives/DRIVEID/root:/FOLDERPATH", folder.getMetadataURL().build(api.getBaseURL(), folder.getResourceIdentifier()).getPath());
        assertEquals("/v1.0/drives/DRIVEID/root:/FOLDERPATH:/children", folder.getChildrenURL().build(api.getBaseURL(), folder.getResourceIdentifier()).getPath());
    }
}
