package org.creatism.telaku.practice.configuration;

import org.creatism.telaku.practice.configuration.properties.SipProperties;
import org.creatism.telaku.practice.sip.SipLayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sdp.SdpFactory;
import javax.sip.*;
import javax.sip.address.AddressFactory;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;
import java.util.Properties;
import java.util.TooManyListenersException;

@Configuration
public class SipJainConfiguration {

    @Autowired
    private SipProperties sipProperties;

    @Bean
    public SipFactory sipFactory(){
        SipFactory sipFactory = SipFactory.getInstance();
        sipFactory.setPathName("gov.nist");
        return sipFactory;
    }

    @Bean
    public SipStack sipStack(SipFactory sipFactory) throws PeerUnavailableException {
        Properties properties = new Properties();
        properties.setProperty("javax.sip.STACK_NAME", "SipClient");
        properties.setProperty("javax.sip.IP_ADDRESS", sipProperties.getHost());
        /** 解决 481 Subscription does not exist */
        properties.setProperty("javax.sip.AUTOMATIC_DIALOG_SUPPORT", "off");
        properties.setProperty("gov.nist.javax.sip.DELIVER_UNSOLICITED_NOTIFY", "true");
        return sipFactory.createSipStack(properties);
    }

    @Bean
    public HeaderFactory headerFactory(SipFactory sipFactory) throws PeerUnavailableException {
        return sipFactory.createHeaderFactory();
    }

    @Bean
    public AddressFactory addressFactory(SipFactory sipFactory) throws PeerUnavailableException {
        return sipFactory.createAddressFactory();
    }

    @Bean
    public MessageFactory messageFactory(SipFactory sipFactory) throws PeerUnavailableException {
        return sipFactory.createMessageFactory();
    }

    @Bean
    public SipProvider sipProvider(SipStack sipStack, SipLayer sipLayer) throws TransportNotSupportedException, InvalidArgumentException, ObjectInUseException, TooManyListenersException {
        ListeningPoint udp = sipStack.createListeningPoint(sipProperties.getListenAddr(), sipProperties.getListenPort(), ListeningPoint.UDP);
        SipProvider sipProvider =  sipStack.createSipProvider(udp);
        sipProvider.addSipListener(sipLayer);
        return sipProvider;
    }

    @Bean
    public SdpFactory sdpFactory(SipFactory sipFactory){
        return SdpFactory.getInstance();
    }
}
