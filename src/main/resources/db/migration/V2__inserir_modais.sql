-- =====================================================================
-- V2 - Modais de transporte e fatores de emissao (g de CO2 por km, por passageiro)
--
-- ATENCAO: os valores abaixo sao APROXIMADOS, apenas para o MVP.
-- O grupo deve revisar com uma fonte (ex.: inventarios de emissoes do
-- setor de transportes) e cita-la no README.
--
-- O modal do tipo CARRO_COMBUSTAO e a REFERENCIA do calculo:
-- CO2 evitado = distancia x (fator da referencia - fator do modal usado)
-- =====================================================================

INSERT INTO TB_MOB_MODAL (NOME, TIPO, FATOR_EMISSAO_G_KM, STATUS) VALUES ('A pe', 'A_PE', 0, 'ATIVO');
INSERT INTO TB_MOB_MODAL (NOME, TIPO, FATOR_EMISSAO_G_KM, STATUS) VALUES ('Bicicleta', 'BICICLETA', 0, 'ATIVO');
INSERT INTO TB_MOB_MODAL (NOME, TIPO, FATOR_EMISSAO_G_KM, STATUS) VALUES ('Patinete eletrico', 'PATINETE_ELETRICO', 15, 'ATIVO');
INSERT INTO TB_MOB_MODAL (NOME, TIPO, FATOR_EMISSAO_G_KM, STATUS) VALUES ('Onibus', 'ONIBUS', 70, 'ATIVO');
INSERT INTO TB_MOB_MODAL (NOME, TIPO, FATOR_EMISSAO_G_KM, STATUS) VALUES ('Metro / Trem', 'METRO_TREM', 30, 'ATIVO');
INSERT INTO TB_MOB_MODAL (NOME, TIPO, FATOR_EMISSAO_G_KM, STATUS) VALUES ('Carro eletrico', 'CARRO_ELETRICO', 20, 'ATIVO');
INSERT INTO TB_MOB_MODAL (NOME, TIPO, FATOR_EMISSAO_G_KM, STATUS) VALUES ('Carona compartilhada', 'CARONA', 75, 'ATIVO');
INSERT INTO TB_MOB_MODAL (NOME, TIPO, FATOR_EMISSAO_G_KM, STATUS) VALUES ('Moto a combustao', 'MOTO_COMBUSTAO', 100, 'ATIVO');
INSERT INTO TB_MOB_MODAL (NOME, TIPO, FATOR_EMISSAO_G_KM, STATUS) VALUES ('Carro a combustao', 'CARRO_COMBUSTAO', 150, 'ATIVO');
