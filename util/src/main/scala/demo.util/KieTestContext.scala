package demo.util

import com.typesafe.scalalogging.LazyLogging

import org.drools.core.spi.ProcessContext
import org.jbpm.bpmn2.handler.ServiceTaskHandler
import org.kie.api.runtime.manager.audit.ProcessInstanceLog
import org.kie.api.runtime.manager.audit.AuditService
import org.kie.api.runtime.process.ProcessInstance
import org.kie.api.runtime.{KieRuntime, KieSession}
import org.kie.api.runtime.manager.{RuntimeManager, RuntimeManagerFactory, RuntimeEngine}
import org.kie.api.task.TaskService
import org.kie.api.task.model.{Status, User, TaskSummary}
import org.kie.internal.runtime.manager.context.EmptyContext
import org.specs2.execute.{Result, AsResult}
import org.specs2.matcher.{Expectable, Matcher}

import org.specs2.specification.ForEach
import scala.collection.JavaConversions._
import scala.util.Try
import scala.util.control.Exception._

trait KieTestContext extends ForEach[RuntimeEngine] with DefaultClasspathRuntimeEnvironment with H2 with LazyLogging {

  val silent = ignoring(classOf[Exception])

  def foreach[R: AsResult](f: RuntimeEngine => R): Result = {
    val runtimeManager: RuntimeManager = RuntimeManagerFactory.Factory.get.newSingletonRuntimeManager(environment)
    val runtimeEngine: RuntimeEngine = runtimeManager.getRuntimeEngine(EmptyContext.get)
    val session: KieSession = runtimeEngine.getKieSession

    session.getWorkItemManager.registerWorkItemHandler("Service Task", new ServiceTaskHandler())


    silent(deleteProcesses(runtimeEngine))
    try AsResult(f(runtimeEngine))
    finally {
      Try(deleteProcesses(runtimeEngine))

      runtimeManager.disposeRuntimeEngine(runtimeEngine)
      runtimeManager.close()
    }
  }


  def deleteProcesses(runtimeEngine: RuntimeEngine): Unit = {
    val auditService: AuditService = runtimeEngine.getAuditService
    val session: KieSession = runtimeEngine.getKieSession
    auditService.findProcessInstances()
      .filter(l => l.getStatus == ProcessInstance.STATE_ACTIVE)
      .map(l => l.getProcessInstanceId)
      .foreach(id => {
        logger.info(s"Aborting process instance of $id ")
        silent(session.abortProcessInstance(id))
      })
  }


}

trait ProcessHelpers{
  def tasksOf(userId : String, locale : String = "en")(implicit  rte : RuntimeEngine): Seq[TaskSummary] = {
    rte.getTaskService.getTasksAssignedAsPotentialOwner(userId, locale)
  }

  def taskStatus(taskId: Long)(implicit rte : RuntimeEngine) : Status = rte.getTaskService.getTaskById(taskId).getTaskData.getStatus

  def ownerOf(taskId: Long)(implicit rte: RuntimeEngine): User = rte.getTaskService.getTaskById(taskId).getTaskData.getActualOwner
}

trait ProcessMatchers {

  import org.specs2.matcher.MatchersImplicits._
  import ProcessState._

  type PI = Either[ProcessInstance, ProcessInstanceLog]

  //noinspection MatchToPartialFunction (needed for implicit)
  def be(s: State): Matcher[Option[PI]] = (pi_pil: Option[PI]) => {
    pi_pil match {
      case Some(x) => x match {
        case Left(pi) => (pi.getState == s.id, s"Process ${pi.getProcessId}:${s.id} is not $s, but is ${ProcessState(pi.getState)}")
        case Right(pil) => (pil.getStatus == s.id, s"Process ${pil.getProcessId}:${s.id} is not $s, but is ${ProcessState(pil.getStatus)}")
      }
      case None => (false, "There is/was no such process")


    }


  }

  def process(processInstance: ProcessInstance)(implicit rte: RuntimeEngine) : Option[PI]  = process(processInstance.getId)

  def process(id: Long)(implicit rte: RuntimeEngine): Option[PI] =
    Option(rte.getKieSession.getProcessInstance(id)) match {
      case Some(pi) => Some(Left(pi))
      case None => Option(rte.getAuditService.findProcessInstance(id)) match {
        case Some(pil) => Some(Right(pil))
        case None => None
      }
    }
}

object ProcessState extends Enumeration {
  type State = Value
  val PENDING, ACTIVE, COMPLETED, ABORTED, SUSPENDED, NON_EXISTENT = Value

}
