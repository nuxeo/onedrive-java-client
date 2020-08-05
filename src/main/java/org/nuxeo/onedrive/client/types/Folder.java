package org.nuxeo.onedrive.client.types;

import com.eclipsesource.json.JsonObject;

public class Folder extends Facet<Folder> {
    private Integer childCount;
    // private FolderView view; // A collection of properties defining the recommended view for the folder.
    // https://docs.microsoft.com/en-us/graph/api/resources/folderview?view=graph-rest-1.0

    public Integer getChildCount() {
        return childCount;
    }

    @Override
    protected void parseMember(JsonObject.Member member) {
        switch (member.getName()) {
            case "childCount":
                childCount = member.getValue().asInt();
                break;

            default:
                super.parseMember(member);
        }
    }
}
