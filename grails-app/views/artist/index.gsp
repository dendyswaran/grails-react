<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Hello React</title>
</head>

<body>
    <div id="content">
		<react:render 
			require="artistScript"
			function="renderFromServer" 
			model="${artistList}"/>
	</div>
	
	<g:javascript>
		(function($) {
			
			$(function() {
				renderFromClient(<react:renderModelAsString model="${artistList}" />);
			});
			
		}(jQuery))
	</g:javascript>
		
</body>
</html>