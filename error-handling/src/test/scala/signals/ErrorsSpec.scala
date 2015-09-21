package signals

import demo.util._
import org.kie.api.runtime.KieSession
import org.kie.api.runtime.process.ProcessInstance
import org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED
import org.specs2.mutable.Specification



class ErrorsSpec extends Specification with KieTestContext {

  override val config = KBaseSession("errorDemo", "mysession")

  "samle 1" >> { s: KieSession =>
    val pi = s.startProcess("errors.ErrorsomeProcess")
    pi.getState  must beEqualTo(STATE_COMPLETED)
  }
}
