package com.fiiiiive.zippop.orders.event;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RefundEventListener {

    private final IamportClient iamportClient;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleRefundAfterRollback(RefundEvent event) throws IamportResponseException, IOException {
            var payment = event.getPayment();
            CancelData cancelData = new CancelData(payment.getImpUid(), true, payment.getAmount());
            iamportClient.cancelPaymentByImpUid(cancelData);
    }

}
