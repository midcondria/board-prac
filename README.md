## 게시판 API 연습용
- 프론트엔드 연습할때도 API 용으로 쓸 수 있을듯
- application.yml 파일은 추가하지 않았으므로 JWT 토큰을 이용하려면 <br/>
  yml 파일에 @ConfigurationProperties(prefix = "midcon") 를 위한 설정 필요 <br/>
  혹은 AppConfig 에서 jwtKey 설정 변경
- 구현된 기능
  - 회원가입
    - 기본적인 비밀번호 암호화 적용
  - 로그인
    - 로그인 시 JWT 발급까지 구현
    - 인가를 원하는 메서드에 @Login 애노테이션 적용 시 JWT를 이용한 인가 과정을 거치도록 구현 <br/>
      ->  ArgumentResolver 이용
  - 게시글 CRUD
    - 작성
    - 조회
      - 간단한 페이징 처리 완료 (페이지 당 게시글 기본값 10)
    - 수정
    - 삭제
  - 예외처리
    - CRUD 및 회원가입 / 로그인에 필요한 기본적인 커스텀 예외 처리 완료
