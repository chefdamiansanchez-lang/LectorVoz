# Lector de Voz

App Android que lee en voz alta (en español) tus libros EPUB y PDF guardados en el
móvil, con reproducción en segundo plano, portadas y guardado del punto de lectura.

## Qué se corrigió respecto a la versión anterior
1. **No compilaba en GitHub Actions** → ahora el workflow usa la acción oficial
   `gradle/actions/setup-gradle` con una versión de Gradle fija (8.7) y versiones
   de AGP/Kotlin compatibles entre sí, sin depender del binario `gradlew` (que
   suele faltar o corromperse al armar el zip a mano).
2. **No detectaba los libros del móvil** → Android no permite leer archivos
   sueltos por ruta directa desde Android 11 en adelante, y MediaStore no indexa
   EPUB/PDF. Ahora la app te pide elegir la carpeta de libros una sola vez (con
   el selector nativo de Android) y a partir de ahí los detecta siempre.

## Cómo subir el ZIP a GitHub sin descomprimir
1. Andá a GitHub → **New repository** (creá uno vacío, sin README).
2. Entrá al repo → **Add file → Upload files**.
3. Subí el archivo `lector-voz.zip` tal cual (no hace falta descomprimirlo antes,
   pero GitHub sí necesita los archivos sueltos para poder compilarlos —
   ver el paso siguiente).
4. Como GitHub no descomprime zips automáticamente al subirlos, la forma más
   simple desde el celular es usar la app de GitHub o el navegador con esta
   secuencia: **Add file → Upload files**, arrastrá el zip, y GitHub te va a
   dejar el zip como un solo archivo. Para que Actions lo compile necesitamos
   el contenido descomprimido en el repo. Si tu cliente de GitHub en el móvil
   permite "descomprimir al subir" usalo; si no, contame y te preparo en su
   lugar un repo ya inicializado por `git` con los archivos sueltos listos
   para subir carpeta por carpeta desde el navegador.
5. Una vez que el contenido (carpetas `app/`, `.github/`, etc.) esté en la raíz
   del repo, andá a la pestaña **Actions** → el workflow "Compilar APK" corre
   solo. Esperá unos minutos.
6. Entrá al workflow terminado (ícono verde ✔) → abajo, en **Artifacts**,
   descargá `LectorVoz-debug-apk`. Adentro está el `app-debug.apk` instalable.
7. Abrí el APK descargado en el móvil (activá "Instalar apps desconocidas" si
   te lo pide) para instalarla, con ícono y todo.

## Primer uso en el teléfono
1. Abrí la app → botón **"Elegir carpeta"** → seleccioná la carpeta donde
   tenés tus libros (por ejemplo `Descargas` o una carpeta `Libros`).
2. La app escanea esa carpeta (y subcarpetas) buscando `.epub` y `.pdf`.
3. Tocá un libro para abrir el lector, dale Play. La lectura sigue funcionando
   aunque bloquees el teléfono o cambies de app; los controles aparecen en la
   notificación.
