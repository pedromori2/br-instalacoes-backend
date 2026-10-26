-- O Supabase Auth passa a ser dono da identidade/senha do usuario. A coluna supabase_user_id
-- liga nosso registro de negocio (papel, nome, ativo) ao usuario correspondente no Supabase;
-- o id BIGINT interno continua o mesmo e nao precisa de nenhuma mudanca nas FKs que o referenciam
-- (orcamentos.usuario_id, lancamentos.usuario_id, fechamentos_caixa.usuario_id).
-- UNIQUE ja cria um indice implicito, nao precisa de um CREATE INDEX separado.
ALTER TABLE usuarios ADD COLUMN supabase_user_id UUID UNIQUE;
ALTER TABLE usuarios DROP COLUMN senha_hash;
