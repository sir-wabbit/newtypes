package newts

import scala.collection.immutable.Seq
import scala.meta._

object MacroUtil {
  val ImplTraitName: Type.Name = Type.Name("Impl")
  val ImplValueName: Term.Name = Term.Name("Impl")

  def typeDef(params: Seq[Type.Param], lo: Option[Type], up: Option[Type]): Decl.Type =
    Decl.Type(Seq(), Type.Name("T"), params, Type.Bounds(lo, up))

  def implMethodDefns(params: Seq[Type.Param], paramNames: Seq[Type.Name], wrapped: Type): Seq[Decl.Def] =
    if (params.nonEmpty) Seq(
      q"def apply[..$params](value: $wrapped): T[..$paramNames]",
      q"def unwrap[..$params](value: T[..$paramNames]): $wrapped",
      q"def subst[F[_], ..$params](value: F[$wrapped]): F[T[..$paramNames]]")
    else Seq(
      q"def apply(value: $wrapped): T",
      q"def unwrap(value: T): $wrapped",
      q"def subst[F[_]](value: F[$wrapped]): F[T]")

  def implTrait(isTranslucent: Boolean, params: Seq[Type.Param], paramNames: Seq[Type.Name], wrapped: Type): Defn.Trait =
    q"""trait $ImplTraitName {
          ${typeDef(params, None, if (isTranslucent) Some(wrapped) else None)}
          ..${implMethodDefns(params, paramNames, wrapped)}
        }"""

  def implValue(params: Seq[Type.Param], paramNames: Seq[Type.Name], wrapped: Type): Defn.Val = {
    if (params.nonEmpty)
      q"""val ${Pat.Var.Term(ImplValueName)}: $ImplTraitName = new ${Ctor.Ref.Name(ImplTraitName.value)} {
              type T[..$params] = $wrapped
              def apply[..$params](value: $wrapped): T[..$paramNames] = value
              def unwrap[..$params](value: T[..$paramNames]): $wrapped = value
              def subst[F[_], ..$params](value: F[$wrapped]): F[T[..$paramNames]] = value
            }"""
    else
      q"""val ${Pat.Var.Term(ImplValueName)}: $ImplTraitName = new ${Ctor.Ref.Name(ImplTraitName.value)} {
              type T = $wrapped
              def apply(value: $wrapped): T = value
              def unwrap(value: T): $wrapped = value
              def subst[F[_]](value: F[$wrapped]): F[T] = value
            }"""
  }

  def newType(isTranslucent: Boolean, mods: Seq[Mod], name: Type.Name, params: Seq[Type.Param], wrapped: Type) = {
    val paramNames: Seq[Type.Name] = params.map {
      case tparam"..$mods ${name: Type.Name}[..$tparams] >: $tpeopt1 <: $tpeopt2 <% ..$tpes1 : ..$tpes2" =>
        name
    }
    val companion: Defn.Object =
      q"""object ${Term.Name(name.value)} {
            ${implTrait(isTranslucent, params, paramNames, wrapped)}
            ${implValue(params, paramNames, wrapped)}
          }"""
    val typeDef: Defn.Type =
      if (params.nonEmpty)
        q"""..$mods type $name[..$params] = ${companion.name}.$ImplValueName.T[..$paramNames]"""
      else
        q"""..$mods type $name = ${companion.name}.$ImplValueName.T"""

    Term.Block(Seq(typeDef, companion))
  }
}

class opaque extends scala.annotation.StaticAnnotation {
  inline def apply(defn: Any): Any = meta {
    import MacroUtil._

    defn match {
      case q"..$mods type $name[..$params] = $wrapped" =>
        newType(isTranslucent = false, mods, name, params, wrapped)
      case q"..$mods type $name = $wrapped" =>
        newType(isTranslucent = false, mods, name, Seq.empty, wrapped)
      case _ =>
        println(defn.structure)
        abort("@opaque (╯°□°）╯︵ ┻━┻")
    }
  }
}

class translucent extends scala.annotation.StaticAnnotation {
  inline def apply(defn: Any): Any = meta {
    import MacroUtil._

    defn match {
      case q"..$mods type $name[..$params] = $wrapped" =>
        newType(isTranslucent = true, mods, name, params, wrapped)
      case q"..$mods type $name = $wrapped" =>
        newType(isTranslucent = true, mods, name, Seq.empty, wrapped)
      case _ =>
        println(defn.structure)
        abort("@opaque (╯°□°）╯︵ ┻━┻")
    }
  }
}