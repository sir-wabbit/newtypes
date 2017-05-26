package newts

import scala.collection.immutable.Seq
import scala.meta._

class opaque extends scala.annotation.StaticAnnotation {
  inline def apply(defn: Any): Any = meta {
    def createApply(name: Type.Name, paramss: Seq[Seq[Term.Param]]): Defn.Def = {
      val args = paramss.map(_.map(param => Term.Name(param.name.value)))
      q"""def apply(...$paramss): $name =
            new ${Ctor.Ref.Name(name.value)}(...$args)"""
    }

    def implTrait(repr: Type): Defn.Trait = {
      /*trait Newt {
        type T
        def apply(i: Int): T
        def unwrap(t: T): Int
        def subst[F[_]](fa: F[Int]): F[T]
      }*/
      q"""trait Newt {
            type T
            def apply(i: $repr): T
            def unwrap(t: T): $repr
            def subst[F[_]](fa: F[Int]): F[T]
          }"""
    }

    def implVal(repr: Type): Defn.Val = {
      /*val Newt: Newt = new Newt {
        type T
        def apply(i: Int): T = i
        def unwrap(t: T): Int = t
        def subst[F[_]](fa: F[Int]): F[T] = fa
      }*/

      q"""val Newt: Newt = new Newt {
            type T = $repr
            def apply(x: $repr): T = x
            def unwrap(x: T): $repr = x
            def subst[F[_]](fa: F[Int]): F[T] = fa
          }"""
    }

    defn match {
      case tpe @ Defn.Type(mods, name, Seq(), body) =>
        /*type Manual = Manual.$Impl.T
          object Manual { }*/
//        val applyMethod = createApply(name, Seq.empty)
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