package nos.elfak.rs.senseclient;

import android.app.Activity;

import java.io.IOException;
import java.net.DatagramPacket;
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
            }
            socket.receive(receivePacket);
            String id = new String(receivePacket.getData());
            activity.subscribed(Long.parseLong(id.trim()));
            receiving = true;
            while (receiving) //ovde dodaj neki boolean za svaki slucaj
            {
                socket.receive(receivePacket);
                info = (new String(receivePacket.getData())).trim();
                if (info.contains("ping"))
                {
                    sendData(communication.activity.generateSensorList(), false);
                    activity.PrikaziPing(0, 0);
                }
            }
          //  closeSocket();
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


    public void sendData(ArrayList<SensorData> datas, boolean subscribing)
    {
        try
        {
            String info;
            DatagramPacket packet;
            byte [] sendData;
            if (socket == null)
                socket = new DatagramSocket();
            if(!subscribing)
            {
                for (int i = 0; i < datas.size(); i++)
                {
                    info = datas.get(i).toString() + "\n";
                    sendData = info.getBytes();

                    packet = new DatagramPacket(sendData, sendData.length);

                    packet.setAddress(InetAddress.getByName(Constants.ip_address));
                    packet.setPort(Integer.parseInt(Constants.port));
                    socket.send(packet);
                }
            }else
            {
                if(poslaoSubscribe)
                    return;
                info = "subscribe\n";
                for(int i = 0; i < datas.size(); i++)
                    info += datas.get(i).getSensor() + "\n";


                sendData = info.getBytes();

                packet = new DatagramPacket(sendData,sendData.length);
                packet.setAddress(InetAddress.getByName(Constants.ip_address));
                packet.setPort(Integer.parseInt(Constants.port));
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
