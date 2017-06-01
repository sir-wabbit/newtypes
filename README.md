# Newts

Use [this fork](https://github.com/alexknvl/paradise/commit/29ac9f6a5aa7e7b0d7784cb028a7bb0456ae2d97) of scalameta/paradise until https://github.com/scalameta/paradise/pull/207 is merged in.

## Quick Start
```scala
addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M8" cross CrossVersion.full)
resolvers += Resolver.bintrayRepo("alexknvl", "maven")
libraryDependencies += "com.alexknvl"  %%  "newts" % "0.0.1"
```

## License
Code is provided under the MIT license available at https://opensource.org/licenses/MIT,
as well as in the LICENSE file.
