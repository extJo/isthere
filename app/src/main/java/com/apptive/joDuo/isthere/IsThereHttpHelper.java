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
    private static String basicURLStr = "http://52.78.33.177:9000";
    private static String gettingReviewsURLStr = "/reviews/";
    private static String postingReviewURLStr = "/reviews/review";
    private static String postingLikeReviewURLStr = "/review/like";
    private static String deletingLikeReviewURLStr = "/review/dislike";
    private static String gettingReviewCommentURLStr = "/comments/";
    private static String postingCommentURLStr = "/comment";
    private static String deletingCommentURLStr = "/comment";
    private static String updatingLikePointURLStr = "/comment/like-point/";
    private static String updatingDislikePointURLStr = "/comment/dislike-point/";
    private static String postingLoginURLStr = "/users/user/login";
    private static String postingNewAccountURLStr = "/users/user/create";


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

        final ArrayList<HashMap<ReviewKey, String>> reviewsArray = new ArrayList<>();

        getJson(realURL, 200, new OnHttpCallback() {
            @Override
            public void onSucceed(HttpURLConnection connection) {

            }

            @Override
            public void onFailed(boolean isIOException) {
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


    /*
        Post a review
     */
    public boolean postReview(String location, double[] locationPoint, String name, String information, String category, String detailCategory) {
        boolean postResult = true;

        // Real URL
        String realURLStr = basicURLStr + postingReviewURLStr;


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

        postResult = postJsonObject(realURLStr, newReview, 200, new OnHttpCallback() {
            @Override
            public void onSucceed(HttpURLConnection connection) {

            }

            @Override
            public void onFailed(boolean isIOException) {

            }

            @Override
            public void onGetSucceed(JsonReader jsonReader) {

            }
        });

        // return success or fail boolean
        return postResult;
    }

    public boolean postReviewComment(String reviewID, String userId, String comment) throws IOException {

        boolean postResult = true;

        String realURLStr = basicURLStr + postingCommentURLStr;

        JSONObject newComment = new JSONObject();
        // Implement !!!!!!!!

        postResult = postJsonObject(realURLStr, newComment, 201, new OnHttpCallback() {
            @Override
            public void onSucceed(HttpURLConnection connection) {

            }

            @Override
            public void onFailed(boolean isIOException) {

            }

            @Override
            public void onGetSucceed(JsonReader jsonReader) {

            }
        });


        return postResult;

    }

    /*
        Get comments of a review depend on reviewID.
     */
    public ArrayList<HashMap<ReviewKey, String>> getReviewComments(String reviewID) throws IOException {
        String realURLStr = basicURLStr + gettingReviewCommentURLStr + reviewID;
        final ArrayList<HashMap<ReviewKey, String>> commentsArray = new ArrayList<>();

        getJson(realURLStr, 201, new OnHttpCallback() {
            @Override
            public void onSucceed(HttpURLConnection connection) {

            }

            @Override
            public void onFailed(boolean isIOException) {

            }

            @Override
            public void onGetSucceed(JsonReader jsonReader) {
                try {
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // return the dictionary format
        // this return value will be processed in IsThereReview Class with static method.
        // return arraySet of comments
        return commentsArray;
    }

    /*
        Delete a comment of a review depend on reviewID and userID.
        Primary key is the combination of reviewID and userID.
     */
    public boolean deleteReviewComment(String reviewID, String userID) {
        boolean isSucceed = false;

        String urlStr = basicURLStr + deletingCommentURLStr;

        /*
            review_id
            user_id
         */
        JSONObject commentInfoJson = new JSONObject();

        try {
            commentInfoJson.put("review_id", reviewID);
            commentInfoJson.put("user_id", userID);
        } catch (JSONException e) {
            e.printStackTrace();
            isSucceed = false;
            return isSucceed;
        }

        isSucceed = delete(urlStr, commentInfoJson, 200, new OnHttpCallback() {
            @Override
            public void onSucceed(HttpURLConnection connection) {

            }

            @Override
            public void onFailed(boolean isIOException) {

            }

            @Override
            public void onGetSucceed(JsonReader jsonReader) {

            }
        });
        // return boolean value
        return isSucceed;
    }

    /*
        Add a like point for a review comment.
     */
    public boolean updateReviewCommentLikePoint(String reviewID) {
        boolean isSucceed = false;

        String urlStr = basicURLStr + updatingLikePointURLStr + reviewID;

        isSucceed = put(urlStr, null, 200, new OnHttpCallback() {
            @Override
            public void onSucceed(HttpURLConnection connection) {

            }

            @Override
            public void onFailed(boolean isIOException) {

            }

            @Override
            public void onGetSucceed(JsonReader jsonReader) {

            }
        });

        // return boolean value
        return isSucceed;
    }

    /*
        Add a dislike point for a review comment.
     */
    public boolean updateReviewCommentDisliekPoint(String reviewID) {
        boolean isSucceed = false;

        String urlStr = basicURLStr + updatingDislikePointURLStr + reviewID;

        isSucceed = put(urlStr, null, 200, new OnHttpCallback() {
            @Override
            public void onSucceed(HttpURLConnection connection) {

            }

            @Override
            public void onFailed(boolean isIOException) {

            }

            @Override
            public void onGetSucceed(JsonReader jsonReader) {

            }
        });

        return isSucceed;
    }

    /*
        Post this review to a user's favorite review.
     */
    public boolean postLikeReview(String userID, String reviewID) {
        boolean isSucceed = false;

        String urlStr = basicURLStr + postingLikeReviewURLStr;

        JSONObject reviewInfoJson = new JSONObject();
        /*
            user_id
            review_id
         */

        try {
            reviewInfoJson.put("user_id", userID);
            reviewInfoJson.put("review_id", reviewID);
        } catch (JSONException e) {
            e.printStackTrace();
            isSucceed = false;
            return isSucceed;
        }

        isSucceed = postJsonObject(urlStr, reviewInfoJson, 200, new OnHttpCallback() {
            @Override
            public void onSucceed(HttpURLConnection connection) {

            }

            @Override
            public void onFailed(boolean isIOException) {

            }

            @Override
            public void onGetSucceed(JsonReader jsonReader) {

            }
        });

        return isSucceed;
    }

    /*
        Delete this review fro a user's favorite reviews.
     */
    public boolean deleteLikeReview(String userID, String reviewID) {
        boolean isSucceed = false;

        String urlStr = basicURLStr + deletingLikeReviewURLStr;

        JSONObject reviewInfoJson = new JSONObject();
        /*
            user_id
            review_id
         */

        try {
            reviewInfoJson.put("user_id", userID);
            reviewInfoJson.put("review_id", reviewID);
        } catch (JSONException e) {
            e.printStackTrace();
            isSucceed = false;
            return isSucceed;
        }

        isSucceed = postJsonObject(urlStr, reviewInfoJson, 200, new OnHttpCallback() {
            @Override
            public void onSucceed(HttpURLConnection connection) {

            }

            @Override
            public void onFailed(boolean isIOException) {

            }

            @Override
            public void onGetSucceed(JsonReader jsonReader) {

            }
        });

        return isSucceed;
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
        void onSucceed(HttpURLConnection connection);

        void onFailed(boolean isIOException);

        void onGetSucceed(JsonReader jsonReader);
    }
    private interface AfterHttpCallback {
        void onSucceed();

        void onFailed();
    }


    public boolean postJsonObject(String urlStr, JSONObject jsonObject, int responseCode, OnHttpCallback callBack) {
       return post(urlStr, "POST", true, jsonObject, responseCode, callBack);
    }
    public boolean delete(String urlStr, JSONObject jsonObject, int responseCode, OnHttpCallback callback) {
        return post(urlStr, "DELETE", true, jsonObject, responseCode, callback);
    }
    public boolean put(String urlStr, JSONObject jsonObject, int responseCode, OnHttpCallback callback) {
        return post(urlStr, "PUT", true, jsonObject, responseCode, callback);
    }

    public boolean post(String urlStr, String requestMethod, boolean isPrivate, JSONObject jsonObject, int responseCode, OnHttpCallback callback) {
        boolean isSucceed = false;
        try {
            URL url = new URL(urlStr);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(requestMethod);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoInput(true);
            if (isPrivate) {
                connection.setRequestProperty("Authorization", idToken);
            }

            // write on connection
            if (jsonObject != null) {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
                outputStreamWriter.write(jsonObject.toString());
                outputStreamWriter.flush();
            }

            if (connection.getResponseCode() == responseCode) {
                // success
                isSucceed = true;
                callback.onSucceed(connection);
            } else {
                // fail
                callback.onFailed(false);
                isSucceed = false;
            }

            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
            callback.onFailed(true);
            isSucceed = false;
        }
        return isSucceed;
    }

    public boolean getJson(String urlStr, int responseCode, OnHttpCallback callBack) {
        boolean isSucceed = false;
        try {
            URL url = new URL(urlStr);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == responseCode) {
                callBack.onSucceed(connection);

                InputStream responseBody = connection.getInputStream();

                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");

                JsonReader jsonReader = new JsonReader(responseBodyReader);

                // success
                callBack.onGetSucceed(jsonReader);
                isSucceed = true;
            } else {
                // fail
                callBack.onFailed(false);
                isSucceed = false;
            }

            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
            callBack.onFailed(true);
            isSucceed = false;
        }

        return isSucceed;
    }




}
