package com.tutorial.keycloakbackend.model;

public class RoleDetailesDTO {

	private String id;
	private String roleName;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RoleDetailesDTO [id=");
		builder.append(id);
		builder.append(", roleName=");
		builder.append(roleName);
		builder.append("]");
		return builder.toString();
	}
	
}
