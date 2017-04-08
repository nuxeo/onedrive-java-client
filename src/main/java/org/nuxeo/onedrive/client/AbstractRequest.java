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

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @since 1.0
 */
public abstract class AbstractRequest<R extends AbstractResponse> {

    private final Set<RequestHeader> headers = new HashSet<>();
    private final String method;
    private final URL url;

    /**
     * Constructs an unauthenticated request.
     */
    public AbstractRequest(final URL url, final String method) {
        this.url = Objects.requireNonNull(url);
        this.method = Objects.requireNonNull(method);

        this.addHeader("Accept-Encoding", "gzip");
        this.addHeader("Accept-Charset", "utf-8");
    }

    public void addHeader(final String key, final String value) {
        this.headers.add(new RequestHeader(key, value));
    }

    public R sendRequest(final RequestExecutor sender) throws IOException {
        return this.sendRequest(sender, null);
    }

    public R sendRequest(final RequestExecutor sender, final InputStream body) throws IOException {
        switch(method) {
            case "GET": {
                final RequestExecutor.Response response = sender.doGet(url, headers);
                return this.createResponse(response);
            }
            case "DELETE": {
                final RequestExecutor.Response response = sender.doDelete(url, headers);
                return this.createResponse(response);
            }
            case "POST": {
                final RequestExecutor.Upload response = sender.doPost(url, headers);
                IOUtils.copy(body, response.getOutputStream());
                return this.createResponse(response.getResponse());
            }
            case "PUT": {
                final RequestExecutor.Upload response = sender.doPut(url, headers);
                IOUtils.copy(body, response.getOutputStream());
                return this.createResponse(response.getResponse());
            }
            default: {
                throw new OneDriveAPIException(String.format("Unsupported HTTP method %s", method));
            }
        }
    }

    protected abstract R createResponse(RequestExecutor.Response response) throws IOException;

}
