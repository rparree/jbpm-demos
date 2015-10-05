package tasks

import java.lang.Long
import java.util

import demo.util._
import org.kie.api.runtime.KieSession
import org.kie.api.runtime.manager.RuntimeEngine
import org.kie.api.runtime.process.ProcessInstance
import org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED
import org.kie.api.task.TaskService
import org.kie.api.task.model.{User, Status, Task, TaskSummary}
import org.specs2.mutable.Specification

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.collection.mutable
import ProcessState._

class TaskSpec extends Specification with IgnoreZeroResourceTx with KieTestContext with ProcessMatchers with ProcessHelpers {

  sequential


  override val config = KBaseSession("tasksBase", "default")

  "tasks assigned to potential owners" >> { implicit rte: RuntimeEngine =>
    startProcess( scenario = "salesrep")

    tasksOf("jennifer").size should beEqualTo(1)
    tasksOf("john").size should beEqualTo(1)
    tasksOf("cindy").size should beEqualTo(0)
  }

  "work on task" >> { implicit rte: RuntimeEngine =>

    val user = "jennifer"

    val p = startProcess(x = 10, scenario = "salesrep")
    val id = p.getId
    val taskService = rte.getTaskService
    val jenniferInBox = tasksOf(user)

    jenniferInBox.size should beEqualTo(1)
    for (t <- jenniferInBox) {
      println(s"$user is working on ${t.getDescription}")
      val id = t.getId

      taskService.claim(id, user)
      taskService.start(id, user)
      val content: mutable.Map[String, AnyRef] = taskService.getTaskContent(id)
      val value = content.get("value").map(v => v.asInstanceOf[Int]).getOrElse(0)

      value should beEqualTo(10)
      val jennyWorking = value * 2

      taskService.complete(id, user, Map[String,AnyRef]("answer"->Int.box(jennyWorking)))

    }
    process(id) should be(COMPLETED)
  }

  "forward task to another group/user when assigned to (set of) potential owners" >> { implicit rte: RuntimeEngine =>

    val owner = "jennifer" // should be assigned to set of potential owners, not a group
    val forwarder = "jennifer" // (potential) owners or admin (seems to not be implemented correctly, 'narendra' works as well)
    val forwardTo = "cindy" // or group

    startProcess(x=10,scenario = "jennifer")

    val taskService = rte.getTaskService
    val ownerInBox = tasksOf(owner)

    ownerInBox.size should beEqualTo(1)

    for (t <- ownerInBox) {
      val taskId = t.getId
      taskStatus(taskId) should beEqualTo(Status.Reserved) // if 'Reserved'|'InProgress' will be implicitly released (data is kept)
      taskService.forward(taskId, forwarder, forwardTo)
      taskStatus(taskId) should beEqualTo(Status.Ready)
    }
    tasksOf(owner).size should beEqualTo(0)
    tasksOf(forwardTo).size should beEqualTo(1)
  }

  "delegate an active task " >> { implicit rte: RuntimeEngine =>


    val user = "jennifer"
    val delegator = "heisenberg" // (potential) owners or admin (heisenberg)
    val delegateTo = "cindy"
    val p = startProcess(x = 10, scenario = "salesrep")

    val taskService = rte.getTaskService
    val jenniferInBox = taskService.getTasksAssignedAsPotentialOwner(user, "en")

    jenniferInBox.size should beEqualTo(1)
    for (t <- jenniferInBox) {
      println(s"$user is working on ${t.getDescription}")
      val taskId = t.getId

      taskService.claim(taskId, user)
      taskService.start(taskId, user)
      ownerOf(taskId).getId must beEqualTo(user)
      taskStatus(taskId) should beEqualTo(Status.InProgress)

      taskService.delegate(taskId,delegator,delegateTo)

      taskStatus(taskId) should beEqualTo(Status.Reserved)
      ownerOf(taskId).getId must beEqualTo(delegateTo)




    }
    taskService.getTasksAssignedAsPotentialOwner(user, "en").size should beEqualTo(0)
    taskService.getTasksAssignedAsPotentialOwner(delegateTo, "en").size should beEqualTo(1)
  }

  "reassign after period" >> { implicit rte: RuntimeEngine =>
    val owner = "john"
    val anAccountMngr = "cindy"

    val p = startProcess(scenario = "lazyjohn")


    val johnInbox  = tasksOf(owner)
    johnInbox.size should beEqualTo(1)
    tasksOf(anAccountMngr).size should beEqualTo(0)

    val taskid = johnInbox.head.getId

    rte.getTaskService.start(taskid,owner)
    Thread.sleep(5000)

    tasksOf(anAccountMngr).size should beEqualTo(1)




  }

  // Some local helper methods
  private def startProcess(x: Int = 0, scenario: String)(implicit rte: RuntimeEngine): ProcessInstance = {
    val params: Map[String, AnyRef] = Map("x" -> Int.box(x), "scenario" -> scenario)
    val session = rte.getKieSession
    session.startProcess("tasks.SimpleHumanTaskProcess", params)
  }



}
