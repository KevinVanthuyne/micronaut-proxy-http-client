package com.example;

import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.client.ProxyHttpClient;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import org.reactivestreams.Publisher;

@Filter("/proxy/**")
public class ProxyFilter implements HttpServerFilter {

    private final ProxyHttpClient client;

    public ProxyFilter(ProxyHttpClient client) {
        this.client = client;
    }

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request,
                                                      ServerFilterChain chain) {
        MutableHttpRequest<?> newRequest = request.mutate()
                .uri(b -> b
                        .scheme("http")
                        .host("localhost")
                        .port(8083)
                        .replacePath(StringUtils.prependUri(
                                "",
                                request.getPath().substring("/proxy".length())
                        ))
                );

        return client.proxy(newRequest);
    }
}
