package com.apptive.joDuo.isthere;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zeroho on 2017. 5. 24..
 */

public class IsThereReview {
    private String reviewId;
    private String name;
    private String information;
    private String location;
    private String category;
    private String detailCategory;
    // comments
    private ArrayList<Comment> comments;

    public IsThereReview(String name, String information, String location, String category, String detailCategory) {
        this.name = name;
        this.information = information;
        this.location = location;
        this.category = category;
        this.detailCategory = detailCategory;
    }


    /*
        Set comments.
     */
    public void setComments(ArrayList<HashMap<IsThereHttpHelper.ReviewKey, String>> commentsArrayFromHelper) {
        if (comments == null) {
            comments = new ArrayList<>();
        }

        // Clear
        comments.clear();

        for (HashMap<IsThereHttpHelper.ReviewKey, String> commentHolder: commentsArrayFromHelper) {
            Comment newComment = new Comment(
                    commentHolder.get(IsThereHttpHelper.ReviewKey.COMMENT_ID),
                    commentHolder.get(IsThereHttpHelper.ReviewKey.REVIEW_ID),
                    commentHolder.get(IsThereHttpHelper.ReviewKey.USER_ID),
                    commentHolder.get(IsThereHttpHelper.ReviewKey.COMMENT),
                    commentHolder.get(IsThereHttpHelper.ReviewKey.LIKE_POINT),
                    commentHolder.get(IsThereHttpHelper.ReviewKey.DISLIKE_POINT),
                    commentHolder.get(IsThereHttpHelper.ReviewKey.DATE)
            );

            comments.add(newComment);
        }
    }


    // ----------------------------------------------- //
    /*
        Getter
     */
    public String getName() {
        return name;
    }
    public String getInformation() {
        return information;
    }
    public String getLocation() {
        return location;
    }
    public String getCategory() {
        return category;
    }
    public String getDetailCategory() {
        return detailCategory;
    }
    public String getReviewId() { return reviewId; }
    // ----------------------------------------------- //

    public static class Comment {
        private String id;
        private String reviewID;
        private String userID;
        private String comment;
        private String like_point = "0";
        private String dislike_point = "0";
        private String date;

        public Comment(String id, String reviewID, String userID, String comment, String like_point, String dislike_point, String date) {
            this.id = id;
            this.reviewID = reviewID;
            this.userID = userID;
            this.comment = comment;
            this.like_point = like_point;
            this.dislike_point = dislike_point;
            this.date = date;
        }


        // ----------------------------------------------- //
        // ----------------------------------------------- //

        // Getter
        // ----------------------------------------------- //
        public String getId() {
            return id;
        }
        public String getReviewID() {
            return reviewID;
        }
        public String getUserID() {
            return userID;
        }
        public String getComment() {
            return comment;
        }
        public String getLike_point() {
            return like_point;
        }
        public String getDislike_point() {
            return dislike_point;
        }
        public String getDate() {
            return date;
        }
        // ----------------------------------------------- //
    }
}

