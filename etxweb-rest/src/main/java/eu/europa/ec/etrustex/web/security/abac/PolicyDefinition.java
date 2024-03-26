package eu.europa.ec.etrustex.web.security.abac;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PolicyDefinition {
	private String name;
	private String description;
	/*
	 * Boolean SpEL expression. If evaluated to true, then this rule is applied to the request access context.
	 */
	private String  target;
	
	/*
	 * Boolean SpEL expression, if evaluated to true, then access granted.
	 */
	private String  condition;
}
