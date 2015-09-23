package demo

import java.util.Date

import model.Policy
import org.jbpm.test.JbpmJUnitBaseTestCase
import org.junit.{Before, Test}
import org.kie.api.io.ResourceType
import org.kie.api.runtime.KieSession
import org.kie.api.runtime.process.ProcessInstance

import scala.collection.JavaConversions._

class OutComeTest extends JbpmJUnitBaseTestCase(true,true)  {

  @Before
  def setupKie :Unit = createRuntimeManager(Map("policy/risk.bpmn2"->ResourceType.BPMN2,"policy/policy.drl"->ResourceType.DRL))

  @Test
  def highRiskTest() : Unit = {
    val session: KieSession = getRuntimeEngine.getKieSession

    val params : Map[String,AnyRef] = Map("request" -> Policy(600) )
    val pi: ProcessInstance = session.startProcess("policy.Risk", params)
    session.fireAllRules

    assertNodeTriggered(pi.getId,"High Risk")
    assertProcessInstanceAborted(pi.getId)
  }

  @Test
  def lowRiskTest() : Unit = {
    val session: KieSession = getRuntimeEngine.getKieSession

    val params : Map[String,AnyRef] = Map("request" -> Policy(100) )
    val pi: ProcessInstance = session.startProcess("policy.Risk", params)
    session.fireAllRules

    assertNodeTriggered(pi.getId,"End")
    assertProcessInstanceCompleted(pi.getId)
  }


}
