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

/**
 * @since 1.0
 */
public class OneDriveAPIException extends IOException {
    
    private static final long serialVersionUID = 1L;

    private final int responseCode;
    
    private final String response;

    public OneDriveAPIException(String message) {
        super(message);

        this.responseCode = -1;
        this.response = null;
    }

    public OneDriveAPIException(String message, int responseCode, String response) {
        super(message);

        this.responseCode = responseCode;
        this.response = response;
    }

    public OneDriveAPIException(String message, Throwable cause) {
        super(message, cause);

        this.responseCode = -1;
        this.response = null;
    }

    public OneDriveAPIException(String message, int responseCode, String response, Throwable cause) {
        super(message, cause);

        this.responseCode = responseCode;
        this.response = response;
    }

    public int getResponseCode() {
        return this.responseCode;
    }

    public String getResponse() {
        return this.response;
    }

}
