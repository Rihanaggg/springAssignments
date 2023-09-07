package com.prodapt.learningspring.controller.poststats;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.prodapt.learningspring.business.LoggedInUser;
import com.prodapt.learningspring.business.NeedsAuth;
import com.prodapt.learningspring.controller.binding.AddCommentForm;
import com.prodapt.learningspring.controller.binding.AddPostForm;
import com.prodapt.learningspring.controller.exception.ResourceNotFoundException;
import com.prodapt.learningspring.entity.Comment;
import com.prodapt.learningspring.entity.LikeId;
import com.prodapt.learningspring.entity.LikeRecord;
import com.prodapt.learningspring.entity.Post;
import com.prodapt.learningspring.entity.User;
import com.prodapt.learningspring.repository.CommentRepository;
import com.prodapt.learningspring.repository.LikeCRUDRepository;
import com.prodapt.learningspring.repository.LikeCountRepository;
import com.prodapt.learningspring.repository.PostRepository;
import com.prodapt.learningspring.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletException;



@Controller
@RequestMapping("/forum")
public class ForumController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private LikeCountRepository likeCountRepository;

	@Autowired
	private LikeCRUDRepository likeCRUDRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private LoggedInUser loggedInUser;

	private List<User> userList;
	private List<Comment> commentList;

	@PostConstruct
	public void init() {
		userList = new ArrayList<>();
		//commentList = new ArrayList<>();
	}

	 @NeedsAuth(loginPage = "/loginpage")
	@GetMapping("/post/form")
	public String getPostForm(Model model) {
		model.addAttribute("postForm", new AddPostForm());
		userRepository.findAll().forEach(user -> userList.add(user));
		model.addAttribute("userList", userList);
		model.addAttribute("authorid", 1);
		return "forum/postForm";
	}

	@PostMapping("/post/add")
	public String addNewPost(@ModelAttribute("postForm") AddPostForm postForm, BindingResult bindingResult,
			RedirectAttributes attr) throws ServletException {
		if (bindingResult.hasErrors()) {
			System.out.println(bindingResult.getFieldErrors());
			attr.addFlashAttribute("org.springframework.validation.BindingResult.post", bindingResult);
			attr.addFlashAttribute("post", postForm);
			return "redirect:/forum/post/form";
		}
		Optional<User> user = userRepository.findById(loggedInUser.getLoggedInUser().getId());
		if (user.isEmpty()) {
			throw new ServletException("Something went seriously wrong and we couldn't find the user in the DB");
		}
		Post post = new Post();
		post.setAuthor(user.get());
		post.setContent(postForm.getContent());
		postRepository.save(post);

		return String.format("redirect:/forum/post/%d", post.getId());
	}

	@NeedsAuth(loginPage = "/loginpage")
	@GetMapping("/post/{id}")
	public String postDetail(@PathVariable int id, Model model) throws ResourceNotFoundException {
		Optional<Post> post = postRepository.findById(id);
		if (post.isEmpty()) {
			throw new ResourceNotFoundException("No post with the requested ID");
		}
		List<Comment> commentList = commentRepository.findAllByPostId(id);
		model.addAttribute("commentList", commentList);
		model.addAttribute("post", post.get());
		model.addAttribute("userList", userList);
		int numLikes = likeCountRepository.countByPostId(id);
		model.addAttribute("likeCount", numLikes);
		model.addAttribute("commentForm", new AddCommentForm());
		return "forum/postDetail";
	}

	@PostMapping("/post/{id}/like")
	public String postLike(@PathVariable int id, Integer likerId, RedirectAttributes attr) {
		LikeId likeId = new LikeId();
		likeId.setUser(userRepository.findById(loggedInUser.getLoggedInUser().getId()).get());
		likeId.setPost(postRepository.findById(id).get());
		LikeRecord like = new LikeRecord();
		like.setLikeId(likeId);
		likeCRUDRepository.save(like);
		return String.format("redirect:/forum/post/%d", id);
	}

	@PostMapping("/post/{id}/comment")
	public String addCommentToPost(@ModelAttribute("commentForm") AddCommentForm commentForm, @PathVariable int id) {
		Optional<User> user = userRepository.findById(loggedInUser.getLoggedInUser().getId());
		Optional<Post> post = postRepository.findById(id);
		if (user.isPresent() && post.isPresent()) {
			Comment comment = new Comment();
			comment.setContent(commentForm.getContent());
			comment.setPost(post.get());
			comment.setUser(user.get());
			commentRepository.save(comment);
		}
		return String.format("redirect:/forum/post/%d", id);
	}

}