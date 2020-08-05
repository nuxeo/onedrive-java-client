package org.nuxeo.onedrive.client.types;

import com.eclipsesource.json.JsonObject;

public class Quota extends GraphType<Quota> {
    private Long deleted;
    private Long remaining;
    private Long total;
    private Long used;

    public Long getDeleted() {
        return deleted;
    }

    public Long getRemaining() {
        return remaining;
    }

    public Long getTotal() {
        return total;
    }

    public Long getUsed() {
        return used;
    }

    @Override
    protected void parseMember(JsonObject.Member member) {
        switch (member.getName()) {
            case "deleted":
                deleted = member.getValue().asLong();
                break;
            case "remaining":
                remaining = member.getValue().asLong();
                break;
            case "total":
                total = member.getValue().asLong();
                break;
            case "used":
                used = member.getValue().asLong();
                break;
            default:
                super.parseMember(member);
        }
    }
}
