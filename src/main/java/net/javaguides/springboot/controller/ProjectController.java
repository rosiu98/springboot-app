package net.javaguides.springboot.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.javaguides.springboot.exception.ResourceNotFoundException;
import net.javaguides.springboot.model.Project;
import net.javaguides.springboot.repository.ProjectRepository;

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
		return this.projectRepository.save(project);
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
}
