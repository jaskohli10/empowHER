package com.lti.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.lti.model.NgoDetails;
import com.lti.model.TrainingDetails;
import com.lti.model.TrainingType;
import com.lti.model.User;
import com.lti.service.UserService;

@Controller
public class NgoController {

	private boolean result = false;
	@Autowired
	private User user;
	@Autowired
	private UserService service;
	@Autowired
	private TrainingType trainingType;
	@Autowired
	private NgoDetails ngoDetails;

	HttpSession session;

	@RequestMapping(path = "ngoRegistrationPage")
	public String ngoRegistrationPage() {
		return "ngoregistration";
	}

	@RequestMapping(path = "ngoadd.do", method = RequestMethod.POST)
	public String addNgo(NgoDetails ngoDetails, @RequestParam("username") String username,
			@RequestParam("name") String[] names, @RequestParam("file") MultipartFile[] files) {
		user.setUserName(username);
		if (files.length != names.length)
			return "Mandatory information missing";
		for (int i = 0; i < files.length; i++) {
			MultipartFile file = files[i];
			String name = names[i];
			try {
				byte[] bytes = file.getBytes();

				// Creating the directory to store file
				String rootPath = "D:/";
				File dir = new File(rootPath + File.separator + "FILESUPLOADED");
				if (!dir.exists())
					dir.mkdirs();

				// Create the file on server
				File serverFile = new File(dir.getAbsolutePath() + File.separator + name + ".pdf");
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();
				ngoDetails.setMemorandumOfAssociation(names[0]);
				ngoDetails.setSocietiesRegistrationAffidavit(names[1]);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		ngoDetails.setApprovalStatus("pending");
		ngoDetails.setUser(user);
		service.registerNgo(ngoDetails);
		return "homepage";
	}

	@RequestMapping(path = "ngoTrainingRegistrationPage")
	public String ngoTrainingRegistrationPage(HttpServletRequest request) {
		session = request.getSession(false);
		String username = (String) session.getAttribute("username");
		long ngoRegisterationId = service.findNgoRegistrationIdByUsername(username);

		String organizationName = service.findNgoNameByUsername(username);
		session.setAttribute("ngoRegisterationId", ngoRegisterationId);
		session.setAttribute("organizationName", organizationName);
		return "ngotrainingregistration";
	}

	@RequestMapping(path = "ngoTrainingAdd.do", method = RequestMethod.POST)
	public String ngoTrainingAdd(TrainingDetails trainingDetails,
			@RequestParam("trainingcategoryID") String trainingcategoryID,
			@RequestParam("ngoRegisterationId") long ngoRegisterationId) {

		trainingType.setTrainingTypeId(trainingcategoryID);
		trainingDetails.setTrainingType(trainingType);

		ngoDetails.setNgoRegisterationId(ngoRegisterationId);
		trainingDetails.setNgoDetails(ngoDetails);

		result = service.registerTrainingByNgo(trainingDetails);
		return (result) ? "userhome" : "errorpage";
	}

	@RequestMapping(path = "ngoHostelRegistrationPage")
	public String ngoHostelRegistrationPage() {
		return "ngohostelregistration";
	}

}
