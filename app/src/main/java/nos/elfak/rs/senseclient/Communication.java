package nos.elfak.rs.senseclient;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by filip on 30.4.16..
 */
public class Communication
{
    public DatagramSocket socket;

    public Communication()
    {
        try
        {
            socket = new DatagramSocket(Integer.parseInt(Constants.port), InetAddress.getByName(Constants.ip_address));
        } catch (SocketException e)
        {
            e.printStackTrace();
            socket = null;
        } catch (UnknownHostException e)
        {
            e.printStackTrace();
            socket = null;
        } catch (Exception e)
        {
            e.printStackTrace();
            socket = null;
        }
    }

    public void sendData(ArrayList<SensorData> datas)
    {
        String info = "";
        for(int i = 0; i < datas.size(); i++)
            info += datas.get(i).toString() + "\n";

    }
}
