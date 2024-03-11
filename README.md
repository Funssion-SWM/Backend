# 인포럼 (Inforum)

<p align='center'><img width="40%" src="https://github.com/Funssion-SWM/Frontend/assets/68095803/7ded1a36-0616-40d2-9f7d-c756fdee7a62"/></p>

## About the Project

개발 기간 : 2023.06 ~ 2023.11  
개발 인원 : 3명 (FE 1, BE 2)  
배포 링크 : https://www.inforum24.com

생성형 AI를 활용한 텍스트 자동 생성 기능, 채용 도우미 서비스 등 다양한 기능을 제공하는 통합 개발자 커뮤니티 서비스

<p align='center'><img width="75%" src="https://github.com/Funssion-SWM/Frontend/assets/68095803/91849d33-7942-4299-9f6a-48b732d64e21"/></p>

## Stacks

### FE

<img src="https://img.shields.io/badge/typescript-3178C6?style=for-the-badge&logo=typescript&logoColor=white"> <img src="https://img.shields.io/badge/next.js-000000?style=for-the-badge&logo=next.js&logoColor=white"> <img src="https://img.shields.io/badge/react-61DAFB?style=for-the-badge&logo=react&logoColor=white"> <img src="https://img.shields.io/badge/tailwind-F0F0F0?style=for-the-badge&logo=tailwindcss&logoColor=#38BDF8">

### BE

<img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"> <img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white"> <img src="https://img.shields.io/badge/spring boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/postgresql-4169E1?style=for-the-badge&logo=postgresql&logoColor=white">

### DevOps

<img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"> <img src="https://img.shields.io/badge/aws-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white"> <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white"> <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"> <img src="https://img.shields.io/badge/github actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white">

## </br> 아키텍쳐
![image](https://github.com/Funssion-SWM/Backend/assets/95288696/e6c2a4da-15ad-47e1-8179-c890afe138f2)

## ERD
### Member (회원 관련 Schema)
- - -
![image](https://github.com/Funssion-SWM/Backend/assets/95288696/e5b2c6ed-7535-4f36-a4c0-c7769acaa976)


### Post (게시글 관련 Schema)
- - -
![image](https://github.com/Funssion-SWM/Backend/assets/95288696/6bf3a6ad-c525-47b4-ad9d-081cd3ac6063)

## Etc. (채용 프로세스 Schema(employer,interview), 점수 Schema, Tag Schema)
- - -
![image](https://github.com/Funssion-SWM/Backend/assets/95288696/cbf76cfd-47d4-4367-b157-aba644079040)


## 화면 구성

|                                                               랜딩 페이지                                                                |                                                                  로그인                                                                   |
| :--------------------------------------------------------------------------------------------------------------------------------------: | :---------------------------------------------------------------------------------------------------------------------------------------: |
|   <img width="90%" alt="landing" src="https://github.com/Funssion-SWM/Frontend/assets/68095803/1302d935-404b-4dd0-8ec0-6de3550faeda">    |     <img width="90%" alt="login" src="https://github.com/Funssion-SWM/Frontend/assets/68095803/c60df2bb-0a45-4592-ae34-17b4ecfb774c">     |
|                                                                 회원가입                                                                 |                                                                  시리즈                                                                   |
|    <img width="90%" alt="signup" src="https://github.com/Funssion-SWM/Frontend/assets/68095803/43a4535a-6890-4db2-bc82-2e42022e93bf">    |    <img width="90%" alt="series" src="https://github.com/Funssion-SWM/Frontend/assets/68095803/f34711ed-fa95-471e-b1de-cdfc4cde797f">     |
|                                                                   메모                                                                   |                                                                    Q&A                                                                    |
|     <img width="90%" alt="memo" src="https://github.com/Funssion-SWM/Frontend/assets/68095803/e0430d0c-2847-4008-bb15-1c5df5d807b1">     |      <img width="90%" alt="q a" src="https://github.com/Funssion-SWM/Frontend/assets/68095803/9ecd3fb1-5ece-4c03-866b-af160ddffbbb">      |
|                                                               시리즈 상세                                                                |                                                                 메모 상세                                                                 |
| <img width="90%" alt="seriesDetail" src="https://github.com/Funssion-SWM/Frontend/assets/68095803/6f9638e9-47cd-46b6-b9f3-5ed0aafe1153"> |  <img width="90%" alt="memoDetail" src="https://github.com/Funssion-SWM/Frontend/assets/68095803/80acc8ae-0862-42d2-abca-b5b09f54a678">   |
|                                                                 Q&A 상세                                                                 |                                                                시리즈 작성                                                                |
|  <img width="90%" alt="q aDetail" src="https://github.com/Funssion-SWM/Frontend/assets/68095803/afba37ee-70ad-4735-a631-0ca4f5155b89">   | <img width="1410" alt="createSeries" src="https://github.com/Funssion-SWM/Frontend/assets/68095803/c6958f3c-bbf7-4039-a7f1-e4b21dace730"> |
|                                                                메모 작성                                                                 |                                                                 Q&A 작성                                                                  |
|  <img width="90%" alt="createMemo" src="https://github.com/Funssion-SWM/Frontend/assets/68095803/099fb504-dc7d-4193-b5b0-5c1d99df6954">  |   <img width="90%" alt="createQ a" src="https://github.com/Funssion-SWM/Frontend/assets/68095803/bb5bc50f-b327-4eaa-b1d7-4018a4d4cb0b">   |
|                                                                마이페이지                                                                |                                                              자기소개서 작성                                                              |
|    <img width="90%" alt="mypage" src="https://github.com/Funssion-SWM/Frontend/assets/68095803/98e4176f-ad36-4194-b3a9-72ea69605851">    |     <img width="90%" alt="intro" src="https://github.com/Funssion-SWM/Frontend/assets/68095803/9cf8bade-0a6a-4238-b26f-34f050185c1c">     |
|                                                               채용 도우미                                                                |                                                               채용자 페이지                                                               |
|     <img width="90%" alt="job1" src="https://github.com/Funssion-SWM/Frontend/assets/68095803/9c456eea-4b7e-42ad-916c-3f2d22a5599f">     |     <img width="90%" alt="job2" src="https://github.com/Funssion-SWM/Frontend/assets/68095803/24280746-5338-43cf-8cef-b084125b28ad">      |

## 주요 기능

### 1. 컨텐츠 생산성 향상을 위한 기능

- 생성형 AI를 활용한 텍스트 자동 생성
- 생성형 AI를 활용한 요약, 태그 자동 생성
- Notion 형식의 블럭 기반 웹에디터
- 메모를 여러 개 모아둘 수 있는 시리즈

### 2. 컨텐츠 생산 동기 부여를 위한 기능

- 등급 제도 (브론즈, 실버, 골드, 플래티넘, 다이아, 인피니티)
- 작성 기록을 UI로 확인할 수 있는 Post history
- 채용 도우미 서비스
  - 자기소개서 작성
  - 구직자 추천
  - 미니 면접

### 3. 일반 기능

- 관심 기술 분야를 한 눈에 확인할 수 있는 마이페이지
- 커뮤니티 기능(댓글, 좋아요, 검색, 팔로우/팔로잉)
- 태그 기반 게시물 추천

## 개발 현황

MVP 개발완료 및 배포 (23.08.14)  
사용성 테스트 수행 (23.08.15 ~ 23.08.22)  
피드백 반영 (23.08.23~)  
배포 2 (23.10.11)  
배포 3 (23.10.23)  
마케팅 (23.10.25 ~ 23.11.12)  
배포 4 (23.11.07)  
리팩토링 및 성능 개선 (11.07~)
