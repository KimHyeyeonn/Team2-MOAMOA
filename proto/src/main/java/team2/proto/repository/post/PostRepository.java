package team2.proto.repository.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team2.proto.dto.post.PostUpdateDTO;
import team2.proto.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAll(Pageable pageable);


    Post findByIdAndDeleteYnFalse(Long id);


    Page<Post> findAllByDeleteYnIsFalse(Pageable pageable);
}
