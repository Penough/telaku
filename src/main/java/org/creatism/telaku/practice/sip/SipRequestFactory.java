package org.creatism.telaku.practice.sip;

import org.creatism.telaku.practice.configuration.properties.SipProperties;
import org.creatism.telaku.practice.util.SdpUtil;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.header.HeaderFactoryExt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sdp.*;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.*;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

@Slf4j
@Component
public class SipRequestFactory {

    @Autowired
    private HeaderFactory headerFactory;
    @Autowired
    private SipProperties sipProperties;
    @Autowired
    private AddressFactory addressFactory;
    @Autowired
    private MessageFactory messageFactory;
    @Autowired
    private SdpFactory sdpFactory;

    private static long seqnum = 1L;

    /**
     * create REGISTER request
     * @param callIdHeader
     * @return
     */
    public Request createRegisterRequest(CallIdHeader callIdHeader) {
        try {
            // REGISTER request to user is the same as config user
            ToHeader to = createToHeader(sipProperties.getUsername(), sipProperties.getUsername());
            Request request = createCommonRequest(Request.REGISTER, callIdHeader, to);
            /** Expires header **/
            ExpiresHeader expiresHeader = headerFactory.createExpiresHeader(300);
            request.addHeader(expiresHeader);
            /** Allow **/
            AllowHeader allowHeader = headerFactory.createAllowHeader("PRACK, INVITE, ACK, BYE, CANCEL, UPDATE, INFO, SUBSCRIBE, NOTIFY, REFER, MESSAGE, OPTIONS");
            request.addHeader(allowHeader);
            return request;
        } catch (InvalidArgumentException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param callIdHeader
     * @param response
     * @return
     */
    public Request createAuthRegisterRequest(CallIdHeader callIdHeader, Response response) {
        Request request = createRegisterRequest(callIdHeader);
        AuthorizationHeader authorizationHeader = null;
        try {
            authorizationHeader = createAuthorizationHeader(response, Request.REGISTER);
        } catch (NoSuchAlgorithmException | ParseException e) {
            log.error("create auth register request failed...");
            e.printStackTrace();
        }
        request.addHeader(authorizationHeader);
        return request;
    }


    public Request createInviteRequest(CallIdHeader callIdHeader, String phone) throws ParseException, SdpException, InvalidArgumentException {
        ToHeader to = createToHeader(phone,null);
        Request request = createCommonRequest(Request.INVITE, callIdHeader, to);
        SipURI uri = addressFactory.createSipURI(phone, sipProperties.getHost()+":"+sipProperties.getPort());
        request.setRequestURI(uri);
        request.setHeader(headerFactory.createSupportedHeader("replaces, 100rel, timer, norefersub"));
        request.setHeader(headerFactory.createMinExpiresHeader(90));
        List<String> userAgents = new ArrayList<>();
        userAgents.add("MicroSIP/3.20.7");
        request.setHeader(headerFactory.createUserAgentHeader(userAgents));
        request.setHeader(((HeaderFactoryExt)headerFactory).createSessionExpiresHeader(1800));
        request.removeHeader(ExpiresHeader.NAME);

        ContentTypeHeader contentTypeHeader = headerFactory
                .createContentTypeHeader("application", "sdp");

        SessionDescription sessionDescription = sdpFactory.createSessionDescription();
        sessionDescription.setVersion(sdpFactory.createVersion(0));
        long sessionId = System.currentTimeMillis();
        sessionDescription.setOrigin(sdpFactory.createOrigin("-", sessionId, sessionId, "IN",
                "IP4", sipProperties.getListenAddr()));
        sessionDescription.setSessionName(sdpFactory.createSessionName("pjmedia"));
        Vector<BandWidth> bandWidths = new Vector<>();
        bandWidths.addElement(sdpFactory.createBandwidth("TIAS", 64000));
        bandWidths.addElement(sdpFactory.createBandwidth("AS", 84));
        sessionDescription.setBandwidths(bandWidths);

        Vector<TimeDescription> timeDescriptions = new Vector<>();
        timeDescriptions.addElement(sdpFactory.createTimeDescription());
        sessionDescription.setTimeDescriptions(timeDescriptions);

        Vector<Attribute> sessionAttrs = new Vector<>();
        sessionAttrs.addElement(sdpFactory.createAttribute("X-nat", "0"));
        sessionDescription.setAttributes(sessionAttrs);

        MediaDescription mediaDescription = sdpFactory.createMediaDescription("audio", 4000,  1, "RTP/AVP", new int[]{8,0,101});
        Vector<MediaDescription> mediaDescriptions = new Vector<>();
        mediaDescriptions.addElement(mediaDescription);
        Vector<Attribute> medias = new Vector<>();
        medias.addElement(sdpFactory.createAttribute("rtcp","4001 IN IP4 " + sipProperties.getListenAddr()));
        medias.addElement(sdpFactory.createAttribute("sendrecv",null));
        medias.addElement(sdpFactory.createAttribute("rtpmap","8 PCMA/8000"));
        medias.addElement(sdpFactory.createAttribute("rtpmap","0 PCMA/8000"));
        medias.addElement(sdpFactory.createAttribute("rtpmap","101 telephone-event/8000"));
        medias.addElement(sdpFactory.createAttribute("fmtp","101 0-16"));

        medias.addElement(sdpFactory.createAttribute("ssrc", SdpUtil.generateSsrc() + " cname:" + SdpUtil.generateCname()));
        mediaDescription.setAttributes(medias);
        sessionDescription.setMediaDescriptions(mediaDescriptions);

        sessionDescription.setConnection(sdpFactory.createConnection("IN","IP4",sipProperties.getListenAddr()));

        request.setContent(sessionDescription, contentTypeHeader);
        return request;
    }

    public Request createAuthInviteRequest(Response response) throws SdpException, InvalidArgumentException, ParseException, NoSuchAlgorithmException {
        CallIdHeader callIdHeader = (CallIdHeader)response.getHeader(CallIdHeader.NAME);
        ToHeader toHeader = (ToHeader)response.getHeader(ToHeader.NAME);
        Address addr = toHeader.getAddress();
        URI uri = addr.getURI();
        String s = uri.toString();
        String phone = s.substring(4, s.indexOf("@"));
        Request req = createInviteRequest(callIdHeader, phone);
        ProxyAuthorizationHeader header = createProxyAuthorizationHeader(response, Request.INVITE);
        req.addHeader(header);
        return req;
    }

    public Request createCommonRequest(String requestType, CallIdHeader callIdHeader, ToHeader toHeader) throws InvalidArgumentException, ParseException {
        /** Call-ID header **/

        /** via header **/
        ArrayList viaHeaders = new ArrayList();
        ViaHeader viaHeader = headerFactory.createViaHeader(
                sipProperties.getListenAddr(),
                sipProperties.getListenPort(),
                ListeningPoint.UDP,
                null);
        viaHeader.setRPort();
        viaHeader.setBranch("z9hG4bK"+callIdHeader.getCallId()+System.currentTimeMillis());
        viaHeaders.add(viaHeader);

        /** Max-Forwards header **/
        // default num is 70
        MaxForwardsHeader maxForwardsHeader = headerFactory.createMaxForwardsHeader(70);

        SipURI sipUri = addressFactory.createSipURI(sipProperties.getUsername(),
                sipProperties.getHost()+":"+sipProperties.getPort());
        /** from header **/
        SipURI from = addressFactory.createSipURI(sipProperties.getUsername(), sipProperties.getHost());
        Address fromNameAddress = addressFactory.createAddress(from);
        fromNameAddress.setDisplayName(sipProperties.getUsername());
        FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress, callIdHeader.getCallId());

        /** set transfer protocol **/
//        from.setTransportParam(ListeningPoint.UDP);

        /** CSeq header **/
        // just set sequence number as 1L temporally, it should be revised in normal.
        CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(seqnum++, requestType);

        /** generate sip request message **/
        Request request =  messageFactory.createRequest(sipUri, requestType, callIdHeader, cSeqHeader, fromHeader, toHeader, viaHeaders, maxForwardsHeader);

        request.addHeader(createContactHeader());
        return request;
    }

    /**
     * create contact header
     * Contact header is required in INVITE request and response with status 200
     * @return contact header
     * @throws ParseException
     */
    public ContactHeader createContactHeader() throws ParseException {
        /** Contact header **/
        // Contact header is required in INVITE request and response with status 200
        SipURI contactURI = addressFactory.createSipURI(sipProperties.getUsername(), sipProperties.getListenAddr());
        contactURI.setPort(sipProperties.getListenPort());
        ((SipUri)contactURI).getParameters().set("ob", null);
        Address contactAddress = addressFactory.createAddress(contactURI);
        contactAddress.setDisplayName(sipProperties.getUsername());
        return headerFactory.createContactHeader(contactAddress);
    }

    /**
     * create to header
     * @param userName
     * @param displayName
     * @return
     * @throws ParseException
     */
    public ToHeader createToHeader(String userName, String displayName) throws ParseException {
        /** to header **/
        // in register condition, toAddress is the same as from
        // to header needn't tag
        SipURI to = addressFactory.createSipURI(userName, sipProperties.getHost());
        Address toAddress = addressFactory.createAddress(to);
        toAddress.setDisplayName(displayName);
        return headerFactory.createToHeader(toAddress, null);
    }

    /**
     *
     * @param response
     * @param reqType
     * @return
     * @throws NoSuchAlgorithmException
     * @throws ParseException
     */
    private AuthorizationHeader createAuthorizationHeader(Response response, String reqType) throws NoSuchAlgorithmException, ParseException {
        MessageDigest md5 = MessageDigest.getInstance("md5");
        ListIterator authHeaders = response.getHeaders(WWWAuthenticateHeader.NAME);

        while(authHeaders.hasNext()) {
            WWWAuthenticateHeader authHeader = (WWWAuthenticateHeader) authHeaders.next();
            String h1Str = concat(sipProperties.getUsername(), authHeader.getRealm(), sipProperties.getPwd());
            byte[] digest = md5.digest(h1Str.getBytes(StandardCharsets.UTF_8));
            String h1 = new BigInteger(1, digest).toString(16);

            //sip:app62_01.kmicloud.com:5518
            String digestURI = "sip:" + sipProperties.getHost() + sipProperties.getPort();
            digest = md5.digest(concat(reqType, digestURI).getBytes(StandardCharsets.UTF_8));
            String h2 = new BigInteger(1, digest).toString(16);

            String nonceCount = "00000001";
            digest = md5.digest(String.valueOf(System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8));
            String cnonce = new BigInteger(1, digest).toString(16);
            digest = md5.digest(concat(h1, authHeader.getNonce(), nonceCount, cnonce, authHeader.getQop(), h2).getBytes(StandardCharsets.UTF_8));
            String rep = new BigInteger(1, digest).toString(16);
            AuthorizationHeader reqHeader = headerFactory.createAuthorizationHeader("Digest");
            reqHeader.setUsername(sipProperties.getUsername());
            reqHeader.setRealm(authHeader.getRealm());
            reqHeader.setNonce(authHeader.getNonce());
            reqHeader.setURI(addressFactory.createURI(digestURI));
            reqHeader.setResponse(rep);
            reqHeader.setAlgorithm(authHeader.getAlgorithm());
            reqHeader.setCNonce(cnonce);
            reqHeader.setQop(authHeader.getQop());
            reqHeader.setNonceCount(1);
            return reqHeader;
        }
        return null;
    }

    /**
     *
     * @param response
     * @param reqType
     * @return
     * @throws ParseException
     * @throws NoSuchAlgorithmException
     */
    private ProxyAuthorizationHeader createProxyAuthorizationHeader(Response response, String reqType) throws ParseException, NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("md5");
        ListIterator authHeaders = response.getHeaders(ProxyAuthenticateHeader.NAME);

        while(authHeaders.hasNext()) {
            ProxyAuthenticateHeader authHeader = (ProxyAuthenticateHeader) authHeaders.next();
            String h1Str = concat(sipProperties.getUsername(), authHeader.getRealm(), sipProperties.getPwd());
            byte[] digest = md5.digest(h1Str.getBytes(StandardCharsets.UTF_8));
            String h1 = new BigInteger(1, digest).toString(16);

            String digestURI = "sip:" + sipProperties.getHost() + sipProperties.getPort();
            digest = md5.digest(concat(reqType, digestURI).getBytes(StandardCharsets.UTF_8));
            String h2 = new BigInteger(1, digest).toString(16);

            String nonceCount = "00000001";
            digest = md5.digest(String.valueOf(System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8));
            String cnonce = new BigInteger(1, digest).toString(16);
            digest = md5.digest(concat(h1, authHeader.getNonce(), nonceCount, cnonce, authHeader.getQop(), h2).getBytes(StandardCharsets.UTF_8));
            String rep = new BigInteger(1, digest).toString(16);

            ProxyAuthorizationHeader reqHeader = headerFactory.createProxyAuthorizationHeader("Digest");
            reqHeader.setUsername(sipProperties.getUsername());
            reqHeader.setRealm(authHeader.getRealm());
            reqHeader.setNonce(authHeader.getNonce());
            reqHeader.setURI(addressFactory.createURI(digestURI));
            reqHeader.setResponse(rep);
            reqHeader.setAlgorithm(authHeader.getAlgorithm());
            reqHeader.setCNonce(cnonce);
            reqHeader.setQop(authHeader.getQop());
            reqHeader.setNonceCount(1);
            return reqHeader;
        }
        return null;
    }

    public Response createAck(int code, Request req) throws ParseException {
        return messageFactory.createResponse(code, req);
    }

    private static String concat(String... sts){
        StringBuilder sb = new StringBuilder();
        for (String s: sts) {
            sb.append(s);
            sb.append(":");
        }
        if (sb.length()>0) sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }
}
