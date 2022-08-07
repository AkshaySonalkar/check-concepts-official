package com.checkconcepts.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.checkconcepts.persistence.model.Post;
import com.checkconcepts.persistence.model.PostsAttachments;
import com.checkconcepts.web.error.StorageException;

@Service
@Transactional
public class AWSStorageService {

	@Value("${cloud.aws.application.bucket.name}")
	private String bucketName;

    @Autowired
    private AmazonS3 s3Client;
    
    @Autowired
	private PostService postService;
    
	@Autowired
	private PostsAttachmentsService postsAttachmentsService;

    public String uploadFile(MultipartFile file, Long postId) {
        File fileObj = convertMultiPartFileToFile(file);
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj).withCannedAcl(CannedAccessControlList.PublicRead));
        fileObj.delete();
        Post parentPost = postService.findPostById(postId).get();
		PostsAttachments att = new PostsAttachments();
		att.setAttachmentSrc(returnDocAwsS3Url(fileName));
		att.setAttachmentType(com.google.common.io.Files.getFileExtension(file.getOriginalFilename()));
		att.setName(fileName);
		att.setParentPostAttachment(parentPost);
		postsAttachmentsService.save(att);
        return "File uploaded : " + fileName;
    }


    public byte[] downloadFile(String fileName) {
    	
        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public byte[] getByteArrayFromImageS3Bucket(String fileName) throws IOException {
        InputStream in = getImageFromS3Bucket(fileName).getObjectContent();

        BufferedImage imageFromAWS = ImageIO.read(in);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(imageFromAWS, "jpg", baos );
        byte[] imageBytes = baos.toByteArray();
        in.close();
        return imageBytes;

    }
    
    public S3Object getImageFromS3Bucket(String fileName) {
        S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, fileName));
        return object;
    }

    public String deleteFile(String fileName) {
        s3Client.deleteObject(bucketName, fileName);
        return fileName + " removed ...";
    }
    
    public String returnDocAwsS3Url(String fileName) {
    	URL url = s3Client.getUrl(bucketName, fileName);
		return url.toExternalForm();
    }


    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
        	throw new StorageException("Error converting multipartFile to file", e);
        }
        return convertedFile;
    }
}
