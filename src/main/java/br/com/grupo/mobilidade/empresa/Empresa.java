package br.com.grupo.mobilidade.empresa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_MOB_EMPRESA")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOME", nullable = false, length = 120)
    private String nome;

    @Column(name = "CNPJ", nullable = false, unique = true, length = 14)
    private String cnpj;

    @Column(name = "META_CO2_MENSAL_KG", precision = 12, scale = 2)
    private BigDecimal metaCo2MensalKg;

    @Column(name = "CRIADO_EM", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    protected Empresa() {
        // exigido pelo JPA
    }

    public Empresa(String nome, String cnpj, BigDecimal metaCo2MensalKg) {
        this.nome = nome;
        this.cnpj = cnpj;
        this.metaCo2MensalKg = metaCo2MensalKg;
    }

    @PrePersist
    void prePersist() {
        this.criadoEm = LocalDateTime.now();
    }

    public void atualizar(String nome, String cnpj, BigDecimal metaCo2MensalKg) {
        this.nome = nome;
        this.cnpj = cnpj;
        this.metaCo2MensalKg = metaCo2MensalKg;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public BigDecimal getMetaCo2MensalKg() {
        return metaCo2MensalKg;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
