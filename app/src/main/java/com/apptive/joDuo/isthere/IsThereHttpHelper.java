package com.apptive.joDuo.isthere;

import android.util.JsonReader;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

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

    postLogin
    postCreateNewAccount
 */
public class IsThereHttpHelper {
    private String basicURLStr = "http://52.78.33.177:9000";
    private String gettingReviewsURLStr = "/reviews/";
    private String postingReviewURLStr = "/reviews/review";
    private String postingFavoritReviewURLStr = "/review/like";
    private String deletingForvirtReviewURLStr = "/review/dislike";
    private String gettingReviewCommentURLStr = "/comments/";
    private String postingCommentURLStr = "/comment";
    private String deletingCommentURLStr = "/comment";
    private String updatingLikePointURLStr = "/comment/like-point/";
    private String updatingDislikePointURLStr = "/comment/dislike-point/";
    private String postingLoginURLStr = "/users/user/login";
    private String postingNewAccountURLStr = "/users/user/create";


    /// Authorization ///
    private String idToken = null;


    public enum ReviewKey {
        NAME, INFORMATION, LOCATION, LOCATION_CORD_X, LOCATION_CORD_Y, CATEGORY, DETAIL_CATEGORY, REVIEW_ID, USER_ID,
        COMMENT_ID, COMMENT, LIKE_POINT, DISLIKE_POINT, DATE
    }

    ;

    /*
        Get reviews depending on location and category.
     */
    public ArrayList<HashMap<ReviewKey, String>> getReviews(String category, String detailCategory, String loc) throws IOException {

        String realURL = basicURLStr + gettingReviewsURLStr + "/" + URLEncoder.encode(category, "UTF-8") + "?detail=" + detailCategory + "&loc=" + loc;

        ArrayList<HashMap<ReviewKey, String>> reviewsArray = null;

        URL gettingReviewsURL = new URL(realURL);

        HttpURLConnection gettingReviewsConnection = (HttpURLConnection) gettingReviewsURL.openConnection();
        gettingReviewsConnection.setRequestMethod("GET");


        // if connection is succeed.
        if (gettingReviewsConnection.getResponseCode() == 200) {
            // Allocation memory for array having re
            // view values.
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
            Log.d("IsThereHttpHelper", "getReviews(): Connection Failed");

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
        String realURLStr = basicURLStr + postingReviewURLStr;

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
            newReview.put("location_y", locationPoint[1]);
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

    public boolean postReviewComment(String reviewID, String userId, String comment) throws IOException {

        boolean postResult = true;

        String realURLStr = basicURLStr + postingCommentURLStr;

        URL postingCommentURL = new URL(realURLStr);

        HttpURLConnection postingCommentConnection = (HttpURLConnection) postingCommentURL.openConnection();
        postingCommentConnection.setDoInput(true);
        postingCommentConnection.setRequestProperty("Content-Type", "application/json");

        JSONObject newComment = new JSONObject();

        // Implement !!!!!!!!


        return postResult;

    }

    /*
        Get comments of a review depend on reviewID.
     */
    public ArrayList<HashMap<ReviewKey, String>> getReviewComments(String reviewID) throws IOException {
        String realURLStr = basicURLStr + gettingReviewCommentURLStr + reviewID;
        ArrayList<HashMap<ReviewKey, String>> commentsArray = null;

        URL gettingCommentsURL = new URL(realURLStr);

        HttpURLConnection gettingReviewsConnection = (HttpURLConnection) gettingCommentsURL.openConnection();
        gettingReviewsConnection.setRequestMethod("GET");


        // if connection is succeed.
        if (gettingReviewsConnection.getResponseCode() == 201) {
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

    /*
        Post login information. And receive id_token for authorization.
     */
    public boolean postLogin(String id, String password) throws IOException {
        // Login success or not
        boolean loginResult = false;

        // Real URL
        String realURLStr = basicURLStr + postingLoginURLStr;

        URL postingLogin = new URL(realURLStr);

        HttpURLConnection postingLoginConnection = (HttpURLConnection) postingLogin.openConnection();
        postingLoginConnection.setRequestMethod("POST");
        postingLoginConnection.setRequestProperty("Content-Type", "application/json");
        postingLoginConnection.setDoInput(true);

            /*
                email
                password
             */

        // Make json
        JSONObject loginInformation = new JSONObject();


        try {
            loginInformation.put("email", id);
            loginInformation.put("password", encryptSHA256(password));
        } catch (JSONException e) {
            Log.d("MakingJSON", "Failure: in postLogin()");
            e.printStackTrace();
        }

        // write on connection
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(postingLoginConnection.getOutputStream());
        outputStreamWriter.write(loginInformation.toString());
        outputStreamWriter.flush();

        if (postingLoginConnection.getResponseCode() == 201) {
            // success
            loginResult = true;

            InputStream responseBody = postingLoginConnection.getInputStream();

            InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");

            JsonReader jsonReader = new JsonReader(responseBodyReader);

            // Get id token for authorization
            // we need this token for adding new posting, adding a comment and so on...
            jsonReader.beginObject();
            jsonReader.skipValue(); // id_token
            idToken = jsonReader.nextString();

        } else {
            // fail
            loginResult = false;
        }


        // return Success or Fail (boolean)
        return loginResult;
    }


    /*
        Post a new account information for creating a new account.
     */
    public boolean postCreateNewAccount(String email, String password, String nickname) throws IOException {
        // Login success or not
        boolean isSucced = false;

        // Real URL
        String realURLStr = basicURLStr + postingNewAccountURLStr;

        URL postingCreateNewAccountURL = new URL(realURLStr);

        HttpURLConnection postingCreatNewAccountConnection = (HttpURLConnection) postingCreateNewAccountURL.openConnection();
        postingCreatNewAccountConnection.setRequestMethod("POST");
        postingCreatNewAccountConnection.setRequestProperty("Content-Type", "application/json");
        postingCreatNewAccountConnection.setDoInput(true);

            /*
                email
                password
                nickname
             */

        // Make json
        JSONObject newAccountInformation = new JSONObject();


        try {
            newAccountInformation.put("email", email);
            newAccountInformation.put("password", encryptSHA256(password));
            newAccountInformation.put("nickname", nickname);
        } catch (JSONException e) {
            Log.d("MakingJSON", "Failure: in postCreateNewAccount()");
            e.printStackTrace();
        }

        // write on connection
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(postingCreatNewAccountConnection.getOutputStream());
        outputStreamWriter.write(newAccountInformation.toString());
        outputStreamWriter.flush();

        if (postingCreatNewAccountConnection.getResponseCode() == 201) {
            // success
            isSucced = true;

//            InputStream responseBody = postingCreatNewAccountConnection.getInputStream();
//
//            InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
//
//            JsonReader jsonReader = new JsonReader(responseBodyReader);
//
//            // Get id token for authorization
//            // we need this token for adding new posting, adding a comment and so on...
//            jsonReader.beginObject();
//            jsonReader.skipValue(); // id_token
//            idToken = jsonReader.nextString();

        } else {
            // fail
            isSucced = false;
        }


        // return Success or Fail (boolean)
        return isSucced;
    }


    //// Using IsThereReview Object ////
    /*
        Update Comments using IsThereReview object.
     */
    public void updateComments(IsThereReview review) throws IOException {
        review.setComments(getReviewComments(review.getReviewId()));
    }

    public ArrayList<IsThereReview> getIsThereReviews(String category, String detailCategory, String location) throws IOException {

        ArrayList<IsThereReview> reviews = null;

         ArrayList<HashMap<ReviewKey, String>> reviewsArray = getReviews(category, detailCategory, location);
        if (reviewsArray != null) {
            // Allocating reviews.
            reviews = new ArrayList<>();

            for (HashMap<ReviewKey, String> value : reviewsArray) {
                IsThereReview newReview = new IsThereReview(
                        value.get(ReviewKey.REVIEW_ID),
                        value.get(ReviewKey.NAME),
                        value.get(ReviewKey.INFORMATION),
                        value.get(ReviewKey.LOCATION),
                        value.get(ReviewKey.LOCATION_CORD_X),
                        value.get(ReviewKey.LOCATION_CORD_Y),
                        value.get(ReviewKey.CATEGORY),
                        value.get(ReviewKey.DETAIL_CATEGORY)
                );

                // Add a new review object into IsThereReview[].
                reviews.add(newReview);
            }
        }


        return reviews;
    }


    ////  Getter ////
    public String getIdToken() {
        return idToken;
    }
    //// Getter - end ////


    private String encryptSHA256(String str) {
        String sha = null;

        try {
            MessageDigest sh = MessageDigest.getInstance("SHA-256");
            sh.update(str.getBytes());
            byte byteData[] = sh.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16)).substring(1);
            }
            sha = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // e.printStackTrace();
            Log.d("IsThereHttpHelper", "Encrypt Error");
            sha = null;
        }


        return sha;

    }

    //// Callback Interface ////
    private interface OnHttpCallback {
        void isSucceed(HttpURLConnection connection);

        void isFailed(boolean isIOException);

        void onGetSucceed(JsonReader jsonReader);
    }


    public void postJsonObject(String urlStr, JSONObject jsonObject, int responseCode, OnHttpCallback callBack) {
        try {
            URL url = new URL(urlStr);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoInput(true);

            // write on connection
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
            outputStreamWriter.write(jsonObject.toString());
            outputStreamWriter.flush();

            if (connection.getResponseCode() == responseCode) {
                // success
                callBack.isSucceed(connection);
            } else {
                // fail
                callBack.isFailed(false);
            }

            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
            callBack.isFailed(true);
        }
    }

    public void getJson(String urlStr, int responseCode, OnHttpCallback callBack) {
        try {
            URL url = new URL(urlStr);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == responseCode) {
                callBack.isSucceed(connection);

                InputStream responseBody = connection.getInputStream();

                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");

                JsonReader jsonReader = new JsonReader(responseBodyReader);

                // success
                callBack.onGetSucceed(jsonReader);
            } else {
                // fail
                callBack.isFailed(false);
            }

            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
            callBack.isFailed(true);
        }
    }

    public ArrayList<HashMap<ReviewKey, String>> getReviews2(String category, String detailCategory, String loc) throws IOException {

        String realURL = basicURLStr + gettingReviewsURLStr + "/" + URLEncoder.encode(category, "UTF-8") + "?detail=" + detailCategory + "&loc=" + loc;

        final ArrayList<HashMap<ReviewKey, String>> reviewsArray = new ArrayList<>();

        getJson(realURL, 200, new OnHttpCallback() {
            @Override
            public void isSucceed(HttpURLConnection connection) {

            }

            @Override
            public void isFailed(boolean isIOException) {
                LogDebuger.debugPrinter(LogDebuger.TagType.HTTP_HELPER, "getReview2 failed!");
            }

            @Override
            public void onGetSucceed(JsonReader jsonReader) {
                try {
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
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


            // return the dictionary format
        });

        return reviewsArray;
    }


}
