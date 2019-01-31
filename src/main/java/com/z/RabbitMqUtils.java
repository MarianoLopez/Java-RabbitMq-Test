package com.z;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class RabbitMqUtils {

    private static final String EXCHANGE_NAME = "JavaToNode-direct";
    private static final String EXCHANGE_TYPE = "direct";

    public enum RabbitTopic{
        NEW_MESSAGE("message.new"),
        UPDATE_MESSAGE("message.update");
        public String name;
        private RabbitTopic(String name) {
            this.name= name;
        }
    }

    private static final boolean AUTO_ACK = false;
    private static final boolean DURABLE = true;
    private static final boolean EXCLUSIVE = false;
    private static final boolean AUTO_DELETE = false;

    
    public static Channel initChannel() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("rabbitmq");
        factory.setPassword("rabbitmq");
        factory.setAutomaticRecoveryEnabled(true);
        factory.setConnectionTimeout(10000);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE,DURABLE);
        return channel;
    }

    public static void sendTo(Channel channel,RabbitTopic routingKey,String message) throws IOException {
        System.out.println(" [x] Send '"  + message + "' - routingKey: "+routingKey);
        channel.basicPublish(EXCHANGE_NAME,routingKey.name, MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes(StandardCharsets.UTF_8));
    }

    public static void subscribeTo(Channel channel,String queueName,RabbitTopic bindingKey, Consumer callback ) throws IOException {
        AMQP.Queue.DeclareOk queue =  channel.queueDeclare(queueName, DURABLE,EXCLUSIVE,AUTO_DELETE,null);
        System.out.println("Queue name: "+queue.getQueue());
        channel.queueBind(queue.getQueue(), EXCHANGE_NAME, bindingKey.name);
        System.out.println(" [*] Waiting for messages in: " +bindingKey+". To exit press CTRL+C");
        channel.basicConsume(queue.getQueue(), AUTO_ACK, "java-listener-tag", callback);
    }

    public static Consumer getDefaultCallBack(Channel channel){
        return new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String routingKey = envelope.getRoutingKey();
                String message = new String(body);
                long deliveryTag = envelope.getDeliveryTag();
                System.out.println(" [x] Received '" + routingKey + "':'" + message + "' - deliveryTag: "+deliveryTag);
                channel.basicAck(deliveryTag, false);
            }
        };
    }

}
