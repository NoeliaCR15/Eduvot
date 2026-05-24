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
|   |
|   |-- Encuesta.java
|       Modelo de datos para encuestas y procesos de votacion.
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
|   |
|   |-- EncuestaDAO.java
|       Acceso a base de datos para encuestas:
|       - insertar encuesta
|       - actualizar encuesta
|       - eliminar encuesta
|       - buscar/listar encuestas
|       - guardar opciones de voto
|       - guardar subcategorias destinatarias
|       - registrar votos y consultar participacion/resultados
|
|-- controladores
|   |-- LoginController.java
|   |   Controla el inicio de sesion.
|   |
|   |-- MenuController.java
|   |   Controla el panel del administrador.
|   |   Abre las vistas de usuarios, colectivos, votaciones y resultados dentro del panel.
|   |
|   |-- GestionUsuariosController.java
|   |   Controla el CRUD de usuarios.
|   |
|   |-- GestionSubcategoriasController.java
|   |   Controla el CRUD de subcategorias.
|   |
|   |-- GestionEncuestasController.java
|   |   Controla la creacion y administracion de encuestas.
|   |
|   |-- ResultadosController.java
|   |   Controla la consulta de resultados y participacion para administracion.
|   |
|   |-- PanelUsuarioController.java
|       Controla el panel del usuario votante.
|       Muestra votaciones disponibles, registro de voto e historial de participacion.
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
|-- GestionEncuestas.fxml
|   Vista de gestion de encuestas y votaciones.
|
|-- Resultados.fxml
|   Vista de resultados, participacion y grafica de recuento.
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

Para actualizar una base ya creada sin borrar datos, se pueden ejecutar las migraciones
puntuales de la carpeta `database`, por ejemplo:

```text
database/migracion_archivo_encuestas.sql
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

encuestas
Guarda los procesos de votacion, fechas, estado y tipo de voto.

encuesta_subcategoria
Relaciona cada encuesta con las subcategorias destinatarias.

opciones_voto
Guarda las opciones disponibles para cada encuesta.

votos
Guarda la participacion de cada usuario en una encuesta.

detalle_voto
Guarda la opcion u opciones elegidas en cada voto.

encuestas_archivadas
Registra que encuestas ya realizadas pasan a historico, quien las archivo y el motivo.
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

5. Gestion de encuestas y votaciones
   Permite crear, editar, eliminar, buscar y listar encuestas.
   Permite definir fechas, tipo de voto, subcategorias destinatarias y opciones.
   No permite editar o eliminar encuestas que ya tienen votos registrados.

6. Resultados y participacion
   Permite seleccionar una encuesta, ver votos, destinatarios, porcentaje de participacion,
   recuento por opcion y grafica de barras.

7. Panel de usuario
   Muestra votaciones disponibles, permite votar y permite consultar la participacion propia.

8. Archivo de encuestas
   La base de datos ya contempla una tabla para archivar encuestas finalizadas.
   Desde resultados se pueden archivar encuestas finalizadas y consultar el listado historico.
```

## Mejoras futuras

Ideas de mejora:

```text
- Mejorar confirmacion del voto antes de registrarlo.
- Decidir si se mostraran resultados publicados cuando una encuesta este cerrada.
- Pulir mensajes y estados vacios.
```

## Orden recomendado de desarrollo

```text
1. Usuarios                         Ya implementado
2. Subcategorias                    Ya implementado
3. Encuestas y votaciones           Ya implementado en administracion
4. Panel de usuario/votante         Ya implementado
5. Resultados y participacion       Ya implementado en administracion
```
