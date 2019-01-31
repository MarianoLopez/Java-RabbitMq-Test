package com.z;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class Sender {


    public static void main(String[] argv) throws Exception {
            Channel channel = RabbitMqUtils.initChannel();
            Executors.newSingleThreadExecutor().execute(() -> {
                while (true){
                    try {
                        RabbitMqUtils.sendTo(channel, RabbitMqUtils.RabbitTopic.NEW_MESSAGE,LocalDateTime.now().toString());
                        RabbitMqUtils.sendTo(channel, RabbitMqUtils.RabbitTopic.UPDATE_MESSAGE,LocalDateTime.now().toString());
                        Thread.sleep(5000);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
    }
}

