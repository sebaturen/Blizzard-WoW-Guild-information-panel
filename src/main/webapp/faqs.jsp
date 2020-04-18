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
            .dung-title {
                padding: 70px;
                text-align: center;
            }
            .img_info {
                height: 15em;
                margin: auto;
                background-size: cover;
                background-position: center center;
                margin-top: 1em;
                margin-bottom: 1em;

                border: 1px solid #464444;
                border-radius: 4px;
                -moz-border-radius: 4px;
                -webkit-border-radius: 4px;
                background-color: #26262d;
            }
            .img_info:hover {
                -webkit-box-shadow: 0px 7px 26px -8px rgba(0,0,0,0.75);
                -moz-box-shadow: 0px 7px 26px -8px rgba(0,0,0,0.75);
                box-shadow: 0px 7px 26px -8px rgba(0,0,0,0.75);
            }
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
        </style>
    </head>
    <body> <!-- style="background-color: #1f0c27;" -->
        <%@include file="includes/menu.jsp" %>
        <div class="dungeon-challenge-img member_bg_content" style="background-image: url('assets/img/raids/battle-of-dazaralor.jpg');">
            <div class="container">
                <h1 class="dung-title">Como simular DPS y Comparar &iacute;tems.</h1>
            </div>
        </div>
        <div class="container fill">
            <br>
            <i><p>Antes de empezar con el tutorial quiero dejar en claro que esto es en base a mi experiencia
                personal y, por lo tanto, cabe la posibilidad de que dentro de esta guie existan algunos detalles
                que no est&aacute;n del todo bien o que se puedan mejorar. Por esta raz&oacute;n cualquier cosa que se le quiera
                agregar, mejorar o modificar es bienvenida. No soy un experto en el tema, pero este es el m&eacute;todo
                que yo he utilizado hasta la fecha y que me ha dado mejores resultados.</p>
                <div style="text-align: right;">Con amor,<br>Icywind<br>21 de febrero / 2019</div></i>
            <br>
            <h2>1- Que hace una simulaci&oacute;n y porque es &uacute;til</h2>
            <div class='cont_inf'>
                <p>Bajo mi entendimiento, una simulaci&oacute;n nos muestra el DPS promedio que deber&iacute;a estar haciendo un
                    personaje seg&uacute;n las condiciones del mismo personaje (<i>equipo/stats/pasivas, etc.</i>), una
                    rotaci&oacute;n perfecta y una serie de par&aacute;metros propios al ambiente de la pelea (<i>cantidad de
                        enemigos/movimiento/duraci&oacute;n de la pelea, etc.</i>), m&aacute;s adelante veremos como jugar con
                    estos par&aacute;metros para ver como ajustarlos a distintas situaciones, pero por ahora hay que quedarse
                    con la idea de que con una simulaci&oacute;n conseguiremos un valor DPS "ideal" al que deber&iacute;amos apuntar.
                    Aqu&iacute; entra uno de los puntos &uacute;tiles de simularse, ya que nos entrega una referencia de cuanto
                    deber&iacute;amos estar pegando idealmente si realiz&aacute;ramos una rotaci&oacute;n perfecta, usando nuestros coldowns
                    (CDs) efectivamente y sac&aacute;ndole el m&aacute;ximo provecho a nuestra clase y (si no me equivoco) tomando en
                    cuenta el uso de hero&iacute;smo, prepotas, flask, comida y otros buff. Es normal estar bajo este valor por
                    un poco (<i>actualmente 1 o 2k de dps inferior</i>) pero si notan que su daño esta muy por debajo
                    del valor entregado por la simulaci&oacute;n, entonces es importante que revisar nuestras rotaciones y ver
                    que aspectos de nuestra clase no est&aacute;n siendo bien aprovechados por nosotros.</p>
                <p>Otro punto &uacute;til de la simulaci&oacute;n radica en que nos permite comparar en base a nuestro DPS simulado
                    distintas piezas de gear, tomando en cuenta pasivas, traits de azerita, encantamientos, gemas y
                    todos los modificadores que un &iacute;tem le aporta a nuestro personaje, permiti&eacute;ndonos decidir que &iacute;tem
                    es mejor para nuestro DPS en cada situaci&oacute;n m&aacute;s all&aacute; del ILVL del mismo.</p>
                <p>Finalmente, otra informaci&oacute;n &uacute;til que podemos conseguir al simularnos son los "Stats Weights" o
                    cuanto aumentar&iacute;a nuestro DPS seg&uacute;n nuestras condiciones actuales de gear al aumentar en 1 punto
                    cualquiera de los stats de nuestro personaje. En otras palabras, cual stat es m&aacute;s importante para
                    aumentar nuestro DPS.</p>
            </div>
            <br>
            <h2>2- Paso a paso:</h2>
            <div class='cont_inf'>
                <h3><i>Pasos previos:</i></h3>
                <p>Antes de empezar a simularnos es importante tener descargado e instalado un addon llamado
                    Simulationcraft (<i>flechita roja en la imagen</i>), que puede ser obtenido a trav&eacute;s de la
                    aplicaci&oacute;n de twitch para instalar Addons, (<i>esta es seg&uacute;n yo la forma m&aacute;s f&aacute;cil de administrar y
                        conseguir Addons para el juego</i>).</p>
                <a href="assets/img/faqs/como_simular_dps_y_comparar_items/1.png" data-fancybox="gallery"><div class='img_info' style='background-image: url("assets/img/faqs/como_simular_dps_y_comparar_items/1.png")'></div></a>
                <p>Este Addon nos permitir&aacute;, de forma sencilla, sacar toda la informaci&oacute;n de nuestro personaje en
                    formato texto, para as&iacute;, mediante las ancestrales t&eacute;cnicas del copy/paste, poder importar la
                    informaci&oacute;n a la p&aacute;gina web <a href="http://www.raidbots.com" target="_blanck">www.raidbots.com</a>,
                    la que usaremos para procesar los datos de nuestro personaje.</p>
                <br>
                <p>A continuaci&oacute;n, veremos los pasos que debemos seguir despu&eacute;s de instalar Simulatorcraft para extraer
                    e importar la informaci&oacute;n de nuestro personaje:</p>
                <h3>Paso 1</h3>
                <p>Una vez tengamos el addon instalado, simplemente nos conectarnos con nuestro personaje a evaluar y
                    escribiremos en la secci&oacute;n de texto el comando "/simc", como se ve en la siguiente imagen de ejemplo.</p>
                <a href="assets/img/faqs/como_simular_dps_y_comparar_items/2.png" data-fancybox="gallery"><div class='img_info' style='background-image: url("assets/img/faqs/como_simular_dps_y_comparar_items/2.png")'></div></a>
                <h3>Paso 2</h3>
                <p>A continuaci&oacute;n, se nos abrir&aacute; una ventana gigante de texto con toda la informaci&oacute;n de nuestro
                    personaje escrito en lenguas arcanas, este texto debemos seleccionarlo y copiarlo presionando las
                    teclas "Ctrl + c". (Ver imagen)</p>
                <a href="assets/img/faqs/como_simular_dps_y_comparar_items/3.png" data-fancybox="gallery"><div class='img_info' style='background-image: url("assets/img/faqs/como_simular_dps_y_comparar_items/3.png")'></div></a>
                <h3>Paso 3</h3>
                <p>Una vez copiada la informaci&oacute;n en forma de texto abriremos la pagina web
                    <a href="http://www.raidbots.com" target="_blanck">www.raidbots.com</a> donde seleccionaremos el
                    tipo de simulaci�n que queremos. Para este ejemplo optaremos por quick simulation, m&aacute;s adelante
                    hablaremos de las otras opciones de simulaci&oacute;n que existen (dentro de lo que conozco)</p>
                <a href="assets/img/faqs/como_simular_dps_y_comparar_items/4.png" data-fancybox="gallery"><div class='img_info' style='background-image: url("assets/img/faqs/como_simular_dps_y_comparar_items/4.png")'></div></a>
                <h3>Paso 4</h3>
                <p>Ya seleccionado el tipo de an&aacute;lisis que queremos obtener, independiente de la opci&oacute;n que escojamos,
                    copiaremos el texto que adquirimos en el paso dos, en la ventana de texto que se encuentra al
                    principio de la p&aacute;gina.</p>
                <a href="assets/img/faqs/como_simular_dps_y_comparar_items/5.png" data-fancybox="gallery"><div class='img_info' style='background-image: url("assets/img/faqs/como_simular_dps_y_comparar_items/5.png")'></div></a>
                <h3>Paso 5</h3>
                <p>Este paso es opcional pero, con el texto ya copiado, tenemos la opci&oacute;n de cambiar los par�metros de
                    pelea con los que la simulaci&oacute;n va a trabajar, esto se logra haciendo clic "Simulation Options"
                    (en donde est&aacute; la <mark style='background: red;'>flechita roja</mark> en la imagen de abajo).</p>
                <a href="assets/img/faqs/como_simular_dps_y_comparar_items/6.png" data-fancybox="gallery"><div class='img_info' style='background-image: url("assets/img/faqs/como_simular_dps_y_comparar_items/6.png")'></div></a>
                <h3>Paso 6</h3>
                <p><mark style='background: green;'>Flechita Verde:</mark> Aqu&iacute; puedes modificar el n&uacute;mero de veces que quieras que se
                    repita el script para calcular el dps, supongo que afecta al % de error que uno pueda obtener en la
                    simulaci&oacute;n. Yo recomiendo dejarlo en Smart Sim, ya que, con esta opci&oacute;n el programa calcula solo la
                    cantidad de veces que debe repetirse para un resultado &oacute;ptimo.</p>
                <p><mark style='background: purple;'>Flechita Morada:</mark> Aqu&iacute; uno puede modificar el tipo de pelea, la opci&oacute;n
                    Patchwerk representa una pelea sin mec&aacute;nicas ni movimiento, b&aacute;sicamente un dummy. Hay otras opciones
                    como peleas con cleave, con movimiento ligero o movimiento fuerte. Al final queda a opci&oacute;n de uno
                    que tipo de pelea se quiere simular.</p>
                <p><mark style='background: blue;'>Flechita Azul:</mark> Aqu&iacute; se modifica la cantidad de enemigos dentro de la pelea,
                    &uacute;til para simular situaciones de AoE.</p>
                <p><mark style='background: brown;'>Flechita Caf&eacute;:</mark> Aqu&iacute; se modifica la duraci&oacute;n de la pelea, generalmente yo uso
                    los 5 minutos que ya vienen seleccionados.</p>
                <p><mark style='background: pink;'>Flechita Rosada:</mark> Esta opcion sirve para modificar la obtencion de datos
                    entre Simulaciones semanales y Recientes, sin embargo no se recomienda utilizar esta opcion a menos
                    que la spec/clase que estes utilizando este muy poco representada.</p>
                <p><mark style='background: white;'>Flechita Blanca:</mark> Aqu&iacute; puedes modificar variables relacionadas con los trait
                    de azerita propios de las raid. Cuanto tiempo de la pelea crees que puedes tener levantado el
                    "Treacherous Covenant" y la cantidad de stacks de "Reorigination Array" de Uldir.</p>
                <h3>Paso 7</h3>
                <p>Con todo seleccionado como nosotros queramos, nada m&aacute;s queda hacer click en el bot&oacute;n verde: "Run Simulation".</p>
                <a href="assets/img/faqs/como_simular_dps_y_comparar_items/7.png" data-fancybox="gallery"><div class='img_info' style='background-image: url("assets/img/faqs/como_simular_dps_y_comparar_items/7.png")'></div></a>
                <p>Posterior a eso nos pondr&aacute;n en una cola de espera donde debemos esperar a nuestro turno para ser simulados.</p>
                <a href="assets/img/faqs/como_simular_dps_y_comparar_items/8.png" data-fancybox="gallery"><div class='img_info' style='background-image: url("assets/img/faqs/como_simular_dps_y_comparar_items/8.png")'></div></a>
                <p>Una vez la cola termine recibiremos la informaci&oacute;n que solicitamos seg&uacute;n el tipo de simulaci&oacute;n que
                    realizamos. En el caso de este ejemplo "Quick Simulation" nos indicaran el n&uacute;mero de nuestro DPS
                    promedio ideal (Flechita roja) seg&uacute;n las opciones que nosotros elegimos y el equipo que tengamos
                    puesto al momento de escribir /simc.</p>
                <a href="assets/img/faqs/como_simular_dps_y_comparar_items/9.png" data-fancybox="gallery"><div class='img_info' style='background-image: url("assets/img/faqs/como_simular_dps_y_comparar_items/9.png")'></div></a>
                <p>Ahora que aprendimos a hacer una simulaci&oacute;n simple, explicar&eacute; dos herramientas m&aacute;s que uso en la
                    pagina de raidbots para elegir mi equipo y conocer mis stats m&aacute;s importantes.</p>
            </div>
            <br>
            <h2>3- Comparar Gear:</h2>
            <div class='cont_inf'>
                <p>Otra funci&oacute;n muy &uacute;til que tiene la p&aacute;gina
                    <a href="http://www.raidbots.com" target="_blanck">www.raidbots.com</a> es poder comparar el equipo
                    que uno tiene con el equipo que esta presente en la bolsa, para realizar esto solo tenemos que
                    seleccionar la opci&oacute;n "Top Gear" al ingresar a la p&aacute;gina web</p>
                <a href="assets/img/faqs/como_simular_dps_y_comparar_items/10.png" data-fancybox="gallery"><div class='img_info' style='background-image: url("assets/img/faqs/como_simular_dps_y_comparar_items/10.png")'></div></a>
                <p>Una vez seleccionada esta opci&oacute;n y copiado el texto obtenido en el paso dos, se nos mostrara en
                    pantalla tanto el equipo que tenemos equipado, como el equipo presente en nuestras bolsas. Los &iacute;tems
                    que aparezcan con borde amarillito ser&aacute;n los seleccionados a comparar en la simulaci&oacute;n, tambi&eacute;n
                    podemos elegir distintos traits de azerita de nuestro gear de azerita para saber cu&aacute;l realmente es
                    mejor en cada situaci&oacute;n.</p>
                <p>Dentro de esta misma opci&oacute;n, se nos permite comparar joyas y encantamientos para nuestro equipo,
                    junto con los distintos talentos. Cabe destacar que hay un limite de
                    objetos/talentos/enchants/traits/joyas que se pueden comparar de una sola vez as&iacute; que no hay que
                    volverse loco comparando.</p>
                <p>En el ejemplo a continuaci&oacute;n yo estar&eacute; comparando los traits de mi casco, mis trinkets y mis armas.</p>
                <a href="assets/img/faqs/como_simular_dps_y_comparar_items/11.png" data-fancybox="gallery"><div class='img_info' style='background-image: url("assets/img/faqs/como_simular_dps_y_comparar_items/11.png")'></div></a>
                <p>(Si bajan m&aacute;s podr&aacute;n ver que hay m&aacute;s opciones con las que se puede jugar al momento de hacer la simulaci&oacute;n)</p>
                <p>Al hacer click en "Find Top Gear" pasaremos a una cola y posteriormente recibiremos una foto con la
                    informaci&oacute;n del mejor equipo que podemos tener seg&uacute;n el DPS, junto con el valor de DPS promedio de
                    dicho equipo.</p>
                <a href="assets/img/faqs/como_simular_dps_y_comparar_items/12.png" data-fancybox="gallery"><div class='img_info' style='background-image: url("assets/img/faqs/como_simular_dps_y_comparar_items/12.png")'></div></a>
            </div>
            <h2>4- Stats Weight</h2>
            <div class='cont_inf'>
                <p>La opci&oacute;n "Stats Wieght" sirve para saber en cuanto afecta cada stat a nuestro DPS seg&uacute;n nuestras condiciones actuales.</p>
                <a href="assets/img/faqs/como_simular_dps_y_comparar_items/13.png" data-fancybox="gallery"><div class='img_info' style='background-image: url("assets/img/faqs/como_simular_dps_y_comparar_items/13.png")'></div></a>
                <p>Al seleccionarla, luego de repetir los pasos 3, 4 y 5, obtendremos la siguiente informaci&oacute;n</p>
                <a href="assets/img/faqs/como_simular_dps_y_comparar_items/14.png" data-fancybox="gallery"><div class='img_info' style='background-image: url("assets/img/faqs/como_simular_dps_y_comparar_items/14.png")'></div></a>
                <p>Esto nos indica cuanto aumentar&iacute;a nuestro DPS al aumentar en un punto cada Stat, es decir, en mi caso,
                    al aumentar un punto de Haste, mi DPS aumentar&aacute;a en 2,59 puntos.</p>
                <p>Esto es compatible con el addon "Pawn", descargable por la aplicaci&oacute;n de Twitch, al copiar el texto
                    que sale abajito (donde dice "Pawn String") e importarlo a la aplicaci&oacute;n mediante el men&uacute; del addon
                    en el juego, podremos ver en nuestras bolsas si un &iacute;tem es una mejora o no y cuanto % aumentar&iacute;a
                    nuestro daño al equiparlo. Es importante de revisar los stats CADA VEZ que se realiza un cambio de
                    Gear, ya que, el peso de nuestros stats es din&aacute;mico y puede ir cambiando seg&uacute;n como vayan variando
                    nuestros n&uacute;meros.</p>
            </div>
            <br>
            <br>
            <div>
                <p>
                    <i>Bueno, eso es todo el conocimiento que tengo respecto a simulaciones. Espero que este tutorial
                        les sea &uacute;til para aprender cosas nuevas. Yo personalmente vi un aumento en mi DPS una vez que
                        empec&eacute; a hacer simulaciones de mi personaje y a elegir en base a estas el equipo que deb&iacute;a
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