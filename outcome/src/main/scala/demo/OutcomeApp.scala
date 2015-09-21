package demo

import java.util.Date


import demo.util.{KBaseSession, H2, SingletonRuntimeManager, DefaultClasspathRuntimeEnvironment}
import model.{ Policy, Driver}
import org.jbpm.workflow.instance.WorkflowProcessInstance
import org.kie.api.runtime.KieSession
import org.kie.api.runtime.manager._
import org.kie.api.runtime.process.ProcessInstance
import org.kie.internal.runtime.manager.context.EmptyContext
import scala.collection.JavaConverters._

object OutcomeApp extends App with DefaultClasspathRuntimeEnvironment with SingletonRuntimeManager with H2 {
  override val config : KBaseSession =  KBaseSession("policyBase","insurance")



  val runtimeEngine: RuntimeEngine = runtimeManager.getRuntimeEngine(EmptyContext.get)
  val session: KieSession = runtimeEngine.getKieSession
  val driver = Driver("John Doe", 46, "555125555", "C125678", 0, 0, 720)
  val policy = Policy(new Date, "Auto", 2010, 0, 0, driver, 600)
  val params : Map[String,AnyRef] = Map("request" ->policy )

  val pi: ProcessInstance = session.startProcess("policy.Risk", params.asJava)
  session.fireAllRules

  System.out.println(pi.getState == ProcessInstance.STATE_ABORTED)
  System.out.println(pi.asInstanceOf[WorkflowProcessInstance].getOutcome)


  runtimeManager.disposeRuntimeEngine(runtimeEngine)
  runtimeManager.close()
}


