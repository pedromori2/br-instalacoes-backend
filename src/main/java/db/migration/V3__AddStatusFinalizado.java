package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Adiciona o status FINALIZADO a lista permitida na coluna "status" de orcamentos.
 * <p>
 * Migration em Java (em vez de SQL puro) porque o nome gerado automaticamente para o
 * CHECK constraint inline do V1 nao e o mesmo entre Postgres e H2 (usado no perfil de
 * teste) - aqui descobrimos o nome real via information_schema antes de recria-lo, em
 * vez de supor um nome fixo.
 * <p>
 * O filtro tambem exige que a clausula do constraint mencione a coluna "status": a partir
 * do Postgres 17, restricoes NOT NULL (inclusive a da propria coluna "status") tambem
 * aparecem em information_schema.table_constraints com constraint_type = 'CHECK' - e como a
 * clausula dessas e so "status IS NOT NULL", o filtro por "STATUS" tambem batia nelas. Por
 * isso o filtro extra por nome: essas restricoes automaticas sempre terminam em "_not_null"
 * (ex.: "2200_17669_5_not_null" - um nome que nem e um identificador SQL valido sem aspas).
 */
public class V3__AddStatusFinalizado extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();

        List<String> constraintNames = new ArrayList<>();
        try (Statement query = connection.createStatement();
             ResultSet rs = query.executeQuery(
                     "SELECT tc.constraint_name " +
                             "FROM information_schema.table_constraints tc " +
                             "JOIN information_schema.check_constraints cc " +
                             "  ON tc.constraint_name = cc.constraint_name " +
                             "WHERE UPPER(tc.table_name) = 'ORCAMENTOS' " +
                             "  AND UPPER(tc.constraint_type) = 'CHECK' " +
                             "  AND UPPER(cc.check_clause) LIKE '%STATUS%' " +
                             "  AND tc.constraint_name NOT LIKE '%not_null'")) {
            while (rs.next()) {
                constraintNames.add(rs.getString(1));
            }
        }

        try (Statement ddl = connection.createStatement()) {
            for (String constraintName : constraintNames) {
                ddl.execute("ALTER TABLE orcamentos DROP CONSTRAINT " + constraintName);
            }
            ddl.execute("ALTER TABLE orcamentos ADD CONSTRAINT orcamentos_status_check " +
                    "CHECK (status IN ('RASCUNHO','ENVIADO','APROVADO','FINALIZADO','REJEITADO','CANCELADO'))");
        }
    }
}
