package nos.elfak.rs.senseclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by filip on 26.4.16..
 */
public class SensorData
{
    protected double x;
    protected double y;
    protected double z;
    protected String sensor;
    protected long id;
    protected String type = "upload";

    public SensorData(){}

    public SensorData(int x, int y, int z, String sensor)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.sensor = sensor;
    }

    public SensorData(String sensor)
    {
        this(0, 0, 0, sensor);
    }

    public double getX()
    {
        return x;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public double getY()
    {
        return y;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public double getZ()
    {
        return z;
    }

    public void setZ(double z)
    {
        this.z = z;
    }

    public String getSensor()
    {
        return sensor;
    }

    public void setSensor(String sensor)
    {
        this.sensor = sensor;
    }

    public void setAll(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    @Override
    public String toString()
    {
        Gson gson = new GsonBuilder().create();

        return gson.toJson(this);
    }

}
