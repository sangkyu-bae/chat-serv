package org.example.modules.websoket.server;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockSendMsgService extends SendMsg implements SubService<StockSendMsg> {

    @Value("${test.stock.room}")
    private String stockRoom;

    @Override
    public void sendMessage(StockSendMsg sendObj) {
        send(stockRoom,sendObj);
    }
}
