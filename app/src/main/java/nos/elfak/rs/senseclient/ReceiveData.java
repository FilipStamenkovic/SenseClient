package nos.elfak.rs.senseclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by filip on 14.5.16..
 */
public class ReceiveData extends SensorData
{
    public String timestamp;

    @Override
    public String toString()
    {
        Gson gson = new GsonBuilder().create();

        return gson.toJson(this);
    }
}
