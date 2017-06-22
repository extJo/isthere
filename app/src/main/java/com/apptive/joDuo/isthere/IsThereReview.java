package com.apptive.joDuo.isthere;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zeroho on 2017. 5. 15..
 */

public class IsThereReview {
    private String reviewId;
    private String name;
    private String information;
    private String location;
    private double coordX;
    private double coordY;
    private String category;
    private String detailCategory;
    // comments
    private ArrayList<Comment> comments;

    public IsThereReview(String reviewId, String name, String information, String location, String coordX, String coordY, String category, String detailCategory) {
        this.reviewId = reviewId;
        this.name = name;
        this.information = information;
        this.location = location;
        this.coordX = Double.parseDouble(coordX);
        this.coordY = Double.parseDouble(coordY);
        this.category = category;
        this.detailCategory = detailCategory;
    }

    /*
        Print Values using System.out.println() for debugging
     */
    public void printValues() {
        System.out.println("id: " + reviewId +
                "\nname: " + name +
                "\ninformation: " + information +
                "\nlocation: " + location +
                "\ncoordination_x: " + coordX +
                "\ncoordination_y: " + coordY +
                "\ncategory: " + category +
                "\ndetailCategory: " + detailCategory
        );

        System.out.println("comments: ");

        if (comments != null) {
            for (Comment value : comments) {
                System.out.println("commentId: " + value.id +
                        " reviewID: " + value.reviewID +
                        " userID: " + value.userID +
                        " comment: " + value.comment +
                        " like_point: " + value.like_point +
                        " dislike_point: " + value.dislike_point +
                        " date: " + value.date
                );
            }
        }


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

        for (HashMap<IsThereHttpHelper.ReviewKey, String> commentHolder : commentsArrayFromHelper) {
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

    public double getCoordX() {
        return coordX;
    }

    public double getCoordY() {
        return coordY;
    }

    public String getCategory() {
        return category;
    }

    public String getDetailCategory() {
        return detailCategory;
    }

    public String getReviewId() {
        return reviewId;
    }

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
