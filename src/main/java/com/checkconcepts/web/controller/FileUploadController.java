package com.checkconcepts.web.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.checkconcepts.persistence.model.Post;
import com.checkconcepts.persistence.model.PostsAttachments;
import com.checkconcepts.service.PostService;
import com.checkconcepts.service.StorageService;
import com.checkconcepts.web.error.StorageException;
import com.checkconcepts.web.error.StorageFileNotFoundException;

@Controller
public class FileUploadController {

	private final StorageService storageService;

	@Autowired
	public FileUploadController(StorageService storageService) {
		this.storageService = storageService;
	}

	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}

	@GetMapping("/image/{imageName}")
	@ResponseBody
	public byte[] getImage(@PathVariable(value = "imageName") String imageName) throws IOException {

		Resource file = storageService.loadAsResource(imageName + ".PNG");
		File serverFile = file.getFile();
		return Files.readAllBytes(serverFile.toPath());
	}

	@GetMapping(value = "/image/display/{imageName}", produces = MediaType.IMAGE_PNG_VALUE)
	@ResponseBody
	public byte[] getDisplayImage(@PathVariable(value = "imageName") String imageName) throws IOException {

		Resource file = storageService.loadAsResource(imageName);
		File serverFile = file.getFile();
		return Files.readAllBytes(serverFile.toPath());
	}

	@GetMapping(value = "/pdf/display/{imageName}", produces = MediaType.APPLICATION_PDF_VALUE)
	@ResponseBody
	public byte[] getDisplayPdf(@PathVariable(value = "imageName") String imageName) throws IOException {

		Resource file = storageService.loadAsResource(imageName);
		File serverFile = file.getFile();
		return Files.readAllBytes(serverFile.toPath());
	}

	@PostMapping("/staff/post/fileupload/{id}")
	public String handleStaffPostFileUpload(@PathVariable("id") long id, @RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes, HttpServletRequest request) {

		try {
			storageService.storePostFile(file, id, request);

			redirectAttributes.addFlashAttribute("message",
					"You successfully uploaded " + file.getOriginalFilename() + "!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "File upload failed " + file.getOriginalFilename() + "!");
			return "redirect:/staff/post/edit/" + id;
		}

		return "redirect:/staff/post/edit/" + id;
	}

	@PostMapping("/admin/fileupload")
	public String handleAdminFileUpload(@PathVariable("id") long id, @RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes, HttpServletRequest request) {

		storageService.storePostFile(file, id, request);
		redirectAttributes.addFlashAttribute("message",
				"You successfully uploaded " + file.getOriginalFilename() + "!");

		return "redirect:/staff/postsCrud";
	}

	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}
}
