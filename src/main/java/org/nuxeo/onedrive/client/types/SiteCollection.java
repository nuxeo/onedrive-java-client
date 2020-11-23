package org.nuxeo.onedrive.client.types;

import com.eclipsesource.json.JsonObject;

public class SiteCollection extends Facet<SiteCollection> {
    private String hostname;
    private String dataLocationCode;
    private Root root;

    public String getHostname() {
        return hostname;
    }

    public String getDataLocationCode() {
        return dataLocationCode;
    }

    public Root getRoot() {
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
                root = new Root().fromJson(member.getValue().asObject());
                break;
            default:
                super.parseMember(member);
        }
    }
}
