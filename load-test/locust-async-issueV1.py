import random
from locust import task, FastHttpUser


class CouponIssueAsyncV1(FastHttpUser):
    connection_timeout = 10.0
    network_timeout = 10.0

    @task
    def issue(self):
        payload = {
            "userId" : random.randint(1, 100_000_000),
            "couponId" : 1
        }
        with self.rest("POST", "/coupons/issue/redis", json=payload):
            pass