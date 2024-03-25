package team2.proto.service.post;

import jakarta.servlet.http.HttpServletRequest;
import team2.proto.dto.post.PostListResponseDTO;
import team2.proto.dto.post.PostResponseDTO;
import team2.proto.dto.post.PostUpdateDTO;
import team2.proto.dto.post.PostWriteDTO;


import java.util.List;

public interface PostService {
    Long createPost(PostWriteDTO params, String userEmail);
    PostResponseDTO getPostById(Long id);
    void updatePost(Long id, PostUpdateDTO param);
    List<PostListResponseDTO> getAllPosts(Integer pageno);
    void delete(Long id);

    void join(Long postId, HttpServletRequest request);
    void cancel(Long id);
}
