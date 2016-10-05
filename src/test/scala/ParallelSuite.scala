import com.pp.concurrent.Parallel
import org.scalameter.api._

import scala.util.Random
/**
  * Created by przemek on 28/09/16.
  */
class ParallelSuite extends Bench.LocalTime {

  val sizes = Gen.range("size")(300000, 1500000, 300000)

  def randomDouble(from: Double, to: Double) = from + Random.nextDouble() * (to - from)

  val lists = for {
    size <- sizes
  } yield List.fill(size)(randomDouble(0, 10))

  performance of "Parallel List" in {
    measure method "sumOfPowers" in {
      using(lists) in {
        l => Parallel.sumOfPowers(l.par)
      }
    }
  }

  performance of "List" in {
    measure method "sumOfPowers" in {
      using(lists) in {
        l => Parallel.sumOfPowers(l)
      }
    }
  }

  val vectors = for {
    size <- sizes
  } yield Vector.fill(size)(randomDouble(0, 10))

  performance of "Parallel Vector" in {
    measure method "sumOfPowers" in {
      using(vectors) in {
        l => Parallel.sumOfPowers(l.par)
      }
    }
  }

  performance of "Vector" in {
    measure method "sumOfPowers" in {
      using(vectors) in {
        l => Parallel.sumOfPowers(l)
      }
    }
  }

  val arrays = for {
    size <- sizes
  } yield Array.fill(size)(randomDouble(0, 10))

  performance of "Parallel Array" in {
    measure method "sumOfPowers" in {
      using(arrays) in {
        l => Parallel.sumOfPowers(l.par)
      }
    }
  }

  performance of "Array" in {
    measure method "sumOfPowers" in {
      using(arrays) in {
        l => Parallel.sumOfPowers(l)
      }
    }
  }

}
