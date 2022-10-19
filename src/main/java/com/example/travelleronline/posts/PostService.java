package com.example.travelleronline.posts;

import com.example.travelleronline.categories.Category;
import com.example.travelleronline.categories.CategoryService;
import com.example.travelleronline.exceptions.BadRequestException;
import com.example.travelleronline.exceptions.NotFoundException;
import com.example.travelleronline.posts.dtos.PostDTO;
import com.example.travelleronline.posts.dtos.PostDTONoOwner;
import com.example.travelleronline.util.MasterService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService extends MasterService {

    @Autowired
    private CategoryService categoryService;

    public PostDTO createPost(Post p){
        validatePost(p);
        p.setDateOfUpload(LocalDateTime.now());
        postRepository.save(p);
        return modelMapper.map(p,PostDTO.class);
    }
    public List<PostDTONoOwner> getAllPosts(){
        return postRepository.findAll().stream().map((p -> modelMapper.map(p,PostDTONoOwner.class))).collect(Collectors.toList());
    }

    public PostDTO getPostById(int id){
        Post p = postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post not found."));
        return modelMapper.map(p,PostDTO.class);
    }

    public void deletePostById(int id){ //TODO check who is deleting(owner/admin/otherUser)
        postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post not found."));
        postRepository.deleteById(id);
    }

    public void deleteAllPosts(){
        postRepository.deleteAll();
    }

    public PostDTO editPost(int id, PostDTO dto){ //TODO check who is editing(owner/admin/otherUser)
        Post p = modelMapper.map(dto, Post.class);
        validatePost(p);
        Post existingPost = postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post not found."));
        existingPost.setTitle(dto.getTitle());
        existingPost.setDescription(dto.getDescription());
        existingPost.setCategory(dto.getCategory());
        existingPost.setClipUri(dto.getClipUri());
        existingPost.setLocationLatitude(dto.getLocationLatitude());
        existingPost.setLocationLongitude(dto.getLocationLongitude());
        postRepository.save(existingPost);
        return modelMapper.map(existingPost, PostDTO.class);
    }

    private void validatePost(Post p) {
        validateTitle(p.getTitle());
        validateDescription(p.getDescription());
        validateCategory(p.getCategory());
//        validateClipUri();//TODO Wednesday we will know how to validate this
        validateLocation(p.getLocationLatitude(),p.getLocationLongitude());
    }

    private void validateLocation(double locationLatitude, double locationLongitude) {
        if(locationLongitude < -180 || locationLongitude > 180 || locationLatitude < -90 || locationLatitude > 90) {
            throw new BadRequestException("Location latitude must be a number between -90 and 90 and the longitude between -180 and 180.");
        }
    }

    //TODO check if this is the right way to validate category.
    private void validateCategory(Category category) {
        if(categoryService.getCategoryById(category.getId()) == null){
            throw new BadRequestException("No such category.");
        }
    }

    private void validateDescription(String desc) {
        if(desc.length() > 500) {
            throw new BadRequestException("Description must be between 0 and 500 letters.");
        }
    }

    private void validateTitle(String title) {
        if(title == null || title.equals("null")) {
            throw new BadRequestException("Title can not be null.");
        }
        if(title.length() < 3 || title.isBlank() || title.length() > 10) {
            throw new BadRequestException("Title must be between 3 and 100 letters");
        }
        List<Post> posts = postRepository.findAll();
        for (Post p : posts) {
            if(p.getTitle().equals(title)) {
                throw new BadRequestException("Title already exists.");
            }
        }
    }

//    public void uploadPostImageOrVideo(int pid, MultipartFile multipartFile) {
//        Post p = postRepository.findById(pid).orElseThrow(() -> new NotFoundException("Post not found."));
//        //TODO validate if the user is logged in and if the user is the owner of the post.
//        String name = System.nanoTime() + File.separator + "." ;
//        if(multipartFile.getContentType().equals(){//picture types
//            //save in pictures
//        }
//        else if(multipartFile.getContentType().equals()){ //video types
//            //save in video url
//        }
//        else {
//            //throw exception
//        }
//
//    }


}
