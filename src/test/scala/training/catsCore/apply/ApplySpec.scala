package training.catsCore.apply

import cats.{Functor, Apply}
import org.scalatest.{Matchers, WordSpec}
import training.catsCore.functor.{wrapperFunctor => _, _}

class ApplySpec extends WordSpec with Matchers {

  "Apply[?]" should {
    "correctly ap for List" in {
      import cats.std.list.listInstance
      val fs = List(1, 2, 3) map { x => x * 3 + (_: Int) } // == {(_: Int) * 3 + (_: Int)}.curried
      val vs = List(5, 10)
      Apply[List].ap(vs)(fs) shouldBe List(8, 13, 11, 16, 14, 19)
    }
    "correctly ap for Value" in {
      import training.catsCore.apply.wrapperApply
      val f: Wrapper[Int => String] = Value { x: Int => (x * 2 + 2).toString }
      val v = Value(6)
      Apply[Wrapper].ap(v)(f) shouldBe Value((6 * 2 + 2).toString)
    }
    "correctly ap for Value, using Functor mapping" in {
      import training.catsCore.apply.wrapperApply
      //f(x) = (x + 3) * 5
      val f: Wrapper[Int => Int] = Functor[Wrapper].map(Value[Int](5)) { x => ((_: Int) + 3) andThen (_ * x) }
      val v = Value(6)
      Apply[Wrapper].ap(v)(f) shouldBe Value((6 + 3) * 5)
      Apply[Wrapper].ap(NoValue)(f) shouldBe NoValue
      Apply[Wrapper].ap(Value(0))(f) shouldBe Value(3 * 5)
    }
    "correctly ap for NoValue as a F[A => B]" in {
      import training.catsCore.apply.wrapperApply
      val f = NoValue
      val v = Value(6)
      Apply[Wrapper].ap(v)(f) shouldBe NoValue
    }
    "correctly ap for NoValue as a F[A]" in {
      import training.catsCore.apply.wrapperApply
      val f = Value {
        (_: Int) * 2 + 2
      }
      val v = NoValue
      Apply[Wrapper].ap(v)(f) shouldBe NoValue
    }
  }
}
