
package app;

import com.rabbitmq.client.AMQP.BasicProperties;
import connection.EndPoint;
import interfaces.ProducerDelegate;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.SerializationUtils;

public class Producer extends EndPoint {

    private ProducerDelegate delegate;

    public Producer(String endPointName, ProducerDelegate delegate) throws IOException, TimeoutException {
        super(endPointName);
        this.delegate = delegate;
    }

    public void sendMessage(Serializable object, String replyTo) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BasicProperties props = new BasicProperties
                            .Builder()
                            .replyTo(replyTo)
                            .build();

                    channel.basicPublish("", endPointName, props, SerializationUtils.serialize(object));
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
