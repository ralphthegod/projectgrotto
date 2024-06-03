package com.deemaso.grotto.ui.elements;

import android.graphics.Bitmap;
import android.graphics.Paint;

import com.deemaso.grotto.ui.UIElement;
import com.deemaso.grotto.utils.RenderUtils;

public class ImageUIElement extends UIElement {

    private Bitmap image;

    public ImageUIElement(float x, float y, float width, float height, Bitmap image) {
        super(x, y, width, height);
        this.image = RenderUtils.getResizedBitmap(image, (int) getWidth(), (int) getHeight());
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = RenderUtils.getResizedBitmap(image, (int) getWidth(), (int) getHeight());
    }

    @Override
    public void draw(float screenX, float screenY) {
        getCanvas().drawBitmap(image, screenX, screenY, new Paint());
    }
}
