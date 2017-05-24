package com.apptive.joDuo.isthere;

import android.util.JsonReader;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zeroho on 2017. 5. 24..
 */

/*
    getReviews
    postReview
    getReviewComments
    postReviewComment
    deleteReviewComment
    updateReviewCommentLikePoint
    updateReviewCommentDislikePoint
    postLikeReview
    deleteLikeReview
 */
public class IsThereHttpHelper {
    private String basicURL = "http://52.78.33.177:9000";
    private String gettingReviewsURL = "/reviews/";
    private String postingReviewURL = "/reviews/review";
    private String postingFavoritReviewURL = "/review/like";
    private String deletingForvirtReviewURL = "/review/dislike";
    private String gettingReviewComment = "/comments/";
    private String postingCommentURL = "/comment";
    private String deletingCommentURL = "/comment";
    private String updatingLikePointURL = "/comment/like-point/";
    private String updatingDislikePointURL = "/comment/dislike-point/";


    public enum ReviewKey {
        NAME, INFORMATION, LOCATION, LOCATION_CORD_X, LOCATION_CORD_Y, CATEGORY, DETAIL_CATEGORY, REVIEW_ID, USER_ID,
        COMMENT_ID, COMMENT, LIKE_POINT, DISLIKE_POINT, DATE
    }

    ;

    /*
        Get reviews depending on location and category.
     */
    public ArrayList<HashMap<ReviewKey, String>> getReviews(String category, String detailCategory, String loc) throws IOException {

        String realURL = basicURL + gettingReviewsURL + "/" + category + "?detail=" + detailCategory + "&loc=" + loc;
        ArrayList<HashMap<ReviewKey, String>> reviewsArray = null;

        URL gettingReviewsURL = new URL(realURL);

        HttpURLConnection gettingReviewsConnection = (HttpURLConnection) gettingReviewsURL.openConnection();
        gettingReviewsConnection.setRequestMethod("GET");


        // if connection is succeed.
        if (gettingReviewsConnection.getResponseCode() == 200) {
            // Allocation memory for array having review values.
            reviewsArray = new ArrayList<>();

            InputStream responseBody = gettingReviewsConnection.getInputStream();

            InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");

            JsonReader jsonReader = new JsonReader(responseBodyReader);

            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                    /*
                        JSON Order
                        id
                        location
                        location x
                        location y
                        name
                        information
                        category_1
                        category_2
                        is_deleted
                     */

                HashMap<ReviewKey, String> review = new HashMap<>();

                jsonReader.beginObject();

                jsonReader.skipValue(); // id key
                review.put(ReviewKey.REVIEW_ID, jsonReader.nextString()); // id value
                jsonReader.skipValue(); // location key
                review.put(ReviewKey.LOCATION, jsonReader.nextString()); // location value
                jsonReader.skipValue(); // location x key
                review.put(ReviewKey.LOCATION_CORD_X, jsonReader.nextString()); // location x value
                jsonReader.skipValue(); // location y key
                review.put(ReviewKey.LOCATION_CORD_Y, jsonReader.nextString()); // location y value
                jsonReader.skipValue(); // name key
                review.put(ReviewKey.NAME, jsonReader.nextString()); // name value
                jsonReader.skipValue(); // information key
                review.put(ReviewKey.INFORMATION, jsonReader.nextString()); // information value
                jsonReader.skipValue(); // category key
                review.put(ReviewKey.CATEGORY, jsonReader.nextString()); // category value
                jsonReader.skipValue(); // detail category key
                review.put(ReviewKey.DETAIL_CATEGORY, jsonReader.nextString()); // detail category value
                jsonReader.skipValue(); // is deleted key
                jsonReader.skipValue(); // is deleted value

                jsonReader.endObject();

                reviewsArray.add(review); // Add a review to review array
            }

            // Close JSON
            jsonReader.close();

        } else {
            // connection failed.


        }

        // Disconnect connection.
        gettingReviewsConnection.disconnect();

        // parsing json and make into dictionary format

        // return the dictionary format
        // this return value will be processed in IsThereReview Class with static method.
        return reviewsArray;
    }

    /*
        Post a review
     */
    public boolean postReview(String location, double[] locationPoint, String name, String information, String category, String detailCategory) throws IOException {
        boolean postResult = true;

        // Real URL
        String realURLStr = basicURL + postingReviewURL;

        URL postingReviewURL = new URL(realURLStr);

        HttpURLConnection postingReviewConnection = (HttpURLConnection) postingReviewURL.openConnection();
        postingReviewConnection.setRequestMethod("POST");
        postingReviewConnection.setRequestProperty("Content-Type", "application/json");
        postingReviewConnection.setDoInput(true);

            /*
                location
                location_x
                location_y
                name
                information
                category_1
                category_2
             */

        // Make json
        JSONObject newReview = new JSONObject();


        try {
            newReview.put("location", location);
            newReview.put("location_x", locationPoint[0]);
            newReview.put("location_Y", locationPoint[1]);
            newReview.put("name", name);
            newReview.put("information", information);
            newReview.put("category_1", category);
            newReview.put("category_2", detailCategory);
        } catch (JSONException e) {
            Log.d("MakingJSON", "Failure: in postReview()");
            e.printStackTrace();
        }

        // write on connection
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(postingReviewConnection.getOutputStream());
        outputStreamWriter.write(newReview.toString());
        outputStreamWriter.flush();

        if (postingReviewConnection.getResponseCode() == 200) {
            // success
            postResult = true;
        } else {
            // fail
            postResult = false;
        }


        // return success or fail boolean
        return postResult;
    }

    /*
        Get comments of a review depend on reviewID.
     */
    public ArrayList<HashMap<ReviewKey, String>> getReviewComments(String reviewID) throws IOException {
        String realURLStr = basicURL + gettingReviewComment + reviewID;
        ArrayList<HashMap<ReviewKey, String>> commentsArray = null;

        URL gettingCommentsURL = new URL(realURLStr);

        HttpURLConnection gettingReviewsConnection = (HttpURLConnection) gettingCommentsURL.openConnection();
        gettingReviewsConnection.setRequestMethod("GET");


        // if connection is succeed.
        if (gettingReviewsConnection.getResponseCode() == 200) {
            // Allocation memory for array having comment values.
            commentsArray = new ArrayList<>();

            InputStream responseBody = gettingReviewsConnection.getInputStream();

            InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");

            JsonReader jsonReader = new JsonReader(responseBodyReader);

            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                    /*
                        JSON Order
                        id
                        review_id
                        user_id
                        comment
                        like_point
                        dislike_point
                        date
                        is_deleted
                     */

                HashMap<ReviewKey, String> comment = new HashMap<>();

                jsonReader.beginObject();

                jsonReader.skipValue(); // id key
                comment.put(ReviewKey.COMMENT_ID, jsonReader.nextString()); // id value
                jsonReader.skipValue(); // review_id key
                comment.put(ReviewKey.REVIEW_ID, jsonReader.nextString()); // location value
                jsonReader.skipValue(); // user_id key
                comment.put(ReviewKey.USER_ID, jsonReader.nextString()); // location x value
                jsonReader.skipValue(); // comment key
                comment.put(ReviewKey.COMMENT, jsonReader.nextString()); // location y value
                jsonReader.skipValue(); // like_point key
                comment.put(ReviewKey.LIKE_POINT, jsonReader.nextString()); // name value
                jsonReader.skipValue(); // dislike_point key
                comment.put(ReviewKey.DISLIKE_POINT, jsonReader.nextString()); // information value
                jsonReader.skipValue(); // date key
                comment.put(ReviewKey.DATE, jsonReader.nextString()); // category value
                jsonReader.skipValue(); // is deleted key
                jsonReader.skipValue(); // is deleted value

                jsonReader.endObject();

                commentsArray.add(comment); // Add a review to review array
            }

            // Close JSON
            jsonReader.close();

        } else {
            // connection failed.


        }

        // Disconnect connection.
        gettingReviewsConnection.disconnect();

        // return the dictionary format
        // this return value will be processed in IsThereReview Class with static method.
        // return arraySet of comments
        return commentsArray;
    }

    /*
        Delete a comment of a review depend on reviewID and userID.
        Primary key is the combination of reviewID and userID.
     */
    public void deleteReviewComment(String reviewID, String userID) {


        // return boolean value
    }

    /*
        Add a like point for a review comment.
     */
    public void updateReviewCommentLikePoint(String reviewID) {

        // return boolean value
    }

    /*
        Add a disliek point for a review comment.
     */
    public void updateReviewCommentDisliekPoint(String reviewID) {

    }

    /*
        Post this review to a user's favorite review.
     */
    public void postLikeReview(String userID, String reviewID) {

    }

    /*
        Delete this review fro a user's favorite reviews.
     */
    public void deleteLikeReview(String userId, String reviewID) {

    }


    public boolean updateComments(IsThereReview review) {
        boolean updateResult = true;

        try {
            review.setComments(getReviewComments(review.getReviewId()));
        } catch (Exception e) {
            e.printStackTrace();
            updateResult = false;
        }

        return updateResult;
    }

}