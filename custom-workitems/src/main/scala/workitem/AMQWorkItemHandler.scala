package workitem

import java.util
import javax.jms.{TextMessage, Session}

import com.typesafe.scalalogging.LazyLogging
import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.activemq.command.ActiveMQQueue
import org.apache.activemq.pool.PooledConnectionFactory
import org.jbpm.process.workitem.AbstractLogOrThrowWorkItemHandler
import org.kie.api.runtime.process.{WorkItemManager, WorkItem}
import org.kie.internal.runtime.{Cacheable, Closeable}
import resource._

import scala.collection.mutable
import scala.collection.JavaConversions._

/**
 * todo
 */
class AMQWorkItemHandler(username : String, password : String) extends AbstractLogOrThrowWorkItemHandler with Cacheable  with LazyLogging {
  override def abortWorkItem(workItem: WorkItem, manager: WorkItemManager): Unit = {}



  override def executeWorkItem(workItem: WorkItem, manager: WorkItemManager): Unit = {
    val brokerURL = Option(workItem.getParameter("brokerURL")).map(p => p.asInstanceOf[String]).getOrElse("vm:kiemqbroker")
    val queueName = Option(workItem.getParameter("queueName")).map(p => p.asInstanceOf[String]).get
    val payload = Option(workItem.getParameter("payload")).get

    logger.debug(s"using brokerUrl: $brokerURL")
    val cnf = getConnectionFactory(brokerURL)

    for (connection <- managed(cnf.createConnection(username,password))) {
      for (session <- managed(connection.createSession(false, Session.AUTO_ACKNOWLEDGE));
           producer <- managed(session.createProducer(new ActiveMQQueue(queueName)))) {

        val message = payload match {
          case t: String => session.createTextMessage(t)
          case s: Serializable => session.createObjectMessage(s)
        }

        producer.send(message)

        manager.completeWorkItem(workItem.getId,null)
      }
    }

  }

  val pooledCnfs: scala.collection.mutable.Map[String, PooledConnectionFactory] = mutable.Map.empty

  def getConnectionFactory(brokerURL: String): PooledConnectionFactory = {
    pooledCnfs.getOrElseUpdate(brokerURL, new PooledConnectionFactory(new ActiveMQConnectionFactory(brokerURL)))
  }
  override def close() = pooledCnfs.values.foreach(pcnf => pcnf.stop())

}
