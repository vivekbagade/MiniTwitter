package com.RabbitmqClient.utils;


import com.jolbox.bonecp.BoneCPDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.object.StoredProcedure;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseProcessor.class);

    private static final RowMapper<String> STRING_ROW_MAPPER = new RowMapper<String>() {
        public String mapRow(ResultSet resultSet, int i) throws SQLException {
            return resultSet.getString(1);
        }
    };

    private DataSource dataSource;

    public DatabaseProcessor(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private SimpleJdbcTemplate getTemplate() {
        return new SimpleJdbcTemplate(dataSource);
    }

    private class RtsStoredProcedure extends StoredProcedure {
        public RtsStoredProcedure(DataSource ds, String spName) {
            setDataSource(ds);
            setSql(spName);
            compile();
        }

        public Map execute() {
            return execute(new HashMap());
        }
    }

    public int callSP(String spName, Object... args) {
        StringBuilder questionMarks = new StringBuilder("");
        if (args.length > 0) {
            questionMarks.append("(");
            for (int i = 0; i < args.length; i++) {
                questionMarks.append("?");
                if (i != args.length - 1) {
                    questionMarks.append(",");
                }
            }
            questionMarks.append(")");
        }
        if (args.length == 0) {
            LOG.info("Calling stored procedure {}", spName);
            new RtsStoredProcedure(dataSource, spName).execute();
            return 0;
        } else {
            return getTemplate().update("{call " + spName + questionMarks.toString() + " }", args);
        }

    }

    public void markImported(String columnFamily, String timestamp) {
        getTemplate().update("{call rts_MarkStatsImported (?, ?)}",
                columnFamily,
                timestamp);
    }

    public void markStarted(String columnFamily, String timestamp) {
        getTemplate().update("{call rts_MarkFileImportStarted (?, ?, ?, ?, ?)}",
                columnFamily,
                timestamp,
                KeyUtil.makeStatsDate(timestamp),
                KeyUtil.makeHourId(timestamp),
                KeyUtil.makeMinuteId(timestamp));
    }

    public void markDeleted(String columnFamily, String rowKey) {
        getTemplate().update("{call rts_MarkCassandraStatsDeleted (?, ?)}",
                columnFamily,
                rowKey);
    }

    public boolean isImported(String columnFamily, String timestamp) {
        return getTemplate().queryForInt("{call rts_IsStatsImported (?, ?)}",
                columnFamily,
                timestamp) == 1;
    }

    public String getLastProcessedKey(String columnFamily) {
        return getTemplate().queryForObject("{call rts_GetLastProcessedKey (?)}",
                STRING_ROW_MAPPER,
                columnFamily);
    }


    /**
     * Returns all the keys for the <code>columnFamily</code> whose data is
     * imported in the database.
     * Keys are returned in ascending order.
     *
     * @param columnFamily
     * @return
     */
    public List<String> getProcessedKeys(String columnFamily) {
        return getTemplate().query("{call rts_GetImportedStatsProcessedKeys (?)}",
                STRING_ROW_MAPPER,
                columnFamily);
    }

    public static DataSource configureDataSource(String JdbcUrl)
            throws ClassNotFoundException {
        Class.forName("net.sourceforge.jtds.jdbc.Driver");
        BoneCPDataSource dataSource = new BoneCPDataSource();
        dataSource.setJdbcUrl(JdbcUrl);
        dataSource.setPartitionCount(1);
        dataSource.setMinConnectionsPerPartition(5);
        dataSource.setMaxConnectionsPerPartition(15);
        return dataSource;
    }
}
