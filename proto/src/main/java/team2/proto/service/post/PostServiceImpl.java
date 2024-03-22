package team2.proto.service.post;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.proto.dto.post.PostResponseDTO;
import team2.proto.dto.post.PostUpdateDTO;
import team2.proto.dto.post.PostUserDTO;
import team2.proto.dto.post.PostWriteDTO;
import team2.proto.entity.Post;
import team2.proto.entity.PostUser;
import team2.proto.entity.PostUserPK;


import team2.proto.entity.User;
import team2.proto.repository.post.PostRepository;

import team2.proto.service.authentication.UserService;

import team2.proto.repository.post.PostUserRepository;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository ;
    private  final PostUserRepository postUserRepository ;
    private final UserService userService ;

    // 게시글 작성
    @Transactional
    @Override
    public Long createPost(PostWriteDTO params, String userEmail) {
        // 유저 이메일로부터 해당 유저의 ID를 가져옴
        String userNickname = userService.findByEmail(userEmail).getNickname();
        User user = userService.findByEmail(userEmail);


        // dto -> entity
        Post post = Post.builder()
                .title(params.getTitle())
                .price(params.getPrice())
                .headCount(params.getHeadCount())
                .deadline(params.getDeadLine())
                .receivePlace(params.getReceivePlace())
                .productUrl(params.getProductUrl())
                .writer(userNickname)
                .build();

        postRepository.save(post);

        // PostUserDTO에서 정보 가져와서 PostUser 엔티티 생성
        PostUserDTO postUserDTO = new PostUserDTO(true, post, user);
        PostUser postUser = convertToEntity(postUserDTO);

        // PostUser 엔티티 저장
        postUserRepository.save(postUser);
        return post.getId();
    }

    private PostUser convertToEntity(PostUserDTO postUserDTO) {
        return PostUser.builder()
                .id(new PostUserPK(postUserDTO.getUser(),postUserDTO.getPost()))
                .isHost(postUserDTO.isHost())
                .post(postUserDTO.getPost())
                .user(postUserDTO.getUser())
                .build();
    }
    // 단일 게시글 조회
    @Transactional(readOnly = true)
    @Override
    public PostResponseDTO getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow();
        // entity -> dto
        PostResponseDTO postResponseDTO = new PostResponseDTO(post.getTitle(),
                post.getPrice(),
                post.getHeadCount(),
                post.getDeadline(),
                post.getReceivePlace(),
                post.getProductUrl());
        return postResponseDTO ;
    }
    // 게시글 수정
    @Override
    public void updatePost(Long id, PostUpdateDTO param) {
        Post post = postRepository.findById(id).orElseThrow();
        post.setTitle(param.getTitle());
        post.setPrice(param.getPrice());
        post.setHeadCount(param.getHeadCount());
        post.setDeadline(param.getDeadLine());
        post.setReceivePlace(param.getReceivePlace());
        post.setProductUrl(param.getProductUrl());

        postRepository.save(post);
    }

    // 전체 게시글 조회
    @Transactional(readOnly = true)
    @Override
    public List<PostWriteDTO> getAllPosts(Integer pageno) {
        Page<Post> posts = postRepository.findAll(PageRequest.of(pageno, 10));
        List<PostWriteDTO> postList  = posts.getContent()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return postList;
    }

    // entity -> dto
    private PostWriteDTO convertToDTO(Post post) {
        return new PostWriteDTO(
                post.getTitle(),
                post.getPrice(),
                post.getHeadCount(),
                post.getDeadline(),
                post.getReceivePlace(),
                post.getProductUrl());
    }

    // 게시글 삭제
    @Transactional
    @Override
    public void delete(Long postId) {
        // 게시글을 불러옴
        Post post = postRepository.findById(postId).orElseThrow();

        // 삭제 여부 플래그를 설정하고 엔티티를 저장
        post.setDeleteYn(true);
        postRepository.save(post);
    }


}
