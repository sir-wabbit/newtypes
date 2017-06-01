package test

import scala.language.higherKinds
import newtypes.{opaque, translucent}
import org.scalatest.{FlatSpec, FunSpec}

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

//  @opaque type OpaqueIntWithCompanion = Int
//  object OpaqueIntWithCompanion {
//    val a = 1
//  }
//  val b: Int = OpaqueIntWithCompanion.a

  @opaque type OpaqueListWithVariance[+A] = List[A]

  @opaque type OpaqueFunctionWithVariance[-A, +B] = A => B

  type HK[G[_], A] = G[A]
  @opaque type OpaqueHK[G[_], A] = HK[G, A]

//  Test2.Id
}

object Test2 {
  import scala.{ specialized => sp }

  @opaque type IArray[@sp A] = Array[A]
  object IArrayOps1 extends IArrayOps2 {
    implicit def toIntOps(value: IArray[Int]): IArrayOps.IntOps =
      new IArrayOps.IntOps(value)
  }
  trait IArrayOps2 {
    implicit def toAnyOps[A](value: IArray[A]): IArrayOps.AnyOps[A] =
      new IArrayOps.AnyOps[A](value)
  }

  object IArrayOps {
    class AnyOps[A](val value: IArray[A]) extends AnyVal {
      def apply(index: Int): A = IArray.Impl.unwrap(value)(index)
    }
    class IntOps(val value: IArray[Int]) extends AnyVal {
      def apply(index: Int): Int = IArray.Impl.unwrap(value)(index)
    }
  }

  import IArrayOps1._

  def run: Int = {
    val arr: IArray[Int] = IArray.Impl.apply(Array(1, 2, 3))
    arr(0)
  }
}
