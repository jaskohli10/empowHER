package com.lti.service;

import java.util.List;

import com.lti.model.NgoDetails;
import com.lti.model.User;

public interface UserService {

	public boolean checkLoginUser(String username, String password);

	public boolean checkLoginAdmin(String username, String password);

	public boolean registerUser(User user);

	public boolean registerNgo(NgoDetails ngoDetails);

	public List<NgoDetails> findAllNgos();

	public NgoDetails findNgoById(Long ngoRegisterationId);

	boolean modifyNgo(Long ngoRegisterationId, String approvalStatus);

	void send(String email);
}