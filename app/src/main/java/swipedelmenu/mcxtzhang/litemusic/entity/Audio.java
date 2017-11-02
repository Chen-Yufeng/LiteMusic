package swipedelmenu.mcxtzhang.litemusic.entity;

import java.io.Serializable;

public class Audio implements Serializable {

    private String title;
    private String data;


    public Audio(String title, String data) {
        this.title = title;
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
