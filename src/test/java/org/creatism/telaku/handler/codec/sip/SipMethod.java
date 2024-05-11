package org.creatism.telaku.handler.codec.sip;

import io.netty.util.AsciiString;

import static io.netty.util.internal.MathUtil.findNextPositivePowerOfTwo;
import static io.netty.util.internal.ObjectUtil.checkNonEmptyAfterTrim;

/**
 * The request method of SIP or its derived protocols
 * <p>
 * SIP has 13 Method request types. The most commonly used Methods are INVITE,
 * ACK, BYE, and REGISTER which are used during voice calls. The first line of
 * a SIP request message includes the Method type and the request URI which is
 * the current destination of the request.
 * </p>
 */
public class SipMethod implements Comparable<SipMethod> {

    /**
     * Indicates a client is being invited to participate in a call session.
     */
    public static final SipMethod INVITE = new SipMethod("INVITE");

    /**
     * 	Confirms that the client has received a final response to an INVITE request.
     */
    public static final SipMethod ACK = new SipMethod("ACK");

    /**
     * Cancels any pending request.
     */
    public static final SipMethod CANCEL = new SipMethod("CANCEL");

    /**
     * 	Queries the capabilities of servers.
     */
    public static final SipMethod OPTIONS = new SipMethod("OPTIONS");

    /**
     * Registers the address listed in the To header field with a SIP server.
     */
    public static final SipMethod REGISTER = new SipMethod("REGISTER");

    /**
     * 	Provisional acknowledgement.
     */
    public static final SipMethod PRACK = new SipMethod("PRACK");

    /**
     * Subscribes for an Event of Notification from the Notifier.
     */
    public static final SipMethod SUBSCRIBE = new SipMethod("SUBSCRIBE");

    /**
     * Notify the subscriber of a new Event.
     */
    public static final SipMethod NOTIFY = new SipMethod("NOTIFY");

    /**
     * 	Publishes an event to the Server.
     */
    public static final SipMethod PUBLISH = new SipMethod("PUBLISH");

    /**
     * 	Sends mid-session information that does not modify the session state.
     */
    public static final SipMethod INFO = new SipMethod("INFO");


    /**
     * Asks recipient to issue SIP request (call transfer.)
     */
    public static final SipMethod REFER = new SipMethod("REFER");

    /**
     * Transports instant messages using SIP.
     */
    public static final SipMethod MESSAGE = new SipMethod("MESSAGE");

    /**
     * Modifies the state of a session without changing the state of the dialog.
     */
    public static final SipMethod UPDATE = new SipMethod("UPDATE");

    private static final SipMethod.EnumNameMap<SipMethod> methodMap;

    static {
        methodMap = new SipMethod.EnumNameMap<SipMethod>(
                new SipMethod.EnumNameMap.Node<SipMethod>(INVITE.toString(), INVITE, 0),
                new SipMethod.EnumNameMap.Node<SipMethod>(ACK.toString(), ACK, 1),
                new SipMethod.EnumNameMap.Node<SipMethod>(CANCEL.toString(), CANCEL,2),
                new SipMethod.EnumNameMap.Node<SipMethod>(OPTIONS.toString(), OPTIONS,3),
                new SipMethod.EnumNameMap.Node<SipMethod>(REGISTER.toString(), REGISTER,4),
                new SipMethod.EnumNameMap.Node<SipMethod>(PRACK.toString(), PRACK,5),
                new SipMethod.EnumNameMap.Node<SipMethod>(SUBSCRIBE.toString(), SUBSCRIBE,6),
                new SipMethod.EnumNameMap.Node<SipMethod>(NOTIFY.toString(), NOTIFY,7),
                new SipMethod.EnumNameMap.Node<SipMethod>(PUBLISH.toString(), PUBLISH,8),
                new SipMethod.EnumNameMap.Node<SipMethod>(INFO.toString(), INFO,9),
                new SipMethod.EnumNameMap.Node<SipMethod>(REFER.toString(), REFER,10),
                new SipMethod.EnumNameMap.Node<SipMethod>(MESSAGE.toString(), MESSAGE,11),
                new SipMethod.EnumNameMap.Node<SipMethod>(UPDATE.toString(), UPDATE,12));
    }

    /**
     * Returns the {@link SipMethod} represented by the specified name.
     * If the specified name is a standard HTTP method name, a cached instance
     * will be returned.  Otherwise, a new instance will be returned.
     */
    public static SipMethod valueOf(String name) {
        SipMethod result = methodMap.get(name);
        return result != null ? result : new SipMethod(name);
    }

    private final AsciiString name;

    /**
     * Creates a new SIP method with the specified name.  You will not need to
     * create a new method unless you are implementing a protocol derived from
     * SIP
     */
    public SipMethod(String name) {
        name = checkNonEmptyAfterTrim(name, "name");

        for (int i = 0; i < name.length(); i ++) {
            char c = name.charAt(i);
            if (Character.isISOControl(c) || Character.isWhitespace(c)) {
                throw new IllegalArgumentException("invalid character in name");
            }
        }

        this.name = AsciiString.cached(name);
    }


    /**
     * Returns the name of this method.
     */
    public String name() {
        return name.toString();
    }


    /**
     * Returns the name of this method.
     */
    public AsciiString asciiName() {
        return name;
    }

    @Override
    public int compareTo(SipMethod o) {
        if (o == this) {
            return 0;
        }
        return name().compareTo(o.name());
    }

    private static final class EnumNameMap<T> {
        private final SipMethod.EnumNameMap.Node<T>[] values;
        private final int valuesMask;

        private EnumNameMap(SipMethod.EnumNameMap.Node<T>... nodes) {
            values = (SipMethod.EnumNameMap.Node<T>[]) new SipMethod.EnumNameMap.Node[findNextPositivePowerOfTwo(nodes.length)];
            valuesMask = values.length - 1;
            for (SipMethod.EnumNameMap.Node<T> node : nodes) {
                int i = node.hash & valuesMask;
                if (values[i] != null) {
                    throw new IllegalArgumentException("index " + i + " collision between values: [" +
                            values[i].key + ", " + node.key + ']');
                }
                values[i] = node;
            }
        }

        T get(String name) {
            SipMethod.EnumNameMap.Node<T> node = values[hashCode(name) & valuesMask];
            return node == null || !node.key.equals(name) ? null : node.value;
        }

        private static int hashCode(String name) {
            return methodMap.get(name).hashCode();
        }

        private static final class Node<T> {
            final String key;
            final T value;
            final int hash;

            private Node(String key, T value, int hash) {
                this.key = key;
                this.value = value;
                this.hash = hash;
            }

            @Override
            public int hashCode() {
                return hash;
            }
        }
    }
}
