package com.tehbeard.beardstat.dataproviders;

import java.sql.SQLException;

import com.tehbeard.beardstat.BeardStat;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

public class MysqlStatDataProvider extends JDBCStatDataProvider {

    public MysqlStatDataProvider(BeardStat plugin, String host, int port, String database, String tablePrefix,
            String username, String password, boolean backups) throws SQLException {

        super(plugin, "sql", "com.mysql.jdbc.Driver", backups);
        this.tblPrefix = tablePrefix;

        this.connectionUrl = String.format("jdbc:mysql://%s:%s/%s", host, port, database);
        this.connectionProperties.put("user", username);
        this.connectionProperties.put("password", password);
        this.connectionProperties.put("autoReconnect", "true");

        initialise();
    }

    @Override
    public void generateBackup(File file) {
        plugin.getLogger().log(Level.INFO, "Creating backup of database at {0}", file.toString());
        try {
            FileOutputStream fw = new FileOutputStream(file);
            GZIPOutputStream gos = new GZIPOutputStream(fw){{def.setLevel(Deflater.BEST_COMPRESSION);}};
            dumpToBuffer(new BufferedWriter(new OutputStreamWriter(gos)));
        } catch (IOException ex) {
            Logger.getLogger(MysqlStatDataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    private ResultSet query(String sql) throws SQLException{
        return conn.prepareStatement(sql).executeQuery();
    }

    private void dumpToBuffer(BufferedWriter buff) {
        try {
            
            String version = "-- If restoring to this backup, set stats.database.sql_db_version to : " + this.plugin.getConfig().getInt("stats.database.sql_db_version", 1);
            StringBuilder sb = new StringBuilder();
            ResultSet rs =query("SHOW FULL TABLES WHERE Table_type != 'VIEW'");
            while (rs.next()) {
                String tbl = rs.getString(1);
                if(!tbl.startsWith(tblPrefix)){continue;}
                sb.append(version).append("\n");
                sb.append("\n");
                sb.append("-- ----------------------------\n")
                        .append("-- Table structure for `").append(tbl)
                        .append("`\n-- ----------------------------\n");
                sb.append("DROP TABLE IF EXISTS `").append(tbl).append("`;\n");
                ResultSet rs2 = query("SHOW CREATE TABLE `" + tbl + "`");
                rs2.next();
                String crt = rs2.getString(2) + ";";
                sb.append(crt).append("\n");
                sb.append("\n");
                sb.append("-- ----------------------------\n").append("-- Records for `").append(tbl).append("`\n-- ----------------------------\n");

                ResultSet rss = query("SELECT * FROM " + tbl);
                while (rss.next()) {
                    int colCount = rss.getMetaData().getColumnCount();
                    if (colCount > 0) {
                        sb.append("INSERT INTO ").append(tbl).append(" VALUES(");

                        for (int i = 0; i < colCount; i++) {
                            if (i > 0) {
                                sb.append(",");
                            }
                            String s = "";
                            try {
                                s += "'";
                                s += rss.getObject(i + 1).toString();
                                s += "'";
                            } catch (Exception e) {
                                s = "NULL";
                            }
                            sb.append(s);
                        }
                        sb.append(");\n");
                        buff.append(sb.toString());
                        sb = new StringBuilder();
                    }
                }
            }

            ResultSet rs2 = query("SHOW FULL TABLES WHERE Table_type = 'VIEW'");
            while (rs2.next()) {
                String tbl = rs2.getString(1);

                sb.append("\n");
                sb.append("-- ----------------------------\n")
                        .append("-- View structure for `").append(tbl)
                        .append("`\n-- ----------------------------\n");
                sb.append("DROP VIEW IF EXISTS `").append(tbl).append("`;\n");
                ResultSet rs3 = query("SHOW CREATE VIEW `" + tbl + "`");
                rs3.next();
                String crt = rs3.getString(2) + ";";
                sb.append(crt).append("\n");
            }

            buff.flush();
            buff.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
