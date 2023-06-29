About the API

- This is an API for a simulation of a lending application.
- In this API, a customer is registered, and given a random loan limit which is a config in the application.properties where a range of limits are declared.
- Customer can fetch their loan limit using they phone number.
- Customer can request a loan, which is withing their limit;
- Customer can repay a loan partially and can borrow upto their available limits.
- Customer specify the repayment period which are configs in application.properties.
- The payment reference is the loan id that can ge gotten when customer request for their loans
- A cron jobs runs every day at 12:10 am to update the status of the loan and the remaining days.
- Another cron job runs every day at 12:30 am to clear/sweep the defaulted loans
- Another cron job runs every 30 seconds to send sms to the customer upon repayment or borrowing of the loan.

Below are the available endpoints
- Create customer -> {ip:port}/customers/api/v1/register-customer
- Get customer loan limit -> {ip:port}/customers/api/v1/get-customer-limit?msisdn={mobileNumber}
- Get customer loans -> {ip:port}/loan/api/v1/get-loans?msisdn={mobileNumber}
- Request loan -> {ip:port}/loan/api/v1/loan-request
- Repay loan -> {ip:port}/loan/api/v1/loan-repayment

There is attached postman collection for the same that can be imported to a postman for testing.


How to run the application.

- Clone the project from github your local machine by running git clone https://github.com/KIbanyu/lending-api.git
- Open the directory using the editor of your choice.
- Open terminal on the project directory and run mvn test to run unit tests
- Click the run button to run the application and open http://localhost:8083/swagger-ui/index.html in your browser.
