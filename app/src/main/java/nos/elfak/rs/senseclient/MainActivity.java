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
    private boolean sendAcc, sendGyr, sendMag, sendGps, sendingData;
    private SensorData sensorAcc, sensorGyr, sensorMag, sensorGps;
    private Thread thread;
    private SensorManager sensorManager;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        acc = (CheckBox) findViewById(R.id.check_acc);
        gyr = (CheckBox) findViewById(R.id.check_gyr);
        mag = (CheckBox) findViewById(R.id.check_mag);
        gps = (CheckBox) findViewById(R.id.check_gps);
        button = (Button) findViewById(R.id.send_data);

        sendingData = sendAcc = sendGps = sendGyr = sendMag = false;

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        sensorAcc = new SensorData(Constants.accelerometer);
        sensorGyr = new SensorData(Constants.gyroscope);
        sensorMag = new SensorData(Constants.magnetometer);
        sensorGps = new SensorData(Constants.gps);
    }

    private void recreateActivity()
    {
        findViewById(R.id.acc_info).setVisibility(View.GONE);
        acc.setVisibility(View.VISIBLE);

        findViewById(R.id.gyr_info).setVisibility(View.GONE);
        gyr.setVisibility(View.VISIBLE);

        findViewById(R.id.mag_info).setVisibility(View.GONE);
        mag.setVisibility(View.VISIBLE);

        findViewById(R.id.gps_info).setVisibility(View.GONE);
        gps.setVisibility(View.VISIBLE);

        sensorManager.unregisterListener(this);
        sendAcc = sendGps = sendGyr = sendMag = false;
    }

    private void subscribe()
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

    public void buttonClick(View v)
    {
        if(sendingData)
        {
            button.setText(Constants.sendLabel);
            thread.interrupt();
            recreateActivity();
        }
        else
        {
            button.setText(Constants.stopLabel);
            subscribe();
            if (thread != null)
            {
                if(thread.isAlive())
                {
                    thread.interrupt();
                }
            }

            thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    while(sendingData)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                refreshData();
                            }
                        });

                        sendData();
                        try
                        {
                            Thread.sleep(Constants.interval * 1000);
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
        }
        sendingData = !sendingData;
    }

    private void refreshData()
    {
        TextView textView;
        textView = (TextView) findViewById(R.id.acc_x);
        textView.setText(String.format("%.2f", sensorAcc.getX()));

        textView = (TextView) findViewById(R.id.acc_y);
        textView.setText(String.format("%.2f", sensorAcc.getY()));

        textView = (TextView) findViewById(R.id.acc_z);
        textView.setText(String.format("%.2f", sensorAcc.getZ()));


        textView = (TextView) findViewById(R.id.gyr_x);
        textView.setText(String.format("%.2f", sensorGyr.getX()));

        textView = (TextView) findViewById(R.id.gyr_y);
        textView.setText(String.format("%.2f", sensorGyr.getY()));

        textView = (TextView) findViewById(R.id.gyr_z);
        textView.setText(String.format("%.2f", sensorGyr.getZ()));


        textView = (TextView) findViewById(R.id.mag_x);
        textView.setText(String.format("%.2f", sensorMag.getX()));

        textView = (TextView) findViewById(R.id.mag_y);
        textView.setText(String.format("%.2f", sensorMag.getY()));

        textView = (TextView) findViewById(R.id.mag_z);
        textView.setText(String.format("%.2f", sensorMag.getZ()));
    }

    private void sendData()
    {

    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        double ax, ay, az;
        ax = event.values[0];
        ay = event.values[1];
        az = event.values[2];

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            sensorAcc.setAll(ax, ay, az);

        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE)
        {
            sensorGyr.setAll(ax, ay, az);

        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
        {
            sensorMag.setAll(ax, ay, az);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }
}
