package test

import scala.language.higherKinds
import newtypes.{opaque, translucent}
import org.scalatest.{FlatSpec, FunSpec, FunSuite, Matchers}

class Foo[A]

class Test extends FunSuite with Matchers {
  @opaque type OpaqueInt = Int

  test("casting array of opaque types") {
    val arr: Array[Int] = Array(1, 2, 3)
    OpaqueInt.Impl.subst[Array](arr)(0)
  }

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
    implicit val foo: Foo[OpaqueIntWithCompanion] = new Foo[OpaqueIntWithCompanion]()
  }
  val b: Int = OpaqueIntWithCompanion.a

  implicitly[Foo[OpaqueIntWithCompanion]]

  @opaque type OpaqueListWithVariance[+A] = List[A]

  @opaque type OpaqueFunctionWithVariance[-A, +B] = A => B

  type HK[G[_], A] = G[A]
  @opaque type OpaqueHK[G[_], A] = HK[G, A]

//  Test2.Id
}

object ImmutableArray {
  import scala.{ specialized => sp }

  trait IArray1 {
    implicit def toAnyOps[A](value: IArray[A]): AnyOps[A] =
      new AnyOps[A](value)
  }
  @opaque type IArray[@sp A] = Array[A]
  object IArray extends IArray1 {
    implicit def toIntOps(value: Type[Int]): IntOps =
      new IntOps(value)
  }

  class AnyOps[A](val value: IArray[A]) extends AnyVal {
    def apply(index: Int): A = IArray.Impl.unwrap(value)(index)
  }
  class IntOps(val value: IArray[Int]) extends AnyVal {
    def apply(index: Int): Int = IArray.Impl.unwrap(value)(index)
  }
}

class Test2 extends FunSuite with Matchers {
  import ImmutableArray._

  test("immutable arrays") {
    run should be (1)
  }

  def run: Int = {
    val arr: IArray[Int] = IArray.Impl.apply(Array(1, 2, 3))
    arr(0)
  }
}
