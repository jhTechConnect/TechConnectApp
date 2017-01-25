package org.techconnect.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.centum.techconnect.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by doranwalsten on 1/24/17.
 */

public class ThumbFeedbackView extends RelativeLayout implements View.OnClickListener {

    //UI Objects
    @Bind(R.id.upThumbButton)
    ImageButton upThumbButton;
    @Bind(R.id.downThumbButton)
    ImageButton downThumbButton;
    @Bind(R.id.upCountTextView)
    TextView upCountTextView;
    @Bind(R.id.downCountTextView)
    TextView downCountTextView;

    //Fields
    private int upCount = 0;
    private int downCount = 0;
    private int currentState = 0; //Neutral to start

    //Status
    public static final int STATE_NEUTRAL = 0;
    public static final int STATE_UP = 1;
    public static final int STATE_DOWN = 2;

    //Default constructors
    public ThumbFeedbackView(Context context) {
        super(context);
    }

    public ThumbFeedbackView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThumbFeedbackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        updateViews();

        upThumbButton.setOnClickListener(this);
        downThumbButton.setOnClickListener(this);
    }


    private void updateViews() {
        downCountTextView.setText(String.format("%d",downCount));
        upCountTextView.setText(String.format("%d",upCount));

        switch(currentState) {
            case STATE_NEUTRAL:
                //Make sure border icon is used
                downThumbButton.setImageResource(R.drawable.ic_thumb_down_border_24dp);
                upThumbButton.setImageResource(R.drawable.ic_thumb_up_border_24dp);
                break;
            case STATE_UP:
                //Make sure green icon is used for up, border used for down
                upThumbButton.setImageResource(R.drawable.ic_thumb_up_primary_24dp);
                downThumbButton.setImageResource(R.drawable.ic_thumb_down_border_24dp);
                break;
            case STATE_DOWN:
                //make sure green icon is used for down, border used for up
                upThumbButton.setImageResource(R.drawable.ic_thumb_up_border_24dp);
                downThumbButton.setImageResource(R.drawable.ic_thumb_down_primary_24dp);
                break;
        }
    }



    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int s) {
        this.currentState = s;
        updateViews();
    }


    public int getUpCount() {
        return upCount;
    }

    public void setUpCount(int upCount) {
        this.upCount = upCount;
        updateViews();
    }

    public int getDownCount() {
        return downCount;
    }

    public void setDownCount(int downCount) {
        this.downCount = downCount;
        updateViews();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.upThumbButton) {
            //Check to see what current state is
            switch(currentState) {
                case STATE_NEUTRAL:
                    upThumbButton.setImageResource(R.drawable.ic_thumb_up_primary_24dp);
                    currentState = STATE_UP;
                    break;
                case STATE_UP:
                    //Make sure green icon is used for up, border used for down
                    upThumbButton.setImageResource(R.drawable.ic_thumb_up_border_24dp);
                    currentState = STATE_NEUTRAL;
                    break;
                case STATE_DOWN:
                    //make sure green icon is used for down, border used for up
                    upThumbButton.setImageResource(R.drawable.ic_thumb_up_primary_24dp);
                    downThumbButton.setImageResource(R.drawable.ic_thumb_down_border_24dp);
                    currentState = STATE_UP;
                    break;
            }
        } else if (view.getId() == R.id.downThumbButton) {
            switch(currentState) {
                case STATE_NEUTRAL:
                    downThumbButton.setImageResource(R.drawable.ic_thumb_down_primary_24dp);
                    currentState = STATE_DOWN;
                    break;
                case STATE_UP:
                    //Make sure green icon is used for up, border used for down
                    upThumbButton.setImageResource(R.drawable.ic_thumb_up_border_24dp);
                    downThumbButton.setImageResource(R.drawable.ic_thumb_down_primary_24dp);
                    currentState = STATE_DOWN;
                    break;
                case STATE_DOWN:
                    //make sure green icon is used for down, border used for up
                    downThumbButton.setImageResource(R.drawable.ic_thumb_down_border_24dp);
                    currentState = STATE_NEUTRAL;
                    break;
            }
        }

    }
}
