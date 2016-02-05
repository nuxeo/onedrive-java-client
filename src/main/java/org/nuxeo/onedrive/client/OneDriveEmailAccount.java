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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.eclipsesource.json.JsonObject;

/**
 * @since 1.0
 */
public class OneDriveEmailAccount {

    public static String getCurrentUserEmailAccount(OneDriveAPI api) throws OneDriveAPIException {
        URL url = URLTemplate.EMPTY_TEMPLATE.build(api.getEmailURL());
        if (api.isBusinessConnection()) {
            OneDriveBusinessEmailRequest request = new OneDriveBusinessEmailRequest(api, url);
            OneDriveBusinessEmailResponse response = request.send();
            return response.getContent();
        }
        OneDriveJsonRequest request = new OneDriveJsonRequest(api, url, "GET");
        OneDriveJsonResponse response = request.send();
        JsonObject jsonObject = response.getContent();
        return jsonObject.get("emails").asObject().get("account").asString();
    }

    public static class OneDriveBusinessEmailRequest extends AbstractRequest<OneDriveBusinessEmailResponse> {

        public OneDriveBusinessEmailRequest(OneDriveAPI api, URL url) {
            super(api, url, "GET");
        }

        @Override
        protected OneDriveBusinessEmailResponse createResponse(HttpURLConnection connection)
                throws OneDriveAPIException {
            return new OneDriveBusinessEmailResponse(connection);
        }

    }

    public static class OneDriveBusinessEmailResponse extends AbstractResponse<String> {

        public OneDriveBusinessEmailResponse(HttpURLConnection connection) throws OneDriveAPIException {
            super(connection);
        }

        @Override
        public String getContent() throws OneDriveAPIException {
            try (InputStream body = getBody()) {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                documentBuilderFactory.setNamespaceAware(true);
                Document parse = documentBuilderFactory.newDocumentBuilder().parse(body);
                parse.getDocumentElement().normalize();
                NodeList emailTags = parse.getDocumentElement().getElementsByTagNameNS("*", "Email");
                if (emailTags.getLength() > 0 && !"".equals(emailTags.item(0).getTextContent())) {
                    return emailTags.item(0).getTextContent();
                }
                NodeList profileProperties = parse.getDocumentElement().getElementsByTagNameNS("*", "element");
                for (int i = 0; i < profileProperties.getLength(); i++) {
                    Node item = profileProperties.item(i);
                    if (Node.ELEMENT_NODE == item.getNodeType()) {
                        Element elem = (Element) item;
                        NodeList keys = elem.getElementsByTagNameNS("*", "Key");
                        NodeList values = elem.getElementsByTagNameNS("*", "Value");
                        if (keys.getLength() > 0 && "UserName".equalsIgnoreCase(keys.item(0).getTextContent())) {
                            return values.item(0).getTextContent();
                        }
                    }
                }
                return null;
            } catch (Exception e) {
                throw new OneDriveAPIException("Could not read the business get email response.", e);
            }
        }
    }

}
