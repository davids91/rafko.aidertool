package Controller;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class GlobalIPFieldController extends CopyAbleFieldController {
    @Override
    String getText() {
        String text = "";
        /* get the IP address and display it */
        try (java.util.Scanner s = new java.util.Scanner(new java.net.URL("https://ip.seeip.org").openStream(), "UTF-8").useDelimiter("\\A")) {
            text = s.next();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    @Override
    String getFieldName() {
        return "WAN Address";
    }
}