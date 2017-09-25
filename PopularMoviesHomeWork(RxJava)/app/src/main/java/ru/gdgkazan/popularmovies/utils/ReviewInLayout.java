package ru.gdgkazan.popularmovies.utils;

import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import ru.gdgkazan.popularmovies.model.content.Review;

/**
 * Created by DIMON on 25.08.2017.
 */

public class ReviewInLayout {
    TextView author;
    TextView content;
    LinearLayout ll;
    LinearLayout.LayoutParams linearParamsForAuthor;
    LinearLayout.LayoutParams linearParamsForContent;
    LinearLayout.LayoutParams linearParamsForNoReview;
    List<Review> reviews;

    public ReviewInLayout(LinearLayout ll, List<Review> reviews)
    {
        linearParamsForAuthor = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearParamsForAuthor.topMargin = 40;
        linearParamsForContent = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearParamsForNoReview = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearParamsForNoReview.gravity = Gravity.CENTER_HORIZONTAL;

        this.ll = ll;
        this.reviews = reviews;
    }
    public void setReviewsInLayout()
    {
        if (reviews.size() != 0) {
            for (Review r : reviews) {
                author = new TextView(ll.getContext());
                author.setTextSize(22);
                content = new TextView(ll.getContext());
                content.setTextSize(17);
                author.setText(r.getAuthor());
                content.setText(r.getContent());
                ll.addView(author, linearParamsForAuthor);
                ll.addView(content, linearParamsForContent);
            }
        } else {
            TextView noReviews = new TextView(ll.getContext());
            noReviews.setText("no reviews :(");
            noReviews.setTextSize(19);
            ll.addView(noReviews, linearParamsForNoReview);
        }
    }

}
