package newtypes

import scala.collection.immutable.Seq
import scala.meta._

object NewTypeMacros {
  def wrongTargetMessage(defn: Any): String =
    s"(╯°□°）╯︵ ┻━┻ Can't make a newtype out of:\n$defn"
  val implTraitName: Type.Name = Type.Name("Impl")
  val implValueName: Term.Name = Term.Name("Impl")
  val baseName: Type.Name      = Type.Name("Base$$1")
  val tagName: Type.Name       = Type.Name("Tag$$1")
  val anyName: Type.Name       = Type.Name("scala.Any")

  def typeNameOrApply(qual: Type, args: Seq[Type.Name]): Type =
    if (args.nonEmpty) Type.Apply(qual, args) else qual

  def typeDef(params: Seq[Type.Param], lo: Option[Type], up: Option[Type]): Decl.Type =
    Decl.Type(Seq(), Type.Name("Type"), params, Type.Bounds(lo, up))

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

  def implDefn(translucent: Boolean, params: Seq[Type.Param], paramNames: Seq[Type.Name], wrapped: Type): Seq[Stat] = {
    val T = typeNameOrApply(t"Type", paramNames)
    val F = t"F$$1"
    val Fp = tparam"F$$1[_]"

    List(
      q"""type $baseName""",
      q"""trait $tagName extends Any""",
      q"""${typeDef(params, None, if (translucent) Some(Type.With(wrapped, tagName)) else Some(Type.With(baseName, tagName)))}""",
      q"""object $implValueName {
            def apply[..${stripVariance(params)}](value: $wrapped): $T = value.asInstanceOf[$T]
            def unwrap[..${stripVariance(params)}](value: $T): $wrapped = value.asInstanceOf[$wrapped]
            def subst[$Fp, ..${stripSpecialized(stripVariance(params))}](value: $F[$wrapped]): $F[$T] = value.asInstanceOf[$F[$T]]
          }""")
  }

  def expandNewType(translucent: Boolean, mods: Seq[Mod], name: Type.Name, params: Seq[Type.Param], wrapped: Type, companion: Defn.Object): Term.Block = {
    val paramNames: Seq[Type.Name] = params.map(tp => Type.Name(tp.name.value))

    val typeDef: Defn.Type = q"""..$mods type $name[..$params] = ?"""
      .copy(body = typeNameOrApply(t"${companion.name}.type#Type", paramNames))

    val stats = implDefn(translucent, params, paramNames, wrapped)

    val templateStats: Seq[Stat] = stats ++: companion.templ.stats.getOrElse(Nil)
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
