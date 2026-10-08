package taller

import scala.annotation.tailrec

/**
 * Taller 1 — cifrados clásicos con recursión.
 *
 * Solo se cifran las 26 letras minúsculas del alfabeto inglés; cualquier otro
 * carácter se copia sin cambio.
 */
class CifradosClasicos {

  type Mensaje = String
  type Clave = String

  // Una frecuencia asocia cada letra con las veces que aparece.
  type Frecuencias = List[(Char, Int)]

  val letras = 26
  val primera = 'a'.toInt

  def esMinuscula(c: Char): Boolean = c >= 'a' && c <= 'z'

  // Punto 1 -------------------------------------------------------------------
  def cifrarChar(c: Char, k: Int): Char = {
    if (esMinuscula(c)) {
      val nuevaPosicion = Math.floorMod(c - 'a' + k, 26)
      ('a' + nuevaPosicion).toChar
    } else {
      c
    }
  }
  /** César con recursión lineal: una operación pendiente por letra. */
  def cesar(m: Mensaje, k: Int): Mensaje = {
    if (m.isEmpty) {
      ""
    } else {
      cifrarChar(m.head, k).toString + cesar(m.tail, k)
    }
  }

  // Punto 2 -------------------------------------------------------------------

  /**
   * El mismo César como proceso iterativo: espacio constante.
   * Cuando la función esté escrita, anótela con @tailrec: el compilador
   * comprueba que la llamada recursiva sea lo último que hace.
   */
  @tailrec
  final def cesarCola(m: Mensaje, k: Int, acc: Mensaje = ""): Mensaje = {
    if (m.isEmpty) acc
    else {
      cesarCola(m.tail,k ,acc + cifrarChar(m.head,k))
    }
  }


  // Punto 3 -------------------------------------------------------------------

  /**
   * Cuenta las letras minúsculas del mensaje, de mayor a menor frecuencia y,
   * en empate, en orden alfabético. El recorrido es recursivo de cola.
   */
  def frecuencias(m: Mensaje): Frecuencias ={

    @tailrec
    def contar(resto: List[Char], acc: Map[Char, Int]): Map[Char, Int] =
      resto match {
        case Nil => acc
        case c :: cola if esMinuscula(c) =>
          contar(cola, acc.updated(c, acc.getOrElse(c, 0)+1))
        case _ :: cola => contar(cola, acc)
      }
    contar(m.toList, Map()).toList.sortBy {case (c, n) => (-n, c)}
  }

  // Punto 4 -------------------------------------------------------------------

  /**
   * Supone que la letra más frecuente del mensaje cifrado es la 'e' del
   * original y devuelve la distancia entre las dos. Sin letras, cero.
   */
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

  // Punto 5 -------------------------------------------------------------------

  /**
   * Cuántos mensajes de longitud n se forman con a letras sin dos iguales
   * seguidas.
   */
  def combinaciones(n: Int, a: Int): BigInt = {
    if (n==0){
      BigInt(1)
    } else if (n==1){
      BigInt(a)
    } else{
      (BigInt(a-1)* combinaciones(n-1,a))
    }
  }

  /**
   * Vigenère: cada letra se corre según la letra de la clave que le toca. Lo
   * que no es letra minúscula se copia y no consume clave.
   */
  def vigenere(m: Mensaje, clave: Clave): Mensaje = {

    if (clave.isEmpty){
      m
    }else{
      def cifrar(
                  mensaje: List[Char],
                  posicionClave: Int
                ): String = {
        mensaje match {
          case Nil =>
            ""
          case c :: resto =>
            if (c >= 'a' && c <='z'){
              val k = clave(posicionClave % clave.length)
              val desplazamiento = k - 'a'
              val posicion = (c - 'a' + desplazamiento) %26
              val nuevaLetra = ('a' + posicion).toChar

              nuevaLetra + cifrar (resto, posicionClave + 1)
            } else {
              c + cifrar (resto, posicionClave)
            }
        }
      }
      cifrar(m.toList,0)
    }
  }
}
