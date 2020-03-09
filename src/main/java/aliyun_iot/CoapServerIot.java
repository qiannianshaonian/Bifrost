package aliyun_iot;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.network.config.NetworkConfig;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * Created by Administrator on 2020/3/5 0005.
 */
public class CoapServerIot extends CoapServer {

    private static final int COAP_PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_PORT);

    public void addEndpoints() {
        NetworkConfig config = NetworkConfig.getStandard();
        for (InetAddress addr : EndpointManager.getEndpointManager().getNetworkInterfaces()) {
            InetSocketAddress bindToAddress = new InetSocketAddress(addr, COAP_PORT);
            CoapEndpoint.Builder builder = new CoapEndpoint.Builder();
            builder.setInetSocketAddress(bindToAddress);
            builder.setNetworkConfig(config);
            addEndpoint(builder.build());
        }
    }

    public CoapServerIot() throws SocketException {
        add(new CoapServerIotResource());
    }
}
