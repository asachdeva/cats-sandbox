import $dep.`org.typelevel::cats-core:2.1.1`

import cats.implicits._

1 + 2
"Error".asLeft.getOrElse(0)
"Error".asLeft[Int].orElse(2.asRight[String])

-1.asRight[String].ensure("Must be non-negative!")(_ > 0)

"error".asLeft[Int].recover { case _: String =>
  -1
}

"error".asLeft[Int].recoverWith { case _: String =>
  -1.asRight
}

"foo".asLeft[Int].leftMap(_.reverse)

6.asRight[String].bimap(_.reverse, _ * 7)

"bar".asLeft[Int].bimap(_.reverse, _ * 7)

123.asRight[String]

123.asRight[String].swap

for {
  a <- 1.asRight[String]
  b <- 0.asRight[String]
  c <-
    if (b == 0) "DIVO".asLeft[Int]
    else (a / b).asRight[String]
} yield c * 100

type Result[A] = Either[Throwable, A]

val x = {
  println("Computing x")
  math.random
}

x

def y = {
  println("Computring y")
  math.random
}

y

y

lazy val z = {
  println("Computing z")
  math.random
}

z

import cats._

val now = Eval.now(math.random() + 1000)

val always = Eval.always(math.random() + 1000)

val later = Eval.later(math.random() + 1000)

now.value
always.value
later.value

val greeting = Eval
  .always {
    println("Step 1"); "Hello"
  }
  .map { str => println("Step 2"); s"$str world" }

greeting.value

def factorial(n: BigInt): BigInt =
  if (n == 1) n else n * factorial(n - 1)

factorial(7)

def factorial2(n: BigInt): Eval[BigInt] =
  if (n == 1)
    Eval.now(n)
  else
    Eval.defer(factorial2(n - 1).map(_ * n))

factorial2(50000).value
