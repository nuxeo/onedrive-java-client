package org.nuxeo.onedrive.client.types;

import com.eclipsesource.json.JsonObject;

public class Hashes extends GraphType<Hashes> {
    private String crc32Hash;
    private String sha1Hash;
    private String sha256Hash;
    private String quickXorHash;

    public String getCRC32Hash() {
        return crc32Hash;
    }

    public String getSHA1Hash() {
        return sha1Hash;
    }

    public String getSHA256Hash() {
        return sha256Hash;
    }

    public String getQuickXorHash() {
        return quickXorHash;
    }

    @Override
    protected void parseMember(JsonObject.Member member) {
        switch (member.getName()) {
            case "crc32Hash":
                crc32Hash = member.getValue().asString();
                break;
            case "sha1Hash":
                sha1Hash = member.getValue().asString();
                break;
            case "sha256Hash":
                sha256Hash = member.getValue().asString();
                break;
            case "quickXorhash":
                quickXorHash = member.getValue().asString();
                break;

            default:
                super.parseMember(member);
        }
    }
}
