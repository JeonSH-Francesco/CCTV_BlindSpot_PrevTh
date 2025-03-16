# CCTV_BlindSpot_PrevTh

## 개발 동기
![image](https://github.com/JeonSH-Francesco/CCTV_BlindSpot_PrevTh/assets/112309895/8672fc78-5ff5-4e0e-b7a7-e7d4ad3eebbe)

## 구성도
![image](https://github.com/JeonSH-Francesco/CCTV_BlindSpot_PrevTh/assets/112309895/7b5dfa6c-a376-4d11-8d0b-45c4de119df9)

## 구현

## 1. 공공 데이터 기반의 CCTV 위치 추출 및 데이터 연동
![image](https://github.com/JeonSH-Francesco/CCTV_BlindSpot_PrevTh/assets/112309895/2423b18d-abc8-4476-b098-e5f468ef7e00)

![image](https://github.com/JeonSH-Francesco/CCTV_BlindSpot_PrevTh/assets/112309895/ecb88379-bd95-4872-9a07-0b82e6f3536f)

![image](https://github.com/JeonSH-Francesco/CCTV_BlindSpot_PrevTh/assets/112309895/34f35628-c566-463d-a8c6-a3d22ee34729)

## 2. 사용자의 실시간 위치 파악 및 CCTV 사각지대 주기적 판단 -> Haversine 공식 적용
![image](https://github.com/JeonSH-Francesco/CCTV_BlindSpot_PrevTh/assets/112309895/07a15ef6-6b45-4261-9753-119534eeefa6)

![image](https://github.com/JeonSH-Francesco/CCTV_BlindSpot_PrevTh/assets/112309895/f75e9f53-d28d-417a-bf31-ce5ce2cd4b88)

## 3. 음성 인식 기능을 통한 위험 상황(특정 단어 : "살려줘") 탐지 가능

![image](https://github.com/JeonSH-Francesco/CCTV_BlindSpot_PrevTh/assets/112309895/d3bf5b4e-0bc5-477b-9a5f-69d612e64049)

## 4. 위험 상황 탐지 후 지정된 번호로 자동 전화 연결 및 메시지 자동 전송 기능 구현

![image](https://github.com/JeonSH-Francesco/CCTV_BlindSpot_PrevTh/assets/112309895/5d38c412-810f-4a9a-8921-cd8fb3164652)

![image](https://github.com/JeonSH-Francesco/CCTV_BlindSpot_PrevTh/assets/112309895/5fdd1496-e1a0-4ac4-9c9a-68ccfbff6bb5)
## 5. 위험 상황과 관련된 증거 자료 확보

![image](https://github.com/JeonSH-Francesco/CCTV_BlindSpot_PrevTh/assets/112309895/d1b5d02d-43c6-4e73-996c-a9f4383c2842)

## 시현 영상


https://github.com/user-attachments/assets/0e839d66-5f98-4175-b390-d75f9fafc6fc



### R&R
전 승 호
(주 제안자)
-> 공공 데이터 전 처리 작업(O)
-> 액티비티와 Intent 세부 작업 구성(O) 
-> 사용자 위치의 위도 경도 파악 도움+Update 작업( O )
-> CCTV 사각지대 판별과 사용자의 위치에 따른 탐지상태 및 알림 작업(O)
-> 일정 시간 마다 주기적으로 탐지버튼 Handler를 통해 앱 활성화 작업(O)
-> 일정 기준이 넘어가면, 소리 및 진동기능으로 사용자와 contact 작업(O)
-> PPT 및 논문작성(O)

권 오 준
-> 공공데이터를 이용한 CCTV 위치 위도 경도 파악하여 지도 상에 마킹 작업 ( O)
-> 사용자 위치의 실시간 위도 경도 파악 Update 작업 도움( O )
-> CCTV 탐지 반경 표시 작업 및 마커 타이틀, 이미지 패치작업 (O)
-> 연락처를 저장할 수 있도록 Room DB 구현 (O)
-> 음성 인식 후 텍스트 변환, 위험상황 단어 탐지 후 전화 및 메시지 전송 기능(O)  
-> 음성 인식 종료 후 녹음 기능 전환, 일정 시간 녹음 기능 활성화 (O)
-> 녹음 종료 후 이메일로 녹음 파일 전송 + UI 구성 + 스크린 샷 도움 (O)


유 현 종
->액티비티와 Intent 세부 작업 구성 도움(O)
->메시지 설정에서 연락처 연동 작업 (O)
-> 앱 사용 및 녹음 알림 작업(O)
-> 현재위치가 담긴 구글 맵 API 스크린샷 저장 (o)
-> 스크린 샷 메일 전송기능 작업 (O)

</br>
--> 2022.07.13 제 11회 대학생 작품 경진대회 동상 수상

### 기대효과
![image](https://github.com/JeonSH-Francesco/CCTV_BlindSpot_PrevTh/assets/112309895/21493555-cf77-43c3-ae93-b270ec1bf487)








