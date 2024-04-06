from locust import task, FastHttpUser

class CouponIssuePerformanceTest(FastHttpUser):
    connection_timeout = 10.0
    network_timeout = 10.0

    @task
    def hello(self):
        payload = {
            "userId"
        }