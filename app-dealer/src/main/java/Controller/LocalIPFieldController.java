package Controller;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class LocalIPFieldController extends CopyAbleFieldController {
    @Override
    String getText() {
        String text = "";
        /* get the IP address and display it */
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            text = socket.getLocalAddress().getHostAddress();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
        return text;
    }

    @Override
    String getFieldName() {
        return "LAN Address";
    }
}
