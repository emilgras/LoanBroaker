export default function (jsonObject) {

	let ssn = jsonObject.ssn || '';
	let creditScore = jsonObject.creditScore || '';
	let loanAmount = jsonObject.loanAmount || '';
	let loanDuration = jsonObject.loanDuration || 365;

	let formattedJson = {
		ssn,
		creditScore,
		loanAmount,
		loanDuration
	};

	return formattedJson;
}