package pict.s2k.frameit.models;

/**
 * Created by suryaa on 16/9/17.
 */

public class NotificationModel {

    private String uidOfPoster ;
    private String pathForImage ;
    private String text;
    private String key ;

    public NotificationModel() {

    }

    public NotificationModel(String uidOfPoster, String pathForImage, String text, String key) {
        this.uidOfPoster = uidOfPoster;
        this.pathForImage = pathForImage;
        this.text = text;
        this.key = key;
    }

    public String getPathForImage() {
        return pathForImage;
    }

    public void setPathForImage(String pathForImage) {
        this.pathForImage = pathForImage;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUidOfPoster() {
        return uidOfPoster;
    }

    public void setUidOfPoster(String uidOfPoster) {
        this.uidOfPoster = uidOfPoster;
    }
}
