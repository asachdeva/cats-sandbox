package chapter5

object Chapter5 {

  //Ex 5.4
  object AutoBots {

    import cats.data.EitherT
    import scala.concurrent.Future
    import scala.concurrent.ExecutionContext.Implicits.global
    import scala.concurrent.Await
    import scala.concurrent.duration._
    import cats.implicits._

    type Response[A] = EitherT[Future, String, A]

    val powerLevels = Map("Jazz" -> 6, "Bumblebee" -> 8, "Hot Rod" -> 10)

    def getPowerLevel(ally: String): Response[Int] =
      powerLevels.get(ally) match {
        case Some(avg) => EitherT.right(Future(avg))
        case None      => EitherT.left(Future(s"$ally unreachable"))
      }

    def canSpecialMove(ally1: String, ally2: String): Response[Boolean] =
      for {
        power1 <- getPowerLevel(ally1)
        power2 <- getPowerLevel(ally2)
      } yield (power1 + power2) > 15

    def tacticalReport(ally1: String, ally2: String): String = {
      val stack = canSpecialMove(ally1, ally2).value

      Await.result(stack, 1.second) match {
        case Left(msg) =>
          s"Comms error: $msg"
        case Right(true) =>
          s"$ally1 and $ally2 are ready to roll"
        case Right(false) =>
          s"$ally1 and $ally2 need a recharge"
      }
    }
  }

}
