# 선착순 쿠폰 발급 시스템 
<br> 
<img width="397" alt="스크린샷 2024-05-12 오전 2 42 12" src="https://github.com/Jungsu-lilly/Coupon-Service/assets/56336436/2ad6eee7-ad57-4e9b-b8d8-b6bd171703f7">
<br>

### 🧑‍💻 프로젝트 소개
- 이벤트로 한정된 수량의 쿠폰을 선착순으로 제공하는 서비스
- 단기간 대용량 트래픽이 몰릴 때를 대비해 설계하였습니다.
<br> 

### 기술 스택
- SpringBoot, Java 17, MySQL
- Locust
- CloudFront, Prometheus + Grafana 로 모니터링
<img width="392" alt="스크린샷 2024-05-12 오전 2 32 54" src="https://github.com/Jungsu-lilly/Coupon-Service/assets/56336436/23597d99-806a-4cb0-8f2b-2cad6c101bd1">

<br><br> 

### 시스템 구조
<img width="749" alt="스크린샷 2024-05-12 오전 2 52 22" src="https://github.com/Jungsu-lilly/Coupon-Service/assets/56336436/c2c13aa1-a249-4387-abed-d6fbade6f6ad">

<br> <br>


### Trouble-Shooting & 성능 개선
- 분산 락을 통한 동시성 이슈 해결
  - https://matt1235.tistory.com/73
<br>

- 병목 지점을 찾아, rps 를 약 5배 향상시켰습니다.
  - https://matt1235.tistory.com/75
<br>

- 약 29만 건의 요청에 대하여 Failure 는 90건 → 0.03% 실패율
- 최대 2000명의 유저가 동시에 요청을 보내도, 99.97% 확률로 성공하는 서버를 구축했습니다.
