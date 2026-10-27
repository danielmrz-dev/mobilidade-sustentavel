-- =====================================================================
-- V3 - Dados iniciais para testes (empresas e catalogo de recompensas)
-- O usuario ADMIN e criado pela aplicacao na inicializacao (AdminInitializer),
-- usando a senha definida em variavel de ambiente e criptografada com BCrypt.
-- =====================================================================

INSERT INTO TB_MOB_EMPRESA (NOME, CNPJ, META_CO2_MENSAL_KG) VALUES ('Verde Logistica Ltda', '11222333000181', 500);
INSERT INTO TB_MOB_EMPRESA (NOME, CNPJ, META_CO2_MENSAL_KG) VALUES ('Tech Ciclo Solucoes', '45723174000110', 300);

INSERT INTO TB_MOB_RECOMPENSA (TITULO, DESCRICAO, CUSTO_PONTOS, ESTOQUE)
VALUES ('Credito em bike compartilhada', '30 minutos de bicicleta compartilhada', 50, 100);

INSERT INTO TB_MOB_RECOMPENSA (TITULO, DESCRICAO, CUSTO_PONTOS, ESTOQUE)
VALUES ('Recarga de bilhete de transporte', 'Credito de uma passagem no transporte publico', 80, 200);

INSERT INTO TB_MOB_RECOMPENSA (TITULO, DESCRICAO, CUSTO_PONTOS, ESTOQUE)
VALUES ('Day off sustentavel', 'Um dia de folga concedido pela empresa parceira', 2000, 5);

INSERT INTO TB_MOB_RECOMPENSA (TITULO, DESCRICAO, CUSTO_PONTOS, ESTOQUE)
VALUES ('Muda de arvore nativa', 'Plantio de uma muda em area de reflorestamento', 150, 50);
