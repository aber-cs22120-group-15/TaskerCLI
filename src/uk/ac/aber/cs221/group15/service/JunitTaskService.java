package uk.ac.aber.cs221.group15.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javafx.embed.swing.JFXPanel;

import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.Before;

import uk.ac.aber.cs221.group15.TaskSync;
import uk.ac.aber.cs221.group15.TaskerCLI;
import uk.ac.aber.cs221.group15.task.Step;
import uk.ac.aber.cs221.group15.task.Task;

public class JunitTaskService {
	
	private LoginService service;
	private int id = 0;
	private String comment = "Testing comment now junit test";	
	private int newStatus = Task.COMPLETED;
	
	@Before
	public void setUp() throws Exception {
		service = new LoginService();
	}

	@Test
	public void updateTaskStepCommentTest() throws Exception {
		
		String token = service.login("sis22@aber.ac.uk", "scott");
		TaskService updateTaskStepCommentTest = new TaskService();
		TaskSync sync = new TaskSync(token);
		
		new JFXPanel();
		sync.forceSync();
		
		while(sync.getTasks().size()==0){
				Thread.sleep(50);
		}
		
		List<Task> tasks = sync.getTasks();
		Task t = tasks.get(id);
		Iterator<Step> it = t.getSteps().iterator();
		
		//it.next();
		
		Step s = it.next();
		
		System.out.println("Changing comment for task id: "+t.getId());
	
		updateTaskStepCommentTest.updateTaskStepComment(token, s.getId(), comment);
		
		Thread.sleep(500);
		
		sync.getTasks().clear();
		sync.forceSync();
		
		while(sync.getTasks().size()==0){
				Thread.sleep(50);
		}
		
		
		tasks = sync.getTasks();
		t = tasks.get(id);
		s = t.getSteps().iterator().next();
		
		while(sync.getTasks().size()==0){
			Thread.sleep(50);
		}
		
		
		System.out.println(s.getComment());
		
		assertEquals(comment, s.getComment());
		
	}
	
	
	
	
	@Test
	public void updateTaskStatusTest() throws Exception {
		
		String token = service.login("sis22@aber.ac.uk", "scott");
		TaskService updateTaskStatusTest = new TaskService();
		TaskSync sync = new TaskSync(token);
		
		new JFXPanel();
		sync.forceSync();
		
		while(sync.getTasks().size()==0){
				Thread.sleep(50);
		}
		
		List<Task> tasks = sync.getTasks();
		Task t = tasks.get(id);

		System.out.println("Changing status for task id: "+ t.getId());
		System.out.println("current status is : " + t.getStatus());
		
		t.setStatus(newStatus);
		t.setDateCompleted(Calendar.getInstance());
		
		System.out.println("Changing status to: " + newStatus);
		
		//updateTaskStatusTest.updateTaskStatus(token, id, newStatus, 0);
		//updateTaskStatus(String token, Task task);
		updateTaskStatusTest.updateTaskStatus(token, t);
		
		Thread.sleep(500);
		
		sync.getTasks().clear();
		sync.forceSync();
		
		while(sync.getTasks().size()==0){
				Thread.sleep(50);
		}
		
		tasks = sync.getTasks();
		t = tasks.get(id);
		
		System.out.println("new status is : " + t.getStatus());
		System.out.println(t.getStatus());
		System.out.println(newStatus);
		
		assertEquals(newStatus, t.getStatus());
	}
}