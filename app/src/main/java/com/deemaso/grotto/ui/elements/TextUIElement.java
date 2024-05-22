package com.deemaso.grotto.ui.elements;

import android.graphics.Paint;
import android.graphics.Typeface;

import com.deemaso.core.Entity;
import com.deemaso.grotto.ui.UIElement;

public class TextUIElement extends UIElement {

    private String text;
    private Paint paint;

    public TextUIElement(float x, float y, float width, float height, String text, float textSize, Typeface typeface, int color) {
        super(x, y, width, height);
        this.text = text;
        this.paint = new Paint();
        this.paint.setTextSize(textSize);
        this.paint.setTypeface(typeface);
        this.paint.setColor(color);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    @Override
    public void draw(float screenX, float screenY) {
        getCanvas().drawText(text, screenX, screenY, paint);
    }
}
