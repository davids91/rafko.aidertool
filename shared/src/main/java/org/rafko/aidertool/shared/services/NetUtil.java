package org.rafko.aidertool.shared.services;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetUtil {
    private static final Logger LOGGER = Logger.getLogger(NetUtil.class.getName());

    public static String getLANIP(){
        String text = "<UNKNOWN>";
        /* get the IP address and display it */
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            text = socket.getLocalAddress().getHostAddress();
        } catch (SocketException | UnknownHostException e) {
            LOGGER.log(Level.WARNING, "Unable to read local IP address! ", e);
        }
        return text;
    }

    public static String getWANIP(){
        String text = "<UNKNOWN>";
        /* get the IP address and display it */
        try (java.util.Scanner s = new java.util.Scanner(new java.net.URL("https://ip.seeip.org").openStream(), "UTF-8").useDelimiter("\\A")) {
            text = s.next();
        } catch (java.io.IOException e) {
            LOGGER.log(Level.WARNING, "Unable to read global IP address! ", e);
        }
        return text;
    }
}
