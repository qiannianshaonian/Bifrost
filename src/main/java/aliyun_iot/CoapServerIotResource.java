package aliyun_iot;

import com.alibaba.fastjson.JSONObject;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;

/**
 * Created by Administrator on 2020/3/5 0005.
 */
public class CoapServerIotResource extends CoapResource {
    private static final String DEVICE_PRODUCTKEY = "productkey";
    private static final String DEVICE_NAME = "devicename";
    private static final String DEVICE_SECRET = "devicesecret";
    public CoapServerIotResource() {
        // set resource identifier
        super("startdevice");
        // set display name
        getAttributes().setTitle("Hello-World Resource");
    }

    @Override
    public void handlePUT(CoapExchange exchange) {
        String content = exchange.getRequestText();
        JSONObject jsonObject = JSONObject.parseObject(content);
        if (null != jsonObject.get(DEVICE_NAME)
                && null != jsonObject.getString(DEVICE_PRODUCTKEY)
                && null != jsonObject.getString(DEVICE_SECRET)) {
            this.startMqttClient(jsonObject);
        }
        exchange.respond(CoAP.ResponseCode.METHOD_NOT_ALLOWED,"start success");
    }

    private void startMqttClient(JSONObject deviceInfo) {
        String productKey = deviceInfo.getString(DEVICE_PRODUCTKEY);
        String deviceName = deviceInfo.getString(DEVICE_NAME);
        String deviceSecret = deviceInfo.getString(DEVICE_SECRET);
        MqttClient mqttClient = new MqttClient(productKey, deviceName, deviceSecret);
        try {
            mqttClient.connect();
        } catch (Exception e) {
            System.err.println("Failed to initialize mqttclient: " + e.getMessage());
        }
    }
}