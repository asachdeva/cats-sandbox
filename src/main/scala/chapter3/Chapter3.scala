package chapter3

object Chapter3 {

  object Tree {
    import cats.Functor
    // 3.5.4
    sealed trait Tree[+A]
    final case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]
    final case class Leaf[A](value: A) extends Tree[A]

    implicit val treeFunctor: Functor[Tree] =
      new Functor[Tree] {
        def map[A, B](tree: Tree[A])(f: A => B): Tree[B] =
          tree match {
            case Branch(left, right) =>
              Branch(map(left)(f), map(right)(f))
            case Leaf(value) =>
              Leaf(f(value))
          }
      }

    def branch[A](left: Tree[A], right: Tree[A]): Tree[A] =
      Branch(left, right)

    def leaf[A](value: A): Tree[A] =
      Leaf(value)
  }
}
