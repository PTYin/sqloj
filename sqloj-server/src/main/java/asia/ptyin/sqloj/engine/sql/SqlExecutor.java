package asia.ptyin.sqloj.engine.sql;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Executor for multiple SQL statements based on JDBC.
 */
@Log4j2
@Service
public class SqlExecutor
{
    /**
     * Default statement delimiter: {@code ";"}.
     */
    public static final String DEFAULT_STATEMENT_DELIMITER = ";";
    /**
     * Fallback statement separator within SQL scripts: {@code "\n"}.
     * <p>Used if neither a custom separator nor the
     * {@link #DEFAULT_STATEMENT_DELIMITER} is present.
     */
    public static final String FALLBACK_STATEMENT_DELIMITER = "\n";
    /**
     * Default prefixes for single-line comments: {@code ["--"]}.
     */
    public static final String[] DEFAULT_COMMENT_PREFIXES = {"--"};
    /**
     * Default start delimiter for block comments: {@code "/*"}.
     */
    public static final String DEFAULT_BLOCK_COMMENT_START_DELIMITER = "/*";

    /**
     * Default end delimiter for block comments: <code>"*&#47;"</code>.
     */
    public static final String DEFAULT_BLOCK_COMMENT_END_DELIMITER = "*/";

    public static boolean containsStatementDelimiter(String source, String delimiter, String[] commentPrefixes,
                                                     String blockCommentStartDelimiter,
                                                     String blockCommentEndDelimiter)
    {
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean inEscape = false;

        for (int i = 0; i < source.length(); i++)
        {
            char c = source.charAt(i);
            if (inEscape)
            {
                inEscape = false;
                continue;
            }
            // MySQL style escapes
            if (c == '\\')
            {
                inEscape = true;
                continue;
            }
            if (!inDoubleQuote && (c == '\''))
            {
                inSingleQuote = !inSingleQuote;
            } else if (!inSingleQuote && (c == '"'))
            {
                inDoubleQuote = !inDoubleQuote;
            }
            if (!inSingleQuote && !inDoubleQuote)
            {
                if (source.startsWith(delimiter, i))
                {
                    return true;
                } else if (startsWithAny(source, commentPrefixes, i))
                {
                    // Skip over any content from the start of the comment to the EOL
                    int indexOfNextNewline = source.indexOf('\n', i);
                    if (indexOfNextNewline > i)
                    {
                        i = indexOfNextNewline;
                    } else
                    {
                        // If there's no EOL, we must be at the end of the source, so stop here.
                        break;
                    }
                } else if (source.startsWith(blockCommentStartDelimiter, i))
                {
                    // Skip over any block comments
                    int indexOfCommentEnd = source.indexOf(blockCommentEndDelimiter, i);
                    if (indexOfCommentEnd > i)
                    {
                        i = indexOfCommentEnd + blockCommentEndDelimiter.length() - 1;
                    } else
                    {
                        throw new SqlStatementsParseException(
                                "Missing block comment end delimiter: " + blockCommentEndDelimiter, source);
                    }
                }
            }
        }

        return false;
    }

    public static void split(String source, String delimiter, String[] commentPrefixes,
                             String blockCommentStartDelimiter, String blockCommentEndDelimiter,
                             List<String> statements)
    {
        // TODO assertion before splitting statements.
        StringBuilder sb = new StringBuilder();
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean inEscape = false;

        for (int i = 0; i < source.length(); i++)
        {
            char c = source.charAt(i);
            if (inEscape)
            {
                inEscape = false;
                sb.append(c);
                continue;
            }
            // MySQL style escapes
            if (c == '\\')
            {
                inEscape = true;
                sb.append(c);
                continue;
            }
            if (!inDoubleQuote && (c == '\''))
            {
                inSingleQuote = !inSingleQuote;
            } else if (!inSingleQuote && (c == '"'))
            {
                inDoubleQuote = !inDoubleQuote;
            }
            if (!inSingleQuote && !inDoubleQuote)
            {
                if (source.startsWith(delimiter, i))
                {
                    // We've reached the end of the current statement
                    if (sb.length() > 0)
                    {
                        statements.add(sb.toString());
                        sb = new StringBuilder();
                    }
                    i += delimiter.length() - 1;
                    continue;
                } else if (startsWithAny(source, commentPrefixes, i))
                {
                    // Skip over any content from the start of the comment to the EOL
                    int indexOfNextNewline = source.indexOf('\n', i);
                    if (indexOfNextNewline > i)
                    {
                        i = indexOfNextNewline;
                        continue;
                    } else
                    {
                        // If there's no EOL, we must be at the end of the source, so stop here.
                        break;
                    }
                } else if (source.startsWith(blockCommentStartDelimiter, i))
                {
                    // Skip over any block comments
                    int indexOfCommentEnd = source.indexOf(blockCommentEndDelimiter, i);
                    if (indexOfCommentEnd > i)
                    {
                        i = indexOfCommentEnd + blockCommentEndDelimiter.length() - 1;
                        continue;
                    } else
                    {
                        throw new SqlStatementsParseException(
                                "Missing block comment end delimiter: " + blockCommentEndDelimiter, source);
                    }
                } else if (c == ' ' || c == '\r' || c == '\n' || c == '\t')
                {
                    // Avoid multiple adjacent whitespace characters
                    if (sb.length() > 0 && sb.charAt(sb.length() - 1) != ' ')
                    {
                        c = ' ';
                    } else
                    {
                        continue;
                    }
                }
            }
            sb.append(c);
        }

        if (StringUtils.hasText(sb))
        {
            statements.add(sb.toString());
        }
    }

    private static boolean startsWithAny(String source, String[] prefixes, int offset)
    {
        for (String prefix : prefixes)
        {
            if (source.startsWith(prefix, offset))
            {
                return true;
            }
        }
        return false;
    }

    public static QueryResult execute(Connection connection, String source, String delimiter,
                                      String[] commentPrefixes, String blockCommentStartDelimiter,
                                      String blockCommentEndDelimiter) throws SQLException
    {
        long startTime = System.currentTimeMillis();


        if (delimiter == null)
            delimiter = DEFAULT_STATEMENT_DELIMITER;

        if (!containsStatementDelimiter(source, delimiter, commentPrefixes,
                blockCommentStartDelimiter, blockCommentEndDelimiter))
            delimiter = FALLBACK_STATEMENT_DELIMITER;

        List<String> statements = new ArrayList<>();
        split(source, delimiter, commentPrefixes, blockCommentStartDelimiter,
                blockCommentEndDelimiter, statements);

        int stmtNumber = 0;
        Statement stmt = connection.createStatement();
        QueryResult result = null;
        try
        {
            for (String statement : statements)
            {
                stmtNumber++;
                if(log.isDebugEnabled())
                    log.debug("Statement [%d]: %s".formatted(stmtNumber, statement));
                try
                {
                    // true if the first result is a ResultSet object;
                    // false if it is an update count or there are no results.
                    if (stmt.execute(statement))
                        result = result == null ? new QueryResult(stmt.getResultSet()) : result;
                    else
                    {
                        int rowsAffected = stmt.getUpdateCount();
                        if (log.isDebugEnabled())
                        {
                            log.debug(rowsAffected + " returned as update count for SQL: " + statement);
                            SQLWarning warningToLog = stmt.getWarnings();
                            while (warningToLog != null)
                            {
                                log.debug("SQLWarning ignored: SQL state '" + warningToLog.getSQLState() +
                                        "', error code '" + warningToLog.getErrorCode() +
                                        "', message [" + warningToLog.getMessage() + "]");
                                warningToLog = warningToLog.getNextWarning();
                            }
                        }
                    }
                } catch (SQLException ex)
                {
                    throw new SqlStatementFailedException(statement, stmtNumber, source, ex);
                }
            }
        } finally
        {
            try
            {
                stmt.close();
            } catch (Throwable ex)
            {
                log.trace("Could not close JDBC Statement", ex);
            }
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        if (log.isDebugEnabled())
        {
            log.debug("Executed SQL source from " + source + " in " + elapsedTime + " ms.");
        }
        return result;
    }

    /**
     * Execute SQL source code in the connection and get the first query result.
     * @param connection Database connection.
     * @param source Source SQL code.
     * @return The first query result in the execution of {@code source}.
     * @throws SQLException Throw if SQLException during manipulating JDBC.
     * @see QueryResult
     */
    public static QueryResult execute(Connection connection, String source) throws SQLException
    {
        return execute(connection, source, DEFAULT_STATEMENT_DELIMITER, DEFAULT_COMMENT_PREFIXES,
                DEFAULT_BLOCK_COMMENT_START_DELIMITER, DEFAULT_BLOCK_COMMENT_END_DELIMITER);
    }
}
