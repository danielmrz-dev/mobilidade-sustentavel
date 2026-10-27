package br.com.grupo.mobilidade.config;

import br.com.grupo.mobilidade.usuario.Role;
import br.com.grupo.mobilidade.usuario.Usuario;
import br.com.grupo.mobilidade.usuario.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cria o primeiro usuario ADMIN na inicializacao, caso ainda nao exista nenhum.
 * Credenciais vem das variaveis ADMIN_EMAIL e ADMIN_SENHA (ver application.yml).
 */
@Component
public class AdminInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final MobilidadeProperties properties;

    public AdminInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                            MobilidadeProperties properties) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.properties = properties;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (usuarioRepository.existsByRole(Role.ADMIN)) {
            return;
        }
        var admin = properties.admin();
        usuarioRepository.save(new Usuario(admin.nome(), admin.email().toLowerCase(),
                passwordEncoder.encode(admin.senha()), Role.ADMIN, null));
        log.info("Usuario ADMIN inicial criado: {}", admin.email());
    }
}
