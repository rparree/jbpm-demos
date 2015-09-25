package signals

import demo.util._
import org.kie.api.runtime.KieSession
import org.kie.api.runtime.manager.RuntimeEngine
import org.kie.api.runtime.process.ProcessInstance
import org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED
import org.specs2.mutable.Specification



class ErrorsSpec extends Specification with KieTestContext  with ProcessMatchers   {

  override val config = KBaseSession("errorDemo", "mysession")

  "smoke test" >> { implicit rte: RuntimeEngine=>
    val pi = rte.getKieSession.startProcess("errors.ErrorsomeProcess")

    process(pi) must be(ProcessState.COMPLETED)

  }
}
