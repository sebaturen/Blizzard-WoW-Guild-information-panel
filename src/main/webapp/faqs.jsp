<%@include file="includes/globalObject.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title>${guild.name} Simeo</title>
        <%@include file="includes/header.jsp" %>
        <link type="text/css" rel="stylesheet" href="assets/css/index.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/fancyapps/fancybox@3.5.6/dist/jquery.fancybox.min.css" />
        <script src="https://cdn.jsdelivr.net/gh/fancyapps/fancybox@3.5.6/dist/jquery.fancybox.min.js"></script>
        <style>
            @media screen and (min-width:768px) {
                .cont_inf {
                    padding-left: 2em;
                }    
                .img_info {
                    width: 50%;
                }
            }
            @media screen and (max-width:768px) {
                .img_info {
                    width: 80%;
                }
            }
            .img_info {
                height: 15em;
                margin: auto;
                background-size: cover;
                background-position: center center;
                margin-top: 1em;
                margin-bottom: 1em;
                -webkit-box-shadow: 10px 10px 40px -5px rgba(0,0,0,0.75);
                -moz-box-shadow: 10px 10px 40px -5px rgba(0,0,0,0.75);
                box-shadow: 10px 10px 40px -5px rgba(0,0,0,0.75);
            }
            .dung-title {
                padding: 70px;
            }
        </style>
    </head>
    <body>
    <%@include file="includes/menu.jsp" %>
        <div class="container fill">
            <div class="dungeon-challenge-img" style="background-image: url('assets/img/raids/battle-of-dazaralor.jpg');">
                <h1 class="dung-title">Como simular DPS y Comparar ítems.</h1>
            </div>
            <br>
                <i><p>Antes de empezar con el tutorial quiero dejar en claro que esto es en base a mi experiencia
                    personal y, por lo tanto, cabe la posibilidad de que dentro de esta guie existan algunos detalles
                    que no est�n del todo bien o que se puedan mejorar. Por esta raz�n cualquier cosa que se le quiera
                    agregar, mejorar o modificar es bienvenida. No soy un experto en el tema, pero este es el m�todo
                    que yo he utilizado hasta la fecha y que me ha dado mejores resultados.</p>
                <div style="text-align: right;">Con amor,<br>Icywind<br>21 de febrero / 2019</div></i>
            <br>
            <h2>1- Que hace una simulación y porque es útil</h2>
                <div class='cont_inf'>
                <p>Bajo mi entendimiento, una simulación nos muestra el DPS promedio que debería estar haciendo un
                    personaje según las condiciones del mismo personaje (<i>equipo/stats/pasivas, etc.</i>), una
                    rotación perfecta y una serie de parámetros propios al ambiente de la pelea (<i>cantidad de
                        enemigos/movimiento/duración de la pelea, etc.</i>), más adelante veremos como jugar con
                    estos parámetros para ver como ajustarlos a distintas situaciones, pero por ahora hay que quedarse
                    con la idea de que con una simulación conseguiremos un valor DPS "ideal" al que deberíamos apuntar.
                    Aquí entra uno de los puntos útiles de simularse, ya que nos entrega una referencia de cuanto
                    deberíamos estar pegando idealmente si realizáramos una rotación perfecta, usando nuestros coldowns
                    (CDs) efectivamente y sacándole el máximo provecho a nuestra clase y (si no me equivoco) tomando en
                    cuenta el uso de heroísmo, prepotas, flask, comida y otros buff. Es normal estar bajo este valor por
                    un poco (<i>actualmente 1 o 2k de dps inferior</i>) pero si notan que su daño esta muy por debajo
                    del valor entregado por la simulación, entonces es importante que revisar nuestras rotaciones y ver
                    que aspectos de nuestra clase no están siendo bien aprovechados por nosotros.</p>
                <p>Otro punto útil de la simulación radica en que nos permite comparar en base a nuestro DPS simulado
                    distintas piezas de gear, tomando en cuenta pasivas, traits de azerita, encantamientos, gemas y
                    todos los modificadores que un ítem le aporta a nuestro personaje, permitiéndonos decidir que ítem
                    es mejor para nuestro DPS en cada situación más allá del ILVL del mismo.</p>
                <p>Finalmente, otra información útil que podemos conseguir al simularnos son los "Stats Weights" o
                    cuanto aumentaría nuestro DPS según nuestras condiciones actuales de gear al aumentar en 1 punto
                    cualquiera de los stats de nuestro personaje. En otras palabras, cual stat es más importante para
                    aumentar nuestro DPS.</p>
                </div>
            <br>
            <h2>2- Paso a paso:</h2>
                <div class='cont_inf'>
                <h3><i>Pasos previos:</i></h3>
                <p>Antes de empezar a simularnos es importante tener descargado e instalado un addon llamado
                    Simulationcraft (<i>flechita roja en la imagen</i>), que puede ser obtenido a través de la
                    aplicación de twitch para instalar Addons, (<i>esta es según yo la forma más fácil de administrar y
                        conseguir Addons para el juego</i>).</p>
                <a href="assets/img/faqs/como_simular_dps_y_comparar_items/1.png" data-fancybox="gallery"><div class='img_info' style='background-image: url("assets/img/faqs/como_simular_dps_y_comparar_items/1.png")'></div></a>
                <p>Este Addon nos permitirá, de forma sencilla, sacar toda la información de nuestro personaje en
                    formato texto, para así, mediante las ancestrales técnicas del copy/paste, poder importar la
                    información a la página web <a href="http://www.raidbots.com" target="_blanck">www.raidbots.com</a>,
                    la que usaremos para procesar los datos de nuestro personaje.</p>
                <br>
                <p>A continuación, veremos los pasos que debemos seguir despu�s de instalar Simulatorcraft para extraer
                    e importar la información de nuestro personaje:</p>
                <h3>Paso 1</h3>
                <p>Una vez tengamos el addon instalado, simplemente nos conectarnos con nuestro personaje a evaluar y
                    escribiremos en la sección de texto el comando "/simc", como se ve en la siguiente imagen de ejemplo.</p>
                <a href="assets/img/faqs/como_simular_dps_y_comparar_items/2.png" data-fancybox="gallery"><div class='img_info' style='background-image: url("assets/img/faqs/como_simular_dps_y_comparar_items/2.png")'></div></a>
                <h3>Paso 2</h3>
                <p>A continuación, se nos abrirá una ventana gigante de texto con toda la información de nuestro
                    personaje escrito en lenguas arcanas, este texto debemos seleccionarlo y copiarlo presionando las
                    teclas "Ctrl + c". (Ver imagen)</p>
                <a href="assets/img/faqs/como_simular_dps_y_comparar_items/3.png" data-fancybox="gallery"><div class='img_info' style='background-image: url("assets/img/faqs/como_simular_dps_y_comparar_items/3.png")'></div></a>
                <h3>Paso 3</h3>
                <p>Una vez copiada la información en forma de texto abriremos la pagina web
                    <a href="http://www.raidbots.com" target="_blanck">www.raidbots.com</a> donde seleccionaremos el
                    tipo de simulaci�n que queremos. Para este ejemplo optaremos por quick simulation, más adelante
                    hablaremos de las otras opciones de simulación que existen (dentro de lo que conozco)</p>
                <a href="assets/img/faqs/como_simular_dps_y_comparar_items/4.png" data-fancybox="gallery"><div class='img_info' style='background-image: url("assets/img/faqs/como_simular_dps_y_comparar_items/4.png")'></div></a>
                <h3>Paso 4</h3>
                <p>Ya seleccionado el tipo de análisis que queremos obtener, independiente de la opción que escojamos,
                    copiaremos el texto que adquirimos en el paso dos, en la ventana de texto que se encuentra al
                    principio de la página.</p>
                <a href="assets/img/faqs/como_simular_dps_y_comparar_items/5.png" data-fancybox="gallery"><div class='img_info' style='background-image: url("assets/img/faqs/como_simular_dps_y_comparar_items/5.png")'></div></a>
                <h3>Paso 5</h3>
                <p>Este paso es opcional pero, con el texto ya copiado, tenemos la opción de cambiar los par�metros de
                    pelea con los que la simulación va a trabajar, esto se logra haciendo clic "Simulation Options"
                    (en donde está la <mark style='background: red;'>flechita roja</mark> en la imagen de abajo).</p>
                <a href="assets/img/faqs/como_simular_dps_y_comparar_items/6.png" data-fancybox="gallery"><div class='img_info' style='background-image: url("assets/img/faqs/como_simular_dps_y_comparar_items/6.png")'></div></a>
                <h3>Paso 6</h3>
                <p><mark style='background: green;'>Flechita Verde:</mark> Aquí puedes modificar el número de veces que quieras que se
                    repita el script para calcular el dps, supongo que afecta al % de error que uno pueda obtener en la
                    simulación. Yo recomiendo dejarlo en Smart Sim, ya que, con esta opción el programa calcula solo la
                    cantidad de veces que debe repetirse para un resultado óptimo.</p>
                <p><mark style='background: purple;'>Flechita Morada:</mark> Aquí uno puede modificar el tipo de pelea, la opción
                    Patchwerk representa una pelea sin mecánicas ni movimiento, básicamente un dummy. Hay otras opciones
                    como peleas con cleave, con movimiento ligero o movimiento fuerte. Al final queda a opción de uno
                    que tipo de pelea se quiere simular.</p>
                <p><mark style='background: blue;'>Flechita Azul:</mark> Aquí se modifica la cantidad de enemigos dentro de la pelea,
                    útil para simular situaciones de AoE.</p>
                <p><mark style='background: brown;'>Flechita Café:</mark> Aquí se modifica la duración de la pelea, generalmente yo uso
                    los 5 minutos que ya vienen seleccionados.</p>
                <p><mark style='background: pink;'>Flechita Rosada:</mark> Esta opcion sirve para modificar la obtencion de datos
                    entre Simulaciones semanales y Recientes, sin embargo no se recomienda utilizar esta opcion a menos
                    que la spec/clase que estes utilizando este muy poco representada.</p>
                <p><mark style='background: white;'>Flechita Blanca:</mark> Aquí puedes modificar variables relacionadas con los trait
                    de azerita propios de las raid. Cuanto tiempo de la pelea crees que puedes tener levantado el
                    "Treacherous Covenant" y la cantidad de stacks de "Reorigination Array" de Uldir.</p>
                <h3>Paso 7</h3>
                <p>Con todo seleccionado como nosotros queramos, nada más queda hacer click en el botón verde: "Run Simulation".</p>
                <a href="assets/img/faqs/como_simular_dps_y_comparar_items/7.png" data-fancybox="gallery"><div class='img_info' style='background-image: url("assets/img/faqs/como_simular_dps_y_comparar_items/7.png")'></div></a>
                <p>Posterior a eso nos pondrán en una cola de espera donde debemos esperar a nuestro turno para ser simulados.</p>
                <a href="assets/img/faqs/como_simular_dps_y_comparar_items/8.png" data-fancybox="gallery"><div class='img_info' style='background-image: url("assets/img/faqs/como_simular_dps_y_comparar_items/8.png")'></div></a>
                <p>Una vez la cola termine recibiremos la información que solicitamos según el tipo de simulación que
                    realizamos. En el caso de este ejemplo "Quick Simulation" nos indicaran el número de nuestro DPS
                    promedio ideal (Flechita roja) según las opciones que nosotros elegimos y el equipo que tengamos
                    puesto al momento de escribir /simc.</p>
                <a href="assets/img/faqs/como_simular_dps_y_comparar_items/9.png" data-fancybox="gallery"><div class='img_info' style='background-image: url("assets/img/faqs/como_simular_dps_y_comparar_items/9.png")'></div></a>
                <p>Ahora que aprendimos a hacer una simulación simple, explicaré dos herramientas más que uso en la
                    pagina de raidbots para elegir mi equipo y conocer mis stats más importantes.</p>
                </div>
            <br>
            <h2>3- Comparar Gear:</h2>
                <div class='cont_inf'>
                <p>Otra función muy útil que tiene la página
                    <a href="http://www.raidbots.com" target="_blanck">www.raidbots.com</a> es poder comparar el equipo
                    que uno tiene con el equipo que esta presente en la bolsa, para realizar esto solo tenemos que
                    seleccionar la opción "Top Gear" al ingresar a la página web</p>
                <a href="assets/img/faqs/como_simular_dps_y_comparar_items/10.png" data-fancybox="gallery"><div class='img_info' style='background-image: url("assets/img/faqs/como_simular_dps_y_comparar_items/10.png")'></div></a>
                <p>Una vez seleccionada esta opción y copiado el texto obtenido en el paso dos, se nos mostrara en
                    pantalla tanto el equipo que tenemos equipado, como el equipo presente en nuestras bolsas. Los ítems
                    que aparezcan con borde amarillito ser�n los seleccionados a comparar en la simulación, también
                    podemos elegir distintos traits de azerita de nuestro gear de azerita para saber cuál realmente es
                    mejor en cada situación.</p>
                <p>Dentro de esta misma opción, se nos permite comparar joyas y encantamientos para nuestro equipo,
                    junto con los distintos talentos. Cabe destacar que hay un limite de
                    objetos/talentos/enchants/traits/joyas que se pueden comparar de una sola vez as� que no hay que
                    volverse loco comparando.</p>
                <p>En el ejemplo a continuación yo estaré comparando los traits de mi casco, mis trinkets y mis armas.</p>
                <a href="assets/img/faqs/como_simular_dps_y_comparar_items/11.png" data-fancybox="gallery"><div class='img_info' style='background-image: url("assets/img/faqs/como_simular_dps_y_comparar_items/11.png")'></div></a>
                <p>(Si bajan más podrán ver que hay más opciones con las que se puede jugar al momento de hacer la simulaci�n)</p>
                <p>Al hacer click en "Find Top Gear" pasaremos a una cola y posteriormente recibiremos una foto con la
                    información del mejor equipo que podemos tener según el DPS, junto con el valor de DPS promedio de
                    dicho equipo.</p>
                <a href="assets/img/faqs/como_simular_dps_y_comparar_items/12.png" data-fancybox="gallery"><div class='img_info' style='background-image: url("assets/img/faqs/como_simular_dps_y_comparar_items/12.png")'></div></a>
                </div>
            <h2>4- Stats Weight</h2>
                <div class='cont_inf'>
                <p>La opción "Stats Wieght" sirve para saber en cuanto afecta cada stat a nuestro DPS según nuestras condiciones actuales.</p>
                <a href="assets/img/faqs/como_simular_dps_y_comparar_items/13.png" data-fancybox="gallery"><div class='img_info' style='background-image: url("assets/img/faqs/como_simular_dps_y_comparar_items/13.png")'></div></a>
                <p>Al seleccionarla, luego de repetir los pasos 3, 4 y 5, obtendremos la siguiente información</p>
                <a href="assets/img/faqs/como_simular_dps_y_comparar_items/14.png" data-fancybox="gallery"><div class='img_info' style='background-image: url("assets/img/faqs/como_simular_dps_y_comparar_items/14.png")'></div></a>
                <p>Esto nos indica cuanto aumentaría nuestro DPS al aumentar en un punto cada Stat, es decir, en mi caso,
                    al aumentar un punto de Haste, mi DPS aumentaráa en 2,59 puntos.</p>
                <p>Esto es compatible con el addon "Pawn", descargable por la aplicación de Twitch, al copiar el texto
                    que sale abajito (donde dice "Pawn String") e importarlo a la aplicación mediante el menú del addon
                    en el juego, podremos ver en nuestras bolsas si un ítem es una mejora o no y cuanto % aumentaría
                    nuestro daño al equiparlo. Es importante de revisar los stats CADA VEZ que se realiza un cambio de
                    Gear, ya que, el peso de nuestros stats es din�mico y puede ir cambiando seg�n como vayan variando
                    nuestros números.</p>
                </div>
            <br>
            <br>
            <div>
                <p>
                    <i>Bueno, eso es todo el conocimiento que tengo respecto a simulaciones. Espero que este tutorial
                        les sea útil para aprender cosas nuevas. Yo personalmente vi un aumento en mi DPS una vez que
                        empecé a hacer simulaciones de mi personaje y a elegir en base a estas el equipo que debía
                        utilizar. Cualquier duda no duden en preguntar.</i>
                    <br>
                </p>
                <div style="text-align: center;">
                    <i>Saludos</i>
                    <br>
                    <i>Icywind :D</i>
                </div>
            </div>
        </div>
        <%@include file="includes/footer.jsp" %>
    </body>
</html>