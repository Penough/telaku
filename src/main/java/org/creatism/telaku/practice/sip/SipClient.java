package org.creatism.telaku.practice.sip;

import org.creatism.telaku.practice.rtp.RtpEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.sdp.SdpException;
import javax.sip.*;
import javax.sip.header.CallIdHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;

@Slf4j
@Component
public class SipClient {
    @Autowired
    @Lazy
    private SipProvider sipProvider;
    @Autowired
    private HeaderFactory headerFactory;
    @Autowired
    private SipRequestFactory requestFactory;
    @Autowired
    private ApplicationContext applicationContext;

    public String register() throws ParseException {
        CallIdHeader callIdHeader = createNewCallId();
        Request request = requestFactory.createRegisterRequest(callIdHeader);
        try {
            sendRequest(request);
        } catch (SipException e) {
            log.error("send Register msg error, callId:{}", callIdHeader.getCallId());
            e.printStackTrace();
        }
        return callIdHeader.getCallId();
    }
    /**
     * call
     * @param phone MT call
     * @return call id
     */
    public String call(String phone) throws ParseException, InvalidArgumentException, SipException {
        CallIdHeader callIdHeader = createNewCallId();
        try {
            // 尝试发送INVITE请求
            Request req = requestFactory.createInviteRequest(callIdHeader, phone);
            sendRequest(req);
        } catch (ParseException | SdpException | InvalidArgumentException e) {
            e.printStackTrace();
        }
        return callIdHeader.getCallId();
    }

    public CallIdHeader createNewCallId() throws ParseException {
        CallIdHeader callIdHeader = sipProvider.getNewCallId();
        String callId = callIdHeader.getCallId().substring(0, callIdHeader.getCallId().indexOf("@"));
        callIdHeader.setCallId(callId);
        return callIdHeader;
    }

    public CallIdHeader generateCallIdHeader(String callId) throws ParseException {
        return headerFactory.createCallIdHeader(callId);
    }

    public ClientTransaction sendRequest(Request request) throws  SipException {
        ClientTransaction ct = sipProvider.getNewClientTransaction(request);
        ct.sendRequest();
        return ct;
    }

    public ServerTransaction sendResponse(Request req, Response response) throws SipException, InvalidArgumentException {
        ServerTransaction ct = sipProvider.getNewServerTransaction(req);
        ct.sendResponse(response);
        return ct;
    }

    public Response successAck(Request req) throws ParseException, InvalidArgumentException, SipException {
        Response response = requestFactory.createAck(200, req);
        sendResponse(req, response);
        return response;
    }

    public void publishEvent(Object obj){
        applicationContext.publishEvent(new RtpEvent(obj));
    }
}
