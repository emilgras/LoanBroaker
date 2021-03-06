/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.main;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.util.HashMap;
import org.apache.commons.lang.SerializationUtils;
import org.json.simple.JSONObject;

/**
 *
 * @author emilgras
 */
public class Dummy {

    protected static final String HOST = "datdb.cphbusiness.dk";
    protected static final String USERNAME = "student";
    protected static final String PASSWORD = "cph";

    protected static Connection connection;
    protected static Channel channel;
    protected static String endPointName = "LoanBroker9.getCreditScore_in";

    public static void main(String[] args) {
        //Create a connection factory
        ConnectionFactory factory = new ConnectionFactory();

        try {

            //hostname of your rabbitmq server
            factory.setHost(HOST);
            factory.setUsername(USERNAME);
            factory.setPassword(PASSWORD);

            //getting a connection
            connection = factory.newConnection();

            //creating a channel
            channel = connection.createChannel();

            //declaring a queue for this channel. If queue does not exist,
            //it will be created on the server.
            channel.queueDeclare(endPointName, false, false, false, null);
            
            JSONObject request = new JSONObject();
            request.put("ssn", "123456-7890");
            request.put("loanAmount", 10000.0);
            request.put("loanDuration", 3);
            
            
            HashMap message = new HashMap();
            message.put("ssn", "123456-7890");
            message.put("loanAmount", "10000.0");
            message.put("loanDuration", "1973-01-01 01:00:00.0 CET");
            
            channel.basicPublish("", endPointName, null, SerializationUtils.serialize(request));
            
            channel.close();
            connection.close();
            System.out.println("message sent");
        } catch (Exception e) {
            System.out.println("Error connecting to MQ Server. Message: " + e.getLocalizedMessage());
        }
    }
}
