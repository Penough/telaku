package org.creatism.telaku.unit.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LogEvent {
    public static final byte SEP = ':';
    private final String msg;
    private final String file;
}
