package main;

import app.Producer;
import app.QueueConsumer;
import app.ReciepientListService;
import interfaces.ConsumerDelegate;
import interfaces.ProducerDelegate;
import interfaces.ReciepientListServiceDelegate;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.json.simple.JSONObject;

public class Main implements ConsumerDelegate, ProducerDelegate {

    private QueueConsumer consumer;
    private Producer producer;
    private ReciepientListService service;

    public Main() throws Exception {
        consumer = new QueueConsumer("LoanBroker9.getBanks_out", this);
        producer = new Producer("LoanBroker9.getRecipients_out", this);
        service = new ReciepientListService();

        Thread consumerThread = new Thread(consumer);
        consumerThread.start();
    }

    @Override
    public void didConsumeMessageWithOptionalException(HashMap application, IOException ex) {
        if (ex == null) {
            JSONObject applicationJson = service.DistributeLoan(application);
            System.out.println("banks: " + ((ArrayList<HashMap>) application.get("banks")).size());
            int durationInYears = (int)applicationJson.get("loanDuration");
            for (HashMap bank : (ArrayList<HashMap>) application.get("banks")) {

                String bankId = (String) bank.get("bankId");
                if (bankId.equals("bank-lån-and-spar")) { // XML (cphbusiness)

                    
                    
                    Instant instant = Instant.ofEpochSecond(durationInYears * 365 * 24 * 60 * 60);
                    Date myDate = Date.from(instant);
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.S z");
                    String formattedDate = formatter.format(myDate);
                    applicationJson.put("loanDuration", formattedDate);
                    System.out.println("DAAAAAAAAATE BABY : " + formattedDate);
                    // duration format = 1972-01-01 01:00:00.0 CET

                }
                if (bankId.equals("bank-jyske-bank")) { // JSON (cphbusiness)
                    System.out.println("herea");
//                    bank.put("exhange", "cphbusiness.JSONBank");
                    // duration format = days --> 360

                    System.out.println("mama...");
                    int durationInDays = durationInYears * 360;
                    
                    applicationJson.put("loanDuration", durationInDays);
                    System.out.println("DAAAAAAAAATE BABY (in days) : " + durationInDays);
                    
                }
                if (bankId.equals("bank-nordea")) {
                }

                System.out.println("*** sending ***");
                producer.sendMessage(applicationJson, bank, (String) bank.get("bankId"));

            }
        } else {
            System.out.println("{didConsumeMessageWithOptionalException} Failed with exception: " + ex.getLocalizedMessage());
        }
    }

    @Override
    public void didProduceMessageWithOptionalException(IOException ex) {
        if (ex == null) {
            System.out.println("success");
        } else {
            System.out.println("{didProduceMessageWithOptionalException} Failed with exception: " + ex.getLocalizedMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        new Main();
    }

}
