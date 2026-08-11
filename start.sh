#!/bin/bash
set -e

echo "[Railway] Iniciando Tacomoloco (monoservicio con docker-compose interno)"

# Lanzamos servicios en orden con delays
java -jar /workspace/eureka/target/eureka-0.0.1-SNAPSHOT.jar > /dev/stdout 2>&1 &
echo "[start] Eureka lanzado (PID $!)"
sleep 15

java -jar /workspace/authserver/target/authserver-0.0.1-SNAPSHOT.jar > /dev/stdout 2>&1 &
echo "[start] Authserver lanzado (PID $!)"
sleep 15

java -jar /workspace/usuarios/target/usuarios-0.0.1-SNAPSHOT.jar > /dev/stdout 2>&1 &
echo "[start] Usuarios lanzado (PID $!)"
java -jar /workspace/inventario/target/inventario-0.0.1-SNAPSHOT.jar > /dev/stdout 2>&1 &
echo "[start] Inventario lanzado (PID $!)"
java -jar /workspace/catalogo/target/catalogo-0.0.1-SNAPSHOT.jar > /dev/stdout 2>&1 &
echo "[start] Catalogo lanzado (PID $!)"
java -jar /workspace/pedidos/target/pedidos-0.0.1-SNAPSHOT.jar > /dev/stdout 2>&1 &
echo "[start] Pedidos lanzado (PID $!)"
java -jar /workspace/carrito/target/carrito-0.0.1-SNAPSHOT.jar > /dev/stdout 2>&1 &
echo "[start] Carrito lanzado (PID $!)"
java -jar /workspace/reportes/target/reportes-0.0.1-SNAPSHOT.jar > /dev/stdout 2>&1 &
echo "[start] Reportes lanzado (PID $!)"
sleep 15

java -jar /workspace/api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar > /dev/stdout 2>&1 &
echo "[start] API Gateway lanzado (PID $!)"

# Esperamos a que alguno falle
wait -n
