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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

/**
 * @since 1.0
 */
class URLTemplate {

    public static final URLTemplate EMPTY_TEMPLATE = new URLTemplate("");

    private String template;

    URLTemplate(String template) {
        this.template = Objects.requireNonNull(template);
    }

    URL build(String base, Object... values) {
        String urlString = String.format(base + this.template, values);
        try {
            return new URL(urlString);
        } catch (MalformedURLException e) {
            throw new OneDriveRuntimeException("Template produced an invalid URL (maybe a bug in client).", e);
        }
    }

    URL build(String base, QueryStringBuilder query, Object... values) {
        String urlString = String.format(base + this.template, values) + query.toString();
        try {
            return new URL(urlString);
        } catch (MalformedURLException e) {
            throw new OneDriveRuntimeException("Template produced an invalid URL (maybe a bug in client).", e);
        }
    }

}
