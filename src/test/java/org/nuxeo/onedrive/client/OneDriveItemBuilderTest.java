package org.nuxeo.onedrive.client;

import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class OneDriveItemBuilderTest extends OneDriveTestCase {
    private OneDriveAPI testApi = new OneDriveAPI() {
        @Override
        public RequestExecutor getExecutor() {
            return null;
        }

        @Override
        public boolean isBusinessConnection() {
            return false;
        }

        @Override
        public boolean isGraphConnection() {
            return false;
        }

        @Override
        public String getBaseURL() {
            return "http://mock";
        }

        @Override
        public String getEmailURL() {
            return null;
        }
    };

    @Test
    public void testDriveResolve() {
        OneDriveDrive testDrive = new OneDriveDrive(testApi, "DRIVEID");
        OneDriveFolder folder = new OneDriveFolder(testApi, testDrive);
        assertEquals("/drives/DRIVEID/root", folder.getMetadataURL().build(testApi.getBaseURL(), folder.getResourceIdentifier()).getPath());
        assertEquals("/drives/DRIVEID/root/children", folder.getChildrenURL().build(testApi.getBaseURL(), folder.getResourceIdentifier()).getPath());
    }

    @Test
    public void testDriveSubdirectoryResolve() {
        OneDriveDrive testDrive = new OneDriveDrive(testApi, "DRIVEID");
        OneDriveFolder folder = new OneDriveFolder(testApi, testDrive, "FOLDERPATH");
        assertEquals("/drives/DRIVEID/root:/FOLDERPATH", folder.getMetadataURL().build(testApi.getBaseURL(), folder.getResourceIdentifier()).getPath());
        assertEquals("/drives/DRIVEID/root:/FOLDERPATH:/children", folder.getChildrenURL().build(testApi.getBaseURL(), folder.getResourceIdentifier()).getPath());
    }

    @Test
    public void testIdResolve() {
        OneDriveDrive testDrive = new OneDriveDrive(testApi, "DRIVEID");
        OneDriveFolder folder = new OneDriveFolder(testApi, testDrive, "FOLDERID", OneDriveResource.ResourceIdentifierType.Id);
        assertEquals("/drives/DRIVEID/items/FOLDERID", folder.getMetadataURL().build(testApi.getBaseURL(), folder.getResourceIdentifier()).getPath());
        assertEquals("/drives/DRIVEID/items/FOLDERID/children", folder.getChildrenURL().build(testApi.getBaseURL(), folder.getResourceIdentifier()).getPath());
    }
}
