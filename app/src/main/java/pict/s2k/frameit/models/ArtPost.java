package pict.s2k.frameit.models;

/**
 * Created by kshitij on 5/9/17.
 */

public class ArtPost {

    private String description;
    private String post_image;
    private long sent_time;
    private double price;
    private String uid;
    private float height;
    private float width;

    public ArtPost() {
    }

    public ArtPost(String description, String post_image, long sent_time, double price, String uid, float height, float width) {
        this.description = description;
        this.post_image = post_image;
        this.sent_time = sent_time;
        this.price = price;
        this.uid = uid;
        this.height = height;
        this.width = width;
    }
    public ArtPost(String post_image){
        this.post_image=post_image;
    }

    public String getDescription() {
        return description;
    }

    public String getPost_image() {
        return post_image;
    }

    public long getSent_time() {
        return sent_time;
    }

    public double getPrice() {
        return price;
    }

    public String getUid() {
        return uid;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }
}
