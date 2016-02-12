# Java Client Library for OneDrive & OneDrive for Business REST APIs

The OneDrive & OneDrive for Business Java Client is a Java client library for REST API. It is designed to work with both OneDrive & OneDrive for Business and to have light dependencies.

![Build Status](https://qa.nuxeo.org/jenkins/buildStatus/icon?job=onedrive-java-client-master)

## Building

`mvn clean install`

## Getting Started

### Maven

To import the client as maven dependency, declare it as follow for latest release :

```
<dependency>
  <groupId>org.nuxeo.onedrive</groupId>
  <artifactId>onedrive-java-client</artifactId>
  <version>1.0</version>
</dependency>
```

If you want to use the on development version, declare :

```
<dependency>
  <groupId>org.nuxeo.onedrive</groupId>
  <artifactId>onedrive-java-client</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

Artifact is available in nuxeo repositories :
- `http://maven.nuxeo.org/nexus/content/groups/public`
- `http://maven.nuxeo.org/nexus/content/groups/public-snapshot`

### OneDrive

To use the client with OneDrive you first need to create a `OneDriveBasicApi` to use it after with items of client :
 
```OneDriveAPI api = new OneDriveBasicAPI("YOUR_ACCESS_TOKEN");```
 
### OneDrive for Business

To use the client with OneDrive for Business you need to create a `OneDriveBusinessAPI` to use it after with items of client :

```OneDriveAPI api = new OneDriveBusinessAPI("YOUR_RESOURCE_URL", "YOUR_ACCESS_TOKEN");```
 
`YOUR_RESOURCE_URL` corresponds to your sharepoint resource url provided by microsoft, for example : `https://nuxeofr-my.sharepoint.com`.

### First calls

Now you have your `api` object you can request the APIs, for example to get the root folder run :

```OneDriveFolder root = OneDriveFolder.getRoot(api);```

Or just get a folder or file item :

```
OneDriveFolder folder = new OneDriveFolder(api, "FOLDER_ID");
OneDriveFile file = new OneDriveFile(api, "FILE_ID");
```

Then retrieve the metadata :

```
OneDriveFolder.Metadata folderMetadata = folder.getMetadata();
OneDriveFile.Metadata fileMetadata = item.getMetadata();
```

## Features

- Iterate over folder children, to get all temporary download urls for example :
```
public List<String> getChildrenDownloadUrls(OneDriveFolder folder) {
    List<String> urls = new ArrayList<>();
    for (OneDriveItem.Metadata metadata : folder) {
        if (metadata.isFile()) {
            urls.add(metadata.asFile().getDownloadUrl());
        } else if (metadata.isFolder()) {
            urls.addAll(getChildrenDownloadUrls(metadata.asFolder().getResource()));
        }
    }
    return urls;
}
```

- Download content of file :
```InputStream stream = file.download();```

- Get ThumbnailSet :
```
Iterable<OneDriveThumbnailSet.Metadata> folderThumbnail = folder.getThumbnailSets();
OneDriveThumbnailSet.Metadata fileThumbnail = file.getThumbnailSet();
String smallUrl = fileThumbnail.getSmall().getUrl(); // Get the small thumbnail url
InputStream smallStream = fileThumbnail.getSmall().getResource().download(); // Download the content of small thumbnail
```
You can also request thumbnail this way :
```
OneDriveThumbnail thumbnail = new OneDriveThumbnail(api, "FILE_ID", OneDriveThumbnailSize.SMALL);
String smallUrl = thumbnail.getMetadata().getUrl();
InputStream smallStream = thumbnail.download();
```
Or while getting metadata with `expand` parameter :
```
OneDriveFile.Metadata metadata = file.getMetadata(OneDriveExpand.THUMBNAILS);
...
OneDriveThumbnailSet.Metadata fileThumbnail = metadata.getThumbnailSet();
```
- Get email of current user :
```String email = OneDriveEmailAccount.getCurrentUserEmailAccount(api);```

## Limitations

Currently the OneDrive Java Client doesn't provide OAuth support to obtain or refresh an access token. You might obtain one before using the client.

# Licensing
 
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
 
# About Nuxeo
 
Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with
SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Netflix, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris.
More information is available at [www.nuxeo.com](http://www.nuxeo.com).
