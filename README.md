# EduVot

Aplicacion JavaFX para la gestion de usuarios y votaciones de EduVot.

## Requisitos

- Java 17
- Maven
- MySQL

## Base de datos

El script inicial esta en:

```text
database/eduvot_mysql.sql
```

La conexion se configura en:

```text
src/main/java/com/proyecto/ConexionBD.java
```

Por defecto usa:

```text
jdbc:mysql://localhost:3306/eduvot
usuario: root
password: vacio
```

## Ejecutar

Desde la raiz del proyecto:

```bash
mvn javafx:run
```

## Compilar

```bash
mvn compile
```
