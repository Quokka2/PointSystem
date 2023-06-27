package com.mat.zip.point;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
public class PointExchangeHistoryServiceImplTest3 {

	@Autowired
	private PointExchangeHistoryDAO PointExchangeHistoryDAO;
	
	// 클래스 변수로 BeforeEach에 생성된 변수 저장
	private PointSaveHistoryVO vo;
	private ProductPointVO vo1;

	// 테스트가 진행될때 마다 항상 동작함.
	@BeforeEach
	public void beforeEach() {
		String user_id = "home";
		int id = 1;
		vo = PointExchangeHistoryDAO.pointsaveFind(user_id);
		vo1 = PointExchangeHistoryDAO.productPointFind(id);
	}

	@Test
	@Transactional
	@DisplayName("실제 동작할 메인메서드 입력값 테스트")
	public void testExChange() {

		assertEquals(6000, vo.getSumpoint());
		assertEquals("home", vo.getUser_id());
		assertEquals(4600, vo1.getPoint());

	}

	// 포인트 비교하는 로직 직접 테스트하려고 작성
	@Test
	@Transactional
	@DisplayName("보유 포인트 / 상품 가격 비교하는 로직")
	public void testCheckPoint() {
		// given(준비): 어떠한 데이터가 준비되었을 때
		int userPoint = 4000;
		int product = vo1.getPoint();

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
	@DisplayName("사용한 포인트 DB 저장")
	public void testinsertUsePoint() {

		// given(준비): 어떠한 데이터가 준비되었을 때
		String user_id = vo.getUser_id();
		int sumpoint = vo.getSumpoint();
		int point = vo1.getPoint();
		PointExchangeHistoryVO pointbag = new PointExchangeHistoryVO();
		pointbag.setUser_id(user_id);
		pointbag.setPoint(point);

		// when(실행): 어떠한 함수를 실행하면
		PointExchangeHistoryDAO.usepointinsert(pointbag);

		PointSaveHistoryVO dbtest = PointExchangeHistoryDAO.pointsaveFind(user_id);
		System.out.println(dbtest.getSumpoint());
		int testpoint = (sumpoint - point);

		// then(검증): 어떠한 결과가 나와야 한다.
		assertEquals(testpoint, dbtest.getSumpoint());

	}

	@Test
	@Transactional
	@DisplayName("사용한 포인트 상세이력관리 DB 저장")
	public void testuseDetailHistory() {
		// given
        String user_id = vo.getUser_id();
        List<PointSaveHistoryVO> savedPointsList = PointExchangeHistoryDAO.pointsaveFindAll(user_id);

        System.out.println("불러온 적립 포인트 리스트 : " + savedPointsList);
        Queue<PointSaveHistoryVO> savedPoints = new LinkedList<>(savedPointsList);
        int productPoint = vo1.getPoint();

        // when
        while (!savedPoints.isEmpty() && productPoint > 0) {
            PointSaveHistoryVO savedPoint = savedPoints.poll();
            int currentPoint = savedPoint.getPoint();

            if (currentPoint <= productPoint) {
                productPoint -= currentPoint;
                savedPoint.setPoint(-currentPoint);
            } else {
                savedPoint.setPoint(-productPoint);
                productPoint = 0;
            }

            PointDetailHistoryVO detailhistory = new PointDetailHistoryVO();
            detailhistory.setUser_id(user_id);
            detailhistory.setPoint(savedPoint.getPoint());

            PointExchangeHistoryDAO.usedetailpointinsert(detailhistory);
        }

        // then
        // 데이터베이스에서 detailhistory의 값을 읽어와서 확인
        PointSaveHistoryVO result = PointExchangeHistoryDAO.testFind(user_id);
        System.out.println("상세이력 테이블 포인트 합계 : " + result);
        // result에 볼러온 sumpoint 값은 ( - )값이 저장되어 예상값에 ( - ) 를 붙여줬음.
        assertEquals(-vo1.getPoint(), result.getSumpoint());
    }

}