package ph.txtdis;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

@Configuration("securityConfiguration")
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private DataSource datasource;

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {

		JdbcUserDetailsManager userDetailsService = new JdbcUserDetailsManager();
		userDetailsService.setDataSource(datasource);
		PasswordEncoder encoder = new BCryptPasswordEncoder();

		auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
		auth.jdbcAuthentication().dataSource(datasource);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.httpBasic().and().authorizeRequests().anyRequest().authenticated().and().csrf().disable();
	}
}