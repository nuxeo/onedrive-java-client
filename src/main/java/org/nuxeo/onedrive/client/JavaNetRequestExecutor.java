package org.nuxeo.onedrive.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;
import java.util.zip.GZIPInputStream;

public class JavaNetRequestExecutor implements RequestExecutor {

    private static final String DEFAULT_USER_AGENT = "Nuxeo OneDrive Java SDK v1.0";

    private final String accessToken;
    private int timeout;
    private String userAgent = DEFAULT_USER_AGENT;

    public JavaNetRequestExecutor(final String accessToken) {
        this.accessToken = accessToken;
    }

    public JavaNetRequestExecutor withTimeout(final int timeout) {
        this.timeout = timeout;
        return this;
    }

    public JavaNetRequestExecutor withUserAgent(final String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    @Override
    public Upload doPost(final URL url, final Set<RequestHeader> headers) throws IOException {
        HttpURLConnection connection = this.createConnection(url, "POST", headers);
        this.authorize(connection);
        connection.setDoOutput(true);
        connection.connect();
        return new Upload() {
            @Override
            public OutputStream getOutputStream() throws IOException {
                return connection.getOutputStream();
            }

            @Override
            public Response getResponse() throws IOException {
                return toResponse(connection);
            }
        };
    }

    @Override
    public Upload doPut(URL url, Set<RequestHeader> headers) throws IOException {
        HttpURLConnection connection = this.createConnection(url, "PUT", headers);
        this.authorize(connection);
        connection.setDoOutput(true);
        connection.connect();
        return new Upload() {
            @Override
            public OutputStream getOutputStream() throws IOException {
                return connection.getOutputStream();
            }

            @Override
            public Response getResponse() throws IOException {
                return toResponse(connection);
            }
        };
    }

    @Override
    public Response doGet(final URL url, final Set<RequestHeader> headers) throws IOException {
        final HttpURLConnection connection = this.createConnection(url, "GET", headers);
        this.authorize(connection);
        connection.connect();
        return this.toResponse(connection);
    }

    @Override
    public Response doDelete(URL url, Set<RequestHeader> headers) throws IOException {
        final HttpURLConnection connection = this.createConnection(url, "DELETE", headers);
        this.authorize(connection);
        connection.connect();
        return this.toResponse(connection);
    }

    @Override
    public Upload doPatch(URL url, Set<RequestHeader> headers) throws IOException {
        HttpURLConnection connection = this.createConnection(url, "PATCH", headers);
        this.authorize(connection);
        connection.setDoOutput(true);
        connection.connect();
        return new Upload() {
            @Override
            public OutputStream getOutputStream() throws IOException {
                return connection.getOutputStream();
            }

            @Override
            public Response getResponse() throws IOException {
                return toResponse(connection);
            }
        };
    }

    protected void authorize(final HttpURLConnection connection) {
        connection.setRequestProperty("User-Agent", userAgent);
        connection.addRequestProperty("Authorization", String.format("Bearer %s", accessToken));
    }

    protected Response toResponse(final HttpURLConnection connection) throws IOException {
        final InputStream stream;
        int responseCode = connection.getResponseCode();
        if(responseCode >= 400 || responseCode == -1) {
            stream = connection.getErrorStream();
        }
        else {
            stream = connection.getInputStream();
        }
        // Returns a gzip input stream if the connection has gzip content encoding, else returns input stream.
        return new JavaNetResponse(connection, stream);
    }

    protected HttpURLConnection createConnection(URL url, String method, final Set<RequestHeader> headers) throws IOException {
        final HttpURLConnection connection;
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        if(timeout > 0) {
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
        }
        connection.setInstanceFollowRedirects(true);
        headers.forEach(header -> connection.addRequestProperty(header.getKey(), header.getValue()));
        return connection;
    }

    @Override
    public void close() throws IOException {
        //
    }

    private final class JavaNetResponse extends Response {

        private HttpURLConnection connection;

        public JavaNetResponse(final HttpURLConnection connection, final InputStream stream) throws IOException {
            super("gzip".equalsIgnoreCase(connection.getContentEncoding()) ? new GZIPInputStream(stream) : stream
            );
            this.connection = connection;
        }

        @Override
        public int getStatusCode() throws IOException {
            return connection.getResponseCode();
        }

        @Override
        public String getStatusMessage() throws IOException {
            return connection.getResponseMessage();
        }
    }
}
