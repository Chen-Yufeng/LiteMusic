package swipedelmenu.mcxtzhang.litemusic.entity;

import android.net.Uri;

import java.io.OutputStream;
import java.io.Serializable;

public class Audio implements Serializable {

    private String title;
    private Uri uri;

    public Audio(String title, Uri uri) {
        this.title = title;
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
