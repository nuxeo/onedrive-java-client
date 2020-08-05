package org.nuxeo.onedrive.client.types;

import com.eclipsesource.json.JsonObject;
import org.nuxeo.onedrive.client.OneDriveAPI;
import org.nuxeo.onedrive.client.OneDriveJsonRequest;
import org.nuxeo.onedrive.client.OneDriveJsonResponse;
import org.nuxeo.onedrive.client.URLTemplate;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class DriveItem extends BaseItem {
    private final ParentReference parent;
    private final ItemIdentifierType itemIdentifierType;

    public DriveItem(Drive parent) {
        // Root Folder of Drive
        super(parent.getApi(), null);
        this.parent = new DriveParent(parent);
        itemIdentifierType = null;
    }

    public DriveItem(Drive parent, String id) {
        // Single Item (directly used with /drive/items/<id>)
        super(parent.getApi(), id);
        this.parent = new DriveParent(parent);
        itemIdentifierType = ItemIdentifierType.Id;
    }

    public DriveItem(DriveItem parent, String path) {
        // Path-relative item (/drive/root/children:/Path/To/File)
        super(parent.getApi(), path);
        this.parent = new ItemParent(parent);
        this.itemIdentifierType = ItemIdentifierType.Path;
    }

    public ItemIdentifierType getItemIdentifierType() {
        return itemIdentifierType;
    }

    public Drive getDrive() {
        if (parent instanceof DriveParent) {
            return ((DriveParent) parent).getParent();
        }
        return ((ItemParent) parent).getParent().getDrive();
    }

    @Override
    public String getPath() {
        if (parent instanceof DriveParent) {
            final DriveParent drive = (DriveParent) parent;
            if (null == itemIdentifierType) {
                return drive.getParent().getAction("/root");
            } else if (ItemIdentifierType.Id == itemIdentifierType) {
                return drive.getParent().getAction("/items/" + getId());
            }
        } else if (parent instanceof ItemParent) {
            final ItemParent item = (ItemParent) parent;
            return item.getParent().getAction(":/" + getId()); //TODO Force non-leading slash in Path?
        }
        return null;
    }

    @Override
    public String getAction(String action) {
        final StringBuilder actionPathBuilder = new StringBuilder();
        actionPathBuilder.append(getPath());

        if (ItemIdentifierType.Path == itemIdentifierType) {
            actionPathBuilder.append(":");
        }
        actionPathBuilder.append(action);

        return actionPathBuilder.toString();
    }

    @Override
    public Metadata getMetadata() throws IOException {
        final URL url = new URLTemplate(getPath()).build(getApi().getBaseURL());
        OneDriveJsonRequest request = new OneDriveJsonRequest(url, "GET");
        OneDriveJsonResponse response = request.sendRequest(getApi().getExecutor());
        return new Metadata().fromJson(response.getContent());
    }

    public enum ItemIdentifierType {
        Id,
        Path
    }

    public static DriveItem.Metadata parseJson(final OneDriveAPI api, final JsonObject jsonObject) {
        final String id = jsonObject.get("id").asString();
        final Drive drive = new Drive(api, jsonObject.get("parentReference").asObject().get("driveId").asString());
        final DriveItem item = new DriveItem(drive, id);
        return item.new Metadata().fromJson(jsonObject);
    }

    private static abstract class ParentReference<T> {
        private final T parent;

        ParentReference(T parent) {
            this.parent = parent;
        }

        public T getParent() {
            return parent;
        }
    }

    private static class DriveParent extends ParentReference<Drive> {
        DriveParent(Drive parent) {
            super(parent);
        }
    }

    private static class ItemParent extends ParentReference<DriveItem> {
        ItemParent(DriveItem parent) {
            super(parent);
        }
    }

    public class Metadata extends BaseItem.Metadata<Metadata> {
        private String cTag;
        private String description;
        private DriveItem.Metadata remoteItem;
        private Integer size;
        private String webDavUrl;

        private final Map<Class, Facet> facetMap = new HashMap<>();

        public Package getPackage() {
            return getFacet(Package.class);
        }

        public File getFile() {
            return getFacet(File.class);
        }

        public Folder getFolder() {
            return getFacet(Folder.class);
        }

        public boolean isPackage() {
            return null != getFacet(Package.class);
        }

        public boolean isFile() {
            return null != getFacet(File.class);
        }

        public boolean isFolder() {
            return null != getFacet(Folder.class);
        }

        public String getcTag() {
            return cTag;
        }

        @Override
        public String getDescription() {
            return description;
        }

        public Metadata getRemoteItem() {
            return remoteItem;
        }

        public Integer getSize() {
            return size;
        }

        public String getWebDavUrl() {
            return webDavUrl;
        }

        public <T extends Facet> T getFacet(Class<T> clazz) {
            return (T) facetMap.getOrDefault(clazz, null);
        }

        @Override
        protected void parseMember(JsonObject.Member member) {
            switch (member.getName()) {
                case "cTag":
                    cTag = member.getValue().asString();
                    break;

                case "description":
                    description = member.getValue().asString();
                    break;

                case "remoteItem":
                    remoteItem = DriveItem.parseJson(getApi(), member.getValue().asObject());
                    break;

                case "size":
                    size = member.getValue().asInt();
                    break;

                case "webDavUrl":
                    webDavUrl = member.getValue().asString();
                    break;

                case "file":
                    facetMap.put(File.class, new File().fromJson(member.getValue().asObject()));
                    break;

                case "fileSystemInfo":
                    facetMap.put(FileSystemInfo.class, new FileSystemInfo().fromJson(member.getValue().asObject()));
                    break;

                case "folder":
                    facetMap.put(Folder.class, new Folder().fromJson(member.getValue().asObject()));
                    break;

                // Properties
                case "audio":
                case "content":
                case "deleted":
                case "image":
                case "location":
                case "pendingOperations":
                case "photo":
                case "publication":
                case "root":
                case "searchResult":
                case "shared":
                case "specialFolder":
                case "video":
                    break;

                // Relations
                case "activities":
                case "analytics":
                case "children":
                case "createdByUser":
                case "lastModifiedByUser":
                case "permissions":
                case "subscriptions":
                case "thumbnails":
                case "versions":
                    break;

                default:
                    super.parseMember(member);
            }
        }
    }
}
