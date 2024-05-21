package com.deemaso.grotto.ui.elements;

import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.deemaso.core.Entity;
import com.deemaso.grotto.ui.GameSpaceUIElement;

public class GameSpaceTextUIElement extends GameSpaceUIElement {

    private String text;
    private Paint paint;

    public GameSpaceTextUIElement(float x, float y, float width, float height, Entity entity, String text, float textSize, Typeface typeface, int color, Paint paint) {
        super(x, y, width, height, entity);
        this.text = text;
        this.paint = paint;
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
