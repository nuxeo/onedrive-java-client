package org.nuxeo.onedrive.client;

import org.junit.Test;
import org.nuxeo.onedrive.client.types.Site;

import static org.junit.Assert.assertEquals;

public class SiteBuilderTest extends OneDriveTestCase {
    @Test
    public void TestRoot() {
        Site site = Site.byId(api, "root");
        assertEquals("/sites/root", site.getPath());
    }

    @Test
    public void TestHostname() {
        Site site = Site.byHostname(api, "HOSTNAME");
        assertEquals("/sites/HOSTNAME", site.getPath());
    }

    @Test
    public void TestRootPath() {
        Site site = Site.byId(api, "root");
        Site test = Site.byPath(site, "/PATH");
        assertEquals("/sites/root:/PATH", test.getPath());
    }

    @Test
    public void TestRootId() {
        Site site = Site.byId(api, "root");
        Site test = Site.byId(site, "ID");
        assertEquals("/sites/root/sites/ID", test.getPath());
    }

    @Test
    public void TestHostnamePath() {
        Site site = Site.byHostname(api, "HOSTNAME");
        Site test = Site.byPath(site, "/PATH");
        assertEquals("/sites/HOSTNAME:/PATH", test.getPath());
    }

    @Test
    public void TestHostnameId() {
        Site site = Site.byHostname(api, "HOSTNAME");
        Site test = Site.byId(site, "ID");
        assertEquals("/sites/HOSTNAME/sites/ID", test.getPath());
    }
}
