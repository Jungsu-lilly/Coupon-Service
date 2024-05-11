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

- Reids를 채택해 비동기 구조로 서버를 구현,
- 빠른 연산을 통해 성능을 향상시켰습니다.
<br> 

### Trouble-Shooting & 성능 개선
- 분산 락으로 동시성 이슈를 해결했습니다.
- 링크 : https://matt1235.tistory.com/73

- 병목 지점을 찾아, 서버 RPS를 5배 향상 시켰습니다.
- 링크 : https://matt1235.tistory.com/75
