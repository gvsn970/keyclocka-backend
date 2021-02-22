package com.tutorial.keycloakbackend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tutorial.keycloakbackend.model.CustomRoleModel;
import com.tutorial.keycloakbackend.model.RoleDetailesDTO;
import com.tutorial.keycloakbackend.service.KeycloakService;

@RestController
@RequestMapping("/role")
@CrossOrigin
public class RoleController {

	@Autowired
	private KeycloakService keycloakService;

	@PostMapping("/createrole")
	public String createRole(@RequestBody CustomRoleModel role) {

		return keycloakService.createRole(role);
	}

	@GetMapping("/roledetail")
	public List<RoleDetailesDTO> roleDetail() {
		List<RoleDetailesDTO> usersList = keycloakService.getRolesDetailes();
		return usersList;
	}

	@GetMapping("/roledetailbyid/{id}")
	public RoleDetailesDTO detailById(@PathVariable String id) {
		RoleDetailesDTO users = keycloakService.getRoleDetailesById(id);
		return users;
	}

	@DeleteMapping("/deleterole/{name}")
	public String deleteRoleById(@PathVariable String name) {
		return keycloakService.deleteRoleById(name);
	}

	@PostMapping("/update/{roleName}")
	public String updateRole(@PathVariable("roleName") String roleName, @RequestBody CustomRoleModel role) {
		return keycloakService.updateRole(roleName, role);
	}
}
