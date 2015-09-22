package signals

import java.util

import demo.util._
import org.kie.api.runtime.KieSession
import org.kie.api.runtime.process.ProcessInstance
import org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED
import org.specs2.mutable.Specification
import scala.collection.JavaConversions._
import scala.concurrent.duration._
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

class SignalSpec extends Specification with KieTestContext {

  sequential

  override val config = KBaseSession("signalDemo", "mysession")

  "foo smoke test" >> { s: KieSession =>
    val params: Map[String, AnyRef] = Map("itemType" -> "foo")
    val noParams: Map[String, AnyRef] = Map()
    val fooProcess = s.startProcess("signals.FooProcess",noParams )
    val mainProcess = s.startProcess("signals.MainSignallingProcess", params)
    ok("smoke")


  }

  "foo timeout" >> { s: KieSession =>
    val noParams: Map[String, AnyRef] = Map()
    val fooProcess = s.startProcess("signals.FooProcess",noParams )
    ok("smoke")


  }

  "non foo smoke test" >> { s: KieSession =>
    val params: Map[String, AnyRef] = Map("itemType" -> "bar")
    val pi = s.startProcess("signals.MainSignallingProcess", params)
    pi.getState  must beEqualTo(STATE_COMPLETED)
    ok("smoke")
  }
}
