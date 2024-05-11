package org.creatism.telaku.practice.rtp;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RtpEvent extends ApplicationEvent {
    public static final String NAME = "rtp-event";
    private Object data;

    public RtpEvent(Object data) {
        super(NAME);
        this.data = data;
    }
}
