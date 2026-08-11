PASOS PARA SUBIR A RAILWAY (1 por 1)

1. PREPARA EL REPO
   git add .
   git commit -m "feat: docker + railway config"
   git push origin main

2. ENTRA A RAILWAY (https://railway.app)
   - Crea un proyecto nuevo (New Project).
   - Selecciona "Deploy from GitHub Repo".
   - Conecta tu repo de Tacomoloco_Backend (monorepo con backend; el front está en ../tacomoloco_front si lo usas).

3. AÑADE EL PLUGIN POSTGRES (base de datos)
   - En el proyecto Railway: New > Database > Add PostgreSQL.
   - Copia el valor DATABASE_URL que te aparece (ej: postgresql://postgres:...:5432/postgres).

4. CONFIGURA LAS VARIABLES GLOBALES (Project Variables en Railway)
   - DATABASE_URL = Copia del paso 3.
   - POSTGRES_USER = postgres
   - POSTGRES_PASSWORD = postgres (o lo que Railway te dé)

5. CONFIGURA EL SERVICIO EUREKA (primer servicio a desplegar)
   - Railway detectará los subdirectorios con railway.json.
   - Selecciona servicio "eureka".
   - Variables locales (Service Variables):
     EUREKA_HOST = localhost (o el dominio si conectas a otro servicio)
   - Deploy.

6. CONFIGURA AUTHSERVER (segundo servicio)
   - Variables locales:
     SCHEMA = authserver
     DATABASE_URL = ${{ DATABASE_URL }}?currentSchema=authserver (o edita la URL del paso 3)
     EUREKA_HOST = eureka
     OAUTH2_ISSUER_URI = http://authserver.railway.internal:9000
     RSA_PUBLIC_KEY = <base64 del paso 7>
     RSA_PRIVATE_KEY = <base64 del paso 7>
   - Deploy.

7. GENERA LAS CLAVES RSA (base64)
   - En tu máquina local (en la carpeta del backend):
     bash generate-rsa-base64.sh
   - Copia los 2 bloques base64 (public / private) al paso 6.

8. CONFIGURA LOS SERVICIOS RESTANTES (usuarios, inventario, catalogo, pedidos, carrito, reportes)
   - Para cada servicio, variables locales:
     SCHEMA = <nombre_del_servicio> (ej: usuarios, inventario...)
     DATABASE_URL = ${DATABASE_URL}?currentSchema=<nombre>
     EUREKA_HOST = eureka
     OAUTH2_ISSUER_URI = http://authserver.railway.internal:9000
   - Deploy uno por uno o todos a la vez.

9. CONFIGURA API-GATEWAY
   - Variables locales:
     EUREKA_HOST = eureka
     OAUTH2_ISSUER_URI = http://authserver.railway.internal:9000
   - No necesita DATABASE_URL (no usa DB).
   - Deploy.

10. CONFIGURA FRONTEND (opcional, si quieres nginx + proxy)
    - Variables locales (si usas nginx en Railway): ninguna extra.
    - Deploy.
    - Nota: tu front ya está en ../tacomoloco_front con su Dockerfile y nginx.conf.

11. VERIFICA EL DESPLIEGUE
    - Comando local de verificación:
      bash verify-railway-ready.sh
    - En Railway Dashboard: revisa los logs de cada servicio para confirmar que arrancó sin errores.
    - Visita la URL del gateway que Railway te da para probar el API.

12. AJUSTA DOMINIOS INTERNOS (si Eureka no conecta)
    - Railway asigna `.railway.internal` como dominio entre servicios.
    - Si los servicios no se registran en Eureka, cambia `eureka.client.service-url.defaultZone` o usa directamente el dominio del servicio en Railway para los Feign Clients.
