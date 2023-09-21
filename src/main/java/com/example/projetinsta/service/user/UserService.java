package com.example.projetinsta.service.user;

import com.example.projetinsta.dto.UserDto;
import com.example.projetinsta.entity.ConfirmationToken;
import com.example.projetinsta.entity.UserEntity;
import com.example.projetinsta.entity.UserRole;
import com.example.projetinsta.exception.NotAllowedException;
import com.example.projetinsta.exception.NotFoundException;
import com.example.projetinsta.repository.UserRepository;
import com.example.projetinsta.service.email.EmailSender;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private static final String USER_NOT_FOUND_MSG = "user with email %s not found";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailSender emailSender;
    private final ConfirmationTokenService confirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }

    public void signUpUser(UserDto userDto) {
        UserEntity userEntity = new UserEntity(
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getEmail(),
                userDto.getPassword(),
                UserRole.USER

        );
        boolean userExists = userRepository.findByEmail(userEntity.getEmail())
                .isPresent();

        if (userExists) {
            throw new IllegalStateException("Email already taken");
        }
        String encodePassword = passwordEncoder.encode(userEntity.getPassword());

        userEntity.setPassword(encodePassword);

        String token = UUID.randomUUID()
                .toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now()
                        .plusMinutes(15),
                userEntity
        );

        userRepository.save(userEntity);
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        String link = "http://localhost:8080/registrations/confirm?token=" + token;
        emailSender.send(
                userDto.getEmail(),
                buildEmail(userDto.getFirstName(), link)
        );
    }

    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found")
                );
        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("Email already confirmed");

        }
        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException(("token expired"));
        }

        confirmationTokenService.setConfirmedAt(token);
        userRepository.enableUser(confirmationToken.getUserEntity()
                .getEmail());
        return "confirmed";
    }

    private String buildEmail(
            String name,
            String link
    ) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;" +
                "color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;" +
                "min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" " +
                "border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" " +
                "style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" " +
                "cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" " +
                "border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;" +
                "Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;" +
                "font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;" +
                "display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" " +
                "align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" " +
                "style=\"border-collapse:collapse;max-width:580px;width:100%!important\" " +
                "width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" " +
                "cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" " +
                "align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" " +
                "style=\"border-collapse:collapse;max-width:580px;width:100%!important\" " +
                "width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;" +
                "line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;" +
                "color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;" +
                "line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the " +
                "below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;" +
                "border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;" +
                "line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;" +
                "color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link " +
                "will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }

    public ResponseEntity<List<UserEntity>> findAll() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!auth.getAuthorities().contains(UserRole.ADMIN)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (userRepository.findAll().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<UserEntity> findById(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!((UserEntity) auth.getPrincipal()).getId().equals(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (userRepository.findById(id).isEmpty()) {
            throw new ResponseStatusException((HttpStatus.NOT_FOUND));
        }
        return new ResponseEntity<>(userRepository.findById(id).get(), HttpStatus.OK);
    }

    public ResponseEntity<HttpStatus> updateUser(Long id, UserDto userDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!((UserEntity) auth.getPrincipal()).getId().equals(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        UserEntity user =
                userRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("user with id: " + id + "cannot be found"));

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        userRepository.save(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    public void deleteUserById(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!((UserEntity) auth.getPrincipal()).getId().equals(id)) {
            throw new NotAllowedException();
        }
        if ((userRepository.findById(id).isEmpty())) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        userRepository.deleteById(id);
    }
}