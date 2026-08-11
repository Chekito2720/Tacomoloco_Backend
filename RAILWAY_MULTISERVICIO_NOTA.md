NOTA PARA RAILWAY (MULTI-SERVICIO MONOREPO)

El archivo railway.toml de arriba va en la raíz, pero Railway NO detecta automáticamente los sub-servicios del monorepo. Debes configurar manualmente cada servicio en Railway Dashboard:

1. New Service (cada servicio: authserver, usuarios, inventario, etc.)
2. Source: GitHub repo (tu repo de Tacomoloco)
3. Root Directory: authserver/  (o usuarios/, catalogo/, pedidos/, carrito/, reportes/, api-gateway/, eureka/)
4. Build Command / Deploy: Railway leerá automáticamente el Dockerfile dentro de ese subdirectorio.
5. Variables: agrega DATABASE_URL, SCHEMA, RSA_PUBLIC_KEY, etc. en el servicio correspondiente.

Cada subdirectorio ya tiene su railway.json (o puedes usar railway.toml por servicio). Railway leerá el archivo de configuración dentro del Root Directory.

IMPORTANTE: El error anterior (Railpack 0.35.0) ocurrió porque Railway intentó construir desde la raíz (.) sin encontrar start.sh. Al configurar Root Directory por servicio, Railway entrará al subdirectorio, encontrará Dockerfile y funcionará correctamente.
