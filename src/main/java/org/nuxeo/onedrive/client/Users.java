package org.nuxeo.onedrive.client;

import com.eclipsesource.json.JsonObject;
import org.nuxeo.onedrive.client.types.DirectoryObject;
import org.nuxeo.onedrive.client.types.GroupItem;
import org.nuxeo.onedrive.client.types.User;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

public class Users {

    public static User.Metadata get(final User user, final User.Select... select) throws IOException {
        final QueryStringBuilder query = new QueryStringBuilder().set("$select", select);
        final URL url = new URLTemplate(user.getPath()).build(user.getApi().getBaseURL(), query);
        final OneDriveJsonRequest request = new OneDriveJsonRequest(url, "GET");
        final OneDriveJsonResponse response = request.sendRequest(user.getApi().getExecutor());
        final JsonObject jsonObject = response.getContent();
        response.close();
        return user.new Metadata(jsonObject);
    }

    public static Iterator<DirectoryObject.Metadata> memberOf(final OneDriveAPI api, final User user) {
        return new MemberOfIterator(api, new URLTemplate(user.getOperationPath("memberOf")).build(api.getBaseURL()));
    }

    private static class MemberOfIterator implements Iterator<DirectoryObject.Metadata> {
        private final OneDriveAPI api;
        private final JsonObjectIterator iterator;

        MemberOfIterator(final OneDriveAPI api, final URL url) {
            this.api = api;
            iterator = new JsonObjectIterator(api, url);
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public DirectoryObject.Metadata next() {
            final JsonObject root = iterator.next();
            final String type = root.get("@odata.type").asString();
            final String id = root.get("id").asString();
            if ("#microsoft.graph.group".equals(type)) {
                return new GroupItem(api, id).new Metadata(root);
            }
            throw new RuntimeException("The object type is currently not handled");
        }
    }
}
