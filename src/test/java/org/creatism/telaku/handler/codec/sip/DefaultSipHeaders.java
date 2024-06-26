package org.creatism.telaku.handler.codec.sip;

import io.netty.handler.codec.*;
import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import io.netty.handler.codec.DefaultHeaders.NameValidator;
import io.netty.util.internal.PlatformDependent;

import java.util.*;
import java.util.Map.Entry;

import static io.netty.util.AsciiString.CASE_INSENSITIVE_HASHER;
import static io.netty.util.AsciiString.CASE_SENSITIVE_HASHER;


/**
 * Basically implementation like {@link DefaultSipHeaders}
 */
public class DefaultSipHeaders extends SipHeaders {
    private static final int HIGHEST_INVALID_VALUE_CHAR_MASK = ~15;
    private static final ByteProcessor HEADER_NAME_VALIDATOR = new ByteProcessor() {
        @Override
        public boolean process(byte value) throws Exception {
            validateHeaderNameElement(value);
            return true;
        }
    };
    static final NameValidator<CharSequence> SipNameValidator = new NameValidator<CharSequence>() {
        @Override
        public void validateName(CharSequence name) {
            if (name == null || name.length() == 0) {
                throw new IllegalArgumentException("empty headers are not allowed [" + name + "]");
            }
            if (name instanceof AsciiString) {
                try {
                    ((AsciiString) name).forEachByte(HEADER_NAME_VALIDATOR);
                } catch (Exception e) {
                    PlatformDependent.throwException(e);
                }
            } else {
                // Go through each character in the name
                for (int index = 0; index < name.length(); ++index) {
                    validateHeaderNameElement(name.charAt(index));
                }
            }
        }
    };

    private final DefaultHeaders<CharSequence, CharSequence, ?> headers;

    public DefaultSipHeaders() {
        this(true);
    }

    /**
     * <b>Warning!</b> Setting <code>validate</code> to <code>false</code> will mean that Netty won't
     * validate & protect against user-supplied header values that are malicious.
     * This can leave your server implementation vulnerable to
     * <a href="https://cwe.mitre.org/data/definitions/113.html">
     *     CWE-113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')
     * </a>.
     * When disabling this validation, it is the responsibility of the caller to ensure that the values supplied
     * do not contain a non-url-escaped carriage return (CR) and/or line feed (LF) characters.
     *
     * @param validate Should Netty validate Header values to ensure they aren't malicious.
     */
    public DefaultSipHeaders(boolean validate) {
        this(validate, nameValidator(validate));
    }

    protected DefaultSipHeaders(boolean validate, NameValidator<CharSequence> nameValidator) {
        this(new DefaultHeadersImpl<CharSequence, CharSequence>(CASE_INSENSITIVE_HASHER,
                valueConverter(validate),
                nameValidator));
    }

    protected DefaultSipHeaders(DefaultHeaders<CharSequence, CharSequence, ?> headers) {
        this.headers = headers;
    }

    @Override
    public SipHeaders add(SipHeaders headers) {
        if (headers instanceof DefaultSipHeaders) {
            this.headers.add(((DefaultSipHeaders) headers).headers);
            return this;
        } else {
            return super.add(headers);
        }
    }

    @Override
    public SipHeaders set(SipHeaders headers) {
        if (headers instanceof DefaultSipHeaders) {
            this.headers.set(((DefaultSipHeaders) headers).headers);
            return this;
        } else {
            return super.set(headers);
        }
    }

    @Override
    public SipHeaders add(String name, Object value) {
        headers.addObject(name, value);
        return this;
    }

    @Override
    public SipHeaders add(CharSequence name, Object value) {
        headers.addObject(name, value);
        return this;
    }

    @Override
    public SipHeaders add(String name, Iterable<?> values) {
        headers.addObject(name, values);
        return this;
    }

    @Override
    public SipHeaders add(CharSequence name, Iterable<?> values) {
        headers.addObject(name, values);
        return this;
    }

    @Override
    public SipHeaders addInt(CharSequence name, int value) {
        headers.addInt(name, value);
        return this;
    }

    @Override
    public SipHeaders addShort(CharSequence name, short value) {
        headers.addShort(name, value);
        return this;
    }

    @Override
    public SipHeaders remove(String name) {
        headers.remove(name);
        return this;
    }

    @Override
    public SipHeaders remove(CharSequence name) {
        headers.remove(name);
        return this;
    }

    @Override
    public SipHeaders set(String name, Object value) {
        headers.setObject(name, value);
        return this;
    }

    @Override
    public SipHeaders set(CharSequence name, Object value) {
        headers.setObject(name, value);
        return this;
    }

    @Override
    public SipHeaders set(String name, Iterable<?> values) {
        headers.setObject(name, values);
        return this;
    }

    @Override
    public SipHeaders set(CharSequence name, Iterable<?> values) {
        headers.setObject(name, values);
        return this;
    }

    @Override
    public SipHeaders setInt(CharSequence name, int value) {
        headers.setInt(name, value);
        return this;
    }

    @Override
    public SipHeaders setShort(CharSequence name, short value) {
        headers.setShort(name, value);
        return this;
    }

    @Override
    public SipHeaders clear() {
        headers.clear();
        return this;
    }

    @Override
    public String get(String name) {
        return get((CharSequence) name);
    }

    @Override
    public String get(CharSequence name) {
        return HeadersUtils.getAsString(headers, name);
    }

    @Override
    public Integer getInt(CharSequence name) {
        return headers.getInt(name);
    }

    @Override
    public int getInt(CharSequence name, int defaultValue) {
        return headers.getInt(name, defaultValue);
    }

    @Override
    public Short getShort(CharSequence name) {
        return headers.getShort(name);
    }

    @Override
    public short getShort(CharSequence name, short defaultValue) {
        return headers.getShort(name, defaultValue);
    }

    @Override
    public Long getTimeMillis(CharSequence name) {
        return headers.getTimeMillis(name);
    }

    @Override
    public long getTimeMillis(CharSequence name, long defaultValue) {
        return headers.getTimeMillis(name, defaultValue);
    }

    @Override
    public List<String> getAll(String name) {
        return getAll((CharSequence) name);
    }

    @Override
    public List<String> getAll(CharSequence name) {
        return HeadersUtils.getAllAsString(headers, name);
    }

    @Override
    public List<Entry<String, String>> entries() {
        if (isEmpty()) {
            return Collections.emptyList();
        }
        List<Entry<String, String>> entriesConverted = new ArrayList<Entry<String, String>>(
                headers.size());
        for (Entry<String, String> entry : this) {
            entriesConverted.add(entry);
        }
        return entriesConverted;
    }

    @Deprecated
    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return HeadersUtils.iteratorAsString(headers);
    }

    @Override
    public Iterator<Entry<CharSequence, CharSequence>> iteratorCharSequence() {
        return headers.iterator();
    }

    @Override
    public Iterator<String> valueStringIterator(CharSequence name) {
        final Iterator<CharSequence> itr = valueCharSequenceIterator(name);
        return new Iterator<String>() {
            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }

            @Override
            public String next() {
                return itr.next().toString();
            }

            @Override
            public void remove() {
                itr.remove();
            }
        };
    }

    @Override
    public Iterator<CharSequence> valueCharSequenceIterator(CharSequence name) {
        return headers.valueIterator(name);
    }

    @Override
    public boolean contains(String name) {
        return contains((CharSequence) name);
    }

    @Override
    public boolean contains(CharSequence name) {
        return headers.contains(name);
    }

    @Override
    public boolean isEmpty() {
        return headers.isEmpty();
    }

    @Override
    public int size() {
        return headers.size();
    }

    @Override
    public boolean contains(String name, String value, boolean ignoreCase) {
        return contains((CharSequence) name, (CharSequence) value, ignoreCase);
    }

    @Override
    public boolean contains(CharSequence name, CharSequence value, boolean ignoreCase) {
        return headers.contains(name, value, ignoreCase ? CASE_INSENSITIVE_HASHER : CASE_SENSITIVE_HASHER);
    }

    @Override
    public Set<String> names() {
        return HeadersUtils.namesAsString(headers);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DefaultSipHeaders
                && headers.equals(((DefaultSipHeaders) o).headers, CASE_SENSITIVE_HASHER);
    }

    @Override
    public int hashCode() {
        return headers.hashCode(CASE_SENSITIVE_HASHER);
    }

    @Override
    public SipHeaders copy() {
        return new DefaultSipHeaders(headers.copy());
    }

    private static void validateHeaderNameElement(byte value) {
        switch (value) {
            case 0x1c:
            case 0x1d:
            case 0x1e:
            case 0x1f:
            case 0x00:
            case '\t':
            case '\n':
            case 0x0b:
            case '\f':
            case '\r':
            case ' ':
            case ',':
            case ':':
            case ';':
            case '=':
                throw new IllegalArgumentException(
                        "a header name cannot contain the following prohibited characters: =,;: \\t\\r\\n\\v\\f: " +
                                value);
            default:
                // Check to see if the character is not an ASCII character, or invalid
                if (value < 0) {
                    throw new IllegalArgumentException("a header name cannot contain non-ASCII character: " + value);
                }
        }
    }

    private static void validateHeaderNameElement(char value) {
        switch (value) {
            case 0x1c:
            case 0x1d:
            case 0x1e:
            case 0x1f:
            case 0x00:
            case '\t':
            case '\n':
            case 0x0b:
            case '\f':
            case '\r':
            case ' ':
            case ',':
            case ':':
            case ';':
            case '=':
                throw new IllegalArgumentException(
                        "a header name cannot contain the following prohibited characters: =,;: \\t\\r\\n\\v\\f: " +
                                value);
            default:
                // Check to see if the character is not an ASCII character, or invalid
                if (value > 127) {
                    throw new IllegalArgumentException("a header name cannot contain non-ASCII character: " +
                            value);
                }
        }
    }

    static ValueConverter<CharSequence> valueConverter(boolean validate) {
        return validate ? HeaderValueConverterAndValidator.INSTANCE : HeaderValueConverter.INSTANCE;
    }

    @SuppressWarnings("unchecked")
    static NameValidator<CharSequence> nameValidator(boolean validate) {
        return validate ? SipNameValidator : NameValidator.NOT_NULL;
    }

    private static class HeaderValueConverter extends CharSequenceValueConverter {
        static final HeaderValueConverter INSTANCE = new HeaderValueConverter();

        @Override
        public CharSequence convertObject(Object value) {
            if (value instanceof CharSequence) {
                return (CharSequence) value;
            }
            if (value instanceof Date) {
                return DateFormatter.format((Date) value);
            }
            if (value instanceof Calendar) {
                return DateFormatter.format(((Calendar) value).getTime());
            }
            return value.toString();
        }
    }

    private static final class HeaderValueConverterAndValidator extends HeaderValueConverter {
        static final HeaderValueConverterAndValidator INSTANCE = new HeaderValueConverterAndValidator();

        @Override
        public CharSequence convertObject(Object value) {
            CharSequence seq = super.convertObject(value);
            int state = 0;
            // Start looping through each of the character
            for (int index = 0; index < seq.length(); index++) {
                state = validateValueChar(seq, state, seq.charAt(index));
            }

            if (state != 0) {
                throw new IllegalArgumentException("a header value must not end with '\\r' or '\\n':" + seq);
            }
            return seq;
        }

        private static int validateValueChar(CharSequence seq, int state, char character) {
            /*
             * State:
             * 0: Previous character was neither CR nor LF
             * 1: The previous character was CR
             * 2: The previous character was LF
             */
            if ((character & HIGHEST_INVALID_VALUE_CHAR_MASK) == 0) {
                // Check the absolutely prohibited characters.
                switch (character) {
                    case 0x0: // NULL
                        throw new IllegalArgumentException("a header value contains a prohibited character '\0': " + seq);
                    case 0x0b: // Vertical tab
                        throw new IllegalArgumentException("a header value contains a prohibited character '\\v': " + seq);
                    case '\f':
                        throw new IllegalArgumentException("a header value contains a prohibited character '\\f': " + seq);
                    default:
                        break;
                }
            }

            // Check the CRLF (HT | SP) pattern
            switch (state) {
                case 0:
                    switch (character) {
                        case '\r':
                            return 1;
                        case '\n':
                            return 2;
                        default:
                            break;
                    }
                    break;
                case 1:
                    if (character == '\n') {
                        return 2;
                    }
                    throw new IllegalArgumentException("only '\\n' is allowed after '\\r': " + seq);
                case 2:
                    switch (character) {
                        case '\t':
                        case ' ':
                            return 0;
                        default:
                            throw new IllegalArgumentException("only ' ' and '\\t' are allowed after '\\n': " + seq);
                    }
                default:
                    break;
            }
            return state;
        }
    }
}
