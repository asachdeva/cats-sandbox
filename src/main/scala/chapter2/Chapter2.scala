package chapter2

object Chapter2 {

  trait Semigroup[A] {
    def combine(a: A, b: A): A
  }

  trait Monoid[A] extends Semigroup[A] {
    def empty: A
  }

  object Monoid {
    def apply[A](implicit monoid: Monoid[A]) = monoid
  }

  // Ex 2.3
  object BooleanMonoid {
    implicit val andMonoid: Monoid[Boolean] = new Monoid[Boolean] {
      def empty: Boolean = true
      def combine(a: Boolean, b: Boolean) = a && b
    }

    implicit val orMonoid: Monoid[Boolean] = new Monoid[Boolean] {
      def empty: Boolean = false
      def combine(a: Boolean, b: Boolean) = a || b
    }

    implicit val booleanEitherMonoid: Monoid[Boolean] = new Monoid[Boolean] {
      def empty: Boolean = false
      def combine(a: Boolean, b: Boolean) = (a && !b) || (!a && b)
    }

    implicit val booleanXnorMonoid: Monoid[Boolean] = new Monoid[Boolean] {
      def empty: Boolean = true
      def combine(a: Boolean, b: Boolean) = (!a || b) && (a || !b)
    }
  }

  // Ex 2.4 -- Note Set Intersection is a semigroup
  object SetMonoid {
    implicit def setUnionMonoid[A]: Monoid[Set[A]] =
      new Monoid[Set[A]] {
        def empty: Set[A] = Set.empty[A]
        def combine(set1: Set[A], set2: Set[A]): Set[A] = set1.union(set2)
      }

    implicit def symDiffMonoid[A]: Monoid[Set[A]] =
      new Monoid[Set[A]] {
        def empty: Set[A] = Set.empty
        def combine(set1: Set[A], set2: Set[A]): Set[A] = (set1.diff(set2)).union(set2.diff(set1))
      }
  }

  //  2.5.4
  object SuperAdder {
    def add(items: List[Int]): Int =
      items.foldLeft(0)(_ + _)

    import cats.syntax.all._

    def add2[A](items: List[A])(implicit monoid: cats.Monoid[A]): A =
      items.foldLeft(monoid.empty)(_ |+| _)

    def add3[A: cats.Monoid](items: List[A]): A =
      items.foldLeft(cats.Monoid[A].empty)(_ |+| _)

    case class Order(totalCost: Double, quantity: Double)

    implicit val orderMonoid =
      new cats.Monoid[Order] {
        def empty: Order = Order(0.0, 0.0)
        def combine(order1: Order, order2: Order) =
          Order(order1.totalCost + order2.totalCost, order1.quantity + order2.quantity)
      }
  }

  import cats.syntax.all._

  cats.Monoid[String].combine("Hi", "There")

  val a = Option(10)
  val b = Option(20)

  val c = cats.Monoid[Option[Int]].combine(a, b)

  val d = a |+| b
}
