package com.deemaso.grotto.components;


import com.deemaso.core.components.Component;

public class CameraComponent extends Component {
    private float zoom;

    public CameraComponent(float zoom) {
        this.zoom = zoom;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

}
