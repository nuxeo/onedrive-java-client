package org.nuxeo.onedrive.client.resources;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.ParseException;
import org.nuxeo.onedrive.client.OneDriveAPIException;
import org.nuxeo.onedrive.client.OneDriveRuntimeException;

import java.time.ZonedDateTime;

public class GroupItem extends DirectoryObject {
    public GroupItem(String id) {
        super(id);
    }

    public static class Metadata extends DirectoryObject.Metadata {
        private boolean allowExternalSenders = false;
        private boolean autoSubscribeNewMembers = false;
        private ZonedDateTime createdDateTime;
        private String description;
        private String displayName;
        private boolean isSubscribedByMail = true;
        private String mail;
        private boolean mailEnabled;
        private String mailNickname;
        private ZonedDateTime onPremisesLastSyncDateTime;
        private String onPremisesSecurityIdentifier;
        private Boolean onPremisesSyncEnabled = null;
        private ZonedDateTime renewedDateTime;
        private boolean securityEnabled;
        private int unseenCount;
        private String visibility;

        public Metadata() {
        }

        public Metadata(JsonObject json) {
            super(json);
        }

        @Override
        public GroupItem asDirectoryObject() {
            return new GroupItem(getId());
        }

        public boolean isAllowExternalSenders() {
            return allowExternalSenders;
        }

        public boolean isAutoSubscribeNewMembers() {
            return autoSubscribeNewMembers;
        }

        public ZonedDateTime getCreatedDateTime() {
            return createdDateTime;
        }

        public String getDescription() {
            return description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isSubscribedByMail() {
            return isSubscribedByMail;
        }

        public String getMail() {
            return mail;
        }

        public boolean isMailEnabled() {
            return mailEnabled;
        }

        public String getMailNickname() {
            return mailNickname;
        }

        public ZonedDateTime getOnPremisesLastSyncDateTime() {
            return onPremisesLastSyncDateTime;
        }

        public String getOnPremisesSecurityIdentifier() {
            return onPremisesSecurityIdentifier;
        }

        public Boolean getOnPremisesSyncEnabled() {
            return onPremisesSyncEnabled;
        }

        public ZonedDateTime getRenewedDateTime() {
            return renewedDateTime;
        }

        public boolean isSecurityEnabled() {
            return securityEnabled;
        }

        public int getUnseenCount() {
            return unseenCount;
        }

        public String getVisibility() {
            return visibility;
        }

        @Override
        protected void parseMember(JsonObject.Member member) {
            final String name = member.getName();
            final JsonValue value = member.getValue();
            try {
                switch (name) {
                    case "allowExternalSenders":
                        allowExternalSenders = value.asBoolean();
                        break;
                    case "autoSubscribeNewMembers":
                        autoSubscribeNewMembers = value.asBoolean();
                        break;
                    case "createdDateTime":
                        createdDateTime = ZonedDateTime.parse(value.asString());
                        break;
                    case "description":
                        description = value.asString();
                        break;
                    case "displayName":
                        displayName = value.asString();
                        break;
                    case "isSubscribedByMail":
                        isSubscribedByMail = value.asBoolean();
                        break;
                    case "mail":
                        mail = value.asString();
                        break;
                    case "mailEnabled":
                        mailEnabled = value.asBoolean();
                        break;
                    case "mailNickname":
                        mailNickname = value.asString();
                        break;
                    case "onPremisesLastSyncDate":
                        onPremisesLastSyncDateTime = ZonedDateTime.parse(value.asString());
                        break;
                    case "onPremisesSecurityIdentifier":
                        onPremisesSecurityIdentifier = value.asString();
                        break;
                    case "onPremisesSyncEnabled":
                        onPremisesSyncEnabled = value.isNull() ? null : value.asBoolean();
                        break;
                    case "renewedDateTime":
                        renewedDateTime = ZonedDateTime.parse(value.asString());
                        break;
                    case "securityEnabled":
                        securityEnabled = value.asBoolean();
                        break;
                    case "unseenCount":
                        unseenCount = value.asInt();
                        break;
                    case "visibility":
                        visibility = value.asString();
                        break;

                    case "groupTypes":
                    case "proxyAddresses":
                    case "acceptedSenders":
                    case "calendar":
                    case "calendarView":
                    case "conversations":
                    case "createdOnBehalfOf":
                    case "drive":
                    case "extensions":
                    case "memberOf":
                    case "members":
                    case "onenote":
                    case "owners":
                    case "photo":
                    case "photos":
                    case "planner":
                    case "rejectedSenders":
                    case "settings":
                    case "sites":
                    case "threads":
                        break; // TODO not handled

                    default:
                        super.parseMember(member);
                }
            } catch (ParseException e) {
                throw new OneDriveRuntimeException(new OneDriveAPIException(e.getMessage(), e));
            }
        }
    }
}
