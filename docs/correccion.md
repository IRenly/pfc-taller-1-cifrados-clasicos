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

```scala
def frecuencias(m: Mensaje): Frecuencias = {

  @tailrec
  def contar(resto: List[Char], acc: Map[Char, Int]): Map[Char, Int] =
    resto match {
      case Nil => acc
      case c :: cola if esMinuscula(c) =>
        contar(cola, acc.updated(c, acc.getOrElse(c, 0) + 1))
      case _ :: cola => contar(cola, acc)
    }

  contar(m.toList, Map()).toList.sortBy { case (c, n) => (-n, c) }
}
```
### Especificación

Sea $m = c_1 c_2 \ldots c_n$ un mensaje. Sea $\text{cuenta}(\ell, x)$ el número
de veces que aparece la letra $\ell$ en la secuencia $x$. La función debe
devolver las parejas $(\ell, \text{cuenta}(\ell, m))$ de las letras minúsculas
con cuenta mayor que cero, ordenadas de mayor a menor frecuencia y, en empate,
alfabéticamente.

### Proceso iterativo de `contar`

- Un estado $s = (resto, acc)$, donde $resto$ es la lista de caracteres por
  revisar y $acc$ es el mapa de conteos.
- El estado inicial es $s_0 = (m.\text{toList}, \text{Map}())$.
- Un estado es final si $resto = \text{Nil}$.
- La invariante es
  $\text{Inv}(resto, acc) \equiv resto = \text{List}(c_i, \ldots, c_n) \land$
  $acc$ contiene, para cada minúscula $\ell$ que aparece en $c_1 \ldots c_{i-1}$,
  el valor $\text{cuenta}(\ell, c_1 \ldots c_{i-1})$, y ninguna otra entrada.
- $\text{transformar}((c_i :: cola, acc))$ es $(cola, acc')$, donde
  $acc' = acc.\text{updated}(c_i, acc(c_i) + 1)$ si $c_i$ es minúscula
  (con $acc(c_i) = 0$ si no estaba), y $acc' = acc$ si no lo es.

### Demostración

**1.** $\text{Inv}(s_0)$: en $s_0$, $resto = \text{List}(c_1, \ldots, c_n)$ y
$acc$ está vacío, que es el conteo de la secuencia vacía ($i = 1$).

**2.** La invariante se mantiene. Si $\text{Inv}(s_i)$ y $resto \neq \text{Nil}$:

- Si $c_i$ es minúscula, su conteo sube en 1, así que $acc'$ es el conteo de
  $c_1 \ldots c_i$.
- Si no lo es, el conteo de $c_1 \ldots c_i$ es el mismo que el de
  $c_1 \ldots c_{i-1}$, y $acc' = acc$.
- En ambos casos $cola = \text{List}(c_{i+1}, \ldots, c_n)$.

**3.** En el estado final, $resto = \text{Nil}$, o sea $i = n + 1$. Por la
invariante, $acc$ tiene $\text{cuenta}(\ell, m)$ para cada minúscula de $m$.

**4.** Terminación: en cada paso $resto$ pierde un elemento, así que después de
$n$ pasos queda vacío.

### Orden final

`sortBy` con la clave $(-n, \ell)$ ordena las parejas de mayor a menor $n$ y, si
hay empate, por letra. Como cada letra aparece una sola vez en el mapa, dos
parejas nunca tienen la misma clave. Por eso el orden es único y coincide con
el que pide el enunciado.

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

*(A cargo del integrante del equipo responsable del Punto 5).*

