package com.mat.zip.point;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mat.zip.point.Exchange.PointExchangeHistoryDAO;
import com.mat.zip.point.Exchange.PointExchangeHistoryServiceImpl;
import com.mat.zip.point.save.PointSaveHistoryVO;

@ExtendWith(MockitoExtension.class)
public class PointExchangeHistoryServiceImplTest2 {

	@InjectMocks //테스트를 진행할 주체 주입(기존에 있는 서비스 레이어를 모킹으로 사용하게 주입)
    private PointExchangeHistoryServiceImpl pointExchangeHistoryService;

    @Mock //테스트에 필요한 가짜 객체 생성
    private PointExchangeHistoryDAO pointExchangeHistoryDAO;

    //Junit5(제이유닛/자바유닛 5)사용
    @Test
    @DisplayName("상품권 교환 로직(보유한 포인트가 상품금액보다 많으면 교환성공)")
    public void testExChange() {
        // given
        String user_id = "home";
        int id = 1;
        
        PointSaveHistoryVO pointSaveHistoryVO = new PointSaveHistoryVO();
        pointSaveHistoryVO.setSumpoint(6000);
        pointSaveHistoryVO.setUser_id(user_id);

        ProductPointVO productPointVO = new ProductPointVO();
        productPointVO.setPoint(4600);
        
        // when
        when(pointExchangeHistoryDAO.pointsaveFind(user_id)).thenReturn(pointSaveHistoryVO);
        when(pointExchangeHistoryDAO.productPointFind(id)).thenReturn(productPointVO);
        pointExchangeHistoryService.exChange(user_id, id);

        // then
        //메서드가 몇번 호출됐는지 테스트 times(숫자) 숫자만큼 실행되면 true
        verify(pointExchangeHistoryDAO, Mockito.times(1)).pointsaveFind(user_id);
        verify(pointExchangeHistoryDAO, Mockito.times(1)).productPointFind(id);
    }

    @Test
    @DisplayName("상품권 교환 로직(보유한 포인트가 상품금액보다 적으면 교환실패)")
    public void testExChangeWithInsufficientPoint() {
        // given
        String user_id = "home";
        int id = 1;
        PointSaveHistoryVO pointSaveHistoryVO = new PointSaveHistoryVO();
        pointSaveHistoryVO.setSumpoint(3000);  // 포인트를 제품 가격보다 적게 설정
        pointSaveHistoryVO.setUser_id(user_id);

        ProductPointVO productPointVO = new ProductPointVO();
        productPointVO.setPoint(4600);
        

        // when
        when(pointExchangeHistoryDAO.pointsaveFind(user_id)).thenReturn(pointSaveHistoryVO);
        when(pointExchangeHistoryDAO.productPointFind(id)).thenReturn(productPointVO);

        // then
        
        //doThrow()를 사용해서 예외 발생이 가능하지만 이건 @Mock에서 쓰인다.
        //실제 서비스 레이어에 있는 메서드를 호출하는 경우 assertThrows를 사용한다
        assertThrows(RuntimeException.class, () -> pointExchangeHistoryService.exChange(user_id, id));  // 포인트 부족 시 예외 발생 검증
        verify(pointExchangeHistoryDAO, Mockito.times(1)).pointsaveFind(user_id);
        verify(pointExchangeHistoryDAO, Mockito.times(1)).productPointFind(id);
    }
}






