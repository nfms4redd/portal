<%@page import="java.util.Enumeration"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.ResourceBundle"%>
<%@page import="net.tanesha.recaptcha.ReCaptchaImpl"%>
<%@page session="true"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="pack" uri="http://packtag.sf.net"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<c:set var="req" value="${pageContext.request}" />
<c:set var="uri" value="${req.requestURI}" />
<c:set var="base" value="${fn:replace(req.requestURL, fn:substring(uri, 0, fn:length(uri)), req.contextPath)}" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!--
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
-->
<html>
  <head>
    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    
    <title><spring:message code="title" /></title>
   
    <script type="text/javascript">
        var languageCode = "${pageContext.response.locale}";
        var messages = <jsp:include page="messages.jsp"/>;
        var recaptchaKey = "${recaptchaKey}";
    </script>
    
    <script type="text/javascript" src="layers.json?jsonp"></script>
    
    <pack:script enabled="${config.minifiedJs}">
    	<!-- src>/js/OpenLayers-2.12.full.js</src -->
    	<src>/js/OpenLayers.unredd.js</src>
    	<src>/js/jquery-1.7.1.js</src>
    	<src>/js/jquery.mustache.js</src>
    	<src>/js/jquery-ui-1.8.16.custom.min.js</src>
    	<src>/js/jquery.fancybox.js</src>
    	<src>/js/ol-extensions/PortalToolbar.js</src>
    	<src>/js/unredd.js</src>
    	<src>${base}/static/custom.js</src>
    </pack:script>

    <pack:style enabled="${config.minifiedJs}">
    	<src>/css/openlayers/style.css</src>
    	<src>/css/jquery-ui-1.8.16.custom.css</src>
    	<src>/css/jquery.fancybox.css</src>
    	<src>/css/toolbar.css</src>
    	<src>${base}/static/unredd.css</src>
    </pack:style>

  </head>
  <body>
    <div id="header">
      ${config.header}
      
      <div id="toolbar">
        <c:forEach items="${config.languages}" var="lang">
          <a href="?lang=${lang.key}" class="blue_button lang_button <c:if test="${lang.key == pageContext.response.locale}">selected</c:if>" id="button_${lang.key}">${lang.value}</a>
        </c:forEach>
<!--         <a href="#" class="blue_button" id="button_feedback"><spring:message code="feedback" /></a> -->
        <div id="time_slider_pane">
          <div id="time_slider"></div>
          <div id="time_slider_label"></div>
        </div>
      </div>

    </div>
    
    <div id="layer_list_selector_pane">
		  <input type="radio" id="all_layers" name="layer_list_selector" checked="checked"></input><label for="all_layers"><spring:message code="layers" /></label>
		  <input type="radio" id="active_layers" name="layer_list_selector"></input><label for="active_layers"><spring:message code="selected_layers" /></label>
    </div>
    <div style="z-index:1100;position:absolute;top:215px;left:10px;width:250px;font-size:10px;">
      <div id="active_layers_pane" style="position:relative;top:0;left:0;display:none">
        <h3><a href="#">Selected Layers</a></h3>
        <div></div>
      </div>
      <div id="layers_pane" style="position:relative;top:0;left:0;display:none"></div>   
    </div>
    
    <a class="blue_button" style="z-index:1000;top:150px;right:20px;margin-right:0px;position:absolute;width:60px;margin-top:0" href="#" id="toggle_legend"><spring:message code="legend_button" /></a>
    
    ${config.footer}
    
    <a href="#" onclick="UNREDD.map.zoomIn();return false" id="zoom_in"></a>
    <a href="#" onclick="UNREDD.map.zoomOut();return false" id="zoom_out"></a>
    <a href="#" onclick="UNREDD.map.setCenter(UNREDD.mapCenter, UNREDD.defaultZoomLevel); return false" id="zoom_to_max_extent"></a>
    
    <div id="legend_pane" title="Legend" style="padding:2px;">
      <div id="legend_pane_content" style="background-color:#fff;width:100%;height:100%"> 
      </div>
    </div>
    
    <div id="info_popup"></div>

	<div id="invalid-mail"  style="display:none;" title="<spring:message code="invalid_email_title"/>">
		<p><spring:message code="invalid_email_text"/></p>
	</div>

    <div id="feedback_popup" style="display:none;">
      <table class="feedback">
        <tr>
          <th><spring:message code="layer" />:</th>
          <td>
            <select id="fb_layers"></select>
            <span id="fb_time"></span>
          </td>
        </tr>
        <tr>
          <th><spring:message code="feedback_drawing_tools" />:</th>
          <td>
             <div id="fb_toolbar" class="olControlPortalToolbar"></div>
             <div class="fb_comment"><spring:message code="feedback_text"/></div>
          </td>
        </tr>
        <tr>
          <th><spring:message code="name" />:</th>
          <td><input name="name_" id="fb_name_" type="text"></td>
        </tr>
        <tr>
          <th><spring:message code="email" />:</th>
          <td><input name="email_" id="fb_email_" type="text"></td>
        </tr>
        <tr>
          <th><spring:message code="feedback" />:</th>
          <td><textarea id="feedback_" name="feedback_text_"></textarea></td>
        </tr>
		<tr>
		  <td colspan="2" class="recaptcha">${captchaHtml}</td>
		</tr>
        <tr>
          <td></td>
          <td>
            <input id="feedback_submit" type="submit" value="<spring:message code="submit" />" />
            <input type="button" id="feedback_cancel" value="<spring:message code="cancel" />" />
          </td>
        </tr>
      </table>
    </div>

    <div id="stats_popup" style="display:none;">
      <table class="feedback">
        <tr>
          <th><spring:message code="chart" />:</th>
          <td><select id="stats_charts"></select></td>
        </tr>
        <tr>
          <th><spring:message code="feedback_drawing_tools" />:</th>
          <td>
             <div id="stats_toolbar" class="olControlPortalToolbar"></div>
             <div class="fb_comment"><spring:message code="feedback_text"/></div>
          </td>
        </tr>
        <tr>
          <th><spring:message code="name" />:</th>
          <td><input name="name_" id="stats_name_" type="text"></td>
        </tr>
        <tr>
          <th><spring:message code="email" />:</th>
          <td><input name="email_" id="stats_email_" type="text"></td>
        </tr>
 		<tr>
 		  <td>&nbsp;</td>
          <!--td colspan="2" class="recaptcha">${captchaHtml}</td-->
        </tr>
        <tr>
          <td></td>
          <td>
            <input id="stats_submit" type="submit" value="<spring:message code="submit" />" />
            <input type="button" id="stats_cancel" value="<spring:message code="cancel" />" />
          </td>
        </tr>
      </table>
    </div>
   
    <div id="map"></div>
    
    <div style="display:none">
    	<div id="custom_popup"></div>
    </div>
    
  </body>
</html>
