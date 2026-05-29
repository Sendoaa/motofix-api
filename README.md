# 🏍️ MotoFix API

**MotoFix** es una API REST desarrollada con **Spring Boot** para la gestión integral de operaciones en un taller de motos. Este proyecto está diseñado como pieza de portfolio, aplicando las mejores prácticas de arquitectura de software, seguridad y persistencia de datos.

---

## 🚀 Tecnologías Utilizadas

* **Java 17** & **Spring Boot 3**
* **Spring Security** (Autenticación Básica basada en Roles)
* **Spring Data JPA** & **Hibernate**
* **PostgreSQL** (Desplegado en contenedor **Docker**)
* **Lombok** & **Jakarta Validation**

---

## 🛡️ Seguridad y Roles

La API está blindada mediante un muro de seguridad que restringe los accesos según el puesto de trabajo en el taller:

* **Público (Sin autenticación):** Consulta básica de catálogo de motos (`GET`).
* **Mecánico (`USER`):** Puede ver clientes y motos, y actualizar el estado de las reparaciones (`PUT`).
* **Jefe de Taller (`ADMIN`):** Control total de la aplicación (Crear, editar y eliminar tanto motos como clientes).

---

## 📊 Arquitectura de Datos (Relaciones)

El sistema modela las relaciones del taller del mundo real mediante JPA:
* **Relación Un-Cliente-a-Muchas-Motos (`@OneToMany` / `@ManyToOne`):** Un cliente puede tener varios vehículos registrados, pero una moto pertenece obligatoriamente a un único dueño (`nullable = false`). El borrado de clientes está protegido para evitar vehículos huérfanos.

---

## 🎛️ Arquitectura de Endpoints (v1)

### 🛵 Gestión de Motos (`/api/v1/motos`)
| Método | Endpoint | Permiso | Descripción |
| **GET** | `/api/v1/motos` | Público | Lista todas las motos del taller |
| **GET** | `/api/v1/motos/{id}` | Público | Busca una moto específica por su ID |
| **POST** | `/api/v1/motos` | `JEFE (ADMIN)` | Registra una moto vinculándola a un ID de cliente |
| **PUT** | `/api/v1/motos/{id}` | `JEFE` / `MECÁNICO` | Actualiza los datos o estado de una moto |
| **DELETE** | `/api/v1/motos/{id}` | `JEFE (ADMIN)` | Elimina una moto del sistema |

### 👥 Gestión de Clientes (`/api/v1/clients`)
| Método | Endpoint | Permiso | Descripción |
| **GET** | `/api/v1/clients` | `JEFE` / `MECÁNICO` | Lista todos los clientes registrados |
| **GET** | `/api/v1/clients/{id}` | `JEFE` / `MECÁNICO` | Busca un cliente por su ID |
| **POST** | `/api/v1/clients` | `JEFE (ADMIN)` | Registra un nuevo cliente (Email único) |
| **DELETE** | `/api/v1/clients/{id}` | `JEFE (ADMIN)` | Elimina un cliente (Bloqueado si tiene motos) |

---

## 🛡️ Gestión Centralizada de Errores

La API cuenta con un parachoques global (`@RestControllerAdvice`) que intercepta las excepciones y las transforma en respuestas JSON estandarizadas y limpias, evitando fugas de información del servidor:

* **400 Bad Request:** Datos de validación incorrectos o matrículas/emails duplicados.
* **404 Not Found:** Búsqueda de motos o clientes con IDs inexistentes.
* **409 Conflict:** Intentos de violación de integridad (como borrar un cliente que aún tiene motos asignadas).