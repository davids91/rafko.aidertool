package Controller;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.util.Duration;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class DealerDashboardController {
    @FXML
    private ImageView clipboard_img;
    @FXML
    private TextField addressField;
    @FXML
    private Accordion requestList;

    @FXML
    public void initialize() {
        clipboard_img.setImage(new Image("Img/clipboard.png"));

        /* get the IP address and display it */
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            addressField.setText(socket.getLocalAddress().getHostAddress());
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }

        addressField.setOnMouseClicked(event -> {
            addressField.selectAll();
            final ClipboardContent content = new ClipboardContent();
            content.put(DataFormat.PLAIN_TEXT, addressField.getText());
            Clipboard.getSystemClipboard().setContent(content);

            KeyValue transparent = new KeyValue(clipboard_img.opacityProperty(), 0.0);
            KeyValue opaque = new KeyValue(clipboard_img.opacityProperty(), 1.0);

            final KeyFrame delayHideKf = new KeyFrame(Duration.ZERO, opaque);
            final KeyFrame hideKf = new KeyFrame(Duration.millis(500), transparent);
            Platform.runLater(new Timeline(delayHideKf, hideKf)::play);
        });
    }
}
