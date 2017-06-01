package newtypes

import scala.collection.immutable.Seq
import scala.meta._

object NewTypeMacros {
  def wrongTargetMessage(defn: Any): String =
    s"(╯°□°）╯︵ ┻━┻ Can't make a newtype out of:\n$defn"
  val implTraitName: Type.Name = Type.Name("Impl")
  val implValueName: Term.Name = Term.Name("Impl")

  def typeNameOrApply(qual: Type, args: Seq[Type.Name]): Type =
    if (args.nonEmpty) Type.Apply(qual, args) else qual

  def typeDef(params: Seq[Type.Param], lo: Option[Type], up: Option[Type]): Decl.Type =
    Decl.Type(Seq(), Type.Name("T"), params, Type.Bounds(lo, up))

  def stripSpecialized(params: Seq[Type.Param]): Seq[Type.Param] =
    params.map(p => p.copy(mods = p.mods.filter {
        case Mod.Annot(body) => false
        case _ => true
      }))

  def stripVariance(params: Seq[Type.Param]): Seq[Type.Param] =
    params.map(p => p.copy(mods = p.mods.filter {
      case c: Mod.Covariant => false
      case c: Mod.Contravariant => false
      case _ => true
    }))

  def implDefn(translucent: Boolean, params: Seq[Type.Param], paramNames: Seq[Type.Name], wrapped: Type): (Defn.Trait, Defn.Val) = {
    val T = typeNameOrApply(t"T", paramNames)
    val F = t"F$$1"
    val Fp = tparam"F$$1[_]"

    (
      q"""trait $implTraitName {
            ${typeDef(params, None, if (translucent) Some(wrapped) else None)}
            def apply[..${stripVariance(params)}](value: $wrapped): $T
            def unwrap[..${stripVariance(params)}](value: $T): $wrapped
            def subst[$Fp, ..${stripSpecialized(stripVariance(params))}](value: $F[$wrapped]): $F[$T]
          }""",
      q"""val ${Pat.Var.Term(implValueName)}: $implTraitName = new ${Ctor.Ref.Name(implTraitName.value)} {
            type T[..$params] = $wrapped
            def apply[..${stripVariance(params)}](value: $wrapped): $T = value
            def unwrap[..${stripVariance(params)}](value: $T): $wrapped = value
            def subst[$Fp, ..${stripSpecialized(stripVariance(params))}](value: $F[$wrapped]): $F[$T] = value
          }""")
  }

  def expandNewType(translucent: Boolean, mods: Seq[Mod], name: Type.Name, params: Seq[Type.Param], wrapped: Type, companion: Defn.Object): Term.Block = {
    val paramNames: Seq[Type.Name] = params.map(tp => Type.Name(tp.name.value))

    val typeDef: Defn.Type = q"""..$mods type $name[..$params] = ?"""
      .copy(body = typeNameOrApply(t"${companion.name}.$implValueName.T", paramNames))

    val (implTrait, implVal) = implDefn(translucent, params, paramNames, wrapped)

    val templateStats: Seq[Stat] = implTrait +: implVal+: companion.templ.stats.getOrElse(Nil)
    val newCompanion = companion.copy(templ = companion.templ.copy(stats = Some(templateStats)))

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