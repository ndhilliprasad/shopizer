<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page session="false"%>

<div class="control-group">
	<label class="required"><s:message
			code="module.shipping.priceBased.shippingCost"
			text="Flat Shipping Cost" /></label>
	<div class="controls">
		<form:input cssClass="input-large highlight"
			path="integrationKeys['shippingCost']" />
	</div>
	<span class="help-inline"> <c:if test="${shippingCost!=null}">
			<span id="identifiererrors" class="error"><s:message
					code="module.shipping.usps.message.shippingCost"
					text="Field in error" /></span>
		</c:if>
	</span>
</div>