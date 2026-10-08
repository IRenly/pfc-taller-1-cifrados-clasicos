# Informe de Corrección: Cifrados Clásicos con Recursión

Fundamentos de Programación Funcional y Concurrente  


---

## Punto 1: Cifrado César Lineal (`cesar`) — Inducción Estructural

Demostramos que `cesar(m, k)` cifra correctamente cualquier mensaje $m$ mediante inducción estructural sobre cadenas:

* **Caso base:** Si la cadena es vacía `""`, la condición `if (m.isEmpty)` retorna `""` inmediatamente. Esto es correcto porque un mensaje vacío no tiene caracteres para cifrar.
* **Hipótesis de inducción (HI):** Asumimos que para la cola del mensaje (`m.tail`) la función ya opera correctamente y devuelve todas sus letras cifradas con la clave $k$.
* **Paso inductivo:** Para un mensaje completo `m.head +: m.tail`, el código ejecuta:
  $$\text{cifrarChar}(m.\text{head}, k) + \text{cesar}(m.\text{tail}, k)$$
  Como `cifrarChar` cifra correctamente el primer carácter y por hipótesis `cesar(m.tail, k)` ya tiene cifrado todo el resto, al concatenar ambos resultados obtenemos el mensaje completo cifrado correctamente.

Por lo tanto, la función es correcta para cualquier mensaje por inducción estructural.

---

## Punto 2: Cifrado César de Cola (`cesarCola`) — Proceso Iterativo con Acumulador

Argumentamos la corrección de `cesarCola` formalizando su proceso iterativo:

* **Estado:** $(m, acc)$, donde $m$ es el mensaje que falta por procesar y $acc$ es el texto que ya acumulamos en la maleta.
* **Estado inicial:** $(m_0, "")$, arrancando con el mensaje original y el acumulador vacío.
* **Estado final:** $("", acc_f)$, cuando no quedan caracteres por procesar (`m.isEmpty == true`).
* **Respuesta:** En el estado final retorna el acumulador: $\text{respuesta}(("", acc_f)) = acc_f$.

### Invariante
En todo momento de la ejecución se cumple:
$$acc + \text{cifrar}(m, k) == \text{cifrar}(m_0, k)$$

> **En palabras sencillas:** Lo que ya llevo en el acumulador $acc$ más lo que me falta por cifrar del mensaje $m$, siempre equivale al mensaje final esperado.

### Demostración:
1. **Inicio:** Al comenzar, $acc$ es `""`, por lo que `"" + cifrar(m_0, k) == cifrar(m_0, k)`. El invariante se cumple.
2. **Paso a paso (Preservación):** En cada llamada recursiva hacemos `cesarCola(m.tail, k, acc + cifrarChar(m.head, k))`. La letra que quitamos de la cabeza de $m$ pasa cifrada al acumulador $acc$, por lo que la suma total de lo acumulado más lo pendiente se mantiene intacta.
3. **Final:** Al llegar al caso base ($m = ""$), se tiene $acc + "" = acc$. Por el invariante, ese valor acumulado $acc$ es exactamente el mensaje completo cifrado.
4. **Terminación:** En cada iteración procesamos `m.tail`, reduciendo la longitud de $m$ en 1 carácter. Como el mensaje tiene una longitud finita, obligatoriamente llegará a `""` tras un número finito de pasos, garantizando que el programa siempre termina.

---

## Punto 3: Frecuencias (`frecuencias`)

*(A cargo del integrante del equipo responsable del Punto 3).*

---

## Punto 4: Romper César (`desplazamientoProbable` y `romperCesar`)

Se desea romper un mensaje cifrado por cesar del cual se desconoce el desplazamiento, calculando la distancia entre las dos letras más frecuentes del mensaje, la primera se asume que es 'e'.

### Código analizado
``` scala
def desplazamientoProbable(m: Mensaje): Int = {
    def getFrequentChar(frec: Frecuencias): Char = 
    frec match {
      case Nil => '0'
      case x :: xs => if(x._1 == 'e') getFrequentChar(xs) else x._1
    }

    val frequentChar = getFrequentChar(frecuencias(m))

    if (frequentChar == '0') 0
    else {
        if ('e' > frequentChar) (('e' - frequentChar) - letras) * -1
        else ('e' - frequentChar) * -1
    }

}

def romperCesar(m: Mensaje): Mensaje = {
  cesar(m, -desplazamientoProbable(m))
}
```

### Especificación
Sea $m = c_1 c_2 \ldots c_n$ un mensaje. Sea $Frecuencias$ una tulpa con parejas de las letras del mensaje y su cuenta de aparición, organizadas de mayor a menor numerica, y alfabéticamente en empate.  La función debe devolver el valor número de la distancia entre la letra $\text{e}$ y el primer objeto de $Frecuencias$. 

La función sigue recorriendo $Frecuencias$ si la letra $e$ está en el primer index para así no devolver un desplazamiento de 0.

### Proceso Iterativo de `getFrequentChar`
- **Estado**: $s = frec$ donde $frec = Frecuencias[a_i, a_i+1,\ldots,a_k].$
- **Estado Inicial $(s_0)$**: `frecuencias(m)`. 
- **Estado Final $(s_f)$ :** Es final si $frec$ es vacía o si el primer subindex de $ai$, donde se encuentra el caracter, es distinto de la letra $e$.
- **Invariante**: $ Inv(frec) \equiv$ todos los elementos eliminados de la lista original tienen carácter '$e$'.
- **Transformar:** $frec = frec.xs$ siempre que $frec.x.$_$1 = $ '$e$'.
- **Respuesta**: La función retorna el caracter $0$ si la lista está vacía, o el carácter del estado final.

### Demostración

**1.** $\text{Inv}(s_0)$: el estado inicial cumple la condición invariante.

$s_0 = \text{frecuencias}(m)$

Por lo tanto, la invariante se cumple.

**2.** Si la lista está vacía, se devuelve '0'. Caso contrario:

$(s_i \neq s_f \land \text{Inv}(s_i)) \rightarrow \text{Inv}(\text{transformar}(s_i))$

Si la primera letra es diferente de 'e', el proceso termina y devuelve esa letra. En ese caso, getFrequentChar descarta x y continúa con xs. Como el elemento descartado es 'e', el invariante sigue cumpliéndose:

$Inv(s_i) \Rightarrow Inv(xs)$

**3.** $\text{Inv}(s_f) \rightarrow \text{respuesta}(s_f) == f(a)$

Si se encuentra una letra distinta a la 'e', getFrequentChar devuelve esa letra.

Si se llega a Nil, devuelve '0', indicando que no se encontró una letra diferente de 'e'.

**4.** Terminación

Ya que la función `frecuencias()` da un $Frecuencias$ donde no hay caracteres repetidos, si se encuentra 'e' como letra más frecuente se elimina el primer elemento de la lista. Después se vuelve a recorrer la lista y se llega a Nil o se encuentra la letra más frecuente diferente de 'e'.

Si la lista estaba vacía o no se encontró una letra diferente de 'e', dando como resultado un desplazamiento de 0. En el caso opuesto, el resultado es la letra más frecuente. 

Ya con la letra obtenida, a quien llamaremos `C`, se calcula la distancia entre esta y la letra $e$, donde:
- Si `C > 'e'`, se hace la operación `(('e' - C) - letras) * -1` donde `letras = 26` que es el número de letras minusculas.
- Caso contrario, la operación es `('e' - C) * -1`.

Y ahora se utiliza `romperCesar(m)`, que recibe el mensaje cifrado e internamente llama a la función `cesar(m,k)` con el mensaje y el negativo de `desplazamientoProbable()`.

Para que la función se ejecute correctamente, el mensaje debe tener la letra $e$ como primera letra más frecuente. En el caso donde el mensaje es *"El triunfo es permanente"*, su versión cifrada podrá romperse porque la letra $e$ es la primera más frecuente. Pero en el caso donde el mensaje es *"La derrota es devastadora"*, `desplazamientoProbable()` falla porque la letra $a$ es la primera más frecuente, no la letra $e$.

---

## Punto 5: Combinaciones y Cifrado Vigenère (`combinaciones` y `vigenere`)

Se desea calcular la cantidad de mensajes de longitud (n) que pueden formarse con un alfabeto de (a) letras sin que existan dos letras iguales consecutivas. Además, se desea implementar el cifrado Vigenère, donde cada letra minúscula del mensaje se desplaza según la letra correspondiente de la clave.

Código analizado
def combinaciones(n: Int, a: Int): BigInt = {
if (n == 0) {
BigInt(1)
} else if (n == 1) {
BigInt(a)
} else {
BigInt(a - 1) * combinaciones(n - 1, a)
}
}

def vigenere(m: Mensaje, clave: Clave): Mensaje = {

if (clave.isEmpty) {
m
} else {

    def cifrar(
        mensaje: List[Char],
        posicionClave: Int
    ): String = {

      mensaje match {

        case Nil =>
          ""

        case c :: resto =>

          if (c >= 'a' && c <= 'z') {

            val k = clave(posicionClave % clave.length)

            val desplazamiento = k - 'a'
            val posicion = (c - 'a' + desplazamiento) % 26
            val nuevaLetra = ('a' + posicion).toChar

            s"$nuevaLetra${cifrar(resto, posicionClave + 1)}"

          } else {

            s"$c${cifrar(resto, posicionClave)}
          }
        }
      }

      cifrar(m.toList, 0)
    }
}
}
Especificación de combinaciones

Sea (C(n,a)) la cantidad de mensajes de longitud (n) que se pueden formar utilizando un alfabeto de (a) letras sin tener dos letras iguales consecutivas.

La función debe cumplir:

$$
C(n,a)=
\begin{cases}
1 & \text{si } n=0\
a & \text{si } n=1\
(a-1)C(n-1,a) & \text{si } n>1
\end{cases}
$$

Demostración de combinaciones

1. Caso base: (n=0)

Cuando la longitud del mensaje es cero solamente existe un mensaje posible: el mensaje vacío.

El código realiza:

if (n == 0) {
BigInt(1)
}

Por lo tanto:

$$
C(0,a)=1
$$

que coincide con la especificación.

2. Caso base: (n=1)

Cuando el mensaje tiene una sola letra, cualquiera de las (a) letras del alfabeto puede ser utilizada.

El código realiza:

else if (n == 1) {
BigInt(a)
}

Por lo tanto:

$$
C(1,a)=a
$$

que también coincide con la especificación.

3. Hipótesis de inducción

Suponemos que para una longitud (n-1), la función calcula correctamente la cantidad de mensajes:

$$
C(n-1,a)
$$

4. Paso inductivo

Para formar un mensaje de longitud (n), primero se puede escoger cualquiera de los mensajes válidos de longitud (n-1).

Por hipótesis de inducción existen:

$$
C(n-1,a)
$$

posibilidades.

Una vez escogido el mensaje anterior, la nueva letra no puede ser igual a la última letra utilizada. Como existen (a) letras disponibles y una de ellas no puede utilizarse, quedan:

$$
a-1
$$

posibilidades.

Por lo tanto:

$$
C(n,a)=(a-1)C(n-1,a)
$$

Esto es exactamente lo que realiza la función:

BigInt(a - 1) * combinaciones(n - 1, a)

Por lo tanto, por inducción sobre (n), la función combinaciones calcula correctamente la cantidad de mensajes de longitud (n) sin dos letras iguales consecutivas.

Encadenamiento de llamadas de combinaciones

Por ejemplo, para:

combinaciones(3, 26)

la función realiza las siguientes llamadas:

$$
C(3,26)
$$

$$
=25C(2,26)
$$

$$
=25(25C(1,26))
$$

$$
=25(25(26))
$$

$$
=16250
$$

La última llamada llega al caso base (C(1,26)=26). Después, las llamadas anteriores multiplican ese resultado por (25).

Especificación de vigenere

Sea:

$$
m=c_1c_2\ldots c_n
$$

un mensaje y sea (k) la clave utilizada para cifrarlo.

Para cada letra minúscula del mensaje se utiliza una letra de la clave. El desplazamiento corresponde a la posición de la letra de la clave dentro del alfabeto.

El nuevo carácter se calcula mediante:

$$
c'=(c+k)\bmod 26
$$

La clave se repite cuando se alcanza su final.

Los caracteres que no son letras minúsculas se copian sin modificaciones y no consumen una posición de la clave.

Caso base: clave vacía

Antes de comenzar el proceso se verifica:

if (clave.isEmpty) {
m
}

Si la clave está vacía no existe ningún desplazamiento que aplicar. Por lo tanto, el mensaje debe permanecer sin cambios:

$$
V(m,"")=m
$$

Esto coincide con la especificación.

Caso base: mensaje vacío

La función auxiliar cifrar procesa el mensaje mediante recursión sobre una lista de caracteres.

Cuando no quedan caracteres:

case Nil =>
""

Por lo tanto:

$$
V("",clave)=""
$$

Esto es correcto porque no existen caracteres pendientes de cifrar.

Hipótesis de inducción

Suponemos que la función auxiliar cifra correctamente el resto del mensaje, utilizando la posición correspondiente de la clave.

Sea:

$$
m=c::resto
$$

y supongamos que cifrar(resto, posicionClave) produce correctamente el cifrado del resto del mensaje.

Paso inductivo: carácter minúsculo

Si el carácter actual es una letra minúscula, el código obtiene la letra correspondiente de la clave:

val k = clave(posicionClave % clave.length)

El operador módulo permite repetir la clave cuando se llega a su final.

Después se calcula:

val desplazamiento = k - 'a'
val posicion = (c - 'a' + desplazamiento) % 26
val nuevaLetra = ('a' + posicion).toChar

Esto corresponde matemáticamente a:

$$
c'=(c+k)\bmod26
$$

Por lo tanto, el carácter actual se cifra correctamente.

Luego se realiza la llamada recursiva:

cifrar(resto, posicionClave + 1)

La posición de la clave aumenta en uno porque el carácter actual es una letra minúscula y, por lo tanto, consume una posición de la clave.

Por la hipótesis de inducción, el resto del mensaje también será cifrado correctamente.

Finalmente, se concatena la letra cifrada con el resultado de la llamada recursiva:

s"$nuevaLetra${cifrar(resto, posicionClave + 1)}"

Por lo tanto, se obtiene correctamente el mensaje cifrado.

Paso inductivo: carácter que no es letra minúscula

Si el carácter actual no es una letra minúscula, la función no realiza ningún desplazamiento.

En este caso se ejecuta:

s"$c${cifrar(resto, posicionClave)}"

El carácter se copia directamente al resultado y la posición de la clave permanece igual.

Esto es necesario porque los espacios y demás caracteres que no son letras minúsculas no deben consumir una posición de la clave.

Por lo tanto, si tenemos:

hola mundo

el espacio entre hola y mundo no hace avanzar la clave.

Repetición de la clave

La repetición de la clave se consigue mediante:

clave(posicionClave % clave.length)

Por ejemplo, si la clave es:

sol

sus posiciones son:

$$
0,1,2
$$

Cuando se llega nuevamente a la posición (3):

$$
3\bmod3=0
$$

por lo que se vuelve a utilizar la primera letra de la clave.

Así, la secuencia de posiciones utilizadas es:

$$
0,1,2,0,1,2,0,1,2,\ldots
$$

Por lo tanto, la clave puede repetirse durante todo el mensaje.

Encadenamiento de llamadas de vigenere

Para el ejemplo:

vigenere("abc", "bc")

la primera llamada comienza con:

cifrar("abc", 0)

La letra a utiliza la letra b de la clave. Después se realiza:

cifrar("bc", 1)

La letra b utiliza la letra c de la clave. Después se realiza:

cifrar("c", 2)

Como la clave tiene longitud (2), la posición utilizada es:

$$
2\bmod2=0
$$

por lo que nuevamente se utiliza la primera letra de la clave, b.

Finalmente se realiza:

cifrar("", 1)

que devuelve:

""

Las llamadas pendientes concatenan sus resultados hasta obtener el mensaje cifrado.

Ejemplo con un espacio

Para:

vigenere("hola mundo", "ab")

la clave se utiliza de la siguiente manera:

Mensaje: h o l a   m u n d o
Clave:   a b a b   a b a b a

El espacio no consume una posición de la clave. Por eso, la letra m vuelve a utilizar la letra a.

El resultado obtenido es:

hplb mvneo

que coincide con el resultado esperado.

Conclusión

La función combinaciones es correcta porque cumple los dos casos base establecidos y aplica correctamente la recurrencia:

$$
C(n,a)=(a-1)C(n-1,a)
$$

para todo (n>1).

Por otra parte, vigenere procesa recursivamente cada carácter del mensaje, aplica el desplazamiento correspondiente de la clave mediante módulo (26), repite la clave cuando es necesario y conserva los caracteres que no son letras minúsculas sin consumir una posición de la clave.

Por lo tanto, las funciones combinaciones y vigenere cumplen con la especificación establecida para el Punto 5.