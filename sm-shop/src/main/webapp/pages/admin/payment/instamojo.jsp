<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page session="false"%>

<div class="control-group">
	<label class="required"><s:message
			code="module.payment.instamojo.clientId" text="clientId" /></label>
	<div class="controls">
		<form:input cssClass="input-xxlarge highlight"
			path="integrationKeys['clientId']" />
	</div>
	<span class="help-inline"> <c:if test="${clientId!=null}">
			<span id="identifiererrors" class="error"><s:message
					code="module.payment.instamojo.message.clientId"
					text="Field in error" /></span>
		</c:if>
	</span>
</div>

<div class="control-group">
	<label class="required"><s:message
			code="module.payment.instamojo.clientSecret" text="clientSecret" /></label>
	<div class="controls">
		<form:input cssClass="input-xxlarge highlight"
			path="integrationKeys['clientSecret']" />
	</div>
	<span class="help-inline"> <c:if test="${clientSecret!=null}">
			<span id="apikeyerrors" class="error"><s:message
					code="module.payment.instamojo.message.clientSecret"
					text="Field in error" /></span>
		</c:if>
	</span>
</div>


<div class="control-group">
	<label class="required"><s:message
			code="module.payment.transactiontype" text="Transaction type" /></label>
	<div class="controls">
		<form:radiobutton cssClass="input-large highlight"
			path="integrationKeys['transaction']" value="AUTHORIZE" />
		&nbsp;
		<s:message code="module.payment.transactiontype.preauth"
			text="Pre-authorization" />
		<br />
		<form:radiobutton cssClass="input-large highlight"
			path="integrationKeys['transaction']" value="AUTHORIZECAPTURE" />
		&nbsp;
		<s:message code="module.payment.transactiontype.sale" text="Sale" />
		</br>
	</div>
</div>


