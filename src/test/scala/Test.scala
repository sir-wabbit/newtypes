import newts.{opaque, translucent}

object Test {
  // @opaque type Manual = Int
  type Manual = Manual.Newt.T
  object Manual {
    trait Newt {
      type T
      def apply(i: Int): T
      def unwrap(t: T): Int
      def subst[F[_]](fa: F[Int]): F[T]
    }
    val Newt: Newt = new Newt {
      type T = Int
      def apply(i: Int): T = i
      def unwrap(t: T): Int = t
      def subst[F[_]](fa: F[Int]): F[T] = fa
    }
  }

  // @opaque type ManualK[A] = List[A]
  type ManualK[A] = ManualK.Newt.T[A]
  object ManualK {
    trait Newt {
      type T[A]
      def apply[A](i: List[A]): T[A]
      def unwrap[A](t: T[A]): List[A]
      def subst[F[_], A](fa: F[List[A]]): F[T[A]]
    }
    val Newt: Newt = new Newt {
      type T[A] = List[A]
      def apply[A](i: List[A]): T[A] = i
      def unwrap[A](t: T[A]): List[A] = t
      def subst[F[_], A](fa: F[List[A]]): F[T[A]] = fa
    }
  }

  // @opaque type ManualEx[A] = Int
  type ManualEx[A] = ManualEx.Newt.T[A]
  object ManualEx {
    trait Newt {
      type T[A]
      def apply[A](i: Int): T[A]
      def unwrap[A](t: T[A]): Int
      def subst[F[_], A](fa: F[Int]): F[T[A]]
    }
    val Newt: Newt = new Newt {
      type T[A] = Int
      def apply[A](i: Int): T[A] = i
      def unwrap[A](t: T[A]): Int = t
      def subst[F[_], A](fa: F[Int]): F[T[A]] = fa
    }
  }

  @opaque type Auto = Int
  @opaque type AutoK[A] = Array[A]
  @opaque type AutoEx[A] = Int

  @opaque type Auto1 = (String, List[Int])
  @opaque type AutoK1[A <: Int] = Array[A]

  @translucent type Auto2 = Int
  @translucent type AutoK2[A] = Array[A]
  @translucent type AutoEx2[A] = Int

  @translucent type Auto3 = (String, List[Int])
  @translucent type AutoK3[A <: Int] = Array[A]
//  @opaque type AutoEx1[-A] = Int
}
