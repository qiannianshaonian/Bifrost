package aliyun_iot;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2020/3/5 0005.
 */
public class MqttClient {
    private MqttSign sign = new MqttSign();
    private String host = "";
    private ChannelFuture future = null;

    public MqttClient(String productKey, String deviceName, String deviceSecret) {
        this.sign.calculate(productKey, deviceName, deviceSecret);
        this.setHost();
    }

    public void connect() throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            io.netty.bootstrap.Bootstrap b = new io.netty.bootstrap.Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            MqttClientHandler mqttClientHandler = new MqttClientHandler(this.sign);
            b.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast("encoder", MqttEncoder.INSTANCE);
                    ch.pipeline().addLast("decoder", new MqttDecoder());
                    ch.pipeline().addLast("heartBeatHandler", new IdleStateHandler(0, 30, 0, TimeUnit.SECONDS));
                    ch.pipeline().addLast("handler", mqttClientHandler);
                }
            });
            this.future = b.connect(this.host, 1883).sync();
            System.out.println("Client connected");
        } finally {
            if (null == future) {
                System.out.println("ready reconnect");
                connect();
            }
        }
    }

    public void setHost() {
        String productKey = this.sign.getProductKey();
        String host = productKey + ".iot-as-mqtt.cn-shanghai.aliyuncs.com";
        this.host = host;
    }


}
