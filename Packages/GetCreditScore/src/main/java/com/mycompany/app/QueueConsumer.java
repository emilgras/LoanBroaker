/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.app;

import com.mycompany.connection.EndPoint;
import com.mycompany.interfaces.ConsumerDelegate;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.SerializationUtils;
import org.json.simple.JSONObject;

/**
 *
 * @author emilgras
 */
public class QueueConsumer extends EndPoint implements Runnable, com.rabbitmq.client.Consumer {

    
    private ConsumerDelegate delegate;
    
    public QueueConsumer(String endPointName, ConsumerDelegate delegate) throws IOException, TimeoutException {
        super(endPointName);
        this.delegate = delegate;
    }

    @Override
    public void run() {
        try {
            //start consuming messages. Auto acknowledge messages.
            channel.basicConsume(endPointName, true, (com.rabbitmq.client.Consumer) this);
        } catch (IOException ex) {
            delegate.didConsumeMessageWithOptionalException(null, ex);
        }
    }

    /**
     * Called when consumer is registered.
     * @param consumerTag
     */
    @Override
    public void handleConsumeOk(String consumerTag) {
        System.out.println("Consumer " + consumerTag + " registered");
    }

    /**
     * Called when new message is available.
     */
    public void handleDelivery(String consumerTag, Envelope env,
            BasicProperties props, byte[] body) throws IOException {

        JSONObject request = (JSONObject)SerializationUtils.deserialize(body);
        System.out.println("{GetCreditScore} didConsumeJSONMessage: " + request);
        HashMap<String, Object> application = new HashMap();
        application.put("ssn", (String)request.get("ssn"));
        application.put("loanAmount", (double)request.get("loanAmount"));
        application.put("loanDuration", (int)request.get("loanDuration"));
        
        
//        HashMap application = (HashMap) SerializationUtils.deserialize(body);
        delegate.didConsumeMessageWithOptionalException(application, null);
    }

    public void handleCancel(String consumerTag) { 
    }

    public void handleCancelOk(String consumerTag) {
    }

    public void handleRecoverOk(String consumerTag) {
    }

    public void handleShutdownSignal(String consumerTag, ShutdownSignalException arg1) {
    }

    public void accept(Object t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    

}
