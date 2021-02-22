package com.tutorial.keycloakbackend.controller;

import java.util.List;

import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tutorial.keycloakbackend.dto.ResponseMessage;
import com.tutorial.keycloakbackend.model.User;
import com.tutorial.keycloakbackend.model.UserDetailesDTO;
import com.tutorial.keycloakbackend.service.KeycloakService;

@RestController
@RequestMapping("/user")
@CrossOrigin
/**
 * 
 * @author ONPASSIVE
 *
 */
public class UserController {

	@Autowired
	private KeycloakService keycloakService;

	@PostMapping("/create")
	public ResponseEntity<ResponseMessage> create(@RequestBody User user) {
		Object[] obj = keycloakService.createUser(user);
		int status = (int) obj[0];
		ResponseMessage message = (ResponseMessage) obj[1];
		return ResponseEntity.status(status).body(message);
	}

	@PostMapping("/update/{id}")
	public String updateUserData(@PathVariable String id, @RequestBody User user) {
		return keycloakService.updateUserData(id, user);
	}

	@GetMapping("/userdetail")
	public List<UserDetailesDTO> detail() {
		List<UserDetailesDTO> usersList = keycloakService.getUserDetailes();
		return usersList;
	}

	@GetMapping("/userdetailbyid/{id}")
	public UserDetailesDTO detailById(@PathVariable String id) {

		UserDetailesDTO users = keycloakService.getUserDetailesById(id);
		return users;
	}

	@DeleteMapping("/deleteuser/{id}")
	public String deleteById(@PathVariable String id) {
		UsersResource usersResource = keycloakService.deleteById(id);
		usersResource.count();
		return "user deleted successful..!";
	}

}
