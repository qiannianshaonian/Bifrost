package aliyun_iot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.elements.exception.ConnectorException;

import java.io.IOException;

/**
 * Created by Administrator on 2020/3/5 0005.
 */
public class CoapClient {
    public static void main(String args[]) {

        org.eclipse.californium.core.CoapClient client = new org.eclipse.californium.core.CoapClient("127.0.0.1:5683/startdevice");
        CoapResponse response = null;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("productkey", "a13ztDee6tQ");
            jsonObject.put("devicename", "spring1");
            jsonObject.put("devicesecret", "xxxx");//加1437747313要或者自己创建阿里云账号
            String payload = JSON.toJSONString(jsonObject);
            response = client.put(payload, MediaTypeRegistry.TEXT_PLAIN);
        } catch (ConnectorException | IOException e) {
            System.err.println("Got an error: " + e);
        }
        if (response != null) {
            System.out.println("responseCode    " + response.getCode());
            System.out.println("responseInfo    " + response.getResponseText());
        } else {
            System.out.println("put fail ");
        }
    }
}
