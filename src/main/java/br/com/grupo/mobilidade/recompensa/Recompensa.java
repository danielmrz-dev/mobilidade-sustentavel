package br.com.grupo.mobilidade.recompensa;

import br.com.grupo.mobilidade.shared.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "TB_MOB_RECOMPENSA")
public class Recompensa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TITULO", nullable = false, length = 100)
    private String titulo;

    @Column(name = "DESCRICAO", length = 255)
    private String descricao;

    @Column(name = "CUSTO_PONTOS", nullable = false)
    private Integer custoPontos;

    @Column(name = "ESTOQUE", nullable = false)
    private Integer estoque;

    protected Recompensa() {
        // exigido pelo JPA
    }

    public Recompensa(String titulo, String descricao, Integer custoPontos, Integer estoque) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.custoPontos = custoPontos;
        this.estoque = estoque;
    }

    public void atualizar(String titulo, String descricao, Integer custoPontos, Integer estoque) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.custoPontos = custoPontos;
        this.estoque = estoque;
    }

    public void baixarEstoque() {
        if (estoque <= 0) {
            throw new RegraNegocioException("A recompensa '" + titulo + "' esta sem estoque");
        }
        this.estoque--;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getCustoPontos() {
        return custoPontos;
    }

    public Integer getEstoque() {
        return estoque;
    }
}
