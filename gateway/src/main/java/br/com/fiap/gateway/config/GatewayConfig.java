package br.com.fiap.gateway.config;

import br.com.fiap.gateway.filter.AuthAdminFilter;
import br.com.fiap.gateway.filter.AuthUserFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class GatewayConfig {

    private final AuthUserFilter authUserFilter;
    private final AuthAdminFilter authAdminFilter;

    public GatewayConfig(AuthUserFilter authUserFilter, AuthAdminFilter authAdminFilter) {
        this.authUserFilter = authUserFilter;
        this.authAdminFilter = authAdminFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        // adding 2 rotes to first microservice as we need to log request body if method is POST
        return builder.routes()
                /*.route("first-microservice",r -> r.path("/first")
                        .and().method("POST")
                        .and().readBody(Student.class, s -> true).filters(f -> f.filters(requestFilter, authFilter))
                        .uri("http://localhost:8081"))
                .route("first-microservice",r -> r.path("/first")
                        .and().method("GET").filters(f-> f.filters(authFilter))
                        .uri("http://localhost:8081"))
                .route("second-microservice",r -> r.path("/second")
                        .and().method("POST")
                        .and().readBody(Company.class, s -> true).filters(f -> f.filters(requestFilter, authFilter))
                        .uri("http://localhost:8082"))
                .route("second-microservice",r -> r.path("/second")
                        .and().method("GET").filters(f-> f.filters(authFilter))
                        .uri("http://localhost:8082"))*/
                .route("auth-server",r -> r.path("/api/auth/login")
                        .uri("http://localhost:8002"))
                .route("auth-server",r -> r.path("/api/user/me")
                        //.and().method("GET").filters(f-> f.filters(authAdminFilter))
                        .uri("http://localhost:8002"))

                // Rotas para o serviço de cupons
                .route("cupom-service-aplicar", r -> r.path("/cupom/aplicar")
                        .uri("http://localhost:8003"))
                .route("cupom-service-listar", r -> r.path("/cupom/listar")
                        .uri("http://localhost:8003"))

                // Rotas para o serviço de pagamentos
                .route("pagamento-service-processar", r -> r.path("/pagamento/processar")
                        .uri("http://localhost:8003"))
                .route("pagamento-service-listar-usuario", r -> r.path("/pagamento/usuario/{usuarioId}/pagamentos")
                        .uri("http://localhost:8003"))

                .build();
    }
    /*
    @Bean
    public GlobalFilter customFilter() {
        return new AuthUserFilter();
    }

     */
}
