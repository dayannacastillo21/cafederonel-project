# Fase 2 - Estructura Angular

## Nombre del frontend

`cafedronel-frontend-angular`

## Version base

- Angular CLI: 21.2.17
- Estilos: SCSS
- Routing: habilitado
- Arquitectura: componentes standalone

## Carpetas creadas

```text
src/app/core
src/app/shared
src/app/layouts
src/app/features/auth
src/app/features/dashboard
src/app/features/productos
src/app/features/inventario
src/app/features/proveedores
src/app/features/pedidos
src/app/features/ventas
src/app/features/usuarios
src/app/models
```

## Componentes principales

| Area | Componente |
| --- | --- |
| Layout | `MainLayout` |
| Auth | `AuthLayout` |
| Navegacion | `Sidebar`, `Topbar` |
| Reutilizable | `PageHeader` |
| Paginas | `LoginPage`, `DashboardPage`, `ProductosPage`, `InventarioPage`, `ProveedoresPage`, `PedidosPage`, `VentasPage`, `UsuariosPage` |

## Rutas

| Ruta | Uso |
| --- | --- |
| `/login` | Acceso al sistema |
| `/dashboard` | Panel principal |
| `/productos` | Catalogo de productos |
| `/inventario` | Control de stock |
| `/proveedores` | Gestion de proveedores |
| `/pedidos` | Seguimiento de pedidos |
| `/ventas` | Registro de ventas |
| `/usuarios` | Administracion de usuarios |

## Preparado para fase 3

El frontend ya contiene:

- `API_BASE_URL` apuntando a `http://localhost:8081/api`.
- Modelos base para auth, productos, inventario y usuarios.
- Formulario reactivo inicial de login con validaciones.
- Layout administrativo responsive.
- Navegacion interna funcional.
