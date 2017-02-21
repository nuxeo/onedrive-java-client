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

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * @since 1.0
 */
class JsonObjectIterator implements Iterator<JsonObject> {

    private final OneDriveAPI api;

    private URL url;

    private boolean hasMorePages;

    private Iterator<JsonValue> currentPage;

    public JsonObjectIterator(OneDriveAPI api, URL url) {
        this.api = api;
        this.url = url;
        this.hasMorePages = true;
    }

    @Override
    public boolean hasNext() throws OneDriveRuntimeException {
        if(currentPage != null && currentPage.hasNext()) {
            return true;
        }
        else if(hasMorePages) {
            loadNextPage();
            return currentPage != null && currentPage.hasNext();
        }
        return false;
    }

    @Override
    public JsonObject next() throws OneDriveRuntimeException {
        if(hasNext()) {
            return currentPage.next().asObject();
        }
        throw new NoSuchElementException();
    }

    private void loadNextPage() throws OneDriveRuntimeException {
        try {
            OneDriveJsonRequest request = new OneDriveJsonRequest(url, "GET");
            OneDriveJsonResponse response = request.sendRequest(api.getExecutor());
            JsonObject json = response.getContent();
            onResponse(json);

            JsonValue values = json.get("value");
            if(values.isNull()) {
                currentPage = Collections.emptyIterator();
            }
            else {
                currentPage = values.asArray().iterator();
            }

            JsonValue nextUrl = json.get("@odata.nextLink");
            hasMorePages = nextUrl != null && !nextUrl.isNull();
            if(hasMorePages) {
                url = new URL(nextUrl.asString());
            }
        }
        catch(IOException e) {
            throw new OneDriveRuntimeException(new OneDriveAPIException(e.getMessage(), e));
        }
    }

    /**
     * @since 1.1
     */
    protected void onResponse(JsonObject response) {
        // Hook method
    }

}
