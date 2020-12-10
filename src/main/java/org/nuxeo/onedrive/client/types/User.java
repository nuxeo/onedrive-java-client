package org.nuxeo.onedrive.client.types;

import com.eclipsesource.json.JsonObject;
import org.nuxeo.onedrive.client.OneDriveAPI;
import org.nuxeo.onedrive.client.QueryStringCommaParameter;

import java.util.Optional;

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

    @Override
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

    public enum Select implements QueryStringCommaParameter {
        CreationType("creationType"),
        UserPrincipalName("userPrincipalName");

        private final String key;

        Select(final String key) {
            this.key = key;
        }

        @Override
        public String getKey() {
            return key;
        }
    }

    public class Metadata extends DirectoryObject.Metadata {
        private Optional<String> creationType;
        private String userPrincipalName;

        public Optional<String> getCreationType() {
            return creationType;
        }

        public String getUserPrincipalName() {
            return userPrincipalName;
        }

        public Metadata(final JsonObject jsonObject) {
            super(jsonObject);
        }

        @Override
        public User asDirectoryObject() {
            return User.this;
        }

        @Override
        protected void parseMember(JsonObject.Member member) {
            switch (member.getName()) {
                case "aboutMe":
                case "accountEnabled":
                case "ageGroup":
                case "assignedLicenses":
                case "assignedPlans":
                case "birthday":
                case "businessPhones":
                case "city":
                case "companyName":
                case "consentProvidedForMinor":
                case "country":
                case "createdDateTime":
                    break;
                case "creationType":
                    // creationType can be non-assigned (Microsoft Account)
                    // or null, Inviation, LocalAccount or EmailVerified.
                    creationType = member.getValue().isNull() ? Optional.empty() : Optional.ofNullable(member.getValue().asString());
                    break;
                case "department":
                case "displayName":
                case "employeeHireDate":
                case "employeeId":
                case "employeeOrgData":
                case "employeeType":
                case "faxNumber":
                case "givenName":
                case "hireDate":
                case "identities":
                case "imAddresses":
                case "interests":
                case "isResourceAccount":
                case "jobTitle":
                case "legalAgeGroupClassification":
                case "licenseAssignmentStates":
                case "lastPasswordChangeDateTime":
                case "mail":
                case "mailboxSettings":
                case "mailNickname":
                case "mobilePhone":
                case "mySite":
                case "officeLocation":
                case "onPremisesDistinguishedName":
                case "onPremisesDomainName":
                case "onPremisesExtensionAttributes":
                case "onPremisesImmutableId":
                case "onPremisesLastSyncDateTime":
                case "onPremisesProvisioningErrors":
                case "onPremisesSamAccountName":
                case "onPremisesSecurityIdentifier":
                case "onPremisesSyncEnabled":
                case "onPremisesUserPrincipalName":
                case "otherMails":
                case "passwordPolicies":
                case "passwordProfile":
                case "pastProjects":
                case "postalCode":
                case "preferredLanguage":
                case "preferredName":
                case "provisionedPlans":
                case "proxyAddresses":
                case "responsibilities":
                case "schools":
                case "showInAddressList":
                case "signInSessionsValidFromDateTime":
                case "skills":
                case "state":
                case "streetAddress":
                case "surname":
                case "usageLocation":
                    break;
                case "userPrincipalName":
                    userPrincipalName = member.getValue().asString();
                    break;
                case "userType":
                    break;
                default:
                    super.parseMember(member);
            }
        }
    }
}
