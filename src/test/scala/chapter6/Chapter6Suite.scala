package chapter6

class Chapter6Suite extends munit.FunSuite {
  test("Ex 6.4.4 == ValidatedTests") {
    import Chapter6.FormValidation._

    val getName = getValue("name") _
    println(getName.toString)

    getName(Map("name" -> "Dade Murphy"))
    println(getName)
  }
}
