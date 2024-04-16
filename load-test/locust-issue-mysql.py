import random
from locust import task, FastHttpUser


class CouponIssueMysql(FastHttpUser):
    connection_timeout = 10.0
    network_timeout = 10.0

    @task
    def issueMysql(self):
        payload = {
            "userId" : random.randint(1, 100_000_000),
            "couponId" : 1
        }
        with self.rest("POST", "/coupons/issue/mysql", json=payload):
            pass