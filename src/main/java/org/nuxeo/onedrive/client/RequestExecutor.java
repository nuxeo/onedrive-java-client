package org.nuxeo.onedrive.client;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Set;

public interface RequestExecutor extends Closeable {
    Upload doPost(URL url, Set<RequestHeader> headers) throws IOException;

    Upload doPut(URL url, Set<RequestHeader> headers) throws IOException;

    Response doGet(URL url, Set<RequestHeader> headers) throws IOException;

    Response doDelete(URL url, Set<RequestHeader> headers) throws IOException;

    Upload doPatch(URL url, Set<RequestHeader> headers) throws IOException;

    abstract class Response {
        private final InputStream responseBody;

        public Response(final InputStream responseBody) {
            this.responseBody = responseBody;
        }

        public abstract int getStatusCode() throws IOException;

        public abstract String getStatusMessage() throws IOException;

        public InputStream getInputStream() {
            return responseBody;
        }
    }

    abstract class Upload {
        public abstract OutputStream getOutputStream() throws IOException;

        public abstract Response getResponse() throws IOException;
    }
}
