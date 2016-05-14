package nos.elfak.rs.senseclient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity implements NestedScrollView.OnScrollChangeListener
{

    Communication communication;
    int pageSize = -1;
    int offset = 0;
    ArrayList<String> sensorTypes = new ArrayList<>();
    ArrayList<ReceiveData> datas = new ArrayList<>();
    ArrayList<ReceiveData> receivedData;
    View picker;
    boolean hasMoreData = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addRowToTable(null);
        NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.scroller);
        nestedScrollView.setOnScrollChangeListener(this);

        if(communication == null)
        {
            communication = Communication.getCommunication(null);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(fab != null)
        {
            fab.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ResultsActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                    builder.setMessage("Poruka").setTitle("Izaberite radjius");

                    builder.setView(createView());
                    builder.setPositiveButton("Request data", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            sensorTypes.clear();
                            addRowToTable(null);
                            pageSize = ((NumberPicker) picker.findViewById(R.id.picker)).getValue();
                            if (((CheckBox) picker.findViewById(R.id.res_acc)).isChecked())
                            {
                                sensorTypes.add(Constants.accelerometer);
                            }
                            if (((CheckBox) picker.findViewById(R.id.res_mag)).isChecked())
                            {
                                sensorTypes.add(Constants.magnetometer);
                            }
                            if (((CheckBox) picker.findViewById(R.id.res_gyr)).isChecked())
                            {
                                sensorTypes.add(Constants.gyroscope);
                            }
                            if (((CheckBox) picker.findViewById(R.id.res_gps)).isChecked())
                            {
                                sensorTypes.add(Constants.gps);
                            }
                            new Thread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    getData();
                                }
                            }).start();

                            dialog.dismiss();
                            dialog.cancel();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                            dialog.cancel();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }
            });
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private View createView()
    {
        picker = getLayoutInflater().inflate(R.layout.dialog_view, null);
        NumberPicker num = (NumberPicker) picker.findViewById(R.id.picker);
        num.setMinValue(10);
        num.setMaxValue(1000);
        num.setValue(50);
        return picker;
    }

    private void addRowToTable(ReceiveData data)
    {
        TableLayout table = (TableLayout) findViewById(R.id.table);
        TableRow row = (TableRow) getLayoutInflater().inflate(R.layout.info_row, null);
        if(data != null)
        {
            TextView view = (TextView) row.findViewById(R.id.sens_id);
            view.setText(Long.toString(data.getId()));
            view = (TextView) row.findViewById(R.id.sens_name);
            view.setText(data.getSensor());
            view = (TextView) row.findViewById(R.id.sens_x);
            view.setText(String.format("%.2f", data.getX()));
            view = (TextView) row.findViewById(R.id.sens_y);
            view.setText(String.format("%.2f", data.getY()));
            view = (TextView) row.findViewById(R.id.sens_z);
            view.setText(String.format("%.2f", data.getZ()));
            view = (TextView) row.findViewById(R.id.sens_ts);
            view.setText(data.timestamp);
        } else
        {
            table.removeAllViews();
        }
        table.addView(row);
    }

    private synchronized void getData()
    {
        Request request = new Request(pageSize, datas.size(), sensorTypes);
        receivedData = communication.getData(request);
        if (receivedData == null || receivedData.size() == 0)
            hasMoreData = false;
        else
        {
            hasMoreData = true;
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    for(ReceiveData data : receivedData)
                        addRowToTable(data);
                }
            });

            datas.addAll(receivedData);
        }
    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
    {
        View view = v.getChildAt(v.getChildCount() - 1);
        int diff = (view.getBottom() - (v.getHeight() + v.getScrollY()));

        // if diff is zero, then the bottom has been reached
        if (diff < 10)
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    getData();
                }
            }).start();
        }
    }
}
