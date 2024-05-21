package com.deemaso.grotto.listeners;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.deemaso.grotto.systems.PhysicsSystem;

public class AccelerometerListener implements SensorEventListener {

    private final PhysicsSystem physicsSystem;

    public AccelerometerListener(PhysicsSystem physicsSystem)
    {
        this.physicsSystem = physicsSystem;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0], y = event.values[1], z = event.values[2];
        physicsSystem.updateGravity(-x, y);
        // Log.i("AccelerometerListener", "new gravity x= " + -x + "\t y= " + y);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // NOP
    }
}
