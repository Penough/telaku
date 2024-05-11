package org.creatism.telaku.practice.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
@ConfigurationProperties(prefix = "trans")
public class TransferLayerProperties {
    private int tcpServerPort;
    private int rtpServerPort;
    private int rtcpServerPort;
}
