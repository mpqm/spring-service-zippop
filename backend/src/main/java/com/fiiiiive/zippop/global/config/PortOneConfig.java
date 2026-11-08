package com.fiiiiive.zippop.global.config;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Configuration
public class PortOneConfig {

    @Value("${imp.imp_key}")
    private String impKey;

    @Value("${imp.imp_secret}")
    private String impSecret;

    @Value("${imp.include_sandbox:false}")
    private boolean includeSandbox;

    @Bean
    public IamportClient iamportClient() {
        if (!includeSandbox) return new IamportClient(impKey, impSecret);

        HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
        Gson gson = new Gson();
        return new IamportClient(impKey, impSecret) {
            @Override
            public IamportResponse<Payment> paymentByImpUid(String impUid)
                    throws IamportResponseException, IOException {
                var auth = getAuth();
                if (auth == null || auth.getResponse() == null) {
                    throw new IOException("PortOne authentication response was empty");
                }

                String encodedId = URLEncoder.encode(impUid, StandardCharsets.UTF_8);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.iamport.kr/payments/" + encodedId + "?include_sandbox=true"))
                        .header("Authorization", auth.getResponse().getToken())
                        .timeout(Duration.ofSeconds(10))
                        .GET()
                        .build();
                try {
                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    if (response.statusCode() < 200 || response.statusCode() >= 300) {
                        throw new IOException("PortOne payment lookup returned HTTP " + response.statusCode());
                    }
                    IamportResponse<Payment> payment = gson.fromJson(response.body(),
                            new TypeToken<IamportResponse<Payment>>() {}.getType());
                    if (payment == null) throw new IOException("PortOne payment response was empty");
                    return payment;
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    throw new IOException("PortOne payment lookup was interrupted", exception);
                } catch (JsonParseException exception) {
                    throw new IOException("PortOne payment response was invalid", exception);
                }
            }
        };
    }

}
