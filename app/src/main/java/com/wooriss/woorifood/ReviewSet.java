package com.wooriss.woorifood;

import android.net.Uri;

import java.util.List;

public class ReviewSet {
    public Review review;
    public User user;
    public List<Uri> images;

    ReviewSet() {
    }

    public ReviewSet(Review review, User user, List<Uri> images) {
        this.review = review;
        this.user = user;
        this.images = images;
    }

    public List<Uri> getImages() {
        return images;
    }

    public Review getReview() {
        return review;
    }

    public User getUser() {
        return user;
    }
}