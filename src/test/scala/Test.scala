package test

import scala.language.higherKinds

import newts.{opaque, translucent}

object Test {
  @opaque type OpaqueInt = Int

  @opaque type OpaqueArray[A] = Array[A]

  @opaque type OpaqueIntWithExtraTypeParam[A] = Int

  @opaque type OpaqueTuple = (String, List[Int])

  @opaque type OpaqueArrayWithBounds[A >: Int <: AnyVal] = Array[A]

  @translucent type TranslucentInt = Int

  @translucent type TranslucentArray[A] = Array[A]

  @translucent type TranslucentIntWithExtraParam[A] = Int

  @translucent type TranslucentTuple = (String, List[Int])

  @translucent type TranslucentArrayWithBounds[A >: Int <: AnyVal] = Array[A]

  @opaque type OpaqueIntWithCompanion = Int
  object OpaqueIntWithCompanion {
    val a = 1
  }
  val b: Int = OpaqueIntWithCompanion.a
}
