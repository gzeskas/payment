package lt.gzeskas.payment.configuration.datasource;

public class DatabaseConfiguration {
    private final String driverClass;
    private final String jdbcConnectionString;
    private final String userName;
    private final String password;

    public DatabaseConfiguration(String driverClass, String jdbcConnectionString, String userName, String password) {
        this.driverClass = driverClass;
        this.jdbcConnectionString = jdbcConnectionString;
        this.userName = userName;
        this.password = password;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public String getJdbcConnectionString() {
        return jdbcConnectionString;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public static DatabaseConfiguration h2EmbededDatabaseConfiguration() {
        return new DatabaseConfiguration("org.h2.Driver", "jdbc:h2:~/payments", "test", "test");
    }
}
