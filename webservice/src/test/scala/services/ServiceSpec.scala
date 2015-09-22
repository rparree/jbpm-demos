package services

import java.io.File
import java.nio.file.{Paths, FileSystems, Files}
import java.util

import demo.cxf.DemoRequest
import demo.util._
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.IOFileFilter
import org.jbpm.process.workitem.webservice.WebServiceWorkItemHandler
import org.kie.api.runtime.KieSession
import org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeEach

import scala.collection.Iterable
import scala.collection.JavaConversions._
import scala.io.BufferedSource


class ServiceSpec extends Specification with KieTestContext with BeforeEach {

  override val config = KBaseSession("serviceDemo", "mysession")


  "web service smoke test" >> { s: KieSession =>
    s.getWorkItemManager.registerWorkItemHandler("WSTask", new WebServiceWorkItemHandler(s))
    val params: Map[String, AnyRef] = Map("r" -> new DemoRequest("jenny","jenny@work.com"))
    val serviceProcess = s.startProcess("defaultprocessid",params )
    Thread.sleep(500)
    val files  : Iterable[File] = FileUtils.listFiles(Paths.get("/tmp/camel/out").toFile,null,false)
    val txt  = scala.io.Source.fromFile(files.head).mkString
    txt must beEqualTo("""{"name":"jenny","email":"jenny@work.com"}""")


    ok("smoke")
  }


  override protected def before = FileUtils.deleteDirectory(Paths.get("/tmp/camel/out").toFile)
}
