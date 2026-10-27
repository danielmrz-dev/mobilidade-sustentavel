package br.com.grupo.mobilidade.modal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "TB_MOB_MODAL")
public class ModalTransporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOME", nullable = false, unique = true, length = 60)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false, length = 30)
    private TipoModal tipo;

    /** Gramas de CO2 emitidas por km, por passageiro. */
    @Column(name = "FATOR_EMISSAO_G_KM", nullable = false, precision = 8, scale = 2)
    private BigDecimal fatorEmissaoGKm;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 10)
    private StatusModal status = StatusModal.ATIVO;

    protected ModalTransporte() {
        // exigido pelo JPA
    }

    public ModalTransporte(String nome, TipoModal tipo, BigDecimal fatorEmissaoGKm) {
        this.nome = nome;
        this.tipo = tipo;
        this.fatorEmissaoGKm = fatorEmissaoGKm;
        this.status = StatusModal.ATIVO;
    }

    public void atualizar(String nome, TipoModal tipo, BigDecimal fatorEmissaoGKm) {
        this.nome = nome;
        this.tipo = tipo;
        this.fatorEmissaoGKm = fatorEmissaoGKm;
    }

    public void inativar() {
        this.status = StatusModal.INATIVO;
    }

    public void ativar() {
        this.status = StatusModal.ATIVO;
    }

    public boolean isAtivo() {
        return status == StatusModal.ATIVO;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public TipoModal getTipo() {
        return tipo;
    }

    public BigDecimal getFatorEmissaoGKm() {
        return fatorEmissaoGKm;
    }

    public StatusModal getStatus() {
        return status;
    }
}
