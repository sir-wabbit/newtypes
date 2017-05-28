package newts

import scala.collection.immutable.Seq
import scala.meta._

object NewTypeMacros {
  def wrongTargetMessage(defn: Any): String =
    "(╯°□°）╯︵ ┻━┻ Can't make a newtype out of:\n$defn"
  val implTraitName: Type.Name = Type.Name("Impl")
  val implValueName: Term.Name = Term.Name("Impl")

  def typeDef(params: Seq[Type.Param], lo: Option[Type], up: Option[Type]): Decl.Type =
    Decl.Type(Seq(), Type.Name("T"), params, Type.Bounds(lo, up))

  def implMethodDefns(params: Seq[Type.Param], paramNames: Seq[Type.Name],
                      invariantParams: Seq[Type.Param], wrapped: Type): Seq[Decl.Def] =
    if (params.nonEmpty) Seq(
      q"def apply[..$invariantParams](value: $wrapped): T[..$paramNames]",
      q"def unwrap[..$invariantParams](value: T[..$paramNames]): $wrapped",
      q"def subst[F[_], ..$invariantParams](value: F[$wrapped]): F[T[..$paramNames]]")
    else Seq(
      q"def apply(value: $wrapped): T",
      q"def unwrap(value: T): $wrapped",
      q"def subst[F$$1[_]](value: F$$1[$wrapped]): F$$1[T]")

  def implTrait(isTranslucent: Boolean, params: Seq[Type.Param], paramNames: Seq[Type.Name],
                invariantParams: Seq[Type.Param], wrapped: Type): Defn.Trait =
    q"""trait $implTraitName {
          ${typeDef(params, None, if (isTranslucent) Some(wrapped) else None)}
          ..${implMethodDefns(params, paramNames, invariantParams, wrapped)}
        }"""

  def implValue(params: Seq[Type.Param], paramNames: Seq[Type.Name],
                invariantParams: Seq[Type.Param], wrapped: Type): Defn.Val = {
    if (params.nonEmpty)
      q"""val ${Pat.Var.Term(implValueName)}: $implTraitName = new ${Ctor.Ref.Name(implTraitName.value)} {
            type T[..$params] = $wrapped
            def apply[..$invariantParams](value: $wrapped): T[..$paramNames] = value
            def unwrap[..$invariantParams](value: T[..$paramNames]): $wrapped = value
            def subst[F[_], ..$invariantParams](value: F[$wrapped]): F[T[..$paramNames]] = value
          }"""
    else
      q"""val ${Pat.Var.Term(implValueName)}: $implTraitName = new ${Ctor.Ref.Name(implTraitName.value)} {
            type T = $wrapped
            def apply(value: $wrapped): T = value
            def unwrap(value: T): $wrapped = value
            def subst[F[_]](value: F[$wrapped]): F[T] = value
          }"""
  }

  def expandNewType(translucent: Boolean, mods: Seq[Mod], name: Type.Name, params: Seq[Type.Param], wrapped: Type, companion: Defn.Object): Term.Block = {
    val paramNames: Seq[Type.Name] = params.map {
      case tparam"..$mods ${name: Type.Name}[..$tparams] >: $tpeopt1 <: $tpeopt2 <% ..$tpes1 : ..$tpes2" =>
        name
    }

    val invariantParams = params.map(_.copy(mods = Seq()))

    val typeDef: Defn.Type =
      if (params.nonEmpty)
        q"""..$mods type $name[..$params] = ${companion.name}.$implValueName.T[..$paramNames]"""
      else
        q"""..$mods type $name = ${companion.name}.$implValueName.T"""

    val templateStats: Seq[Stat] =
      implTrait(translucent, params, paramNames, invariantParams, wrapped) +:
        implValue(params, paramNames, invariantParams, wrapped) +:
        companion.templ.stats.getOrElse(Nil)
    val newCompanion = companion.copy(
      templ = companion.templ.copy(stats = Some(templateStats)))

    Term.Block(Seq(typeDef, newCompanion))
  }

  def expandNewTypeForDefn(translucent: Boolean, defn: Any): Either[String, Stat] = defn match {
    case Term.Block(Seq(Defn.Type(mods, name, params, body), companion@Defn.Object(_, _, _))) =>
      Right(expandNewType(translucent = translucent, mods, name, params, body, companion))
    case Defn.Type(mods, name, params, body) =>
      val companion = q"object ${Term.Name(name.value)} { }"
      Right(expandNewType(translucent = translucent, mods, name, params, body, companion))
    case _ => Left(wrongTargetMessage(defn))
  }
}

class opaque extends scala.annotation.StaticAnnotation {
  inline def apply(defn: Any): Any = meta {
    NewTypeMacros.expandNewTypeForDefn(translucent = false, defn)
      .fold(e => { println(e); defn }, t => t)
  }
}

class translucent extends scala.annotation.StaticAnnotation {
  inline def apply(defn: Any): Any = meta {
    NewTypeMacros.expandNewTypeForDefn(translucent = true, defn)
      .fold(e => { println(e); defn }, t => t)
  }
}