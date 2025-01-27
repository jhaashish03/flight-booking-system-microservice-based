package com.ewallet.userservice.services;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Map;
import java.util.function.Consumer;

public class ConnectionUtil {

	//private static Logger serviceLogger = LogManager.getLogger("service");

//  public static Mono<ClientResponse> exchangeFilterResponseProcessor(ClientResponse response) {
//
//    try {
//      response.bodyToMono(BaseResponse.class).doOnNext(next -> {
//        if (!CollectionUtils.isEmpty(next.getTransactionStatuses())) {
//          if (next.getTransactionStatuses().stream()
//              .anyMatch(r -> r.getCategory() == StatusCategory.TIMEOUT)) {
//            throw new ProviderTimeoutException(next.getTransactionStatuses());
//          } else if (next.areAnyFailures()) {
//            throw new BackendServiceFailureException(next.getTransactionStatuses());
//          }
//        }
//      });
//    } catch (ClassCastException e) {
//      serviceLogger.debug(
//          "Response does not extend BaseResponse and cannot be processed using VCL filter");
//    }
//    return Mono.just(response);
//  }

  public static Consumer<HttpHeaders> createDefaultHeaders(Map<String, String> headersMap) {
    return headers -> {
      headers.set(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON));
      headersMap.forEach(headers::set);
    };
  }

  /**
   * Copying MDC context across webclient chain
   * @return
   */
//  public static ExchangeFilterFunction copyMDCMap() {
//    return (request, next) -> {
//      Map<String, Object> contextMap = MDC.getMap();
//      return next.exchange(request).doOnNext(value -> {
//        if (contextMap != null) {
//          contextMap.entrySet().forEach(entry -> MDC.put(entry.getKey(), entry.getValue()));
//        }
//      });
//    };
//  }
}
