export default {
	producer: {
		type: 'fanout',
		// exchange: 'LoanBroker9.TingGodRabbitMQBank',
		exchange: 'cphbusiness.bankJSON',
		replyTo: 'LoanBroker9.banks_out'
	},
	consumer: {
		type: 'direct',
		exchange: 'LoanBroker9.getRecipients_out',
		binding: 'bank-jyske-bank'
	},
	connection: {
		host: 'datdb.cphbusiness.dk',
		port: '15672',
		username: 'student',
		password: 'cph'
	}
};