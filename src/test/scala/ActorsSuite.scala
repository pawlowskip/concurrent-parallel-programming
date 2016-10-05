import org.scalameter.api._
import com.pp.concurrent.Actors
import org.scalameter.{Bench, Gen, Measurer}

import scala.util.Random
import org.scalameter.picklers.noPickler._
/**
  * Created by przemek on 05/10/16.
  */
class ActorsSuite extends Bench[Double] {

  val measurer = new Measurer.IgnoringGC

  override type SameType = this.type

  override def executor: Executor[Double] = LocalExecutor(
    new Executor.Warmer.Default,
    Aggregator.min[Double],
    measurer)

  override def reporter: Reporter[Double] = new LoggingReporter[Double]

  override def persistor: Persistor = Persistor.None



  val sizes = Gen.range("size")(300000, 1500000, 300000)
  val calc = Gen.single("calc"){Actors.initializeCalc(4)}

  def randomDouble(from: Double, to: Double) = from + Random.nextDouble() * (to - from)

  val vectors = for {
    size <- sizes
  } yield Vector.fill(size)(randomDouble(0, 10))


  performance of "sum of powers using Actors" in {
    measure method "sumOfPowers"  in {
      using(Gen.crossProduct(vectors, calc)) tearDown {
        case (_, act) => Actors.shutdown(act)
      } in {
        case (l, act) => Actors.sumOfPowers(act, l, 4)
      }
    }
  }

}
