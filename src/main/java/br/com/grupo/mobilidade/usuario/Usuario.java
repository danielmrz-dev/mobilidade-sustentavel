package br.com.grupo.mobilidade.usuario;

import br.com.grupo.mobilidade.empresa.Empresa;
import br.com.grupo.mobilidade.shared.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "TB_MOB_USUARIO")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "SENHA", nullable = false, length = 100)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(name = "PERFIL", nullable = false, length = 20)
    private Role role;

    @Column(name = "SALDO_PONTOS", nullable = false)
    private Integer saldoPontos = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPRESA_ID")
    private Empresa empresa;

    @Column(name = "CRIADO_EM", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    protected Usuario() {
        // exigido pelo JPA
    }

    public Usuario(String nome, String email, String senhaCriptografada, Role role, Empresa empresa) {
        this.nome = nome;
        this.email = email;
        this.senha = senhaCriptografada;
        this.role = role;
        this.empresa = empresa;
        this.saldoPontos = 0;
    }

    @PrePersist
    void prePersist() {
        this.criadoEm = LocalDateTime.now();
        if (this.saldoPontos == null) {
            this.saldoPontos = 0;
        }
    }

    public void adicionarPontos(int pontos) {
        if (pontos < 0) {
            throw new IllegalArgumentException("Quantidade de pontos nao pode ser negativa");
        }
        this.saldoPontos += pontos;
    }

    public void debitarPontos(int pontos) {
        if (pontos < 0) {
            throw new IllegalArgumentException("Quantidade de pontos nao pode ser negativa");
        }
        if (pontos > this.saldoPontos) {
            throw new RegraNegocioException("Saldo de pontos insuficiente. Saldo atual: " + saldoPontos
                    + ", necessario: " + pontos);
        }
        this.saldoPontos -= pontos;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public Role getRole() {
        return role;
    }

    public Integer getSaldoPontos() {
        return saldoPontos;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
