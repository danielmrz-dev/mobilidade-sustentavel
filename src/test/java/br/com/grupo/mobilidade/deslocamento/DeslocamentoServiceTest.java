package br.com.grupo.mobilidade.deslocamento;

import br.com.grupo.mobilidade.config.MobilidadeProperties;
import br.com.grupo.mobilidade.deslocamento.dto.DeslocamentoRequest;
import br.com.grupo.mobilidade.deslocamento.dto.DeslocamentoResponse;
import br.com.grupo.mobilidade.modal.ModalTransporte;
import br.com.grupo.mobilidade.modal.ModalTransporteRepository;
import br.com.grupo.mobilidade.modal.StatusModal;
import br.com.grupo.mobilidade.modal.TipoModal;
import br.com.grupo.mobilidade.shared.exception.RegraNegocioException;
import br.com.grupo.mobilidade.usuario.Role;
import br.com.grupo.mobilidade.usuario.Usuario;
import br.com.grupo.mobilidade.usuario.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeslocamentoServiceTest {

    @Mock
    private DeslocamentoRepository repository;
    @Mock
    private ModalTransporteRepository modalRepository;
    @Mock
    private UsuarioRepository usuarioRepository;

    private DeslocamentoService service;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        var properties = new MobilidadeProperties(10, new BigDecimal("150"),
                new MobilidadeProperties.Admin("Admin", "admin@teste.com", "Admin@123"));
        service = new DeslocamentoService(repository, modalRepository, usuarioRepository,
                new CalculadoraImpacto(10), properties);
        usuario = new Usuario("Ana", "ana@teste.com", "hash", Role.USER, null);
    }

    @Test
    @DisplayName("Registrar deslocamento de bicicleta credita pontos ao usuario")
    void registrarCreditaPontos() {
        var bicicleta = new ModalTransporte("Bicicleta", TipoModal.BICICLETA, BigDecimal.ZERO);
        var carro = new ModalTransporte("Carro", TipoModal.CARRO_COMBUSTAO, new BigDecimal("150"));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(modalRepository.findById(2L)).thenReturn(Optional.of(bicicleta));
        when(modalRepository.findFirstByTipoAndStatusOrderByIdAsc(TipoModal.CARRO_COMBUSTAO, StatusModal.ATIVO))
                .thenReturn(Optional.of(carro));
        when(repository.save(any(Deslocamento.class))).thenAnswer(inv -> inv.getArgument(0));

        var request = new DeslocamentoRequest(2L, new BigDecimal("10"), LocalDate.now(), "Casa", "Trabalho");
        DeslocamentoResponse resposta = service.registrar(1L, request);

        assertThat(resposta.co2EvitadoKg()).isEqualByComparingTo("1.5");
        assertThat(resposta.pontosGerados()).isEqualTo(15);
        assertThat(usuario.getSaldoPontos()).isEqualTo(15);
    }

    @Test
    @DisplayName("Nao permite registrar deslocamento com modal inativo")
    void modalInativo() {
        var patinete = new ModalTransporte("Patinete", TipoModal.PATINETE_ELETRICO, new BigDecimal("15"));
        patinete.inativar();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(modalRepository.findById(3L)).thenReturn(Optional.of(patinete));

        var request = new DeslocamentoRequest(3L, new BigDecimal("5"), LocalDate.now(), null, null);

        assertThatThrownBy(() -> service.registrar(1L, request))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("inativo");
        verify(repository, never()).save(any());
    }
}
