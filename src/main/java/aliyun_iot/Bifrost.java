package aliyun_iot;

import java.net.SocketException;

/**
 * Created by Administrator on 2020/3/5 0005.
 */
public class Bifrost {
    public static void main(String[] args) {
        //start coapserver
        startCoapServer();
    }

    public static void startCoapServer() {
        try {
            CoapServerIot server = new CoapServerIot();
            CoapServerIotResource coapServerIotResource = new CoapServerIotResource();
            server.add(coapServerIotResource);
            server.addEndpoints();
            server.start();
        } catch (SocketException e) {
            System.err.println("Failed to initialize server: " + e.getMessage());
        }
    }
}
