import newts.opaque

object Test {

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

  @opaque type A = Int
}
