package com.ewallet.userservice.services;


import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.util.HashMap;
import java.util.Map;

@RefreshScope
@Configuration
public class AsyncRestClientBean {

    //private final WebClientLoggingInterceptor webClientLoggingInterceptor;
/*    public AsyncRestClientBean(WebClientLoggingInterceptor webClientLoggingInterceptor) {
        this.webClientLoggingInterceptor = webClientLoggingInterceptor;
    }*/
    @Bean
    @Primary
    public VersionedAsyncRestClient versionedAsyncRestClient() throws SSLException {

//    SslContext sslContext = SslContextBuilder.forClient()
//        .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
//
//    HttpClient httpClient = HttpClient.create()
//        .resolver(DefaultAddressResolverGroup.INSTANCE)
//        .secure(s -> s.sslContext(sslContext))
//        .doOnDisconnected(con -> con.dispose());
       /* HttpClient httpClient=new HttpClient(){
            @Override
            public Request newRequest(URI uri) {
                Request request = super.newRequest(uri);
                return webClientLoggingInterceptor.enhance(request);
            }
        };*/

        Map<String, String> defaultHeaders = new HashMap<>();
        //defaultHeaders.put(authHeaderName, authKeyValue);
        // default accept and content type added
/** ashish code
 WebClient webClient = WebClient.builder()
 .baseUrl(host)
 .defaultHeaders(ConnectionUtil.createDefaultHeaders(defaultHeaders)).
 clientConnector(new JettyClientHttpConnector(httpClient))*/
//        .filter(ExchangeFilterFunction.ofResponseProcessor(
//            ConnectionUtil::exchangeFilterResponseProcessor))
        //.filter(new LoggingIdFilter())
//        .filter(ClientLoggingFilter.logRequest())
//        .filter(ClientLoggingFilter.logResponse())
        //.filter(BSLConnectionUtil.copyMDCMap())
        // .build();

        SslContext sslContext = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();

        HttpClient httpClient = HttpClient.create()
                .resolver(DefaultAddressResolverGroup.INSTANCE)
                .secure(s -> s.sslContext(sslContext));

        WebClient webClient = WebClient.builder()
                //.defaultHeaders(BSLConnectionUtil.createDefaultHeaders(defaultHeaders))
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                //.filter(ExchangeFilterFunction.ofResponseProcessor(
                //  BSLConnectionUtil::exchangeFilterResponseProcessor))
                .exchangeStrategies(ExchangeStrategies
                        .builder()
                        .codecs(codecs -> codecs
                                .defaultCodecs()
                                .maxInMemorySize(-1))
                        .build())
                /* .filter(new LoggingIdFilter())
                 .filter(ClientLoggingFilter.logRequest())
                 .filter(ClientLoggingFilter.logResponse())*/
                .build();
        return new VersionedAsyncRestClient(webClient);//, versioningService);
    }
}