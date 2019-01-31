package com.z;

import com.rabbitmq.client.Channel;


public class Receiver {

    private static final String QUEUE1_NAME = "java-message.new";
    private static final String QUEUE2_NAME = "java-message.update";



    public static void main(String[] argv) throws Exception {
        Channel channel1 = RabbitMqUtils.initChannel();
        RabbitMqUtils.subscribeTo(channel1,QUEUE1_NAME, RabbitMqUtils.RabbitTopic.NEW_MESSAGE,RabbitMqUtils.getDefaultCallBack(channel1));
        Channel channel2 = RabbitMqUtils.initChannel();
        RabbitMqUtils.subscribeTo(channel2,QUEUE2_NAME, RabbitMqUtils.RabbitTopic.UPDATE_MESSAGE,RabbitMqUtils.getDefaultCallBack(channel2));
    }
}
