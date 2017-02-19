package de.ehmkah.lurchi.lurchicam;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Sensor defaultSensor;
    private SensorManager sensorManager;
    private Date lastDate;
    private final static long WARTEZEIT_IN_MILLISEKUNDEN = 1000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lastDate = new Date();
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        defaultSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
    }

    public void klick(View v) {
        System.out.println("HUHU");
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, defaultSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (new Date().getTime() - WARTEZEIT_IN_MILLISEKUNDEN >= lastDate.getTime()) {
            String value = String.format("%s, Temperatur: %s°C", new Date(), event.values[0]);
            System.out.println(value);
            lastDate = new Date();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}