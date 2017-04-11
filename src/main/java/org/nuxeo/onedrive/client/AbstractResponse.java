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
import org.apache.commons.io.input.ProxyInputStream;
import org.apache.commons.io.output.NullOutputStream;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

/**
 * @since 1.0
 */
public abstract class AbstractResponse<C> implements Closeable {

    private final int responseCode;
    private final String responseMessage;

    /**
     * The regular InputStream is the right stream to read body with raw or gzip content.
     */
    private final InputStream inputStream;

    private boolean closed;

    public AbstractResponse(final int responseCode, final String responseMessage, final InputStream inputStream) throws IOException {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.inputStream = inputStream;
        this.validate();
    }

    public abstract C getContent() throws IOException;

    public JsonObject getError() throws IOException {
        try (InputStreamReader in = new InputStreamReader(this.getBody(), StandardCharsets.UTF_8)) {
            return Json.parse(in).asObject();
        }
    }

    protected InputStream getBody() throws OneDriveAPIException {
        return new ResponseInputStream();
    }

    public int getResponseCode() {
        return responseCode;
    }

    /**
     * Returns a string representation of error. Method returns the content of error stream.
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
            throw new OneDriveAPIException(responseMessage, responseCode, this.getError());
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
            // We need to manually read from the stream in case there are any remaining bytes.
            IOUtils.copy(inputStream, new NullOutputStream());
            super.close();
        }
    }
}
