/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Service;

import connection.EndPoint;
import interfaces.ProducerDelegate;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.SerializationUtils;

/**
 *
 * @author emilgras
 */
public class Producer extends EndPoint {

    private ProducerDelegate delegate;

    public Producer(String endPointName, String aggregator, ProducerDelegate delegate) throws IOException, TimeoutException {
        super(endPointName,aggregator);
        this.delegate = delegate;
    }

    public void sendMessage(Serializable object) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    channel.basicPublish("", endPointName, null, SerializationUtils.serialize(object));
                    channel.basicPublish("", aggregator, null, SerializationUtils.serialize(object));
                    delegate.didProduceMessageWithOptionalException(null);
                } catch (IOException ex) {
                    Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
                    delegate.didProduceMessageWithOptionalException(ex);
                }
            }
        });
        t.run();
    }
}
