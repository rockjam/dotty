package dotty.tools
package dottydoc

import org.junit.Test
import org.junit.Assert._

class WhitelistedStdLib extends DottyTest {
  val files: List[String] =
    StdLibSources.whitelisted.filterNot(_.endsWith("package.scala"))

  @Test def arrayHasDocumentation =
    checkFiles(TestWhitelistedCollections.files) { packages =>
      val array =
        packages("scala")
        .children.find(_.path.mkString(".") == "scala.Array")
        .get

      assert(array.comment.get.body.length > 0)
    }

  @Test def traitImmutableHasDocumentation =
    checkFiles(TestWhitelistedCollections.files) { packages =>
      val imm =
        packages("scala")
        .children.find(_.path.mkString(".") == "scala.Immutable")
        .get

      assert(
        imm.kind == "trait" && imm.name == "Immutable",
        "Found wrong `Immutable`")
      assert(
        imm.comment.map(_.body).get.length > 0,
        "Imm did not have a comment with length > 0")
    }
}

object TestWhitelistedCollections {
  val files: List[String] = {
    val whitelist = "./test/dotc/scala-collections.whitelist"

    scala.io.Source.fromFile(whitelist, "UTF8")
      .getLines()
      .map(_.trim) // allow identation
      .filter(!_.startsWith("#")) // allow comment lines prefixed by #
      .map(_.takeWhile(_ != '#').trim) // allow comments in the end of line
      .filter(_.nonEmpty)
      .filterNot(_.endsWith("package.scala"))
      .toList
  }
}
