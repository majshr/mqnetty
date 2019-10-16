package com.mq.start;

import com.mq.broker.server.BrokerMqServer;

public class MqServerStartUp {
    public static void main(String[] args) {
        // MqContainer.getInstance().start();
        BrokerMqServer server = new BrokerMqServer("127.0.0.1:18888");
        server.init();
        server.start();
    }
}
