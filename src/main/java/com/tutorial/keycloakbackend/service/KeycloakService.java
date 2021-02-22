package com.tutorial.keycloakbackend.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tutorial.keycloakbackend.dto.ResponseMessage;
import com.tutorial.keycloakbackend.model.CustomRoleModel;
import com.tutorial.keycloakbackend.model.RoleDetailesDTO;
import com.tutorial.keycloakbackend.model.User;
import com.tutorial.keycloakbackend.model.UserDetailesDTO;

@Service
public class KeycloakService {

	@Value("${keycloak.auth-server-url}")
	private String server_url;

	@Value("${keycloak.realm}")
	private String realm;

	public Object[] createUser(User user) {
		ResponseMessage message = new ResponseMessage();
		int statusId = 0;
		try {
			UsersResource usersResource = getUsersResource();
			UserRepresentation userRepresentation = new UserRepresentation();
			userRepresentation.setUsername(user.getUsername());
			userRepresentation.setEmail(user.getEmail());
			userRepresentation.setFirstName(user.getFirstName());
			userRepresentation.setLastName(user.getLastName());
			userRepresentation.setEnabled(true);

			Response result = usersResource.create(userRepresentation);
			statusId = result.getStatus();

			if (statusId == 201) {
				String path = result.getLocation().getPath();
				String userId = path.substring(path.lastIndexOf("/") + 1);
				CredentialRepresentation passwordCredential = new CredentialRepresentation();
				passwordCredential.setTemporary(false);
				passwordCredential.setType(CredentialRepresentation.PASSWORD);
				passwordCredential.setValue(user.getPassword());
				usersResource.get(userId).resetPassword(passwordCredential);

				RealmResource realmResource = getRealmResource();
				RoleRepresentation roleRepresentation = realmResource.roles().get("realm-user").toRepresentation();
				realmResource.users().get(userId).roles().realmLevel().add(Arrays.asList(roleRepresentation));
				message.setMessage("user created successfully");
			} else if (statusId == 409) {
				message.setMessage("that user already exists");
			} else {
				message.setMessage("error creating user");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new Object[] { statusId, message };
	}

	private RealmResource getRealmResource() {
		Keycloak kc = KeycloakBuilder.builder().serverUrl(server_url).realm("master").username("admin")
				.password("admin").clientId("admin-cli")
				.resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()).build();
		return kc.realm(realm);
	}

	private UsersResource getUsersResource() {
		RealmResource realmResource = getRealmResource();
		return realmResource.users();
	}

	public String createRole(CustomRoleModel role) {
		RoleRepresentation roleRepresentation = new RoleRepresentation();
		roleRepresentation.setName(role.getRoleName());
		roleRepresentation.setClientRole(true);
		roleRepresentation.setDescription("Dummy Text");
		RealmResource realmResource = getRealmResource();
		realmResource.roles().create(roleRepresentation);
		return "role created";
	}

	public List<UserDetailesDTO> getUserDetailes() {
		UsersResource usersResource1 = getUsersResource();
		UserDetailesDTO userDTO = null;
		List<UserDetailesDTO> usersList = new ArrayList<>();
		System.out.println(" usersResource1.list() " + usersResource1.list().size());
		List<UserRepresentation> list = usersResource1.list();
		for (UserRepresentation user : list) {
			userDTO = new UserDetailesDTO();
			userDTO.setId(user.getId());
			userDTO.setName(user.getUsername());
			userDTO.setEmail(user.getEmail());
			usersList.add(userDTO);
		}
		return usersList;
	}

	public UserDetailesDTO getUserDetailesById(String id) {

		UserResource userResource = getUsersResource().get(id);
		System.out.println(userResource.toRepresentation().getEmail());
		UserDetailesDTO userDetailes = new UserDetailesDTO();
		userDetailes.setId(id);
		userDetailes.setEmail(userResource.toRepresentation().getEmail());
		userDetailes.setName(userResource.toRepresentation().getUsername());
		return userDetailes;
	}

	public UsersResource deleteById(String id) {
		UsersResource usersResource = getUsersResource();
		usersResource.get(id).remove();
		return usersResource;
	}

	public String updateUserData(String id, User user) {
		UsersResource usersResource = getUsersResource();
		System.out.println("user ::::" + user.getUsername());
		UserRepresentation userRep = usersResource.get(id).toRepresentation();
		userRep.setEmail(user.getEmail());
		userRep.setUsername(user.getUsername());
		userRep.setEnabled(true);
		userRep.setFirstName(user.getFirstName());
		userRep.setLastName(user.getLastName());
		usersResource.get(id).update(userRep);
		return "user updated successfully..!";
	}

	public List<RoleDetailesDTO> getRolesDetailes() {

		RealmResource realmResource = getRealmResource();
		RoleDetailesDTO roleDTO = null;
		List<RoleDetailesDTO> roleList = new ArrayList<>();
		System.out.println(" usersResource1.list() " + realmResource.roles().list());
		System.out.println("usersResource1.list().get(0).getClientRoles() ::" + realmResource.roles().list());
		List<RoleRepresentation> roles = realmResource.roles().list();
		System.out.println("list :::" + roles.toString());
		for (RoleRepresentation role : roles) {
			roleDTO = new RoleDetailesDTO();
			roleDTO.setId(role.getId());
			roleDTO.setRoleName(role.getName());
			roleList.add(roleDTO);
		}
		return roleList;
	}

	public RoleDetailesDTO getRoleDetailesById(String id) {
		RoleRepresentation userResource = getRealmResource().rolesById().getRole(id);
		System.out.println(userResource.getName());
		RoleDetailesDTO userDetailes = new RoleDetailesDTO();
		userDetailes.setId(userResource.getId());
		userDetailes.setRoleName(userResource.getName());
		return userDetailes;
	}

	public String deleteRoleById(String name) {
		RealmResource realmResource = getRealmResource();
		realmResource.roles().deleteRole(name);
		return "Role Deleted successfully..!";
	}

	public String updateRole(String roleName, CustomRoleModel role) {
		RealmResource roleUpdate = getRealmResource();
		RolesResource rolesResource = roleUpdate.roles();
		RoleRepresentation userResource = new RoleRepresentation();
		userResource.setName(role.getRoleName());
		rolesResource.get(roleName).update(userResource);
		return "Role updated successfully...!";
	}
}
