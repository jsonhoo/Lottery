/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.wyzk.lottery.view;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wyzk.lottery.R;


/**
 * Create a dialog that follows material UI guidelines.
 */
public class DialogMaterial extends android.app.Dialog {

    Context mContext;
    View mView;
    View mBackgroundView;
    LinearLayout mBodyViewContainer;
    String mMainMessage;
    TextView mMessageTextView;
    String mTitle;
    TextView mTitleTextView;
    View mBodyView;
    ProgressBar mProgressView;

    // Buttons
    ButtonFlatMaterial mButtonAccept, mButtonNeutral, mButtonCancel;
    String mButtonAcceptText, mButtonNeutralText, mButtonCancelText;

    // Listeners
    View.OnClickListener mOnAcceptButtonClickListener;
    View.OnClickListener mOnCancelButtonClickListener;
    View.OnClickListener mOnNeutralButtonClickListener;

    // Global variables
    boolean mShowProgress = false;
    boolean mIsDialogCancelableOnTouchOutside = true;

    /**
     * Contractor
     *
     * @param context Context where the dialog will be shown.
     * @param title   Title of the dialog
     * @param message Message within the dialog
     */
    public DialogMaterial(Context context, String title, String message) {
        super(context, android.R.style.Theme_Translucent);
        mContext = context;
        mMainMessage = message;
        mTitle = title;
    }

    /**
     * Contractor
     *
     * @param context Context where the dialog will be shown.
     */
    public DialogMaterial(Context context) {
        super(context, android.R.style.Theme_Translucent);
        this.mContext = context;
    }

    /**
     * Add Cancel button to the dialog
     *
     * @param cancelText Text will be shown as cancel text
     */
    public void addCancelButton(String cancelText) {
        this.mButtonCancelText = cancelText;
    }

    /**
     * Add a custom view to the dialog
     *
     * @param view View to be added as a body in the dialog.
     */
    public void setBodyView(View view) {
        mBodyView = view;

        if (mBodyViewContainer != null) {
            mBodyViewContainer.removeAllViews();
            mBodyViewContainer.addView(mBodyView);
        }
    }

    /**
     * Show loading progress within the dialog.
     *
     * @param progress true to show progress, false to not.
     */
    public void setShowProgress(boolean progress) {
        mShowProgress = progress;

        if (mProgressView != null) {
            mProgressView.setVisibility(mShowProgress ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Make the dialog cancelable when touching outside itself.
     *
     * @param cancelable
     */
    public void setCanceledOnTouchOutside(boolean cancelable) {
        mIsDialogCancelableOnTouchOutside = cancelable;
    }

    /**
     * Add cancel button to the dialog.
     *
     * @param buttonCancelText            Text that will be placed in the cancel button
     * @param onCancelButtonClickListener onClick listener for the cancel button
     */
    public void addCancelButton(String buttonCancelText, View.OnClickListener onCancelButtonClickListener) {
        this.mButtonCancelText = buttonCancelText;
        this.mOnCancelButtonClickListener = onCancelButtonClickListener;
    }

    /**
     * Add add button to the dialog.
     *
     * @param buttonAcceptText            Text that will be placed in the accept button
     * @param onAcceptButtonClickListener onClick listener for the accept button
     */
    public void addAcceptButton(String buttonAcceptText, View.OnClickListener onAcceptButtonClickListener) {
        this.mButtonAcceptText = buttonAcceptText;
        this.mOnAcceptButtonClickListener = onAcceptButtonClickListener;
    }

    /**
     * Add neutral button to the dialog.
     *
     * @param neutralText Text that will be placed in the neutral button
     * @param listener    onClick listener for the neutral button
     */
    public void addNeutralButton(String neutralText, View.OnClickListener listener) {
        this.mButtonNeutralText = neutralText;
        this.mOnNeutralButtonClickListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog);

        mBodyViewContainer = (LinearLayout) findViewById(R.id.body);
        mView = (RelativeLayout) findViewById(R.id.contentDialog);
        mBackgroundView = (RelativeLayout) findViewById(R.id.dialog_rootView);
        mProgressView = (ProgressBar) findViewById(R.id.progress);

        mBackgroundView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getX() < mView.getLeft()
                        || event.getX() > mView.getRight()
                        || event.getY() > mView.getBottom()
                        || event.getY() < mView.getTop()) {
                    if (mIsDialogCancelableOnTouchOutside) {
                        dismiss();
                    }
                }
                return false;
            }
        });

        this.mTitleTextView = (TextView) findViewById(R.id.mTitle);
        setTitle(mTitle);

        this.mMessageTextView = (TextView) findViewById(R.id.message);
        setMainMessage(mMainMessage);

        this.mButtonAccept = (ButtonFlatMaterial) findViewById(R.id.button_accept);
        mButtonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mOnAcceptButtonClickListener != null) {
                    mOnAcceptButtonClickListener.onClick(v);
                } else {
                    dismiss();
                }
            }
        });

        if (mButtonCancelText != null) {
            this.mButtonCancel = (ButtonFlatMaterial) findViewById(R.id.button_cancel);
            this.mButtonCancel.setVisibility(View.VISIBLE);
            this.mButtonCancel.setText(mButtonCancelText);
            mButtonCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (mOnCancelButtonClickListener != null) {
                        mOnCancelButtonClickListener.onClick(v);
                    } else {
                        dismiss();
                    }
                }
            });
        }

        if (mButtonAcceptText != null) {
            this.mButtonAccept = (ButtonFlatMaterial) findViewById(R.id.button_accept);
            this.mButtonAccept.setVisibility(View.VISIBLE);
            this.mButtonAccept.setText(mButtonAcceptText);
            mButtonAccept.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (mOnAcceptButtonClickListener != null) {
                        mOnAcceptButtonClickListener.onClick(v);
                    } else {
                        dismiss();
                    }
                }
            });
        }

        if (mButtonNeutralText != null) {
            this.mButtonNeutral = (ButtonFlatMaterial) findViewById(R.id.button_neutral);
            this.mButtonNeutral.setVisibility(View.VISIBLE);
            this.mButtonNeutral.setText(mButtonNeutralText);
            mButtonNeutral.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (mOnNeutralButtonClickListener != null) {
                        mOnNeutralButtonClickListener.onClick(v);
                    } else {
                        dismiss();
                    }
                }
            });
        }

        if (mBodyView != null) {
            mBodyViewContainer.addView(mBodyView);
        }

        setShowProgress(mShowProgress);
    }

    @Override
    public void show() {
        super.show();

        // Start animations
        mView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.dialog_main_show_amination));
        mBackgroundView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.dialog_root_show_amin));
    }

    /////////////////////////////////// GETTERS AND SETTERS ///////////////////////////////////


    public void setMainMessage(String message) {
        mMainMessage = message;
        mMessageTextView.setText(mMainMessage);
        mMessageTextView.setVisibility(mMainMessage == null ? View.GONE : View.VISIBLE);
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
        if (mTitle == null) {
            mTitleTextView.setVisibility(View.GONE);
        } else {
            mTitleTextView.setVisibility(View.VISIBLE);
            mTitleTextView.setText(mTitle);
        }
    }

    public ButtonFlatMaterial getButtonAccept() {
        return mButtonAccept;
    }

    public ButtonFlatMaterial getButtonCancel() {
        return mButtonCancel;
    }

    public void setOnAcceptButtonClickListener(View.OnClickListener listener) {
        mOnAcceptButtonClickListener = listener;
        if (mButtonAccept != null) {
            mButtonAccept.setOnClickListener(mOnAcceptButtonClickListener);
        }
    }

    public void setOnNeutralButtonClickListener(View.OnClickListener listener) {
        mOnNeutralButtonClickListener = listener;
        if (mButtonNeutral != null) {
            mButtonNeutral.setOnClickListener(mOnNeutralButtonClickListener);
        }
    }

    public void setOnCancelButtonClickListener(View.OnClickListener listener) {
        mOnCancelButtonClickListener = listener;
        if (mButtonCancel != null) {
            mButtonCancel.setOnClickListener(mOnCancelButtonClickListener);
        }
    }

    @Override
    public void dismiss() {
        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.dialog_main_hide_amination);
        anim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mView.post(new Runnable() {
                    @Override
                    public void run() {
                        DialogMaterial.super.dismiss();
                    }
                });

            }
        });

        // Starting animation to hide the dialog.
        Animation backAnim = AnimationUtils.loadAnimation(mContext, R.anim.dialog_root_hide_amin);

        mView.startAnimation(anim);
        mBackgroundView.startAnimation(backAnim);
    }


}
