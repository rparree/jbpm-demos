package remoting

import java.lang.Long
import java.net.URL
import java.util
import javax.xml.bind.annotation.XmlRootElement

import com.typesafe.scalalogging.LazyLogging
import demo.util.ProcessHelpers
import org.kie.api.runtime.KieSession
import org.kie.api.runtime.manager.RuntimeEngine
import org.kie.api.runtime.process.ProcessInstance
import org.kie.api.task.model.TaskSummary
import org.kie.remote.client.api.RemoteRuntimeEngineFactory

import scala.beans.BeanProperty
import scala.collection.mutable
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

/**
  * todo
  */
object RemoteBPMNApp extends App with ProcessHelpers with LazyLogging {

  implicit def createURL(s: String) = new URL(s)

  /* Ensure mary belongs to  `rest-all` group */

  implicit val maryRte: RuntimeEngine = RemoteRuntimeEngineFactory.newRestBuilder()
    .addUrl("http://localhost:8080/jbpm-console").addUserName("mary").addPassword("mary")
    .addDeploymentId("org.jbpm:HR:1.0").addExtraJaxbClasses(classOf[Applicant]) build()

  val session: KieSession = maryRte.getKieSession

  val param: Map[String, AnyRef] = Map("name" -> "Jennifer Wirth")
  val process = session.startProcess("hiring", param)

  val taskSummaries = tasksOf("mary")

  val taskId = taskSummaries.head.getId

  private val option = maryRte.getTaskService.getTaskContent(taskId).asScala.get("in_name")
  println(option.get)
  maryRte.getTaskService.claim(taskId, "mary")
  maryRte.getTaskService.start(taskId, "mary")

  maryRte.getTaskService.complete(taskId, "mary", Map("out_score" -> Int.box(11)))


  /*admin=admin,analyst,kiemgmt
  krisv=admin,analyst
  john=analyst,Accounting,PM
  mary=analyst,HR
  sales-rep=analyst,sales
  jack=analyst,IT
  katy=analyst,HR
  salaboy=admin,analyst,IT,HR,Accounting*/


}

@XmlRootElement
case class Applicant(@BeanProperty var someName: String) {
  def this() = this("")
}
