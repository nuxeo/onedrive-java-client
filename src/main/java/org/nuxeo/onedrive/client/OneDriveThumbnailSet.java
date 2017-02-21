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

import java.util.Objects;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.ParseException;

/**
 * See documentation at https://dev.onedrive.com/resources/thumbnailSet.htm.
 *
 * @since 1.0
 */
public class OneDriveThumbnailSet extends OneDriveResource {

    private final String itemId;

    private final int thumbId;

    OneDriveThumbnailSet(OneDriveAPI api, int thumbId) {
        super(api, "root$$" + thumbId);
        this.itemId = null;
        this.thumbId = thumbId;
    }

    OneDriveThumbnailSet(OneDriveAPI api, String itemId, int thumbId) {
        super(api, itemId + "$$" + thumbId);
        this.itemId = Objects.requireNonNull(itemId);
        this.thumbId = thumbId;
    }

    @Override
    public boolean isRoot() {
        return itemId == null;
    }

    public class Metadata extends OneDriveResource.Metadata {

        /**
         * A 48x48 cropped thumbnail.
         */
        private OneDriveThumbnail.Metadata small;

        /**
         * A 176x176 scaled thumbnail.
         */
        private OneDriveThumbnail.Metadata medium;

        /**
         * A 1920x1920 scaled thumbnail.
         */
        private OneDriveThumbnail.Metadata large;

        /**
         * A custom thumbnail image or the original image used to generate other thumbnails.
         */
        private OneDriveThumbnail.Metadata source;

        public Metadata(JsonObject json) {
            super(json);
        }

        public String getItemId() {
            return itemId;
        }

        public int getThumbId() {
            return thumbId;
        }

        public OneDriveThumbnail.Metadata getSmall() {
            return small;
        }

        public OneDriveThumbnail.Metadata getMedium() {
            return medium;
        }

        public OneDriveThumbnail.Metadata getLarge() {
            return large;
        }

        public OneDriveThumbnail.Metadata getSource() {
            return source;
        }

        @Override
        protected void parseMember(JsonObject.Member member) {
            super.parseMember(member);
            try {
                JsonValue value = member.getValue();
                String memberName = member.getName();
                if("small".equals(memberName)) {
                    OneDriveThumbnail thumbnail = initThumbnail(OneDriveThumbnailSize.SMALL);
                    small = thumbnail.new Metadata(value.asObject());
                }
                else if("medium".equals(memberName)) {
                    OneDriveThumbnail thumbnail = initThumbnail(OneDriveThumbnailSize.MEDIUM);
                    medium = thumbnail.new Metadata(value.asObject());
                }
                else if("large".equals(memberName)) {
                    OneDriveThumbnail thumbnail = initThumbnail(OneDriveThumbnailSize.LARGE);
                    large = thumbnail.new Metadata(value.asObject());
                }
                else if("source".equals(memberName)) {
                    OneDriveThumbnail thumbnail = initThumbnail(OneDriveThumbnailSize.SOURCE);
                    source = thumbnail.new Metadata(value.asObject());
                }
            }
            catch(ParseException e) {
                throw new OneDriveRuntimeException(new OneDriveAPIException(e.getMessage(), e));
            }
        }

        private OneDriveThumbnail initThumbnail(OneDriveThumbnailSize size) {
            if(itemId == null) {
                return new OneDriveThumbnail(getApi(), thumbId, size);
            }
            return new OneDriveThumbnail(getApi(), itemId, thumbId, size);
        }

        @Override
        public OneDriveResource getResource() {
            return OneDriveThumbnailSet.this;
        }

    }

}
