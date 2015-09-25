package tasks

import com.typesafe.scalalogging.LazyLogging
import org.jbpm.process.workitem.AbstractWorkItemHandler
import org.kie.api.runtime.process.{WorkItemHandler, WorkItem, WorkItemManager}
import org.kie.internal.runtime.StatefulKnowledgeSession

import scala.collection.JavaConversions._

/**
 * todo
 */
class HelloWorldWorkItemHandler extends WorkItemHandler with LazyLogging {



  override def abortWorkItem(workItem: WorkItem, manager: WorkItemManager) = {}

  override def executeWorkItem(workItem: WorkItem, manager: WorkItemManager) = {
    logger.debug("executing...")

    val message = Option(workItem.getParameter("message"))
    println(message.getOrElse("you did not supply a message"))
    manager.completeWorkItem(workItem.getId,Map[String,AnyRef]())
  }

}
