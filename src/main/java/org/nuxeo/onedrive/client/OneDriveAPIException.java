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

import com.eclipsesource.json.JsonObject;

import java.io.IOException;

/**
 * @since 1.0
 */
public class OneDriveAPIException extends IOException {

    private static final long serialVersionUID = 1L;

    private final int responseCode;
    private final String errorMessage;
    /**
     * Retry-After header value (in seconds)
     */
    private final Integer retry;

    public OneDriveAPIException(String message) {
        super(message);
        this.responseCode = -1;
        this.errorMessage = null;
        this.retry = null;
    }

    public OneDriveAPIException(String message, Throwable cause) {
        super(message, cause);
        this.responseCode = -1;
        this.errorMessage = null;
        this.retry = null;
    }

    public OneDriveAPIException(String responseMessage, int responseCode) {
        super(responseMessage);
        this.responseCode = responseCode;
        this.errorMessage = null;
        this.retry = null;
    }

    public OneDriveAPIException(OneDriveRuntimeException cause) {
        super(cause);
        this.responseCode = cause.getCause().getResponseCode();
        this.errorMessage = cause.getCause().getErrorMessage();
        this.retry = null;
    }

    public OneDriveAPIException(final String responseMessage, final int responseCode, final JsonObject error) {
        this(responseMessage, responseCode, error, null);
    }

    public OneDriveAPIException(final String responseMessage, final int responseCode, final JsonObject error, final Integer retry) {
        super(responseMessage);
        this.responseCode = responseCode;
        if(error.get("error").isObject()) {
            this.errorMessage = error.get("error").asObject().get("message").asString();
        }
        else {
            this.errorMessage = error.toString();
        }
        this.retry = retry;
    }

    public int getResponseCode() {
        return this.responseCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Retry-After header value (in seconds)
     *
     * @return Value in seconds or null if not Retry-After header in response
     */
    public Integer getRetry() {
        return retry;
    }
}
