package demo.util

/**
 * todo
 */
trait IgnoreZeroResourceTx {

  System.setProperty("bitronix.tm.2pc.warnAboutZeroResourceTransactions", "false")

}
