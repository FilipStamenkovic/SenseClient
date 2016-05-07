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
    private Thread sendingThread;
    private DatagramSocket receivingSocket;
    DatagramPacket receivePacket;
    private static Communication communication;
    private boolean receiving = false;
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

    public void subscribe(ArrayList<SensorData> datas)
    {
        String info = "subscribe\n";
        for(int i = 0; i < datas.size(); i++)
            info += datas.get(i).getSensor() + "\n";

        byte[] sendData;
        byte[] receiveData = new byte[1024];
        sendData = info.getBytes();

        DatagramPacket packet = new DatagramPacket(sendData,sendData.length);
        receivePacket = new DatagramPacket(receiveData, receiveData.length);
        try
        {
            if(socket == null)
            {
                socket = new DatagramSocket();
            }
            packet.setAddress(InetAddress.getByName(Constants.ip_address));
            packet.setPort(Integer.parseInt(Constants.port));
            socket.send(packet);
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
                    sendData(communication.activity.generateSensorList());
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

    private void receivePing()
    {
        try
        {
            byte [] data = new byte[1024];
            receiving = true;

            while (receiving) //ovde dodaj neki boolean za svaki slucaj
            {
               // DatagramPacket packet = new DatagramPacket(data, data.length);
              //  activity.PrikaziPing(receivingSocket.getPort(), 2);
             //   activity.PrikaziPing(receivingSocket.getLocalPort(), 3);
                receivingSocket.receive(receivePacket);
                String info = (new String(receivePacket.getData())).trim();
                if (info.contains("ping"))
                {
                    sendData(communication.activity.generateSensorList());
                    activity.PrikaziPing(0,0);
                }
            }
        } catch (SocketException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
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
        if(sendingThread != null && sendingThread.isAlive())
        {
            sendingThread.interrupt();
            sendingThread = null;
        }

        if(receivingSocket != null)
        {
            receivingSocket.disconnect();
            receivingSocket.close();
            receivingSocket = null;
        }

    }


    public void sendData(ArrayList<SensorData> datas)
    {
        try
        {
            String info;
            for(int i = 0; i < datas.size(); i++)
            {
                info = datas.get(i).toString() + "\n";

                byte[] sendData;
                sendData = info.getBytes();

                DatagramPacket packet = new DatagramPacket(sendData, sendData.length);
                if (socket == null)
                    socket = new DatagramSocket();
                packet.setAddress(InetAddress.getByName(Constants.ip_address));
                packet.setPort(Integer.parseInt(Constants.port));
                socket.send(packet);
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
