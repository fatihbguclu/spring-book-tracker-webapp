package io.java.home;

import io.java.user.BooksByUser;
import io.java.user.BooksByUserRepository;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final String COVER_IMAGE_ROOT = "http://covers.openlibrary.org/b/id/";
    private BooksByUserRepository booksByUserRepository;

    public HomeController(BooksByUserRepository booksByUserRepository) {
        this.booksByUserRepository = booksByUserRepository;
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OAuth2User principal, Model model){

        if (principal == null || principal.getAttribute("login") == null) {
            return "index";
        }
        String userId = principal.getAttribute("login");

        Slice<BooksByUser> booksByUsersSlice = booksByUserRepository.findAllById(userId, CassandraPageRequest.of(0,100));
        List<BooksByUser> booksByUserList = booksByUsersSlice.getContent();
        booksByUserList = booksByUserList.stream().distinct()
                .map(book -> {
                    String coverImgUrl = "/images/no-image.png";
                    if (book.getCoverIds() != null && book.getCoverIds().size() > 0){
                        coverImgUrl = COVER_IMAGE_ROOT + book.getCoverIds().get(0) + "-M.jpg";
                    }
                    book.setCoverUrl(coverImgUrl);
                    return book;
                    })
                .collect(Collectors.toList());

        model.addAttribute("books",booksByUserList);

        return "home";
    }
}
