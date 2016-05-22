package nos.elfak.rs.senseclient;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by filip on 30.4.16..
 */
public class Communication
{
    private DatagramSocket socket;
    private MainActivity activity;
   // private int receivePort;
    private Thread receivingThread;
   // DatagramPacket receivePacket;
    private static Communication communication;
    private boolean receiving = false;
    private boolean poslaoSubscribe = false;
    private Communication(MainActivity activity)
    {
        this.activity = activity;
    }

    public static Communication getCommunication(MainActivity activity)
    {
        if(communication == null)
            communication = new Communication(activity);

        return communication;
    }

    public void receiving()
    {
        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        try
        {
            receivingThread = Thread.currentThread();
            String info;
            if(socket == null)
            {
                socket = new DatagramSocket();
                socket.setBroadcast(true);
            }
            socket.receive(receivePacket);
            String recvId = new String(receivePacket.getData());
            Constants.id = Long.parseLong(recvId.trim());
            InetAddress address = receivePacket.getAddress();
            SharedPreferences.Editor editor = MainActivity.preferences.edit();
            editor.putLong("id", Constants.id);
            editor.commit();
            Constants.ip_address = address.getHostAddress();
            activity.subscribed(Constants.id);
            receiving = true;
            while (receiving)
            {
                socket.receive(receivePacket);
                info = (new String(receivePacket.getData())).trim();
                String[] lines = info.split("\n");
                String pingSens = null;
                if(lines.length == 2)
                    pingSens = lines[1];
                if (lines[0].contains("ping"))
                {
                    sendData(communication.activity.generateSensorList(), false, pingSens);
                    activity.PrikaziPing();
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
            closeSocket();
        } catch (Exception e)
        {
            e.printStackTrace();
            closeSocket();
        }
    }

    public void closeSocket()
    {
        if(socket != null)
        {
            socket.disconnect();
            socket.close();
            socket = null;
        }
        receiving = false;
        poslaoSubscribe = false;
        if(receivingThread != null && receivingThread.isAlive())
        {
            receivingThread.interrupt();
            receivingThread = null;
        }

      /*  if(receivingSocket != null)
        {
            receivingSocket.disconnect();
            receivingSocket.close();
            receivingSocket = null;
        }*/

    }


    public void sendData(ArrayList<SensorData> datas, boolean subscribing, String sensor)
    {
        try
        {
            String info;
            DatagramPacket packet;
            byte [] sendData;
            if (socket == null)
            {
                socket = new DatagramSocket();
                socket.setBroadcast(true);
            }if(!subscribing)
            {
                for (int i = 0; i < datas.size(); i++)
                {
                    if(sensor != null && !datas.get(i).getSensor().contentEquals(sensor))
                        continue;
                    info = datas.get(i).toString() + "\0";
                    sendData = info.getBytes();

                    packet = new DatagramPacket(sendData, sendData.length);


                    packet.setAddress(InetAddress.getByName(Constants.ip_address));
                    packet.setPort(Constants.port);
                    socket.send(packet);
                    int a = 3;
                    a++;
                }
            }else
            {
                if(poslaoSubscribe)
                    return;
                info = "subscribe\n" + Constants.id + "\n";
                for(int i = 0; i < datas.size(); i++)
                    info += datas.get(i).getSensor() + "\n";


                sendData = info.getBytes();

                packet = new DatagramPacket(sendData,sendData.length);
                packet.setAddress(InetAddress.getByName(Constants.ip_address));
                packet.setPort(Constants.port);
                socket.send(packet);
                poslaoSubscribe = true;
            }
           // closeSocket();
        } catch (IOException e)
        {
            e.printStackTrace();
            closeSocket();
        } catch (Exception e)
        {
            e.printStackTrace();
            closeSocket();
        }
    }
}
