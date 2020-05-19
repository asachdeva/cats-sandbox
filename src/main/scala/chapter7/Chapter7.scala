package chapter7

object Chapter7 {

  object Foldable {
    def show[A](list: List[A]): String =
      list.foldLeft("nil")((accum, item) => s"$item then $accum")
  }
}
