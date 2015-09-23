package compensation

import demo.util.IgnoreZeroResourceTx
import org.jbpm.bpmn2.handler.ServiceTaskHandler
import org.jbpm.test.JbpmJUnitBaseTestCase
import org.junit.{Test, Before}
import scala.collection.JavaConversions._
/**
 * todo
 */
class CompensationTest  extends JbpmJUnitBaseTestCase(true,true) with IgnoreZeroResourceTx {

  @Before
  def setupKie :Unit = createRuntimeManager("compensation/CompensatingProcess.bpmn2")


  @Test
  def smokeTestEdu : Unit = {

    val session = getRuntimeEngine.getKieSession
    session.getWorkItemManager.registerWorkItemHandler("Service Task", new ServiceTaskHandler())
    val params : Map[String,AnyRef] = Map("booking" -> BookingRequest("us@home.edu"))
    session.startProcess("Compensation.CompensatingProcess", params)

  }

  @Test
  def smokeTestOrg : Unit = {

    val session = getRuntimeEngine.getKieSession
    session.getWorkItemManager.registerWorkItemHandler("Service Task", new ServiceTaskHandler())
    val params : Map[String,AnyRef] = Map("booking" -> BookingRequest("them@home.org"))
    session.startProcess("Compensation.CompensatingProcess", params)

  }

}
