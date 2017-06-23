package com.apptive.joDuo.isthere;

import android.graphics.Bitmap;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
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
    public static String basicURLStr = "http://52.78.33.177:9000";
    public static String gettingReviewsURLStr = "/reviews/";
    public static String postingReviewURLStr = "/reviews/review";
    public static String gettingLikeReviews = "/reviews/likes/";
    public static String postingLikeReviewURLStr = "/reviews/review/like";
    public static String deletingLikeReviewURLStr = "/reviews/review/dislike";
    public static String gettingReviewCommentURLStr = "/comments/";
    public static String postingCommentURLStr = "/comment";
    public static String deletingCommentURLStr = "/comment";
    public static String updatingLikePointURLStr = "/comment/like-point/";
    public static String updatingDislikePointURLStr = "/comment/dislike-point/";
    public static String postingLoginURLStr = "/users/user/login";
    public static String postingNewAccountURLStr = "/users/user/create";
    public static String gettingCategories = "/reviews/category/get";
    public static String postingImage = "/reviews/image/upload/";
    public static String gettingImage = "/reviews/image/get/";



    /// Authorization ///
    private String idToken = null;
    private String userId = null;


    public enum ReviewKey {
        NAME, INFORMATION, LOCATION, LOCATION_CORD_X, LOCATION_CORD_Y, CATEGORY, DETAIL_CATEGORY, REVIEW_ID, USER_ID,
        COMMENT_ID, COMMENT, LIKE_POINT, DISLIKE_POINT, DATE
    }

    public ArrayList<HashMap<ReviewKey, String>> getBasicReviews(String realURL) throws IOException {

        final ArrayList<HashMap<ReviewKey, String>> reviewsArray = new ArrayList<>();


        getJson(realURL, 200, new OnHttpCallback() {
            @Override
            public void onSucceed(HttpURLConnection connection) {
                LogDebuger.debugPrinter(LogDebuger.TagType.HTTP_HELPER, "getReviews succeed!");
            }

            @Override
            public void onFailed(boolean isIOException) {
                LogDebuger.debugPrinter(LogDebuger.TagType.HTTP_HELPER, "getReviews failed!");
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

    /**
      *  Get reviews depending on location and category.
     **/
    public ArrayList<HashMap<ReviewKey, String>> getReviews(String category, String detailCategory, String loc) throws IOException {
        String realURL = basicURLStr + gettingReviewsURLStr + URLEncoder.encode(category, "UTF-8") + "?detail=" +
                URLEncoder.encode(detailCategory, "UTF-8") + "&loc=" +
                URLEncoder.encode(loc, "UTF-8");

        return getBasicReviews(realURL);
    }


    /**
     *  Post a review
     *  If succeed, return the registered reviewId not -1
     *  If failed, return -1
     **/
    public long postReview(String location, double[] locationPoint, String name, String information, String category, String detailCategory) {
        boolean postResult = true;
        final long[] reviewID = new long[1];
        reviewID[0] = -1;

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
                try {
                    InputStream responseBody = connection.getInputStream();

                    InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");

                    JsonReader jsonReader = new JsonReader(responseBodyReader);
                    jsonReader.setLenient(true);

                    jsonReader.beginObject();
                    jsonReader.skipValue(); // reviewID
                    reviewID[0] = jsonReader.nextLong();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(boolean isIOException) {

            }

            @Override
            public void onGetSucceed(JsonReader jsonReader) {

            }
        });

        // return success or fail reviewID
        return reviewID[0];
    }

    /**
     * Post a image for a review.
     */
    public boolean postImage(String reviewId, Bitmap bitmap) {
        boolean isSucceed = false;

        // Real URL
        String urlStr = basicURLStr + postingImage + reviewId;

        String attachmentName = reviewId;
        String attachmentFileName = reviewId + ".bmp";
        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(false);
            connection.setDoOutput(true);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);




            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + crlf);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + attachmentName + "\";filename=\"" + attachmentFileName + "\"" + crlf);
            outputStream.writeBytes(crlf);

            // I want to send only 8 bit black & white bitmaps
            byte[] pixels = new byte[bitmap.getWidth() * bitmap.getHeight()];
            for (int i = 0; i < bitmap.getWidth(); i++) {
                for (int j = 0; j < bitmap.getHeight(); j++) {
                    // we're interested only in the MSB of the first byte.
                    // since the other 3 bytes are identical for B&W images
                    pixels[i + j] = (byte) ((bitmap.getPixel(i, j) & 0x80) >> 7);
                }
            }

            outputStream.write(pixels);

            // End content wrapper
            outputStream.writeBytes(crlf);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + crlf);

            // Flush output buffer
            outputStream.flush();
            outputStream.close();

            // Get response
            InputStream responseStream = new BufferedInputStream(connection.getInputStream());
            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));

            String line = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();

            String response = stringBuilder.toString();

            responseStream.close();
            connection.disconnect();

            isSucceed = true;
        } catch (IOException e) {
            e.printStackTrace();
            isSucceed = false;
        }



        return isSucceed;
    }

    /**
     *
     * @param reviewID
     * @param userId
     * @param comment
     * @return postResult
     * @throws IOException
     *
     * Post a review comment using IDs.
     */
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


    /**
     * Get comments of a review depending on reviewID.
     *
     * @param reviewID
     * @return ArrayList having comment information.
     * @throws IOException
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
                     *  JSON Order
                     *  id
                     *  review_id
                     *  user_id
                     *  comment
                     *  like_point
                     *  dislike_point
                     *  date
                     *  is_deleted
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

    /**
     * Delete a comment of a review depending on reviewID and userID.
     * Primary key is the combination of reviewId and userID.
     *
     * @param reviewID
     * @param userID
     * @return isSucceed in boolean.
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

    /**
     * Add a like point for a review comment.
     *
     * @param reviewID
     * @return isSucceed.
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

    /**
     * Add a dislike point for areview comment.
     *
     * @param reviewID
     * @return isSucceed.
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


    public ArrayList<IsThereReview> getLikeReviews(String userID) {

        String urlStr = basicURLStr + gettingLikeReviews + userID;
        ArrayList<HashMap<ReviewKey, String>> reviewsArray = null;


        try {
            reviewsArray = getBasicReviews(urlStr);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(reviewsArray == null) {
            return null;
        }

        // Allocating reviews.
        ArrayList<IsThereReview> reviews = new ArrayList<>();

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


        return reviews;
    }


    /**
     * Post this review to a user's favorite review.
     *
     * @param userID
     * @param reviewID
     * @return isSucceed.
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

    /**
     * Delete this review for a user's like reviews.
     *
     * @param userID
     * @param reviewID
     * @return isSucceed.
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

        isSucceed = delete(urlStr, reviewInfoJson, 200, new OnHttpCallback() {
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

    /**
     * Post a login information. And receive <br>id_token</br> for authorization.
     * @param id
     * @param password
     * @return login has be done in success with boolean value.
     * @throws IOException
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
            jsonReader.skipValue(); // id
            userId = jsonReader.nextString();
        } else {
            // fail
            loginResult = false;
        }


        // return Success or Fail (boolean)
        return loginResult;
    }


    /**
        Post a new account information for creating a new account.
     **/
    public boolean postCreateNewAccount(String email, String password, String nickname) throws IOException {
        // Login success or not
        boolean isSucceed = false;

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
            isSucceed = true;

        } else {
            // fail
            isSucceed = false;
        }


        // return Success or Fail (boolean)
        return isSucceed;
    }

    /**
     * Get categories from server.
     * @return ArrayList including categories...
     */
    public ArrayList<ArrayList<String>> getCategories() {
        String urlStr = basicURLStr + gettingCategories;

        final ArrayList<ArrayList<String>> categories = new ArrayList<>();

        getJson(urlStr, 201, new OnHttpCallback() {
            @Override
            public void onSucceed(HttpURLConnection connection) {

            }

            @Override
            public void onFailed(boolean isIOException) {

            }

            @Override
            public void onGetSucceed(JsonReader jsonReader) {
                try {
                    jsonReader.beginObject();
                    while (jsonReader.hasNext()) {
                        ArrayList<String> aCategorySet = new ArrayList<>();

                        String bigCategory = jsonReader.nextName();
                        aCategorySet.add(bigCategory);

                        jsonReader.beginArray();
                        while (jsonReader.hasNext()) {
                            aCategorySet.add(jsonReader.nextString());
                        }
                        jsonReader.endArray();

                        categories.add(aCategorySet);
                        // The first item of aCategorySet is the primary category, and the others are detail categories.
                    }

                    // Close JSON
                    jsonReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return categories;
    }
















    //// Using IsThereReview Object ////
    /**
        Update Comments using IsThereReview object.
     **/
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
    public String getUserId() {
        return userId;
    }
    //// Getter - end ////

    //// Setter ////
    public void setIdTokenNull() {
        idToken = null; // this means logout
    }

    /**
     * Encrypt password using SHA-256.
     *
     * @param str
     * @return Encrypted password.
     */
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


    private boolean postJsonObject(String urlStr, JSONObject jsonObject, int responseCode, OnHttpCallback callBack) {
       return post(urlStr, "POST", true, jsonObject, responseCode, callBack);
    }
    private boolean delete(String urlStr, JSONObject jsonObject, int responseCode, OnHttpCallback callback) {
        return post(urlStr, "DELETE", true, jsonObject, responseCode, callback);
    }
    private boolean put(String urlStr, JSONObject jsonObject, int responseCode, OnHttpCallback callback) {
        return post(urlStr, "PUT", true, jsonObject, responseCode, callback);
    }

    private boolean post(String urlStr, String requestMethod, boolean isPrivate, JSONObject jsonObject, int responseCode, OnHttpCallback callback) {
        boolean isSucceed = false;
        try {
            URL url = new URL(urlStr);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(requestMethod);
            if (isPrivate) {
                connection.setRequestProperty("Authorization", "Bearer " + idToken);
            }
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoInput(true);


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

    private boolean getJson(String urlStr, int responseCode, OnHttpCallback callBack) {
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
                LogDebuger.debugPrinter(LogDebuger.TagType.HTTP_HELPER, "ERROR: getJson() Connection Failed: responseCode=" + connection.getResponseCode());
            }

            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
            callBack.onFailed(true);
            isSucceed = false;
            LogDebuger.debugPrinter(LogDebuger.TagType.HTTP_HELPER, "EXCEPTION: getJson().IOException");
        }

        return isSucceed;
    }




}
