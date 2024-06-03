package com.deemaso.grotto.ui.elements;

import android.graphics.Bitmap;

import com.deemaso.grotto.ui.UIElement;

public class HeartUIElement extends UIElement {

    final Bitmap fullHeart;
    final Bitmap emptyHeart;
    final Bitmap halfHeart;

    public HeartUIElement(float x, float y, float width, float height, Bitmap fullHeart, Bitmap emptyHeart, Bitmap halfHeart) {
        super(x, y, width, height);
        ImageUIElement imageUIElement = new ImageUIElement(x, y, width, height, fullHeart);
        children.add(imageUIElement);
        this.fullHeart = fullHeart;
        this.emptyHeart = emptyHeart;
        this.halfHeart = halfHeart;
    }

    public void setFullHeart() {
        ((ImageUIElement) children.get(0)).setImage(fullHeart);
    }

    public void setEmptyHeart() {
        ((ImageUIElement) children.get(0)).setImage(emptyHeart);
    }

    public void setHalfHeart() {
        ((ImageUIElement) children.get(0)).setImage(halfHeart);
    }

    @Override
    public void draw(float screenX, float screenY) {
        super.draw(screenX, screenY);
    }
}
