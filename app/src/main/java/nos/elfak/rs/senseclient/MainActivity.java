package nos.elfak.rs.senseclient;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements  SensorEventListener
{
    private CheckBox acc, gyr, mag, gps;
    private boolean sendAcc, sendGyr, sendMag, sendGps;
    private SensorData sensorAcc, sensorGyr, sensorMag, sensorGps;
    private Thread thread;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        acc = (CheckBox) findViewById(R.id.check_acc);
        gyr = (CheckBox) findViewById(R.id.check_gyr);
        mag = (CheckBox) findViewById(R.id.check_mag);
        gps = (CheckBox) findViewById(R.id.check_gps);

        sendAcc = sendGps = sendGyr = sendMag = false;

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        sensorAcc = new SensorData(Constants.accelerometer);
        sensorGyr = new SensorData(Constants.gyroscope);
        sensorMag = new SensorData(Constants.magnetometer);
        sensorGps = new SensorData(Constants.gps);
    }

    public void sendData(View v)
    {
        if(acc.isChecked())
        {
            findViewById(R.id.acc_info).setVisibility(View.VISIBLE);
            sendAcc = true;
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 2000000);
        }
        acc.setVisibility(View.GONE);

        if(gyr.isChecked())
        {
            findViewById(R.id.gyr_info).setVisibility(View.VISIBLE);
            sendGyr = true;
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), 2000000);
        }
        gyr.setVisibility(View.GONE);

        if(mag.isChecked())
        {
            findViewById(R.id.mag_info).setVisibility(View.VISIBLE);
            sendMag = true;
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), 2000000);
        }
        mag.setVisibility(View.GONE);

        if(gps.isChecked())
        {
            findViewById(R.id.gps_info).setVisibility(View.VISIBLE);
            sendGps = true;
        }
        gps.setVisibility(View.GONE);

    }


    @Override
    public void onSensorChanged(SensorEvent event)
    {
        double ax, ay, az;
        ax = event.values[0];
        ay = event.values[1];
        az = event.values[2];
        TextView textView;
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            sensorAcc.setAll(ax, ay, az);

            textView = (TextView) findViewById(R.id.acc_x);
            textView.setText(String.valueOf(ax));

            textView = (TextView) findViewById(R.id.acc_y);
            textView.setText(String.valueOf(ay));

            textView = (TextView) findViewById(R.id.acc_z);
            textView.setText(String.valueOf(az));
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE)
        {
            sensorGyr.setAll(ax, ay, az);

            textView = (TextView) findViewById(R.id.gyr_x);
            textView.setText(String.valueOf(ax));

            textView = (TextView) findViewById(R.id.gyr_y);
            textView.setText(String.valueOf(ay));

            textView = (TextView) findViewById(R.id.gyr_z);
            textView.setText(String.valueOf(az));
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
        {
            sensorMag.setAll(ax, ay, az);

            textView = (TextView) findViewById(R.id.mag_x);
            textView.setText(String.valueOf(ax));

            textView = (TextView) findViewById(R.id.mag_y);
            textView.setText(String.valueOf(ay));

            textView = (TextView) findViewById(R.id.mag_z);
            textView.setText(String.valueOf(az));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }
}
