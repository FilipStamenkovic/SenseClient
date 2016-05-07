package nos.elfak.rs.senseclient;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener
{
    private CheckBox acc, gyr, mag, gps;
    private EditText address, port;
    private SeekBar seekBar;
    private boolean sendAcc, sendGyr, sendMag, sendGps, sendingData, subscribed = false;
    private SensorData sensorAcc, sensorGyr, sensorMag, sensorGps;
    private Thread thread;
    private SensorManager sensorManager;
    private Button button;
    LocationManager locationManager;
    LocationListener listener;
    int MY_PERMISSION = 99;
    int intervalCount = 0;
    Communication communication;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

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

        seekBar = (SeekBar) findViewById(R.id.seekbar);
        address = (EditText) findViewById(R.id.edit_address);
        address.setText(Constants.ip_address);

        port = (EditText) findViewById(R.id.edit_port);
        port.setText(Constants.port);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                Constants.interval = ((double) seekBar.getProgress() + 1) / 2.0;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

      //  subscribed = sendingData = sendAcc = sendGps = sendGyr = sendMag = false;
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        sensorAcc = new SensorData(Constants.accelerometer);
        sensorGyr = new SensorData(Constants.gyroscope);
        sensorMag = new SensorData(Constants.magnetometer);
        sensorGps = new SensorData(Constants.gps);

        if(communication == null)
        {
            communication = Communication.getCommunication(this);
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        if(requestCode == MY_PERMISSION)
        {
            boolean b = true;
            for(int i = 0; i < grantResults.length; i++)
            {
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED)
                {
                    b = false;
                    break;
                }
            }

            if(b)
                PickUpLocation(false);
        }
    }

    private void PickUpLocation(boolean unregister)
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSION);
            return;
        }else
        {
            if(unregister)
            {
                if(locationManager != null)
                {
                    locationManager.removeUpdates(listener);
                    locationManager = null;
                    listener = null;
                }
            }else
            {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, (int) (1000 * Constants.interval), 0, listener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (int) (1000 * Constants.interval), 0, listener);
            }
        }
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
        PickUpLocation(true);
        sendAcc = sendGps = sendGyr = sendMag = false;

        address.setEnabled(true);
        port.setEnabled(true);
        seekBar.setEnabled(true);
        subscribed = false;
        communication.closeSocket();
    }

    private void registerGpsListener()
    {
        if(!client.isConnected())
            client.connect();
        if (locationManager == null)
        {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            listener = new LocationListener()
            {
                @Override
                public void onLocationChanged(Location location)
                {
                    sensorGps.setAll(location.getLatitude(), location.getLongitude(), 0.0);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras)
                {
                }

                @Override
                public void onProviderEnabled(String provider)
                {
                }

                @Override
                public void onProviderDisabled(String provider)
                {
                }
            };
            PickUpLocation(false);
        }
    }

    private void subscribe()
    {
        Constants.interval = ((double) seekBar.getProgress() + 1) / 2.0;
        if (acc.isChecked())
        {
            findViewById(R.id.acc_info).setVisibility(View.VISIBLE);
            sendAcc = true;
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 2000000);
        } else
        {
            sendAcc = false;
        }
        acc.setVisibility(View.GONE);

        if (gyr.isChecked())
        {
            findViewById(R.id.gyr_info).setVisibility(View.VISIBLE);
            sendGyr = true;
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), 2000000);
        } else
        {
            sendGyr = false;
        }
        gyr.setVisibility(View.GONE);

        if (mag.isChecked())
        {
            findViewById(R.id.mag_info).setVisibility(View.VISIBLE);
            sendMag = true;
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), 2000000);
        } else
        {
            sendMag = false;
        }
        mag.setVisibility(View.GONE);

        if (gps.isChecked())
        {
            findViewById(R.id.gps_info).setVisibility(View.VISIBLE);
            sendGps = true;
            registerGpsListener();
        } else
        {
            sendGps = false;
        }
        gps.setVisibility(View.GONE);

        Constants.ip_address = address.getText().toString();
        Constants.port = port.getText().toString();

        address.setEnabled(false);
        port.setEnabled(false);

       // seekBar.setEnabled(false);
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                communication.receiving();
            }
        }).start();
    }

    public void subscribed(long id)
    {
        subscribed = true;
        sensorGps.setId(id);
        sensorAcc.setId(id);
        sensorGyr.setId(id);
        sensorMag.setId(id);
    }

    public void buttonClick(View v)
    {
        if (sendingData)
        {
            button.setText(Constants.sendLabel);
            thread.interrupt();
            recreateActivity();
        } else
        {
            button.setText(Constants.stopLabel);
            subscribe();
            if (thread != null)
            {
                if (thread.isAlive())
                {
                    thread.interrupt();
                }
            }

            thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    while (sendingData)
                    {
                        intervalCount++;
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                refreshData();
                            }
                        });
                        if(intervalCount == 2)
                        {
                            communication.sendData(generateSensorList(), !subscribed);

                            intervalCount = 0;
                        }
                        try
                        {
                            Thread.sleep((int)(Constants.interval * 1000));
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

    public void PrikaziPing(final int port,final int id)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if(port == 0)
                    Toast.makeText(getApplicationContext(),"Pingovan sam", Toast.LENGTH_LONG).show();
                else if(id == 1)
                    Toast.makeText(getApplicationContext(),"Od paketa port je: " + port, Toast.LENGTH_LONG).show();
                else if(id == 2)
                    Toast.makeText(getApplicationContext(),"Od soketa port je: " + port, Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(),"Od soketa localport je: " + port, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void PrikaziPing(final String text)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                    Toast.makeText(getApplicationContext(),text, Toast.LENGTH_LONG).show();
            }
        });
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


        textView = (TextView) findViewById(R.id.gps_x);
        textView.setText(String.format("%.2f", sensorGps.getX()));

        textView = (TextView) findViewById(R.id.gps_y);
        textView.setText(String.format("%.2f", sensorGps.getY()));

        textView = (TextView) findViewById(R.id.gps_z);
        textView.setText(String.format("%.2f", sensorGps.getZ()));
    }

    public ArrayList<SensorData> generateSensorList()
    {
        ArrayList<SensorData> datas = new ArrayList<>();
        //sendAcc, sendGyr, sendMag, sendGps,
        if(sendAcc)
            datas.add(sensorAcc);
        if(sendGyr)
            datas.add(sensorGyr);
        if(sendMag)
            datas.add(sensorMag);
        if(sendGps)
            datas.add(sensorGps);

        return datas;
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

    @Override
    public void onStop()
    {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://nos.elfak.rs.senseclient/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
