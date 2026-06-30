# Cafedronel Frontend Angular

Frontend Angular para el Sistema Web de Inventario Cafedronel.

## Objetivo APF3

Construir una interfaz Angular organizada por modulos, con rutas internas, layout administrativo, formularios, validacion del lado del cliente e integracion REST con el backend Spring Boot.

## Comandos

```powershell
npm install
npm start
npm run build
npm test
```

La aplicacion se ejecuta en:

```text
http://localhost:4200
```

Backend esperado:

```text
http://localhost:8081/api
```

## Estructura principal

```text
src/app/core          Configuracion, navegacion y servicios transversales
src/app/layouts       Layout principal y layout de autenticacion
src/app/shared        Componentes reutilizables
src/app/features      Paginas por modulo funcional
src/app/models        Tipos alineados al backend
```

## Rutas base

```text
/login
/dashboard
/productos
/inventario
/proveedores
/pedidos
/ventas
/usuarios
```

## Fases siguientes

1. Autenticacion real con `/api/auth/sesiones`.
2. Interceptor JWT.
3. Guards para rutas privadas y rol admin.
4. Servicios REST por modulo.
5. Formularios CRUD con validaciones y manejo de errores.
