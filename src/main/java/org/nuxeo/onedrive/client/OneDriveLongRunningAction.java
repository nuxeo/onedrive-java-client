package org.nuxeo.onedrive.client;

import com.eclipsesource.json.JsonObject;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

public class OneDriveLongRunningAction {
    private final URL monitorUrl;
    private final OneDriveAPI oneDriveAPI;

    public OneDriveLongRunningAction(final URL monitorUrl, final OneDriveAPI oneDriveAPI) {
        this.monitorUrl = monitorUrl;
        this.oneDriveAPI = oneDriveAPI;
    }

    public void await(ProgressCallback progressCallback) throws IOException {
        // Minimum waiting time between requests: 256ms (1/4 s)

        boolean finished = false;
        int timeout = 8;
        while (!finished) {
            try {
                Thread.sleep(1 << timeout);
            } catch (InterruptedException e) {
                return;
            }

            final StatusObject statusObject = getStatus();
            if (statusObject.status == Status.completed) {
                finished = true;
            }

            if (null != progressCallback) {
                progressCallback.post(statusObject);
            }

            // Max waiting time: 8 seconds between requests
            timeout = Math.min(timeout + 1, 13);
        }
    }

    public StatusObject getStatus() throws IOException {
        final OneDriveJsonResponse jsonRequest = new OneDriveJsonRequest(monitorUrl, "GET") {
            @Override
            protected void addAuthorizationHeader(RequestExecutor executor, Set<RequestHeader> headers) {
                // Monitor URL is preauthenticated and must not have an authorization header
            }
        }.sendRequest(oneDriveAPI.getExecutor());
        return new StatusObject(jsonRequest.getContent());
    }

    public enum Status {
        notStarted,
        inProgress,
        completed,
        updating,
        failed,
        deletePending,
        deleteFailed,
        waiting
    }

    public class StatusObject extends OneDriveJsonObject {
        private String operation;
        private float percentage;
        private Status status;

        public StatusObject(JsonObject json) {
            super(json);
        }

        @Override
        protected void parseMember(JsonObject.Member member) {
            if ("operation".equals(member.getName())) {
                operation = member.getValue().asString();
            } else if ("percentageComplete".equals(member.getName())) {
                percentage = member.getValue().asFloat();
            } else if ("status".equals(member.getName())) {
                status = Status.valueOf(member.getValue().asString());
            }
            super.parseMember(member);
        }

        public Status getStatus() {
            return status;
        }

        public float getPercentage() {
            return percentage;
        }

        public String getOperation() {
            return operation;
        }
    }

    public interface ProgressCallback {
        void post(StatusObject status);
    }
}
