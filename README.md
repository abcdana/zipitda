<img width="1506" alt="스크린샷 2025-03-06 오후 10 43 15" src="https://github.com/user-attachments/assets/2709bef6-708c-4117-ad43-76ebbb4c2586" />


# Zipitda 집잇다

집잇다(zipitda)는 **오늘의집**을 모티브로 한 **콘텐츠와 커뮤니티, 스토어, 전문가 시공 서비스**를 제공하는 인테리어 플랫폼입니다.

사용자는 집 꾸미기 노하우(카드 포스팅)를 공유하고, 인테리어 상품을 구매하며, 전문가와 상담할 수 있습니다.

---

## **주요 기능**
- **콘텐츠 및 커뮤니티**  
  - 사용자들이 인테리어 콘텐츠를 공유  
  - 댓글, 좋아요, 북마크 기능 지원  

- **스토어 (커머스)**  
  - 인테리어 가구 및 소품 검색 및 구매 가능  

- **전문가 시공 서비스**  
  - 인테리어 전문가와 상담 및 견적 요청  

---

## **기술 스택**
### **Backend**
| 기술  | 설명 |
|-------|------|
| **Java 21** | 최신 LTS 버전, 성능 및 보안 최적화 |
| **Spring Boot 3.x** | REST API 개발 |
| **Spring Security + JWT** | 사용자 인증 및 권한 관리 |
| **JPA (Hibernate) + MySQL** | ORM 기반 데이터 관리 |
| **Redis** | 세션 및 토큰 관리 |
| **Thymeleaf** | 이메일 템플릿 렌더링 (테스트용) |

### **Infra & DevOps**
| 기술  | 설명 |
|-------|------|
| **Docker** | 컨테이너 기반 환경 구성 |
| **AWS (EC2, RDS, S3, CloudFront)** | 클라우드 배포 (예정) |
| **CI/CD (GitHub Actions)** | 자동 빌드 및 배포 (예정) |

---

## **API 문서**
Swagger API 문서가 제공됩니다.
```
http://localhost:8081/swagger-ui/index.html
```
