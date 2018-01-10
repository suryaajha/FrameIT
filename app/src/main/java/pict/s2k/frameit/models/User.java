package pict.s2k.frameit.models;

/**
 * Created by suryaa on 3/9/17.
 */


//User Model Class
public class User {
    private String display_name;
    private long mobile_no;
    private String profile_pic_url;
    private float rating;
    private long followers;
    private long posts;
    String email;
    boolean number_verified;

    public float getRating() {
        return rating;
    }

    public User() {
    }

    public User(String display_name,long mobile_no,String email) {
        this.display_name=display_name;
        this.mobile_no = mobile_no;
        this.profile_pic_url ="";
        this.email=email;
        followers=0;
        rating=0.0f;
        posts=0;
        number_verified=false;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public long getMobile_no() {
        return mobile_no;
    }

    public String getProfile_pic_url() {
        return profile_pic_url;
    }

    public long getFollowers() {
        return followers;
    }

    public long getPosts() {
        return posts;
    }

    public String getEmail() {
        return email;
    }

    public void setNumber_verified(boolean number_verified) {
        this.number_verified = number_verified;
    }
}
