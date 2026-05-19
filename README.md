# EduVot

Proyecto final DAM.

EduVot es una aplicacion de escritorio desarrollada con JavaFX y MySQL para gestionar procesos de votacion en un entorno educativo. La aplicacion diferencia entre usuarios administradores y usuarios votantes.

## Estructura actual del proyecto

```text
src/main/java/com/proyecto
|
|-- App.java
|   Clase principal de JavaFX. Arranca la aplicacion y carga la pantalla de login.
|
|-- ConexionBD.java
|   Gestiona la conexion con la base de datos MySQL.
|
|-- module-info.java
|   Configuracion del modulo JavaFX.
|
|-- modelos
|   |-- Usuario.java
|       Modelo de datos para usuarios.
|   |
|   |-- Subcategoria.java
|       Modelo de datos para subcategorias.
|
|-- dao
|   |-- UsuarioDAO.java
|       Acceso a base de datos para usuarios:
|       - insertar usuario
|       - actualizar usuario
|       - eliminar usuario
|       - buscar usuarios
|       - validar login
|       - obtener y guardar subcategorias asociadas
|       - obtener y guardar alumnos vinculados a usuarios de Familias
|   |
|   |-- SubcategoriaDAO.java
|       Acceso a base de datos para subcategorias:
|       - insertar subcategoria
|       - actualizar subcategoria
|       - eliminar subcategoria
|       - buscar/listar subcategorias
|       - contar usuarios asociados
|
|-- controladores
|   |-- LoginController.java
|   |   Controla el inicio de sesion.
|   |
|   |-- MenuController.java
|   |   Controla el panel del administrador.
|   |   Actualmente abre gestion de usuarios y deja pendientes:
|   |   - subcategorias
|   |   - encuestas
|   |   - resultados
|   |
|   |-- GestionUsuariosController.java
|   |   Controla el CRUD de usuarios.
|   |
|   |-- GestionSubcategoriasController.java
|   |   Controla el CRUD de subcategorias.
|   |
|   |-- PanelUsuarioController.java
|       Controla el panel del usuario votante.
|       Por ahora tiene vistas pendientes para votaciones y participacion.
```

## Interfaces actuales

```text
src/main/resources/com/Interfaz
|
|-- login.fxml
|   Pantalla de inicio de sesion.
|
|-- MenuPrincipal.fxml
|   Panel principal del administrador.
|
|-- GestionUsuarios.fxml
|   Vista de gestion de usuarios.
|
|-- GestionSubcategorias.fxml
|   Vista de gestion de subcategorias.
|
|-- PanelUsuario.fxml
|   Panel principal del usuario votante.
|
|-- styles.css
|   Estilos visuales generales de la aplicacion.
|
|-- Logo.png
|   Imagen del proyecto.
```

## Base de datos actual

El script principal esta en:

```text
database/eduvot_mysql.sql
```

Tablas actuales:

```text
usuarios
Guarda usuarios, administradores, contrasenas, curso/grupo y estado.

subcategorias
Guarda colectivos como Alumnado, Profesorado, Familia o tutor, etc.

usuario_subcategoria
Relaciona usuarios con una o varias subcategorias.

usuario_familia
Relaciona usuarios de la subcategoria Familia o tutor con uno o varios alumnos.
```

## Modulos implementados

```text
1. Login
   Permite iniciar sesion con DNI y contrasena.

2. Panel de administrador
   Muestra accesos a usuarios, subcategorias, encuestas y resultados.

3. Gestion de usuarios
   Permite crear, editar, eliminar y buscar usuarios.
   Permite asignar varias subcategorias a un mismo usuario.
   Si el usuario pertenece a Familia o tutor, permite seleccionar de que alumno/s es familiar.

4. Gestion de subcategorias
   Permite crear, editar, eliminar, buscar y listar subcategorias.
   No permite eliminar una subcategoria que este asociada a usuarios.

5. Panel de usuario
   Muestra la zona del votante, todavia pendiente de conectar con votaciones reales.
```

## Modulos pendientes de implementar

### 1. Encuestas y votaciones

Clases previstas:

```text
src/main/java/com/proyecto/modelos/Encuesta.java
src/main/java/com/proyecto/modelos/Opcion.java
src/main/java/com/proyecto/modelos/Voto.java
src/main/java/com/proyecto/dao/EncuestaDAO.java
src/main/java/com/proyecto/dao/VotoDAO.java
src/main/java/com/proyecto/controladores/GestionEncuestasController.java
src/main/resources/com/Interfaz/GestionEncuestas.fxml
```

Funciones previstas:

```text
- Crear encuestas o procesos de votacion.
- Definir fechas de inicio y fin.
- Asociar encuestas a subcategorias.
- Gestionar opciones de voto.
- Activar o cerrar votaciones.
```

### 2. Resultados y participacion

Clases previstas:

```text
src/main/java/com/proyecto/modelos/ResultadoEncuesta.java
src/main/java/com/proyecto/dao/ResultadoDAO.java
src/main/java/com/proyecto/controladores/ResultadosController.java
src/main/resources/com/Interfaz/Resultados.fxml
```

Funciones previstas:

```text
- Consultar resultados por encuesta.
- Ver recuento de votos.
- Consultar participacion general.
- Mostrar porcentajes o resumen de resultados.
```

### 3. Panel de usuario votante

Funciones previstas:

```text
- Mostrar votaciones disponibles para el usuario.
- Permitir votar una sola vez por encuesta.
- Mostrar votaciones ya realizadas.
```

## Orden recomendado de desarrollo

```text
1. Usuarios                         Ya implementado
2. Subcategorias                    Ya implementado
3. Encuestas y votaciones           Siguiente paso
4. Resultados y participacion       Despues
5. Panel de usuario/votante         Se completara cuando existan encuestas
```
