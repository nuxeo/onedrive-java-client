package org.nuxeo.onedrive.client.facets;

import com.eclipsesource.json.JsonObject;

public class SharePointIdsFacet extends Facet<SharePointIdsFacet> {
    private String listId;
    private String listItemId;
    private String listItemUniqueId;
    private String siteId;
    private String siteUrl;
    private String tenantId;
    private String webId;

    public String getListId() {
        return listId;
    }

    public String getListItemId() {
        return listItemId;
    }

    public String getListItemUniqueId() {
        return listItemUniqueId;
    }

    public String getSiteId() {
        return siteId;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getWebId() {
        return webId;
    }

    @Override
    protected void parseMember(JsonObject.Member member) {
        switch (member.getName()) {
            case "listId":
                listId = member.getValue().asString();
                break;
            case "listItemid":
                listItemId = member.getValue().asString();
                break;
            case "listItemUniqueId":
                listItemUniqueId = member.getValue().asString();
                break;
            case "siteId":
                siteId = member.getValue().asString();
                break;
            case "siteUrl":
                siteUrl = member.getValue().asString();
                break;
            case "tenantId":
                tenantId = member.getValue().asString();
                break;
            case "webId":
                webId = member.getValue().asString();
                break;
            default:
                super.parseMember(member);
        }
    }
}
