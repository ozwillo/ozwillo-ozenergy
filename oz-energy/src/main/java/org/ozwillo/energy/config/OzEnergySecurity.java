package org.ozwillo.energy.config;
//
//import org.oasis_eu.spring.config.OasisSecurityConfiguration;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.access.ExceptionTranslationFilter;
//import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

//@Configuration
public class OzEnergySecurity {
//	public class OzEnergySecurity extends OasisSecurityConfiguration{
//	@Value("${application.security.noauthdevmode:false}") private boolean noauthdevmode;
//	@Value("${application.devmode:false}") private boolean devmode;
//	
//	
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		if (noauthdevmode && devmode) {
//			// don't configure any security
//		} else {
//		http
//				.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessHandler(logoutHandler()).and()
//				.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint()).and()
//				.authorizeRequests()
//				.antMatchers("/my/**").authenticated()
//				.anyRequest().permitAll().and()
//				.addFilterBefore(oasisAuthenticationFilter(), AbstractPreAuthenticatedProcessingFilter.class);
//		}
//		http
//			.addFilterAfter(oasisExceptionTranslationFilter(authenticationEntryPoint()), ExceptionTranslationFilter.class);
//	}

}
