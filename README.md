# [팀프로젝트] 맛.JAVA - 맛.ZIP
#### 💡 맛.ZIP은 “진짜 믿고 먹을 수 있는 맛집” 을 공유하는 플랫폼입니다.
* 맛.JAVA 팀은 맛집 탐방에 누구보다 진심인 사람들이 뭉친 팀입니다. 🍔
* 평소에 모두가 겪고 있던 부정한 광고, 믿을 수 없는 후기 속에서 소비자들이 믿고 방문할 수 있는 맛집을 모아 볼 수 있는 사이트의 필요성을 느꼈습니다.
* 그래서, 영수증 2회 이상 인증된 맛집만 등록되도록 해서 신뢰도 및 만족도가 높은 맛집만 선별하여 소비자에게 제공하는 목적으로 개발을 진행했습니다.
* 국내 운영 중인 맛집 추천 사이트, 대형 포털 지도 사이트의 사례 분석을 통해, 웹사이트 기능의 방향성을 "진정성 있는 맛집 공유"로 초점을 맞췄습니다.
* 맛집을 좋아하는 사람들 뿐만 아니라, 맛집을 좋아하는 사람들의 방문을 원하는 요식업계 사장님들도 타켓팅한 사장님 전용 구독 서비스 및 노출 배너 광고를 BM으로 설정했습니다.

<br>

## 1. 제작 기간
#### `2023년 4월 28일 ~ 6월 9일 (1개월)`

<br>

## 2. 사용 기술
### `Back-end`
* Java 8
* Spring Framework 5.0.1, Spring MVC
* Junit5
* Maven
* Mybatis
* Eclipse, Visual Studio Code

### `Front-end`
* HTML
* CSS
* JavaScript
* JQuery 3.6.4
* BootStrap 4.1

### `DevOps`
* AWS EC2, S3, RDS, CloudFront, Route 53, ALB
* Tomcat 8.5
* MySQL 8.0.32

### `Collaboration`
* Git, Sourcetree 
* Slack 


<br>

* #### `[함영휘] 포인트 시스템, 랭킹 시스템`
  * 영수증 등록 시 포인트 적립
  * 적립된 포인트를 사용해서 상품교환(기프티콘) SENS API 사용해서 문자로 전송
  * 등록된 영수증을 카운트해서 많이 등록된 음식점 카테고리별 분류
    
<br>

| <img src ="https://github.com/chujaeyeong/MAT_ZIP_readme_chujy/assets/123634960/3f370f44-47cb-480a-b433-5e885ff4f00d" width="440" height="260" /> | 

<br>


## 3. ERD 설계
<img src="https://user-images.githubusercontent.com/123634960/242927505-6d8c1885-fd63-41a2-84c7-c521fcce39e7.png">

<br>

<br>

<br>

## 4. 핵심 기능 설명 & 트러블 슈팅


<br>

#### [함영휘] 포인트 시스템, 랭킹 시스템
<details>
  <summary>📌 핵심 기능 설명</summary>
	
  ##### `1. OCR을 활용한 영수증 등록 시 포인트 적립`
  * 먼저 OCR을 통한 영수증 등록 로직을 처리하는 DataValidationService에 포인트 적립 로직을 처리하는 PointSaveHistoryService를 @Autowired를 이용해 의존성 주입.
  * (DataValidationService에 있는 로직을 통해 영수증 등록의 가능여부를 판단한 이후 PointSaveHistoryService 로직이 동작하여, 따로 유효성 검사 로직을 사용하지 않았음)
  * PointSaveHistoryService에서는 @Autowired로 PointSaveHistoryDAO를 주입해 메서드 호출.
  * PointSaveHistoryDAO에 주입된 mybatis의 SqlSessionTemplate을 이용해서 pointMapper.xml에 있는 쿼리문을 수행.
  * **‼결과‼** 영수증 등록 시 등록에 성공하면 포인트 적립. DB 테이블에 포인트 내역 저장.

  ##### `2. 네이버 클라우드 SENS API를 활용한 포인트 교환(기프티콘)`
  * 기프티콘 교환 API를 사용하려 했으나 개인의 테스트 용도로 사용이 불가능하여, 네이버 SENS API를 이용해 원하는 상품 선택 시 해당 상품의 이미지를 MMS로 전송해주는 방법사용
    최대한 기존의 기프티콘 교환 방식과 비슷하게 구현.
  * 마이페이지에서 포인트 교환 페이지로 이동 -> 원하는 상품 선택 -> PointExchangeHistoryController에 Service 레이어 호출(세션에 저장된 user_id와 AJAX의 요청 data를 매개변수 전달)
  * Service 레이어에서 보유 포인트를 확인 후 상풍의 가격과 비교해서 보유 포인트가 적을 시 예외처리를 했습니다.
  * 보유 포인트를 확인 후 사용한 포인트를 DB에 저장하고, SENS API를 통해 MMS를 전송하게 됩니다.
  * @Transactional을 통해 예외 발생시 포인트 내역에 저장되기 전으로 롤백하도록 처리했습니다.(root-context에 Exception 설정을 추가해서 모든 예외 발생시 롤백되도록 설정했습니다)
  * **‼결과‼** 보유 포인트가 충분하고, 상품 교환에 성공 시 팝업창을 통해 결과를 알려주고, 회원의 핸드폰번호로 MMS가 전송되게 됩니다.  
	
  ##### `3. 포인트 상세이력 관리`
  * 배민 우아한기술블로그 참고(https://techblog.woowahan.com/2587/) 도메인 로직을 참고해서 설계했습니다.
  * 적립을 할 때 Insert만 존재하는 도메인 모델로 구현하였습니다.
  * 포인트를 사용하고 남은 포인트의 유효기간이 만료되면, 만료된 포인트만 처리해야 하는데 단순한 Insert 모델에서는 처리가 어려워 상세 테이블을 추가하였습니다.
  * 포인트 적립 시 상세 테이블에도 적립 기록이 저장되며, 사용 시 저장된 적립 이력을 큐(Queue)를 이용해서 빠른 시간순으로 정렬된 적립 기록을 불러옵니다.
  * poll을 이용해 List에 저장된 포인트를 상품의 가격과 비교하여 다시 상세 테이블에 저장하고, 상품의 가격이 0원이 되면 종료되는 로직을 구현했습니다.
  * 유효기간만료 이벤트가 발생하면 테이블의 적립아이디를 기준으로 GROUP BY해서 남은 금액을 만료 처리 하면됩니다.
  * 이렇게 하면 기존의 update 로직보다 상세한 이력관리가 가능합니다.
	
* [👉포인트 교환 테스트 코드](https://github.com/Quokka2/codingtest/assets/99588377/d5c3f440-e121-4548-8a4d-b5823283fcc0)
	
 ##### `4. 랭킹 시스템(또슐랭 가이드)`
 * mz_member, restaurant, mzregisterinfo, mzlist 테이블을 각각 JOIN해서 ranking 테이블에 insert
 * ranking 테이블의 컬럼 (mzlist.no, mzregisterinfo.user_id, restaurant.name, mz_member.gender, mzlist.img)
 * mzregisterinfo 테이블 + restaurant 테이블의 tel 컬럼이 같은 restaurant의 name 컬럼 JOIN
 * restaurant 테이블 +  mzlist 테이블의 tel 컬럼이 같은 mzlist의 업체등록번호, 이미지 파일 이름 컬럼 JOIN
 * mzregisterinfo 테이블 + mz_member 테이블의 user_id 컬럼이 같은 user_id 컬럼 JOIN
 * 이벤트 스케줄러(Event Scheduler)를 생성해 매일 자정에 ranking 테이블에 영수증 등록된 업체 정보를 insert하는 SQL문 실행
 * **‼결과‼** ranking 테이블에 있는 데이터를 카테고리별 select해서 메인 페이지 화면에 출력
 * <details>
	<summary>👉코드확인</summary>
	<div markdown="1">
		
	 ```sql
		#ranking 테이블 저장 Event Scheduler
		CREATE EVENT daily_update_ranking
		ON SCHEDULE EVERY 1 DAY STARTS CURDATE() + INTERVAL 1 DAY
		DO
   		INSERT INTO multi.ranking (no, user_id, name, gender, img)
   		SELECT mzlist.`no` , mzregisterinfo.user_id, restaurant.name, mz_member.gender, mzlist.img
   		FROM multi.mzregisterinfo
   		INNER JOIN multi.restaurant ON mzregisterinfo.storePhoneNumber = restaurant.tel
   		INNER JOIN multi.mzlist ON restaurant.tel = mzlist.tel
   		INNER JOIN multi.mz_member ON mzregisterinfo.user_id = mz_member.user_id;
		#카테고리별 5개 리스트 select
		select `no`, name, img, count(name) as total from matzip.ranking
		group by `no`, name, img
		order by total desc
		limit 5;

		select `no`, name, img, count(name) as total from multi.ranking
		where gender = '여'
		group by `no`, name, img
		order by total desc
		limit 5;

		select `no`, name, img, count(name) as total from multi.ranking
		where gender = '남'
		group by `no`, name, img
		order by total desc
		limit 5;
	 ```
	
	</div>
	</details>
</details>

<details>
  <summary>⚽ 트러블 슈팅</summary>

<br>
	
  ##### `1. 포인트 교환 예외발생 시 트랜잭션(transaction) 롤백 미작동`
  * @Transactional exChange메소드 내부 checkPoint(), insertUsePoint(), sendSms() 메서드 Exception 발생 시 기존 DB에 저장된 데이터를 롤백
  * 첫 번째 시도 : sendSms() 메서드에 테스트용 런타임 에러 적용(DB에 저장된 후 동작하는 메서드) DB 롤백 확인  -> ❌롤백 미작동
  * 두 번째 시도 : root context에 트랜잭션 Bean 추가. -> ❌롤백 미작동
  * 세 번째 시도 : cglib 라이브러리를 추가 / servlet context에 proxy-target-class 속성 추가 -> ⭕정상작동!
	* Spring AOP 사용. 인터페이스를 사용하나 proxy-target-class 속성을 사용하기 위해 cglib 라이브러리 추가
	* DB설정을 root context 쪽에 할 경우, 이 DB설정은 servlet context쪽에 설정된 Bean들에는 적용이 안된다.
	* transaction관련 설정을 servlet context에 해줘야한다.
<details>
  <summary>👉코드확인</summary>

  <div markdown="1">    

  ```java
	@Override
	@Transactional
	// 적립 포인트 상품 교환 비즈니스 로직
	public void exChange(String user_id, int id) {

		PointSaveHistoryVO userpoint = PointExchangeHistoryDAO.pointsaveFind(user_id);
		ProductPointVO product = PointExchangeHistoryDAO.productPointFind(id);

		try {
			// 보유 포인트 확인 메서드
			checkPoint(userpoint, product);
			// 보유 포인트 확인 후 교환된 포인트 INSERT
			insertUsePoint(userpoint, product);
			// 교환된 기프티콘 이미지 MMS 전송
	  		sendSms(userpoint, product);
	  
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	// MMS 전송(네이버 SENS API)
	private void sendSms(PointSaveHistoryVO userpoint, ProductPointVO product) {
	  
		SendSmsVO sms = new SendSmsVO();
		sms.setUser_id(userpoint.getUser_id());
		sms.setImg(product.getImg());
		//sms.setTel(tel);
	  
		sensapi.sendSMS(sms);
	  
		throw new IllegalArgumentException("메세지 전송 오류");
	}
  ```
  </div>
</details>
</details>

<br>


<br>

</details>


