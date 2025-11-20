package com.irisid.user.it100_sample.Settings;

import android.net.Uri;

public class MediaFileInfo
{
    private String fileName;
    private Uri fileUri;
    private Uri thumImageFileUri;

    public Uri getThumImageFileUri() {
        return thumImageFileUri;
    }

    public void setThumImageFileUri(Uri thumImageFileUri) {
        this.thumImageFileUri = thumImageFileUri;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public Uri getFileUri()
    {
        return fileUri;
    }

    public void setFileUri(Uri fileUri)
    {
        this.fileUri = fileUri;
    }
}
