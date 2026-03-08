# Requisitos del Usuario Final - TechShop Ecommerce

## Contexto
Tienda de electronica en linea. El cliente quiere vender sus productos por internet
a traves de una API REST. No requiere frontend web ni pasarela de pagos real.

---

## Requisitos Funcionales - Cliente (Comprador)

### RF001 - Listar productos
Ver todos los productos disponibles con nombre, imagen (URL), precio y stock.

### RF002 - Buscar productos por categoria
Filtrar productos por categoria: Laptops, Smartphones, Audifonos, Accesorios.
Cada categoria puede tener atributos distintos (ej: laptop tiene RAM y procesador,
smartphone tiene tamaño de pantalla y camara).

### RF003 - Ver detalle de un producto
Al seleccionar un producto, ver toda su informacion completa.

### RF004 - Agregar productos al carrito
Agregar productos al carrito indicando la cantidad deseada.

### RF005 - Ver carrito
Ver los items en el carrito, cantidades y total a pagar.

### RF006 - Quitar productos del carrito
Eliminar items del carrito.

### RF007 - Realizar pedido
Confirmar la compra desde el carrito. Se genera una orden con estatus PENDIENTE.
No se requiere pasarela de pago real.

### RF008 - Consultar ordenes
Ver historial de pedidos y su estatus (PENDIENTE, CONFIRMADA, ENVIADA).

---

## Requisitos Funcionales - Administrador

### RF009 - CRUD de productos
Crear, editar y eliminar productos desde la API.

### RF010 - Carga masiva de productos
Cargar productos desde un archivo CSV de forma automatica (50+ productos).

### RF011 - Gestionar categorias
Crear y listar categorias de productos.

---

## Fuera de Alcance

- Pasarela de pagos real (Stripe, PayPal)
- Envio de correos electronicos
- Frontend web (todo se consume via API REST)
- Roles y permisos complejos (usuario basico es suficiente)
- Subida de imagenes al servidor (se usa URL de imagen)

---

## Prioridades

| Prioridad | Requisitos |
|---|---|
| Alta | RF001, RF002, RF003, RF004, RF005, RF006, RF007, RF008 |
| Media | RF009, RF010, RF011 |
