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

import java.util.Iterator;
import java.util.Objects;

import com.eclipsesource.json.JsonObject;

/**
 * @since 1.0
 */
public class OneDriveThumbnailSetIterator implements Iterator<OneDriveThumbnailSet.Metadata> {

    private static final URLTemplate GET_THUMBNAILS_URL = new URLTemplate("/drive/items/%s/thumbnails");

    private static final URLTemplate GET_THUMBNAILS_ROOT_URL = new URLTemplate("/drive/items/%s/thumbnails");

    private final OneDriveAPI api;

    private final String itemId;

    private final JsonObjectIterator jsonObjectIterator;

    OneDriveThumbnailSetIterator(OneDriveAPI api) {
        this.api = Objects.requireNonNull(api);
        this.itemId = null;
        this.jsonObjectIterator = new JsonObjectIterator(api, GET_THUMBNAILS_ROOT_URL.build(api.getBaseURL()));
    }

    public OneDriveThumbnailSetIterator(OneDriveAPI api, String itemId) {
        this.api = Objects.requireNonNull(api);
        this.itemId = Objects.requireNonNull(itemId);
        this.jsonObjectIterator = new JsonObjectIterator(api, GET_THUMBNAILS_URL.build(api.getBaseURL(), itemId));
    }

    @Override
    public boolean hasNext() throws OneDriveRuntimeException {
        return jsonObjectIterator.hasNext();
    }

    @Override
    public OneDriveThumbnailSet.Metadata next() throws OneDriveRuntimeException {
        JsonObject nextObject = jsonObjectIterator.next();
        int id = Integer.parseInt(nextObject.get("id").asString());

        OneDriveThumbnailSet thumbnail;
        if(itemId == null) {
            thumbnail = new OneDriveThumbnailSet(api, id);
        }
        else {
            thumbnail = new OneDriveThumbnailSet(api, itemId, id);
        }
        return thumbnail.new Metadata(nextObject);
    }

}
