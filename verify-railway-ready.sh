#!/bin/bash
set -e

FAILURES=0

echo "=== Verificación Railway Ready ==="

for svc in eureka authserver usuarios inventario catalogo pedidos carrito reportes api-gateway; do
    echo "--- $svc ---"
    
    # 1. railway.json
    if [ -f "$svc/railway.json" ]; then
        echo "  [OK] railway.json"
    else
        echo "  [FAIL] railway.json FALTA"
        FAILURES=$((FAILURES+1))
    fi
    
    # 2. Dockerfile
    if [ -f "$svc/Dockerfile" ]; then
        echo "  [OK] Dockerfile"
    else
        echo "  [FAIL] Dockerfile FALTA"
        FAILURES=$((FAILURES+1))
    fi
    
    # 3. DATABASE_URL en properties (excepto eureka y gateway)
    if [ "$svc" = "eureka" ] || [ "$svc" = "api-gateway" ]; then
        echo "  [SKIP] DATABASE_URL no requerido"
    else
        if grep -q 'DATABASE_URL' "$svc/src/main/resources/application.properties"; then
            echo "  [OK] DATABASE_URL en properties"
        else
            echo "  [FAIL] DATABASE_URL en properties FALTA"
            FAILURES=$((FAILURES+1))
        fi
    fi
    
    # 4. flyway.schemas o default_schema (excepto eureka)
    if [ "$svc" = "eureka" ]; then
        echo "  [SKIP] Schema no aplica"
    else
        if grep -q 'spring.flyway.schemas=' "$svc/src/main/resources/application.properties"; then
            echo "  [OK] Schema configurado"
        else
            echo "  [FAIL] Schema (spring.flyway.schemas) FALTA"
            FAILURES=$((FAILURES+1))
        fi
    fi
done

echo ""
if [ $FAILURES -gt 0 ]; then
    echo "RESULTADO: $FAILURES errores encontrados. Corrige antes de subir a Railway."
    exit 1
else
    echo "RESULTADO: Todo listo para Railway."
    exit 0
fi
