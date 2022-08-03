package com.checkconcepts.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.checkconcepts.StorageProperties;
import com.checkconcepts.persistence.model.Post;
import com.checkconcepts.persistence.model.PostsAttachments;
import com.checkconcepts.web.error.StorageException;
import com.checkconcepts.web.error.StorageFileNotFoundException;

@Service
@Transactional
public class FileSystemStorageService implements StorageService {

	private final Path rootLocation;

	@Autowired
	private PostService postService;
	@Autowired
	private PostsAttachmentsService postsAttachmentsService;

	@Value("${spring.profiles.active}")
	private String activeProfile;

	@Autowired
	public FileSystemStorageService(StorageProperties properties) {
		this.rootLocation = Paths.get(properties.getLocation());
	}

	@Override
	public void storePostFile(MultipartFile file, Long postId, HttpServletRequest request) {
		String[] fileTypes = new String[] { "jpg", "jpeg", "png", "JPG", "JPEG", "PNG" };
		List<String> fileTypeList = new ArrayList<>(Arrays.asList(fileTypes));
		String fileStoreName = System.currentTimeMillis() + "_" +file.getOriginalFilename();
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file.");
			}
			Path destinationFile = this.rootLocation.resolve(Paths.get(fileStoreName)).normalize()
					.toAbsolutePath();
			if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
				// This is a security check
				throw new StorageException("Cannot store file outside current directory.");
			}

			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);

				Post parentPost = postService.findPostById(postId).get();
				PostsAttachments att = new PostsAttachments();
				if (fileTypeList.contains(com.google.common.io.Files.getFileExtension(file.getOriginalFilename())))
					att.setAttachmentSrc(getAppUrl(request) + "/image/display/" + fileStoreName);
				else
					att.setAttachmentSrc(getAppUrl(request) + "/pdf/display/" + fileStoreName);

				att.setAttachmentType(com.google.common.io.Files.getFileExtension(file.getOriginalFilename()));
				att.setName(fileStoreName);
				att.setParentPostAttachment(parentPost);
				postsAttachmentsService.save(att);
			} catch (ConstraintViolationException e) {
				throw new StorageException("Failed to store file. Given file name is already exists", e);
			}
		} catch (IOException e) {
			throw new StorageException("Failed to store file.", e);
		} catch (Exception e) {
			throw new StorageException("Something went wrong", e);
		}
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation, 1).filter(path -> !path.equals(this.rootLocation))
					.map(this.rootLocation::relativize);
		} catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}

	}

	@Override
	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}

	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new StorageFileNotFoundException("Could not read file: " + filename);

			}
		} catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}

	@Override
	@PostConstruct
	public void init() {
		try {
			Files.createDirectories(rootLocation);
		} catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
	}

	private String getAppUrl(HttpServletRequest request) {
		if (activeProfile.equalsIgnoreCase("dev"))
			return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
		if (activeProfile.equalsIgnoreCase("prod"))
			return "https://www.checkconcepts.org" + request.getContextPath();
		return null;
	}

	@Override
	public void deleteFile(String filename) {
		// TODO Auto-generated method stub
		try {
			Files.delete(this.rootLocation.resolve(Paths.get(filename)).normalize().toAbsolutePath());
		} catch (IOException e) {
			throw new StorageException("Could not delete file", e);
		}
	}
}
