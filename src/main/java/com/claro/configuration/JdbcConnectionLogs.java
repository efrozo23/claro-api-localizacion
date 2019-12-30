package com.claro.configuration;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.impl.GenericObjectPool;


public class JdbcConnectionLogs {

	private final DataSource dataSource;

	private static interface Singleton {
		final JdbcConnectionLogs INSTANCE = new JdbcConnectionLogs();
	}

	private JdbcConnectionLogs() {
		Properties properties = new Properties();
		properties.setProperty("user", "USER_APITX");
		properties.setProperty("password", "xDR5TGB*");
		ConnectionFactory cf = new DriverManagerConnectionFactory("jdbc:oracle:thin:@172.24.43.93:1711/APITRANS",
				properties);
//		Properties properties = new Properties();
//		properties.setProperty("user", "API_MNG");
//		properties.setProperty("password", "Colombia_20_19");
//		ConnectionFactory cf = new DriverManagerConnectionFactory("jdbc:oracle:thin:@172.24.42.20:1725/DEV_APITRAN",
//				properties);
		PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(cf, null);
		poolableConnectionFactory.setValidationQuery("select 1");

		GenericObjectPool<PoolableConnection> pool = new GenericObjectPool<>(poolableConnectionFactory);
		pool.setTestOnBorrow(false);
		pool.setMaxTotal(10);
		poolableConnectionFactory.setPool(pool);
		this.dataSource = new PoolingDataSource<>(pool);
	}

	public static java.sql.Connection getConn() throws SQLException {
		return Singleton.INSTANCE.dataSource.getConnection();
	}

}
