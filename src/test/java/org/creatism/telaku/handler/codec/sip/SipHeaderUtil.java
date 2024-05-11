package org.creatism.telaku.handler.codec.sip;

/**
 * util for sip header
 */
public class SipHeaderUtil {
    private final static String SLASH = "/";
    private final static String SP = " ";
    private final static String SP_LEFT_AB = " <";
    private final static String RIGHT_AB = ">";
    private final static String COLON = ":";
    private final static String SEMICOLON = ";";
    private final static String QUOTE = "\"";
    private final static String COMMA = ",";
    private final static String BRANCH_COOKIE = "branch=z9hG4bK";

    public static void addVia(SipMessage message, String clientHost, int port, String transferProtocol, String branch, String... params) {
        SipVersion sipVersion = message.protocolVersion();
        StringBuilder via = new StringBuilder(sipVersion.toString());
        via.append(SLASH + transferProtocol);
        via.append(SP);
        via.append(clientHost + COLON + port);
        for (int i = 0; i < params.length; i++) {
            via.append(SEMICOLON + params[i]);
        }
        via.append(SEMICOLON + BRANCH_COOKIE + branch);
        message.headers().add(SipHeaderNames.VIA, via.toString());
    }

    public static void addContact(SipMessage message, String displayName, String uri, String... headerParameters){
        message.headers().add(SipHeaderNames.CONTACT, generateMailMessage(displayName, uri, headerParameters));
    }

    public static void addFrom(SipMessage message, String displayName, String uri, String... headerParameters) {
        message.headers().add(SipHeaderNames.FROM,  generateMailMessage(displayName, uri, headerParameters));
    }

    public static void addTo(SipMessage message, String displayName, String uri, String... headerParameters) {
        message.headers().add(SipHeaderNames.TO,  generateMailMessage(displayName, uri, headerParameters));
    }

    public static void addCallId(SipMessage message, String callId) {
        message.headers().add(SipHeaderNames.CALL_ID, callId);
    }

    public static void addCSeq(SipMessage message, long seq, SipMethod method) {
        // transfer long to unsigned int;
        if(seq < 0 && seq > 0xFFFFFFFFL) throw new IllegalArgumentException("CSeq only support unsigned 32-bits integer, " + seq);
        StringBuilder sb = new StringBuilder(String.valueOf(seq));
        sb.append(SP);
        sb.append(method.name());
        message.headers().add(SipHeaderNames.CSEQ, sb.toString());
    }

    private static String generateMailMessage(String displayName, String uri, String... headerParameters) {
        StringBuilder contact = new StringBuilder();
        if(displayName!=null)
            contact.append(QUOTE + displayName + QUOTE);
        contact.append(SP);
        contact.append(SP_LEFT_AB + uri + RIGHT_AB);
        for (int i = 0; i < headerParameters.length; i++) {
            contact.append(SEMICOLON + headerParameters[i]);
        }
        return contact.toString();
    }

    public static void addMaxForwards(SipMessage message, int num) {
        message.headers().add(SipHeaderNames.MAX_FORWARDS, num);
    }

    public static void addContentLength(SipMessage message, int length) {
        message.headers().add(SipHeaderNames.CONTENT_LENGTH, length);
    }

    public static void allAllow(SipMessage message, SipMethod... methods) {
        StringBuilder allows = new StringBuilder();
        for (int i = 0; i < methods.length; i++) {
            allows.append(methods[i].name())
                    .append(COMMA + SP);
        }
        if (allows.length() > 0) allows.delete(allows.length() - 2,  allows.length());
        message.headers().add(SipHeaderNames.ALLOW, allows.toString());
    }
}
