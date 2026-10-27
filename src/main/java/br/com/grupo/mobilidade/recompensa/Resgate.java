package br.com.grupo.mobilidade.recompensa;

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

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_MOB_RESGATE")
public class Resgate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USUARIO_ID", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RECOMPENSA_ID", nullable = false)
    private Recompensa recompensa;

    @Column(name = "PONTOS_GASTOS", nullable = false)
    private Integer pontosGastos;

    @Column(name = "DATA_RESGATE", nullable = false, updatable = false)
    private LocalDateTime dataResgate;

    protected Resgate() {
        // exigido pelo JPA
    }

    public Resgate(Usuario usuario, Recompensa recompensa, Integer pontosGastos) {
        this.usuario = usuario;
        this.recompensa = recompensa;
        this.pontosGastos = pontosGastos;
    }

    @PrePersist
    void prePersist() {
        this.dataResgate = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Recompensa getRecompensa() {
        return recompensa;
    }

    public Integer getPontosGastos() {
        return pontosGastos;
    }

    public LocalDateTime getDataResgate() {
        return dataResgate;
    }
}
