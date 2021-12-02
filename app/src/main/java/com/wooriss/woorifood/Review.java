package com.wooriss.woorifood;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

//Each custom class must have a public constructor that takes no arguments. In addition, the class must include a public getter for each property.
public class Review {
    private String reviewerUid;
    private double taste;
    @ServerTimestamp private Timestamp timestamp; // server timestamp

    public Review() {}

    public Review(String reviewerUid, double taste) {
        this.reviewerUid = reviewerUid;
        this.taste = taste;
    }

    public double getTaste() {
        return taste;
    }

    public String getReviewerUid() {
        return reviewerUid;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

}
