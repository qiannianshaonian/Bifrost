三天内java零基础到用netty 和 californium库做个中转服务，
1对外部提供链接coapserver接口，
2对阿里云iot提供mqttclient，简单提供链接订阅发布消息。
server入口  src/main/java/aliyun_iot/Bifrost.java
测试客户端入口 src/test/java/aliyun_iot/CoapClient.java
 MqttClient mqttClient = new MqttClient(productKey, deviceName, deviceSecret);
 应该弄成进程池，初学java做demo笔试题，勿模仿
 后续的发布订阅流程也只是图方便实现功能，现实项目中也不能那么做
 阿里云密码账号这些可以自己注册 图方便联系我qq 1437747313
 
