package org.nuxeo.onedrive.client.types;

import org.nuxeo.onedrive.client.OneDriveAPI;

public class User extends DirectoryObject {
    private final UserIdType userIdType;

    public User(final OneDriveAPI api, final String id) {
        this(api, id, UserIdType.UUID);
    }

    User(final OneDriveAPI api, final String id, final UserIdType userIdType) {
        super(api, id);
        this.userIdType = userIdType;
    }

    public static User getCurrent(final OneDriveAPI api) {
        return new User(api, "me", null);
    }

    public String getPath() {
        if (null == userIdType) {
            return "/" + getId();
        }
        return "/users/" + getId();
    }

    public String getOperationPath(final String operation) {
        return getPath() + "/" + operation;
    }

    enum UserIdType {
        UUID
    }

    public class Metadata extends DirectoryObject.Metadata {
        @Override
        public User asDirectoryObject() {
            return User.this;
        }
    }
}
