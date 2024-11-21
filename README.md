# spring-plus 프로젝트

### 프로젝트 개요
이 프로젝트는 Spring Security, JPA, JWT, QueryDSL 등을 사용하여 안전하고 효율적인 일정 관리를 목표로 합니다.
또한 다양한 최신 기술을 활용해 데이터베이스 성능을 최적화하고 확장성을 고려한 설계를 적용했습니다.

### 주요 기능

1. **할 일 관리**
   - 할 일을 생성, 수정, 삭제하는 기능을 제공합니다.
   - 할 일을 생성한 사용자가 자동으로 담당자로 등록됩니다. (`cascade PERSIST`)
   - 할 일 검색 시 날씨 조건과 수정일 기준으로 검색이 가능합니다. (JPQL 사용)

2. **JWT에 닉네임 정보 추가**
   - JWT를 사용하여 인증/인가를 구현했습니다. 사용자 정보를 JWT 토큰에 포함하며, `nickname` 정보를 새로 추가했습니다.

3. **스케줄 검색 최적화**
   - 일정 검색 기능은 QueryDSL을 사용하여 구현하였고, N+1 문제를 해결하기 위해 `@EntityGraph`와 `fetchJoin`을 사용했습니다.

4. **매니저 등록 요청 처리**
   - 매니저 등록 요청 시 로그를 남기는 기능이 있으며, 항상 새로운 트랜잭션을 시작하도록 `@Transactional`의 Propagation.REQUIRES_NEW 속성을 적용했습니다.

5. **사용자 역할 관리**
   - 사용자 역할 변경 기능이 있으며, 이 과정에서 `@Before` 어노테이션을 활용하여 `UserAdminController`의 `changeUserRole` 메소드 실행 전 특정 동작을 수행하도록 설정했습니다.

6. **성능 최적화**
   - 100만 건의 사용자 데이터를 벌크로 생성하는 작업을 JDBC를 통해 성공적으로 구현했습니다.
   - 사용자 검색 시 `nickname`을 기준으로 조회하는 경우 성능을 개선하기 위해 `nocache`를 이용했습니다.

7. **트랜잭션 관리**
   - `saveTodo` 메서드는 `@Transaction`으로 관리하여 필요한 경우만 트랜잭션을 활성화하도록 하였습니다.

8. **Spring Security 통합**
   - 프로젝트는 Spring Security를 사용하여 인증과 인가를 관리하며, `JwtFilter`를 통해 사용자 인증을 처리합니다. 인가 과정은 별도의 `Handler`가 수행합니다.
   - `build.gradle` 파일에서 Spring Security와 QueryDSL 관련 의존성을 추가했습니다.

### 기술 스택
- **Backend**: Java 17, Spring Boot, JPA, QueryDSL, Spring Security, JWT
- **Database**: MySQL, JDBC
- **Build Tool**: Gradle
- **Logging & Monitoring**: `@Transactional`을 활용한 트랜잭션 관리, Propagation 설정 등

### 향후 개선 사항
- 추가적인 최적화 작업을 통해 대규모 데이터셋에 대한 검색 성능을 높일 예정입니다.(Docker, Redis 사용 예정)
- AWS의 EC2, RDS, S3를 이용하여 외부에서도 접속할 수 있고, 이미지 업로드가 가능한 API 구현 등 실제 구동이 가능한 데이터베이스를 구현할 예정입니다.

