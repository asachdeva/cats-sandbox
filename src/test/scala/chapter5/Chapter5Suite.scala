package chapter5

class Chapter5Suite extends munit.FunSuite {

  test("Ex 5.4 -- EitherTTests") {
    import Chapter5.AutoBots._

    assert(tacticalReport("Jazz", "Bumblebee") == "Jazz and Bumblebee need a recharge")
    assert(tacticalReport("Jazz", "Hot Rod") == "Jazz and Hot Rod are ready to roll")
    assert(tacticalReport("Jazz", "Niobe") == "Comms error: Niobe unreachable")
  }
}
