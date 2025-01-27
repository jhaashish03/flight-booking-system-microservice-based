package com.ewallet.userservice.services;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.annotation.Nullable;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class VersionedAsyncRestClient {

  private final WebClient webClient;

  public VersionedAsyncRestClient(WebClient webClient) {
    this.webClient = webClient;
  }

  @Async//("customTaskExecutor")
  public <T> CompletableFuture<ResponseEntity<T>> post(String product, String url, boolean isBsl, Object request,
    Class<T> responseType) {
    try {
       return webClient.post()
			.uri(getVersionedUrl(url, product, isBsl))
			.bodyValue(request)
			.retrieve()
			.toEntity(responseType)
			.toFuture();
    } catch (Exception e) {
       throw new RuntimeException(e);
    }
  }

  @Async//("customTaskExecutor")
  public CompletableFuture<ResponseEntity<Void>> post(String product, String url, boolean isBsl, Object request,
     HttpHeaders headers) {
     try {
       return webClient.post()
 			.uri(url)
			.headers(h -> h.addAll(headers))
			.bodyValue(request)
			.retrieve()
			.toBodilessEntity()
			.toFuture();
	} catch (Exception e) {
		throw new RuntimeException(e);
	}
  }


  @Async//("customTaskExecutor")
  public <T> CompletableFuture<ResponseEntity<List<T>>> get(String product, String url, boolean isBsl,
                                                           @Nullable MultiValueMap<String, String> requestParams, Class<T> responseType) {
    try {
       return webClient.get().uri(URI.create(url))
               .accept(MediaType.APPLICATION_JSON)
               .retrieve().toEntityList(responseType)
               .toFuture();
    } catch (Exception e) {
       throw new RuntimeException(e);
    }
 }

  private String getVersionedUrl(String url, String product, boolean isBsl) {
    return url; //isBsl ? getBslVersionedUrl(url, product) : getVclVersionedUrl(url, product);
  }

  @Async
  public <T> CompletableFuture<ResponseEntity<T>> post(String url, Object request, Class<T> responseType,HttpHeaders headers) {
    try {

      return webClient.post()
        .uri(url)
        .bodyValue(request)
              .headers(h -> h.addAll(headers))
        .retrieve()
        .toEntity(responseType)
        .toFuture();
    } catch (Exception e) {
       throw new RuntimeException(e);
    }
  }
    public <T> CompletableFuture<ResponseEntity<List<T>>> post(String url, boolean isList,Object request, Class<T> responseType,HttpHeaders headers) {
        try {

            return webClient.post()
                    .uri(url)
                    .headers(h -> h.addAll(headers))
                    .retrieve().toEntityList(responseType)
                    .toFuture();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
  @Async//("customTaskExecutor")
  public <T> CompletableFuture<ResponseEntity<T>> get(String product, String url,
                                                             HttpHeaders headers, Class<T> responseType) {
      try {
          return webClient.get().uri(URI.create(url))
                  .accept(MediaType.APPLICATION_JSON)
                  .headers(h -> h.addAll(headers))
                  .retrieve().toEntity(responseType)
                  .toFuture();
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
  }

  @Async
  public <T> CompletableFuture<ResponseEntity<T>> patch(String url, Object request, Class<T> responseType,HttpHeaders headers) {
      try {

          return webClient.patch()
                  .uri(url)
                  .bodyValue(request)
                  .headers(h -> h.addAll(headers))
                  .retrieve()
                  .toEntity(responseType)
                  .toFuture();
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
  }
    
  @Async
  public <T> CompletableFuture<ResponseEntity<T>> post(String url, MultiValueMap<String, String> paramMap, Class<T> responseType, HttpHeaders headers) {
    try {
      return webClient.post()
        .uri(url)
        .body(BodyInserters.fromFormData(paramMap))
        .headers(h -> h.addAll(headers))
        .retrieve()
        .toEntity(responseType)
        .toFuture();
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
  }

    public <T> CompletableFuture<ResponseEntity<T>> post(String url, MultipartBodyBuilder builder,
                    Class<T> responseType, HttpHeaders headers) {
        try {
            return webClient.post()
                    .uri(url)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                     .body(BodyInserters.fromMultipartData(builder.build()))
                    .headers(h -> h.addAll(headers))
                    .retrieve()
                    .toEntity(responseType)
                    .toFuture();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
  @Async
  public <T> CompletableFuture<ResponseEntity<T>> delete(String body, String url,
                                                      HttpHeaders headers, Class<T> responseType) {
      try {
          return webClient.delete().
                  uri(url)
                  .headers(h -> h.addAll(headers))
                  .retrieve()
                  .toEntity(responseType)
                  .toFuture();
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
  }
}
