package com.tacomoloco.api_gateway.config;

import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions;
import org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class GatewayConfig {

    @Bean
    public RouterFunction<ServerResponse> catalogoRoutes() {
        return GatewayRouterFunctions.route("catalogo")
                .route(GatewayRequestPredicates.path("/api/catalogo/**"), HandlerFunctions.http())
                .filter(FilterFunctions.stripPrefix(1))
                .filter(LoadBalancerFilterFunctions.lb("catalogo"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(cb -> cb.setId("catalogo").setStatusCodes("500", "502", "503")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventarioRoutes() {
        return GatewayRouterFunctions.route("inventario")
                .route(GatewayRequestPredicates.path("/api/inventario/**"), HandlerFunctions.http())
                .filter(FilterFunctions.stripPrefix(1))
                .filter(LoadBalancerFilterFunctions.lb("inventario"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(cb -> cb.setId("inventario").setStatusCodes("500", "502", "503")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> pedidosRoutes() {
        return GatewayRouterFunctions.route("pedidos")
                .route(GatewayRequestPredicates.path("/api/pedidos/**"), HandlerFunctions.http())
                .filter(FilterFunctions.stripPrefix(1))
                .filter(LoadBalancerFilterFunctions.lb("pedidos"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(cb -> cb.setId("pedidos").setStatusCodes("500", "502", "503")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> usuariosRoutes() {
        return GatewayRouterFunctions.route("usuarios")
                .route(GatewayRequestPredicates.path("/api/usuarios/**"), HandlerFunctions.http())
                .filter(FilterFunctions.stripPrefix(1))
                .filter(LoadBalancerFilterFunctions.lb("usuarios"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(cb -> cb.setId("usuarios").setStatusCodes("500", "502", "503")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> reportesRoutes() {
        return GatewayRouterFunctions.route("reportes")
                .route(GatewayRequestPredicates.path("/api/reportes/**"), HandlerFunctions.http())
                .filter(FilterFunctions.stripPrefix(1))
                .filter(LoadBalancerFilterFunctions.lb("reportes"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(cb -> cb.setId("reportes").setStatusCodes("500", "502", "503")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> authRoutes() {
        return GatewayRouterFunctions.route("auth")
                .route(GatewayRequestPredicates.path("/api/auth/**"), HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("authserver"))
                .filter((request, next) -> {
                    // Skip OAuth2 resource server for auth endpoints
                    return next.handle(request);
                })
                .build();
    }

@Bean
    public RouterFunction<ServerResponse> carritoRoutes() {
        return GatewayRouterFunctions.route("carrito")
                .route(GatewayRequestPredicates.path("/api/carrito/**"), HandlerFunctions.http())
                .filter(FilterFunctions.stripPrefix(1))
                .filter(LoadBalancerFilterFunctions.lb("carrito"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(cb -> cb.setId("carrito").setStatusCodes("500", "502", "503")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> notificacionesRoutes() {
        return GatewayRouterFunctions.route("notificaciones")
                .route(GatewayRequestPredicates.path("/api/notificaciones/**"), HandlerFunctions.http())
                .filter(FilterFunctions.stripPrefix(1))
                .filter(LoadBalancerFilterFunctions.lb("pedidos"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(cb -> cb.setId("notificaciones").setStatusCodes("500", "502", "503")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> pagosRoutes() {
        return GatewayRouterFunctions.route("pagos")
                .route(GatewayRequestPredicates.path("/api/pagos/**"), HandlerFunctions.http())
                .filter(FilterFunctions.stripPrefix(1))
                .filter(LoadBalancerFilterFunctions.lb("pedidos"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(cb -> cb.setId("pagos").setStatusCodes("500", "502", "503")))
                .build();
    }
}
