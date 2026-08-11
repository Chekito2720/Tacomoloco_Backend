# Deploy en Railway

## Requisitos previos
- Cuenta Railway con GitHub conectado (monorepo).
- Plugin Postgres creado en Railway (o DATABASE_URL externo).

## Variables de entorno (por servicio en Railway Dashboard)

### Globales (todos los servicios):
- DATABASE_URL = `postgresql://postgres:...@...:5432/postgres` (Railway te lo da al crear el plugin)
- POSTGRES_USER=postgres
- POSTGRES_PASSWORD=postgres (o lo que Railway asigne)

### Authserver (authserver):
- SCHEMA=authserver
- RSA_PUBLIC_KEY = contenido base64 del archivo `authserver/src/main/resources/keys/public.pem` (sin cabeceras)
- RSA_PRIVATE_KEY = contenido base64 del archivo `authserver/src/main/resources/keys/private.pem`
- EUREKA_HOST=eureka
- OAUTH2_ISSUER_URI=http://authserver.railway.internal:9000 (o el dominio que Railway te dé)

### Usuarios:
- SCHEMA=usuarios
- DATABASE_URL = `postgresql://postgres:...@...:5432/postgres?currentSchema=usuarios`
- EUREKA_HOST=eureka
- OAUTH2_ISSUER_URI=http://authserver:9000

### Gateway (api-gateway):
- SCHEMA=api-gateway (o sin SCHEMA, no usa DB)
- DATABASE_URL = vacío (no usa DB)
- EUREKA_HOST=eureka
- OAUTH2_ISSUER_URI=http://authserver:9000

### Eureka:
- No usa DATABASE_URL ni SCHEMA. Solo EUREKA_HOST.

## Notas importantes
- **Eureka en Railway**: cada servicio tiene su propia red. Si quieres que Eureka funcione con los nombres internos, Railway permite `.railway.internal` como dominio entre servicios en el mismo proyecto. Asegúrate de que `spring.cloud.discovery.client.simple.instances` esté configurado o usa `eureka.instance.hostname` con el dominio `.railway.internal` asignado por Railway.
- **Postgres con schemas**: cada servicio usa `spring.flyway.schemas` para separar datos en una sola instancia. No necesitas 7 instancias de Postgres.
- **Claves RSA**: los archivos `.pem` ya no se cargan de `classpath:`. Se cargan de las variables de entorno `RSA_PUBLIC_KEY` y `RSA_PRIVATE_KEY`. Usa el script `generate-rsa-base64.sh` (ver más abajo) para codificar tus claves y pegarlas en el dashboard.
- **API Gateway**: expone en el puerto asignado por Railway (variable `PORT` o `8080`). El gateway recibe las peticiones externas y enruta con Feign/Eureka a los servicios internos.

## Script para codificar las claves RSA

```bash
# En el directorio authserver/src/main/resources/keys/
cat public.pem | base64 | tr -d '\n' > public.base64
cat private.pem | base64 | tr -d '\n' > private.base64
```

Pega el contenido de estos archivos `.base64` en las variables `RSA_PUBLIC_KEY` y `RSA_PRIVATE_KEY` del servicio `authserver` en Railway Dashboard.

## Orden de despliegue recomendado en Railway Dashboard
1. `postgres` (plugin)
2. `eureka`
3. `authserver`
4. `usuarios`, `inventario`, `catalogo`, `pedidos`, `carrito`, `reportes`
5. `api-gateway`
6. `frontend` (opcional, sólo si usas el servicio de nginx)
