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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

/**
 * @since 1.0
 */
public abstract class AbstractResponse<C> implements Closeable {

    private static final int BUFFER_SIZE = 8192;

    private final HttpURLConnection connection;

    private int responseCode;

    private String errorString;

    /** The regular InputStream is the right stream to read body with raw or gzip content. */
    private InputStream inputStream;

    private boolean closed;

    /**
     * @param connection a connection which has already sent a request to the API
     */
    public AbstractResponse(HttpURLConnection connection) throws OneDriveAPIException {
        this.connection = connection;

        try {
            responseCode = connection.getResponseCode();
        } catch (IOException e) {
            throw new OneDriveAPIException("Couldn't connect to the OneDrive API due to a network error.", e);
        }

        if (!isSuccess(responseCode)) {
            throw new OneDriveAPIException("The API returned an error code: " + responseCode, responseCode,
                    getErrorString());
        }
    }

    public int getResponseCode() {
        return responseCode;
    }

    public abstract C getContent() throws OneDriveAPIException;

    protected InputStream getBody() throws OneDriveAPIException {
        if (inputStream == null) {
            try {
                inputStream = handleGZIPStream(connection.getInputStream());
            } catch (IOException e) {
                throw new OneDriveAPIException("Couldn't connect to the OneDrive API due to a network error.", e);
            }
        }
        return new ResponseInputStream();
    }

    /**
     * Returns a string representation of error. Method returns the content of error stream.
     */
    protected String getErrorString() {
        if (errorString == null && !isSuccess(responseCode)) {
            errorString = readErrorStream();
        }
        return errorString;
    }

    private String readErrorStream() {
        try {
            return readStream(getErrorStream());
        } catch (OneDriveAPIException e) {
            return null;
        }
    }

    private InputStream getErrorStream() {
        InputStream errorStream = connection.getErrorStream();
        try {
            return handleGZIPStream(errorStream);
        } catch (IOException e) {
            return errorStream;
        }
    }

    /**
     * Returns a gzip input stream if the connection has gzip content encoding, else returns input stream.
     */
    private InputStream handleGZIPStream(InputStream stream) throws IOException {
        if (stream != null && "gzip".equalsIgnoreCase(connection.getContentEncoding())) {
            return new GZIPInputStream(stream);
        }
        return stream;
    }

    /**
     * Disconnects the response, close the input stream, so body can no longer be read after.
     */
    @Override
    public void close() throws OneDriveAPIException {
        if (closed) {
            return;
        }
        try {
            InputStream stream = connection.getInputStream();

            // We need to manually read from the connection's input stream in case there are any remaining bytes.
            // Else JVM won't return the connection to the pool
            byte[] buffer = new byte[BUFFER_SIZE];
            int n = stream.read(buffer);
            while (n != -1) {
                n = stream.read(buffer);
            }
            stream.close();

            if (inputStream != null) {
                inputStream.close();
            }

            closed = true;
        } catch (IOException e) {
            throw new OneDriveAPIException("Couldn't close the connection to OneDrive API due to a network error.", e);
        }
    }

    private static boolean isSuccess(int responseCode) {
        return responseCode >= 200 && responseCode < 300;
    }

    /**
     * Returns the input stream as a string, then close it.
     */
    protected static String readStream(InputStream stream) throws OneDriveAPIException {
        if (stream == null) {
            return null;
        }

        InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[BUFFER_SIZE];

        try {
            int read;
            while ((read = reader.read(buffer, 0, BUFFER_SIZE)) != -1) {
                builder.append(buffer, 0, read);
            }

            stream.close();
        } catch (IOException e) {
            throw new OneDriveAPIException("Couldn't read the stream from OneDrive API.", e);
        }
        return builder.toString();
    }

    private class ResponseInputStream extends InputStream {

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }

        @Override
        public void close() throws IOException {
            // Don't close the stream, it will be done by the response
            AbstractResponse.this.close();
        }
    }

}
