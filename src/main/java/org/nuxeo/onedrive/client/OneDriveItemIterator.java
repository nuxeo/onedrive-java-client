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

import java.net.URL;
import java.util.Iterator;
import java.util.Objects;

import com.eclipsesource.json.JsonObject;

/**
 * @since 1.0
 */
public class OneDriveItemIterator implements Iterator<OneDriveItem.Metadata> {

    private final OneDriveAPI api;

    private final JsonObjectIterator jsonObjectIterator;

    public OneDriveItemIterator(OneDriveAPI api, URL url) {
        this.api = Objects.requireNonNull(api);
        this.jsonObjectIterator = new JsonObjectIterator(api, url);
    }

    @Override
    public boolean hasNext() throws OneDriveRuntimeException {
        return jsonObjectIterator.hasNext();
    }

    @Override
    public OneDriveItem.Metadata next() throws OneDriveRuntimeException {
        JsonObject nextObject = jsonObjectIterator.next();
        String id = nextObject.get("id").asString();

        OneDriveItem.Metadata nextMetadata;
        if (nextObject.get("folder") != null && !nextObject.get("folder").isNull()) {
            OneDriveFolder folder = new OneDriveFolder(api, id);
            nextMetadata = folder.new Metadata(nextObject);
        } else if (nextObject.get("file") != null && !nextObject.get("file").isNull()) {
            OneDriveFile file = new OneDriveFile(api, id);
            nextMetadata = file.new Metadata(nextObject);
        } else {
            throw new OneDriveRuntimeException("The object type is currently not handled.");
        }
        return nextMetadata;
    }

}
