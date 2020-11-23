package org.nuxeo.onedrive.client;

import org.junit.Test;
import org.nuxeo.onedrive.client.types.Drive;
import org.nuxeo.onedrive.client.types.DriveItem;

import static org.junit.Assert.assertEquals;

public class OneDriveItemBuilderTest extends OneDriveTestCase {
    private static void assertPath(String expected, Drive drive) {
        assertEquals(expected, new URLTemplate(drive.getPath()).build(drive.getApi().getBaseURL()).getPath());
    }

    private static void assertPath(String expected, DriveItem item) {
        assertEquals(expected, new URLTemplate(item.getPath()).build(item.getApi().getBaseURL()).getPath());
    }

    private static void assertChildrenPath(String expected, DriveItem item) {
        assertEquals(expected, new URLTemplate(item.getAction("/children")).build(item.getApi().getBaseURL()).getPath());
    }

    /*
    Test Matrix
    +-----------------------+-------------------------+----------------------------------+
    |                       |       Drive Null        |         Drive Available          |
    +-----------------------+-------------------------+----------------------------------+
    | Folder ID/Path Null   | /drive/root             | /drives/DriveID/root             |
    | Folder Id Available   | /drive/items/FolderID   | /drives/DriveID/items/FolderID   |
    | Folder Path Available | /drive/root:/FolderPath | /drives/DriveID/root:/FolderPath |
    +-----------------------+-------------------------+----------------------------------+
     */
    @Test
    public void testDriveDefault() {
        Drive drive = new Drive(api);
        assertPath("/v1.0/drive", drive);
    }

    @Test
    public void testDriveId() {
        Drive drive = new Drive(api, "DRIVE");
        assertPath("/v1.0/drives/DRIVE", drive);
    }

    @Test
    public void testDriveNullFolderNull() {
        Drive drive = new Drive(api);
        DriveItem folder = new DriveItem(drive);
        assertPath("/v1.0/drive/root", folder);
        assertChildrenPath("/v1.0/drive/root/children", folder);
    }

    @Test
    public void testDriveNullFolderId() {
        Drive drive = new Drive(api);
        DriveItem folder = new DriveItem(drive, "FOLDERID");
        assertPath("/v1.0/drive/items/FOLDERID", folder);
        assertChildrenPath("/v1.0/drive/items/FOLDERID/children", folder);
    }

    @Test
    public void testDriveNullFolderPath() {
        Drive drive = new Drive(api);
        DriveItem root = new DriveItem(drive);
        DriveItem folder = new DriveItem(root, "FOLDERPATH");

        assertPath("/v1.0/drive/root:/FOLDERPATH", folder);
        assertChildrenPath("/v1.0/drive/root:/FOLDERPATH:/children", folder);
    }

    @Test
    public void testDriveFolderNull() {
        Drive drive = new Drive(api, "DRIVEID");
        DriveItem folder = new DriveItem(drive);
        assertPath("/v1.0/drives/DRIVEID/root", folder);
        assertChildrenPath("/v1.0/drives/DRIVEID/root/children", folder);
    }

    @Test
    public void testDriveFolderId() {
        Drive drive = new Drive(api, "DRIVEID");
        DriveItem folder = new DriveItem(drive, "FOLDERID");
        assertPath("/v1.0/drives/DRIVEID/items/FOLDERID", folder);
        assertChildrenPath("/v1.0/drives/DRIVEID/items/FOLDERID/children", folder);
    }

    @Test
    public void testDriveFolderPath() {
        Drive drive = new Drive(api, "DRIVEID");
        DriveItem root = new DriveItem(drive);
        DriveItem folder = new DriveItem(root, "FOLDERPATH");
        assertPath("/v1.0/drives/DRIVEID/root:/FOLDERPATH", folder);
        assertChildrenPath("/v1.0/drives/DRIVEID/root:/FOLDERPATH:/children", folder);
    }

    @Test
    public void testParent() {
        Drive drive = new Drive(api, "DRIVEID");
        DriveItem folder = new DriveItem(drive, "FOLDERID");
        DriveItem child = new DriveItem(folder, "Test");
        assertPath("/v1.0/drives/DRIVEID/items/FOLDERID:/Test", child);
        assertChildrenPath("/v1.0/drives/DRIVEID/items/FOLDERID:/Test:/children", child);
    }

    @Test
    public void testSpace() {
        Drive drive = new Drive(api, "DRIVEID");
        DriveItem root = new DriveItem(drive);
        DriveItem folder = new DriveItem(root, "SPACE SPACE");
        assertPath("/v1.0/drives/DRIVEID/root:/SPACE SPACE", folder);
        assertChildrenPath("/v1.0/drives/DRIVEID/root:/SPACE SPACE:/children", folder);
    }

    @Test
    public void testEscapedSpace() {
        Drive drive = new Drive(api, "DRIVEID");
        DriveItem root = new DriveItem(drive);
        DriveItem folder = new DriveItem(root, "SPACE%20SPACE");
        assertPath("/v1.0/drives/DRIVEID/root:/SPACE%20SPACE", folder);
        assertChildrenPath("/v1.0/drives/DRIVEID/root:/SPACE%20SPACE:/children", folder);
    }
}
