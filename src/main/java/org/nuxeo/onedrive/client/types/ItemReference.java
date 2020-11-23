package org.nuxeo.onedrive.client.types;

import com.eclipsesource.json.JsonObject;

public class ItemReference extends GraphType<ItemReference> {
    private String driveId;
    private Drive.DriveType driveType;
    private String id;
    private String name;
    private String path;
    private String shareId;
    private SharePointIds sharePointIds;

    public String getDriveId() {
        return driveId;
    }

    public Drive.DriveType getDriveType() {
        return driveType;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getShareId() {
        return shareId;
    }

    public SharePointIds getSharePointIds() {
        return sharePointIds;
    }

    @Override
    protected void parseMember(JsonObject.Member member) {
        switch (member.getName()) {
            case "driveId":
                driveId = member.getValue().asString();
                break;
            case "driveType":
                driveType = Drive.DriveType.valueOf(member.getValue().asString());
                break;
            case "id":
                id = member.getValue().asString();
                break;
            case "name":
                name = member.getValue().asString();
                break;
            case "path":
                path = member.getValue().asString();
                break;
            case "shareId":
                shareId = member.getValue().asString();
                break;
            case "sharepointIds":
                sharePointIds = new SharePointIds().fromJson(member.getValue().asObject());
                break;

            default:
                super.parseMember(member);
        }
    }
}
