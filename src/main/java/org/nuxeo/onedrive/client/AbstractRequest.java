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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @since 1.0
 */
public abstract class AbstractRequest<R extends AbstractResponse> {

    private static final int MAX_REDIRECTS = 3;

    private static final String USER_AGENT = "Nuxeo OneDrive Java SDK v1.0";

    private final OneDriveAPI api;

    private final List<RequestHeader> headers;

    private final String method;

    private URL url;

    private int timeout;

    private int numRedirects;

    /**
     * Constructs an unauthenticated request.
     */
    public AbstractRequest(URL url, String method) {
        this(null, url, method);
    }

    /**
     * Constructs an authenticated request using a provided OneDriveAPI.
     */
    public AbstractRequest(OneDriveAPI api, URL url, String method) {
        this.api = api;
        this.url = Objects.requireNonNull(url);
        this.method = Objects.requireNonNull(method);
        this.headers = new ArrayList<>();

        addHeader("Accept-Encoding", "gzip");
        addHeader("Accept-Charset", "utf-8");
    }

    public void addHeader(String key, String value) {
        this.headers.add(new RequestHeader(key, value));
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public R send() throws OneDriveAPIException {
        HttpURLConnection connection = createConnection();

        connection.setRequestProperty("User-Agent", USER_AGENT);
        if (api != null) {
            connection.addRequestProperty("Authorization", "Bearer " + api.getAccessToken());
        }

        try {
            connection.connect();
        } catch (IOException e) {
            throw new OneDriveAPIException("Couldn't connect to the OneDrive API due to a network error.", e);
        }

        // We need to manually handle redirects by creating a new HttpURLConnection so that connection pooling
        // happens correctly. There seems to be a bug in Oracle's Java implementation where automatically handled
        // redirects will not keep the connection alive.
        int responseCode;
        try {
            responseCode = connection.getResponseCode();
        } catch (IOException e) {
            throw new OneDriveAPIException("Couldn't connect to the OneDrive API due to a network error.", e);
        }

        if (isResponseRedirect(responseCode)) {
            return handleRedirect(connection);
        }

        return createResponse(connection);
    }

    private R handleRedirect(HttpURLConnection connection) throws OneDriveAPIException {
        if (this.numRedirects >= MAX_REDIRECTS) {
            throw new OneDriveAPIException("The OneDrive API responded with too many redirects.");
        }
        this.numRedirects++;

        // We need to read the InputStream of response, unless Java won't put the connection back in the connection pool
        try {
            AbstractResponse.readStream(connection.getInputStream());
        } catch (IOException e) {
            throw new OneDriveAPIException("Couldn't connect to the OneDrive API due to a network error.", e);
        }

        try {
            String redirect = connection.getHeaderField("Location");
            url = new URL(redirect);
        } catch (MalformedURLException e) {
            throw new OneDriveAPIException("The OneDrive API responded with an invalid redirect url.", e);
        }
        return send();
    }

    private HttpURLConnection createConnection() throws OneDriveAPIException {
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            throw new OneDriveAPIException("Couldn't connect to OneDrive API due to a network error.", e);
        }

        try {
            connection.setRequestMethod(method);
        } catch (ProtocolException e) {
            throw new OneDriveAPIException("Couldn't connect to OneDrive API because method is not correct.", e);
        }

        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);

        // Disable redirects on connection because we handle it manually
        connection.setInstanceFollowRedirects(false);

        headers.forEach(header -> connection.addRequestProperty(header.getKey(), header.getValue()));

        return connection;
    }

    private static boolean isResponseRedirect(int responseCode) {
        return (responseCode == 301 || responseCode == 302);
    }

    protected abstract R createResponse(HttpURLConnection connection) throws OneDriveAPIException;

    private final class RequestHeader {

        private final String key;

        private final String value;

        public RequestHeader(String key, String value) {
            this.key = Objects.requireNonNull(key);
            this.value = Objects.requireNonNull(value);
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

    }

}
