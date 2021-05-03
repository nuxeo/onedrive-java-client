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

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.ParseException;
import org.apache.commons.io.input.ProxyInputStream;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @since 1.0
 */
public abstract class AbstractResponse<C> implements Closeable {

    private final RequestExecutor.Response response;

    private final int responseCode;
    private final String responseMessage;
    private final String location;

    /**
     * The regular InputStream is the right stream to read body with raw or gzip content.
     */
    private final InputStream inputStream;

    private boolean closed;

    public AbstractResponse(final RequestExecutor.Response response) throws IOException {
        this.response = response;
        this.responseCode = response.getStatusCode();
        this.responseMessage = response.getStatusMessage();
        this.location = response.getLocation();
        this.inputStream = response.getInputStream();
        this.validate();
    }

    public RequestExecutor.Response getResponse() {
        return response;
    }

    public abstract C getContent() throws IOException;

    public JsonObject getError() throws IOException {
        try (InputStreamReader in = new InputStreamReader(this.getBody(), StandardCharsets.UTF_8)) {
            return Json.parse(in).asObject();
        }
        catch(ParseException e) {
            // Response body is empty for failing GET requests
            return null;
        }
    }

    protected InputStream getBody() throws OneDriveAPIException {
        return new ResponseInputStream();
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getLocation() {
        return location;
    }

    /**
     *
     * @param header Header name
     * @return Header value or null
     */
    public String getHeader(String header) {
        return response.getHeader(header);
    }

    /**
     * @return A string representation of error. Method returns the content of error stream.
     */
    protected String getResponseMessage() {
        return responseMessage;
    }

    /**
     * Disconnects the response, close the input stream, so body can no longer be read after.
     */
    @Override
    public void close() throws IOException {
        if(closed) {
            return;
        }
        inputStream.close();
        closed = true;
    }

    protected void validate() throws IOException {
        if(!this.isSuccess()) {
            final JsonObject error = this.getError();
            if(null == error) {
                throw new OneDriveAPIException(responseMessage, responseCode);
            }
            final String retry = this.getHeader("Retry-After");
            if(retry != null) {
                throw new OneDriveAPIException(responseMessage, responseCode, error, Integer.valueOf(retry));
            }
            throw new OneDriveAPIException(responseMessage, responseCode, error);
        }
    }

    public boolean isSuccess() {
        return responseCode >= 200 && responseCode < 300;
    }

    private class ResponseInputStream extends ProxyInputStream {

        public ResponseInputStream() {
            super(inputStream);
        }

        @Override
        public void close() throws IOException {
            // Don't close the stream, it will be done by the response
            AbstractResponse.this.close();
        }
    }
}
