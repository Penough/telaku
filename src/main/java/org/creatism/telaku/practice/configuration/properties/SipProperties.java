package org.creatism.telaku.practice.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
@ConfigurationProperties(prefix = "sip")
public class SipProperties {
    private String username;
    private String pwd;
    private String host;
    private int port;
    private String listenAddr;
    private int listenPort = 53592;
}
