# Newts

## Quick Start
```scala
addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-alex" cross CrossVersion.full)
resolvers += Resolver.bintrayRepo("alexknvl", "maven")
libraryDependencies += "com.alexknvl"  %%  "newts" % "0.0.1"
```

Use [this fork](https://github.com/alexknvl/paradise/commit/29ac9f6a5aa7e7b0d7784cb028a7bb0456ae2d97) of scalameta/paradise until https://github.com/scalameta/paradise/pull/207 is merged in. Clone it and `publishM2` in sbt.

## Why `newts`?
--------------------------------------------------
| Features | `AnyVal` | `@opaque` | `@translucent` |
|:---------|:--------:|:---------:|:--------------:|
| `isInstanceOf` | Yes | No | No |
| Generics box | Yes | No | No |
| Primitives box | Sometimes | Yes | No |
| Up-cast works | No | No | Yes |
| Down-cast works | No | No | No |
| Any methods | Yes | Yes | Yes |
| Overrides | Yes | No | No |
| Virtual dispatch | Yes | No | No |
| Wrap `List` elements | O(N) | O(1) | O(1) |
| Unwrap `List` elements | O(N) | O(1) | O(1) |

## License
Code is provided under the MIT license available at https://opensource.org/licenses/MIT,
as well as in the LICENSE file.
