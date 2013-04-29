<%--
 nfms4redd Portal Interface - http://nfms4redd.org/

 (C) 2012, FAO Forestry Department (http://www.fao.org/forestry/)

 This application is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation;
 version 3.0 of the License.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU General Public License for more details.
--%><%@ page session="true"%><%@
taglib uri="http://www.springframework.org/tags" prefix="spring"%><%@
taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@
page contentType="application/json" pageEncoding="UTF-8"%>{
<c:forEach var="message" items="${config.messages}" varStatus="status">  "${message.key}":"${message.value}"<c:if test="${!status.last}">,</c:if>
</c:forEach>}
