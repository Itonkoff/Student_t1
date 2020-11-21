package com.kofu.brighton.student.network;

import com.kofu.brighton.student.model.PaynowUserData;

import zw.co.paynow.constants.MobileMoneyMethod;
import zw.co.paynow.core.Payment;
import zw.co.paynow.core.Paynow;
import zw.co.paynow.responses.MobileInitResponse;
import zw.co.paynow.responses.StatusResponse;

public class PaynowService {
    public static final String INTEGRATION_ID = "9945";
    public static final String INTEGRATION_KEY = "1a42766b-1fea-48f6-ac39-1484dddfeb62";
    private static PaynowService instance;
    private final Paynow mPaynow;
    private Payment mPayment;
    private MobileInitResponse mMobileInitResponse;
    private StatusResponse mStatusResponse;

    public PaynowService() {
        mPaynow = new Paynow(INTEGRATION_ID, INTEGRATION_KEY);
        mPaynow.setResultUrl("http://example.com/gateways/paynow/update");
        mPaynow.setReturnUrl("http://example.com/return?gateway=paynow&merchantReference=1234");
    }

    public static PaynowService getInstance() {
        if (instance == null)
            instance = new PaynowService();
        return instance;
    }

    private void createPayment(double amount, String email) {
        mPayment = mPaynow.createPayment("Telone poject", email);
        mPayment.add("Top up", amount);
    }

    public MobileInitResponse sendPayment(PaynowUserData data) {
        createPayment(data.amount, data.email);
        return mPaynow
                .sendMobile(mPayment, data.phoneNumber, MobileMoneyMethod.ECOCASH);
    }

    public StatusResponse checkStatus(String pollUrl) {
        return mPaynow.pollTransaction(pollUrl);
    }

    public void releaseResources() {
        mPayment = null;
        mMobileInitResponse = null;
    }
}
