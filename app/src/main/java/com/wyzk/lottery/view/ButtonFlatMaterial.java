/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/


package com.wyzk.lottery.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wyzk.lottery.R;
import com.wyzk.lottery.utils.ApplicationUtils;


/**
 * This mView represents a button mView with material design style.
 */
public class ButtonFlatMaterial extends ButtonMaterial {

    TextView textButton;
    Context context;

    public ButtonFlatMaterial(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
    }

    protected void setDefaultProperties() {
        minHeight = 36;
        minWidth = 88;
        rippleSize = 3;
        // Min size
        setMinimumHeight(ApplicationUtils.dpToPx(minHeight, getResources()));
        setMinimumWidth(ApplicationUtils.dpToPx(minWidth, getResources()));

        // Set transparent background
        setBackgroundResource(R.drawable.background_transparent);

    }

    @Override
    protected void setAttributes(AttributeSet attrs) {
        // Set text button
        String text = null;
        int textResource = attrs.getAttributeResourceValue(ANDROIDXML, "text", -1);
        if (textResource != -1) {
            text = getResources().getString(textResource);
        }
        else {
            text = attrs.getAttributeValue(ANDROIDXML, "text");
        }
        if (text != null) {
            textButton = new TextView(getContext());
            textButton.setText(text.toUpperCase());
            textButton.setTextColor(backgroundColor);
            textButton.setTypeface(null, Typeface.NORMAL);
            textButton.setTextSize(14);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            textButton.setLayoutParams(params);
            addView(textButton);
        }

        // Retrieving color of the text.
        background = attrs.getAttributeIntValue(ANDROIDXML, "background", -1);
        setTextColor(background);

        // Retrieving background color and color when button is pressed.
        int newPressColor = attrs.getAttributeIntValue(MATERIALDESIGNXML, "pressColor", Color.parseColor("#88DDDDDD"));
        int backColor = attrs.getAttributeIntValue(MATERIALDESIGNXML, "buttonbackground", -1);

        enabledColor = backColor;

        // Set press color and background color.
        setPressColor(newPressColor);
        if (backColor != -1) {
            setBackgroundColor(backColor);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (x != -1) {

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(makePressColor());
            canvas.drawCircle(x, y, radius, paint);
            if (radius > getHeight() / rippleSize) {
                radius += rippleSpeed;
            }

            if (radius >= getWidth()) {
                x = -1;
                y = -1;
                radius = getHeight() / rippleSize;
                if (onClickListener != null && clickAfterRipple) {
                    onClickListener.onClick(this);
                }
            }
            invalidate();
        }

    }

    /**
     * Make a dark color to ripple effect
     *
     * @return color when button is pressed.
     */
    @Override
    protected int makePressColor() {

        return pressColor;
    }

    /**
     * Set a new color value for the situation when button is pressed.
     *
     * @param color
     */
    public void setPressColor(int color) {
        pressColor = color;
    }

    public void setText(String text) {
        textButton.setText(text.toUpperCase());
    }

    /**
     * Set color of background
     */
    public void setTextColor(int color) {
        textButton.setTextColor(color);
    }

    @Override
    public TextView getTextView() {
        return textButton;
    }

    public String getText() {
        return textButton.getText().toString();
    }

}
