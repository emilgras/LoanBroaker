import rabbitmq from '../config/rabbitmq.js';
import translator from './translator';

export default function (ampqConn, message) {

	let replyTo = rabbitmq.producer.replyTo;

	ampqConn.createChannel((err, ch) => {
		if (err) {
			ampqConn.close();
			console.error('[AMPQ] connection error - closing; ', err);
		}

		let type = rabbitmq.producer.type;
		let exchange = rabbitmq.producer.exchange;
		let headers = {
			type: 'json'
		};

		ch.assertExchange(exchange, type, {
			durable: false
		});

		//	Example of a message correctly formatted and ready to be send to cphbusiness.bankjson
		//var newMessage = {'ssn':1605789787, 'creditScore':749, 'loanAmount': 10.0, 'loanDuration': 360};
		let jsonObject = JSON.parse(message.content.toString());
		let formattedObject = translator.getFormattedJson(jsonObject);

		ch.publish(exchange, '', Buffer.from(JSON.stringify(formattedObject)), {
			headers: headers,
			replyTo: replyTo
		});
		console.log('Successfully sent message');
	});
}