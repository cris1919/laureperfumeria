# Laure Perfumería

Aplicación web de comercio electrónico para una perfumería. Incluye catálogo, cuentas de clientes, carrito y compra, además de herramientas administrativas para productos, pedidos, usuarios y reportes.

## Tecnologías

- Frontend: Angular 22, TypeScript y SSR.
- Backend: Java 21, Spring Boot y Spring Security con JWT.
- Persistencia: MySQL.

## Requisitos

- JDK 21 o superior.
- Node.js y npm.
- MySQL 8.

## Preparar la base de datos

Crea la base de datos antes de iniciar el backend:

```sql
CREATE DATABASE laure_perfumeria;
```

El backend lee la conexión desde `DB_URL`, `DB_USERNAME` y `DB_PASSWORD`. Si no se definen, usa la configuración local de desarrollo de `application.properties`. Para un entorno distinto al local, define también `JWT_SECRET` con una clave aleatoria de al menos 32 bytes y evita usar credenciales predeterminadas.

## Iniciar para desarrollo

En una terminal, inicia el backend:

```bash
cd backend
./mvnw spring-boot:run
```

En Windows usa `mvnw.cmd spring-boot:run`.

En otra terminal, inicia el frontend:

```bash
cd laure-perfumeria-frontend
npm ci
npm start
```

Abre <http://localhost:4200>. El servidor de desarrollo reenvía las solicitudes `/api` al backend en `http://localhost:8080`.

## Recorrido sugerido para la presentación

1. Explorar la portada y las familias olfativas.
2. Abrir el catálogo, buscar un producto y consultar su detalle.
3. Agregar productos al carrito y completar el flujo de compra con una cuenta de cliente de prueba.
4. Iniciar sesión con una cuenta administradora de prueba y mostrar la gestión de catálogo y pedidos, y los reportes.

Prepara las cuentas y los datos de demostración en la base de datos antes de presentar; no hay credenciales de demo predeterminadas en el repositorio.

## Pruebas y compilación

El backend usa una base H2 aislada al ejecutar las pruebas:

```bash
cd backend
./mvnw test
```

Compila el frontend para producción con:

```bash
cd laure-perfumeria-frontend
npm ci
npm run build
```

## Configuración

| Variable | Propósito | Valor local predeterminado |
| --- | --- | --- |
| `DB_URL` | URL JDBC de MySQL | `jdbc:mysql://localhost:3306/laure_perfumeria` |
| `DB_USERNAME` | Usuario de la base de datos | `root` |
| `DB_PASSWORD` | Contraseña de la base de datos | `1234` |
| `JWT_SECRET` | Clave para firmar tokens JWT | Valor solo para desarrollo |
| `JWT_EXPIRATION_MS` | Duración del token en milisegundos | `86400000` |

Los valores predeterminados son únicamente para facilitar el desarrollo local. Configura credenciales propias y una clave JWT segura fuera de entornos locales.
