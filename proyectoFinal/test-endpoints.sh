#!/usr/bin/env bash
# =============================================================================
# TechShop API - Script de pruebas de endpoints
#
# Uso:
#   ./test-endpoints.sh              -> flujo completo (recomendado)
#   ./test-endpoints.sh categorias   -> solo endpoints de categorias
#   ./test-endpoints.sh productos    -> solo endpoints de productos
#   ./test-endpoints.sh usuarios     -> solo endpoints de usuarios
#   ./test-endpoints.sh carrito      -> solo endpoints de carrito
#   ./test-endpoints.sh ordenes      -> solo endpoints de ordenes
#   ./test-endpoints.sh batch        -> solo carga masiva CSV
#
# Requisito: la app debe estar corriendo (docker compose up -d)
# =============================================================================

BASE_URL="http://localhost:8080"

# --- Colores para la salida ---
VERDE='\033[0;32m'
ROJO='\033[0;31m'
AMARILLO='\033[1;33m'
CYAN='\033[0;36m'
RESET='\033[0m'

# --- Variables globales para guardar IDs entre pasos ---
CATEGORIA_ID=""
PRODUCTO_ID=""
USUARIO_ID=""
CARRITO_ITEM_ID=""
ORDEN_ID=""

separador() {
  echo -e "\n${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${RESET}"
  echo -e "${CYAN}  $1${RESET}"
  echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${RESET}"
}

ejecutar() {
  local descripcion="$1"
  local comando="$2"

  echo -e "\n${AMARILLO}▶ $descripcion${RESET}"
  echo -e "  ${descripcion//?/─}"
  respuesta=$(eval "$comando" 2>/dev/null)
  codigo=$(eval "$comando -o /dev/null -w '%{http_code}'" 2>/dev/null)

  echo "$respuesta" | python3 -m json.tool 2>/dev/null || echo "$respuesta"

  if [[ "$codigo" =~ ^2 ]]; then
    echo -e "${VERDE}  ✔ HTTP $codigo${RESET}"
  else
    echo -e "${ROJO}  ✘ HTTP $codigo${RESET}"
  fi

  echo "$respuesta"
}

# =============================================================================
# 1. CATEGORIAS
# =============================================================================
pruebas_categorias() {
  separador "CATEGORIAS  /api/categorias"

  # Crear categoria
  echo -e "\n${AMARILLO}▶ POST /api/categorias — Crear categoria${RESET}"
  respuesta=$(curl -s -X POST "$BASE_URL/api/categorias" \
    -H "Content-Type: application/json" \
    -d '{
      "nombre": "Laptops",
      "descripcion": "Computadoras portatiles",
      "atributos": ["procesador", "ram", "almacenamiento"]
    }')
  echo "$respuesta" | python3 -m json.tool 2>/dev/null || echo "$respuesta"
  CATEGORIA_ID=$(echo "$respuesta" | python3 -c "import sys,json; print(json.load(sys.stdin).get('id',''))" 2>/dev/null)
  echo -e "${VERDE}  ✔ ID guardado: $CATEGORIA_ID${RESET}"

  # Listar todas
  echo -e "\n${AMARILLO}▶ GET /api/categorias — Listar todas${RESET}"
  curl -s -X GET "$BASE_URL/api/categorias" | python3 -m json.tool 2>/dev/null

  # Buscar por ID
  if [ -n "$CATEGORIA_ID" ]; then
    echo -e "\n${AMARILLO}▶ GET /api/categorias/$CATEGORIA_ID — Buscar por ID${RESET}"
    curl -s -X GET "$BASE_URL/api/categorias/$CATEGORIA_ID" | python3 -m json.tool 2>/dev/null
  fi

  # Actualizar
  if [ -n "$CATEGORIA_ID" ]; then
    echo -e "\n${AMARILLO}▶ PUT /api/categorias/$CATEGORIA_ID — Actualizar${RESET}"
    curl -s -X PUT "$BASE_URL/api/categorias/$CATEGORIA_ID" \
      -H "Content-Type: application/json" \
      -d '{
        "nombre": "Laptops y Notebooks",
        "descripcion": "Computadoras portatiles de todas las gamas",
        "atributos": ["procesador", "ram", "almacenamiento", "pantalla"]
      }' | python3 -m json.tool 2>/dev/null
  fi

  # Crear categoria para eliminar
  echo -e "\n${AMARILLO}▶ POST /api/categorias — Crear categoria temporal (para eliminar)${RESET}"
  id_temp=$(curl -s -X POST "$BASE_URL/api/categorias" \
    -H "Content-Type: application/json" \
    -d '{"nombre":"Temporal","descripcion":"Se eliminara"}' \
    | python3 -c "import sys,json; print(json.load(sys.stdin).get('id',''))" 2>/dev/null)
  echo -e "  ID temporal: $id_temp"

  if [ -n "$id_temp" ]; then
    echo -e "\n${AMARILLO}▶ DELETE /api/categorias/$id_temp — Eliminar${RESET}"
    codigo=$(curl -s -o /dev/null -w "%{http_code}" -X DELETE "$BASE_URL/api/categorias/$id_temp")
    echo -e "${VERDE}  ✔ HTTP $codigo${RESET}"
  fi
}

# =============================================================================
# 2. PRODUCTOS
# =============================================================================
pruebas_productos() {
  separador "PRODUCTOS  /api/productos"

  # Crear producto (requiere categoria existente)
  echo -e "\n${AMARILLO}▶ POST /api/productos — Crear producto${RESET}"
  respuesta=$(curl -s -X POST "$BASE_URL/api/productos" \
    -H "Content-Type: application/json" \
    -d '{
      "nombre": "Laptop Dell XPS 13",
      "descripcion": "Ultrabook 13 pulgadas, pantalla OLED",
      "precio": 24999.99,
      "stock": 15,
      "imagenUrl": "https://example.com/xps13.jpg",
      "categoria": "Laptops",
      "atributos": {
        "procesador": "Intel Core i7",
        "ram": "16GB",
        "almacenamiento": "512GB SSD"
      }
    }')
  echo "$respuesta" | python3 -m json.tool 2>/dev/null || echo "$respuesta"
  PRODUCTO_ID=$(echo "$respuesta" | python3 -c "import sys,json; print(json.load(sys.stdin).get('id',''))" 2>/dev/null)
  echo -e "${VERDE}  ✔ ID guardado: $PRODUCTO_ID${RESET}"

  # Listar todos
  echo -e "\n${AMARILLO}▶ GET /api/productos — Listar todos${RESET}"
  curl -s -X GET "$BASE_URL/api/productos" | python3 -m json.tool 2>/dev/null

  # Filtrar por categoria
  echo -e "\n${AMARILLO}▶ GET /api/productos?categoria=Laptops — Filtrar por categoria${RESET}"
  curl -s -X GET "$BASE_URL/api/productos?categoria=Laptops" | python3 -m json.tool 2>/dev/null

  # Buscar por ID
  if [ -n "$PRODUCTO_ID" ]; then
    echo -e "\n${AMARILLO}▶ GET /api/productos/$PRODUCTO_ID — Buscar por ID${RESET}"
    curl -s -X GET "$BASE_URL/api/productos/$PRODUCTO_ID" | python3 -m json.tool 2>/dev/null
  fi

  # Actualizar
  if [ -n "$PRODUCTO_ID" ]; then
    echo -e "\n${AMARILLO}▶ PUT /api/productos/$PRODUCTO_ID — Actualizar precio y stock${RESET}"
    curl -s -X PUT "$BASE_URL/api/productos/$PRODUCTO_ID" \
      -H "Content-Type: application/json" \
      -d '{
        "nombre": "Laptop Dell XPS 13 (2024)",
        "descripcion": "Ultrabook 13 pulgadas, pantalla OLED - Edicion 2024",
        "precio": 26999.99,
        "stock": 10,
        "imagenUrl": "https://example.com/xps13-2024.jpg",
        "categoria": "Laptops",
        "atributos": {
          "procesador": "Intel Core i7 14th Gen",
          "ram": "32GB",
          "almacenamiento": "1TB SSD"
        }
      }' | python3 -m json.tool 2>/dev/null
  fi

  # Crear producto para eliminar
  echo -e "\n${AMARILLO}▶ POST /api/productos — Crear producto temporal (para eliminar)${RESET}"
  id_temp=$(curl -s -X POST "$BASE_URL/api/productos" \
    -H "Content-Type: application/json" \
    -d '{"nombre":"Producto Temporal","descripcion":"Se eliminara","precio":1.00,"stock":1,"categoria":"Laptops"}' \
    | python3 -c "import sys,json; print(json.load(sys.stdin).get('id',''))" 2>/dev/null)
  echo -e "  ID temporal: $id_temp"

  if [ -n "$id_temp" ]; then
    echo -e "\n${AMARILLO}▶ DELETE /api/productos/$id_temp — Eliminar${RESET}"
    codigo=$(curl -s -o /dev/null -w "%{http_code}" -X DELETE "$BASE_URL/api/productos/$id_temp")
    echo -e "${VERDE}  ✔ HTTP $codigo${RESET}"
  fi
}

# =============================================================================
# 3. USUARIOS
# =============================================================================
pruebas_usuarios() {
  separador "USUARIOS  /api/usuarios"

  # Registrar usuario
  echo -e "\n${AMARILLO}▶ POST /api/usuarios/registro — Registrar usuario${RESET}"
  respuesta=$(curl -s -X POST "$BASE_URL/api/usuarios/registro" \
    -H "Content-Type: application/json" \
    -d '{
      "nombre": "Ana Torres",
      "email": "ana.torres@techshop.com",
      "password": "segura123"
    }')
  echo "$respuesta" | python3 -m json.tool 2>/dev/null || echo "$respuesta"
  USUARIO_ID=$(echo "$respuesta" | python3 -c "import sys,json; print(json.load(sys.stdin).get('id',''))" 2>/dev/null)
  echo -e "${VERDE}  ✔ ID guardado: $USUARIO_ID${RESET}"

  # Listar todos
  echo -e "\n${AMARILLO}▶ GET /api/usuarios — Listar todos${RESET}"
  curl -s -X GET "$BASE_URL/api/usuarios" | python3 -m json.tool 2>/dev/null

  # Buscar por ID
  if [ -n "$USUARIO_ID" ]; then
    echo -e "\n${AMARILLO}▶ GET /api/usuarios/$USUARIO_ID — Buscar por ID${RESET}"
    curl -s -X GET "$BASE_URL/api/usuarios/$USUARIO_ID" | python3 -m json.tool 2>/dev/null
  fi

  # Error: email duplicado
  echo -e "\n${AMARILLO}▶ POST /api/usuarios/registro — Email duplicado (debe dar 400)${RESET}"
  codigo=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/usuarios/registro" \
    -H "Content-Type: application/json" \
    -d '{"nombre":"Otro","email":"ana.torres@techshop.com","password":"123"}')
  echo -e "${ROJO}  ✔ HTTP $codigo (esperado 400 o 409)${RESET}"

  # Error: usuario inexistente
  echo -e "\n${AMARILLO}▶ GET /api/usuarios/99999 — Usuario inexistente (debe dar 400)${RESET}"
  codigo=$(curl -s -o /dev/null -w "%{http_code}" -X GET "$BASE_URL/api/usuarios/99999")
  echo -e "${ROJO}  ✔ HTTP $codigo (esperado 400)${RESET}"
}

# =============================================================================
# 4. CARRITO
# =============================================================================
pruebas_carrito() {
  separador "CARRITO  /api/carrito"

  if [ -z "$USUARIO_ID" ] || [ -z "$PRODUCTO_ID" ]; then
    echo -e "${ROJO}  ✘ Se necesita USUARIO_ID y PRODUCTO_ID. Ejecuta el flujo completo.${RESET}"
    return
  fi

  # Agregar primer item
  echo -e "\n${AMARILLO}▶ POST /api/carrito — Agregar producto al carrito (cantidad=2)${RESET}"
  respuesta=$(curl -s -X POST \
    "$BASE_URL/api/carrito?usuarioId=$USUARIO_ID&productoId=$PRODUCTO_ID&cantidad=2")
  echo "$respuesta" | python3 -m json.tool 2>/dev/null || echo "$respuesta"
  CARRITO_ITEM_ID=$(echo "$respuesta" | python3 -c "import sys,json; print(json.load(sys.stdin).get('id',''))" 2>/dev/null)
  echo -e "${VERDE}  ✔ Item ID guardado: $CARRITO_ITEM_ID${RESET}"

  # Ver carrito
  echo -e "\n${AMARILLO}▶ GET /api/carrito?usuarioId=$USUARIO_ID — Ver carrito${RESET}"
  curl -s -X GET "$BASE_URL/api/carrito?usuarioId=$USUARIO_ID" | python3 -m json.tool 2>/dev/null

  # Eliminar item
  if [ -n "$CARRITO_ITEM_ID" ]; then
    echo -e "\n${AMARILLO}▶ DELETE /api/carrito/$CARRITO_ITEM_ID — Eliminar item${RESET}"
    codigo=$(curl -s -o /dev/null -w "%{http_code}" -X DELETE "$BASE_URL/api/carrito/$CARRITO_ITEM_ID")
    echo -e "${VERDE}  ✔ HTTP $codigo${RESET}"
  fi

  # Verificar carrito vacio
  echo -e "\n${AMARILLO}▶ GET /api/carrito?usuarioId=$USUARIO_ID — Carrito vacio tras eliminar${RESET}"
  curl -s -X GET "$BASE_URL/api/carrito?usuarioId=$USUARIO_ID" | python3 -m json.tool 2>/dev/null

  # Volver a agregar para el flujo de ordenes
  echo -e "\n${AMARILLO}▶ POST /api/carrito — Re-agregar producto (para crear orden despues)${RESET}"
  respuesta=$(curl -s -X POST \
    "$BASE_URL/api/carrito?usuarioId=$USUARIO_ID&productoId=$PRODUCTO_ID&cantidad=1")
  echo "$respuesta" | python3 -m json.tool 2>/dev/null || echo "$respuesta"
  CARRITO_ITEM_ID=$(echo "$respuesta" | python3 -c "import sys,json; print(json.load(sys.stdin).get('id',''))" 2>/dev/null)
  echo -e "${VERDE}  ✔ Item ID: $CARRITO_ITEM_ID${RESET}"

  # Error: producto inexistente
  echo -e "\n${AMARILLO}▶ POST /api/carrito — Producto inexistente (debe dar 400)${RESET}"
  codigo=$(curl -s -o /dev/null -w "%{http_code}" -X POST \
    "$BASE_URL/api/carrito?usuarioId=$USUARIO_ID&productoId=000000000000000000000000&cantidad=1")
  echo -e "${ROJO}  ✔ HTTP $codigo (esperado 400)${RESET}"
}

# =============================================================================
# 5. ORDENES
# =============================================================================
pruebas_ordenes() {
  separador "ORDENES  /api/ordenes"

  if [ -z "$USUARIO_ID" ]; then
    echo -e "${ROJO}  ✘ Se necesita USUARIO_ID. Ejecuta el flujo completo.${RESET}"
    return
  fi

  # Crear orden desde carrito
  echo -e "\n${AMARILLO}▶ POST /api/ordenes?usuarioId=$USUARIO_ID — Crear orden desde carrito${RESET}"
  respuesta=$(curl -s -X POST "$BASE_URL/api/ordenes?usuarioId=$USUARIO_ID")
  echo "$respuesta" | python3 -m json.tool 2>/dev/null || echo "$respuesta"
  ORDEN_ID=$(echo "$respuesta" | python3 -c "import sys,json; print(json.load(sys.stdin).get('id',''))" 2>/dev/null)
  echo -e "${VERDE}  ✔ Orden ID guardada: $ORDEN_ID${RESET}"

  # Listar ordenes del usuario
  echo -e "\n${AMARILLO}▶ GET /api/ordenes?usuarioId=$USUARIO_ID — Listar ordenes del usuario${RESET}"
  curl -s -X GET "$BASE_URL/api/ordenes?usuarioId=$USUARIO_ID" | python3 -m json.tool 2>/dev/null

  # Detalle de la orden
  if [ -n "$ORDEN_ID" ]; then
    echo -e "\n${AMARILLO}▶ GET /api/ordenes/$ORDEN_ID — Detalle de la orden${RESET}"
    curl -s -X GET "$BASE_URL/api/ordenes/$ORDEN_ID" | python3 -m json.tool 2>/dev/null
  fi

  # Verificar que el carrito quedo vacio tras la orden
  echo -e "\n${AMARILLO}▶ GET /api/carrito?usuarioId=$USUARIO_ID — Carrito vacio tras crear orden${RESET}"
  curl -s -X GET "$BASE_URL/api/carrito?usuarioId=$USUARIO_ID" | python3 -m json.tool 2>/dev/null

  # Error: carrito vacio
  echo -e "\n${AMARILLO}▶ POST /api/ordenes?usuarioId=$USUARIO_ID — Carrito vacio (debe dar 400)${RESET}"
  codigo=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/ordenes?usuarioId=$USUARIO_ID")
  echo -e "${ROJO}  ✔ HTTP $codigo (esperado 400)${RESET}"
}

# =============================================================================
# 6. BATCH
# =============================================================================
pruebas_batch() {
  separador "BATCH  /api/batch"

  echo -e "\n${AMARILLO}▶ POST /api/batch/cargar-productos — Carga masiva desde CSV${RESET}"
  echo -e "  (Carga el archivo productos.csv incluido en el proyecto)"
  respuesta=$(curl -s -X POST "$BASE_URL/api/batch/cargar-productos")
  echo "$respuesta" | python3 -m json.tool 2>/dev/null || echo "$respuesta"
  codigo=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/batch/cargar-productos")
  echo -e "${VERDE}  ✔ HTTP $codigo${RESET}"
}

# =============================================================================
# FLUJO COMPLETO
# =============================================================================
flujo_completo() {
  echo -e "\n${CYAN}╔══════════════════════════════════════════════════════╗${RESET}"
  echo -e "${CYAN}║       TECHSHOP - PRUEBA DE TODOS LOS ENDPOINTS       ║${RESET}"
  echo -e "${CYAN}║              Flujo completo de la app                ║${RESET}"
  echo -e "${CYAN}╚══════════════════════════════════════════════════════╝${RESET}"
  echo -e "  Base URL: ${AMARILLO}$BASE_URL${RESET}"

  pruebas_categorias
  pruebas_productos
  pruebas_usuarios
  pruebas_carrito
  pruebas_ordenes
  pruebas_batch

  separador "RESUMEN FINAL"
  echo -e "  ${VERDE}Categoria ID : $CATEGORIA_ID${RESET}"
  echo -e "  ${VERDE}Producto ID  : $PRODUCTO_ID${RESET}"
  echo -e "  ${VERDE}Usuario ID   : $USUARIO_ID${RESET}"
  echo -e "  ${VERDE}Carrito Item : $CARRITO_ITEM_ID${RESET}"
  echo -e "  ${VERDE}Orden ID     : $ORDEN_ID${RESET}"
  echo -e "\n  ${CYAN}Swagger UI: $BASE_URL/swagger-ui.html${RESET}"
  echo -e "  ${CYAN}API Docs  : $BASE_URL/api-docs${RESET}\n"
}

# =============================================================================
# PUNTO DE ENTRADA
# =============================================================================
case "${1:-completo}" in
  categorias) pruebas_categorias ;;
  productos)  pruebas_productos  ;;
  usuarios)   pruebas_usuarios   ;;
  carrito)    pruebas_carrito    ;;
  ordenes)    pruebas_ordenes    ;;
  batch)      pruebas_batch      ;;
  *)          flujo_completo     ;;
esac
