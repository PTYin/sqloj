package asia.ptyin.sqloj.engine.task.impl;

import asia.ptyin.sqloj.engine.result.ExecutionResult;
import asia.ptyin.sqloj.engine.sql.SqlExecutionUtils;
import asia.ptyin.sqloj.engine.task.Task;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.UUID;

/***
 * A task class to execute SQL script and return the query result.
 * @version 0.1.0
 * @author PTYin
 * @since 0.1.0
 */
public class AssignQuestionTask extends Task<ExecutionResult>
{

    private final Connection connection;
    private final String source;

    public AssignQuestionTask(Connection connection, String source)
    {
        this.connection = connection;
        this.source = source;
    }

    @Override
    protected ExecutionResult run() throws InterruptedException
    {
        ExecutionResult executionResult = null;
        try
        {
            executionResult = SqlExecutionUtils.execute(connection, source);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return executionResult;
    }
}