package com.apptive.joDuo.isthere.reviewlist;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.androidquery.AQuery;
import com.apptive.joDuo.isthere.IsThereHttpHelper;
import com.apptive.joDuo.isthere.IsThereReview;
import com.apptive.joDuo.isthere.MainActivity;
import com.apptive.joDuo.isthere.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.TimerTask;

/**
 * Created by joseong-yun on 2017. 6. 21..
 */

public class ReviewItemAdaptor extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<IsThereReviewHolder> listViewItemList = new ArrayList<>();
    private ArrayList<IsThereReview> likeReviews = null;
    private ArrayList<IsThereReview> selectedReviews = null;
    private ArrayList<String> likeReviewIDs = null;
    private IsThereHttpHelper httpHelper = MainActivity.GetHttpHelper();
    private AQuery aQuery = null;


    // ListViewAdapter의 생성자
    public ReviewItemAdaptor(Context context) {
        aQuery = new AQuery(context);


    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.review_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.item_picture);
        TextView titleTextView = (TextView) convertView.findViewById(R.id.item_title);
        TextView descTextView = (TextView) convertView.findViewById(R.id.item_description);
        final ToggleButton likeToogleBtn = (ToggleButton) convertView.findViewById(R.id.item_like);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        final IsThereReviewHolder item = listViewItemList.get(pos);

        // 아이템 내 각 위젯에 데이터 반영
        aQuery.id(iconImageView).image(IsThereHttpHelper.basicURLStr + IsThereHttpHelper.gettingImage + item.review.getReviewId());
        titleTextView.setText(item.review.getName());
        descTextView.setText(item.review.getInformation());

        likeToogleBtn.setChecked(item.isLiked);

        likeToogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.isLiked = !item.isLiked;
            }
        });

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    public void setListViewItemList(ArrayList<IsThereReview> reviews) {

        selectedReviews = reviews;

        AsyncTask.execute(new TimerTask() {
            @Override
            public void run() {
                if (httpHelper.getIdToken() != null) { // if in login
                    likeReviews = httpHelper.getLikeReviews(httpHelper.getUserId());

                    // Make the like Review IDs array
                    ArrayList<String> likeReviewIDs = new ArrayList<String>();
                    for (IsThereReview aReview : likeReviews) {
                        likeReviewIDs.add(aReview.getReviewId());
                    }

                    // If a review in selectedReviews has the same likeReviewID then, holder has isLiked true, if not false
                    for(IsThereReview aReview: selectedReviews) {
                        boolean isLiked = likeReviewIDs.contains(aReview.getReviewId());
                        listViewItemList.add(new IsThereReviewHolder(aReview, isLiked));
                    }
                }
            }
        });
    }

    public ArrayList<IsThereReviewHolder> getItems() {
        return listViewItemList;
    }


    public static class IsThereReviewHolder {
        public IsThereReview review;
        public boolean isLiked = false;

        public IsThereReviewHolder(IsThereReview review, boolean isLiked) {
            this.review = review;
            this.isLiked = isLiked;
        }

        public IsThereReviewHolder(IsThereReview review) {
            this.review = review;
        }


    }

}
