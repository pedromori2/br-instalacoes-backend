-- Usuario admin inicial. Senha: admin123 (TROQUE em producao apos o primeiro acesso)
INSERT INTO usuarios (nome, email, senha_hash, role, ativo) VALUES
('Administrador', 'admin@financeiro.local', '$2b$10$RCPyD3g/AVopIZclghh2yupH6U7/qndl7aKlVhIIvIwkdLcXfxQ6O', 'ADMIN', TRUE);

INSERT INTO categorias_lancamento (nome, tipo, descricao) VALUES
('Venda de Produto', 'ENTRADA', 'Receita proveniente da venda de produtos'),
('Venda de Servico', 'ENTRADA', 'Receita proveniente da prestacao de servicos'),
('Recebimento de Orcamento', 'ENTRADA', 'Gerado automaticamente ao aprovar um orcamento'),
('Outras Receitas', 'ENTRADA', 'Receitas diversas'),
('Despesas Fixas', 'SAIDA', 'Aluguel, contas, assinaturas'),
('Fornecedores', 'SAIDA', 'Pagamento a fornecedores'),
('Salarios', 'SAIDA', 'Folha de pagamento'),
('Impostos', 'SAIDA', 'Tributos e taxas'),
('Outras Despesas', 'SAIDA', 'Despesas diversas');
