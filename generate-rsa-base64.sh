#!/bin/bash
# Codifica los archivos .pem para Railway (base64 sin cabeceras)
set -e

FILE_PUB="authserver/src/main/resources/keys/public.pem"
FILE_PRIV="authserver/src/main/resources/keys/private.pem"

if [ ! -f "$FILE_PUB" ]; then
    echo "ERROR: No se encuentra $FILE_PUB"
    exit 1
fi

if [ ! -f "$FILE_PRIV" ]; then
    echo "ERROR: No se encuentra $FILE_PRIV"
    exit 1
fi

echo "=== Base64 para Railway (copiar en variables RSA_PUBLIC_KEY / RSA_PRIVATE_KEY) ==="
echo "--- PUBLIC ---"
cat "$FILE_PUB" | base64 | tr -d '\n' | echo -n ""
echo ""
echo "--- PRIVATE ---"
cat "$FILE_PRIV" | base64 | tr -d '\n' | echo -n ""
echo ""
echo "=== Fin ==="
