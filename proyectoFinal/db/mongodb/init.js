// =============================================================================
// TechShop Ecommerce - Script de inicializacion MongoDB
// Se ejecuta automaticamente solo la primera vez que se crea el contenedor
// =============================================================================

// Crear usuario de aplicacion para la base de datos techshop
db = db.getSiblingDB('techshop');

db.createUser({
    user: 'techshop_user',
    pwd: 'techshop_pass',
    roles: [
        { role: 'readWrite', db: 'techshop' }
    ]
});

// Crear collections
db.createCollection('categorias');
db.createCollection('productos');

// Insertar categorias iniciales
db.categorias.insertMany([
    { nombre: 'Laptops', descripcion: 'Computadoras portatiles', atributos: ['ram', 'procesador', 'almacenamiento', 'pantalla'] },
    { nombre: 'Smartphones', descripcion: 'Telefonos inteligentes', atributos: ['pantalla', 'camara', 'bateria', 'almacenamiento'] },
    { nombre: 'Audifonos', descripcion: 'Audifonos y accesorios de audio', atributos: ['tipo', 'conectividad', 'bateria'] },
    { nombre: 'Accesorios', descripcion: 'Accesorios de tecnologia', atributos: ['tipo', 'compatibilidad'] }
]);

// Crear indice en productos por categoria para busquedas eficientes
db.productos.createIndex({ categoria: 1 });
db.productos.createIndex({ nombre: 'text' });
