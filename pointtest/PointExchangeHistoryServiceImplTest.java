package com.mat.zip.point;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.mat.zip.point.Exchange.PointExchangeHistoryDAO;
import com.mat.zip.point.Exchange.PointExchangeHistoryVO;
import com.mat.zip.point.save.PointSaveHistoryVO;

@ExtendWith(SpringExtension.class)
//생성된 스프링 컨테이너에 스프링 빈을 추가하기 위해서는 context같은 설정 파일을 읽어야 하는데, 이런 설정파일을 로드하는 어노테이션
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring/root-context.xml", "file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml" })
//Spring Framework에서 애플리케이션 컨텍스트의 웹 버전을 생성하는 데 사용되는 클래스 레벨 어노테이션
@WebAppConfiguration 
public class PointExchangeHistoryServiceImplTest {

	@Autowired
	private PointExchangeHistoryDAO PointExchangeHistoryDAO;

	@Test
	@Transactional
	@DisplayName("상품권 교환 로직(보유한 포인트가 상품금액보다 많으면 교환성공")
	public void testExChange() {
		// given(준비): 어떠한 데이터가 준비되었을 때
		String user_id = "home";
		int id = 1;
		PointSaveHistoryVO bag = new PointSaveHistoryVO();
		ProductPointVO bag2 = new ProductPointVO();
		// when(실행): 어떠한 함수를 실행하면
		PointSaveHistoryVO vo = PointExchangeHistoryDAO.pointsaveFind(user_id);
		ProductPointVO vo1 = PointExchangeHistoryDAO.productPointFind(id);
		// then(검증): 어떠한 결과가 나와야 한다.
		System.out.println(vo);
		System.out.println(vo1);

		// x(예상 값)와 y(실제 값)가 같으면 테스트 통과
		assertEquals(6000, vo.getSumpoint());
		assertEquals("home", vo.getUser_id());
		assertEquals(4600, vo1.getPoint());

		bag.setSumpoint(vo.getSumpoint());
		bag2.setPoint(vo1.getPoint());

		bag.setUser_id(vo.getUser_id());
		bag2.setPoint(vo1.getPoint());

		// 실제 비즈니스 로직을 최대한 구현해서, 메서드 성공 시 테스트 통과
		executeCheckPoint(bag.getSumpoint(), bag2.getPoint());
		executeInsertUsePoint(bag.getUser_id(), bag2.getPoint());
	}

	private void executeCheckPoint(int bag, int bag2) {
		checkPoint(bag, bag2);
	}

	private void executeInsertUsePoint(String bag, int bag2) {
		insertUsePoint(bag, bag2);
	}

	// 포인트 비교하는 로직 직접 테스트하려고 작성
	@Test
	@Transactional
	@DisplayName("보유 포인트 / 상품 가격 비교하는 로직")
	public void testCheckPoint() {
		// given(준비): 어떠한 데이터가 준비되었을 때
		int userPoint = 1000;
		int product = 1500;

		// when(실행): 어떠한 함수를 실행하면
		try {
			if (userPoint < product) {
				throw new IllegalArgumentException("보유한 포인트가 부족합니다.");
			}
			Assertions.fail("Expected IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			// then(검증): 어떠한 결과가 나와야 한다.
			assertEquals("보유한 포인트가 부족합니다.", e.getMessage());
		}
	}

	@Test
	@Transactional
	@DisplayName("사용한 포인트 DB 저장 -> 트랜잭션 롤백")
	public void testinsertUsePoint() {
		// given(준비): 어떠한 데이터가 준비되었을 때
		String user_id = "home";
		int point = 4600;
		PointExchangeHistoryVO pointbag = new PointExchangeHistoryVO();
		pointbag.setUser_id(user_id);
		pointbag.setPoint(point);

		// when(실행): 어떠한 함수를 실행하면
		PointExchangeHistoryDAO.usepointinsert(pointbag);
		// then(검증): 어떠한 결과가 나와야 한다.
		//assertEquals("success", PointExchangeHistoryDAO.usepointinsert(pointbag));

	}

	// testExChange 메서드에서 값들이 전달되는지 테스트하려고 만든 메서드
	private void checkPoint(int bag, int bag2) {

		int userpoint = bag;
		int product = bag2;

		if (userpoint < product) {
			throw new IllegalArgumentException("보유한 포인트가 부족합니다.");
		}
	}

	// 사용 포인트 저장
	private void insertUsePoint(String bag, int bag2) {
		PointExchangeHistoryVO pointbag = new PointExchangeHistoryVO();
		pointbag.setUser_id(bag);
		pointbag.setPoint(bag2);
		PointExchangeHistoryDAO.usepointinsert(pointbag);
	}
}