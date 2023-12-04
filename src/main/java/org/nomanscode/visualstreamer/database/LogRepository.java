package org.nomanscode.visualstreamer.database;

import org.nomanscode.visualstreamer.common.ErrorLevel;
import org.nomanscode.visualstreamer.common.LogEnvelope;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
public class LogRepository {

    /*@Autowired
    private JdbcTemplate jdbcTemplate;*/


    public void writeInformation(String title, String nodeName)
    {
        this.writeLog(title, ErrorLevel.INFORMATION, "", nodeName);
    }

    public void writeWarning(String title, String nodeName)
    {
        this.writeLog(title, ErrorLevel.WARNING, "", nodeName);
    }

    public void writeError(String title, String nodeName)
    {
        this.writeLog(title, ErrorLevel.COMMON_ERROR, "", nodeName);
    }

    public void writeSevereError(String title, String cause, String nodeName)
    {
        this.writeLog(title, ErrorLevel.SEVERE_ERROR, cause, nodeName);
    }

    private void writeLog(String title, ErrorLevel level, String extraData, String nodeName)
    {
        /*try (Connection conn = jdbcTemplate.getDataSource().getConnection()) {

            conn.setAutoCommit(true);

            String sql = "INSERT INTO system.log (title, level, subsystem, extradata, nodename) VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, title);
                stmt.setString(2, level.toString());
                stmt.setString(3, subsystem.toString());
                stmt.setString(4, extraData);
                stmt.setString(5, nodeName);
                stmt.executeUpdate();
            }

        } catch (Exception ex) {
            log.error( "An error occurred while setting object in " + this.getClass().getName(), ex.getMessage(), ex);
        }*/
    }

    public List<LogEnvelope> getLogs(Date startDate, Date endDate, String subject, List<ErrorLevel> level, String nodeName) {

        List<LogEnvelope> outList = new ArrayList<LogEnvelope>();

        /*try (Connection conn = jdbcTemplate.getDataSource().getConnection()) {

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String sql = "SELECT * FROM system.log WHERE 1=1 AND";

            //Looking for an interval
            if ( Objects.nonNull(startDate) && Objects.nonNull(endDate) ) {
                sql = sql.concat(" datetime BETWEEN '" + dateFormat.format(startDate) + "' AND '" + dateFormat.format(endDate) + "' AND");
            }
            else if (Objects.nonNull(endDate) ) {
                //When the user sets the end date alone it means the user wants a reverse
                //checking of the database up to 1 month in the past (TOPS)
                //if endDate is 2020-01-01 the startDate will be 2019-01-01 by default
                //This is intended to protect the system from retrieving everything
                //since the beginning.

                Calendar start = Calendar.getInstance();
                start.setTime(endDate);
                start.add(Calendar.MONTH, -1);

                sql = sql.concat(" datetime BETWEEN '" + dateFormat.format(start) + "' AND '" + dateFormat.format(endDate) + "' AND");
            }
            else if (Objects.nonNull(startDate) ) {
                //When the user selects the start date alone it means the user wants a
                //progressive checking of the database up to today.
                //WARNING: This may consume a lot of memory.

                //TODO: Shall we include a limit?

                sql = sql.concat(" datetime >= '" + dateFormat.format(startDate) + "' AND");
            }
            else {
                //If no start date was issued then we will search
                //for the current date in a reverse way until one month in the past.

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();

                sql = sql.concat(" datetime >= '" + dtf.format(now.minusDays(30)) + "' AND");
            }

            if ( Objects.nonNull(subject) && !subject.isEmpty() ) {
                sql = sql.concat(" title like '%" + subject + "%' AND");
            }

            if ( Objects.nonNull(nodeName) && !nodeName.isEmpty() ) {
                sql = sql.concat(" nodename = '" + subject + "' AND");
            }

            sql = sql.concat(concatFieldValues("level", level));
            sql = sql.concat(concatFieldValues("subsystem", subsystem));

            sql = sql.substring(0, sql.length() - 3 - 1);
            sql = sql.concat(" ORDER BY datetime DESC");

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {

                        try {
                            LogEnvelope envelope = LogEnvelope.create(UUID.fromString(rs.getString("id")),
                                    rs.getString("nodename"),
                                    SubSystem.fromString(rs.getString("subsystem")),
                                    rs.getString("title"),
                                    rs.getString("extradata"),
                                    ErrorLevel.fromString(rs.getString("level")),
                                    rs.getDate("datetime"));
                            outList.add(envelope);
                        }
                        catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            return outList;
        } catch (Exception ex) {
            log.error( "An error occurred while getting log rows in " + this.getClass().getName(), ex.getMessage(), ex);
            return null;
        }*/

        return outList;
    }

    private String concatFieldValues (String fieldName, List<?> list) {

        if ( Objects.isNull(list) || list.size() == 0 ) {
            return "";
        }

        String sql = " (";

        int i = 0;

        for (Object s : list) {
            if (i++ > 0) {
                sql = sql.concat("OR ");
            }
            sql = sql.concat(fieldName + " LIKE '%" + s.toString() + "%'");
        }

        return sql.concat(") AND");
    }
}
