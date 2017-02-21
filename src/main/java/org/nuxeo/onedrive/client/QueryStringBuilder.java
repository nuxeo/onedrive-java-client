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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @since 1.0
 */
class QueryStringBuilder {

    private Map<String, String> parameters = new HashMap<>();

    public QueryStringBuilder set(String key, int value) {
        return set(key, Integer.toString(value));
    }

    public QueryStringBuilder set(String key, long value) {
        return set(key, Long.toString(value));
    }

    public QueryStringBuilder set(String key, boolean value) {
        return set(key, Boolean.toString(value));
    }

    public QueryStringBuilder set(String key, String value) {
        parameters.put(key, value);
        return this;
    }

    public QueryStringBuilder set(String key, QueryStringCommaParameter... parameters) {
        if(parameters != null && parameters.length > 0) {
            StringBuilder builder = new StringBuilder();
            for(QueryStringCommaParameter parameter : parameters) {
                if(builder.length() > 0) {
                    builder.append(",");
                }
                builder.append(parameter.getKey());
            }
            this.parameters.put(key, builder.toString());
        }
        return this;
    }

    @Override
    public String toString() {
        if(parameters.isEmpty()) {
            return "";
        }
        try {
            StringBuilder builder = new StringBuilder("?");
            for(Entry<String, String> entry : parameters.entrySet()) {
                if(builder.length() != 1) {
                    builder.append("&");
                }
                builder.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.name()));
            }
            return builder.toString();
        }
        catch(UnsupportedEncodingException e) {
            throw new OneDriveRuntimeException(new OneDriveAPIException(e.getMessage(), e));
        }
    }

}
