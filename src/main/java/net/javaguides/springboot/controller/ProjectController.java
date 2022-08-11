package net.javaguides.springboot.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import net.javaguides.springboot.exception.ResourceNotFoundException;
import net.javaguides.springboot.model.Project;
import net.javaguides.springboot.repository.ProjectRepository;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/v1/")
public class ProjectController {
	
	@Autowired
	private ProjectRepository projectRepository;
	
	// get projects
	
	@GetMapping("projects")
	public List<Project> getAllProjects(){
		return this.projectRepository.findAll();
	}
	
	// get project by id
	@GetMapping("projects/{id}")
	public ResponseEntity<Project> getProjectById(@PathVariable(value = "id") long projectId) throws ResourceNotFoundException{
		Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found for this id : " + projectId));
		return ResponseEntity.ok().body(project);
	}
	// save project
	@PostMapping("projects")
	public Project createProject(@RequestBody Project project) {
		Project createProject =  this.projectRepository.save(project);
		Path htmlWebsite = Paths.get("./src/main/resources/static/" + "website" + createProject.getId() + ".html");
		List<String> list = List.of(project.getHtml());
		try {
			Files.write(htmlWebsite, list);
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		return createProject;
	}
	// update project
	@PutMapping("projects/{id}")
	public ResponseEntity<Project> updateProject(@PathVariable(value = "id") Long projectId, @Validated @RequestBody Project projectDetails) throws ResourceNotFoundException{
		Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found for this id : " + projectId));
		
		project.setTitle(projectDetails.getTitle());
		project.setCategory(projectDetails.getCategory());
		project.setHtml(projectDetails.getHtml());
		
		return ResponseEntity.ok(this.projectRepository.save(project));
	}
	
	// delete project
	@DeleteMapping("projects/{id}")
	public Map<String, Boolean> deleteProject(@PathVariable(value = "id") Long projectId) throws ResourceNotFoundException{
		Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found for this id : " + projectId));
		
		this.projectRepository.delete(project);
		
		Map<String, Boolean> response = new HashMap<>();
		response.put("deleted", Boolean.TRUE);
		
		return response;
	}
	
	@GetMapping("screenshot/{website}")
	public Map<String, Boolean> pageScreenShot(@PathVariable(value = "website") String website ) {
		
		
		try(Playwright playwright = Playwright.create()){
			BrowserType browserTypes = 
					playwright.chromium();
			Browser browser = browserTypes.launch();
				
				BrowserContext context = browser.newContext();
				Page page = context.newPage();
				page.navigate("https://pawelrosiek-springboot-app.herokuapp.com/" + website + ".html");
				page.setViewportSize(640, 640);
				Locator web = page.locator("#body");
				double x = web.boundingBox().x;
				double y = web.boundingBox().y;
				double height = web.boundingBox().height;
				double width = web.boundingBox().width;
				
				String image = "screenshoot11-" + browserTypes.name() + ".png";

				page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("./src/main/resources/static/images/" + image)).setClip(x, y, width, height).setFullPage(true));
				page.close();
		}
		
		Map<String, Boolean> response = new HashMap<>();
		response.put("Image", Boolean.TRUE);
		
		return response;
	}
	
}
