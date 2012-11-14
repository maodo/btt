import org.specs2.mutable._
import models._
import play.api.test._
import play.api.test.Helpers._

class TestTaskModel extends Specification {

  "Task model" should {

    "list for a week" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val tasks = Task.listForWeek

        tasks must not be empty
      }
    }
    
    "calculate total duration" in {
      running(FakeApplication()) {
       val totalDuration = Task.totalDurations
       totalDuration.intValue() must be_>(0)
      }
    }

    "create task" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val tasksSize = Task.listForWeek.size
        Task.create(Task(1, 3))
        tasksSize.toInt must be_<(Task.listForWeek.size.toInt)
      }
    }

    "stop a task" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        Task.stop(1)

        val tasks = Task.listForWeek
        val taskById1 = tasks.find(_.task.id.get == 1).get.task
        taskById1.duration must be_>(0)
        taskById1.failed must beFalse
      }
    }
    
    "fail a task" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        Task.fail(2)
        val tasks = Task.listForWeek
        val taskById2 = tasks.find(_.task.id.get == 2).get.task
        taskById2.duration must be_>(0)
        taskById2.failed must beTrue
      }
    }
    
    "cancel a task" in {
      running(FakeApplication()) {
        Task.cancel(3)

        val tasks = Task.listForWeek
        tasks.find(_.task.id.get == 3) must beNone
      }
    }

  }
}