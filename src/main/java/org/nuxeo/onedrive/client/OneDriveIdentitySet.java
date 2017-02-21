/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and others.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *  
 * Contributors:
 *     Kevin Leturc
 */
package org.nuxeo.onedrive.client;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.ParseException;

/**
 * @since 1.0
 */
public class OneDriveIdentitySet extends OneDriveJsonObject {

    private OneDriveIdentity user;

    private OneDriveIdentity application;

    private OneDriveIdentity device;

    public OneDriveIdentitySet(JsonObject json) {
        super(json);
    }

    public OneDriveIdentity getUser() {
        return user;
    }

    public OneDriveIdentity getApplication() {
        return application;
    }

    public OneDriveIdentity getDevice() {
        return device;
    }

    @Override
    protected void parseMember(JsonObject.Member member) {
        super.parseMember(member);
        try {
            JsonValue value = member.getValue();
            String memberName = member.getName();
            if("user".equals(memberName)) {
                user = new OneDriveIdentity(value.asObject());
            }
            else if("application".equals(memberName)) {
                application = new OneDriveIdentity(value.asObject());
            }
            else if("device".equals(memberName)) {
                device = new OneDriveIdentity(value.asObject());
            }
        }
        catch(ParseException e) {
            throw new OneDriveRuntimeException(new OneDriveAPIException(e.getMessage(), e));
        }
    }

}
