package br.com.grupo.mobilidade.empresa.dto;

import br.com.grupo.mobilidade.empresa.Empresa;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EmpresaResponse(
        Long id,
        String nome,
        String cnpj,
        BigDecimal metaCo2MensalKg,
        LocalDateTime criadoEm
) {

    public static EmpresaResponse from(Empresa empresa) {
        return new EmpresaResponse(empresa.getId(), empresa.getNome(), empresa.getCnpj(),
                empresa.getMetaCo2MensalKg(), empresa.getCriadoEm());
    }
}
