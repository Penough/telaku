package org.creatism.telaku.handler.codec.sip;

import java.util.List;

import static io.netty.util.internal.ObjectUtil.checkPositiveOrZero;
import static io.netty.util.internal.StringUtil.COMMA;

public class SipUtil {

    private static final String COMMA_STRING = String.valueOf(COMMA);

    public static long normalizeAndGetContentLength(List<? extends CharSequence> contentLengthFields) {
        if (contentLengthFields.isEmpty()) {
            return -1;
        }

        // Guard against multiple Content-Length headers as stated in
        // https://tools.ietf.org/html/rfc7230#section-3.3.2:
        //
        // If a message is received that has multiple Content-Length header
        //   fields with field-values consisting of the same decimal value, or a
        //   single Content-Length header field with a field value containing a
        //   list of identical decimal values (e.g., "Content-Length: 42, 42"),
        //   indicating that duplicate Content-Length header fields have been
        //   generated or combined by an upstream message processor, then the
        //   recipient MUST either reject the message as invalid or replace the
        //   duplicated field-values with a single valid Content-Length field
        //   containing that decimal value prior to determining the message body
        //   length or forwarding the message.
        String firstField = contentLengthFields.get(0).toString();
        boolean multipleContentLengths =
                contentLengthFields.size() > 1 || firstField.indexOf(COMMA) >= 0;

        if (multipleContentLengths) {
                // Reject the message as invalid
                throw new IllegalArgumentException(
                        "Multiple Content-Length values found: " + contentLengthFields);
        }
        // Ensure we not allow sign as part of the content-length:
        // See https://github.com/squid-cache/squid/security/advisories/GHSA-qf3v-rc95-96j5
        if (firstField.isEmpty() || !Character.isDigit(firstField.charAt(0))) {
            // Reject the message as invalid
            throw new IllegalArgumentException(
                    "Content-Length value is not a number: " + firstField);
        }
        try {
            final long value = Long.parseLong(firstField);
            return checkPositiveOrZero(value, "Content-Length value");
        } catch (NumberFormatException e) {
            // Reject the message as invalid
            throw new IllegalArgumentException(
                    "Content-Length value is not a number: " + firstField, e);
        }
    }
}
