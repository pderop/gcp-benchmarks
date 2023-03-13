package org.example;

import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import reactor.netty.http.Http11SslContextSpec;
import reactor.netty.http.Http2SslContextSpec;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServer;
import reactor.netty.resources.ConnectionProvider;

import java.security.cert.CertificateException;

public class ServerApplication {
    static final String HOST = System.getProperty("HOST", "0");
    static final String PROTOCOL = System.getProperty("PROTOCOL", "H1");
    static final int PORT = Integer.parseInt(System.getProperty("PORT", "8080"));
    static final String BACKEND_HOST = System.getProperty("BACKEND_HOST", "127.0.0.1");
    static final String BACKEND_PORT = System.getProperty("BACKEND_PORT", "8090");

    static final ConnectionProvider PROVIDER =
            ConnectionProvider.builder("gateway")
                    .maxConnections(500)
                    .pendingAcquireMaxCount(8 * 500)
                    .build();

    static final HttpClient CLIENT = configure(HttpClient.create(PROVIDER));

    static HttpClient configure(HttpClient client) {
        return switch (PROTOCOL) {
            case "H1" -> client
                    .baseUrl("http://" + BACKEND_HOST + ":" + BACKEND_PORT);

            case "H1S" -> client
                    .secure(spec -> spec.sslContext(Http11SslContextSpec.forClient().configure(builder -> builder.trustManager(InsecureTrustManagerFactory.INSTANCE))))
                    .baseUrl("https://" + BACKEND_HOST + ":" + BACKEND_PORT);

            case "H2" -> client
                    .protocol(HttpProtocol.H2)
                    .secure(spec -> spec.sslContext(Http2SslContextSpec.forClient().configure(builder -> builder.trustManager(InsecureTrustManagerFactory.INSTANCE))))
                    .baseUrl("https://" + BACKEND_HOST + ":" + BACKEND_PORT);

            default -> throw new IllegalArgumentException("Invalid protocol: " + PROTOCOL);
        };
    }

    static HttpServer configure(HttpServer server) {
        try {
            SelfSignedCertificate cert = new SelfSignedCertificate();

            return switch (PROTOCOL) {
                case "H1" -> server
                        .protocol(HttpProtocol.HTTP11);

                case "H2C" -> server
                        .protocol(HttpProtocol.H2C);

                case "H1S" -> server
                        .secure(spec -> spec.sslContext(Http11SslContextSpec.forServer(cert.certificate(), cert.privateKey())))
                        .protocol(HttpProtocol.HTTP11);

                case "H2" -> server
                        .secure(spec -> spec.sslContext(Http2SslContextSpec.forServer(cert.certificate(), cert.privateKey())))
                        .protocol(HttpProtocol.H2);

                default -> throw new IllegalArgumentException("Invalid protocol: " + PROTOCOL);
            };
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Server starting (protocol=" + PROTOCOL + ").");
        configure(HttpServer.create().host(HOST).port(PORT))
                .route(RouterFunctionConfig.routesBuilder())
                .doOnBound(server -> System.out.println("Server is bound on " + server.address()))
                .bindNow()
                .onDispose()
                .block();
    }
}
