package org.creatism.telaku.practice.sip;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.sdp.SdpException;
import javax.sip.*;
import javax.sip.header.CallIdHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

@Slf4j
@Data
@Component
@DependsOn("sipJainConfiguration")
public class SipLayer implements SipListener {
    @Autowired
    private SipRequestFactory requestFactory;
    @Autowired
    private SipClient client;

    @Override
    public void processRequest(RequestEvent requestEvent) {
        System.err.println("preq");
        System.err.println(requestEvent);
        Request req = requestEvent.getRequest();
        if(Request.NOTIFY.equals(req.getMethod())) {
            try {
                // 完成注册流程
                client.successAck(req);
            } catch (SipException e) {
                e.printStackTrace();
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void processResponse(ResponseEvent responseEvent) {
        // 解析401请求，MD5加密resp
        System.err.println("prep");
        Response response = responseEvent.getResponse();
        int statusCode = response.getStatusCode();
        System.err.println();
        if(statusCode == 401){
            Request authReq = null;
            try {
                CallIdHeader callIdHeader = (CallIdHeader) response.getHeader(CallIdHeader.NAME);
                authReq = requestFactory.createAuthRegisterRequest(callIdHeader, response);
                client.sendRequest(authReq);
            } catch (SipException e) {
                e.printStackTrace();
            }
        }
        if(407==statusCode) {
            log.info("407 {}", response.getReasonPhrase());
            try {
                Request request = requestFactory.createAuthInviteRequest(response);
                client.sendRequest(request);
            } catch (NoSuchAlgorithmException | ParseException | InvalidArgumentException | SdpException | SipException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(183==response.getStatusCode()) {
            // 183 meaning has try to Session Progess
            // todo init RTP connection
            // todo publish a RTP event
            client.publishEvent(response);
        }
    }



    @Override
    public void processTimeout(TimeoutEvent timeoutEvent) {
        System.err.println("ptim");
        System.err.println(timeoutEvent);
    }

    @Override
    public void processIOException(IOExceptionEvent ioExceptionEvent) {
        System.err.println("pioe");
        System.err.println(ioExceptionEvent);
    }

    private static boolean flag = true;
    @Override
    public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {
        System.err.println("ptransaction");
        if(transactionTerminatedEvent.isServerTransaction()) {
            System.out.println("server transaction terminated");
        }
        System.err.println(transactionTerminatedEvent);
    }

    @Override
    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {
        System.err.println("prdia");
        System.err.println(dialogTerminatedEvent);
    }
}
