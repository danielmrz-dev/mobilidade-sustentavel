package br.com.grupo.mobilidade.deslocamento;

import br.com.grupo.mobilidade.modal.ModalTransporte;
import br.com.grupo.mobilidade.usuario.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_MOB_DESLOCAMENTO")
public class Deslocamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USUARIO_ID", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MODAL_ID", nullable = false)
    private ModalTransporte modal;

    @Column(name = "DISTANCIA_KM", nullable = false, precision = 8, scale = 2)
    private BigDecimal distanciaKm;

    @Column(name = "DATA_DESLOCAMENTO", nullable = false)
    private LocalDate dataDeslocamento;

    @Column(name = "ORIGEM", length = 120)
    private String origem;

    @Column(name = "DESTINO", length = 120)
    private String destino;

    @Column(name = "CO2_EMITIDO_KG", nullable = false, precision = 12, scale = 3)
    private BigDecimal co2EmitidoKg;

    @Column(name = "CO2_EVITADO_KG", nullable = false, precision = 12, scale = 3)
    private BigDecimal co2EvitadoKg;

    @Column(name = "PONTOS_GERADOS", nullable = false)
    private Integer pontosGerados;

    @Column(name = "CRIADO_EM", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    protected Deslocamento() {
        // exigido pelo JPA
    }

    public Deslocamento(Usuario usuario, ModalTransporte modal, BigDecimal distanciaKm, LocalDate dataDeslocamento,
                        String origem, String destino, ResultadoImpacto impacto) {
        this.usuario = usuario;
        this.modal = modal;
        this.distanciaKm = distanciaKm;
        this.dataDeslocamento = dataDeslocamento;
        this.origem = origem;
        this.destino = destino;
        aplicarImpacto(impacto);
    }

    @PrePersist
    void prePersist() {
        this.criadoEm = LocalDateTime.now();
    }

    public void atualizar(ModalTransporte modal, BigDecimal distanciaKm, LocalDate dataDeslocamento,
                          String origem, String destino, ResultadoImpacto impacto) {
        this.modal = modal;
        this.distanciaKm = distanciaKm;
        this.dataDeslocamento = dataDeslocamento;
        this.origem = origem;
        this.destino = destino;
        aplicarImpacto(impacto);
    }

    private void aplicarImpacto(ResultadoImpacto impacto) {
        this.co2EmitidoKg = impacto.co2EmitidoKg();
        this.co2EvitadoKg = impacto.co2EvitadoKg();
        this.pontosGerados = impacto.pontos();
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public ModalTransporte getModal() {
        return modal;
    }

    public BigDecimal getDistanciaKm() {
        return distanciaKm;
    }

    public LocalDate getDataDeslocamento() {
        return dataDeslocamento;
    }

    public String getOrigem() {
        return origem;
    }

    public String getDestino() {
        return destino;
    }

    public BigDecimal getCo2EmitidoKg() {
        return co2EmitidoKg;
    }

    public BigDecimal getCo2EvitadoKg() {
        return co2EvitadoKg;
    }

    public Integer getPontosGerados() {
        return pontosGerados;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
