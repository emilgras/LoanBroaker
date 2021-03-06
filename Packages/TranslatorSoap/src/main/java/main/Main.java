/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import app.Producer;
import app.QueueConsumer;
import interfaces.ConsumerDelegate;
import interfaces.ProducerDelegate;
import java.io.IOException;
import org.json.simple.JSONObject;

/**
 *
 * @author emilgras
 */
public class Main implements ConsumerDelegate, ProducerDelegate {
    private QueueConsumer consumer;
    private Producer producer;
    private static final String EXCHANGE_NAME = "LoanBroker9.getRecipients_out";

    public Main() throws Exception {
        consumer = new QueueConsumer(EXCHANGE_NAME, "bank-danske-bank", this);
        producer = new Producer("LoanBroker9.getWebService_in", this);

        Thread consumerThread = new Thread(consumer);
        consumerThread.start();
    }

    @Override
    public void didConsumeMessageWithOptionalException(JSONObject application, Exception ex) {
        if (ex == null) {
            System.out.println("translator did consume - " + application);
            
            producer.sendMessage(application.toJSONString(), "LoanBroker9.banks_out");
        } else {
            System.out.println("Failed with exception: " + ex.getLocalizedMessage());
        }
    }

    @Override
    public void didProduceMessageWithOptionalException(IOException ex) {
        if (ex == null) {
            System.out.println("translator did send message to bank - success");
        } else {
            System.out.println("Failed with exception: " + ex.getLocalizedMessage());
        }
    }

    /**
     * @param args
     * @throws SQLException
     * @throws IOException
     */
    public static void main(String[] args) throws Exception {
        new Main();
    }
}
