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

    public Communication(MainActivity activity)
    {
        this.activity = activity;
    }

    public void subscribe(ArrayList<SensorData> datas)
    {
        String info = "";
        for(int i = 0; i < datas.size(); i++)
            info += datas.get(i).getSensor() + "\n";

        byte[] sendData;
        byte[] receiveData = new byte[1024];
        sendData = info.getBytes();

        DatagramPacket packet = new DatagramPacket(sendData,sendData.length);
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        try
        {
            socket = new DatagramSocket();
            packet.setAddress(InetAddress.getByName(Constants.ip_address));
            packet.setPort(Integer.parseInt(Constants.port));
            socket.send(packet);
            socket.receive(receivePacket);
            String id = new String(receivePacket.getData());
            activity.subscribed(Integer.parseInt(id.trim()));
            closeSocket();
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

    private int parseIdFromSocket(String idStr)
    {
        int index = 0;
        while(Character.isDigit(idStr.charAt(index)))
            index++;

        if(index == 0)
            throw new NumberFormatException("not a number");
        else
            index--;

        idStr = idStr.substring(0, index);

        return Integer.parseInt(idStr);
    }

    public void closeSocket()
    {
        if(socket != null)
        {
            socket.disconnect();
            socket.close();
            socket = null;
        }
    }


    public void sendData(ArrayList<SensorData> datas)
    {
        String info = "";
        for(int i = 0; i < datas.size(); i++)
            info += datas.get(i).toString() + "\n";

        byte[] sendData;
        sendData = info.getBytes();

        DatagramPacket packet = new DatagramPacket(sendData,sendData.length);

        try
        {
            socket = new DatagramSocket();
            packet.setAddress(InetAddress.getByName(Constants.ip_address));
            packet.setPort(Integer.parseInt(Constants.port));
            socket.send(packet);
            closeSocket();
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
