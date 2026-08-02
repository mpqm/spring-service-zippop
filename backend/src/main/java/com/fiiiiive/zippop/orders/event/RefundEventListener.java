package com.fiiiiive.zippop.orders.event;

import com.fiiiiive.zippop.global.base.ServerErrorCode;
import com.fiiiiive.zippop.global.base.ServerException;
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
    public void handleRefundAfterRollback(RefundEvent event) {
        try {
            var payment = event.getPayment();
            CancelData cancelData = new CancelData(payment.getImpUid(), true, payment.getAmount());
            iamportClient.cancelPaymentByImpUid(cancelData);
        } catch (IamportResponseException | IOException exception) {
            throw new ServerException(ServerErrorCode.PAYMENT_PROVIDER_ERROR, exception);
        }
    }

}
