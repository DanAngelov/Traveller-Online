package com.example.travelleronline;

import com.example.travelleronline.comments.dtos.CommentWithParentDTO;
import com.example.travelleronline.comments.dtos.CommentWithoutParentDTO;
import com.example.travelleronline.comments.Comment;
import com.example.travelleronline.posts.Post;
import com.example.travelleronline.posts.dtos.PostDTO;
import com.example.travelleronline.users.User;
import com.example.travelleronline.users.dtos.UserProfileDTO;
import com.example.travelleronline.general.util.converters.CommentReactionsListToIntegersConverter;
import com.example.travelleronline.general.util.converters.PostReactionsListToIntegersConverter;
import com.example.travelleronline.general.util.converters.UserSubscribersListToIntegerConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Properties;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableScheduling
public class TravellerOnlineApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravellerOnlineApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(User.class, UserProfileDTO.class)
                .addMappings(new PropertyMap<User, UserProfileDTO>() {
            @Override
            protected void configure() {
                using(new UserSubscribersListToIntegerConverter())
                        .map(source.getSubscribers(), destination.getSubscribers());
            }
        });

        modelMapper.typeMap(Post.class, PostDTO.class)
                .addMappings(new PropertyMap<Post, PostDTO>() {
                    @Override
                    protected void configure() {
                        using(new PostReactionsListToIntegersConverter())
                                .map(source.getPostReactions(), destination.getReactions());
                    }
                });

        modelMapper.typeMap(Comment.class, CommentWithoutParentDTO.class)
                .addMappings(new PropertyMap<Comment, CommentWithoutParentDTO>() {
                    @Override
                    protected void configure() {
                        using(new CommentReactionsListToIntegersConverter())
                                .map(source.getCommentReactions(), destination.getReactions());
                    }
                });

        modelMapper.typeMap(Comment.class, CommentWithParentDTO.class)
                .addMappings(new PropertyMap<Comment, CommentWithParentDTO>() {
                    @Override
                    protected void configure() {
                        using(new CommentReactionsListToIntegersConverter())
                                .map(source.getCommentReactions(), destination.getReactions());
                    }
                });

        return modelMapper;
    }

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername("traveller.online.s14@gmail.com");
        mailSender.setPassword("sanihrzjigngeypg");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        return mailSender;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/traveller_online");
        dataSource.setUsername("root");
        dataSource.setPassword("root");

        return new JdbcTemplate(dataSource);
    }

}