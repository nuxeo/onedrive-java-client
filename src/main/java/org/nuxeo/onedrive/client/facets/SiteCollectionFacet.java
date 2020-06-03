package org.nuxeo.onedrive.client.facets;

import com.eclipsesource.json.JsonObject;

public class SiteCollectionFacet extends Facet<SiteCollectionFacet> {
    private String hostname;
    private String dataLocationCode;
    private RootFacet root;

    public String getHostname() {
        return hostname;
    }

    public String getDataLocationCode() {
        return dataLocationCode;
    }

    public RootFacet getRoot() {
        return root;
    }

    @Override
    protected void parseMember(JsonObject.Member member) {
        switch(member.getName()) {
            case "hostname":
                hostname = member.getValue().asString();
                break;
            case "dataLocationCode":
                dataLocationCode = member.getValue().asString();
                break;
            case "root":
                root = new RootFacet().fromJson(member.getValue().asObject());
                break;
            default:
                super.parseMember(member);
        }
    }
}
