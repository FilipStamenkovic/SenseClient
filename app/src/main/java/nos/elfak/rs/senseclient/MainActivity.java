package nos.elfak.rs.senseclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class MainActivity extends AppCompatActivity
{
    private CheckBox acc, gyr, mag, gps;
    private boolean sendAcc, sendGyr, sendMag, sendGps;

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

    }

    public void sendData(View v)
    {
        if(acc.isChecked())
        {
            findViewById(R.id.acc_info).setVisibility(View.VISIBLE);
            sendAcc = true;
        }
        acc.setVisibility(View.GONE);

        if(gyr.isChecked())
        {
            findViewById(R.id.gyr_info).setVisibility(View.VISIBLE);
            sendGyr = true;
        }
        gyr.setVisibility(View.GONE);

        if(mag.isChecked())
        {
            findViewById(R.id.mag_info).setVisibility(View.VISIBLE);
            sendMag = true;
        }
        mag.setVisibility(View.GONE);

        if(gps.isChecked())
        {
            findViewById(R.id.gps_info).setVisibility(View.VISIBLE);
            sendGps = true;
        }
        gps.setVisibility(View.GONE);
    }
}
