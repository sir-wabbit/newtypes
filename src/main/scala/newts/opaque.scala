package newts

import scala.collection.immutable.Seq
import scala.meta._

class opaque extends scala.annotation.StaticAnnotation {
  inline def apply(defn: Any): Any = meta {
    def implTrait(repr: Type): Defn.Trait = {
      q"""trait Newt {
            type T
            def apply(i: $repr): T
            def unwrap(t: T): $repr
            def subst[F[_]](fa: F[Int]): F[T]
          }"""
    }

    def implVal(repr: Type): Defn.Val = {
      q"""val Newt: Newt = new Newt {
            type T = $repr
            def apply(x: $repr): T = x
            def unwrap(x: T): $repr = x
            def subst[F[_]](fa: F[Int]): F[T] = fa
          }"""
    }

    defn match {
      case tpe @ Defn.Type(mods, name, Seq(), body) =>
        val companion   =
          q"""object ${Term.Name(name.value)} {
                ${implTrait(body)}
                ${implVal(body)}
             }"""
        val newType = tpe.copy(body=Type.Name(name.value + ".Newt.T"))
        Term.Block(Seq(newType, companion))
      case _ =>
        println(defn.structure)
        abort("@opaque (╯°□°）╯︵ ┻━┻")
    }
  }
}