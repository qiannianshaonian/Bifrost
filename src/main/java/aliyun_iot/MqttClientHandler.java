package aliyun_iot;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2020/3/5 0005.
 */
public class MqttClientHandler extends ChannelInboundHandlerAdapter {

    private static final String PROTOCOL_NAME_MQTT_3_1_1 = "MQTT";
    private static final int PROTOCOL_VERSION_MQTT_3_1_1 = 4;

    private final String clientId;
    private final String userName;
    private final byte[] password;
    private final String subTopic;
    private final String pubTopic;

    public MqttClientHandler(MqttSign sign) {
        this.clientId = sign.getClientid();
        this.userName = sign.getUsername();
        this.password = sign.getPassword().getBytes();
        String sysInfo = "/sys/" + sign.getProductKey() + "/" + sign.getDeviceName();
        this.subTopic = sysInfo + "/thing/event/property/post_reply";
        this.pubTopic = sysInfo + "/thing/event/property/post";
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MqttMessage mqttMessage = (MqttMessage) msg;

        System.out.println("Received MQTT message: " + mqttMessage);
        switch (mqttMessage.fixedHeader().messageType()) {
            case CONNACK:
                MqttFixedHeader subFixedHeader =
                        new MqttFixedHeader(MqttMessageType.SUBSCRIBE, false, MqttQoS.AT_MOST_ONCE, false, 0);
                MqttMessageIdVariableHeader mqttidVariableHeader = MqttMessageIdVariableHeader.from(1);
                List<MqttTopicSubscription> topicSubscriptions = new LinkedList<MqttTopicSubscription>();
                topicSubscriptions.add(new MqttTopicSubscription(this.subTopic, MqttQoS.AT_LEAST_ONCE));
                MqttSubscribePayload subscribePayload = new MqttSubscribePayload(topicSubscriptions);
                MqttSubscribeMessage mqttSubMessage = new MqttSubscribeMessage(subFixedHeader, mqttidVariableHeader, subscribePayload);
                ctx.writeAndFlush(mqttSubMessage);
                break;
            case SUBACK:
                MqttFixedHeader pubFixedHeader =
                        new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.AT_MOST_ONCE, false, 0);
                MqttPublishVariableHeader mqttPublishVariableHeader = new MqttPublishVariableHeader(this.pubTopic, 1);
                String content = "{\"id\":\"1\",\"version\":\"1.0\",\"params\":{\"Status\":1,\"Data\":\"Hello, World!\"}}";
                ByteBuf heapBuf = Unpooled.wrappedBuffer(content.getBytes());
                MqttPublishMessage mqttPubMessage = new MqttPublishMessage(pubFixedHeader, mqttPublishVariableHeader, heapBuf);

                ctx.writeAndFlush(mqttPubMessage);
                break;
            case DISCONNECT:
                System.out.println("mqtt disconnect: " + mqttMessage.fixedHeader().messageType() + mqttMessage);
                ctx.close();
                break;
            default:
                System.out.println("Unexpected message type: " + mqttMessage.fixedHeader().messageType() + mqttMessage);
        }
        // release all messages
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        MqttFixedHeader connectFixedHeader =
                new MqttFixedHeader(MqttMessageType.CONNECT, false, MqttQoS.AT_MOST_ONCE, false, 0);
        MqttConnectVariableHeader connectVariableHeader =
                new MqttConnectVariableHeader(PROTOCOL_NAME_MQTT_3_1_1, PROTOCOL_VERSION_MQTT_3_1_1, true, true, false,
                        0, false, true, 40);
        MqttConnectPayload connectPayload = new MqttConnectPayload(clientId, null, null, userName, password);
        MqttConnectMessage connectMessage =
                new MqttConnectMessage(connectFixedHeader, connectVariableHeader, connectPayload);
        ctx.writeAndFlush(connectMessage);
        System.out.println("Sent CONNECT");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            MqttFixedHeader pingreqFixedHeader =
                    new MqttFixedHeader(MqttMessageType.PINGREQ, false, MqttQoS.AT_MOST_ONCE, false, 0);
            MqttMessage pingreqMessage = new MqttMessage(pingreqFixedHeader);
            ctx.writeAndFlush(pingreqMessage);
            System.out.println("Sent PINGREQ");
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
