# Newts

Better newtypes for Scala based on the following two articles:
 * [The High Cost of AnyVal subclasses...](https://failex.blogspot.com/2017/04/the-high-cost-of-anyval-subclasses.html)
 * [...and the glorious subst to come](https://failex.blogspot.com/2017/04/and-glorious-subst-to-come.html)

## Quick Start
```scala
addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M10" cross CrossVersion.full)
resolvers += Resolver.bintrayRepo("alexknvl", "maven")
libraryDependencies += "com.alexknvl"  %%  "newtypes" % "0.3.0"
```

Use [this fork](https://github.com/alexknvl/paradise/commit/29ac9f6a5aa7e7b0d7784cb028a7bb0456ae2d97) 
of scalameta/paradise until https://github.com/scalameta/paradise/pull/207 is merged in if you need 
companion object support and **can't use `paradise-3.0.0-M10` (it supports only 2.12.3+)**. 
Clone it and `publishM2` in sbt, then change the `paradise` plugin to:
```scala
addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-alex" cross CrossVersion.full)
```

### Why `newts`?

| Features | `AnyVal` | `@newtypes.opaque` | `@newtypes.translucent` |
|:---------|:--------:|:---------:|:--------------:|
| `isInstanceOf` | Yes | No | No |
| Generics box `AnyRef` subtypes | Yes | **No** | **No** |
| Generics box primitives | Yes | Yes | Yes |
| Primitives box | No | Yes | **No** |
| Up-cast works | No | No | Yes |
| Down-cast works | No | No | No |
| Any methods | Yes | Yes | Yes |
| Overrides | Yes | No | No |
| Virtual dispatch | Yes | No | No |
| Typeclass-based dispatch | Yes | Yes | Yes |
| Wrap `List` elements | O(N) | **O(1)** (using `subst`) | **O(1)** (using `subst`) |
| Unwrap `List` elements | O(N) | **O(1)** (using `subst` and `Is`) | **O(1)** (automatic widening) |
| Supports HK parameters | Yes | Yes | Yes |
| Supports existential parameters | Yes | Yes | Yes |

### What does it do?

```scala
@opaque type ArrayWrapper[A] = Array[A]

@translucent type Flags = Int
```
becomes
```scala
type ArrayWrapper[A] = ArrayWrapper.Type[A]
object ArrayWrapper {
  type Base$$1
  trait Tag$$1 extends Any
  type Type[A] <: Base$$1 with Tag$$1
  
  object Impl {
    def apply[A](value: Array[A]): Type[A] = value.asInstanceOf[Type[A]]
    def unwrap[A](value: Type[A]): Array[A] = value.asInstanceOf[Array[A]]
    def subst[F[_], A](value: F[Array[A]]): F[Type[A]] = value.asInstanceOf[F[Type[A]]]
  }
}

type Flags = Flags.Type
object Flags {
  trait Tag$$1 extends Any
  type Type <: Int with Tag$$1
  object Impl {
    def apply(value: Int): Type = value.asInstanceOf[Type]
    def unwrap(value: Type): Int = value.asInstanceOf[Int]
    def subst[F[_]](value: F[Int]): F[Type] = value.asInstanceOf[F[Type]]
  }
}
```

## License
Code is provided under the MIT license available at https://opensource.org/licenses/MIT,
as well as in the LICENSE file.
