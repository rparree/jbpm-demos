package tasks

import demo.util.IgnoreZeroResourceTx
import org.jbpm.test.JbpmJUnitBaseTestCase
import org.junit.{Before, Test}
import org.kie.api.io.ResourceType
import org.kie.api.runtime.KieSession
import org.kie.api.runtime.process.ProcessInstance

import scala.collection.JavaConversions._

class WorkItemTest extends JbpmJUnitBaseTestCase(true,true) with IgnoreZeroResourceTx  {

  @Before
  def setupKie :Unit = createRuntimeManager("workitem/ProcessWithCustomWorkItems.bpmn2")

  @Test
  def smokeTest() : Unit = {
    val session: KieSession = getRuntimeEngine.getKieSession
    session.getWorkItemManager.registerWorkItemHandler("Hello", new HelloWorldWorkItemHandler)
    session.getWorkItemManager.registerWorkItemHandler("ActiveMQSender", new AMQWorkItemHandler("admin","admin"))

    val params : Map[String,AnyRef] = Map( )
    val pi: ProcessInstance = session.startProcess("workitem.ProcessWithCustomWorkItems", params)

  }



}
