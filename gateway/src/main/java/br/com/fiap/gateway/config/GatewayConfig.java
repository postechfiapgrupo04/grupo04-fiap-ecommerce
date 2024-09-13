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

        return builder.routes()
                // Rotas para o serviço de carrinho
                .route("Get carrinho by User ID", r -> r.path("/carrinho/usuario/**")
                        .and().method("GET").filters(f-> f.filters(authUserFilter))
                        .uri("http://ms-carrinho:8000"))
                .route("Create Carrinho", r -> r.path("/carrinho/usuario/**")
                        .and().method("POST").filters(f-> f.filters(authUserFilter))
                        .uri("http://ms-carrinho:8000"))
                .route("Delete Item Carrinho", r -> r.path("/carrinho/usuario/**")
                        .and().method("DELETE").filters(f-> f.filters(authUserFilter))
                        .uri("http://ms-carrinho:8000"))


                // Rotas para o serviço de itens
                .route("Get All Itens", r -> r.path("/item")
                        .uri("http://ms-item:8001"))
                .route("Get Item by ID", r -> r.path("/item/**")
                        .uri("http://ms-item:8001"))
                .route("Create Item", r -> r.path("/item")
                        .and().method("POST").filters(f-> f.filters(authAdminFilter))
                        .uri("http://ms-item:8001"))
                .route("Delete Item", r -> r.path("/item")
                        .and().method("DELETE").filters(f-> f.filters(authAdminFilter))
                        .uri("http://ms-item:8001"))


                // Rotas para o serviço de Login
                .route("auth-server",r -> r.path("/api/auth/login")
                        .uri("http://ms-login:8002"))
                .route("Create user",r -> r.path("/api/user")
                        .uri("http://ms-login:8002"))
                .route("auth-server",r -> r.path("/api/user/me")
                        //.and().method("GET").filters(f-> f.filters(authAdminFilter))
                        .uri("http://ms-login:8002"))

                // Rotas para o serviço de cupons
                .route("cupom-service-aplicar", r -> r.path("/cupom/aplicar")
                        .and().method("POST").filters(f-> f.filters(authUserFilter))
                        .uri("http://ms-pagamento:8003"))
                .route("cupom-service-listar", r -> r.path("/cupom/listar")
                        .and().method("GET").filters(f-> f.filters(authUserFilter))
                        .uri("http://ms-pagamento:8003"))
                .route("cupom-service-listar", r -> r.path("/cupom/criar")
                        .and().method("POST").filters(f-> f.filters(authUserFilter))
                        .uri("http://ms-pagamento:8003"))


                // Rotas para o serviço de pagamentos
                .route("pagamento-service-processar", r -> r.path("/pagamento/processar")
                        .and().method("POST").filters(f-> f.filters(authUserFilter))
                        .uri("http://ms-pagamento:8003"))
                .route("pagamento-service-listar-usuario", r -> r.path("/pagamento/usuario/{usuarioId}/pagamentos")
                        .and().method("GET").filters(f-> f.filters(authUserFilter))
                        .uri("http://ms-pagamento:8003"))

                .build();
    }
}
